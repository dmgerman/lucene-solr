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
DECL|class|TestDynamicFieldResource
specifier|public
class|class
name|TestDynamicFieldResource
extends|extends
name|SchemaRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testGetDynamicField
specifier|public
name|void
name|testGetDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/dynamicfields/*_i?indent=on&wt=xml&showDefaults=on"
argument_list|,
literal|"count(/response/lst[@name='dynamicfield']) = 1"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/str[@name='name'] = '*_i'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/str[@name='type'] = 'int'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='indexed'] = 'true'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='stored'] = 'true'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='docValues'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='termVectors'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='termPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='termOffsets'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='omitNorms'] = 'true'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='omitTermFreqAndPositions'] = 'true'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='omitPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='storeOffsetsWithPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='multiValued'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='required'] = 'false'"
argument_list|,
literal|"/response/lst[@name='dynamicfield']/bool[@name='tokenized'] = 'false'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNotFoundDynamicField
specifier|public
name|void
name|testGetNotFoundDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/dynamicfields/*not_in_there?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='dynamicfield']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetDynamicField
specifier|public
name|void
name|testJsonGetDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/dynamicfields/*_i?indent=on&showDefaults=on"
argument_list|,
literal|"/dynamicfield/name=='*_i'"
argument_list|,
literal|"/dynamicfield/type=='int'"
argument_list|,
literal|"/dynamicfield/indexed==true"
argument_list|,
literal|"/dynamicfield/stored==true"
argument_list|,
literal|"/dynamicfield/docValues==false"
argument_list|,
literal|"/dynamicfield/termVectors==false"
argument_list|,
literal|"/dynamicfield/termPositions==false"
argument_list|,
literal|"/dynamicfield/termOffsets==false"
argument_list|,
literal|"/dynamicfield/omitNorms==true"
argument_list|,
literal|"/dynamicfield/omitTermFreqAndPositions==true"
argument_list|,
literal|"/dynamicfield/omitPositions==false"
argument_list|,
literal|"/dynamicfield/storeOffsetsWithPositions==false"
argument_list|,
literal|"/dynamicfield/multiValued==false"
argument_list|,
literal|"/dynamicfield/required==false"
argument_list|,
literal|"/dynamicfield/tokenized==false"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

