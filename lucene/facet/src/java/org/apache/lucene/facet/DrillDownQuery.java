begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|Filter
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
name|MatchAllDocsQuery
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
name|TermQuery
import|;
end_import

begin_comment
comment|/**  * A {@link Query} for drill-down over facet categories. You  * should call {@link #add(String, String...)} for every group of categories you  * want to drill-down over.  *<p>  *<b>NOTE:</b> if you choose to create your own {@link Query} by calling  * {@link #term}, it is recommended to wrap it with {@link ConstantScoreQuery}  * and set the {@link ConstantScoreQuery#setBoost(float) boost} to {@code 0.0f},  * so that it does not affect the scores of the documents.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DrillDownQuery
specifier|public
specifier|final
class|class
name|DrillDownQuery
extends|extends
name|Query
block|{
comment|/** Creates a drill-down term. */
DECL|method|term
specifier|public
specifier|static
name|Term
name|term
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
DECL|field|config
specifier|private
specifier|final
name|FacetsConfig
name|config
decl_stmt|;
DECL|field|baseQuery
specifier|private
specifier|final
name|Query
name|baseQuery
decl_stmt|;
DECL|field|dimQueries
specifier|private
specifier|final
name|List
argument_list|<
name|BooleanQuery
operator|.
name|Builder
argument_list|>
name|dimQueries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|drillDownDims
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Used by clone() and DrillSideways */
DECL|method|DrillDownQuery
name|DrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|List
argument_list|<
name|BooleanQuery
operator|.
name|Builder
argument_list|>
name|dimQueries
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
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
name|dimQueries
operator|.
name|addAll
argument_list|(
name|dimQueries
argument_list|)
expr_stmt|;
name|this
operator|.
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|drillDownDims
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Used by DrillSideways */
DECL|method|DrillDownQuery
name|DrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|DrillDownQuery
name|other
parameter_list|)
block|{
name|this
operator|.
name|baseQuery
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
name|other
operator|.
name|baseQuery
operator|==
literal|null
condition|?
operator|new
name|MatchAllDocsQuery
argument_list|()
else|:
name|other
operator|.
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|filter
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|dimQueries
operator|.
name|addAll
argument_list|(
name|other
operator|.
name|dimQueries
argument_list|)
expr_stmt|;
name|this
operator|.
name|drillDownDims
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|drillDownDims
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Creates a new {@code DrillDownQuery} without a base query,     *  to perform a pure browsing query (equivalent to using    *  {@link MatchAllDocsQuery} as base). */
DECL|method|DrillDownQuery
specifier|public
name|DrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|config
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new {@code DrillDownQuery} over the given base query. Can be    *  {@code null}, in which case the result {@link Query} from    *  {@link #rewrite(IndexReader)} will be a pure browsing query, filtering on    *  the added categories only. */
DECL|method|DrillDownQuery
specifier|public
name|DrillDownQuery
parameter_list|(
name|FacetsConfig
name|config
parameter_list|,
name|Query
name|baseQuery
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
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/** Adds one dimension of drill downs; if you pass the same    *  dimension more than once it is OR'd with the previous    *  cofnstraints on that dimension, and all dimensions are    *  AND'd against each other and the base query. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|String
name|indexedField
init|=
name|config
operator|.
name|getDimConfig
argument_list|(
name|dim
argument_list|)
operator|.
name|indexFieldName
decl_stmt|;
name|add
argument_list|(
name|dim
argument_list|,
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|indexedField
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: add a custom drill-down subQuery.  Use this    *  when you have a separate way to drill-down on the    *  dimension than the indexed facet ordinals. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|dim
parameter_list|,
name|Query
name|subQuery
parameter_list|)
block|{
assert|assert
name|drillDownDims
operator|.
name|size
argument_list|()
operator|==
name|dimQueries
operator|.
name|size
argument_list|()
assert|;
if|if
condition|(
name|drillDownDims
operator|.
name|containsKey
argument_list|(
name|dim
argument_list|)
operator|==
literal|false
condition|)
block|{
name|drillDownDims
operator|.
name|put
argument_list|(
name|dim
argument_list|,
name|drillDownDims
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dimQueries
operator|.
name|add
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|index
init|=
name|drillDownDims
operator|.
name|get
argument_list|(
name|dim
argument_list|)
decl_stmt|;
name|dimQueries
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|add
argument_list|(
name|subQuery
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
DECL|method|getFilter
specifier|static
name|Filter
name|getFilter
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
name|ConstantScoreQuery
name|csq
init|=
operator|(
name|ConstantScoreQuery
operator|)
name|query
decl_stmt|;
name|Query
name|sub
init|=
name|csq
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|instanceof
name|Filter
condition|)
block|{
return|return
operator|(
name|Filter
operator|)
name|sub
return|;
block|}
else|else
block|{
return|return
name|getFilter
argument_list|(
name|sub
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|DrillDownQuery
name|clone
parameter_list|()
block|{
return|return
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|,
name|baseQuery
argument_list|,
name|dimQueries
argument_list|,
name|drillDownDims
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
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|Objects
operator|.
name|hash
argument_list|(
name|baseQuery
argument_list|,
name|dimQueries
argument_list|)
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
name|DrillDownQuery
name|other
init|=
operator|(
name|DrillDownQuery
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|baseQuery
argument_list|,
name|other
operator|.
name|baseQuery
argument_list|)
operator|&&
name|dimQueries
operator|.
name|equals
argument_list|(
name|other
operator|.
name|dimQueries
argument_list|)
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
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
name|rewritten
init|=
name|getBooleanQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|rewritten
operator|.
name|clauses
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
return|return
name|rewritten
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
return|return
name|getBooleanQuery
argument_list|()
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|getBooleanQuery
specifier|private
name|BooleanQuery
name|getBooleanQuery
parameter_list|()
block|{
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseQuery
operator|!=
literal|null
condition|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|BooleanQuery
operator|.
name|Builder
name|builder
range|:
name|dimQueries
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getBaseQuery
name|Query
name|getBaseQuery
parameter_list|()
block|{
return|return
name|baseQuery
return|;
block|}
DECL|method|getDrillDownQueries
name|Query
index|[]
name|getDrillDownQueries
parameter_list|()
block|{
name|Query
index|[]
name|dimQueries
init|=
operator|new
name|Query
index|[
name|this
operator|.
name|dimQueries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dimQueries
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|dimQueries
index|[
name|i
index|]
operator|=
name|this
operator|.
name|dimQueries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|dimQueries
return|;
block|}
DECL|method|getDims
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getDims
parameter_list|()
block|{
return|return
name|drillDownDims
return|;
block|}
block|}
end_class

end_unit

