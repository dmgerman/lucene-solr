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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|search
operator|.
name|BoostQuery
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
name|IndexSearcher
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
name|Query
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

begin_comment
comment|/**  * Counterpart of {@link BoostQuery} for spans.  */
end_comment

begin_class
DECL|class|SpanBoostQuery
specifier|public
specifier|final
class|class
name|SpanBoostQuery
extends|extends
name|SpanQuery
block|{
DECL|field|query
specifier|private
specifier|final
name|SpanQuery
name|query
decl_stmt|;
DECL|field|boost
specifier|private
specifier|final
name|float
name|boost
decl_stmt|;
comment|/** Sole constructor: wrap {@code query} in such a way that the produced    *  scores will be boosted by {@code boost}. */
DECL|method|SpanBoostQuery
specifier|public
name|SpanBoostQuery
parameter_list|(
name|SpanQuery
name|query
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/**    * Return the wrapped {@link SpanQuery}.    */
DECL|method|getQuery
specifier|public
name|SpanQuery
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
comment|/**    * Return the applied boost.    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SpanBoostQuery
name|that
init|=
operator|(
name|SpanBoostQuery
operator|)
name|obj
decl_stmt|;
return|return
name|query
operator|.
name|equals
argument_list|(
name|that
operator|.
name|query
argument_list|)
operator|&&
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
operator|==
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|that
operator|.
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|query
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|boost
operator|==
literal|1f
condition|)
block|{
return|return
name|query
return|;
block|}
specifier|final
name|SpanQuery
name|rewritten
init|=
operator|(
name|SpanQuery
operator|)
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
name|rewritten
condition|)
block|{
return|return
operator|new
name|SpanBoostQuery
argument_list|(
name|rewritten
argument_list|,
name|boost
argument_list|)
return|;
block|}
if|if
condition|(
name|query
operator|.
name|getClass
argument_list|()
operator|==
name|SpanBoostQuery
operator|.
name|class
condition|)
block|{
name|SpanBoostQuery
name|in
init|=
operator|(
name|SpanBoostQuery
operator|)
name|query
decl_stmt|;
return|return
operator|new
name|SpanBoostQuery
argument_list|(
name|in
operator|.
name|query
argument_list|,
name|boost
operator|*
name|in
operator|.
name|boost
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|")^"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|query
operator|.
name|getField
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SpanWeight
name|weight
init|=
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
decl_stmt|;
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
return|return
name|weight
return|;
block|}
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|terms
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|weight
operator|.
name|extractTermContexts
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|weight
operator|.
name|normalize
argument_list|(
literal|1f
argument_list|,
name|boost
argument_list|)
expr_stmt|;
return|return
operator|new
name|SpanWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|,
name|terms
argument_list|)
block|{
annotation|@
name|Override
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
name|weight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
return|return
name|weight
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
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|weight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|SpanBoostQuery
operator|.
name|this
operator|.
name|boost
operator|*
name|boost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|Postings
name|requiredPostings
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|getSpans
argument_list|(
name|ctx
argument_list|,
name|requiredPostings
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SpanScorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
name|weight
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

