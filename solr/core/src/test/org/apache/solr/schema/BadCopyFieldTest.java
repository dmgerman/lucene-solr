begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|core
operator|.
name|AbstractBadConfigTestBase
import|;
end_import

begin_comment
comment|/**  * SOLR-4650: copyField source with no asterisk should trigger an error if it doesn't match an explicit or dynamic field   */
end_comment

begin_class
DECL|class|BadCopyFieldTest
specifier|public
class|class
name|BadCopyFieldTest
extends|extends
name|AbstractBadConfigTestBase
block|{
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
specifier|final
name|String
name|schema
parameter_list|,
specifier|final
name|String
name|errString
parameter_list|)
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
name|schema
argument_list|,
name|errString
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonGlobCopyFieldSourceMatchingNothingShouldFail
specifier|public
name|void
name|testNonGlobCopyFieldSourceMatchingNothingShouldFail
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-non-glob-copyfield-source-matching-nothing-should-fail-test.xml"
argument_list|,
literal|"copyField source :'matches_nothing' is not a glob and doesn't match any explicit field or dynamicField."
argument_list|)
expr_stmt|;
block|}
DECL|field|INVALID_GLOB_MESSAGE
specifier|private
specifier|static
specifier|final
name|String
name|INVALID_GLOB_MESSAGE
init|=
literal|" is an invalid glob: either it contains more than one asterisk,"
operator|+
literal|" or the asterisk occurs neither at the start nor at the end."
decl_stmt|;
DECL|method|testMultipleAsteriskCopyFieldSourceShouldFail
specifier|public
name|void
name|testMultipleAsteriskCopyFieldSourceShouldFail
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-multiple-asterisk-copyfield-source-should-fail-test.xml"
argument_list|,
literal|"copyField source :'*too_many_asterisks*'"
operator|+
name|INVALID_GLOB_MESSAGE
argument_list|)
expr_stmt|;
block|}
DECL|method|testMisplacedAsteriskCopyFieldSourceShouldFail
specifier|public
name|void
name|testMisplacedAsteriskCopyFieldSourceShouldFail
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-misplaced-asterisk-copyfield-source-should-fail-test.xml"
argument_list|,
literal|"copyField source :'misplaced_*_asterisk'"
operator|+
name|INVALID_GLOB_MESSAGE
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleAsteriskCopyFieldDestShouldFail
specifier|public
name|void
name|testMultipleAsteriskCopyFieldDestShouldFail
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-multiple-asterisk-copyfield-dest-should-fail-test.xml"
argument_list|,
literal|"copyField dest :'*too_many_asterisks*'"
operator|+
name|INVALID_GLOB_MESSAGE
argument_list|)
expr_stmt|;
block|}
DECL|method|testMisplacedAsteriskCopyFieldDestShouldFail
specifier|public
name|void
name|testMisplacedAsteriskCopyFieldDestShouldFail
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-misplaced-asterisk-copyfield-dest-should-fail-test.xml"
argument_list|,
literal|"copyField dest :'misplaced_*_asterisk'"
operator|+
name|INVALID_GLOB_MESSAGE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

