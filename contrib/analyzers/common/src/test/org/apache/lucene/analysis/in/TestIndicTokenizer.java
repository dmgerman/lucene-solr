begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.in
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|in
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
name|IOException
import|;
end_import

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
name|TokenStream
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Test IndicTokenizer  */
end_comment

begin_class
DECL|class|TestIndicTokenizer
specifier|public
class|class
name|TestIndicTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Test tokenizing Indic vowels, signs, and punctuation */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
operator|new
name|IndicTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"à¤®à¥à¤à¥ à¤¹à¤¿à¤à¤¦à¥ à¤à¤¾ à¤à¤° à¤à¤­à¥à¤¯à¤¾à¤¸ à¤à¤°à¤¨à¤¾ à¤¹à¥à¤à¤¾ à¥¤"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¤®à¥à¤à¥"
block|,
literal|"à¤¹à¤¿à¤à¤¦à¥"
block|,
literal|"à¤à¤¾"
block|,
literal|"à¤à¤°"
block|,
literal|"à¤à¤­à¥à¤¯à¤¾à¤¸"
block|,
literal|"à¤à¤°à¤¨à¤¾"
block|,
literal|"à¤¹à¥à¤à¤¾"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that words with format chars such as ZWJ are kept */
DECL|method|testFormat
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
operator|new
name|IndicTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"à¤¶à¤¾à¤°à¥âà¤®à¤¾ à¤¶à¤¾à¤°à¥âà¤®à¤¾"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¤¶à¤¾à¤°à¥âà¤®à¤¾"
block|,
literal|"à¤¶à¤¾à¤°à¥âà¤®à¤¾"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

