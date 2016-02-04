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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Seed
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
DECL|class|TestFieldCollectionResource
specifier|public
class|class
name|TestFieldCollectionResource
extends|extends
name|SolrRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testGetAllFields
specifier|public
name|void
name|testGetAllFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[1] = 'HTMLstandardtok'"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[2] = 'HTMLwhitetok'"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[3] = '_version_'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetTwoFields
specifier|public
name|void
name|testGetTwoFields
parameter_list|()
throws|throws
name|IOException
block|{
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml&fl=id,_version_"
argument_list|,
literal|"count(/response/arr[@name='fields']/lst/str[@name='name']) = 2"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[1] = 'id'"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[2] = '_version_'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetThreeFieldsDontIncludeDynamic
specifier|public
name|void
name|testGetThreeFieldsDontIncludeDynamic
parameter_list|()
throws|throws
name|IOException
block|{
comment|//
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml&fl=id,_version_,price_i"
argument_list|,
literal|"count(/response/arr[@name='fields']/lst/str[@name='name']) = 2"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[1] = 'id'"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[2] = '_version_'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetThreeFieldsIncludeDynamic
specifier|public
name|void
name|testGetThreeFieldsIncludeDynamic
parameter_list|()
throws|throws
name|IOException
block|{
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml&fl=id,_version_,price_i&includeDynamic=on"
argument_list|,
literal|"count(/response/arr[@name='fields']/lst/str[@name='name']) = 3"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[1] = 'id'"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[2] = '_version_'"
argument_list|,
literal|"(/response/arr[@name='fields']/lst/str[@name='name'])[3] = 'price_i'"
argument_list|,
literal|"/response/arr[@name='fields']/lst[    str[@name='name']='price_i'    "
operator|+
literal|"                                  and str[@name='dynamicBase']='*_i']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNotFoundFields
specifier|public
name|void
name|testNotFoundFields
parameter_list|()
throws|throws
name|IOException
block|{
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml&fl=not_in_there,this_one_either"
argument_list|,
literal|"count(/response/arr[@name='fields']) = 1"
argument_list|,
literal|"count(/response/arr[@name='fields']/lst/str[@name='name']) = 0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetAllFields
specifier|public
name|void
name|testJsonGetAllFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/fields?indent=on"
argument_list|,
literal|"/fields/[0]/name=='HTMLstandardtok'"
argument_list|,
literal|"/fields/[1]/name=='HTMLwhitetok'"
argument_list|,
literal|"/fields/[2]/name=='_version_'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetTwoFields
specifier|public
name|void
name|testJsonGetTwoFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/fields?indent=on&fl=id,_version_&wt=xml"
argument_list|,
comment|// assertJQ should fix the wt param to be json
literal|"/fields/[0]/name=='id'"
argument_list|,
literal|"/fields/[1]/name=='_version_'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

