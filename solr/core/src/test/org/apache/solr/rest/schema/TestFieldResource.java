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
DECL|class|TestFieldResource
specifier|public
class|class
name|TestFieldResource
extends|extends
name|SolrRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testGetField
specifier|public
name|void
name|testGetField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/test_postv?indent=on&wt=xml&showDefaults=true"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"count(/response/lst[@name='field']/*) = 17"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='name'] = 'test_postv'"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='type'] = 'text'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='indexed'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='stored'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='docValues'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='termVectors'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='termPositions'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='termPayloads'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='termOffsets'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='omitNorms'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='omitTermFreqAndPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='omitPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='storeOffsetsWithPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='multiValued'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='required'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='tokenized'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='useDocValuesAsStored'] = 'true'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNotFoundField
specifier|public
name|void
name|testGetNotFoundField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/not_in_there?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetField
specifier|public
name|void
name|testJsonGetField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/fields/test_postv?indent=on&showDefaults=true"
argument_list|,
literal|"/field/name=='test_postv'"
argument_list|,
literal|"/field/type=='text'"
argument_list|,
literal|"/field/indexed==true"
argument_list|,
literal|"/field/stored==true"
argument_list|,
literal|"/field/docValues==false"
argument_list|,
literal|"/field/termVectors==true"
argument_list|,
literal|"/field/termPositions==true"
argument_list|,
literal|"/field/termOffsets==false"
argument_list|,
literal|"/field/termPayloads==false"
argument_list|,
literal|"/field/omitNorms==false"
argument_list|,
literal|"/field/omitTermFreqAndPositions==false"
argument_list|,
literal|"/field/omitPositions==false"
argument_list|,
literal|"/field/storeOffsetsWithPositions==false"
argument_list|,
literal|"/field/multiValued==false"
argument_list|,
literal|"/field/required==false"
argument_list|,
literal|"/field/tokenized==true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFieldIncludeDynamic
specifier|public
name|void
name|testGetFieldIncludeDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/some_crazy_name_i?indent=on&wt=xml&includeDynamic=true"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='name'] = 'some_crazy_name_i'"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='dynamicBase'] = '*_i'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFieldDontShowDefaults
specifier|public
name|void
name|testGetFieldDontShowDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|tests
init|=
block|{
literal|"count(/response/lst[@name='field']) = 1"
block|,
literal|"count(/response/lst[@name='field']/*) = 7"
block|,
literal|"/response/lst[@name='field']/str[@name='name'] = 'id'"
block|,
literal|"/response/lst[@name='field']/str[@name='type'] = 'string'"
block|,
literal|"/response/lst[@name='field']/bool[@name='indexed'] = 'true'"
block|,
literal|"/response/lst[@name='field']/bool[@name='stored'] = 'true'"
block|,
literal|"/response/lst[@name='field']/bool[@name='multiValued'] = 'false'"
block|,
literal|"/response/lst[@name='field']/bool[@name='required'] = 'true'"
block|}
decl_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/id?indent=on&wt=xml"
argument_list|,
name|tests
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/id?indent=on&wt=xml&showDefaults=false"
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonPutFieldToNonMutableIndexSchema
specifier|public
name|void
name|testJsonPutFieldToNonMutableIndexSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield"
argument_list|,
literal|"{\"type\":\"text_general\", \"stored\":\"false\"}"
argument_list|,
literal|"/error/msg=='This IndexSchema is not mutable.'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonPostFieldsToNonMutableIndexSchema
specifier|public
name|void
name|testJsonPostFieldsToNonMutableIndexSchema
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
literal|"[{\"name\":\"foobarbaz\", \"type\":\"text_general\", \"stored\":\"false\"}]"
argument_list|,
literal|"/error/msg=='This IndexSchema is not mutable.'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

