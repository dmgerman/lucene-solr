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
name|IOException
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
name|List
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|queryParser
operator|.
name|QueryParser
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
comment|/**  * Removes stop words from a token stream.  *   *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating StopFilter:  *<ul>  *<li> As of 3.1, StopFilter correctly handles Unicode 4.0  *         supplementary characters in stopwords and position  *         increments are preserved  *</ul>  */
end_comment

begin_class
DECL|class|StopFilter
specifier|public
specifier|final
class|class
name|StopFilter
extends|extends
name|TokenFilter
block|{
DECL|field|stopWords
specifier|private
specifier|final
name|CharArraySet
name|stopWords
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
init|=
literal|true
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Construct a token stream filtering the given input. If    *<code>stopWords</code> is an instance of {@link CharArraySet} (true if    *<code>makeStopSet()</code> was used to construct the set) it will be    * directly used and<code>ignoreCase</code> will be ignored since    *<code>CharArraySet</code> directly controls case sensitivity.    *<p/>    * If<code>stopWords</code> is not an instance of {@link CharArraySet}, a new    * CharArraySet will be constructed and<code>ignoreCase</code> will be used    * to specify the case sensitivity of that set.    *     * @param matchVersion    *          Lucene version to enable correct Unicode 4.0 behavior in the stop    *          set if Version> 3.0. See<a href="#version">above</a> for details.    * @param input    *          Input TokenStream    * @param stopWords    *          A Set of Strings or char[] or any other toString()-able set    *          representing the stopwords    * @param ignoreCase    *          if true, all words are lower cased first    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|stopWords
operator|=
name|stopWords
operator|instanceof
name|CharArraySet
condition|?
operator|(
name|CharArraySet
operator|)
name|stopWords
else|:
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a filter which removes words from the input TokenStream that are    * named in the Set.    *     * @param matchVersion    *          Lucene version to enable correct Unicode 4.0 behavior in the stop    *          set if Version> 3.0.  See<a href="#version">above</a> for details.    * @param in    *          Input stream    * @param stopWords    *          A Set of Strings or char[] or any other toString()-able set    *          representing the stopwords    * @see #makeStopSet(Version, java.lang.String...)    */
DECL|method|StopFilter
specifier|public
name|StopFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|in
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|in
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds a Set from an array of stop words,    * appropriate for passing into the StopFilter constructor.    * This permits this stopWords construction to be cached once when    * an Analyzer is constructed.    *     * @param matchVersion Lucene version to enable correct Unicode 4.0 behavior in the returned set if Version> 3.0    * @param stopWords An array of stopwords    * @see #makeStopSet(Version, java.lang.String[], boolean) passing false to ignoreCase    */
DECL|method|makeStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|Object
argument_list|>
name|makeStopSet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopWords
parameter_list|)
block|{
return|return
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Builds a Set from an array of stop words,    * appropriate for passing into the StopFilter constructor.    * This permits this stopWords construction to be cached once when    * an Analyzer is constructed.    *     * @param matchVersion Lucene version to enable correct Unicode 4.0 behavior in the returned set if Version> 3.0    * @param stopWords A List of Strings or char[] or any other toString()-able list representing the stopwords    * @return A Set ({@link CharArraySet}) containing the words    * @see #makeStopSet(Version, java.lang.String[], boolean) passing false to ignoreCase    */
DECL|method|makeStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|Object
argument_list|>
name|makeStopSet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|List
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|)
block|{
return|return
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Creates a stopword set from the given stopword array.    *     * @param matchVersion Lucene version to enable correct Unicode 4.0 behavior in the returned set if Version> 3.0    * @param stopWords An array of stopwords    * @param ignoreCase If true, all words are lower cased first.      * @return a Set containing the words    */
DECL|method|makeStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|Object
argument_list|>
name|makeStopSet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
index|[]
name|stopWords
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|CharArraySet
name|stopSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
operator|.
name|length
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|stopSet
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|stopWords
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stopSet
return|;
block|}
comment|/**    * Creates a stopword set from the given stopword list.    * @param matchVersion Lucene version to enable correct Unicode 4.0 behavior in the returned set if Version> 3.0    * @param stopWords A List of Strings or char[] or any other toString()-able list representing the stopwords    * @param ignoreCase if true, all words are lower cased first    * @return A Set ({@link CharArraySet}) containing the words    */
DECL|method|makeStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|Object
argument_list|>
name|makeStopSet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|List
argument_list|<
name|?
argument_list|>
name|stopWords
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|CharArraySet
name|stopSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
decl_stmt|;
name|stopSet
operator|.
name|addAll
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
return|return
name|stopSet
return|;
block|}
comment|/**    * Returns the next input Token whose term() is not a stop word.    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
comment|// return the first non-stop word found
name|int
name|skippedPositions
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|stopWords
operator|.
name|contains
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|+
name|skippedPositions
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
name|skippedPositions
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
comment|// reached EOS -- return false
return|return
literal|false
return|;
block|}
comment|/**    * @see #setEnablePositionIncrements(boolean)    */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
comment|/**    * If<code>true</code>, this StopFilter will preserve    * positions of the incoming tokens (ie, accumulate and    * set position increments of the removed stop tokens).    * Generally,<code>true</code> is best as it does not    * lose information (positions of the original tokens)    * during indexing.    *    * Default is true.    *     *<p> When set, when a token is stopped    * (omitted), the position increment of the following    * token is incremented.    *    *<p><b>NOTE</b>: be sure to also    * set {@link QueryParser#setEnablePositionIncrements} if    * you use QueryParser to create queries.    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enable
expr_stmt|;
block|}
block|}
end_class

end_unit

