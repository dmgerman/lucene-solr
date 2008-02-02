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
name|util
operator|.
name|BitSet
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
comment|/** Abstract base class providing a mechanism to use a subset of an index  *  for restriction or permission of index search results.  *<p>  *<b>Note:</b> In Lucene 3.0 {@link #bits(IndexReader)} will be removed  *  and {@link #getDocIdSet(IndexReader)} will be defined as abstract.  *  All implementing classes must therefore implement {@link #getDocIdSet(IndexReader)}  *  in order to work with Lucene 3.0.  */
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
comment|/**    * @return A BitSet with true for documents which should be permitted in    * search results, and false for those that should not.    * @deprecated Use {@link #getDocIdSet(IndexReader)} instead.    */
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
comment|/**    * @return a DocIdSet that provides the documents which should be    * permitted or prohibited in search results.    * @see DocIdBitSet    */
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocIdBitSet
argument_list|(
name|bits
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

