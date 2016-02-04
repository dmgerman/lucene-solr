begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.commongrams
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|commongrams
package|;
end_package

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
name|Tokenizer
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
name|TestStopFilter
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
name|CharArraySet
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
name|ResourceLoader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_comment
comment|/**  * Tests pretty much copied from StopFilterFactoryTest We use the test files  * used by the StopFilterFactoryTest TODO: consider creating separate test files  * so this won't break if stop filter test files change  **/
end_comment

begin_class
DECL|class|TestCommonGramsFilterFactory
specifier|public
class|class
name|TestCommonGramsFilterFactory
extends|extends
name|BaseTokenStreamFactoryTestCase
block|{
DECL|method|testInform
specifier|public
name|void
name|testInform
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceLoader
name|loader
init|=
operator|new
name|ClasspathResourceLoader
argument_list|(
name|TestStopFilter
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"loader is null and it shouldn't be"
argument_list|,
name|loader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|CommonGramsFilterFactory
name|factory
init|=
operator|(
name|CommonGramsFilterFactory
operator|)
name|tokenFilterFactory
argument_list|(
literal|"CommonGrams"
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|,
name|loader
argument_list|,
literal|"words"
argument_list|,
literal|"stop-1.txt"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
name|factory
operator|.
name|getCommonWords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|(
name|CommonGramsFilterFactory
operator|)
name|tokenFilterFactory
argument_list|(
literal|"CommonGrams"
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|,
name|loader
argument_list|,
literal|"words"
argument_list|,
literal|"stop-1.txt, stop-2.txt"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|words
operator|=
name|factory
operator|.
name|getCommonWords
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|(
name|CommonGramsFilterFactory
operator|)
name|tokenFilterFactory
argument_list|(
literal|"CommonGrams"
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|,
name|loader
argument_list|,
literal|"words"
argument_list|,
literal|"stop-snowball.txt"
argument_list|,
literal|"format"
argument_list|,
literal|"snowball"
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|words
operator|=
name|factory
operator|.
name|getCommonWords
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|words
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"he"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"him"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"his"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"himself"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"she"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"her"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"hers"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"herself"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * If no words are provided, then a set of english default stopwords is used.    */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|CommonGramsFilterFactory
name|factory
init|=
operator|(
name|CommonGramsFilterFactory
operator|)
name|tokenFilterFactory
argument_list|(
literal|"CommonGrams"
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
name|factory
operator|.
name|getCommonWords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"the"
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"testing the factory"
argument_list|)
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
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
literal|"testing"
block|,
literal|"testing_the"
block|,
literal|"the"
block|,
literal|"the_factory"
block|,
literal|"factory"
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
literal|"CommonGrams"
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
block|}
end_class

end_unit

