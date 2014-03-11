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
name|java
operator|.
name|util
operator|.
name|Random
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
name|AtomicReaderContext
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

begin_class
DECL|class|AssertingWeight
class|class
name|AssertingWeight
extends|extends
name|Weight
block|{
DECL|method|wrap
specifier|static
name|Weight
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Weight
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|AssertingWeight
condition|?
name|other
else|:
operator|new
name|AssertingWeight
argument_list|(
name|random
argument_list|,
name|other
argument_list|)
return|;
block|}
DECL|field|scoresDocsOutOfOrder
specifier|final
name|boolean
name|scoresDocsOutOfOrder
decl_stmt|;
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|Weight
name|in
decl_stmt|;
DECL|method|AssertingWeight
name|AssertingWeight
parameter_list|(
name|Random
name|random
parameter_list|,
name|Weight
name|in
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|scoresDocsOutOfOrder
operator|=
name|in
operator|.
name|scoresDocsOutOfOrder
argument_list|()
operator|||
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|in
operator|.
name|getQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|in
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if the caller asks for in-order scoring or if the weight does not support
comment|// out-of order scoring then collection will have to happen in-order.
specifier|final
name|Scorer
name|inScorer
init|=
name|in
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
return|return
name|AssertingScorer
operator|.
name|wrap
argument_list|(
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|inScorer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if the caller asks for in-order scoring or if the weight does not support
comment|// out-of order scoring then collection will have to happen in-order.
name|BulkScorer
name|inScorer
init|=
name|in
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|,
name|scoreDocsInOrder
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|inScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|AssertingBulkScorer
operator|.
name|shouldWrap
argument_list|(
name|inScorer
argument_list|)
condition|)
block|{
comment|// The incoming scorer already has a specialized
comment|// implementation for BulkScorer, so we should use it:
return|return
name|AssertingBulkScorer
operator|.
name|wrap
argument_list|(
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|inScorer
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|scoreDocsInOrder
operator|==
literal|false
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// The caller claims it can handle out-of-order
comment|// docs; let's confirm that by pulling docs and
comment|// randomly shuffling them before collection:
comment|//Scorer scorer = in.scorer(context, acceptDocs);
name|Scorer
name|scorer
init|=
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
comment|// Scorer should not be null if bulkScorer wasn't:
assert|assert
name|scorer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingBulkOutOfOrderScorer
argument_list|(
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|scorer
argument_list|)
return|;
block|}
else|else
block|{
comment|// Let super wrap this.scorer instead, so we use
comment|// AssertingScorer:
return|return
name|super
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|,
name|scoreDocsInOrder
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
name|scoresDocsOutOfOrder
return|;
block|}
block|}
end_class

end_unit

