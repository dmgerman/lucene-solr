begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
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
name|InputStreamReader
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|analysis
operator|.
name|ar
operator|.
name|ArabicLetterTokenizer
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
name|ar
operator|.
name|ArabicNormalizationFilter
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
comment|/**  * {@link Analyzer} for Persian.  *<p>  * This Analyzer uses {@link ArabicLetterTokenizer} which implies tokenizing around  * zero-width non-joiner in addition to whitespace. Some persian-specific variant forms (such as farsi  * yeh and keheh) are standardized. "Stemming" is accomplished via stopwords.  *</p>  */
end_comment

begin_class
DECL|class|PersianAnalyzer
specifier|public
specifier|final
class|class
name|PersianAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**    * File containing default Persian stopwords.    *     * Default stopword list is from    * http://members.unine.ch/jacques.savoy/clef/index.html The stopword list is    * BSD-Licensed.    *     */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"stopwords.txt"
decl_stmt|;
comment|/**    * Contains the stopwords used with the StopFilter.    */
DECL|field|stoptable
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stoptable
decl_stmt|;
comment|/**    * The comment character in the stopwords file. All lines prefixed with this    * will be ignored    */
DECL|field|STOPWORDS_COMMENT
specifier|public
specifier|static
specifier|final
name|String
name|STOPWORDS_COMMENT
init|=
literal|"#"
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop-words set.    * @return an unmodifiable instance of the default stop-words set.    */
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
comment|/**    * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer class     * accesses the static final set the first time.;    */
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
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
name|loadDefaultStopWordSet
argument_list|()
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
DECL|method|loadDefaultStopWordSet
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|loadDefaultStopWordSet
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
name|PersianAnalyzer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|DEFAULT_STOPWORD_FILE
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|// make sure it is unmodifiable as we expose it in the outer class
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|reader
argument_list|,
name|STOPWORDS_COMMENT
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words:    * {@link #DEFAULT_STOPWORD_FILE}.    */
DECL|method|PersianAnalyzer
specifier|public
name|PersianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words     *     * @param matchversion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|PersianAnalyzer
specifier|public
name|PersianAnalyzer
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
name|stoptable
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #PersianAnalyzer(Version, Set)} instead    */
DECL|method|PersianAnalyzer
specifier|public
name|PersianAnalyzer
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
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @deprecated use {@link #PersianAnalyzer(Version, Set)} instead    */
DECL|method|PersianAnalyzer
specifier|public
name|PersianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Hashtable
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
comment|/**    * Builds an analyzer with the given stop words. Lines can be commented out    * using {@link #STOPWORDS_COMMENT}    * @deprecated use {@link #PersianAnalyzer(Version, Set)} instead    */
DECL|method|PersianAnalyzer
specifier|public
name|PersianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|,
name|STOPWORDS_COMMENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link TokenStream} which tokenizes all the text in the provided    * {@link Reader}.    *     * @return A {@link TokenStream} built from a {@link ArabicLetterTokenizer}    *         filtered with {@link LowerCaseFilter},     *         {@link ArabicNormalizationFilter},    *         {@link PersianNormalizationFilter} and Persian Stop words    */
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|ArabicLetterTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ArabicNormalizationFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|/* additional persian-specific normalization */
name|result
operator|=
operator|new
name|PersianNormalizationFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|/*      * the order here is important: the stopword list is normalized with the      * above!      */
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|source
name|Tokenizer
name|source
decl_stmt|;
DECL|field|result
name|TokenStream
name|result
decl_stmt|;
block|}
comment|/**    * Returns a (possibly reused) {@link TokenStream} which tokenizes all the text     * in the provided {@link Reader}.    *     * @return A {@link TokenStream} built from a {@link ArabicLetterTokenizer}    *         filtered with {@link LowerCaseFilter},     *         {@link ArabicNormalizationFilter},    *         {@link PersianNormalizationFilter} and Persian Stop words    */
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SavedStreams
name|streams
init|=
operator|(
name|SavedStreams
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
name|streams
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|streams
operator|.
name|source
operator|=
operator|new
name|ArabicLetterTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|streams
operator|.
name|source
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|ArabicNormalizationFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|)
expr_stmt|;
comment|/* additional persian-specific normalization */
name|streams
operator|.
name|result
operator|=
operator|new
name|PersianNormalizationFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|)
expr_stmt|;
comment|/*        * the order here is important: the stopword list is normalized with the        * above!        */
name|streams
operator|.
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|streams
operator|.
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|source
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|streams
operator|.
name|result
return|;
block|}
block|}
end_class

end_unit

