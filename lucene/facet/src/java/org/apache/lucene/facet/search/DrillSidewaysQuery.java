begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Arrays
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
name|AtomicReader
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
name|index
operator|.
name|DocsEnum
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
name|index
operator|.
name|TermsEnum
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
name|Collector
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
name|util
operator|.
name|Bits
import|;
end_import

begin_class
DECL|class|DrillSidewaysQuery
class|class
name|DrillSidewaysQuery
extends|extends
name|Query
block|{
DECL|field|baseQuery
specifier|final
name|Query
name|baseQuery
decl_stmt|;
DECL|field|drillDownCollector
specifier|final
name|Collector
name|drillDownCollector
decl_stmt|;
DECL|field|drillSidewaysCollectors
specifier|final
name|Collector
index|[]
name|drillSidewaysCollectors
decl_stmt|;
DECL|field|drillDownTerms
specifier|final
name|Term
index|[]
index|[]
name|drillDownTerms
decl_stmt|;
DECL|method|DrillSidewaysQuery
name|DrillSidewaysQuery
parameter_list|(
name|Query
name|baseQuery
parameter_list|,
name|Collector
name|drillDownCollector
parameter_list|,
name|Collector
index|[]
name|drillSidewaysCollectors
parameter_list|,
name|Term
index|[]
index|[]
name|drillDownTerms
parameter_list|)
block|{
name|this
operator|.
name|baseQuery
operator|=
name|baseQuery
expr_stmt|;
name|this
operator|.
name|drillDownCollector
operator|=
name|drillDownCollector
expr_stmt|;
name|this
operator|.
name|drillSidewaysCollectors
operator|=
name|drillSidewaysCollectors
expr_stmt|;
name|this
operator|.
name|drillDownTerms
operator|=
name|drillDownTerms
expr_stmt|;
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
return|return
literal|"DrillSidewaysQuery"
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
name|Query
name|newQuery
init|=
name|baseQuery
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Query
name|rewrittenQuery
init|=
name|newQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenQuery
operator|==
name|newQuery
condition|)
block|{
break|break;
block|}
name|newQuery
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
if|if
condition|(
name|newQuery
operator|==
name|baseQuery
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|DrillSidewaysQuery
argument_list|(
name|newQuery
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownTerms
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|baseWeight
init|=
name|baseQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|()
block|{
annotation|@
name|Override
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
name|baseWeight
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
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|baseQuery
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
name|baseWeight
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
name|topLevelBoost
parameter_list|)
block|{
name|baseWeight
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
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
comment|// TODO: would be nice if AssertingIndexSearcher
comment|// confirmed this for us
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|DrillSidewaysScorer
operator|.
name|DocsEnumsAndFreq
index|[]
name|dims
init|=
operator|new
name|DrillSidewaysScorer
operator|.
name|DocsEnumsAndFreq
index|[
name|drillDownTerms
operator|.
name|length
index|]
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
literal|null
decl_stmt|;
name|String
name|lastField
init|=
literal|null
decl_stmt|;
name|int
name|nullCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|dims
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|dims
index|[
name|dim
index|]
operator|=
operator|new
name|DrillSidewaysScorer
operator|.
name|DocsEnumsAndFreq
argument_list|()
expr_stmt|;
name|dims
index|[
name|dim
index|]
operator|.
name|sidewaysCollector
operator|=
name|drillSidewaysCollectors
index|[
name|dim
index|]
expr_stmt|;
name|String
name|field
init|=
name|drillDownTerms
index|[
name|dim
index|]
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
decl_stmt|;
name|dims
index|[
name|dim
index|]
operator|.
name|dim
operator|=
name|drillDownTerms
index|[
name|dim
index|]
index|[
literal|0
index|]
operator|.
name|text
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastField
operator|==
literal|null
operator|||
operator|!
name|lastField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
name|lastField
operator|=
name|field
expr_stmt|;
block|}
name|dims
index|[
name|dim
index|]
operator|.
name|docsEnums
operator|=
operator|new
name|DocsEnum
index|[
name|drillDownTerms
index|[
name|dim
index|]
operator|.
name|length
index|]
expr_stmt|;
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
block|{
name|nullCount
operator|++
expr_stmt|;
continue|continue;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|drillDownTerms
index|[
name|dim
index|]
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|drillDownTerms
index|[
name|dim
index|]
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
name|DocsEnum
name|docsEnum
init|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|docsEnum
operator|!=
literal|null
condition|)
block|{
name|dims
index|[
name|dim
index|]
operator|.
name|docsEnums
index|[
name|i
index|]
operator|=
name|docsEnum
expr_stmt|;
name|dims
index|[
name|dim
index|]
operator|.
name|maxCost
operator|=
name|Math
operator|.
name|max
argument_list|(
name|dims
index|[
name|dim
index|]
operator|.
name|maxCost
argument_list|,
name|docsEnum
operator|.
name|cost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|nullCount
operator|>
literal|1
operator|||
operator|(
name|nullCount
operator|==
literal|1
operator|&&
name|dims
operator|.
name|length
operator|==
literal|1
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Sort drill-downs by most restrictive first:
name|Arrays
operator|.
name|sort
argument_list|(
name|dims
argument_list|)
expr_stmt|;
comment|// TODO: it could be better if we take acceptDocs
comment|// into account instead of baseScorer?
name|Scorer
name|baseScorer
init|=
name|baseWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|scoreDocsInOrder
argument_list|,
literal|false
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseScorer
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
name|DrillSidewaysScorer
argument_list|(
name|this
argument_list|,
name|context
argument_list|,
name|baseScorer
argument_list|,
name|drillDownCollector
argument_list|,
name|dims
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|// TODO: these should do "deeper" equals/hash on the 2-D drillDownTerms array
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|baseQuery
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|baseQuery
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|drillDownCollector
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|drillDownCollector
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|drillDownTerms
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|drillSidewaysCollectors
argument_list|)
expr_stmt|;
return|return
name|result
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|DrillSidewaysQuery
name|other
init|=
operator|(
name|DrillSidewaysQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|baseQuery
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|baseQuery
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|baseQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|baseQuery
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|drillDownCollector
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|drillDownCollector
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|drillDownCollector
operator|.
name|equals
argument_list|(
name|other
operator|.
name|drillDownCollector
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|drillDownTerms
argument_list|,
name|other
operator|.
name|drillDownTerms
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|drillSidewaysCollectors
argument_list|,
name|other
operator|.
name|drillSidewaysCollectors
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

