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
name|TableScan
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a Solr collection.  */
end_comment

begin_class
DECL|class|SolrTableScan
class|class
name|SolrTableScan
extends|extends
name|TableScan
implements|implements
name|SolrRel
block|{
DECL|field|solrTable
specifier|private
specifier|final
name|SolrTable
name|solrTable
decl_stmt|;
DECL|field|projectRowType
specifier|private
specifier|final
name|RelDataType
name|projectRowType
decl_stmt|;
comment|/**    * Creates a SolrTableScan.    *    * @param cluster        Cluster    * @param traitSet       Traits    * @param table          Table    * @param solrTable      Solr table    * @param projectRowType Fields and types to project; null to project raw row    */
DECL|method|SolrTableScan
name|SolrTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|SolrTable
name|solrTable
parameter_list|,
name|RelDataType
name|projectRowType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrTable
operator|=
name|solrTable
expr_stmt|;
name|this
operator|.
name|projectRowType
operator|=
name|projectRowType
expr_stmt|;
assert|assert
name|solrTable
operator|!=
literal|null
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|SolrRel
operator|.
name|CONVENTION
assert|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|deriveRowType
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|projectRowType
operator|!=
literal|null
condition|?
name|projectRowType
else|:
name|super
operator|.
name|deriveRowType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|SolrToEnumerableConverterRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|SolrRules
operator|.
name|RULES
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
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
name|solrTable
operator|=
name|solrTable
expr_stmt|;
name|implementor
operator|.
name|table
operator|=
name|table
expr_stmt|;
block|}
block|}
end_class

end_unit

