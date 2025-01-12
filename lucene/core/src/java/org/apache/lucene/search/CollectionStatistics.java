begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|// javadocs
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
name|Terms
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**  * Contains statistics for a collection (field)  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CollectionStatistics
specifier|public
class|class
name|CollectionStatistics
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|long
name|maxDoc
decl_stmt|;
DECL|field|docCount
specifier|private
specifier|final
name|long
name|docCount
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|private
specifier|final
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|private
specifier|final
name|long
name|sumDocFreq
decl_stmt|;
DECL|method|CollectionStatistics
specifier|public
name|CollectionStatistics
parameter_list|(
name|String
name|field
parameter_list|,
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|)
block|{
assert|assert
name|maxDoc
operator|>=
literal|0
assert|;
assert|assert
name|docCount
operator|>=
operator|-
literal|1
operator|&&
name|docCount
operator|<=
name|maxDoc
assert|;
comment|// #docs with field must be<= #docs
assert|assert
name|sumDocFreq
operator|==
operator|-
literal|1
operator|||
name|sumDocFreq
operator|>=
name|docCount
assert|;
comment|// #postings must be>= #docs with field
assert|assert
name|sumTotalTermFreq
operator|==
operator|-
literal|1
operator|||
name|sumTotalTermFreq
operator|>=
name|sumDocFreq
assert|;
comment|// #positions must be>= #postings
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|this
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
block|}
comment|/** returns the field name */
DECL|method|field
specifier|public
specifier|final
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** returns the total number of documents, regardless of     * whether they all contain values for this field.     * @see IndexReader#maxDoc() */
DECL|method|maxDoc
specifier|public
specifier|final
name|long
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
comment|/** returns the total number of documents that    * have at least one term for this field.     * @see Terms#getDocCount() */
DECL|method|docCount
specifier|public
specifier|final
name|long
name|docCount
parameter_list|()
block|{
return|return
name|docCount
return|;
block|}
comment|/** returns the total number of tokens for this field    * @see Terms#getSumTotalTermFreq() */
DECL|method|sumTotalTermFreq
specifier|public
specifier|final
name|long
name|sumTotalTermFreq
parameter_list|()
block|{
return|return
name|sumTotalTermFreq
return|;
block|}
comment|/** returns the total number of postings for this field     * @see Terms#getSumDocFreq() */
DECL|method|sumDocFreq
specifier|public
specifier|final
name|long
name|sumDocFreq
parameter_list|()
block|{
return|return
name|sumDocFreq
return|;
block|}
block|}
end_class

end_unit

