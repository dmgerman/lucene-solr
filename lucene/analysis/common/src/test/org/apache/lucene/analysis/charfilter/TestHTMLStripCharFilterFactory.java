begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.charfilter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|util
operator|.
name|BaseTokenStreamFactoryTestCase
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure this factory is working  */
end_comment

begin_class
DECL|class|TestHTMLStripCharFilterFactory
specifier|public
class|class
name|TestHTMLStripCharFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testNothingChanged
specifier|public
name|void
name|testNothingChanged
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             11111111112
comment|//                   012345678901234567890
specifier|final
name|String
name|text
init|=
literal|"this is only a test."
decl_stmt|;
name|Reader
name|cs
init|=
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|,
literal|"escapedTags"
argument_list|,
literal|"a, Title"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"this"
block|,
literal|"is"
block|,
literal|"only"
block|,
literal|"a"
block|,
literal|"test."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|8
block|,
literal|13
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|,
literal|12
block|,
literal|14
block|,
literal|20
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoEscapedTags
specifier|public
name|void
name|testNoEscapedTags
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             11111111112222222222333333333344
comment|//                   012345678901234567890123456789012345678901
specifier|final
name|String
name|text
init|=
literal|"<u>this</u> is<b>only</b> a<I>test</I>."
decl_stmt|;
name|Reader
name|cs
init|=
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"this"
block|,
literal|"is"
block|,
literal|"only"
block|,
literal|"a"
block|,
literal|"test."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|12
block|,
literal|18
block|,
literal|27
block|,
literal|32
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|14
block|,
literal|26
block|,
literal|28
block|,
literal|41
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEscapedTags
specifier|public
name|void
name|testEscapedTags
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             11111111112222222222333333333344
comment|//                   012345678901234567890123456789012345678901
specifier|final
name|String
name|text
init|=
literal|"<u>this</u> is<b>only</b> a<I>test</I>."
decl_stmt|;
name|Reader
name|cs
init|=
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|,
literal|"escapedTags"
argument_list|,
literal|"U i"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"<u>this</u>"
block|,
literal|"is"
block|,
literal|"only"
block|,
literal|"a"
block|,
literal|"<I>test</I>."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|12
block|,
literal|18
block|,
literal|27
block|,
literal|29
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|14
block|,
literal|26
block|,
literal|28
block|,
literal|41
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSeparatorOnlyEscapedTags
specifier|public
name|void
name|testSeparatorOnlyEscapedTags
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             11111111112222222222333333333344
comment|//                   012345678901234567890123456789012345678901
specifier|final
name|String
name|text
init|=
literal|"<u>this</u> is<b>only</b> a<I>test</I>."
decl_stmt|;
name|Reader
name|cs
init|=
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|,
literal|"escapedTags"
argument_list|,
literal|",, , "
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"this"
block|,
literal|"is"
block|,
literal|"only"
block|,
literal|"a"
block|,
literal|"test."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|12
block|,
literal|18
block|,
literal|27
block|,
literal|32
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|14
block|,
literal|26
block|,
literal|28
block|,
literal|41
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyEscapedTags
specifier|public
name|void
name|testEmptyEscapedTags
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             11111111112222222222333333333344
comment|//                   012345678901234567890123456789012345678901
specifier|final
name|String
name|text
init|=
literal|"<u>this</u> is<b>only</b> a<I>test</I>."
decl_stmt|;
name|Reader
name|cs
init|=
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|,
literal|"escapedTags"
argument_list|,
literal|""
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"this"
block|,
literal|"is"
block|,
literal|"only"
block|,
literal|"a"
block|,
literal|"test."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|12
block|,
literal|18
block|,
literal|27
block|,
literal|32
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|14
block|,
literal|26
block|,
literal|28
block|,
literal|41
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleEscapedTag
specifier|public
name|void
name|testSingleEscapedTag
parameter_list|()
throws|throws
name|Exception
block|{
comment|//                             11111111112222222222333333333344
comment|//                   012345678901234567890123456789012345678901
specifier|final
name|String
name|text
init|=
literal|"<u>this</u> is<b>only</b> a<I>test</I>."
decl_stmt|;
name|Reader
name|cs
init|=
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|,
literal|"escapedTags"
argument_list|,
literal|", B\r\n\t"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|whitespaceMockTokenizer
argument_list|(
name|cs
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
literal|"this"
block|,
literal|"is"
block|,
literal|"<b>only</b>"
block|,
literal|"a"
block|,
literal|"test."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|12
block|,
literal|15
block|,
literal|27
block|,
literal|32
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|14
block|,
literal|26
block|,
literal|28
block|,
literal|41
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|charFilterFactory
argument_list|(
literal|"HTMLStrip"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

