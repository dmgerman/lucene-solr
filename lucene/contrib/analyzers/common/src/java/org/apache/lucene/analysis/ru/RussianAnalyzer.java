begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|Reader
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
name|LowerCaseFilter
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
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
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
name|KeywordMarkerFilter
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
name|StopFilter
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
name|StopwordAnalyzerBase
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
name|WordlistLoader
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
comment|/**  * {@link Analyzer} for Russian language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all).  * A default set of stopwords is used unless an alternative list is specified.  *</p>  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating RussianAnalyzer:  *<ul>  *<li> As of 3.1, StandardTokenizer is used, Snowball stemming is done with  *        SnowballFilter, and Snowball stopwords are used by default.  *</ul>  */
end_comment

begin_class
DECL|class|RussianAnalyzer
specifier|public
specifier|final
class|class
name|RussianAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/**      * List of typical Russian stopwords. (for backwards compatibility)      * @deprecated Remove this for LUCENE 4.0      */
annotation|@
name|Deprecated
DECL|field|RUSSIAN_STOP_WORDS_30
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|RUSSIAN_STOP_WORDS_30
init|=
block|{
literal|"Ð°"
block|,
literal|"Ð±ÐµÐ·"
block|,
literal|"Ð±Ð¾Ð»ÐµÐµ"
block|,
literal|"Ð±Ñ"
block|,
literal|"Ð±ÑÐ»"
block|,
literal|"Ð±ÑÐ»Ð°"
block|,
literal|"Ð±ÑÐ»Ð¸"
block|,
literal|"Ð±ÑÐ»Ð¾"
block|,
literal|"Ð±ÑÑÑ"
block|,
literal|"Ð²"
block|,
literal|"Ð²Ð°Ð¼"
block|,
literal|"Ð²Ð°Ñ"
block|,
literal|"Ð²ÐµÑÑ"
block|,
literal|"Ð²Ð¾"
block|,
literal|"Ð²Ð¾Ñ"
block|,
literal|"Ð²ÑÐµ"
block|,
literal|"Ð²ÑÐµÐ³Ð¾"
block|,
literal|"Ð²ÑÐµÑ"
block|,
literal|"Ð²Ñ"
block|,
literal|"Ð³Ð´Ðµ"
block|,
literal|"Ð´Ð°"
block|,
literal|"Ð´Ð°Ð¶Ðµ"
block|,
literal|"Ð´Ð»Ñ"
block|,
literal|"Ð´Ð¾"
block|,
literal|"ÐµÐ³Ð¾"
block|,
literal|"ÐµÐµ"
block|,
literal|"ÐµÐ¹"
block|,
literal|"ÐµÑ"
block|,
literal|"ÐµÑÐ»Ð¸"
block|,
literal|"ÐµÑÑÑ"
block|,
literal|"ÐµÑÐµ"
block|,
literal|"Ð¶Ðµ"
block|,
literal|"Ð·Ð°"
block|,
literal|"Ð·Ð´ÐµÑÑ"
block|,
literal|"Ð¸"
block|,
literal|"Ð¸Ð·"
block|,
literal|"Ð¸Ð»Ð¸"
block|,
literal|"Ð¸Ð¼"
block|,
literal|"Ð¸Ñ"
block|,
literal|"Ðº"
block|,
literal|"ÐºÐ°Ðº"
block|,
literal|"ÐºÐ¾"
block|,
literal|"ÐºÐ¾Ð³Ð´Ð°"
block|,
literal|"ÐºÑÐ¾"
block|,
literal|"Ð»Ð¸"
block|,
literal|"Ð»Ð¸Ð±Ð¾"
block|,
literal|"Ð¼Ð½Ðµ"
block|,
literal|"Ð¼Ð¾Ð¶ÐµÑ"
block|,
literal|"Ð¼Ñ"
block|,
literal|"Ð½Ð°"
block|,
literal|"Ð½Ð°Ð´Ð¾"
block|,
literal|"Ð½Ð°Ñ"
block|,
literal|"Ð½Ðµ"
block|,
literal|"Ð½ÐµÐ³Ð¾"
block|,
literal|"Ð½ÐµÐµ"
block|,
literal|"Ð½ÐµÑ"
block|,
literal|"Ð½Ð¸"
block|,
literal|"Ð½Ð¸Ñ"
block|,
literal|"Ð½Ð¾"
block|,
literal|"Ð½Ñ"
block|,
literal|"Ð¾"
block|,
literal|"Ð¾Ð±"
block|,
literal|"Ð¾Ð´Ð½Ð°ÐºÐ¾"
block|,
literal|"Ð¾Ð½"
block|,
literal|"Ð¾Ð½Ð°"
block|,
literal|"Ð¾Ð½Ð¸"
block|,
literal|"Ð¾Ð½Ð¾"
block|,
literal|"Ð¾Ñ"
block|,
literal|"Ð¾ÑÐµÐ½Ñ"
block|,
literal|"Ð¿Ð¾"
block|,
literal|"Ð¿Ð¾Ð´"
block|,
literal|"Ð¿ÑÐ¸"
block|,
literal|"Ñ"
block|,
literal|"ÑÐ¾"
block|,
literal|"ÑÐ°Ðº"
block|,
literal|"ÑÐ°ÐºÐ¶Ðµ"
block|,
literal|"ÑÐ°ÐºÐ¾Ð¹"
block|,
literal|"ÑÐ°Ð¼"
block|,
literal|"ÑÐµ"
block|,
literal|"ÑÐµÐ¼"
block|,
literal|"ÑÐ¾"
block|,
literal|"ÑÐ¾Ð³Ð¾"
block|,
literal|"ÑÐ¾Ð¶Ðµ"
block|,
literal|"ÑÐ¾Ð¹"
block|,
literal|"ÑÐ¾Ð»ÑÐºÐ¾"
block|,
literal|"ÑÐ¾Ð¼"
block|,
literal|"ÑÑ"
block|,
literal|"Ñ"
block|,
literal|"ÑÐ¶Ðµ"
block|,
literal|"ÑÐ¾ÑÑ"
block|,
literal|"ÑÐµÐ³Ð¾"
block|,
literal|"ÑÐµÐ¹"
block|,
literal|"ÑÐµÐ¼"
block|,
literal|"ÑÑÐ¾"
block|,
literal|"ÑÑÐ¾Ð±Ñ"
block|,
literal|"ÑÑÐµ"
block|,
literal|"ÑÑÑ"
block|,
literal|"ÑÑÐ°"
block|,
literal|"ÑÑÐ¸"
block|,
literal|"ÑÑÐ¾"
block|,
literal|"Ñ"
block|}
decl_stmt|;
comment|/** File containing default Russian stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"russian_stop.txt"
decl_stmt|;
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
comment|/** @deprecated remove this for Lucene 4.0 */
annotation|@
name|Deprecated
DECL|field|DEFAULT_STOP_SET_30
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_STOP_SET_30
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|RUSSIAN_STOP_WORDS_30
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_STOP_SET
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_STOP_SET
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_STOP_SET
operator|=
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|SnowballFilter
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// default set should always be present as it is part of the
comment|// distribution (JAR)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to load default stopword set"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|stemExclusionSet
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionSet
decl_stmt|;
comment|/**      * Returns an unmodifiable instance of the default stop-words set.      *       * @return an unmodifiable instance of the default stop-words set.      */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|?
argument_list|>
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
return|;
block|}
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|?
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
else|:
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET_30
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      * @deprecated use {@link #RussianAnalyzer(Version, Set)} instead      */
annotation|@
name|Deprecated
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words      *       * @param matchVersion      *          lucene compatibility version      * @param stopwords      *          a stopword set      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words      *       * @param matchVersion      *          lucene compatibility version      * @param stopwords      *          a stopword set      * @param stemExclusionSet a set of words not to be stemmed      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionSet
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemExclusionSet
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|stemExclusionSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      * TODO: create a Set version of this ctor      * @deprecated use {@link #RussianAnalyzer(Version, Set)} instead      */
annotation|@
name|Deprecated
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates    * {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents}    * used to tokenize all the text in the provided {@link Reader}.    *     * @return {@link org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents}    *         built from a {@link StandardTokenizer} filtered with    *         {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}    *         , {@link KeywordMarkerFilter} if a stem exclusion set is    *         provided, and {@link SnowballFilter}    */
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
if|if
condition|(
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|)
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stemExclusionSet
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|stemExclusionSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
operator|new
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|RussianStemmer
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stemExclusionSet
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|stemExclusionSet
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|RussianStemFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

