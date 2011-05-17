begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
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
name|Closeable
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
name|util
operator|.
name|Collection
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
name|index
operator|.
name|values
operator|.
name|DocValues
import|;
end_import

begin_comment
comment|/**  * Abstract API that provides access to one or more per-document storage  * features. The concrete implementations provide access to the underlying  * storage on a per-document basis corresponding to their actual  * {@link PerDocConsumer} counterpart.  *<p>  * The {@link PerDocValues} API is accessible through flexible indexing / the  * {@link Codec} - API providing per field consumers and producers for inverted  * data (terms, postings) as well as per-document data.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|PerDocValues
specifier|public
specifier|abstract
class|class
name|PerDocValues
implements|implements
name|Closeable
block|{
comment|/**    * Returns {@link DocValues} for the current field.    *     * @param field    *          the field name    * @return the {@link DocValues} for this field or<code>null</code> if not    *         applicable.    * @throws IOException    */
DECL|method|docValues
specifier|public
specifier|abstract
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|PerDocValues
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|PerDocValues
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Returns all fields this {@link PerDocValues} contains values for.    */
DECL|method|fields
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|()
function_decl|;
block|}
end_class

end_unit

