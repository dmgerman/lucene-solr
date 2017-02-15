begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.sql
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|Convention
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|ops
operator|.
name|BooleanOperation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Relational expression that uses Solr calling convention.  */
end_comment

begin_interface
DECL|interface|SolrRel
interface|interface
name|SolrRel
extends|extends
name|RelNode
block|{
DECL|method|implement
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
function_decl|;
comment|/** Calling convention for relational operations that occur in Solr. */
DECL|field|CONVENTION
name|Convention
name|CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"Solr"
argument_list|,
name|SolrRel
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Callback for the implementation process that converts a tree of {@link SolrRel} nodes into a Solr query. */
DECL|class|Implementor
class|class
name|Implementor
block|{
DECL|field|fieldMappings
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reverseAggMappings
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|reverseAggMappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|query
name|String
name|query
init|=
literal|null
decl_stmt|;
DECL|field|havingPredicate
name|String
name|havingPredicate
decl_stmt|;
DECL|field|negativeQuery
name|boolean
name|negativeQuery
decl_stmt|;
DECL|field|limitValue
name|String
name|limitValue
init|=
literal|null
decl_stmt|;
DECL|field|orders
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|orders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|buckets
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|metricPairs
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|metricPairs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|table
name|RelOptTable
name|table
decl_stmt|;
DECL|field|solrTable
name|SolrTable
name|solrTable
decl_stmt|;
DECL|method|addFieldMapping
name|void
name|addFieldMapping
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
operator|!
name|fieldMappings
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|this
operator|.
name|fieldMappings
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addReverseAggMapping
name|void
name|addReverseAggMapping
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
operator|!
name|reverseAggMappings
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|this
operator|.
name|reverseAggMappings
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addQuery
name|void
name|addQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
DECL|method|setNegativeQuery
name|void
name|setNegativeQuery
parameter_list|(
name|boolean
name|negativeQuery
parameter_list|)
block|{
name|this
operator|.
name|negativeQuery
operator|=
name|negativeQuery
expr_stmt|;
block|}
DECL|method|addOrder
name|void
name|addOrder
parameter_list|(
name|String
name|column
parameter_list|,
name|String
name|direction
parameter_list|)
block|{
name|column
operator|=
name|this
operator|.
name|fieldMappings
operator|.
name|getOrDefault
argument_list|(
name|column
argument_list|,
name|column
argument_list|)
expr_stmt|;
name|this
operator|.
name|orders
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<>
argument_list|(
name|column
argument_list|,
name|direction
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addBucket
name|void
name|addBucket
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
name|bucket
operator|=
name|this
operator|.
name|fieldMappings
operator|.
name|getOrDefault
argument_list|(
name|bucket
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|this
operator|.
name|buckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
DECL|method|addMetricPair
name|void
name|addMetricPair
parameter_list|(
name|String
name|outName
parameter_list|,
name|String
name|metric
parameter_list|,
name|String
name|column
parameter_list|)
block|{
name|column
operator|=
name|this
operator|.
name|fieldMappings
operator|.
name|getOrDefault
argument_list|(
name|column
argument_list|,
name|column
argument_list|)
expr_stmt|;
name|this
operator|.
name|metricPairs
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<>
argument_list|(
name|metric
argument_list|,
name|column
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|metricIdentifier
init|=
name|metric
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"("
operator|+
name|column
operator|+
literal|")"
decl_stmt|;
if|if
condition|(
name|outName
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|addFieldMapping
argument_list|(
name|outName
argument_list|,
name|metricIdentifier
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setHavingPredicate
name|void
name|setHavingPredicate
parameter_list|(
name|String
name|havingPredicate
parameter_list|)
block|{
name|this
operator|.
name|havingPredicate
operator|=
name|havingPredicate
expr_stmt|;
block|}
DECL|method|setLimit
name|void
name|setLimit
parameter_list|(
name|String
name|limit
parameter_list|)
block|{
name|limitValue
operator|=
name|limit
expr_stmt|;
block|}
DECL|method|visitChild
name|void
name|visitChild
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
assert|assert
name|ordinal
operator|==
literal|0
assert|;
operator|(
operator|(
name|SolrRel
operator|)
name|input
operator|)
operator|.
name|implement
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

