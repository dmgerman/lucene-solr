begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
DECL|class|TestCopyFieldCollectionResource
specifier|public
class|class
name|TestCopyFieldCollectionResource
extends|extends
name|SchemaRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testGetAllCopyFields
specifier|public
name|void
name|testGetAllCopyFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/copyfields?indent=on&wt=xml"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='title']"
operator|+
literal|"                                      and str[@name='dest'][.='title_stemmed']"
operator|+
literal|"                                      and int[@name='maxChars'][.='200']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='title']"
operator|+
literal|"                                      and str[@name='dest'][.='dest_sub_no_ast_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='*_dest_sub_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='dest_sub_no_ast_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='*_dest_sub_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_src_sub_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='dest_sub_no_ast_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='*_dest_sub_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='src_sub_no_ast_i']"
operator|+
literal|"                                      and str[@name='sourceDynamicBase'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='dest_sub_no_ast_s']"
operator|+
literal|"                                      and str[@name='destDynamicBase'][.='*_s']]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetAllCopyFields
specifier|public
name|void
name|testJsonGetAllCopyFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/copyfields?indent=on&wt=json"
argument_list|,
literal|"/copyfields/[6]=={'source':'title','dest':'dest_sub_no_ast_s','destDynamicBase':'*_s'}"
argument_list|,
literal|"/copyfields/[7]=={'source':'*_i','dest':'title'}"
argument_list|,
literal|"/copyfields/[8]=={'source':'*_i','dest':'*_s'}"
argument_list|,
literal|"/copyfields/[9]=={'source':'*_i','dest':'*_dest_sub_s','destDynamicBase':'*_s'}"
argument_list|,
literal|"/copyfields/[10]=={'source':'*_i','dest':'dest_sub_no_ast_s','destDynamicBase':'*_s'}"
argument_list|,
literal|"/copyfields/[11]=={'source':'*_src_sub_i','sourceDynamicBase':'*_i','dest':'title'}"
argument_list|,
literal|"/copyfields/[12]=={'source':'*_src_sub_i','sourceDynamicBase':'*_i','dest':'*_s'}"
argument_list|,
literal|"/copyfields/[13]=={'source':'*_src_sub_i','sourceDynamicBase':'*_i','dest':'*_dest_sub_s','destDynamicBase':'*_s'}"
argument_list|,
literal|"/copyfields/[14]=={'source':'*_src_sub_i','sourceDynamicBase':'*_i','dest':'dest_sub_no_ast_s','destDynamicBase':'*_s'}"
argument_list|,
literal|"/copyfields/[15]=={'source':'src_sub_no_ast_i','sourceDynamicBase':'*_i','dest':'title'}"
argument_list|,
literal|"/copyfields/[16]=={'source':'src_sub_no_ast_i','sourceDynamicBase':'*_i','dest':'*_s'}"
argument_list|,
literal|"/copyfields/[17]=={'source':'src_sub_no_ast_i','sourceDynamicBase':'*_i','dest':'*_dest_sub_s','destDynamicBase':'*_s'}"
argument_list|,
literal|"/copyfields/[18]=={'source':'src_sub_no_ast_i','sourceDynamicBase':'*_i','dest':'dest_sub_no_ast_s','destDynamicBase':'*_s'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestrictSource
specifier|public
name|void
name|testRestrictSource
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=title,*_i,*_src_sub_i,src_sub_no_ast_i"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst) = 16"
argument_list|,
comment|// 4 + 4 + 4 + 4
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='source'][.='title']) = 4"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='source'][.='*_i']) = 4"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='source'][.='*_src_sub_i']) = 4"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='source'][.='src_sub_no_ast_i']) = 4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestrictDest
specifier|public
name|void
name|testRestrictDest
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&dest.fl=title,*_s,*_dest_sub_s,dest_sub_no_ast_s"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst) = 13"
argument_list|,
comment|// 3 + 3 + 3 + 4
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='dest'][.='title']) = 3"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='dest'][.='*_s']) = 3"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='dest'][.='*_dest_sub_s']) = 3"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst/str[@name='dest'][.='dest_sub_no_ast_s']) = 4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestrictSourceAndDest
specifier|public
name|void
name|testRestrictSourceAndDest
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=title,*_i&dest.fl=title,dest_sub_no_ast_s"
argument_list|,
literal|"count(/response/arr[@name='copyfields']/lst) = 3"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='title']"
operator|+
literal|"                                      and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='title']]"
argument_list|,
literal|"/response/arr[@name='copyfields']/lst[    str[@name='source'][.='*_i']"
operator|+
literal|"                                      and str[@name='dest'][.='dest_sub_no_ast_s']]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

