begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CachingTokenFilter
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
name|CharFilter
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
name|CrankyTokenFilter
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
name|MockCharFilter
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
name|MockFixedLengthPayloadFilter
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
name|MockGraphTokenFilter
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
name|MockHoleInjectingTokenFilter
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
name|MockLowerCaseFilter
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
name|MockRandomLookaheadTokenFilter
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
name|MockSynonymFilter
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
name|MockTokenFilter
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
name|MockVariableLengthPayloadFilter
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
name|SimplePayloadFilter
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
name|TokenFilter
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
name|ValidatingTokenFilter
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
name|PatternKeywordMarkerFilter
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
name|SetKeywordMarkerFilter
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
name|path
operator|.
name|ReversePathHierarchyTokenizer
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
name|sinks
operator|.
name|TeeSinkTokenFilter
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
name|snowball
operator|.
name|SnowballFilter
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
name|sr
operator|.
name|SerbianNormalizationRegularFilter
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
name|CharFilterFactory
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
name|ResourceLoaderAware
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
name|TokenizerFactory
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

begin_comment
comment|/**  * Tests that any newly added Tokenizers/TokenFilters/CharFilters have a  * corresponding factory (and that the SPI configuration is correct)  */
end_comment

begin_class
DECL|class|TestAllAnalyzersHaveFactories
specifier|public
class|class
name|TestAllAnalyzersHaveFactories
extends|extends
name|LuceneTestCase
block|{
comment|// these are test-only components (e.g. test-framework)
DECL|field|testComponents
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|testComponents
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
argument_list|>
operator|>
name|addAll
argument_list|(
name|testComponents
argument_list|,
name|MockTokenizer
operator|.
name|class
argument_list|,
name|MockCharFilter
operator|.
name|class
argument_list|,
name|MockFixedLengthPayloadFilter
operator|.
name|class
argument_list|,
name|MockGraphTokenFilter
operator|.
name|class
argument_list|,
name|MockHoleInjectingTokenFilter
operator|.
name|class
argument_list|,
name|MockLowerCaseFilter
operator|.
name|class
argument_list|,
name|MockRandomLookaheadTokenFilter
operator|.
name|class
argument_list|,
name|MockSynonymFilter
operator|.
name|class
argument_list|,
name|MockTokenFilter
operator|.
name|class
argument_list|,
name|MockVariableLengthPayloadFilter
operator|.
name|class
argument_list|,
name|ValidatingTokenFilter
operator|.
name|class
argument_list|,
name|CrankyTokenFilter
operator|.
name|class
argument_list|,
name|SimplePayloadFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// these are 'crazy' components like cachingtokenfilter. does it make sense to add factories for these?
DECL|field|crazyComponents
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|crazyComponents
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
argument_list|>
operator|>
name|addAll
argument_list|(
name|crazyComponents
argument_list|,
name|CachingTokenFilter
operator|.
name|class
argument_list|,
name|TeeSinkTokenFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// these are oddly-named (either the actual analyzer, or its factory)
comment|// they do actually have factories.
comment|// TODO: clean this up!
DECL|field|oddlyNamedComponents
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|oddlyNamedComponents
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
static|static
block|{
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
argument_list|>
operator|>
name|addAll
argument_list|(
name|oddlyNamedComponents
argument_list|,
name|ReversePathHierarchyTokenizer
operator|.
name|class
argument_list|,
comment|// this is supported via an option to PathHierarchyTokenizer's factory
name|SnowballFilter
operator|.
name|class
argument_list|,
comment|// this is called SnowballPorterFilterFactory
name|PatternKeywordMarkerFilter
operator|.
name|class
argument_list|,
name|SetKeywordMarkerFilter
operator|.
name|class
argument_list|,
name|UnicodeWhitespaceTokenizer
operator|.
name|class
argument_list|,
comment|// a supported option via WhitespaceTokenizerFactory
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|StopFilter
operator|.
name|class
argument_list|,
comment|// class from core, but StopFilterFactory creates one from this module
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|LowerCaseFilter
operator|.
name|class
comment|// class from core, but LowerCaseFilterFactory creates one from this module
argument_list|)
expr_stmt|;
block|}
comment|// The following token filters are excused from having their factory.
DECL|field|tokenFiltersWithoutFactory
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokenFiltersWithoutFactory
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|tokenFiltersWithoutFactory
operator|.
name|add
argument_list|(
name|SerbianNormalizationRegularFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|ResourceLoader
name|loader
init|=
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|analysisClasses
init|=
name|TestRandomChains
operator|.
name|getClassesForPackage
argument_list|(
literal|"org.apache.lucene.analysis"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|c
range|:
name|analysisClasses
control|)
block|{
specifier|final
name|int
name|modifiers
init|=
name|c
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
if|if
condition|(
comment|// don't waste time with abstract classes
name|Modifier
operator|.
name|isAbstract
argument_list|(
name|modifiers
argument_list|)
operator|||
operator|!
name|Modifier
operator|.
name|isPublic
argument_list|(
name|modifiers
argument_list|)
operator|||
name|c
operator|.
name|isSynthetic
argument_list|()
operator|||
name|c
operator|.
name|isAnonymousClass
argument_list|()
operator|||
name|c
operator|.
name|isMemberClass
argument_list|()
operator|||
name|c
operator|.
name|isInterface
argument_list|()
operator|||
name|testComponents
operator|.
name|contains
argument_list|(
name|c
argument_list|)
operator|||
name|crazyComponents
operator|.
name|contains
argument_list|(
name|c
argument_list|)
operator|||
name|oddlyNamedComponents
operator|.
name|contains
argument_list|(
name|c
argument_list|)
operator|||
name|tokenFiltersWithoutFactory
operator|.
name|contains
argument_list|(
name|c
argument_list|)
operator|||
name|c
operator|.
name|isAnnotationPresent
argument_list|(
name|Deprecated
operator|.
name|class
argument_list|)
comment|// deprecated ones are typically back compat hacks
operator|||
operator|!
operator|(
name|Tokenizer
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
operator|||
name|TokenFilter
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
operator|||
name|CharFilter
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
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
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
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
if|if
condition|(
name|Tokenizer
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|String
name|clazzName
init|=
name|c
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|clazzName
operator|.
name|endsWith
argument_list|(
literal|"Tokenizer"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|simpleName
init|=
name|clazzName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clazzName
operator|.
name|length
argument_list|()
operator|-
literal|9
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|TokenizerFactory
operator|.
name|lookupClass
argument_list|(
name|simpleName
argument_list|)
argument_list|)
expr_stmt|;
name|TokenizerFactory
name|instance
init|=
literal|null
decl_stmt|;
try|try
block|{
name|instance
operator|=
name|TokenizerFactory
operator|.
name|forName
argument_list|(
name|simpleName
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|instance
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
name|assertSame
argument_list|(
name|c
argument_list|,
name|instance
operator|.
name|create
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// TODO: For now pass because some factories have not yet a default config that always works
block|}
block|}
elseif|else
if|if
condition|(
name|TokenFilter
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|String
name|clazzName
init|=
name|c
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|clazzName
operator|.
name|endsWith
argument_list|(
literal|"Filter"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|simpleName
init|=
name|clazzName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clazzName
operator|.
name|length
argument_list|()
operator|-
operator|(
name|clazzName
operator|.
name|endsWith
argument_list|(
literal|"TokenFilter"
argument_list|)
condition|?
literal|11
else|:
literal|6
operator|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|TokenFilterFactory
operator|.
name|lookupClass
argument_list|(
name|simpleName
argument_list|)
argument_list|)
expr_stmt|;
name|TokenFilterFactory
name|instance
init|=
literal|null
decl_stmt|;
try|try
block|{
name|instance
operator|=
name|TokenFilterFactory
operator|.
name|forName
argument_list|(
name|simpleName
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|instance
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|TokenStream
argument_list|>
name|createdClazz
init|=
name|instance
operator|.
name|create
argument_list|(
operator|new
name|KeywordTokenizer
argument_list|()
argument_list|)
operator|.
name|getClass
argument_list|()
decl_stmt|;
comment|// only check instance if factory have wrapped at all!
if|if
condition|(
name|KeywordTokenizer
operator|.
name|class
operator|!=
name|createdClazz
condition|)
block|{
name|assertSame
argument_list|(
name|c
argument_list|,
name|createdClazz
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// TODO: For now pass because some factories have not yet a default config that always works
block|}
block|}
elseif|else
if|if
condition|(
name|CharFilter
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|String
name|clazzName
init|=
name|c
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|clazzName
operator|.
name|endsWith
argument_list|(
literal|"CharFilter"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|simpleName
init|=
name|clazzName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clazzName
operator|.
name|length
argument_list|()
operator|-
literal|10
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|CharFilterFactory
operator|.
name|lookupClass
argument_list|(
name|simpleName
argument_list|)
argument_list|)
expr_stmt|;
name|CharFilterFactory
name|instance
init|=
literal|null
decl_stmt|;
try|try
block|{
name|instance
operator|=
name|CharFilterFactory
operator|.
name|forName
argument_list|(
name|simpleName
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|instance
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|Reader
argument_list|>
name|createdClazz
init|=
name|instance
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|getClass
argument_list|()
decl_stmt|;
comment|// only check instance if factory have wrapped at all!
if|if
condition|(
name|StringReader
operator|.
name|class
operator|!=
name|createdClazz
condition|)
block|{
name|assertSame
argument_list|(
name|c
argument_list|,
name|createdClazz
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// TODO: For now pass because some factories have not yet a default config that always works
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

