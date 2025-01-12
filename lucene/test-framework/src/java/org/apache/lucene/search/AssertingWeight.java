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
name|LeafReaderContext
import|;
end_import

begin_class
DECL|class|AssertingWeight
class|class
name|AssertingWeight
extends|extends
name|FilterWeight
block|{
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|needsScores
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|method|AssertingWeight
name|AssertingWeight
parameter_list|(
name|Random
name|random
parameter_list|,
name|Weight
name|in
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
specifier|final
name|Scorer
name|inScorer
init|=
name|in
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
assert|assert
name|inScorer
operator|==
literal|null
operator|||
name|inScorer
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
assert|;
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
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|ScorerSupplier
name|scorerSupplier
init|=
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorerSupplier
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
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Evil: make sure computing the cost has no side effects
name|scorerSupplier
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
return|return
name|scorerSupplier
operator|.
name|get
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|scorerSupplier
specifier|public
name|ScorerSupplier
name|scorerSupplier
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ScorerSupplier
name|inScorerSupplier
init|=
name|in
operator|.
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|inScorerSupplier
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ScorerSupplier
argument_list|()
block|{
specifier|private
name|boolean
name|getCalled
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Scorer
name|get
parameter_list|(
name|boolean
name|randomAccess
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|getCalled
operator|==
literal|false
assert|;
name|getCalled
operator|=
literal|true
expr_stmt|;
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
name|inScorerSupplier
operator|.
name|get
argument_list|(
name|randomAccess
argument_list|)
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
specifier|final
name|long
name|cost
init|=
name|inScorerSupplier
operator|.
name|cost
argument_list|()
decl_stmt|;
assert|assert
name|cost
operator|>=
literal|0
assert|;
return|return
name|cost
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BulkScorer
name|inScorer
init|=
name|in
operator|.
name|bulkScorer
argument_list|(
name|context
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
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

