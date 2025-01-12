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
name|PostingsEnum
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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomNumbers
import|;
end_import

begin_comment
comment|/** Wraps a Scorer with additional checks */
end_comment

begin_class
DECL|class|AssertingBulkScorer
specifier|final
class|class
name|AssertingBulkScorer
extends|extends
name|BulkScorer
block|{
DECL|method|wrap
specifier|public
specifier|static
name|BulkScorer
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|BulkScorer
name|other
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|other
operator|instanceof
name|AssertingBulkScorer
condition|)
block|{
return|return
name|other
return|;
block|}
return|return
operator|new
name|AssertingBulkScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|BulkScorer
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|max
name|int
name|max
init|=
literal|0
decl_stmt|;
DECL|method|AssertingBulkScorer
specifier|private
name|AssertingBulkScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|BulkScorer
name|in
parameter_list|,
name|int
name|maxDoc
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
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
DECL|method|getIn
specifier|public
name|BulkScorer
name|getIn
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|max
operator|==
literal|0
assert|;
name|collector
operator|=
operator|new
name|AssertingLeafCollector
argument_list|(
name|random
argument_list|,
name|collector
argument_list|,
literal|0
argument_list|,
name|PostingsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|int
name|next
init|=
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
literal|0
argument_list|,
name|PostingsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
decl_stmt|;
assert|assert
name|next
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
specifier|final
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|min
operator|>=
name|this
operator|.
name|max
operator|:
literal|"Scoring backward: min="
operator|+
name|min
operator|+
literal|" while previous max was max="
operator|+
name|this
operator|.
name|max
assert|;
assert|assert
name|min
operator|<=
name|max
operator|:
literal|"max must be greater than min, got min="
operator|+
name|min
operator|+
literal|", and max="
operator|+
name|max
assert|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|collector
operator|=
operator|new
name|AssertingLeafCollector
argument_list|(
name|random
argument_list|,
name|collector
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
specifier|final
name|int
name|next
init|=
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
decl_stmt|;
assert|assert
name|next
operator|>=
name|max
assert|;
if|if
condition|(
name|max
operator|>=
name|maxDoc
operator|||
name|next
operator|>=
name|maxDoc
condition|)
block|{
assert|assert
name|next
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
return|return
name|RandomNumbers
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
name|max
argument_list|,
name|next
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AssertingBulkScorer("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

