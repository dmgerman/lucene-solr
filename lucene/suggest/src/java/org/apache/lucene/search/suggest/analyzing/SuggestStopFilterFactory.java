begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
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
name|StopAnalyzer
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
name|WordlistLoader
import|;
end_import

begin_comment
comment|// jdocs
end_comment

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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Factory for {@link SuggestStopFilter}.  *  *<pre class="prettyprint">  *&lt;fieldType name="autosuggest" class="solr.TextField"   *            positionIncrementGap="100" autoGeneratePhraseQueries="true"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.LowerCaseFilterFactory"/&gt;  *&lt;filter class="solr.SuggestStopFilterFactory" ignoreCase="true"  *             words="stopwords.txt" format="wordset"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  *<p>  * All attributes are optional:  *</p>  *<ul>  *<li><code>ignoreCase</code> defaults to<code>false</code></li>  *<li><code>words</code> should be the name of a stopwords file to parse, if not   *      specified the factory will use {@link StopAnalyzer#ENGLISH_STOP_WORDS_SET}  *</li>  *<li><code>format</code> defines how the<code>words</code> file will be parsed,   *      and defaults to<code>wordset</code>.  If<code>words</code> is not specified,   *      then<code>format</code> must not be specified.  *</li>  *</ul>  *<p>  * The valid values for the<code>format</code> option are:  *</p>  *<ul>  *<li><code>wordset</code> - This is the default format, which supports one word per   *      line (including any intra-word whitespace) and allows whole line comments   *      begining with the "#" character.  Blank lines are ignored.  See   *      {@link WordlistLoader#getLines WordlistLoader.getLines} for details.  *</li>  *<li><code>snowball</code> - This format allows for multiple words specified on each   *      line, and trailing comments may be specified using the vertical line ("&#124;").   *      Blank lines are ignored.  See   *      {@link WordlistLoader#getSnowballWordSet WordlistLoader.getSnowballWordSet}  *      for details.  *</li>  *</ul>  */
end_comment

begin_class
DECL|class|SuggestStopFilterFactory
specifier|public
class|class
name|SuggestStopFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|FORMAT_WORDSET
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT_WORDSET
init|=
literal|"wordset"
decl_stmt|;
DECL|field|FORMAT_SNOWBALL
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT_SNOWBALL
init|=
literal|"snowball"
decl_stmt|;
DECL|field|stopWords
specifier|private
name|CharArraySet
name|stopWords
decl_stmt|;
DECL|field|stopWordFiles
specifier|private
specifier|final
name|String
name|stopWordFiles
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|String
name|format
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
comment|/** Creates a new StopFilterFactory */
DECL|method|SuggestStopFilterFactory
specifier|public
name|SuggestStopFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|stopWordFiles
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"words"
argument_list|)
expr_stmt|;
name|format
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"format"
argument_list|,
operator|(
literal|null
operator|==
name|stopWordFiles
condition|?
literal|null
else|:
name|FORMAT_WORDSET
operator|)
argument_list|)
expr_stmt|;
name|ignoreCase
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|stopWordFiles
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|FORMAT_WORDSET
operator|.
name|equalsIgnoreCase
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|stopWords
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|stopWordFiles
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|FORMAT_SNOWBALL
operator|.
name|equalsIgnoreCase
argument_list|(
name|format
argument_list|)
condition|)
block|{
name|stopWords
operator|=
name|getSnowballWordSet
argument_list|(
name|loader
argument_list|,
name|stopWordFiles
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown 'format' specified for 'words' file: "
operator|+
name|format
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
literal|null
operator|!=
name|format
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'format' can not be specified w/o an explicit 'words' file: "
operator|+
name|format
argument_list|)
throw|;
block|}
name|stopWords
operator|=
operator|new
name|CharArraySet
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isIgnoreCase
specifier|public
name|boolean
name|isIgnoreCase
parameter_list|()
block|{
return|return
name|ignoreCase
return|;
block|}
DECL|method|getStopWords
specifier|public
name|CharArraySet
name|getStopWords
parameter_list|()
block|{
return|return
name|stopWords
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|SuggestStopFilter
name|suggestStopFilter
init|=
operator|new
name|SuggestStopFilter
argument_list|(
name|input
argument_list|,
name|stopWords
argument_list|)
decl_stmt|;
return|return
name|suggestStopFilter
return|;
block|}
block|}
end_class

end_unit

