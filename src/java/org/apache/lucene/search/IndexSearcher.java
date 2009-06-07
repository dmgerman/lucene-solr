begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|ArrayList
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
name|FieldSelector
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
name|CorruptIndexException
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/** Implements search over a single IndexReader.  *  *<p>Applications usually need only call the inherited {@link #search(Query)}  * or {@link #search(Query,Filter)} methods. For performance reasons it is   * recommended to open only one IndexSearcher and use it for all of your searches.  *   *<p>Note that you can only access Hits from an IndexSearcher as long as it is  * not yet closed, otherwise an IOException will be thrown.   */
end_comment

begin_class
DECL|class|IndexSearcher
specifier|public
class|class
name|IndexSearcher
extends|extends
name|Searcher
block|{
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|closeReader
specifier|private
name|boolean
name|closeReader
decl_stmt|;
DECL|field|subReaders
specifier|private
name|IndexReader
index|[]
name|subReaders
decl_stmt|;
DECL|field|docStarts
specifier|private
name|int
index|[]
name|docStarts
decl_stmt|;
comment|/** Creates a searcher searching the index in the named directory.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link #IndexSearcher(Directory, boolean)} instead    */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the index in the named    *  directory.  You should pass readOnly=true, since it    *  gives much better concurrent performance, unless you    *  intend to do write operations (delete documents or    *  change norms) with the underlying IndexReader.    * @param path directory where IndexReader will be opened    * @param readOnly if true, the underlying IndexReader    * will be opened readOnly    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link #IndexSearcher(Directory, boolean)} instead    */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|readOnly
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the index in the provided directory.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link #IndexSearcher(Directory, boolean)} instead    */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the index in the named    *  directory.  You should pass readOnly=true, since it    *  gives much better concurrent performance, unless you    *  intend to do write operations (delete documents or    *  change norms) with the underlying IndexReader.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @param path directory where IndexReader will be opened    * @param readOnly if true, the underlying IndexReader    * will be opened readOnly    */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|Directory
name|path
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|path
argument_list|,
name|readOnly
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the provided index. */
DECL|method|IndexSearcher
specifier|public
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|this
argument_list|(
name|r
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexSearcher
specifier|private
name|IndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|boolean
name|closeReader
parameter_list|)
block|{
name|reader
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|closeReader
operator|=
name|closeReader
expr_stmt|;
name|List
name|subReadersList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|gatherSubReaders
argument_list|(
name|subReadersList
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|subReaders
operator|=
operator|(
name|IndexReader
index|[]
operator|)
name|subReadersList
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
name|subReadersList
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|docStarts
operator|=
operator|new
name|int
index|[
name|subReaders
operator|.
name|length
index|]
expr_stmt|;
name|int
name|maxDoc
init|=
literal|0
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docStarts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
name|maxDoc
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|gatherSubReaders
specifier|protected
name|void
name|gatherSubReaders
parameter_list|(
name|List
name|allSubReaders
parameter_list|,
name|IndexReader
name|r
parameter_list|)
block|{
name|IndexReader
index|[]
name|subReaders
init|=
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|subReaders
operator|==
literal|null
condition|)
block|{
comment|// Add the reader itself, and do not recurse
name|allSubReaders
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|gatherSubReaders
argument_list|(
name|allSubReaders
argument_list|,
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Return the {@link IndexReader} this searches. */
DECL|method|getIndexReader
specifier|public
name|IndexReader
name|getIndexReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
comment|/**    * Note that the underlying IndexReader is not closed, if    * IndexSearcher was constructed with IndexSearcher(IndexReader r).    * If the IndexReader was supplied implicitly by specifying a directory, then    * the IndexReader gets closed.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closeReader
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// inherit javadoc
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|,
name|fieldSelector
argument_list|)
return|;
block|}
comment|// inherit javadoc
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|maxDoc
argument_list|()
return|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nDocs
operator|<=
literal|0
condition|)
comment|// null might be returned from hq.top() below.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"nDocs must be> 0"
argument_list|)
throw|;
comment|// TODO: The following should be changed to first obtain a Scorer and then ask it
comment|// if it's going to return in-order or out-of-order docs, and create TSDC
comment|// accordingly.
name|TopScoreDocCollector
name|collector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|nDocs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|nDocs
argument_list|,
name|sort
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Just like {@link #search(Weight, Filter, int, Sort)}, but you choose    * whether or not the fields in the returned {@link FieldDoc} instances should    * be set by specifying fillFields.<br>    *<b>NOTE:</b> currently, this method tracks document scores and sets them in    * the returned {@link FieldDoc}, however in 3.0 it will move to not track    * document scores. If document scores tracking is still needed, you can use    * {@link #search(Weight, Filter, Collector)} and pass in a    * {@link TopFieldCollector} instance.    */
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|int
name|nDocs
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|fillFields
parameter_list|)
throws|throws
name|IOException
block|{
name|SortField
index|[]
name|fields
init|=
name|sort
operator|.
name|fields
decl_stmt|;
name|boolean
name|legacy
init|=
literal|false
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SortField
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|String
name|fieldname
init|=
name|field
operator|.
name|getField
argument_list|()
decl_stmt|;
name|int
name|type
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// Resolve AUTO into its true type
if|if
condition|(
name|type
operator|==
name|SortField
operator|.
name|AUTO
condition|)
block|{
name|int
name|autotype
init|=
name|SortField
operator|.
name|detectFieldType
argument_list|(
name|reader
argument_list|,
name|fieldname
argument_list|)
decl_stmt|;
if|if
condition|(
name|autotype
operator|==
name|SortField
operator|.
name|STRING
condition|)
block|{
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|field
operator|.
name|getLocale
argument_list|()
argument_list|,
name|field
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldname
argument_list|,
name|autotype
argument_list|,
name|field
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|field
operator|.
name|getUseLegacySearch
argument_list|()
condition|)
block|{
name|legacy
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|legacy
condition|)
block|{
comment|// Search the single top-level reader
name|TopDocCollector
name|collector
init|=
operator|new
name|TopFieldDocCollector
argument_list|(
name|reader
argument_list|,
name|sort
argument_list|,
name|nDocs
argument_list|)
decl_stmt|;
name|HitCollectorWrapper
name|hcw
init|=
operator|new
name|HitCollectorWrapper
argument_list|(
name|collector
argument_list|)
decl_stmt|;
name|hcw
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|doSearch
argument_list|(
name|reader
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|hcw
argument_list|)
expr_stmt|;
return|return
operator|(
name|TopFieldDocs
operator|)
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
comment|// Search each sub-reader
comment|// TODO: The following should be changed to first obtain a Scorer and then ask it
comment|// if it's going to return in-order or out-of-order docs, and create TSDC
comment|// accordingly.
name|TopFieldCollector
name|collector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|nDocs
argument_list|,
name|fillFields
argument_list|,
name|fieldSortDoTrackScores
argument_list|,
name|fieldSortDoMaxScore
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
operator|(
name|TopFieldDocs
operator|)
name|collector
operator|.
name|topDocs
argument_list|()
return|;
block|}
comment|// inherit javadoc
comment|/** @deprecated use {@link #search(Weight, Filter, Collector)} instead. */
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
operator|new
name|HitCollectorWrapper
argument_list|(
name|results
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// inherit javadoc
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// search each subreader
name|collector
operator|.
name|setNextReader
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|,
name|docStarts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|doSearch
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|,
name|weight
argument_list|,
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSearch
specifier|private
name|void
name|doSearch
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
specifier|final
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
return|return;
name|int
name|docID
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
assert|assert
name|docID
operator|==
operator|-
literal|1
operator|||
name|docID
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
block|{
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// CHECKME: use ConjunctionScorer here?
name|DocIdSetIterator
name|filterIter
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|filterDoc
init|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|scorerDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
decl_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|scorerDoc
operator|==
name|filterDoc
condition|)
block|{
comment|// Check if scorer has exhausted, only before collecting.
if|if
condition|(
name|scorerDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|collector
operator|.
name|collect
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
name|filterDoc
operator|=
name|filterIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|scorerDoc
operator|>
name|filterDoc
condition|)
block|{
name|filterDoc
operator|=
name|filterIter
operator|.
name|advance
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|filterDoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|original
decl_stmt|;
for|for
control|(
name|Query
name|rewrittenQuery
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
init|;
name|rewrittenQuery
operator|!=
name|query
condition|;
name|rewrittenQuery
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
control|)
block|{
name|query
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
return|;
block|}
DECL|field|fieldSortDoTrackScores
specifier|private
name|boolean
name|fieldSortDoTrackScores
decl_stmt|;
DECL|field|fieldSortDoMaxScore
specifier|private
name|boolean
name|fieldSortDoMaxScore
decl_stmt|;
comment|/** @deprecated */
DECL|method|setDefaultFieldSortScoring
specifier|public
name|void
name|setDefaultFieldSortScoring
parameter_list|(
name|boolean
name|doTrackScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|)
block|{
name|fieldSortDoTrackScores
operator|=
name|doTrackScores
expr_stmt|;
name|fieldSortDoMaxScore
operator|=
name|doMaxScore
expr_stmt|;
block|}
block|}
end_class

end_unit

