begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|pattern
operator|.
name|PatternTokenizerFactory
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
name|TokenFilterFactory
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
name|ClasspathResourceLoader
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
name|StringMockResourceLoader
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
name|cjk
operator|.
name|CJKAnalyzer
import|;
end_import

begin_class
DECL|class|TestSynonymFilterFactory
specifier|public
class|class
name|TestSynonymFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
comment|/** test that we can parse and use the solr syn file */
DECL|method|testSynonyms
specifier|public
name|void
name|testSynonyms
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
literal|"GB"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|instanceof
name|SynonymFilter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GB"
block|,
literal|"gib"
block|,
literal|"gigabyte"
block|,
literal|"gigabytes"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** if the synonyms are completely empty, test that we still analyze correctly */
DECL|method|testEmptySynonyms
specifier|public
name|void
name|testEmptySynonyms
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
literal|"GB"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stream
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|,
comment|// empty file!
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|)
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GB"
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
try|try
block|{
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|,
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
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
comment|/** Test that analyzer and tokenizerFactory is both specified */
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|analyzer
init|=
name|CJKAnalyzer
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|tokenizerFactory
init|=
name|PatternTokenizerFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|TokenFilterFactory
name|factory
init|=
literal|null
decl_stmt|;
name|factory
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms2.txt"
argument_list|,
literal|"analyzer"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
try|try
block|{
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|,
literal|"analyzer"
argument_list|,
name|analyzer
argument_list|,
literal|"tokenizerFactory"
argument_list|,
name|tokenizerFactory
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Analyzer and TokenizerFactory can't be specified both"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|TOK_SYN_ARG_VAL
specifier|static
specifier|final
name|String
name|TOK_SYN_ARG_VAL
init|=
literal|"argument"
decl_stmt|;
DECL|field|TOK_FOO_ARG_VAL
specifier|static
specifier|final
name|String
name|TOK_FOO_ARG_VAL
init|=
literal|"foofoofoo"
decl_stmt|;
comment|/** Test that we can parse TokenierFactory's arguments */
DECL|method|testTokenizerFactoryArguments
specifier|public
name|void
name|testTokenizerFactoryArguments
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|clazz
init|=
name|PatternTokenizerFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|TokenFilterFactory
name|factory
init|=
literal|null
decl_stmt|;
comment|// simple arg form
name|factory
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|,
literal|"tokenizerFactory"
argument_list|,
name|clazz
argument_list|,
literal|"pattern"
argument_list|,
literal|"(.*)"
argument_list|,
literal|"group"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
comment|// prefix
name|factory
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|,
literal|"tokenizerFactory"
argument_list|,
name|clazz
argument_list|,
literal|"tokenizerFactory.pattern"
argument_list|,
literal|"(.*)"
argument_list|,
literal|"tokenizerFactory.group"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
comment|// sanity check that sub-PatternTokenizerFactory fails w/o pattern
try|try
block|{
name|factory
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|,
literal|"tokenizerFactory"
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"tokenizerFactory should have complained about missing pattern arg"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
comment|// :NOOP:
block|}
comment|// sanity check that sub-PatternTokenizerFactory fails on unexpected
try|try
block|{
name|factory
operator|=
name|tokenFilterFactory
argument_list|(
literal|"Synonym"
argument_list|,
literal|"synonyms"
argument_list|,
literal|"synonyms.txt"
argument_list|,
literal|"tokenizerFactory"
argument_list|,
name|clazz
argument_list|,
literal|"tokenizerFactory.pattern"
argument_list|,
literal|"(.*)"
argument_list|,
literal|"tokenizerFactory.bogusbogusbogus"
argument_list|,
literal|"bogus"
argument_list|,
literal|"tokenizerFactory.group"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"tokenizerFactory should have complained about missing pattern arg"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
comment|// :NOOP:
block|}
block|}
block|}
end_class

end_unit

