begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.el
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|el
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashSet
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

begin_comment
comment|/**  * Analyzer for the Greek language. Supports an external list of stopwords (words  * that will not be indexed at all).  * A default set of stopwords is used unless an alternative list is specified.  *  * @author  Panagiotis Astithas, past@ebs.gr  */
end_comment

begin_class
DECL|class|GreekAnalyzer
specifier|public
specifier|final
class|class
name|GreekAnalyzer
extends|extends
name|Analyzer
block|{
comment|// the letters are indexes to the charset array (see GreekCharsets.java)
DECL|field|A
specifier|private
specifier|static
name|char
name|A
init|=
literal|6
decl_stmt|;
DECL|field|B
specifier|private
specifier|static
name|char
name|B
init|=
literal|7
decl_stmt|;
DECL|field|G
specifier|private
specifier|static
name|char
name|G
init|=
literal|8
decl_stmt|;
DECL|field|D
specifier|private
specifier|static
name|char
name|D
init|=
literal|9
decl_stmt|;
DECL|field|E
specifier|private
specifier|static
name|char
name|E
init|=
literal|10
decl_stmt|;
DECL|field|Z
specifier|private
specifier|static
name|char
name|Z
init|=
literal|11
decl_stmt|;
DECL|field|H
specifier|private
specifier|static
name|char
name|H
init|=
literal|12
decl_stmt|;
DECL|field|TH
specifier|private
specifier|static
name|char
name|TH
init|=
literal|13
decl_stmt|;
DECL|field|I
specifier|private
specifier|static
name|char
name|I
init|=
literal|14
decl_stmt|;
DECL|field|K
specifier|private
specifier|static
name|char
name|K
init|=
literal|15
decl_stmt|;
DECL|field|L
specifier|private
specifier|static
name|char
name|L
init|=
literal|16
decl_stmt|;
DECL|field|M
specifier|private
specifier|static
name|char
name|M
init|=
literal|17
decl_stmt|;
DECL|field|N
specifier|private
specifier|static
name|char
name|N
init|=
literal|18
decl_stmt|;
DECL|field|KS
specifier|private
specifier|static
name|char
name|KS
init|=
literal|19
decl_stmt|;
DECL|field|O
specifier|private
specifier|static
name|char
name|O
init|=
literal|20
decl_stmt|;
DECL|field|P
specifier|private
specifier|static
name|char
name|P
init|=
literal|21
decl_stmt|;
DECL|field|R
specifier|private
specifier|static
name|char
name|R
init|=
literal|22
decl_stmt|;
DECL|field|S
specifier|private
specifier|static
name|char
name|S
init|=
literal|24
decl_stmt|;
comment|// skip final sigma
DECL|field|T
specifier|private
specifier|static
name|char
name|T
init|=
literal|25
decl_stmt|;
DECL|field|Y
specifier|private
specifier|static
name|char
name|Y
init|=
literal|26
decl_stmt|;
DECL|field|F
specifier|private
specifier|static
name|char
name|F
init|=
literal|27
decl_stmt|;
DECL|field|X
specifier|private
specifier|static
name|char
name|X
init|=
literal|28
decl_stmt|;
DECL|field|PS
specifier|private
specifier|static
name|char
name|PS
init|=
literal|29
decl_stmt|;
DECL|field|W
specifier|private
specifier|static
name|char
name|W
init|=
literal|30
decl_stmt|;
comment|/**      * List of typical Greek stopwords.      */
DECL|field|GREEK_STOP_WORDS
specifier|private
specifier|static
name|char
index|[]
index|[]
name|GREEK_STOP_WORDS
init|=
block|{
block|{
name|O
block|}
block|,
block|{
name|H
block|}
block|,
block|{
name|T
block|,
name|O
block|}
block|,
block|{
name|O
block|,
name|I
block|}
block|,
block|{
name|T
block|,
name|A
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|Y
block|}
block|,
block|{
name|T
block|,
name|H
block|,
name|S
block|}
block|,
block|{
name|T
block|,
name|W
block|,
name|N
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|N
block|}
block|,
block|{
name|T
block|,
name|H
block|,
name|N
block|}
block|,
block|{
name|K
block|,
name|A
block|,
name|I
block|}
block|,
block|{
name|K
block|,
name|I
block|}
block|,
block|{
name|K
block|}
block|,
block|{
name|E
block|,
name|I
block|,
name|M
block|,
name|A
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|I
block|,
name|S
block|,
name|A
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|I
block|,
name|N
block|,
name|A
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|I
block|,
name|M
block|,
name|A
block|,
name|S
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|E
block|,
name|I
block|,
name|S
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|S
block|,
name|T
block|,
name|O
block|}
block|,
block|{
name|S
block|,
name|T
block|,
name|O
block|,
name|N
block|}
block|,
block|{
name|S
block|,
name|T
block|,
name|H
block|}
block|,
block|{
name|S
block|,
name|T
block|,
name|H
block|,
name|N
block|}
block|,
block|{
name|M
block|,
name|A
block|}
block|,
block|{
name|A
block|,
name|L
block|,
name|L
block|,
name|A
block|}
block|,
block|{
name|A
block|,
name|P
block|,
name|O
block|}
block|,
block|{
name|G
block|,
name|I
block|,
name|A
block|}
block|,
block|{
name|P
block|,
name|R
block|,
name|O
block|,
name|S
block|}
block|,
block|{
name|M
block|,
name|E
block|}
block|,
block|{
name|S
block|,
name|E
block|}
block|,
block|{
name|W
block|,
name|S
block|}
block|,
block|{
name|P
block|,
name|A
block|,
name|R
block|,
name|A
block|}
block|,
block|{
name|A
block|,
name|N
block|,
name|T
block|,
name|I
block|}
block|,
block|{
name|K
block|,
name|A
block|,
name|T
block|,
name|A
block|}
block|,
block|{
name|M
block|,
name|E
block|,
name|T
block|,
name|A
block|}
block|,
block|{
name|TH
block|,
name|A
block|}
block|,
block|{
name|N
block|,
name|A
block|}
block|,
block|{
name|D
block|,
name|E
block|}
block|,
block|{
name|D
block|,
name|E
block|,
name|N
block|}
block|,
block|{
name|M
block|,
name|H
block|}
block|,
block|{
name|M
block|,
name|H
block|,
name|N
block|}
block|,
block|{
name|E
block|,
name|P
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|N
block|,
name|W
block|}
block|,
block|{
name|E
block|,
name|A
block|,
name|N
block|}
block|,
block|{
name|A
block|,
name|N
block|}
block|,
block|{
name|T
block|,
name|O
block|,
name|T
block|,
name|E
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|Y
block|}
block|,
block|{
name|P
block|,
name|W
block|,
name|S
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|O
block|,
name|S
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|A
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|O
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|O
block|,
name|I
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|E
block|,
name|S
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|W
block|,
name|N
block|}
block|,
block|{
name|P
block|,
name|O
block|,
name|I
block|,
name|O
block|,
name|Y
block|,
name|S
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|O
block|,
name|S
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|H
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|O
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|O
block|,
name|I
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|W
block|,
name|N
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|O
block|,
name|Y
block|,
name|S
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|E
block|,
name|S
block|}
block|,
block|{
name|A
block|,
name|Y
block|,
name|T
block|,
name|A
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|O
block|,
name|S
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|H
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|O
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|O
block|,
name|I
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|E
block|,
name|S
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|A
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|W
block|,
name|N
block|}
block|,
block|{
name|E
block|,
name|K
block|,
name|E
block|,
name|I
block|,
name|N
block|,
name|O
block|,
name|Y
block|,
name|S
block|}
block|,
block|{
name|O
block|,
name|P
block|,
name|W
block|,
name|S
block|}
block|,
block|{
name|O
block|,
name|M
block|,
name|W
block|,
name|S
block|}
block|,
block|{
name|I
block|,
name|S
block|,
name|W
block|,
name|S
block|}
block|,
block|{
name|O
block|,
name|S
block|,
name|O
block|}
block|,
block|{
name|O
block|,
name|T
block|,
name|I
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
comment|/**      * Charset for Greek letters.      * Represents encoding for 24 lowercase Greek letters.      * Predefined charsets can be taken from GreekCharSets class      */
DECL|field|charset
specifier|private
name|char
index|[]
name|charset
decl_stmt|;
DECL|method|GreekAnalyzer
specifier|public
name|GreekAnalyzer
parameter_list|()
block|{
name|charset
operator|=
name|GreekCharsets
operator|.
name|UnicodeGreek
expr_stmt|;
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|makeStopWords
argument_list|(
name|GreekCharsets
operator|.
name|UnicodeGreek
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer.      */
DECL|method|GreekAnalyzer
specifier|public
name|GreekAnalyzer
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
DECL|method|GreekAnalyzer
specifier|public
name|GreekAnalyzer
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
comment|// Takes greek stop words and translates them to a String array, using
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
name|GREEK_STOP_WORDS
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
name|GREEK_STOP_WORDS
index|[
name|i
index|]
decl_stmt|;
comment|// translate the word,using the charset
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
comment|/**      * Builds an analyzer with the given stop words.      */
DECL|method|GreekAnalyzer
specifier|public
name|GreekAnalyzer
parameter_list|(
name|char
index|[]
name|charset
parameter_list|,
name|Map
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
comment|/**      * Creates a TokenStream which tokenizes all the text in the provided Reader.      *      * @return  A TokenStream build from a StandardTokenizer filtered with      *                  GreekLowerCaseFilter and StopFilter      */
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
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|GreekLowerCaseFilter
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
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

