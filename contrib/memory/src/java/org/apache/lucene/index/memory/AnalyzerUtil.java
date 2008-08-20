begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
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
name|PrintStream
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
name|Arrays
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|regex
operator|.
name|Pattern
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
name|PorterStemFilter
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
name|Token
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

begin_comment
comment|/**  * Various fulltext analysis utilities avoiding redundant code in several  * classes.  *   * @author whoschek.AT.lbl.DOT.gov  */
end_comment

begin_class
DECL|class|AnalyzerUtil
specifier|public
class|class
name|AnalyzerUtil
block|{
DECL|method|AnalyzerUtil
specifier|private
name|AnalyzerUtil
parameter_list|()
block|{}
empty_stmt|;
comment|/**    * Returns a simple analyzer wrapper that logs all tokens produced by the    * underlying child analyzer to the given log stream (typically System.err);    * Otherwise behaves exactly like the child analyzer, delivering the very    * same tokens; useful for debugging purposes on custom indexing and/or    * querying.    *     * @param child    *            the underlying child analyzer    * @param log    *            the print stream to log to (typically System.err)    * @param logName    *            a name for this logger (typically "log" or similar)    * @return a logging analyzer    */
DECL|method|getLoggingAnalyzer
specifier|public
specifier|static
name|Analyzer
name|getLoggingAnalyzer
parameter_list|(
specifier|final
name|Analyzer
name|child
parameter_list|,
specifier|final
name|PrintStream
name|log
parameter_list|,
specifier|final
name|String
name|logName
parameter_list|)
block|{
if|if
condition|(
name|child
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"child analyzer must not be null"
argument_list|)
throw|;
if|if
condition|(
name|log
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"logStream must not be null"
argument_list|)
throw|;
return|return
operator|new
name|Analyzer
argument_list|()
block|{
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TokenFilter
argument_list|(
name|child
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|)
block|{
specifier|private
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
comment|// from filter super class
name|log
operator|.
name|println
argument_list|(
name|toString
argument_list|(
name|nextToken
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|nextToken
return|;
block|}
specifier|private
name|String
name|toString
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
return|return
literal|"["
operator|+
name|logName
operator|+
literal|":EOS:"
operator|+
name|fieldName
operator|+
literal|"]\n"
return|;
name|position
operator|+=
name|token
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
return|return
literal|"["
operator|+
name|logName
operator|+
literal|":"
operator|+
name|position
operator|+
literal|":"
operator|+
name|fieldName
operator|+
literal|":"
operator|+
name|token
operator|.
name|term
argument_list|()
operator|+
literal|":"
operator|+
name|token
operator|.
name|startOffset
argument_list|()
operator|+
literal|"-"
operator|+
name|token
operator|.
name|endOffset
argument_list|()
operator|+
literal|":"
operator|+
name|token
operator|.
name|type
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an analyzer wrapper that returns at most the first    *<code>maxTokens</code> tokens from the underlying child analyzer,    * ignoring all remaining tokens.    *     * @param child    *            the underlying child analyzer    * @param maxTokens    *            the maximum number of tokens to return from the underlying    *            analyzer (a value of Integer.MAX_VALUE indicates unlimited)    * @return an analyzer wrapper    */
DECL|method|getMaxTokenAnalyzer
specifier|public
specifier|static
name|Analyzer
name|getMaxTokenAnalyzer
parameter_list|(
specifier|final
name|Analyzer
name|child
parameter_list|,
specifier|final
name|int
name|maxTokens
parameter_list|)
block|{
if|if
condition|(
name|child
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"child analyzer must not be null"
argument_list|)
throw|;
if|if
condition|(
name|maxTokens
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTokens must not be negative"
argument_list|)
throw|;
if|if
condition|(
name|maxTokens
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
return|return
name|child
return|;
comment|// no need to wrap
return|return
operator|new
name|Analyzer
argument_list|()
block|{
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
return|return
operator|new
name|TokenFilter
argument_list|(
name|child
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|)
block|{
specifier|private
name|int
name|todo
init|=
name|maxTokens
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
return|return
operator|--
name|todo
operator|>=
literal|0
condition|?
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
else|:
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an English stemming analyzer that stems tokens from the    * underlying child analyzer according to the Porter stemming algorithm. The    * child analyzer must deliver tokens in lower case for the stemmer to work    * properly.    *<p>    * Background: Stemming reduces token terms to their linguistic root form    * e.g. reduces "fishing" and "fishes" to "fish", "family" and "families" to    * "famili", as well as "complete" and "completion" to "complet". Note that    * the root form is not necessarily a meaningful word in itself, and that    * this is not a bug but rather a feature, if you lean back and think about    * fuzzy word matching for a bit.    *<p>    * See the Lucene contrib packages for stemmers (and stop words) for German,    * Russian and many more languages.    *     * @param child    *            the underlying child analyzer    * @return an analyzer wrapper    */
DECL|method|getPorterStemmerAnalyzer
specifier|public
specifier|static
name|Analyzer
name|getPorterStemmerAnalyzer
parameter_list|(
specifier|final
name|Analyzer
name|child
parameter_list|)
block|{
if|if
condition|(
name|child
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"child analyzer must not be null"
argument_list|)
throw|;
return|return
operator|new
name|Analyzer
argument_list|()
block|{
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
return|return
operator|new
name|PorterStemFilter
argument_list|(
name|child
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|)
return|;
comment|//        /* PorterStemFilter and SnowballFilter have the same behaviour,
comment|//        but PorterStemFilter is much faster. */
comment|//        return new org.apache.lucene.analysis.snowball.SnowballFilter(
comment|//            child.tokenStream(fieldName, reader), "English");
block|}
block|}
return|;
block|}
comment|/**    * Returns an analyzer wrapper that wraps the underlying child analyzer's    * token stream into a {@link SynonymTokenFilter}.    *     * @param child    *            the underlying child analyzer    * @param synonyms    *            the map used to extract synonyms for terms    * @param maxSynonyms    *            the maximum number of synonym tokens to return per underlying    *            token word (a value of Integer.MAX_VALUE indicates unlimited)    * @return a new analyzer    */
DECL|method|getSynonymAnalyzer
specifier|public
specifier|static
name|Analyzer
name|getSynonymAnalyzer
parameter_list|(
specifier|final
name|Analyzer
name|child
parameter_list|,
specifier|final
name|SynonymMap
name|synonyms
parameter_list|,
specifier|final
name|int
name|maxSynonyms
parameter_list|)
block|{
if|if
condition|(
name|child
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"child analyzer must not be null"
argument_list|)
throw|;
if|if
condition|(
name|synonyms
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"synonyms must not be null"
argument_list|)
throw|;
if|if
condition|(
name|maxSynonyms
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxSynonyms must not be negative"
argument_list|)
throw|;
if|if
condition|(
name|maxSynonyms
operator|==
literal|0
condition|)
return|return
name|child
return|;
comment|// no need to wrap
return|return
operator|new
name|Analyzer
argument_list|()
block|{
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
return|return
operator|new
name|SynonymTokenFilter
argument_list|(
name|child
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|,
name|synonyms
argument_list|,
name|maxSynonyms
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an analyzer wrapper that caches all tokens generated by the underlying child analyzer's    * token streams, and delivers those cached tokens on subsequent calls to     *<code>tokenStream(String fieldName, Reader reader)</code>     * if the fieldName has been seen before, altogether ignoring the Reader parameter on cache lookup.    *<p>    * If Analyzer / TokenFilter chains are expensive in terms of I/O or CPU, such caching can     * help improve performance if the same document is added to multiple Lucene indexes,     * because the text analysis phase need not be performed more than once.    *<p>    * Caveats:     *<ul>    *<li>Caching the tokens of large Lucene documents can lead to out of memory exceptions.</li>     *<li>The Token instances delivered by the underlying child analyzer must be immutable.</li>    *<li>The same caching analyzer instance must not be used for more than one document    * because the cache is not keyed on the Reader parameter.</li>    *</ul>    *     * @param child    *            the underlying child analyzer    * @return a new analyzer    */
DECL|method|getTokenCachingAnalyzer
specifier|public
specifier|static
name|Analyzer
name|getTokenCachingAnalyzer
parameter_list|(
specifier|final
name|Analyzer
name|child
parameter_list|)
block|{
if|if
condition|(
name|child
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"child analyzer must not be null"
argument_list|)
throw|;
return|return
operator|new
name|Analyzer
argument_list|()
block|{
specifier|private
specifier|final
name|HashMap
name|cache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
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
specifier|final
name|ArrayList
name|tokens
init|=
operator|(
name|ArrayList
operator|)
name|cache
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|==
literal|null
condition|)
block|{
comment|// not yet cached
specifier|final
name|ArrayList
name|tokens2
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
operator|new
name|TokenFilter
argument_list|(
name|child
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|)
block|{
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
comment|// from filter super class
if|if
condition|(
name|nextToken
operator|!=
literal|null
condition|)
name|tokens2
operator|.
name|add
argument_list|(
name|nextToken
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nextToken
return|;
block|}
block|}
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|tokens2
argument_list|)
expr_stmt|;
return|return
name|tokenStream
return|;
block|}
else|else
block|{
comment|// already cached
return|return
operator|new
name|TokenStream
argument_list|()
block|{
specifier|private
name|Iterator
name|iter
init|=
name|tokens
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
assert|assert
name|token
operator|!=
literal|null
assert|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|Token
operator|)
name|iter
operator|.
name|next
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Returns (frequency:term) pairs for the top N distinct terms (aka words),    * sorted descending by frequency (and ascending by term, if tied).    *<p>    * Example XQuery:    *<pre>    * declare namespace util = "java:org.apache.lucene.index.memory.AnalyzerUtil";    * declare namespace analyzer = "java:org.apache.lucene.index.memory.PatternAnalyzer";    *     * for $pair in util:get-most-frequent-terms(    *    analyzer:EXTENDED_ANALYZER(), doc("samples/shakespeare/othello.xml"), 10)    * return&lt;word word="{substring-after($pair, ':')}" frequency="{substring-before($pair, ':')}"/>    *</pre>    *     * @param analyzer    *            the analyzer to use for splitting text into terms (aka words)    * @param text    *            the text to analyze    * @param limit    *            the maximum number of pairs to return; zero indicates     *            "as many as possible".    * @return an array of (frequency:term) pairs in the form of (freq0:term0,    *         freq1:term1, ..., freqN:termN). Each pair is a single string    *         separated by a ':' delimiter.    */
DECL|method|getMostFrequentTerms
specifier|public
specifier|static
name|String
index|[]
name|getMostFrequentTerms
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"analyzer must not be null"
argument_list|)
throw|;
if|if
condition|(
name|text
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"text must not be null"
argument_list|)
throw|;
if|if
condition|(
name|limit
operator|<=
literal|0
condition|)
name|limit
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// compute frequencies of distinct terms
name|HashMap
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
for|for
control|(
name|Token
name|nextToken
init|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|MutableInteger
name|freq
init|=
operator|(
name|MutableInteger
operator|)
name|map
operator|.
name|get
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|freq
operator|==
literal|null
condition|)
block|{
name|freq
operator|=
operator|new
name|MutableInteger
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|.
name|setValue
argument_list|(
name|freq
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e2
argument_list|)
throw|;
block|}
block|}
comment|// sort by frequency, text
name|Map
operator|.
name|Entry
index|[]
name|entries
init|=
operator|new
name|Map
operator|.
name|Entry
index|[
name|map
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|toArray
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|Map
operator|.
name|Entry
name|e1
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o1
decl_stmt|;
name|Map
operator|.
name|Entry
name|e2
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o2
decl_stmt|;
name|int
name|f1
init|=
operator|(
operator|(
name|MutableInteger
operator|)
name|e1
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|int
name|f2
init|=
operator|(
operator|(
name|MutableInteger
operator|)
name|e2
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|f2
operator|-
name|f1
operator|!=
literal|0
condition|)
return|return
name|f2
operator|-
name|f1
return|;
name|String
name|s1
init|=
operator|(
name|String
operator|)
name|e1
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
operator|(
name|String
operator|)
name|e2
operator|.
name|getKey
argument_list|()
decl_stmt|;
return|return
name|s1
operator|.
name|compareTo
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// return top N entries
name|int
name|size
init|=
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|entries
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
index|[]
name|pairs
init|=
operator|new
name|String
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|pairs
index|[
name|i
index|]
operator|=
name|entries
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
operator|+
literal|":"
operator|+
name|entries
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
return|return
name|pairs
return|;
block|}
DECL|class|MutableInteger
specifier|private
specifier|static
specifier|final
class|class
name|MutableInteger
block|{
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|method|MutableInteger
specifier|public
name|MutableInteger
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|intValue
specifier|public
name|int
name|intValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
empty_stmt|;
comment|// TODO: could use a more general i18n approach ala http://icu.sourceforge.net/docs/papers/text_boundary_analysis_in_java/
comment|/** (Line terminator followed by zero or more whitespace) two or more times */
DECL|field|PARAGRAPHS
specifier|private
specifier|static
specifier|final
name|Pattern
name|PARAGRAPHS
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([\\r\\n\\u0085\\u2028\\u2029][ \\t\\x0B\\f]*){2,}"
argument_list|)
decl_stmt|;
comment|/**    * Returns at most the first N paragraphs of the given text. Delimiting    * characters are excluded from the results. Each returned paragraph is    * whitespace-trimmed via String.trim(), potentially an empty string.    *     * @param text    *            the text to tokenize into paragraphs    * @param limit    *            the maximum number of paragraphs to return; zero indicates "as    *            many as possible".    * @return the first N paragraphs    */
DECL|method|getParagraphs
specifier|public
specifier|static
name|String
index|[]
name|getParagraphs
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
return|return
name|tokenize
argument_list|(
name|PARAGRAPHS
argument_list|,
name|text
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|tokenize
specifier|private
specifier|static
name|String
index|[]
name|tokenize
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|String
index|[]
name|tokens
init|=
name|pattern
operator|.
name|split
argument_list|(
name|text
argument_list|,
name|limit
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|tokens
operator|.
name|length
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
name|tokens
index|[
name|i
index|]
operator|=
name|tokens
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
return|return
name|tokens
return|;
block|}
comment|// TODO: don't split on floating point numbers, e.g. 3.1415 (digit before or after '.')
comment|/** Divides text into sentences; Includes inverted spanish exclamation and question mark */
DECL|field|SENTENCES
specifier|private
specifier|static
specifier|final
name|Pattern
name|SENTENCES
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[!\\.\\?\\xA1\\xBF]+"
argument_list|)
decl_stmt|;
comment|/**    * Returns at most the first N sentences of the given text. Delimiting    * characters are excluded from the results. Each returned sentence is    * whitespace-trimmed via String.trim(), potentially an empty string.    *     * @param text    *            the text to tokenize into sentences    * @param limit    *            the maximum number of sentences to return; zero indicates "as    *            many as possible".    * @return the first N sentences    */
DECL|method|getSentences
specifier|public
specifier|static
name|String
index|[]
name|getSentences
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
comment|//    return tokenize(SENTENCES, text, limit); // equivalent but slower
name|int
name|len
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
return|return
operator|new
name|String
index|[]
block|{
name|text
block|}
return|;
if|if
condition|(
name|limit
operator|<=
literal|0
condition|)
name|limit
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// average sentence length heuristic
name|String
index|[]
name|tokens
init|=
operator|new
name|String
index|[
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
literal|1
operator|+
name|len
operator|/
literal|40
argument_list|)
index|]
decl_stmt|;
name|int
name|size
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|size
operator|<
name|limit
condition|)
block|{
comment|// scan to end of current sentence
name|int
name|start
init|=
name|i
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|len
operator|&&
operator|!
name|isSentenceSeparator
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|size
operator|==
name|tokens
operator|.
name|length
condition|)
block|{
comment|// grow array
name|String
index|[]
name|tmp
init|=
operator|new
name|String
index|[
name|tokens
operator|.
name|length
operator|<<
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|tokens
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|tmp
expr_stmt|;
block|}
comment|// add sentence (potentially empty)
name|tokens
index|[
name|size
operator|++
index|]
operator|=
name|text
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|i
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// scan to beginning of next sentence
while|while
condition|(
name|i
operator|<
name|len
operator|&&
name|isSentenceSeparator
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|==
name|tokens
operator|.
name|length
condition|)
return|return
name|tokens
return|;
name|String
index|[]
name|results
init|=
operator|new
name|String
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|tokens
argument_list|,
literal|0
argument_list|,
name|results
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
DECL|method|isSentenceSeparator
specifier|private
specifier|static
name|boolean
name|isSentenceSeparator
parameter_list|(
name|char
name|c
parameter_list|)
block|{
comment|// regex [!\\.\\?\\xA1\\xBF]
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'!'
case|:
return|return
literal|true
return|;
case|case
literal|'.'
case|:
return|return
literal|true
return|;
case|case
literal|'?'
case|:
return|return
literal|true
return|;
case|case
literal|0xA1
case|:
return|return
literal|true
return|;
comment|// spanish inverted exclamation mark
case|case
literal|0xBF
case|:
return|return
literal|true
return|;
comment|// spanish inverted question mark
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

