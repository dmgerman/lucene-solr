begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
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
name|Analyzer
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
name|KeywordMarkerTokenFilter
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
name|LowerCaseTokenizer
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

begin_class
DECL|class|TestGermanAnalyzer
specifier|public
class|class
name|TestGermanAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|GermanAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"Tisch"
argument_list|,
literal|"tisch"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"Tische"
argument_list|,
literal|"tisch"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"Tischen"
argument_list|,
literal|"tisch"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusionTableBWCompat
specifier|public
name|void
name|testExclusionTableBWCompat
parameter_list|()
throws|throws
name|IOException
block|{
name|GermanStemFilter
name|filter
init|=
operator|new
name|GermanStemFilter
argument_list|(
operator|new
name|LowerCaseTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"Fischen Trinken"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"fischen"
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setExclusionSet
argument_list|(
name|set
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fischen"
block|,
literal|"trink"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithKeywordAttribute
specifier|public
name|void
name|testWithKeywordAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"fischen"
argument_list|)
expr_stmt|;
name|GermanStemFilter
name|filter
init|=
operator|new
name|GermanStemFilter
argument_list|(
operator|new
name|KeywordMarkerTokenFilter
argument_list|(
operator|new
name|LowerCaseTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"Fischen Trinken"
argument_list|)
argument_list|)
argument_list|,
name|set
argument_list|)
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
literal|"fischen"
block|,
literal|"trink"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithKeywordAttributeAndExclusionTable
specifier|public
name|void
name|testWithKeywordAttributeAndExclusionTable
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"fischen"
argument_list|)
expr_stmt|;
name|CharArraySet
name|set1
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set1
operator|.
name|add
argument_list|(
literal|"trinken"
argument_list|)
expr_stmt|;
name|set1
operator|.
name|add
argument_list|(
literal|"fischen"
argument_list|)
expr_stmt|;
name|GermanStemFilter
name|filter
init|=
operator|new
name|GermanStemFilter
argument_list|(
operator|new
name|KeywordMarkerTokenFilter
argument_list|(
operator|new
name|LowerCaseTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"Fischen Trinken"
argument_list|)
argument_list|)
argument_list|,
name|set
argument_list|)
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setExclusionSet
argument_list|(
name|set1
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fischen"
block|,
literal|"trinken"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test that changes to the exclusion table are applied immediately    * when using reusable token streams.    */
DECL|method|testExclusionTableReuse
specifier|public
name|void
name|testExclusionTableReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|GermanAnalyzer
name|a
init|=
operator|new
name|GermanAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"tischen"
argument_list|,
literal|"tisch"
argument_list|)
expr_stmt|;
name|a
operator|.
name|setStemExclusionTable
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"tischen"
block|}
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"tischen"
argument_list|,
literal|"tischen"
argument_list|)
expr_stmt|;
block|}
comment|/** test some features of the new snowball filter    * these only pass with LUCENE_CURRENT, not if you use o.a.l.a.de.GermanStemmer    */
DECL|method|testGermanSpecials
specifier|public
name|void
name|testGermanSpecials
parameter_list|()
throws|throws
name|Exception
block|{
name|GermanAnalyzer
name|a
init|=
operator|new
name|GermanAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
comment|// a/o/u + e is equivalent to the umlaut form
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"SchaltflÃ¤chen"
argument_list|,
literal|"schaltflach"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"Schaltflaechen"
argument_list|,
literal|"schaltflach"
argument_list|)
expr_stmt|;
comment|// here they are with the old stemmer
name|a
operator|=
operator|new
name|GermanAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"SchaltflÃ¤chen"
argument_list|,
literal|"schaltflach"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"Schaltflaechen"
argument_list|,
literal|"schaltflaech"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

