begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
package|;
end_package

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
name|Sort
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
name|schema
operator|.
name|SchemaField
import|;
end_import

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
name|List
import|;
end_import

begin_comment
comment|/**  * Defines a grouping command.  * This is an abstraction on how the {@link Collector} instances are created  * and how the results are retrieved from the {@link Collector} instances.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Command
specifier|public
interface|interface
name|Command
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Returns a list of {@link Collector} instances to be    * included in the search based on the .    *    * @return a list of {@link Collector} instances    * @throws IOException If I/O related errors occur    */
DECL|method|create
name|List
argument_list|<
name|Collector
argument_list|>
name|create
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the results that the collectors created    * by {@link #create()} contain after a search has been executed.    *    * @return The results of the collectors    */
DECL|method|result
name|T
name|result
parameter_list|()
function_decl|;
comment|/**    * @return The key of this command to uniquely identify itself    */
DECL|method|getKey
name|String
name|getKey
parameter_list|()
function_decl|;
comment|/**    * @return The group sort (overall sort)    */
DECL|method|getGroupSort
name|Sort
name|getGroupSort
parameter_list|()
function_decl|;
comment|/**    * @return The sort inside a group    */
DECL|method|getSortWithinGroup
name|Sort
name|getSortWithinGroup
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

