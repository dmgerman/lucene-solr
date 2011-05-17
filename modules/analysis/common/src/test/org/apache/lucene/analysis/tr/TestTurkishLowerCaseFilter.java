begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.tr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tr
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockTokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Test the Turkish lowercase filter.  */
end_comment

begin_class
DECL|class|TestTurkishLowerCaseFilter
specifier|public
class|class
name|TestTurkishLowerCaseFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Test composed forms    */
DECL|method|testTurkishLowerCaseFilter
specifier|public
name|void
name|testTurkishLowerCaseFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"\u0130STANBUL \u0130ZM\u0130R ISPARTA"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TurkishLowerCaseFilter
name|filter
init|=
operator|new
name|TurkishLowerCaseFilter
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"istanbul"
block|,
literal|"izmir"
block|,
literal|"\u0131sparta"
block|,}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test decomposed forms    */
DECL|method|testDecomposed
specifier|public
name|void
name|testDecomposed
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"\u0049\u0307STANBUL \u0049\u0307ZM\u0049\u0307R ISPARTA"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TurkishLowerCaseFilter
name|filter
init|=
operator|new
name|TurkishLowerCaseFilter
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"istanbul"
block|,
literal|"izmir"
block|,
literal|"\u0131sparta"
block|,}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test decomposed forms with additional accents    * In this example, U+0049 + U+0316 + U+0307 is canonically equivalent    * to U+0130 + U+0316, and is lowercased the same way.    */
DECL|method|testDecomposed2
specifier|public
name|void
name|testDecomposed2
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"\u0049\u0316\u0307STANBUL \u0049\u0307ZM\u0049\u0307R I\u0316SPARTA"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TurkishLowerCaseFilter
name|filter
init|=
operator|new
name|TurkishLowerCaseFilter
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"i\u0316stanbul"
block|,
literal|"izmir"
block|,
literal|"\u0131\u0316sparta"
block|,}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

