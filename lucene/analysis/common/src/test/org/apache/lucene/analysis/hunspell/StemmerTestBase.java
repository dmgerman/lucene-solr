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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
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
name|CharsRef
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
name|IOUtils
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_comment
comment|/** base class for hunspell stemmer tests */
end_comment

begin_class
DECL|class|StemmerTestBase
specifier|public
specifier|abstract
class|class
name|StemmerTestBase
extends|extends
name|LuceneTestCase
block|{
DECL|field|stemmer
specifier|private
specifier|static
name|Stemmer
name|stemmer
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|stemmer
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|init
specifier|static
name|void
name|init
parameter_list|(
name|String
name|affix
parameter_list|,
name|String
name|dictionary
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|init
argument_list|(
literal|false
argument_list|,
name|affix
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|static
name|void
name|init
parameter_list|(
name|boolean
name|ignoreCase
parameter_list|,
name|String
name|affix
parameter_list|,
name|String
modifier|...
name|dictionaries
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
if|if
condition|(
name|dictionaries
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"there must be at least one dictionary"
argument_list|)
throw|;
block|}
name|InputStream
name|affixStream
init|=
name|StemmerTestBase
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|affix
argument_list|)
decl_stmt|;
if|if
condition|(
name|affixStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"file not found: "
operator|+
name|affix
argument_list|)
throw|;
block|}
name|InputStream
name|dictStreams
index|[]
init|=
operator|new
name|InputStream
index|[
name|dictionaries
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
name|dictionaries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dictStreams
index|[
name|i
index|]
operator|=
name|StemmerTestBase
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|dictionaries
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|dictStreams
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"file not found: "
operator|+
name|dictStreams
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
try|try
block|{
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|,
literal|"dictionary"
argument_list|,
name|affixStream
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictStreams
argument_list|)
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|stemmer
operator|=
operator|new
name|Stemmer
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|affixStream
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|dictStreams
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertStemsTo
specifier|static
name|void
name|assertStemsTo
parameter_list|(
name|String
name|s
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|stemmer
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|String
name|actual
index|[]
init|=
operator|new
name|String
index|[
name|stems
operator|.
name|size
argument_list|()
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
name|actual
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|actual
index|[
name|i
index|]
operator|=
name|stems
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
literal|"expected="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
operator|+
literal|",actual="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|actual
argument_list|)
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

