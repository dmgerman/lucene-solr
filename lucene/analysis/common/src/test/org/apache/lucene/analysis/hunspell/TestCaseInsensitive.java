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
DECL|class|TestCaseInsensitive
specifier|public
class|class
name|TestCaseInsensitive
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
literal|true
argument_list|,
literal|"simple.aff"
argument_list|,
literal|"mixedcase.dic"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCaseInsensitivity
specifier|public
name|void
name|testCaseInsensitivity
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"lucene"
argument_list|,
literal|"lucene"
argument_list|,
literal|"lucen"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"LuCeNe"
argument_list|,
literal|"lucene"
argument_list|,
literal|"lucen"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mahoute"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"MaHoUte"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimplePrefix
specifier|public
name|void
name|testSimplePrefix
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"solr"
argument_list|,
literal|"olr"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursiveSuffix
specifier|public
name|void
name|testRecursiveSuffix
parameter_list|()
block|{
comment|// we should not recurse here! as the suffix has no continuation!
name|assertStemsTo
argument_list|(
literal|"abcd"
argument_list|)
expr_stmt|;
block|}
comment|// all forms unmunched from dictionary
DECL|method|testAllStems
specifier|public
name|void
name|testAllStems
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"ab"
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"abc"
argument_list|,
literal|"ab"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"apach"
argument_list|,
literal|"apach"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"apache"
argument_list|,
literal|"apach"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"foo"
argument_list|,
literal|"foo"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"food"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"foos"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"lucen"
argument_list|,
literal|"lucen"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"lucene"
argument_list|,
literal|"lucen"
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mahout"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mahoute"
argument_list|,
literal|"mahout"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"moo"
argument_list|,
literal|"moo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"mood"
argument_list|,
literal|"moo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"olr"
argument_list|,
literal|"olr"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"solr"
argument_list|,
literal|"olr"
argument_list|)
expr_stmt|;
block|}
comment|// some bogus stuff that should not stem (empty lists)!
DECL|method|testBogusStems
specifier|public
name|void
name|testBogusStems
parameter_list|()
block|{
name|assertStemsTo
argument_list|(
literal|"abs"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"abe"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sab"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sapach"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sapache"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"apachee"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sfoo"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"sfoos"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"fooss"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"lucenee"
argument_list|)
expr_stmt|;
name|assertStemsTo
argument_list|(
literal|"solre"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

