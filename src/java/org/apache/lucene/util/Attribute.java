begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Base class for Attributes that can be added to a   * {@link org.apache.lucene.util.AttributeSource}.  *<p>  * Attributes are used to add data in a dynamic, yet type-safe way to a source  * of usually streamed objects, e. g. a {@link org.apache.lucene.analysis.TokenStream}.  *<p><font color="#FF0000">  * WARNING: The status of the new TokenStream, AttributeSource and Attributes is experimental.   * The APIs introduced in these classes with Lucene 2.9 might change in the future.   * We will make our best efforts to keep the APIs backwards-compatible.</font>  */
end_comment

begin_class
DECL|class|Attribute
specifier|public
specifier|abstract
class|class
name|Attribute
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
comment|/**    * Clears the values in this Attribute and resets it to its     * default value.    */
DECL|method|clear
specifier|public
specifier|abstract
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Subclasses must implement this method and should follow a syntax    * similar to this one:    *     *<pre>    *   public String toString() {    *     return "start=" + startOffset + ",end=" + endOffset;    *   }    *</pre>    */
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**    * Subclasses must implement this method and should compute    * a hashCode similar to this:    *<pre>    *   public int hashCode() {    *     int code = startOffset;    *     code = code * 31 + endOffset;    *     return code;    *   }    *</pre>     *     * see also {@link #equals(Object)}    */
DECL|method|hashCode
specifier|public
specifier|abstract
name|int
name|hashCode
parameter_list|()
function_decl|;
comment|/**    * All values used for computation of {@link #hashCode()}     * should be checked here for equality.    *     * see also {@link Object#equals(Object)}    */
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
function_decl|;
comment|/**    * Copies the values from this Attribute into the passed-in    * target attribute. The type of the target must match the type    * of this attribute.     */
DECL|method|copyTo
specifier|public
specifier|abstract
name|void
name|copyTo
parameter_list|(
name|Attribute
name|target
parameter_list|)
function_decl|;
comment|/**    * Shallow clone. Subclasses must override this if they     * need to clone any members deeply,    */
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|Object
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// shouldn't happen
block|}
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

