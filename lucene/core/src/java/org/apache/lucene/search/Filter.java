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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LeafReaderContext
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
name|Bits
import|;
end_import

begin_comment
comment|/**   *  Abstract base class for restricting which documents may  *  be returned during searching.  */
end_comment

begin_class
DECL|class|Filter
specifier|public
specifier|abstract
class|class
name|Filter
block|{
comment|/**    * Creates a {@link DocIdSet} enumerating the documents that should be    * permitted in search results.<b>NOTE:</b> null can be    * returned if no documents are accepted by this Filter.    *<p>    * Note: This method will be called once per segment in    * the index during searching.  The returned {@link DocIdSet}    * must refer to document IDs for that segment, not for    * the top-level reader.    *     * @param context a {@link org.apache.lucene.index.LeafReaderContext} instance opened on the index currently    *         searched on. Note, it is likely that the provided reader info does not    *         represent the whole underlying index i.e. if the index has more than    *         one segment the given reader only represents a single segment.    *         The provided context is always an atomic context, so you can call     *         {@link org.apache.lucene.index.LeafReader#fields()}    *         on the context's reader, for example.    *    * @param acceptDocs    *          Bits that represent the allowable docs to match (typically deleted docs    *          but possibly filtering other documents)    *              * @return a DocIdSet that provides the documents which should be permitted or    *         prohibited in search results.<b>NOTE:</b><code>null</code> should be returned if    *         the filter doesn't accept any documents otherwise internal optimization might not apply    *         in the case an<i>empty</i> {@link DocIdSet} is returned.    */
DECL|method|getDocIdSet
specifier|public
specifier|abstract
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

