begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.snowball
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|snowball
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
import|;
end_import

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
name|InputStream
import|;
end_import

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
name|List
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

begin_class
DECL|class|TestSnowballPorterFilterFactory
specifier|public
class|class
name|TestSnowballPorterFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
name|EnglishStemmer
name|stemmer
init|=
operator|new
name|EnglishStemmer
argument_list|()
decl_stmt|;
name|String
index|[]
name|test
init|=
block|{
literal|"The"
block|,
literal|"fledgling"
block|,
literal|"banks"
block|,
literal|"were"
block|,
literal|"counting"
block|,
literal|"on"
block|,
literal|"a"
block|,
literal|"big"
block|,
literal|"boom"
block|,
literal|"in"
block|,
literal|"banking"
block|}
decl_stmt|;
name|String
index|[]
name|gold
init|=
operator|new
name|String
index|[
name|test
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|test
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stemmer
operator|.
name|setCurrent
argument_list|(
name|test
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|stemmer
operator|.
name|stem
argument_list|()
expr_stmt|;
name|gold
index|[
name|i
index|]
operator|=
name|stemmer
operator|.
name|getCurrent
argument_list|()
expr_stmt|;
block|}
name|SnowballPorterFilterFactory
name|factory
init|=
operator|new
name|SnowballPorterFilterFactory
argument_list|()
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
literal|"language"
argument_list|,
literal|"English"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|join
argument_list|(
name|test
argument_list|,
literal|' '
argument_list|)
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|gold
argument_list|)
expr_stmt|;
block|}
DECL|method|join
name|String
name|join
parameter_list|(
name|String
index|[]
name|stuff
parameter_list|,
name|char
name|sep
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stuff
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|stuff
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Test the protected words mechanism of SnowballPorterFilterFactory    */
DECL|method|testProtected
specifier|public
name|void
name|testProtected
parameter_list|()
throws|throws
name|Exception
block|{
name|SnowballPorterFilterFactory
name|factory
init|=
operator|new
name|SnowballPorterFilterFactory
argument_list|()
decl_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|StringMockResourceLoader
argument_list|(
literal|"ridding"
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
literal|"protected"
argument_list|,
literal|"protwords.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"language"
argument_list|,
literal|"English"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ridding of some stemming"
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenizer
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
literal|"ridding"
block|,
literal|"of"
block|,
literal|"some"
block|,
literal|"stem"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

