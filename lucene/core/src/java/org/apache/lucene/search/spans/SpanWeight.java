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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TermContext
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
name|Terms
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
name|similarities
operator|.
name|Similarity
operator|.
name|SimScorer
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Expert-only.  Public for use by other weight implementations  */
end_comment

begin_class
DECL|class|SpanWeight
specifier|public
specifier|abstract
class|class
name|SpanWeight
extends|extends
name|Weight
block|{
DECL|field|similarity
specifier|protected
specifier|final
name|SpanSimilarity
name|similarity
decl_stmt|;
DECL|field|collectorFactory
specifier|protected
specifier|final
name|SpanCollectorFactory
name|collectorFactory
decl_stmt|;
comment|/**    * Create a new SpanWeight    * @param query the parent query    * @param similarity a SpanSimilarity to be used for scoring    * @param collectorFactory a SpanCollectorFactory to be used for Span collection    * @throws IOException on error    */
DECL|method|SpanWeight
specifier|public
name|SpanWeight
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|SpanSimilarity
name|similarity
parameter_list|,
name|SpanCollectorFactory
name|collectorFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
name|this
operator|.
name|collectorFactory
operator|=
name|collectorFactory
expr_stmt|;
block|}
comment|/**    * Collect all TermContexts used by this Weight    * @param contexts a map to add the TermContexts to    */
DECL|method|extractTermContexts
specifier|public
specifier|abstract
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
function_decl|;
comment|/**    * Expert: Return a Spans object iterating over matches from this Weight    * @param ctx a LeafReaderContext for this Spans    * @param acceptDocs a bitset of documents to check    * @param collector a SpanCollector to use for postings data collection    * @return a Spans    * @throws IOException on error    */
DECL|method|getSpans
specifier|public
specifier|abstract
name|Spans
name|getSpans
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Return a Spans object iterating over matches from this Weight, without    * collecting any postings data.    * @param ctx a LeafReaderContext for this Spans    * @param acceptDocs a bitset of documents to check    * @return a Spans    * @throws IOException on error    */
DECL|method|getSpans
specifier|public
specifier|final
name|Spans
name|getSpans
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getSpans
argument_list|(
name|ctx
argument_list|,
name|acceptDocs
argument_list|,
name|collectorFactory
operator|.
name|newCollector
argument_list|()
argument_list|)
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
name|similarity
operator|==
literal|null
condition|?
literal|1.0f
else|:
name|similarity
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
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
if|if
condition|(
name|similarity
operator|!=
literal|null
condition|)
block|{
name|similarity
operator|.
name|normalize
argument_list|(
name|queryNorm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
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
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|similarity
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Terms
name|terms
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|similarity
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
operator|&&
name|terms
operator|.
name|hasPositions
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field \""
operator|+
name|similarity
operator|.
name|getField
argument_list|()
operator|+
literal|"\" was indexed without position data; cannot run SpanQuery (query="
operator|+
name|parentQuery
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|Spans
name|spans
init|=
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|collectorFactory
operator|.
name|newCollector
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|spans
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|SpanScorer
argument_list|(
name|spans
argument_list|,
name|this
argument_list|,
name|similarity
operator|.
name|simScorer
argument_list|(
name|context
argument_list|)
argument_list|)
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
name|SpanScorer
name|scorer
init|=
operator|(
name|SpanScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDoc
operator|==
name|doc
condition|)
block|{
name|float
name|freq
init|=
name|scorer
operator|.
name|sloppyFreq
argument_list|()
decl_stmt|;
name|SimScorer
name|docScorer
init|=
name|similarity
operator|.
name|simScorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Explanation
name|freqExplanation
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|freq
argument_list|,
literal|"phraseFreq="
operator|+
name|freq
argument_list|)
decl_stmt|;
name|Explanation
name|scoreExplanation
init|=
name|docScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freqExplanation
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scoreExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|") ["
operator|+
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"], result of:"
argument_list|,
name|scoreExplanation
argument_list|)
return|;
block|}
block|}
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"no matching term"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

