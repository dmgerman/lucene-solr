begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Test with various combinations of parameters, child entities, caches, transformers.  */
end_comment

begin_class
DECL|class|TestSqlEntityProcessor
specifier|public
class|class
name|TestSqlEntityProcessor
extends|extends
name|AbstractSqlEntityProcessorTestCase
block|{
annotation|@
name|Test
DECL|method|testSingleEntity
specifier|public
name|void
name|testSingleEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|singleEntity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithSimpleTransformer
specifier|public
name|void
name|testWithSimpleTransformer
parameter_list|()
throws|throws
name|Exception
block|{
name|simpleTransform
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithComplexTransformer
specifier|public
name|void
name|testWithComplexTransformer
parameter_list|()
throws|throws
name|Exception
block|{
name|complexTransform
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChildEntities
specifier|public
name|void
name|testChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|withChildEntities
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCachedChildEntities
specifier|public
name|void
name|testCachedChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|withChildEntities
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSportZipperChildEntities
specifier|public
name|void
name|testSportZipperChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|sportsZipper
operator|=
literal|true
expr_stmt|;
name|withChildEntities
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountryZipperChildEntities
specifier|public
name|void
name|testCountryZipperChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|countryZipper
operator|=
literal|true
expr_stmt|;
name|withChildEntities
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBothZipperChildEntities
specifier|public
name|void
name|testBothZipperChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|countryZipper
operator|=
literal|true
expr_stmt|;
name|sportsZipper
operator|=
literal|true
expr_stmt|;
name|withChildEntities
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RuntimeException
operator|.
name|class
comment|/* DIH exceptions are not propagated, here we capturing assertQ exceptions */
argument_list|)
DECL|method|testSportZipperChildEntitiesWrongOrder
specifier|public
name|void
name|testSportZipperChildEntitiesWrongOrder
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|wrongPeopleOrder
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|wrongSportsOrder
operator|=
literal|true
expr_stmt|;
block|}
name|testSportZipperChildEntities
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RuntimeException
operator|.
name|class
argument_list|)
DECL|method|testCountryZipperChildEntitiesWrongOrder
specifier|public
name|void
name|testCountryZipperChildEntitiesWrongOrder
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|wrongPeopleOrder
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|wrongCountryOrder
operator|=
literal|true
expr_stmt|;
block|}
name|testCountryZipperChildEntities
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RuntimeException
operator|.
name|class
argument_list|)
DECL|method|testBothZipperChildEntitiesWrongOrder
specifier|public
name|void
name|testBothZipperChildEntitiesWrongOrder
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|wrongPeopleOrder
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|wrongSportsOrder
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|wrongCountryOrder
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|testBothZipperChildEntities
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"broken see SOLR-3857"
argument_list|)
DECL|method|testSimpleCacheChildEntities
specifier|public
name|void
name|testSimpleCacheChildEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|simpleCacheChildEntities
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deltaQueriesCountryTable
specifier|protected
name|String
name|deltaQueriesCountryTable
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
DECL|method|deltaQueriesPersonTable
specifier|protected
name|String
name|deltaQueriesPersonTable
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

