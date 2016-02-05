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
DECL|class|TestFieldTypeResource
specifier|public
class|class
name|TestFieldTypeResource
extends|extends
name|SolrRestletTestBase
block|{
annotation|@
name|Test
DECL|method|testGetFieldType
specifier|public
name|void
name|testGetFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fieldtypes/float?indent=on&wt=xml&showDefaults=true"
argument_list|,
literal|"count(/response/lst[@name='fieldType']) = 1"
argument_list|,
literal|"count(/response/lst[@name='fieldType']/*) = 18"
argument_list|,
literal|"/response/lst[@name='fieldType']/str[@name='name'] = 'float'"
argument_list|,
literal|"/response/lst[@name='fieldType']/str[@name='class'] = 'solr.TrieFloatField'"
argument_list|,
literal|"/response/lst[@name='fieldType']/str[@name='precisionStep'] ='0'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='indexed'] = 'true'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='stored'] = 'true'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='docValues'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='termVectors'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='termPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='termOffsets'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='omitNorms'] = 'true'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='omitTermFreqAndPositions'] = 'true'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='omitPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='storeOffsetsWithPositions'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='multiValued'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/bool[@name='tokenized'] = 'false'"
argument_list|,
literal|"/response/lst[@name='fieldType']/arr[@name='fields']/str = 'weight'"
argument_list|,
literal|"/response/lst[@name='fieldType']/arr[@name='dynamicFields']/str = '*_f'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetNotFoundFieldType
specifier|public
name|void
name|testGetNotFoundFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fieldtypes/not_in_there?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='fieldtypes']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJsonGetFieldType
specifier|public
name|void
name|testJsonGetFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
literal|"/schema/fieldtypes/float?indent=on&showDefaults=on"
argument_list|,
comment|// assertJQ will add "&wt=json"
literal|"/fieldType/name=='float'"
argument_list|,
literal|"/fieldType/class=='solr.TrieFloatField'"
argument_list|,
literal|"/fieldType/precisionStep=='0'"
argument_list|,
literal|"/fieldType/indexed==true"
argument_list|,
literal|"/fieldType/stored==true"
argument_list|,
literal|"/fieldType/docValues==false"
argument_list|,
literal|"/fieldType/termVectors==false"
argument_list|,
literal|"/fieldType/termPositions==false"
argument_list|,
literal|"/fieldType/termOffsets==false"
argument_list|,
literal|"/fieldType/omitNorms==true"
argument_list|,
literal|"/fieldType/omitTermFreqAndPositions==true"
argument_list|,
literal|"/fieldType/omitPositions==false"
argument_list|,
literal|"/fieldType/storeOffsetsWithPositions==false"
argument_list|,
literal|"/fieldType/multiValued==false"
argument_list|,
literal|"/fieldType/tokenized==false"
argument_list|,
literal|"/fieldType/fields==['weight']"
argument_list|,
literal|"/fieldType/dynamicFields==['*_f']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFieldTypeDontShowDefaults
specifier|public
name|void
name|testGetFieldTypeDontShowDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fieldtypes/teststop?wt=xml&indent=on"
argument_list|,
literal|"count(/response/lst[@name='fieldType']/*) = 5"
argument_list|,
literal|"/response/lst[@name='fieldType']/str[@name='name'] = 'teststop'"
argument_list|,
literal|"/response/lst[@name='fieldType']/str[@name='class'] = 'solr.TextField'"
argument_list|,
literal|"/response/lst[@name='fieldType']/lst[@name='analyzer']/lst[@name='tokenizer']/str[@name='class'] = 'solr.LowerCaseTokenizerFactory'"
argument_list|,
literal|"/response/lst[@name='fieldType']/lst[@name='analyzer']/arr[@name='filters']/lst/str[@name='class'][.='solr.StandardFilterFactory']"
argument_list|,
literal|"/response/lst[@name='fieldType']/lst[@name='analyzer']/arr[@name='filters']/lst/str[@name='class'][.='solr.StopFilterFactory']"
argument_list|,
literal|"/response/lst[@name='fieldType']/lst[@name='analyzer']/arr[@name='filters']/lst/str[@name='words'][.='stopwords.txt']"
argument_list|,
literal|"/response/lst[@name='fieldType']/arr[@name='fields']/str[.='teststop']"
argument_list|,
literal|"/response/lst[@name='fieldType']/arr[@name='dynamicFields']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

