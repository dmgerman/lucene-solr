begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|TestCondition
specifier|public
class|class
name|TestCondition
extends|extends
name|StemmerTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|init
argument_list|(
literal|"condition.aff"
argument_list|,
literal|"condition.dic"
argument_list|)
expr_stmt|;
block|}
DECL|method|testStemming
specifier|public
name|void
name|testStemming
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"hello"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"try"
argument_list|,
literal|"try"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"tried"
argument_list|,
literal|"try"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"work"
argument_list|,
literal|"work"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"worked"
argument_list|,
literal|"work"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"rework"
argument_list|,
literal|"work"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"reworked"
argument_list|,
literal|"work"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"retried"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"workied"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"tryed"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"tryied"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"helloed"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

