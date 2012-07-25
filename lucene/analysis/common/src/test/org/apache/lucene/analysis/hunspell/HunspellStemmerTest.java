begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|List
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
DECL|class|HunspellStemmerTest
specifier|public
class|class
name|HunspellStemmerTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|stemmer
specifier|private
specifier|static
name|HunspellStemmer
name|stemmer
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|createStemmer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
DECL|method|testStem_simpleSuffix
specifier|public
name|void
name|testStem_simpleSuffix
parameter_list|()
block|{
name|List
argument_list|<
name|HunspellStemmer
operator|.
name|Stem
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"lucene"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lucene"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lucen"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"mahoute"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mahout"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStem_simplePrefix
specifier|public
name|void
name|testStem_simplePrefix
parameter_list|()
block|{
name|List
argument_list|<
name|HunspellStemmer
operator|.
name|Stem
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"solr"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"olr"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStem_recursiveSuffix
specifier|public
name|void
name|testStem_recursiveSuffix
parameter_list|()
block|{
name|List
argument_list|<
name|HunspellStemmer
operator|.
name|Stem
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStem_ignoreCase
specifier|public
name|void
name|testStem_ignoreCase
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|List
argument_list|<
name|HunspellStemmer
operator|.
name|Stem
argument_list|>
name|stems
decl_stmt|;
name|createStemmer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"apache"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"apach"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"APACHE"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"apach"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"Apache"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"apach"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"foos"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"food"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"Foos"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"Food"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStem_caseSensitive
specifier|public
name|void
name|testStem_caseSensitive
parameter_list|()
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|createStemmer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|HunspellStemmer
operator|.
name|Stem
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"apache"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|stems
operator|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"Apache"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Apach"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStemString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createStemmer
specifier|private
specifier|static
name|void
name|createStemmer
parameter_list|(
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|InputStream
name|affixStream
init|=
name|HunspellStemmerTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|HunspellStemmerTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test.dic"
argument_list|)
decl_stmt|;
name|HunspellDictionary
name|dictionary
init|=
operator|new
name|HunspellDictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|stemmer
operator|=
operator|new
name|HunspellStemmer
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
name|affixStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|dictStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

