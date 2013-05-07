begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|search
operator|.
name|FacetArrays
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
name|facet
operator|.
name|search
operator|.
name|FacetRequest
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
name|facet
operator|.
name|search
operator|.
name|FacetsAggregator
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
name|facet
operator|.
name|search
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link FacetsAggregator} which chains multiple aggregators for aggregating  * the association values of categories that belong to the same category list.  * While nothing prevents you from chaining general purpose aggregators, it is  * only useful for aggregating association values, as each association type is  * written in its own list.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiAssociationsFacetsAggregator
specifier|public
class|class
name|MultiAssociationsFacetsAggregator
implements|implements
name|FacetsAggregator
block|{
DECL|field|categoryAggregators
specifier|private
specifier|final
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|FacetsAggregator
argument_list|>
name|categoryAggregators
decl_stmt|;
DECL|field|aggregators
specifier|private
specifier|final
name|List
argument_list|<
name|FacetsAggregator
argument_list|>
name|aggregators
decl_stmt|;
comment|/**    * Creates a new {@link MultiAssociationsFacetsAggregator} over the given    * aggregators. The mapping is used by    * {@link #rollupValues(FacetRequest, int, int[], int[], FacetArrays)} to    * rollup the values of the specific category by the corresponding    * {@link FacetsAggregator}. However, since each {@link FacetsAggregator}    * handles the associations of a specific type, which could cover multiple    * categories, the aggregation is done on the unique set of aggregators, which    * are identified by their class.    */
DECL|method|MultiAssociationsFacetsAggregator
specifier|public
name|MultiAssociationsFacetsAggregator
parameter_list|(
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|FacetsAggregator
argument_list|>
name|aggregators
parameter_list|)
block|{
name|this
operator|.
name|categoryAggregators
operator|=
name|aggregators
expr_stmt|;
comment|// make sure that each FacetsAggregator class is invoked only once, or
comment|// otherwise categories may be aggregated multiple times.
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|FacetsAggregator
argument_list|>
argument_list|,
name|FacetsAggregator
argument_list|>
name|aggsClasses
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|FacetsAggregator
argument_list|>
argument_list|,
name|FacetsAggregator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetsAggregator
name|fa
range|:
name|aggregators
operator|.
name|values
argument_list|()
control|)
block|{
name|aggsClasses
operator|.
name|put
argument_list|(
name|fa
operator|.
name|getClass
argument_list|()
argument_list|,
name|fa
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|aggregators
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetsAggregator
argument_list|>
argument_list|(
name|aggsClasses
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|MatchingDocs
name|matchingDocs
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FacetsAggregator
name|fa
range|:
name|aggregators
control|)
block|{
name|fa
operator|.
name|aggregate
argument_list|(
name|matchingDocs
argument_list|,
name|clp
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollupValues
specifier|public
name|void
name|rollupValues
parameter_list|(
name|FacetRequest
name|fr
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|categoryAggregators
operator|.
name|get
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
operator|.
name|rollupValues
argument_list|(
name|fr
argument_list|,
name|ordinal
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requiresDocScores
specifier|public
name|boolean
name|requiresDocScores
parameter_list|()
block|{
for|for
control|(
name|FacetsAggregator
name|fa
range|:
name|aggregators
control|)
block|{
if|if
condition|(
name|fa
operator|.
name|requiresDocScores
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

