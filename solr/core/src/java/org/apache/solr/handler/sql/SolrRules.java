begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
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
name|RelOptRule
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
name|validate
operator|.
name|SqlValidatorUtil
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Rules and relational operators for  * {@link SolrRel#CONVENTION}  * calling convention.  */
end_comment

begin_class
DECL|class|SolrRules
specifier|public
class|class
name|SolrRules
block|{
DECL|method|SolrRules
specifier|private
name|SolrRules
parameter_list|()
block|{}
DECL|field|RULES
specifier|static
specifier|final
name|RelOptRule
index|[]
name|RULES
init|=
block|{
name|SolrFilterRule
operator|.
name|INSTANCE
block|,
name|SolrProjectRule
operator|.
name|INSTANCE
block|,
comment|//    SolrSortRule.INSTANCE
block|}
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
decl_stmt|;
DECL|method|SolrConverterRule
specifier|public
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
name|Predicates
operator|.
expr|<
name|RelNode
operator|>
name|alwaysTrue
argument_list|()
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrConverterRule
specifier|public
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
name|?
super|super
name|R
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
name|predicate
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
name|this
operator|.
name|out
operator|=
name|SolrRel
operator|.
name|CONVENTION
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
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|SolrFilterRule
name|INSTANCE
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
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.logical.LogicalProject} to a {@link SolrProject}.    */
DECL|class|SolrProjectRule
specifier|private
specifier|static
class|class
name|SolrProjectRule
extends|extends
name|SolrConverterRule
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|SolrProjectRule
name|INSTANCE
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
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
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
comment|/**    * Rule to convert a {@link org.apache.calcite.rel.core.Sort} to a {@link SolrSort}.    */
comment|//  private static class SolrSortRule extends RelOptRule {
comment|//    private static final com.google.common.base.Predicate<Sort> SORT_PREDICATE =
comment|//            input -> {
comment|//              // CQL has no support for offsets
comment|//              return input.offset == null;
comment|//            };
comment|//    private static final com.google.common.base.Predicate<SolrFilter> FILTER_PREDICATE =
comment|//            input -> {
comment|//              // We can only use implicit sorting within a single partition
comment|//              return input.isSinglePartition();
comment|//            };
comment|//    private static final RelOptRuleOperand SOLR_OP =
comment|//        operand(SolrToEnumerableConverter.class,
comment|//        operand(SolrFilter.class, null, FILTER_PREDICATE, any()));
comment|//
comment|//    private static final SolrSortRule INSTANCE = new SolrSortRule();
comment|//
comment|//    private SolrSortRule() {
comment|//      super(operand(Sort.class, null, SORT_PREDICATE, SOLR_OP), "SolrSortRule");
comment|//    }
comment|//
comment|//    public RelNode convert(Sort sort, SolrFilter filter) {
comment|//      final RelTraitSet traitSet =
comment|//          sort.getTraitSet().replace(SolrRel.CONVENTION)
comment|//              .replace(sort.getCollation());
comment|//      return new SolrSort(sort.getCluster(), traitSet,
comment|//          convert(sort.getInput(), traitSet.replace(RelCollations.EMPTY)),
comment|//          sort.getCollation(), filter.getImplicitCollation(), sort.fetch);
comment|//    }
comment|//
comment|//    public boolean matches(RelOptRuleCall call) {
comment|//      final Sort sort = call.rel(0);
comment|//      final SolrFilter filter = call.rel(2);
comment|//      return collationsCompatible(sort.getCollation(), filter.getImplicitCollation());
comment|//    }
comment|//
comment|//    /** Check if it is possible to exploit native CQL sorting for a given collation.
comment|//     *
comment|//     * @return True if it is possible to achieve this sort in Solr
comment|//     */
comment|//    private boolean collationsCompatible(RelCollation sortCollation, RelCollation implicitCollation) {
comment|//      List<RelFieldCollation> sortFieldCollations = sortCollation.getFieldCollations();
comment|//      List<RelFieldCollation> implicitFieldCollations = implicitCollation.getFieldCollations();
comment|//
comment|//      if (sortFieldCollations.size()> implicitFieldCollations.size()) {
comment|//        return false;
comment|//      }
comment|//      if (sortFieldCollations.size() == 0) {
comment|//        return true;
comment|//      }
comment|//
comment|//      // Check if we need to reverse the order of the implicit collation
comment|//      boolean reversed = reverseDirection(sortFieldCollations.get(0).getDirection())
comment|//          == implicitFieldCollations.get(0).getDirection();
comment|//
comment|//      for (int i = 0; i< sortFieldCollations.size(); i++) {
comment|//        RelFieldCollation sorted = sortFieldCollations.get(i);
comment|//        RelFieldCollation implied = implicitFieldCollations.get(i);
comment|//
comment|//        // Check that the fields being sorted match
comment|//        if (sorted.getFieldIndex() != implied.getFieldIndex()) {
comment|//          return false;
comment|//        }
comment|//
comment|//        // Either all fields must be sorted in the same direction
comment|//        // or the opposite direction based on whether we decided
comment|//        // if the sort direction should be reversed above
comment|//        RelFieldCollation.Direction sortDirection = sorted.getDirection();
comment|//        RelFieldCollation.Direction implicitDirection = implied.getDirection();
comment|//        if ((!reversed&& sortDirection != implicitDirection)
comment|//                || (reversed&& reverseDirection(sortDirection) != implicitDirection)) {
comment|//          return false;
comment|//        }
comment|//      }
comment|//
comment|//      return true;
comment|//    }
comment|//
comment|//    /** Find the reverse of a given collation direction.
comment|//     *
comment|//     * @return Reverse of the input direction
comment|//     */
comment|//    private RelFieldCollation.Direction reverseDirection(RelFieldCollation.Direction direction) {
comment|//      switch(direction) {
comment|//      case ASCENDING:
comment|//      case STRICTLY_ASCENDING:
comment|//        return RelFieldCollation.Direction.DESCENDING;
comment|//      case DESCENDING:
comment|//      case STRICTLY_DESCENDING:
comment|//        return RelFieldCollation.Direction.ASCENDING;
comment|//      default:
comment|//        return null;
comment|//      }
comment|//    }
comment|//
comment|//    /** @see org.apache.calcite.rel.convert.ConverterRule */
comment|//    public void onMatch(RelOptRuleCall call) {
comment|//      final Sort sort = call.rel(0);
comment|//      SolrFilter filter = call.rel(2);
comment|//      final RelNode converted = convert(sort, filter);
comment|//      if (converted != null) {
comment|//        call.transformTo(converted);
comment|//      }
comment|//    }
comment|//  }
block|}
end_class

end_unit

