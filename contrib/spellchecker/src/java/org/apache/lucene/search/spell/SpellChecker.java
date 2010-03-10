begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|Iterator
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|LogMergePolicy
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|TermQuery
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
name|store
operator|.
name|AlreadyClosedException
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
name|store
operator|.
name|Directory
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
comment|/**  *<p>  *   Spell Checker class  (Main class)<br/>  *  (initially inspired by the David Spencer code).  *</p>  *  *<p>Example Usage:  *   *<pre>  *  SpellChecker spellchecker = new SpellChecker(spellIndexDirectory);  *  // To index a field of a user index:  *  spellchecker.indexDictionary(new LuceneDictionary(my_lucene_reader, a_field));  *  // To index a file containing words:  *  spellchecker.indexDictionary(new PlainTextDictionary(new File("myfile.txt")));  *  String[] suggestions = spellchecker.suggestSimilar("misspelt", 5);  *</pre>  *   *  * @version 1.0  */
end_comment

begin_class
DECL|class|SpellChecker
specifier|public
class|class
name|SpellChecker
implements|implements
name|java
operator|.
name|io
operator|.
name|Closeable
block|{
comment|/**    * Field name for each word in the ngram index.    */
DECL|field|F_WORD
specifier|public
specifier|static
specifier|final
name|String
name|F_WORD
init|=
literal|"word"
decl_stmt|;
DECL|field|F_WORD_TERM
specifier|private
specifier|static
specifier|final
name|Term
name|F_WORD_TERM
init|=
operator|new
name|Term
argument_list|(
name|F_WORD
argument_list|)
decl_stmt|;
comment|/**    * the spell index    */
comment|// don't modify the directory directly - see #swapSearcher()
comment|// TODO: why is this package private?
DECL|field|spellIndex
name|Directory
name|spellIndex
decl_stmt|;
comment|/**    * Boost value for start and end grams    */
DECL|field|bStart
specifier|private
name|float
name|bStart
init|=
literal|2.0f
decl_stmt|;
DECL|field|bEnd
specifier|private
name|float
name|bEnd
init|=
literal|1.0f
decl_stmt|;
comment|// don't use this searcher directly - see #swapSearcher()
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/*    * this locks all modifications to the current searcher.     */
DECL|field|searcherLock
specifier|private
specifier|final
name|Object
name|searcherLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/*    * this lock synchronizes all possible modifications to the     * current index directory. It should not be possible to try modifying    * the same index concurrently. Note: Do not acquire the searcher lock    * before acquiring this lock!     */
DECL|field|modifyCurrentIndexLock
specifier|private
specifier|final
name|Object
name|modifyCurrentIndexLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
comment|// minimum score for hits generated by the spell checker query
DECL|field|minScore
specifier|private
name|float
name|minScore
init|=
literal|0.5f
decl_stmt|;
DECL|field|sd
specifier|private
name|StringDistance
name|sd
decl_stmt|;
comment|/**    * Use the given directory as a spell checker index. The directory    * is created if it doesn't exist yet.    * @param spellIndex the spell index directory    * @param sd the {@link StringDistance} measurement to use     * @throws IOException if Spellchecker can not open the directory    */
DECL|method|SpellChecker
specifier|public
name|SpellChecker
parameter_list|(
name|Directory
name|spellIndex
parameter_list|,
name|StringDistance
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|setSpellIndex
argument_list|(
name|spellIndex
argument_list|)
expr_stmt|;
name|setStringDistance
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Use the given directory as a spell checker index with a    * {@link LevensteinDistance} as the default {@link StringDistance}. The    * directory is created if it doesn't exist yet.    *     * @param spellIndex    *          the spell index directory    * @throws IOException    *           if spellchecker can not open the directory    */
DECL|method|SpellChecker
specifier|public
name|SpellChecker
parameter_list|(
name|Directory
name|spellIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|spellIndex
argument_list|,
operator|new
name|LevensteinDistance
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Use a different index as the spell checker index or re-open    * the existing index if<code>spellIndex</code> is the same value    * as given in the constructor.    * @param spellIndexDir the spell directory to use    * @throws AlreadyClosedException if the Spellchecker is already closed    * @throws  IOException if spellchecker can not open the directory    */
comment|// TODO: we should make this final as it is called in the constructor
DECL|method|setSpellIndex
specifier|public
name|void
name|setSpellIndex
parameter_list|(
name|Directory
name|spellIndexDir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// this could be the same directory as the current spellIndex
comment|// modifications to the directory should be synchronized
synchronized|synchronized
init|(
name|modifyCurrentIndexLock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|IndexReader
operator|.
name|indexExists
argument_list|(
name|spellIndexDir
argument_list|)
condition|)
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|spellIndexDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|swapSearcher
argument_list|(
name|spellIndexDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Sets the {@link StringDistance} implementation for this    * {@link SpellChecker} instance.    *     * @param sd the {@link StringDistance} implementation for this    * {@link SpellChecker} instance    */
DECL|method|setStringDistance
specifier|public
name|void
name|setStringDistance
parameter_list|(
name|StringDistance
name|sd
parameter_list|)
block|{
name|this
operator|.
name|sd
operator|=
name|sd
expr_stmt|;
block|}
comment|/**    * Returns the {@link StringDistance} instance used by this    * {@link SpellChecker} instance.    *     * @return the {@link StringDistance} instance used by this    *         {@link SpellChecker} instance.    */
DECL|method|getStringDistance
specifier|public
name|StringDistance
name|getStringDistance
parameter_list|()
block|{
return|return
name|sd
return|;
block|}
comment|/**    * Sets the accuracy 0&lt; minScore&lt; 1; default 0.5    */
DECL|method|setAccuracy
specifier|public
name|void
name|setAccuracy
parameter_list|(
name|float
name|minScore
parameter_list|)
block|{
name|this
operator|.
name|minScore
operator|=
name|minScore
expr_stmt|;
block|}
comment|/**    * Suggest similar words.    *     *<p>As the Lucene similarity that is used to fetch the most relevant n-grammed terms    * is not the same as the edit distance strategy used to calculate the best    * matching spell-checked word from the hits that Lucene found, one usually has    * to retrieve a couple of numSug's in order to get the true best match.    *    *<p>I.e. if numSug == 1, don't count on that suggestion being the best one.    * Thus, you should set this value to<b>at least</b> 5 for a good suggestion.    *    * @param word the word you want a spell check done on    * @param numSug the number of suggested words    * @throws IOException if the underlying index throws an {@link IOException}    * @throws AlreadyClosedException if the Spellchecker is already closed    * @return String[]    */
DECL|method|suggestSimilar
specifier|public
name|String
index|[]
name|suggestSimilar
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|numSug
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|suggestSimilar
argument_list|(
name|word
argument_list|,
name|numSug
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Suggest similar words (optionally restricted to a field of an index).    *     *<p>As the Lucene similarity that is used to fetch the most relevant n-grammed terms    * is not the same as the edit distance strategy used to calculate the best    * matching spell-checked word from the hits that Lucene found, one usually has    * to retrieve a couple of numSug's in order to get the true best match.    *    *<p>I.e. if numSug == 1, don't count on that suggestion being the best one.    * Thus, you should set this value to<b>at least</b> 5 for a good suggestion.    *    * @param word the word you want a spell check done on    * @param numSug the number of suggested words    * @param ir the indexReader of the user index (can be null see field param)    * @param field the field of the user index: if field is not null, the suggested    * words are restricted to the words present in this field.    * @param morePopular return only the suggest words that are as frequent or more frequent than the searched word    * (only if restricted mode = (indexReader!=null and field!=null)    * @throws IOException if the underlying index throws an {@link IOException}    * @throws AlreadyClosedException if the Spellchecker is already closed    * @return String[] the sorted list of the suggest words with these 2 criteria:    * first criteria: the edit distance, second criteria (only if restricted mode): the popularity    * of the suggest words in the field of the user index    */
DECL|method|suggestSimilar
specifier|public
name|String
index|[]
name|suggestSimilar
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|numSug
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|String
name|field
parameter_list|,
name|boolean
name|morePopular
parameter_list|)
throws|throws
name|IOException
block|{
comment|// obtainSearcher calls ensureOpen
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|obtainSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|float
name|min
init|=
name|this
operator|.
name|minScore
decl_stmt|;
specifier|final
name|int
name|lengthWord
init|=
name|word
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|freq
init|=
operator|(
name|ir
operator|!=
literal|null
operator|&&
name|field
operator|!=
literal|null
operator|)
condition|?
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|word
argument_list|)
argument_list|)
else|:
literal|0
decl_stmt|;
specifier|final
name|int
name|goalFreq
init|=
operator|(
name|morePopular
operator|&&
name|ir
operator|!=
literal|null
operator|&&
name|field
operator|!=
literal|null
operator|)
condition|?
name|freq
else|:
literal|0
decl_stmt|;
comment|// if the word exists in the real index and we don't care for word frequency, return the word itself
if|if
condition|(
operator|!
name|morePopular
operator|&&
name|freq
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
name|word
block|}
return|;
block|}
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|String
index|[]
name|grams
decl_stmt|;
name|String
name|key
decl_stmt|;
for|for
control|(
name|int
name|ng
init|=
name|getMin
argument_list|(
name|lengthWord
argument_list|)
init|;
name|ng
operator|<=
name|getMax
argument_list|(
name|lengthWord
argument_list|)
condition|;
name|ng
operator|++
control|)
block|{
name|key
operator|=
literal|"gram"
operator|+
name|ng
expr_stmt|;
comment|// form key
name|grams
operator|=
name|formGrams
argument_list|(
name|word
argument_list|,
name|ng
argument_list|)
expr_stmt|;
comment|// form word into ngrams (allow dups too)
if|if
condition|(
name|grams
operator|.
name|length
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// hmm
block|}
if|if
condition|(
name|bStart
operator|>
literal|0
condition|)
block|{
comment|// should we boost prefixes?
name|add
argument_list|(
name|query
argument_list|,
literal|"start"
operator|+
name|ng
argument_list|,
name|grams
index|[
literal|0
index|]
argument_list|,
name|bStart
argument_list|)
expr_stmt|;
comment|// matches start of word
block|}
if|if
condition|(
name|bEnd
operator|>
literal|0
condition|)
block|{
comment|// should we boost suffixes
name|add
argument_list|(
name|query
argument_list|,
literal|"end"
operator|+
name|ng
argument_list|,
name|grams
index|[
name|grams
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|bEnd
argument_list|)
expr_stmt|;
comment|// matches end of word
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|grams
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|query
argument_list|,
name|key
argument_list|,
name|grams
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|maxHits
init|=
literal|10
operator|*
name|numSug
decl_stmt|;
comment|//    System.out.println("Q: " + query);
name|ScoreDoc
index|[]
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|maxHits
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
comment|//    System.out.println("HITS: " + hits.length());
name|SuggestWordQueue
name|sugQueue
init|=
operator|new
name|SuggestWordQueue
argument_list|(
name|numSug
argument_list|)
decl_stmt|;
comment|// go thru more than 'maxr' matches in case the distance filter triggers
name|int
name|stop
init|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|,
name|maxHits
argument_list|)
decl_stmt|;
name|SuggestWord
name|sugWord
init|=
operator|new
name|SuggestWord
argument_list|()
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
name|stop
condition|;
name|i
operator|++
control|)
block|{
name|sugWord
operator|.
name|string
operator|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|F_WORD
argument_list|)
expr_stmt|;
comment|// get orig word
comment|// don't suggest a word for itself, that would be silly
if|if
condition|(
name|sugWord
operator|.
name|string
operator|.
name|equals
argument_list|(
name|word
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// edit distance
name|sugWord
operator|.
name|score
operator|=
name|sd
operator|.
name|getDistance
argument_list|(
name|word
argument_list|,
name|sugWord
operator|.
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|sugWord
operator|.
name|score
operator|<
name|min
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|ir
operator|!=
literal|null
operator|&&
name|field
operator|!=
literal|null
condition|)
block|{
comment|// use the user index
name|sugWord
operator|.
name|freq
operator|=
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|sugWord
operator|.
name|string
argument_list|)
argument_list|)
expr_stmt|;
comment|// freq in the index
comment|// don't suggest a word that is not present in the field
if|if
condition|(
operator|(
name|morePopular
operator|&&
name|goalFreq
operator|>
name|sugWord
operator|.
name|freq
operator|)
operator|||
name|sugWord
operator|.
name|freq
operator|<
literal|1
condition|)
block|{
continue|continue;
block|}
block|}
name|sugQueue
operator|.
name|insertWithOverflow
argument_list|(
name|sugWord
argument_list|)
expr_stmt|;
if|if
condition|(
name|sugQueue
operator|.
name|size
argument_list|()
operator|==
name|numSug
condition|)
block|{
comment|// if queue full, maintain the minScore score
name|min
operator|=
name|sugQueue
operator|.
name|top
argument_list|()
operator|.
name|score
expr_stmt|;
block|}
name|sugWord
operator|=
operator|new
name|SuggestWord
argument_list|()
expr_stmt|;
block|}
comment|// convert to array string
name|String
index|[]
name|list
init|=
operator|new
name|String
index|[
name|sugQueue
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|sugQueue
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
name|sugQueue
operator|.
name|pop
argument_list|()
operator|.
name|string
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
finally|finally
block|{
name|releaseSearcher
argument_list|(
name|indexSearcher
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add a clause to a boolean query.    */
DECL|method|add
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|BooleanQuery
name|q
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a clause to a boolean query.    */
DECL|method|add
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|BooleanQuery
name|q
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Form all ngrams for a given word.    * @param text the word to parse    * @param ng the ngram length e.g. 3    * @return an array of all ngrams in the word and note that duplicates are not removed    */
DECL|method|formGrams
specifier|private
specifier|static
name|String
index|[]
name|formGrams
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|ng
parameter_list|)
block|{
name|int
name|len
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
index|[]
name|res
init|=
operator|new
name|String
index|[
name|len
operator|-
name|ng
operator|+
literal|1
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
name|len
operator|-
name|ng
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|text
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|i
operator|+
name|ng
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**    * Removes all terms from the spell check index.    * @throws IOException    * @throws AlreadyClosedException if the Spellchecker is already closed    */
DECL|method|clearIndex
specifier|public
name|void
name|clearIndex
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|modifyCurrentIndexLock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|this
operator|.
name|spellIndex
decl_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|swapSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Check whether the word exists in the index.    * @param word    * @throws IOException    * @throws AlreadyClosedException if the Spellchecker is already closed    * @return true if the word exists in the index    */
DECL|method|exist
specifier|public
name|boolean
name|exist
parameter_list|(
name|String
name|word
parameter_list|)
throws|throws
name|IOException
block|{
comment|// obtainSearcher calls ensureOpen
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|obtainSearcher
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|indexSearcher
operator|.
name|docFreq
argument_list|(
name|F_WORD_TERM
operator|.
name|createTerm
argument_list|(
name|word
argument_list|)
argument_list|)
operator|>
literal|0
return|;
block|}
finally|finally
block|{
name|releaseSearcher
argument_list|(
name|indexSearcher
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Indexes the data from the given {@link Dictionary}.    * @param dict Dictionary to index    * @param mergeFactor mergeFactor to use when indexing    * @param ramMB the max amount or memory in MB to use    * @throws AlreadyClosedException if the Spellchecker is already closed    * @throws IOException    */
DECL|method|indexDictionary
specifier|public
name|void
name|indexDictionary
parameter_list|(
name|Dictionary
name|dict
parameter_list|,
name|int
name|mergeFactor
parameter_list|,
name|int
name|ramMB
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|modifyCurrentIndexLock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|Directory
name|dir
init|=
name|this
operator|.
name|spellIndex
decl_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramMB
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getMergePolicy
argument_list|()
operator|)
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|dict
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|word
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|word
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|3
condition|)
block|{
continue|continue;
comment|// too short we bail but "too long" is fine...
block|}
if|if
condition|(
name|this
operator|.
name|exist
argument_list|(
name|word
argument_list|)
condition|)
block|{
comment|// if the word already exist in the gramindex
continue|continue;
block|}
comment|// ok index the word
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|word
argument_list|,
name|getMin
argument_list|(
name|len
argument_list|)
argument_list|,
name|getMax
argument_list|(
name|len
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// close writer
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// also re-open the spell index to see our own changes when the next suggestion
comment|// is fetched:
name|swapSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Indexes the data from the given {@link Dictionary}.    * @param dict the dictionary to index    * @throws IOException    */
DECL|method|indexDictionary
specifier|public
name|void
name|indexDictionary
parameter_list|(
name|Dictionary
name|dict
parameter_list|)
throws|throws
name|IOException
block|{
name|indexDictionary
argument_list|(
name|dict
argument_list|,
literal|300
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|getMin
specifier|private
specifier|static
name|int
name|getMin
parameter_list|(
name|int
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|>
literal|5
condition|)
block|{
return|return
literal|3
return|;
block|}
if|if
condition|(
name|l
operator|==
literal|5
condition|)
block|{
return|return
literal|2
return|;
block|}
return|return
literal|1
return|;
block|}
DECL|method|getMax
specifier|private
specifier|static
name|int
name|getMax
parameter_list|(
name|int
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|>
literal|5
condition|)
block|{
return|return
literal|4
return|;
block|}
if|if
condition|(
name|l
operator|==
literal|5
condition|)
block|{
return|return
literal|3
return|;
block|}
return|return
literal|2
return|;
block|}
DECL|method|createDocument
specifier|private
specifier|static
name|Document
name|createDocument
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|ng1
parameter_list|,
name|int
name|ng2
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|F_WORD
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// orig term
name|addGram
argument_list|(
name|text
argument_list|,
name|doc
argument_list|,
name|ng1
argument_list|,
name|ng2
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|addGram
specifier|private
specifier|static
name|void
name|addGram
parameter_list|(
name|String
name|text
parameter_list|,
name|Document
name|doc
parameter_list|,
name|int
name|ng1
parameter_list|,
name|int
name|ng2
parameter_list|)
block|{
name|int
name|len
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ng
init|=
name|ng1
init|;
name|ng
operator|<=
name|ng2
condition|;
name|ng
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"gram"
operator|+
name|ng
decl_stmt|;
name|String
name|end
init|=
literal|null
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
name|len
operator|-
name|ng
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|String
name|gram
init|=
name|text
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|i
operator|+
name|ng
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|key
argument_list|,
name|gram
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"start"
operator|+
name|ng
argument_list|,
name|gram
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|end
operator|=
name|gram
expr_stmt|;
block|}
if|if
condition|(
name|end
operator|!=
literal|null
condition|)
block|{
comment|// may not be present if len==ng1
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"end"
operator|+
name|ng
argument_list|,
name|end
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|obtainSearcher
specifier|private
name|IndexSearcher
name|obtainSearcher
parameter_list|()
block|{
synchronized|synchronized
init|(
name|searcherLock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|searcher
return|;
block|}
block|}
DECL|method|releaseSearcher
specifier|private
name|void
name|releaseSearcher
parameter_list|(
specifier|final
name|IndexSearcher
name|aSearcher
parameter_list|)
throws|throws
name|IOException
block|{
comment|// don't check if open - always decRef
comment|// don't decrement the private searcher - could have been swapped
name|aSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Spellchecker has been closed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Close the IndexSearcher used by this SpellChecker    * @throws IOException if the close operation causes an {@link IOException}    * @throws AlreadyClosedException if the {@link SpellChecker} is already closed    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|searcherLock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|swapSearcher
specifier|private
name|void
name|swapSearcher
parameter_list|(
specifier|final
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*      * opening a searcher is possibly very expensive.      * We rather close it again if the Spellchecker was closed during      * this operation than block access to the current searcher while opening.      */
specifier|final
name|IndexSearcher
name|indexSearcher
init|=
name|createSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|searcherLock
init|)
block|{
if|if
condition|(
name|closed
condition|)
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Spellchecker has been closed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// set the spellindex in the sync block - ensure consistency.
name|searcher
operator|=
name|indexSearcher
expr_stmt|;
name|this
operator|.
name|spellIndex
operator|=
name|dir
expr_stmt|;
block|}
block|}
comment|/**    * Creates a new read-only IndexSearcher     * @param dir the directory used to open the searcher    * @return a new read-only IndexSearcher    * @throws IOException f there is a low-level IO error    */
comment|// for testing purposes
DECL|method|createSearcher
name|IndexSearcher
name|createSearcher
parameter_list|(
specifier|final
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Returns<code>true</code> if and only if the {@link SpellChecker} is    * closed, otherwise<code>false</code>.    *     * @return<code>true</code> if and only if the {@link SpellChecker} is    *         closed, otherwise<code>false</code>.    */
DECL|method|isClosed
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
block|}
end_class

end_unit

