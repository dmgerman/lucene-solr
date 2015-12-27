begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
DECL|class|TestSchemaResource
specifier|public
class|class
name|TestSchemaResource
extends|extends
name|SolrRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testXMLResponse
specifier|public
name|void
name|testXMLResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/?indent=on&wt=xml"
argument_list|,
comment|// should work with or without trailing slash on '/schema/' path
literal|"count(/response/lst[@name='schema']/str[@name='name']) = 1"
argument_list|,
literal|"/response/lst[@name='schema']/str[@name='name'][.='test-rest']"
argument_list|,
literal|"count(/response/lst[@name='schema']/float[@name='version']) = 1"
argument_list|,
literal|"/response/lst[@name='schema']/float[@name='version'][.='1.6']"
argument_list|,
literal|"count(/response/lst[@name='schema']/lst[@name='solrQueryParser']/str[@name='defaultOperator']) = 1"
argument_list|,
literal|"/response/lst[@name='schema']/lst[@name='solrQueryParser']/str[@name='defaultOperator'][.='OR']"
argument_list|,
literal|"count(/response/lst[@name='schema']/str[@name='uniqueKey']) = 1"
argument_list|,
literal|"/response/lst[@name='schema']/str[@name='uniqueKey'][.='id']"
argument_list|,
literal|"count(/response/lst[@name='schema']/str[@name='defaultSearchField']) = 1"
argument_list|,
literal|"/response/lst[@name='schema']/str[@name='defaultSearchField'][.='text']"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='fieldTypes']/lst/str[@name='name'])[1] = 'HTMLstandardtok'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='fieldTypes']/lst/str[@name='name'])[2] = 'HTMLwhitetok'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='fieldTypes']/lst/str[@name='name'])[3] = 'boolean'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='fields']/lst/str[@name='name'])[1] = 'HTMLstandardtok'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='fields']/lst/str[@name='name'])[2] = 'HTMLwhitetok'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='fields']/lst/str[@name='name'])[3] = '_version_'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='dynamicFields']/lst/str[@name='name'])[1] = '*_coordinate'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='dynamicFields']/lst/str[@name='name'])[2] = 'ignored_*'"
argument_list|,
literal|"(/response/lst[@name='schema']/arr[@name='dynamicFields']/lst/str[@name='name'])[3] = '*_mfacet'"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='title']"
operator|+
literal|"                                                          and str[@name='dest'][.='title_stemmed']"
operator|+
literal|"                                                          and int[@name='maxChars'][.='200']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='title']"
operator|+
literal|"                                                          and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_dest_sub_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_dest_sub_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_dest_sub_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                                          and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='title_*']"
operator|+
literal|"                                                          and str[@name='dest'][.='text']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='title_*']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='title_*']"
operator|+
literal|"                                                          and str[@name='dest'][.='*_dest_sub_s']]"
argument_list|,
literal|"/response/lst[@name='schema']/arr[@name='copyFields']/lst[    str[@name='source'][.='title_*']"
operator|+
literal|"                                                          and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJSONResponse
specifier|public
name|void
name|testJSONResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema?wt=json"
argument_list|,
comment|// Should work with or without a trailing slash
literal|"/schema/name=='test-rest'"
argument_list|,
literal|"/schema/version==1.6"
argument_list|,
literal|"/schema/solrQueryParser/defaultOperator=='OR'"
argument_list|,
literal|"/schema/uniqueKey=='id'"
argument_list|,
literal|"/schema/defaultSearchField=='text'"
argument_list|,
literal|"/schema/fieldTypes/[0]/name=='HTMLstandardtok'"
argument_list|,
literal|"/schema/fieldTypes/[1]/name=='HTMLwhitetok'"
argument_list|,
literal|"/schema/fieldTypes/[2]/name=='boolean'"
argument_list|,
literal|"/schema/fields/[0]/name=='HTMLstandardtok'"
argument_list|,
literal|"/schema/fields/[1]/name=='HTMLwhitetok'"
argument_list|,
literal|"/schema/fields/[2]/name=='_version_'"
argument_list|,
literal|"/schema/dynamicFields/[0]/name=='*_coordinate'"
argument_list|,
literal|"/schema/dynamicFields/[1]/name=='ignored_*'"
argument_list|,
literal|"/schema/dynamicFields/[2]/name=='*_mfacet'"
argument_list|,
literal|"/schema/copyFields/[1]=={'source':'src_sub_no_ast_i','dest':'title'}"
argument_list|,
literal|"/schema/copyFields/[7]=={'source':'title','dest':'dest_sub_no_ast_s'}"
argument_list|,
literal|"/schema/copyFields/[8]=={'source':'*_i','dest':'title'}"
argument_list|,
literal|"/schema/copyFields/[9]=={'source':'*_i','dest':'*_s'}"
argument_list|,
literal|"/schema/copyFields/[10]=={'source':'*_i','dest':'*_dest_sub_s'}"
argument_list|,
literal|"/schema/copyFields/[11]=={'source':'*_i','dest':'dest_sub_no_ast_s'}"
argument_list|,
literal|"/schema/copyFields/[12]=={'source':'*_src_sub_i','dest':'title'}"
argument_list|,
literal|"/schema/copyFields/[13]=={'source':'*_src_sub_i','dest':'*_s'}"
argument_list|,
literal|"/schema/copyFields/[14]=={'source':'*_src_sub_i','dest':'*_dest_sub_s'}"
argument_list|,
literal|"/schema/copyFields/[15]=={'source':'*_src_sub_i','dest':'dest_sub_no_ast_s'}"
argument_list|,
literal|"/schema/copyFields/[16]=={'source':'src_sub_no_ast_i','dest':'*_s'}"
argument_list|,
literal|"/schema/copyFields/[17]=={'source':'src_sub_no_ast_i','dest':'*_dest_sub_s'}"
argument_list|,
literal|"/schema/copyFields/[18]=={'source':'src_sub_no_ast_i','dest':'dest_sub_no_ast_s'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSchemaXmlResponse
specifier|public
name|void
name|testSchemaXmlResponse
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"/schema?wt=schema.xml"
argument_list|,
comment|// should work with or without trailing slash on '/schema/' path
literal|"/schema/@name = 'test-rest'"
argument_list|,
literal|"/schema/@version = '1.6'"
argument_list|,
literal|"/schema/solrQueryParser/@defaultOperator = 'OR'"
argument_list|,
literal|"/schema/uniqueKey = 'id'"
argument_list|,
literal|"/schema/defaultSearchField = 'text'"
argument_list|,
literal|"(/schema/fieldType)[1]/@name = 'HTMLstandardtok'"
argument_list|,
literal|"(/schema/fieldType)[2]/@name = 'HTMLwhitetok'"
argument_list|,
literal|"(/schema/fieldType)[3]/@name = 'boolean'"
argument_list|,
literal|"(/schema/field)[1]/@name = 'HTMLstandardtok'"
argument_list|,
literal|"(/schema/field)[2]/@name = 'HTMLwhitetok'"
argument_list|,
literal|"(/schema/field)[3]/@name = '_version_'"
argument_list|,
literal|"(/schema/dynamicField)[1]/@name = '*_coordinate'"
argument_list|,
literal|"(/schema/dynamicField)[2]/@name = 'ignored_*'"
argument_list|,
literal|"(/schema/dynamicField)[3]/@name = '*_mfacet'"
argument_list|,
literal|"/schema/copyField[@source='title'][@dest='title_stemmed'][@maxChars='200']"
argument_list|,
literal|"/schema/copyField[@source='title'][@dest='dest_sub_no_ast_s']"
argument_list|,
literal|"/schema/copyField[@source='*_i'][@dest='title']"
argument_list|,
literal|"/schema/copyField[@source='*_i'][@dest='*_s']"
argument_list|,
literal|"/schema/copyField[@source='*_i'][@dest='*_dest_sub_s']"
argument_list|,
literal|"/schema/copyField[@source='*_i'][@dest='dest_sub_no_ast_s']"
argument_list|,
literal|"/schema/copyField[@source='*_src_sub_i'][@dest='title']"
argument_list|,
literal|"/schema/copyField[@source='*_src_sub_i'][@dest='*_s']"
argument_list|,
literal|"/schema/copyField[@source='*_src_sub_i'][@dest='*_dest_sub_s']"
argument_list|,
literal|"/schema/copyField[@source='*_src_sub_i'][@dest='dest_sub_no_ast_s']"
argument_list|,
literal|"/schema/copyField[@source='src_sub_no_ast_i'][@dest='title']"
argument_list|,
literal|"/schema/copyField[@source='src_sub_no_ast_i'][@dest='*_s']"
argument_list|,
literal|"/schema/copyField[@source='src_sub_no_ast_i'][@dest='*_dest_sub_s']"
argument_list|,
literal|"/schema/copyField[@source='src_sub_no_ast_i'][@dest='dest_sub_no_ast_s']"
argument_list|,
literal|"/schema/copyField[@source='title_*'][@dest='text']"
argument_list|,
literal|"/schema/copyField[@source='title_*'][@dest='*_s']"
argument_list|,
literal|"/schema/copyField[@source='title_*'][@dest='*_dest_sub_s']"
argument_list|,
literal|"/schema/copyField[@source='title_*'][@dest='dest_sub_no_ast_s']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

