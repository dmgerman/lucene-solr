begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|IOException
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
name|util
operator|.
name|DocIdBitSet
import|;
end_import

begin_comment
comment|/**   *  Abstract base class providing a mechanism to use a subset of an index  *  for restriction or permission of index search results.  *<p>  */
end_comment

begin_class
DECL|class|Filter
specifier|public
specifier|abstract
class|class
name|Filter
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/**    * Creates a {@link DocIdSet} that provides the documents which should be    * permitted or prohibited in search results.<b>NOTE:</b> null can be    * returned if no documents will be accepted by this Filter.    *<p>    * Note: This method might be called more than once during a search if the    * index has more than one segment. In such a case the {@link DocIdSet}    * must be relative to the document base of the given reader. Yet, the    * segment readers are passed in increasing document base order.    *     * @param reader a {@link IndexReader} instance opened on the index currently    *         searched on. Note, it is likely that the provided reader does not    *         represent the whole underlying index i.e. if the index has more than    *         one segment the given reader only represents a single segment.    *              * @return a DocIdSet that provides the documents which should be permitted or    *         prohibited in search results.<b>NOTE:</b> null can be returned if    *         no documents will be accepted by this Filter.    *     * @see DocIdBitSet    */
DECL|method|getDocIdSet
specifier|public
specifier|abstract
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

