begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|core
operator|.
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/**  * Simple tests to ensure the Shingle filter factory works.  */
end_comment

begin_class
DECL|class|TestShingleFilterFactory
specifier|public
class|class
name|TestShingleFilterFactory
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test the defaults    */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this is"
block|,
literal|"is"
block|,
literal|"is a"
block|,
literal|"a"
block|,
literal|"a test"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with unigrams disabled    */
DECL|method|testNoUnigrams
specifier|public
name|void
name|testNoUnigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is"
block|,
literal|"is a"
block|,
literal|"a test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with a higher max shingle size    */
DECL|method|testMaxShingleSize
specifier|public
name|void
name|testMaxShingleSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this is"
block|,
literal|"this is a"
block|,
literal|"is"
block|,
literal|"is a"
block|,
literal|"is a test"
block|,
literal|"a"
block|,
literal|"a test"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with higher min (and max) shingle size    */
DECL|method|testMinShingleSize
specifier|public
name|void
name|testMinShingleSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this is a"
block|,
literal|"this is a test"
block|,
literal|"is"
block|,
literal|"is a test"
block|,
literal|"a"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with higher min (and max) shingle size and with unigrams disabled    */
DECL|method|testMinShingleSizeNoUnigrams
specifier|public
name|void
name|testMinShingleSizeNoUnigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is a"
block|,
literal|"this is a test"
block|,
literal|"is a test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with higher same min and max shingle size    */
DECL|method|testEqualMinAndMaxShingleSize
specifier|public
name|void
name|testEqualMinAndMaxShingleSize
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this is a"
block|,
literal|"is"
block|,
literal|"is a test"
block|,
literal|"a"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with higher same min and max shingle size and with unigrams disabled    */
DECL|method|testEqualMinAndMaxShingleSizeNoUnigrams
specifier|public
name|void
name|testEqualMinAndMaxShingleSizeNoUnigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this is a"
block|,
literal|"is a test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with a non-default token separator    */
DECL|method|testTokenSeparator
specifier|public
name|void
name|testTokenSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"tokenSeparator"
argument_list|,
literal|"=BLAH="
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this=BLAH=is"
block|,
literal|"is"
block|,
literal|"is=BLAH=a"
block|,
literal|"a"
block|,
literal|"a=BLAH=test"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with a non-default token separator and with unigrams disabled    */
DECL|method|testTokenSeparatorNoUnigrams
specifier|public
name|void
name|testTokenSeparatorNoUnigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"tokenSeparator"
argument_list|,
literal|"=BLAH="
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this=BLAH=is"
block|,
literal|"is=BLAH=a"
block|,
literal|"a=BLAH=test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with an empty token separator    */
DECL|method|testEmptyTokenSeparator
specifier|public
name|void
name|testEmptyTokenSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"tokenSeparator"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"thisis"
block|,
literal|"is"
block|,
literal|"isa"
block|,
literal|"a"
block|,
literal|"atest"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with higher min (and max) shingle size     * and with a non-default token separator    */
DECL|method|testMinShingleSizeAndTokenSeparator
specifier|public
name|void
name|testMinShingleSizeAndTokenSeparator
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"tokenSeparator"
argument_list|,
literal|"=BLAH="
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"this=BLAH=is=BLAH=a"
block|,
literal|"this=BLAH=is=BLAH=a=BLAH=test"
block|,
literal|"is"
block|,
literal|"is=BLAH=a=BLAH=test"
block|,
literal|"a"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with higher min (and max) shingle size     * and with a non-default token separator    * and with unigrams disabled    */
DECL|method|testMinShingleSizeAndTokenSeparatorNoUnigrams
specifier|public
name|void
name|testMinShingleSizeAndTokenSeparatorNoUnigrams
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"this is a test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"minShingleSize"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"maxShingleSize"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"tokenSeparator"
argument_list|,
literal|"=BLAH="
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this=BLAH=is=BLAH=a"
block|,
literal|"this=BLAH=is=BLAH=a=BLAH=test"
block|,
literal|"is=BLAH=a=BLAH=test"
block|, }
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with unigrams disabled except when there are no shingles, with    * a single input token. Using default min/max shingle sizes: 2/2.  No    * shingles will be created, since there are fewer input tokens than    * min shingle size.  However, because outputUnigramsIfNoShingles is    * set to true, even though outputUnigrams is set to false, one    * unigram should be output.    */
DECL|method|testOutputUnigramsIfNoShingles
specifier|public
name|void
name|testOutputUnigramsIfNoShingles
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigrams"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"outputUnigramsIfNoShingles"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|ShingleFilterFactory
name|factory
init|=
operator|new
name|ShingleFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

