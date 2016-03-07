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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestDynamicFieldCollectionResource
specifier|public
class|class
name|TestDynamicFieldCollectionResource
extends|extends
name|SolrRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testGetAllDynamicFields
specifier|public
name|void
name|testGetAllDynamicFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/dynamicfields?indent=on&wt=xml"
argument_list|,
literal|"(/response/arr[@name='dynamicFields']/lst/str[@name='name'])[1] = '*_coordinate'"
argument_list|,
literal|"(/response/arr[@name='dynamicFields']/lst/str[@name='name'])[2] = 'ignored_*'"
argument_list|,
literal|"(/response/arr[@name='dynamicFields']/lst/str[@name='name'])[3] = '*_mfacet'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetAllDynamicFields
specifier|public
name|void
name|testJsonGetAllDynamicFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/dynamicfields?indent=on"
argument_list|,
literal|"/dynamicFields/[0]/name=='*_coordinate'"
argument_list|,
literal|"/dynamicFields/[1]/name=='ignored_*'"
argument_list|,
literal|"/dynamicFields/[2]/name=='*_mfacet'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

