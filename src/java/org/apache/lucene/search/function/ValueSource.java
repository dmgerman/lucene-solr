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
name|index
operator|.
name|IndexReader
import|;
end_import

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
name|function
operator|.
name|DocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ToStringUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
comment|/**  * Expert: source of values for basic function queries.  *<P>At its default/simplest form, values - one per doc - are used as the score of that doc.  *<P>Values are instantiated as   * {@link org.apache.lucene.search.function.DocValues DocValues} for a particular reader.  *<P>ValueSource implementations differ in RAM requirements: it would always be a factor  * of the number of documents, but for each document the number of bytes can be 1, 2, 4, or 8.   *  *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *  *  */
end_comment

begin_class
DECL|class|ValueSource
specifier|public
specifier|abstract
class|class
name|ValueSource
implements|implements
name|Serializable
block|{
comment|/**    * Return the DocValues used by the function query.    * @param reader the IndexReader used to read these values.    * If any caching is involved, that caching would also be IndexReader based.      * @throws IOException for any error.    */
DECL|method|getValues
specifier|public
specifier|abstract
name|DocValues
name|getValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * description of field, used in explain()     */
DECL|method|description
specifier|public
specifier|abstract
name|String
name|description
parameter_list|()
function_decl|;
comment|/* (non-Javadoc) @see java.lang.Object#toString() */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
argument_list|()
return|;
block|}
comment|/**    * Needed for possible caching of query results - used by {@link ValueSourceQuery#equals(Object)}.    * @see Object#equals(Object)    */
DECL|method|equals
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
function_decl|;
comment|/**    * Needed for possible caching of query results - used by {@link ValueSourceQuery#hashCode()}.    * @see Object#hashCode()    */
DECL|method|hashCode
specifier|public
specifier|abstract
name|int
name|hashCode
parameter_list|()
function_decl|;
block|}
end_class

end_unit

