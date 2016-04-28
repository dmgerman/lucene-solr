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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Relational expression that uses Solr calling convention.  */
end_comment

begin_interface
DECL|interface|SolrRel
specifier|public
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
comment|/** Calling convention for relational operations that occur in Cassandra. */
DECL|field|CONVENTION
name|Convention
name|CONVENTION
init|=
operator|new
name|Convention
operator|.
name|Impl
argument_list|(
literal|"SOLR"
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
DECL|field|filterQueries
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|filterQueries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|limitValue
name|String
name|limitValue
init|=
literal|null
decl_stmt|;
DECL|field|order
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|order
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
comment|/** Adds newly projected fields and restricted filterQueries.      *      * @param fieldMappings New fields to be projected from a query      * @param filterQueries New filterQueries to be applied to the query      */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMappings
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|filterQueries
parameter_list|)
block|{
if|if
condition|(
name|fieldMappings
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fieldMappings
operator|.
name|putAll
argument_list|(
name|fieldMappings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterQueries
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|filterQueries
operator|.
name|addAll
argument_list|(
name|filterQueries
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addOrder
specifier|public
name|void
name|addOrder
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|newOrder
parameter_list|)
block|{
name|order
operator|.
name|addAll
argument_list|(
name|newOrder
argument_list|)
expr_stmt|;
block|}
DECL|method|setLimit
specifier|public
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
specifier|public
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

