begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package

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
name|charfilter
operator|.
name|HTMLStripCharFilterFactory
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
name|LowerCaseFilterFactory
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
name|WhitespaceTokenizerFactory
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
name|miscellaneous
operator|.
name|RemoveDuplicatesTokenFilterFactory
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
DECL|class|TestAnalysisSPILoader
specifier|public
class|class
name|TestAnalysisSPILoader
extends|extends
name|LuceneTestCase
block|{
DECL|method|versionArgOnly
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|versionArgOnly
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|method|testLookupTokenizer
specifier|public
name|void
name|testLookupTokenizer
parameter_list|()
block|{
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|TokenizerFactory
operator|.
name|forName
argument_list|(
literal|"Whitespace"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|TokenizerFactory
operator|.
name|forName
argument_list|(
literal|"WHITESPACE"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|TokenizerFactory
operator|.
name|forName
argument_list|(
literal|"whitespace"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusLookupTokenizer
specifier|public
name|void
name|testBogusLookupTokenizer
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenizerFactory
operator|.
name|forName
argument_list|(
literal|"sdfsdfsdfdsfsdfsdf"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenizerFactory
operator|.
name|forName
argument_list|(
literal|"!(**#$U*#$*"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLookupTokenizerClass
specifier|public
name|void
name|testLookupTokenizerClass
parameter_list|()
block|{
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
literal|"Whitespace"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
literal|"WHITESPACE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|WhitespaceTokenizerFactory
operator|.
name|class
argument_list|,
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
literal|"whitespace"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusLookupTokenizerClass
specifier|public
name|void
name|testBogusLookupTokenizerClass
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
literal|"sdfsdfsdfdsfsdfsdf"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
literal|"!(**#$U*#$*"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAvailableTokenizers
specifier|public
name|void
name|testAvailableTokenizers
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|TokenizerFactory
operator|.
name|availableTokenizers
argument_list|()
operator|.
name|contains
argument_list|(
literal|"whitespace"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLookupTokenFilter
specifier|public
name|void
name|testLookupTokenFilter
parameter_list|()
block|{
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"Lowercase"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"LOWERCASE"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"lowercase"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RemoveDuplicatesTokenFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"RemoveDuplicates"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RemoveDuplicatesTokenFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"REMOVEDUPLICATES"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RemoveDuplicatesTokenFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"removeduplicates"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusLookupTokenFilter
specifier|public
name|void
name|testBogusLookupTokenFilter
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"sdfsdfsdfdsfsdfsdf"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
literal|"!(**#$U*#$*"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLookupTokenFilterClass
specifier|public
name|void
name|testLookupTokenFilterClass
parameter_list|()
block|{
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"Lowercase"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"LOWERCASE"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|LowerCaseFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"lowercase"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RemoveDuplicatesTokenFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"RemoveDuplicates"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RemoveDuplicatesTokenFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"REMOVEDUPLICATES"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RemoveDuplicatesTokenFilterFactory
operator|.
name|class
argument_list|,
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"removeduplicates"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusLookupTokenFilterClass
specifier|public
name|void
name|testBogusLookupTokenFilterClass
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"sdfsdfsdfdsfsdfsdf"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"!(**#$U*#$*"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAvailableTokenFilters
specifier|public
name|void
name|testAvailableTokenFilters
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|TokenFilterFactory
operator|.
name|availableTokenFilters
argument_list|()
operator|.
name|contains
argument_list|(
literal|"lowercase"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|TokenFilterFactory
operator|.
name|availableTokenFilters
argument_list|()
operator|.
name|contains
argument_list|(
literal|"removeduplicates"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLookupCharFilter
specifier|public
name|void
name|testLookupCharFilter
parameter_list|()
block|{
name|assertSame
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|CharFilterFactory
operator|.
name|forName
argument_list|(
literal|"HTMLStrip"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|CharFilterFactory
operator|.
name|forName
argument_list|(
literal|"HTMLSTRIP"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|CharFilterFactory
operator|.
name|forName
argument_list|(
literal|"htmlstrip"
argument_list|,
name|versionArgOnly
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusLookupCharFilter
specifier|public
name|void
name|testBogusLookupCharFilter
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CharFilterFactory
operator|.
name|forName
argument_list|(
literal|"sdfsdfsdfdsfsdfsdf"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CharFilterFactory
operator|.
name|forName
argument_list|(
literal|"!(**#$U*#$*"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLookupCharFilterClass
specifier|public
name|void
name|testLookupCharFilterClass
parameter_list|()
block|{
name|assertSame
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"HTMLStrip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"HTMLSTRIP"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|HTMLStripCharFilterFactory
operator|.
name|class
argument_list|,
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"htmlstrip"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusLookupCharFilterClass
specifier|public
name|void
name|testBogusLookupCharFilterClass
parameter_list|()
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"sdfsdfsdfdsfsdfsdf"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
literal|"!(**#$U*#$*"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAvailableCharFilters
specifier|public
name|void
name|testAvailableCharFilters
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|CharFilterFactory
operator|.
name|availableCharFilters
argument_list|()
operator|.
name|contains
argument_list|(
literal|"htmlstrip"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

