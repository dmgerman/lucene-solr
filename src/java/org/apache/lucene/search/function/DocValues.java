begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import

begin_comment
comment|/**  * Expert: represents field values as different types.  * Normally created via a   * {@link org.apache.lucene.search.function.ValueSource ValueSuorce}   * for a particular field and reader.  *  *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *   *  */
end_comment

begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
block|{
comment|/*    * DocValues is distinct from ValueSource because    * there needs to be an object created at query evaluation time that    * is not referenced by the query itself because:    * - Query objects should be MT safe    * - For caching, Query objects are often used as keys... you don't    *   want the Query carrying around big objects    */
comment|/**    * Return doc value as a float.     *<P>Mandatory: every DocValues implementation must implement at least this method.     * @param doc document whose float value is requested.     */
DECL|method|floatVal
specifier|public
specifier|abstract
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**    * Return doc value as an int.     *<P>Optional: DocValues implementation can (but don't have to) override this method.     * @param doc document whose int value is requested.    */
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/**    * Return doc value as a long.     *<P>Optional: DocValues implementation can (but don't have to) override this method.     * @param doc document whose long value is requested.    */
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/**    * Return doc value as a double.     *<P>Optional: DocValues implementation can (but don't have to) override this method.     * @param doc document whose double value is requested.    */
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
comment|/**    * Return doc value as a string.     *<P>Optional: DocValues implementation can (but don't have to) override this method.     * @param doc document whose string value is requested.    */
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Return a string representation of a doc value, as reuired for Explanations.    */
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**    * Explain the scoring value for the input doc.    */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Expert: for test purposes only, return the inner array of values, or null if not applicable.    *<p>    * Allows tests to verify that loaded values are:    *<ol>    *<li>indeed cached/reused.</li>    *<li>stored in the expected size/type (byte/short/int/float).</li>    *</ol>    * Note: implementations of DocValues must override this method for     * these test elements to be tested, Otherwise the test would not fail, just     * print a warning.    */
DECL|method|getInnerArray
name|Object
name|getInnerArray
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this optional method is for test purposes only"
argument_list|)
throw|;
block|}
comment|// --- some simple statistics on values
DECL|field|minVal
specifier|private
name|float
name|minVal
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|field|maxVal
specifier|private
name|float
name|maxVal
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|field|avgVal
specifier|private
name|float
name|avgVal
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|field|computed
specifier|private
name|boolean
name|computed
init|=
literal|false
decl_stmt|;
comment|// compute optional values
DECL|method|compute
specifier|private
name|void
name|compute
parameter_list|()
block|{
if|if
condition|(
name|computed
condition|)
block|{
return|return;
block|}
name|float
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|float
name|val
decl_stmt|;
try|try
block|{
name|val
operator|=
name|floatVal
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
break|break;
block|}
name|sum
operator|+=
name|val
expr_stmt|;
name|minVal
operator|=
name|Float
operator|.
name|isNaN
argument_list|(
name|minVal
argument_list|)
condition|?
name|val
else|:
name|Math
operator|.
name|min
argument_list|(
name|minVal
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|maxVal
operator|=
name|Float
operator|.
name|isNaN
argument_list|(
name|maxVal
argument_list|)
condition|?
name|val
else|:
name|Math
operator|.
name|max
argument_list|(
name|maxVal
argument_list|,
name|val
argument_list|)
expr_stmt|;
operator|++
name|n
expr_stmt|;
block|}
name|avgVal
operator|=
name|n
operator|==
literal|0
condition|?
name|Float
operator|.
name|NaN
else|:
name|sum
operator|/
name|n
expr_stmt|;
name|computed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Returns the minimum of all values or<code>Float.NaN</code> if this    * DocValues instance does not contain any value.    *<p>    * This operation is optional    *</p>    *     * @return the minimum of all values or<code>Float.NaN</code> if this    *         DocValues instance does not contain any value.    */
DECL|method|getMinValue
specifier|public
name|float
name|getMinValue
parameter_list|()
block|{
name|compute
argument_list|()
expr_stmt|;
return|return
name|minVal
return|;
block|}
comment|/**    * Returns the maximum of all values or<code>Float.NaN</code> if this    * DocValues instance does not contain any value.    *<p>    * This operation is optional    *</p>    *     * @return the maximum of all values or<code>Float.NaN</code> if this    *         DocValues instance does not contain any value.    */
DECL|method|getMaxValue
specifier|public
name|float
name|getMaxValue
parameter_list|()
block|{
name|compute
argument_list|()
expr_stmt|;
return|return
name|maxVal
return|;
block|}
comment|/**    * Returns the average of all values or<code>Float.NaN</code> if this    * DocValues instance does not contain any value. *    *<p>    * This operation is optional    *</p>    *     * @return the average of all values or<code>Float.NaN</code> if this    *         DocValues instance does not contain any value    */
DECL|method|getAverageValue
specifier|public
name|float
name|getAverageValue
parameter_list|()
block|{
name|compute
argument_list|()
expr_stmt|;
return|return
name|avgVal
return|;
block|}
block|}
end_class

end_unit

