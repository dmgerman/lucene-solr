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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_comment
comment|/**  * Analyzer for Russian language. Supports an external list of stopwords (words that  * will not be indexed at all).  * A default set of stopwords is used unless an alternative list is specified.  *  * @author  Boris Okner, b.okner@rogers.com  * @version $Id$  */
end_comment

begin_class
DECL|class|RussianAnalyzer
specifier|public
specifier|final
class|class
name|RussianAnalyzer
extends|extends
name|Analyzer
block|{
comment|// letters (currently unused letters are commented out)
DECL|field|A
specifier|private
specifier|final
specifier|static
name|char
name|A
init|=
literal|0
decl_stmt|;
DECL|field|B
specifier|private
specifier|final
specifier|static
name|char
name|B
init|=
literal|1
decl_stmt|;
DECL|field|V
specifier|private
specifier|final
specifier|static
name|char
name|V
init|=
literal|2
decl_stmt|;
DECL|field|G
specifier|private
specifier|final
specifier|static
name|char
name|G
init|=
literal|3
decl_stmt|;
DECL|field|D
specifier|private
specifier|final
specifier|static
name|char
name|D
init|=
literal|4
decl_stmt|;
DECL|field|E
specifier|private
specifier|final
specifier|static
name|char
name|E
init|=
literal|5
decl_stmt|;
DECL|field|ZH
specifier|private
specifier|final
specifier|static
name|char
name|ZH
init|=
literal|6
decl_stmt|;
DECL|field|Z
specifier|private
specifier|final
specifier|static
name|char
name|Z
init|=
literal|7
decl_stmt|;
DECL|field|I
specifier|private
specifier|final
specifier|static
name|char
name|I
init|=
literal|8
decl_stmt|;
DECL|field|I_
specifier|private
specifier|final
specifier|static
name|char
name|I_
init|=
literal|9
decl_stmt|;
DECL|field|K
specifier|private
specifier|final
specifier|static
name|char
name|K
init|=
literal|10
decl_stmt|;
DECL|field|L
specifier|private
specifier|final
specifier|static
name|char
name|L
init|=
literal|11
decl_stmt|;
DECL|field|M
specifier|private
specifier|final
specifier|static
name|char
name|M
init|=
literal|12
decl_stmt|;
DECL|field|N
specifier|private
specifier|final
specifier|static
name|char
name|N
init|=
literal|13
decl_stmt|;
DECL|field|O
specifier|private
specifier|final
specifier|static
name|char
name|O
init|=
literal|14
decl_stmt|;
DECL|field|P
specifier|private
specifier|final
specifier|static
name|char
name|P
init|=
literal|15
decl_stmt|;
DECL|field|R
specifier|private
specifier|final
specifier|static
name|char
name|R
init|=
literal|16
decl_stmt|;
DECL|field|S
specifier|private
specifier|final
specifier|static
name|char
name|S
init|=
literal|17
decl_stmt|;
DECL|field|T
specifier|private
specifier|final
specifier|static
name|char
name|T
init|=
literal|18
decl_stmt|;
DECL|field|U
specifier|private
specifier|final
specifier|static
name|char
name|U
init|=
literal|19
decl_stmt|;
comment|//private final static char F = 20;
DECL|field|X
specifier|private
specifier|final
specifier|static
name|char
name|X
init|=
literal|21
decl_stmt|;
comment|//private final static char TS = 22;
DECL|field|CH
specifier|private
specifier|final
specifier|static
name|char
name|CH
init|=
literal|23
decl_stmt|;
DECL|field|SH
specifier|private
specifier|final
specifier|static
name|char
name|SH
init|=
literal|24
decl_stmt|;
DECL|field|SHCH
specifier|private
specifier|final
specifier|static
name|char
name|SHCH
init|=
literal|25
decl_stmt|;
comment|//private final static char HARD = 26;
DECL|field|Y
specifier|private
specifier|final
specifier|static
name|char
name|Y
init|=
literal|27
decl_stmt|;
DECL|field|SOFT
specifier|private
specifier|final
specifier|static
name|char
name|SOFT
init|=
literal|28
decl_stmt|;
DECL|field|AE
specifier|private
specifier|final
specifier|static
name|char
name|AE
init|=
literal|29
decl_stmt|;
DECL|field|IU
specifier|private
specifier|final
specifier|static
name|char
name|IU
init|=
literal|30
decl_stmt|;
DECL|field|IA
specifier|private
specifier|final
specifier|static
name|char
name|IA
init|=
literal|31
decl_stmt|;
comment|/**      * List of typical Russian stopwords.      */
DECL|field|RUSSIAN_STOP_WORDS
specifier|private
specifier|static
name|char
index|[]
index|[]
name|RUSSIAN_STOP_WORDS
init|=
block|{
block|{
name|A
block|}
block|,
block|{
name|B
block|,
name|E
block|,
name|Z
block|}
block|,
block|{
name|B
block|,
name|O
block|,
name|L
block|,
name|E
block|,
name|E
block|}
block|,
block|{
name|B
block|,
name|Y
block|}
block|,
block|{
name|B
block|,
name|Y
block|,
name|L
block|}
block|,
block|{
name|B
block|,
name|Y
block|,
name|L
block|,
name|A
block|}
block|,
block|{
name|B
block|,
name|Y
block|,
name|L
block|,
name|I
block|}
block|,
block|{
name|B
block|,
name|Y
block|,
name|L
block|,
name|O
block|}
block|,
block|{
name|B
block|,
name|Y
block|,
name|T
block|,
name|SOFT
block|}
block|,
block|{
name|V
block|}
block|,
block|{
name|V
block|,
name|A
block|,
name|M
block|}
block|,
block|{
name|V
block|,
name|A
block|,
name|S
block|}
block|,
block|{
name|V
block|,
name|E
block|,
name|S
block|,
name|SOFT
block|}
block|,
block|{
name|V
block|,
name|O
block|}
block|,
block|{
name|V
block|,
name|O
block|,
name|T
block|}
block|,
block|{
name|V
block|,
name|S
block|,
name|E
block|}
block|,
block|{
name|V
block|,
name|S
block|,
name|E
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|V
block|,
name|S
block|,
name|E
block|,
name|X
block|}
block|,
block|{
name|V
block|,
name|Y
block|}
block|,
block|{
name|G
block|,
name|D
block|,
name|E
block|}
block|,
block|{
name|D
block|,
name|A
block|}
block|,
block|{
name|D
block|,
name|A
block|,
name|ZH
block|,
name|E
block|}
block|,
block|{
name|D
block|,
name|L
block|,
name|IA
block|}
block|,
block|{
name|D
block|,
name|O
block|}
block|,
block|{
name|E
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|E
block|,
name|E
block|}
block|,
block|{
name|E
block|,
name|I_
block|,}
block|,
block|{
name|E
block|,
name|IU
block|}
block|,
block|{
name|E
block|,
name|S
block|,
name|L
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|S
block|,
name|T
block|,
name|SOFT
block|}
block|,
block|{
name|E
block|,
name|SHCH
block|,
name|E
block|}
block|,
block|{
name|ZH
block|,
name|E
block|}
block|,
block|{
name|Z
block|,
name|A
block|}
block|,
block|{
name|Z
block|,
name|D
block|,
name|E
block|,
name|S
block|,
name|SOFT
block|}
block|,
block|{
name|I
block|}
block|,
block|{
name|I
block|,
name|Z
block|}
block|,
block|{
name|I
block|,
name|L
block|,
name|I
block|}
block|,
block|{
name|I
block|,
name|M
block|}
block|,
block|{
name|I
block|,
name|X
block|}
block|,
block|{
name|K
block|}
block|,
block|{
name|K
block|,
name|A
block|,
name|K
block|}
block|,
block|{
name|K
block|,
name|O
block|}
block|,
block|{
name|K
block|,
name|O
block|,
name|G
block|,
name|D
block|,
name|A
block|}
block|,
block|{
name|K
block|,
name|T
block|,
name|O
block|}
block|,
block|{
name|L
block|,
name|I
block|}
block|,
block|{
name|L
block|,
name|I
block|,
name|B
block|,
name|O
block|}
block|,
block|{
name|M
block|,
name|N
block|,
name|E
block|}
block|,
block|{
name|M
block|,
name|O
block|,
name|ZH
block|,
name|E
block|,
name|T
block|}
block|,
block|{
name|M
block|,
name|Y
block|}
block|,
block|{
name|N
block|,
name|A
block|}
block|,
block|{
name|N
block|,
name|A
block|,
name|D
block|,
name|O
block|}
block|,
block|{
name|N
block|,
name|A
block|,
name|SH
block|}
block|,
block|{
name|N
block|,
name|E
block|}
block|,
block|{
name|N
block|,
name|E
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|N
block|,
name|E
block|,
name|E
block|}
block|,
block|{
name|N
block|,
name|E
block|,
name|T
block|}
block|,
block|{
name|N
block|,
name|I
block|}
block|,
block|{
name|N
block|,
name|I
block|,
name|X
block|}
block|,
block|{
name|N
block|,
name|O
block|}
block|,
block|{
name|N
block|,
name|U
block|}
block|,
block|{
name|O
block|}
block|,
block|{
name|O
block|,
name|B
block|}
block|,
block|{
name|O
block|,
name|D
block|,
name|N
block|,
name|A
block|,
name|K
block|,
name|O
block|}
block|,
block|{
name|O
block|,
name|N
block|}
block|,
block|{
name|O
block|,
name|N
block|,
name|A
block|}
block|,
block|{
name|O
block|,
name|N
block|,
name|I
block|}
block|,
block|{
name|O
block|,
name|N
block|,
name|O
block|}
block|,
block|{
name|O
block|,
name|T
block|}
block|,
block|{
name|O
block|,
name|CH
block|,
name|E
block|,
name|N
block|,
name|SOFT
block|}
block|,
block|{
name|P
block|,
name|O
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|D
block|}
block|,
block|{
name|P
block|,
name|R
block|,
name|I
block|}
block|,
block|{
name|S
block|}
block|,
block|{
name|S
block|,
name|O
block|}
block|,
block|{
name|T
block|,
name|A
block|,
name|K
block|}
block|,
block|{
name|T
block|,
name|A
block|,
name|K
block|,
name|ZH
block|,
name|E
block|}
block|,
block|{
name|T
block|,
name|A
block|,
name|K
block|,
name|O
block|,
name|I_
block|}
block|,
block|{
name|T
block|,
name|A
block|,
name|M
block|}
block|,
block|{
name|T
block|,
name|E
block|}
block|,
block|{
name|T
block|,
name|E
block|,
name|M
block|}
block|,
block|{
name|T
block|,
name|O
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|ZH
block|,
name|E
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|I_
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|L
block|,
name|SOFT
block|,
name|K
block|,
name|O
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|M
block|}
block|,
block|{
name|T
block|,
name|Y
block|}
block|,
block|{
name|U
block|}
block|,
block|{
name|U
block|,
name|ZH
block|,
name|E
block|}
block|,
block|{
name|X
block|,
name|O
block|,
name|T
block|,
name|IA
block|}
block|,
block|{
name|CH
block|,
name|E
block|,
name|G
block|,
name|O
block|}
block|,
block|{
name|CH
block|,
name|E
block|,
name|I_
block|}
block|,
block|{
name|CH
block|,
name|E
block|,
name|M
block|}
block|,
block|{
name|CH
block|,
name|T
block|,
name|O
block|}
block|,
block|{
name|CH
block|,
name|T
block|,
name|O
block|,
name|B
block|,
name|Y
block|}
block|,
block|{
name|CH
block|,
name|SOFT
block|,
name|E
block|}
block|,
block|{
name|CH
block|,
name|SOFT
block|,
name|IA
block|}
block|,
block|{
name|AE
block|,
name|T
block|,
name|A
block|}
block|,
block|{
name|AE
block|,
name|T
block|,
name|I
block|}
block|,
block|{
name|AE
block|,
name|T
block|,
name|O
block|}
block|,
block|{
name|IA
block|}
block|}
decl_stmt|;
comment|/**      * Contains the stopwords used with the StopFilter.      */
DECL|field|stopSet
specifier|private
name|Set
name|stopSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**      * Charset for Russian letters.      * Represents encoding for 32 lowercase Russian letters.      * Predefined charsets can be taken from RussianCharSets class      */
DECL|field|charset
specifier|private
name|char
index|[]
name|charset
decl_stmt|;
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|()
block|{
name|charset
operator|=
name|RussianCharsets
operator|.
name|UnicodeRussian
expr_stmt|;
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|makeStopWords
argument_list|(
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer.      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|char
index|[]
name|charset
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|makeStopWords
argument_list|(
name|charset
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|char
index|[]
name|charset
parameter_list|,
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|// Takes russian stop words and translates them to a String array, using
comment|// the given charset
DECL|method|makeStopWords
specifier|private
specifier|static
name|String
index|[]
name|makeStopWords
parameter_list|(
name|char
index|[]
name|charset
parameter_list|)
block|{
name|String
index|[]
name|res
init|=
operator|new
name|String
index|[
name|RUSSIAN_STOP_WORDS
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
name|res
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
index|[]
name|theStopWord
init|=
name|RUSSIAN_STOP_WORDS
index|[
name|i
index|]
decl_stmt|;
comment|// translate the word, using the charset
name|StringBuffer
name|theWord
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|theStopWord
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|theWord
operator|.
name|append
argument_list|(
name|charset
index|[
name|theStopWord
index|[
name|j
index|]
index|]
argument_list|)
expr_stmt|;
block|}
name|res
index|[
name|i
index|]
operator|=
name|theWord
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      * @todo create a Set version of this ctor      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|char
index|[]
name|charset
parameter_list|,
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
name|stopSet
operator|=
operator|new
name|HashSet
argument_list|(
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a TokenStream which tokenizes all the text in the provided Reader.      *      * @return  A TokenStream build from a RussianLetterTokenizer filtered with      *                  RussianLowerCaseFilter, StopFilter, and RussianStemFilter      */
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
name|RussianLetterTokenizer
argument_list|(
name|reader
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|RussianLowerCaseFilter
argument_list|(
name|result
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|RussianStemFilter
argument_list|(
name|result
argument_list|,
name|charset
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

