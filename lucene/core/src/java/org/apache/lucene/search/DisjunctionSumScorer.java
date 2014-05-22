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

begin_comment
comment|/** A Scorer for OR like queries, counterpart of<code>ConjunctionScorer</code>.  * This Scorer implements {@link Scorer#advance(int)} and uses advance() on the given Scorers.   */
end_comment

begin_class
DECL|class|DisjunctionSumScorer
specifier|final
class|class
name|DisjunctionSumScorer
extends|extends
name|DisjunctionScorer
block|{
DECL|field|score
specifier|private
name|double
name|score
decl_stmt|;
DECL|field|coord
specifier|private
specifier|final
name|float
index|[]
name|coord
decl_stmt|;
comment|/** Construct a<code>DisjunctionScorer</code>.    * @param weight The weight to be used.    * @param subScorers Array of at least two subscorers.    * @param coord Table of coordination factors    */
DECL|method|DisjunctionSumScorer
name|DisjunctionSumScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
index|[]
name|subScorers
parameter_list|,
name|float
index|[]
name|coord
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|subScorers
argument_list|)
expr_stmt|;
name|this
operator|.
name|coord
operator|=
name|coord
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|()
block|{
name|score
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accum
specifier|protected
name|void
name|accum
parameter_list|(
name|Scorer
name|subScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|score
operator|+=
name|subScorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFinal
specifier|protected
name|float
name|getFinal
parameter_list|()
block|{
return|return
operator|(
name|float
operator|)
name|score
operator|*
name|coord
index|[
name|freq
index|]
return|;
block|}
block|}
end_class

end_unit

