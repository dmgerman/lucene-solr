begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|SolrRestletTestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestSchemaSimilarityResource
specifier|public
class|class
name|TestSchemaSimilarityResource
extends|extends
name|SolrRestletTestBase
block|{
comment|/**    * NOTE: schema used by parent class doesn't define a global sim, so we get the implicit default    * which causes the FQN of the class to be returned    *     */
annotation|@
name|Test
DECL|method|testGetSchemaSimilarity
specifier|public
name|void
name|testGetSchemaSimilarity
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/similarity?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='similarity']) = 1"
argument_list|,
literal|"/response/lst[@name='similarity']/str[@name='class'][.='org.apache.solr.search.similarities.SchemaSimilarityFactory']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

