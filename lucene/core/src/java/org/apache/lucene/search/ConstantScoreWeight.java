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
name|Set
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
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/**  * A Weight that has a constant score equal to the boost of the wrapped query.  * This is typically useful when building queries which do not produce  * meaningful scores and are mostly useful for filtering.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|ConstantScoreWeight
specifier|public
specifier|abstract
class|class
name|ConstantScoreWeight
extends|extends
name|Weight
block|{
DECL|field|score
specifier|private
specifier|final
name|float
name|score
decl_stmt|;
DECL|method|ConstantScoreWeight
specifier|protected
name|ConstantScoreWeight
parameter_list|(
name|Query
name|query
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
comment|// most constant-score queries don't wrap index terms
comment|// eg. geo filters, doc values queries, ...
comment|// override if your constant-score query does wrap terms
block|}
comment|/** Return the score produced by this {@link Weight}. */
DECL|method|score
specifier|protected
specifier|final
name|float
name|score
parameter_list|()
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|s
init|=
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|exists
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|exists
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TwoPhaseIterator
name|twoPhase
init|=
name|s
operator|.
name|twoPhaseIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|twoPhase
operator|==
literal|null
condition|)
block|{
name|exists
operator|=
name|s
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
expr_stmt|;
block|}
else|else
block|{
name|exists
operator|=
name|twoPhase
operator|.
name|approximation
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
operator|&&
name|twoPhase
operator|.
name|matches
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|exists
condition|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|,
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
operator|(
name|score
operator|==
literal|1f
condition|?
literal|""
else|:
literal|"^"
operator|+
name|score
operator|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
name|getQuery
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't match id "
operator|+
name|doc
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

