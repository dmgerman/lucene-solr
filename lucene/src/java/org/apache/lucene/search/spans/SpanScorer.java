begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|search
operator|.
name|Explanation
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
name|search
operator|.
name|Weight
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|Similarity
import|;
end_import

begin_comment
comment|/**  * Public for extension only.  */
end_comment

begin_class
DECL|class|SpanScorer
specifier|public
class|class
name|SpanScorer
extends|extends
name|Scorer
block|{
DECL|field|spans
specifier|protected
name|Spans
name|spans
decl_stmt|;
DECL|field|norms
specifier|protected
name|byte
index|[]
name|norms
decl_stmt|;
DECL|field|value
specifier|protected
name|float
name|value
decl_stmt|;
DECL|field|more
specifier|protected
name|boolean
name|more
init|=
literal|true
decl_stmt|;
DECL|field|doc
specifier|protected
name|int
name|doc
decl_stmt|;
DECL|field|freq
specifier|protected
name|float
name|freq
decl_stmt|;
DECL|method|SpanScorer
specifier|protected
name|SpanScorer
parameter_list|(
name|Spans
name|spans
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|,
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|spans
operator|=
name|spans
expr_stmt|;
name|this
operator|.
name|norms
operator|=
name|norms
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|weight
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
name|more
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|setFreqCurrentDoc
argument_list|()
condition|)
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|more
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|spans
operator|.
name|doc
argument_list|()
operator|<
name|target
condition|)
block|{
comment|// setFreqCurrentDoc() leaves spans.doc() ahead
name|more
operator|=
name|spans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|setFreqCurrentDoc
argument_list|()
condition|)
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|setFreqCurrentDoc
specifier|protected
name|boolean
name|setFreqCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|more
condition|)
block|{
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|spans
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freq
operator|=
literal|0.0f
expr_stmt|;
do|do
block|{
name|int
name|matchLength
init|=
name|spans
operator|.
name|end
argument_list|()
operator|-
name|spans
operator|.
name|start
argument_list|()
decl_stmt|;
name|freq
operator|+=
name|getSimilarity
argument_list|()
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
name|more
operator|=
name|spans
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|more
operator|&&
operator|(
name|doc
operator|==
name|spans
operator|.
name|doc
argument_list|()
operator|)
condition|)
do|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|raw
init|=
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|freq
argument_list|)
operator|*
name|value
decl_stmt|;
comment|// raw score
return|return
name|norms
operator|==
literal|null
condition|?
name|raw
else|:
name|raw
operator|*
name|getSimilarity
argument_list|()
operator|.
name|decodeNormValue
argument_list|(
name|norms
index|[
name|doc
index|]
argument_list|)
return|;
comment|// normalize
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|freq
return|;
block|}
comment|/** This method is no longer an official member of {@link Scorer},    * but it is needed by SpanWeight to build an explanation. */
DECL|method|explain
specifier|protected
name|Explanation
name|explain
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|tfExplanation
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|int
name|expDoc
init|=
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|float
name|phraseFreq
init|=
operator|(
name|expDoc
operator|==
name|doc
operator|)
condition|?
name|freq
else|:
literal|0.0f
decl_stmt|;
name|tfExplanation
operator|.
name|setValue
argument_list|(
name|getSimilarity
argument_list|()
operator|.
name|tf
argument_list|(
name|phraseFreq
argument_list|)
argument_list|)
expr_stmt|;
name|tfExplanation
operator|.
name|setDescription
argument_list|(
literal|"tf(phraseFreq="
operator|+
name|phraseFreq
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|tfExplanation
return|;
block|}
block|}
end_class

end_unit

