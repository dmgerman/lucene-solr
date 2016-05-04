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
name|RelOptCluster
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
name|RelTraitSet
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
name|rel
operator|.
name|core
operator|.
name|Aggregate
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
name|core
operator|.
name|AggregateCall
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
name|sql
operator|.
name|SqlAggFunction
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|ImmutableBitSet
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.rel.core.Aggregate} relational expression in Solr.  */
end_comment

begin_class
DECL|class|SolrAggregate
class|class
name|SolrAggregate
extends|extends
name|Aggregate
implements|implements
name|SolrRel
block|{
DECL|field|SUPPORTED_AGGREGATIONS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|SqlAggFunction
argument_list|>
name|SUPPORTED_AGGREGATIONS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM
argument_list|,
name|SqlStdOperatorTable
operator|.
name|SUM0
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MIN
argument_list|,
name|SqlStdOperatorTable
operator|.
name|MAX
argument_list|,
name|SqlStdOperatorTable
operator|.
name|AVG
argument_list|)
decl_stmt|;
DECL|method|SolrAggregate
name|SolrAggregate
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
name|indicator
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|SolrRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|child
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|Aggregate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|boolean
name|indicator
parameter_list|,
name|ImmutableBitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
return|return
operator|new
name|SolrAggregate
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|indicator
argument_list|,
name|groupSet
argument_list|,
name|groupSets
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
DECL|method|implement
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inNames
init|=
name|SolrRules
operator|.
name|solrFieldNames
argument_list|(
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|outNames
init|=
name|SolrRules
operator|.
name|solrFieldNames
argument_list|(
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|metrics
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
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
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggCalls
control|)
block|{
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metric
init|=
name|toSolrMetric
argument_list|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
argument_list|,
name|inNames
argument_list|,
name|aggCall
operator|.
name|getArgList
argument_list|()
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|metric
argument_list|)
expr_stmt|;
name|fieldMappings
operator|.
name|put
argument_list|(
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|,
name|metric
operator|.
name|getKey
argument_list|()
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
name|metric
operator|.
name|getValue
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|group
range|:
name|groupSet
control|)
block|{
specifier|final
name|String
name|inName
init|=
name|inNames
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|buckets
operator|.
name|add
argument_list|(
name|inName
argument_list|)
expr_stmt|;
name|fieldMappings
operator|.
name|put
argument_list|(
name|inName
argument_list|,
name|inName
argument_list|)
expr_stmt|;
block|}
name|implementor
operator|.
name|addBuckets
argument_list|(
name|buckets
argument_list|)
expr_stmt|;
name|implementor
operator|.
name|addMetrics
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|implementor
operator|.
name|addFieldMappings
argument_list|(
name|fieldMappings
argument_list|)
expr_stmt|;
block|}
DECL|method|toSolrMetric
specifier|private
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toSolrMetric
parameter_list|(
name|SqlAggFunction
name|aggregation
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|inNames
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|args
parameter_list|)
block|{
switch|switch
condition|(
name|args
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
if|if
condition|(
name|aggregation
operator|.
name|equals
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COUNT
argument_list|)
condition|)
block|{
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|,
literal|"*"
argument_list|)
return|;
block|}
case|case
literal|1
case|:
specifier|final
name|String
name|inName
init|=
name|inNames
operator|.
name|get
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|SUPPORTED_AGGREGATIONS
operator|.
name|contains
argument_list|(
name|aggregation
argument_list|)
condition|)
block|{
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|,
name|inName
argument_list|)
return|;
block|}
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Invalid aggregation "
operator|+
name|aggregation
operator|+
literal|" with args "
operator|+
name|args
operator|+
literal|" with names"
operator|+
name|inNames
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SolrAggregate.java
end_comment

end_unit

