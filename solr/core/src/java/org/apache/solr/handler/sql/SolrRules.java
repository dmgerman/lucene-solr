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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|*
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
name|RelCollations
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
name|convert
operator|.
name|ConverterRule
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
name|Sort
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
name|logical
operator|.
name|LogicalAggregate
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
name|logical
operator|.
name|LogicalFilter
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
name|logical
operator|.
name|LogicalProject
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
name|logical
operator|.
name|LogicalSort
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
name|type
operator|.
name|RelDataType
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexInputRef
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexVisitorImpl
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
name|SqlKind
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
name|validate
operator|.
name|SqlValidatorUtil
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Rules and relational operators for  * {@link SolrRel#CONVENTION}  * calling convention.  */
end_comment

begin_class
DECL|class|SolrRules
class|class
name|SolrRules
block|{
DECL|field|RULES
specifier|static
specifier|final
name|RelOptRule
index|[]
name|RULES
init|=
block|{
name|SolrSortRule
operator|.
name|SORT_RULE
block|,
name|SolrFilterRule
operator|.
name|FILTER_RULE
block|,
name|SolrProjectRule
operator|.
name|PROJECT_RULE
block|,
name|SolrAggregateRule
operator|.
name|AGGREGATE_RULE
block|,   }
decl_stmt|;
DECL|method|solrFieldNames
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|solrFieldNames
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|rowType
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** Translator from {@link RexNode} to strings in Solr's expression language. */
DECL|class|RexToSolrTranslator
specifier|static
class|class
name|RexToSolrTranslator
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
DECL|field|typeFactory
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
DECL|field|inFields
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
decl_stmt|;
DECL|method|RexToSolrTranslator
name|RexToSolrTranslator
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|inFields
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|this
operator|.
name|inFields
operator|=
name|inFields
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitInputRef
specifier|public
name|String
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|inFields
operator|.
name|get
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|visitCall
specifier|public
name|String
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|visitList
argument_list|(
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|CAST
condition|)
block|{
return|return
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
DECL|method|visitList
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|visitList
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|list
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
block|}
comment|/** Base class for planner rules that convert a relational expression to Solr calling convention. */
DECL|class|SolrConverterRule
specifier|abstract
specifier|static
class|class
name|SolrConverterRule
extends|extends
name|ConverterRule
block|{
DECL|field|out
specifier|final
name|Convention
name|out
init|=
name|SolrRel
operator|.
name|CONVENTION
decl_stmt|;
DECL|method|SolrConverterRule
name|SolrConverterRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|relNode
lambda|->
literal|true
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrConverterRule
parameter_list|<
name|R
extends|extends
name|RelNode
parameter_list|>
name|SolrConverterRule
parameter_list|(
name|Class
argument_list|<
name|R
argument_list|>
name|clazz
parameter_list|,
name|Predicate
argument_list|<
name|RelNode
argument_list|>
name|predicate
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|SolrRel
operator|.
name|CONVENTION
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Rule to convert a {@link LogicalFilter} to a {@link SolrFilter}.    */
DECL|class|SolrFilterRule
specifier|private
specifier|static
class|class
name|SolrFilterRule
extends|extends
name|SolrConverterRule
block|{
DECL|method|isNotFilterByExpr
specifier|private
specifier|static
name|boolean
name|isNotFilterByExpr
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexNodes
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
comment|// We dont have a way to filter by result of aggregator now
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|RexNode
name|rexNode
range|:
name|rexNodes
control|)
block|{
if|if
condition|(
name|rexNode
operator|instanceof
name|RexCall
condition|)
block|{
name|result
operator|=
name|result
operator|&&
name|isNotFilterByExpr
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|rexNode
operator|)
operator|.
name|getOperands
argument_list|()
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rexNode
operator|instanceof
name|RexInputRef
condition|)
block|{
name|result
operator|=
name|result
operator|&&
operator|!
name|fieldNames
operator|.
name|get
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|rexNode
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"EXPR$"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|field|FILTER_PREDICATE
specifier|private
specifier|static
specifier|final
name|Predicate
argument_list|<
name|RelNode
argument_list|>
name|FILTER_PREDICATE
init|=
name|relNode
lambda|->
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|filterOperands
init|=
operator|(
call|(
name|RexCall
call|)
argument_list|(
operator|(
name|LogicalFilter
operator|)
name|relNode
argument_list|)
operator|.
name|getCondition
argument_list|()
operator|)
operator|.
name|getOperands
argument_list|()
decl_stmt|;
return|return
name|isNotFilterByExpr
argument_list|(
name|filterOperands
argument_list|,
name|SolrRules
operator|.
name|solrFieldNames
argument_list|(
name|relNode
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
decl_stmt|;
DECL|field|FILTER_RULE
specifier|private
specifier|static
specifier|final
name|SolrFilterRule
name|FILTER_RULE
init|=
operator|new
name|SolrFilterRule
argument_list|()
decl_stmt|;
DECL|method|SolrFilterRule
specifier|private
name|SolrFilterRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|FILTER_PREDICATE
argument_list|,
literal|"SolrFilterRule"
argument_list|)
expr_stmt|;
block|}
DECL|method|convert
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalFilter
name|filter
init|=
operator|(
name|LogicalFilter
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrFilter
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link LogicalProject} to a {@link SolrProject}.    */
DECL|class|SolrProjectRule
specifier|private
specifier|static
class|class
name|SolrProjectRule
extends|extends
name|SolrConverterRule
block|{
DECL|field|PROJECT_RULE
specifier|private
specifier|static
specifier|final
name|SolrProjectRule
name|PROJECT_RULE
init|=
operator|new
name|SolrProjectRule
argument_list|()
decl_stmt|;
DECL|method|SolrProjectRule
specifier|private
name|SolrProjectRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
literal|"SolrProjectRule"
argument_list|)
expr_stmt|;
block|}
DECL|method|convert
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalProject
name|project
init|=
operator|(
name|LogicalProject
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|project
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrProject
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|converted
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link LogicalSort} to a {@link SolrSort}.    */
DECL|class|SolrSortRule
specifier|private
specifier|static
class|class
name|SolrSortRule
extends|extends
name|SolrConverterRule
block|{
DECL|field|SORT_RULE
specifier|static
specifier|final
name|SolrSortRule
name|SORT_RULE
init|=
operator|new
name|SolrSortRule
argument_list|(
name|LogicalSort
operator|.
name|class
argument_list|,
literal|"SolrSortRule"
argument_list|)
decl_stmt|;
DECL|method|SolrSortRule
name|SolrSortRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|convert
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
operator|(
name|Sort
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
operator|.
name|replace
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrSort
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|replace
argument_list|(
name|RelCollations
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert an {@link LogicalAggregate} to an {@link SolrAggregate}.    */
DECL|class|SolrAggregateRule
specifier|private
specifier|static
class|class
name|SolrAggregateRule
extends|extends
name|SolrConverterRule
block|{
comment|//    private static final Predicate<RelNode> AGGREGATE_PREDICTE = relNode ->
comment|//        Aggregate.IS_SIMPLE.apply(((LogicalAggregate)relNode));//&&
comment|//        !((LogicalAggregate)relNode).containsDistinctCall();
DECL|field|AGGREGATE_RULE
specifier|private
specifier|static
specifier|final
name|RelOptRule
name|AGGREGATE_RULE
init|=
operator|new
name|SolrAggregateRule
argument_list|()
decl_stmt|;
DECL|method|SolrAggregateRule
specifier|private
name|SolrAggregateRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
literal|"SolrAggregateRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|convert
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalAggregate
name|agg
init|=
operator|(
name|LogicalAggregate
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|agg
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|SolrAggregate
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|agg
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|simplify
argument_list|()
argument_list|)
argument_list|,
name|agg
operator|.
name|indicator
argument_list|,
name|agg
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|agg
operator|.
name|getGroupSets
argument_list|()
argument_list|,
name|agg
operator|.
name|getAggCallList
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

