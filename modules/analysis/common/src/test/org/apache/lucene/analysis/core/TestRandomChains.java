begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Random
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
name|CharReader
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
name|CharStream
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
name|EmptyTokenizer
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
name|ngram
operator|.
name|EdgeNGramTokenFilter
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
name|ngram
operator|.
name|EdgeNGramTokenizer
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
name|ngram
operator|.
name|NGramTokenFilter
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
name|ngram
operator|.
name|NGramTokenizer
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

begin_comment
comment|/** tests random analysis chains */
end_comment

begin_class
DECL|class|TestRandomChains
specifier|public
class|class
name|TestRandomChains
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|tokenizers
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Tokenizer
argument_list|>
argument_list|>
name|tokenizers
decl_stmt|;
DECL|field|tokenfilters
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|TokenFilter
argument_list|>
argument_list|>
name|tokenfilters
decl_stmt|;
DECL|field|charfilters
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CharStream
argument_list|>
argument_list|>
name|charfilters
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
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|getClassesForPackage
argument_list|(
literal|"org.apache.lucene.analysis"
argument_list|,
name|analysisClasses
argument_list|)
expr_stmt|;
name|tokenizers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Tokenizer
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|tokenfilters
operator|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|TokenFilter
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|charfilters
operator|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|CharStream
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
range|:
name|analysisClasses
control|)
block|{
comment|// don't waste time with abstract classes or deprecated known-buggy ones
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
name|getAnnotation
argument_list|(
name|Deprecated
operator|.
name|class
argument_list|)
operator|!=
literal|null
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
comment|// TODO: fix basetokenstreamtestcase not to trip because this one has no CharTermAtt
operator|||
name|c
operator|.
name|equals
argument_list|(
name|EmptyTokenizer
operator|.
name|class
argument_list|)
comment|// doesn't actual reset itself!
operator|||
name|c
operator|.
name|equals
argument_list|(
name|CachingTokenFilter
operator|.
name|class
argument_list|)
comment|// broken!
operator|||
name|c
operator|.
name|equals
argument_list|(
name|NGramTokenizer
operator|.
name|class
argument_list|)
comment|// broken!
operator|||
name|c
operator|.
name|equals
argument_list|(
name|NGramTokenFilter
operator|.
name|class
argument_list|)
comment|// broken!
operator|||
name|c
operator|.
name|equals
argument_list|(
name|EdgeNGramTokenizer
operator|.
name|class
argument_list|)
comment|// broken!
operator|||
name|c
operator|.
name|equals
argument_list|(
name|EdgeNGramTokenFilter
operator|.
name|class
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
name|tokenizers
operator|.
name|add
argument_list|(
name|c
operator|.
name|asSubclass
argument_list|(
name|Tokenizer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
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
name|tokenfilters
operator|.
name|add
argument_list|(
name|c
operator|.
name|asSubclass
argument_list|(
name|TokenFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CharStream
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|charfilters
operator|.
name|add
argument_list|(
name|c
operator|.
name|asSubclass
argument_list|(
name|CharStream
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Comparator
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|classComp
init|=
operator|new
name|Comparator
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|arg0
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|arg1
parameter_list|)
block|{
return|return
name|arg0
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|arg1
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
empty_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|tokenizers
argument_list|,
name|classComp
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|tokenfilters
argument_list|,
name|classComp
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|charfilters
argument_list|,
name|classComp
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tokenizers = "
operator|+
name|tokenizers
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tokenfilters = "
operator|+
name|tokenfilters
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"charfilters = "
operator|+
name|charfilters
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|tokenizers
operator|=
literal|null
expr_stmt|;
name|tokenfilters
operator|=
literal|null
expr_stmt|;
name|charfilters
operator|=
literal|null
expr_stmt|;
block|}
DECL|class|MockRandomAnalyzer
specifier|static
class|class
name|MockRandomAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|seed
specifier|final
name|long
name|seed
decl_stmt|;
DECL|method|MockRandomAnalyzer
name|MockRandomAnalyzer
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|TokenizerSpec
name|tokenizerspec
init|=
name|newTokenizer
argument_list|(
name|random
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenFilterSpec
name|filterspec
init|=
name|newFilterChain
argument_list|(
name|random
argument_list|,
name|tokenizerspec
operator|.
name|tokenizer
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizerspec
operator|.
name|tokenizer
argument_list|,
name|filterspec
operator|.
name|stream
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initReader
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|CharFilterSpec
name|charfilterspec
init|=
name|newCharFilterChain
argument_list|(
name|random
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
name|charfilterspec
operator|.
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|CharFilterSpec
name|charfilterSpec
init|=
name|newCharFilterChain
argument_list|(
name|random
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\ncharfilters="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|charfilterSpec
operator|.
name|toString
argument_list|)
expr_stmt|;
comment|// intentional: initReader gets its own separate random
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|TokenizerSpec
name|tokenizerSpec
init|=
name|newTokenizer
argument_list|(
name|random
argument_list|,
name|charfilterSpec
operator|.
name|reader
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"tokenizer="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|tokenizerSpec
operator|.
name|toString
argument_list|)
expr_stmt|;
name|TokenFilterSpec
name|tokenfilterSpec
init|=
name|newFilterChain
argument_list|(
name|random
argument_list|,
name|tokenizerSpec
operator|.
name|tokenizer
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"filters="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|tokenfilterSpec
operator|.
name|toString
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// create a new random tokenizer from classpath
DECL|method|newTokenizer
specifier|private
name|TokenizerSpec
name|newTokenizer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenizerSpec
name|spec
init|=
operator|new
name|TokenizerSpec
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
comment|// TODO: check Reader+Version,Version+Reader too
comment|// also look for other variants and handle them special
name|int
name|idx
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|tokenizers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|Tokenizer
argument_list|>
name|c
init|=
name|tokenizers
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getConstructor
argument_list|(
name|Version
operator|.
name|class
argument_list|,
name|Reader
operator|.
name|class
argument_list|)
decl_stmt|;
name|spec
operator|.
name|tokenizer
operator|=
name|c
operator|.
name|newInstance
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|Tokenizer
argument_list|>
name|c
init|=
name|tokenizers
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getConstructor
argument_list|(
name|Reader
operator|.
name|class
argument_list|)
decl_stmt|;
name|spec
operator|.
name|tokenizer
operator|=
name|c
operator|.
name|newInstance
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|spec
operator|.
name|toString
operator|=
name|tokenizers
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|spec
return|;
block|}
DECL|method|newCharFilterChain
specifier|private
name|CharFilterSpec
name|newCharFilterChain
parameter_list|(
name|Random
name|random
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|CharFilterSpec
name|spec
init|=
operator|new
name|CharFilterSpec
argument_list|()
decl_stmt|;
name|spec
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|StringBuilder
name|descr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numFilters
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
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
name|numFilters
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
comment|// TODO: also look for other variants and handle them special
name|int
name|idx
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|charfilters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|CharStream
argument_list|>
name|c
init|=
name|charfilters
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getConstructor
argument_list|(
name|Reader
operator|.
name|class
argument_list|)
decl_stmt|;
name|spec
operator|.
name|reader
operator|=
name|c
operator|.
name|newInstance
argument_list|(
name|spec
operator|.
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|CharStream
argument_list|>
name|c
init|=
name|charfilters
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getConstructor
argument_list|(
name|CharStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|spec
operator|.
name|reader
operator|=
name|c
operator|.
name|newInstance
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
name|spec
operator|.
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|descr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|descr
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|descr
operator|.
name|append
argument_list|(
name|charfilters
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
name|spec
operator|.
name|toString
operator|=
name|descr
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|spec
return|;
block|}
DECL|method|newFilterChain
specifier|private
name|TokenFilterSpec
name|newFilterChain
parameter_list|(
name|Random
name|random
parameter_list|,
name|Tokenizer
name|tokenizer
parameter_list|)
block|{
name|TokenFilterSpec
name|spec
init|=
operator|new
name|TokenFilterSpec
argument_list|()
decl_stmt|;
name|spec
operator|.
name|stream
operator|=
name|tokenizer
expr_stmt|;
name|StringBuilder
name|descr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numFilters
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
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
name|numFilters
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
comment|// TODO: also look for other variants and handle them special
name|int
name|idx
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|tokenfilters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|TokenFilter
argument_list|>
name|c
init|=
name|tokenfilters
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getConstructor
argument_list|(
name|Version
operator|.
name|class
argument_list|,
name|TokenStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|spec
operator|.
name|stream
operator|=
name|c
operator|.
name|newInstance
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|spec
operator|.
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|TokenFilter
argument_list|>
name|c
init|=
name|tokenfilters
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|getConstructor
argument_list|(
name|TokenStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|spec
operator|.
name|stream
operator|=
name|c
operator|.
name|newInstance
argument_list|(
name|spec
operator|.
name|stream
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|descr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|descr
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|descr
operator|.
name|append
argument_list|(
name|tokenfilters
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
name|spec
operator|.
name|toString
operator|=
name|descr
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|spec
return|;
block|}
block|}
DECL|class|TokenizerSpec
specifier|static
class|class
name|TokenizerSpec
block|{
DECL|field|tokenizer
name|Tokenizer
name|tokenizer
decl_stmt|;
DECL|field|toString
name|String
name|toString
decl_stmt|;
block|}
DECL|class|TokenFilterSpec
specifier|static
class|class
name|TokenFilterSpec
block|{
DECL|field|stream
name|TokenStream
name|stream
decl_stmt|;
DECL|field|toString
name|String
name|toString
decl_stmt|;
block|}
DECL|class|CharFilterSpec
specifier|static
class|class
name|CharFilterSpec
block|{
DECL|field|reader
name|Reader
name|reader
decl_stmt|;
DECL|field|toString
name|String
name|toString
decl_stmt|;
block|}
DECL|method|testRandomChains
specifier|public
name|void
name|testRandomChains
parameter_list|()
throws|throws
name|Throwable
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
name|MockRandomAnalyzer
name|a
init|=
operator|new
name|MockRandomAnalyzer
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating random analyzer:"
operator|+
name|a
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Exception from random analyzer: "
operator|+
name|a
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|method|getClassesForPackage
specifier|private
specifier|static
name|void
name|getClassesForPackage
parameter_list|(
name|String
name|pckgname
parameter_list|,
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|classes
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ClassLoader
name|cld
init|=
name|TestRandomChains
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|pckgname
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
specifier|final
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|cld
operator|.
name|getResources
argument_list|(
name|path
argument_list|)
decl_stmt|;
while|while
condition|(
name|resources
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
name|resources
operator|.
name|nextElement
argument_list|()
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|file
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// recurse
name|String
name|subPackage
init|=
name|pckgname
operator|+
literal|"."
operator|+
name|file
decl_stmt|;
name|getClassesForPackage
argument_list|(
name|subPackage
argument_list|,
name|classes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|file
operator|.
name|endsWith
argument_list|(
literal|".class"
argument_list|)
condition|)
block|{
name|String
name|clazzName
init|=
name|file
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|file
operator|.
name|length
argument_list|()
operator|-
literal|6
argument_list|)
decl_stmt|;
comment|// exclude Test classes that happen to be in these packages.
comment|// class.ForName'ing some of them can cause trouble.
if|if
condition|(
operator|!
name|clazzName
operator|.
name|endsWith
argument_list|(
literal|"Test"
argument_list|)
operator|&&
operator|!
name|clazzName
operator|.
name|startsWith
argument_list|(
literal|"Test"
argument_list|)
condition|)
block|{
comment|// Don't run static initializers, as we won't use most of them.
comment|// Java will do that automatically once accessed/instantiated.
name|classes
operator|.
name|add
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|pckgname
operator|+
literal|'.'
operator|+
name|clazzName
argument_list|,
literal|false
argument_list|,
name|cld
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

