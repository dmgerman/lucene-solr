begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|Collection
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
name|Iterator
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|QueryMaker
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
name|Fieldable
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
name|search
operator|.
name|TopDocs
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
name|TopFieldCollector
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
name|Sort
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
name|highlight
operator|.
name|Highlighter
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
name|highlight
operator|.
name|QueryScorer
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
name|highlight
operator|.
name|SimpleHTMLFormatter
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
name|highlight
operator|.
name|TextFragment
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
name|highlight
operator|.
name|TokenSources
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
name|highlight
operator|.
name|InvalidTokenOffsetsException
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
comment|/**  * Read index (abstract) task.  * Sub classes implement withSearch(), withWarm(), withTraverse() and withRetrieve()  * methods to configure the actual action.  *<p/>  *<p>Note: All ReadTasks reuse the reader if it is already open.  * Otherwise a reader is opened at start and closed at the end.  *<p>  * The<code>search.num.hits</code> config parameter sets  * the top number of hits to collect during searching.  *<p>Other side effects: none.  */
end_comment

begin_class
DECL|class|ReadTask
specifier|public
specifier|abstract
class|class
name|ReadTask
extends|extends
name|PerfTask
block|{
DECL|method|ReadTask
specifier|public
name|ReadTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|res
init|=
literal|0
decl_stmt|;
name|boolean
name|closeReader
init|=
literal|false
decl_stmt|;
comment|// open reader or use existing one
name|IndexReader
name|ir
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|ir
operator|==
literal|null
condition|)
block|{
name|Directory
name|dir
init|=
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|closeReader
operator|=
literal|true
expr_stmt|;
comment|//res++; //this is confusing, comment it out
block|}
comment|// optionally warm and add num docs traversed to count
if|if
condition|(
name|withWarm
argument_list|()
condition|)
block|{
name|Document
name|doc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|ir
operator|.
name|maxDoc
argument_list|()
condition|;
name|m
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|ir
operator|.
name|isDeleted
argument_list|(
name|m
argument_list|)
condition|)
block|{
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|res
operator|+=
operator|(
name|doc
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|withSearch
argument_list|()
condition|)
block|{
name|res
operator|++
expr_stmt|;
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
if|if
condition|(
name|closeReader
condition|)
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searcher
operator|=
name|getRunData
argument_list|()
operator|.
name|getIndexSearcher
argument_list|()
expr_stmt|;
block|}
name|QueryMaker
name|queryMaker
init|=
name|getQueryMaker
argument_list|()
decl_stmt|;
name|Query
name|q
init|=
name|queryMaker
operator|.
name|makeQuery
argument_list|()
decl_stmt|;
name|Sort
name|sort
init|=
name|getSort
argument_list|()
decl_stmt|;
name|TopDocs
name|hits
decl_stmt|;
specifier|final
name|int
name|numHits
init|=
name|numHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|numHits
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
comment|// TODO: change the following to create TFC with in/out-of order
comment|// according to whether the query's Scorer.
name|TopFieldCollector
name|collector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
literal|true
argument_list|,
name|withScore
argument_list|()
argument_list|,
name|withMaxScore
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|hits
operator|=
name|collector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("q=" + q + ":" + hits.totalHits + " total hits");
if|if
condition|(
name|withTraverse
argument_list|()
condition|)
block|{
specifier|final
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|hits
operator|.
name|scoreDocs
decl_stmt|;
name|int
name|traversalSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|scoreDocs
operator|.
name|length
argument_list|,
name|traversalSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|traversalSize
operator|>
literal|0
condition|)
block|{
name|boolean
name|retrieve
init|=
name|withRetrieve
argument_list|()
decl_stmt|;
name|int
name|numHighlight
init|=
name|Math
operator|.
name|min
argument_list|(
name|numToHighlight
argument_list|()
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|getRunData
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|Highlighter
name|highlighter
init|=
literal|null
decl_stmt|;
name|int
name|maxFrags
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|numHighlight
operator|>
literal|0
condition|)
block|{
name|highlighter
operator|=
name|getHighlighter
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|maxFrags
operator|=
name|maxNumFragments
argument_list|()
expr_stmt|;
block|}
name|boolean
name|merge
init|=
name|isMergeContiguousFragments
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|traversalSize
condition|;
name|m
operator|++
control|)
block|{
name|int
name|id
init|=
name|scoreDocs
index|[
name|m
index|]
operator|.
name|doc
decl_stmt|;
name|res
operator|++
expr_stmt|;
if|if
condition|(
name|retrieve
condition|)
block|{
name|Document
name|document
init|=
name|retrieveDoc
argument_list|(
name|ir
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|res
operator|+=
name|document
operator|!=
literal|null
condition|?
literal|1
else|:
literal|0
expr_stmt|;
if|if
condition|(
name|numHighlight
operator|>
literal|0
operator|&&
name|m
operator|<
name|numHighlight
condition|)
block|{
name|Collection
comment|/*<String>*/
name|fieldsToHighlight
init|=
name|getFieldsToHighlight
argument_list|(
name|document
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|fieldsToHighlight
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|field
init|=
operator|(
name|String
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|text
init|=
name|document
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
name|TokenSources
operator|.
name|getAnyTokenStream
argument_list|(
name|ir
argument_list|,
name|id
argument_list|,
name|field
argument_list|,
name|document
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|res
operator|+=
name|doHighlight
argument_list|(
name|ts
argument_list|,
name|text
argument_list|,
name|highlighter
argument_list|,
name|merge
argument_list|,
name|maxFrags
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|closeReader
condition|)
block|{
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|retrieveDoc
specifier|protected
name|Document
name|retrieveDoc
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Return query maker used for this task.    */
DECL|method|getQueryMaker
specifier|public
specifier|abstract
name|QueryMaker
name|getQueryMaker
parameter_list|()
function_decl|;
comment|/**    * Return true if search should be performed.    */
DECL|method|withSearch
specifier|public
specifier|abstract
name|boolean
name|withSearch
parameter_list|()
function_decl|;
comment|/**    * Return true if warming should be performed.    */
DECL|method|withWarm
specifier|public
specifier|abstract
name|boolean
name|withWarm
parameter_list|()
function_decl|;
comment|/**    * Return true if, with search, results should be traversed.    */
DECL|method|withTraverse
specifier|public
specifier|abstract
name|boolean
name|withTraverse
parameter_list|()
function_decl|;
comment|/** Whether scores should be computed (only useful with    *  field sort) */
DECL|method|withScore
specifier|public
name|boolean
name|withScore
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Whether maxScores should be computed (only useful with    *  field sort) */
DECL|method|withMaxScore
specifier|public
name|boolean
name|withMaxScore
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Specify the number of hits to traverse.  Tasks should override this if they want to restrict the number    * of hits that are traversed when {@link #withTraverse()} is true. Must be greater than 0.    *<p/>    * Read task calculates the traversal as: Math.min(hits.length(), traversalSize())    *    * @return Integer.MAX_VALUE    */
DECL|method|traversalSize
specifier|public
name|int
name|traversalSize
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
DECL|field|DEFAULT_SEARCH_NUM_HITS
specifier|static
specifier|final
name|int
name|DEFAULT_SEARCH_NUM_HITS
init|=
literal|10
decl_stmt|;
DECL|field|numHits
specifier|private
name|int
name|numHits
decl_stmt|;
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|numHits
operator|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"search.num.hits"
argument_list|,
name|DEFAULT_SEARCH_NUM_HITS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Specify the number of hits to retrieve.  Tasks should override this if they want to restrict the number    * of hits that are collected during searching. Must be greater than 0.    *    * @return 10 by default, or search.num.hits config if set.    */
DECL|method|numHits
specifier|public
name|int
name|numHits
parameter_list|()
block|{
return|return
name|numHits
return|;
block|}
comment|/**    * Return true if, with search& results traversing, docs should be retrieved.    */
DECL|method|withRetrieve
specifier|public
specifier|abstract
name|boolean
name|withRetrieve
parameter_list|()
function_decl|;
comment|/**    * Set to the number of documents to highlight.    *    * @return The number of the results to highlight.  O means no docs will be highlighted.    */
DECL|method|numToHighlight
specifier|public
name|int
name|numToHighlight
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|getHighlighter
specifier|protected
name|Highlighter
name|getHighlighter
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
return|return
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|q
argument_list|)
argument_list|)
return|;
block|}
comment|/**    *    * @return the maxiumum number of highlighter fragments    */
DECL|method|maxNumFragments
specifier|public
name|int
name|maxNumFragments
parameter_list|()
block|{
return|return
literal|10
return|;
block|}
comment|/**    *    * @return true if the highlighter should merge contiguous fragments    */
DECL|method|isMergeContiguousFragments
specifier|public
name|boolean
name|isMergeContiguousFragments
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|doHighlight
specifier|protected
name|int
name|doHighlight
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
name|text
parameter_list|,
name|Highlighter
name|highlighter
parameter_list|,
name|boolean
name|mergeContiguous
parameter_list|,
name|int
name|maxFragments
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
name|TextFragment
index|[]
name|frag
init|=
name|highlighter
operator|.
name|getBestTextFragments
argument_list|(
name|ts
argument_list|,
name|text
argument_list|,
name|mergeContiguous
argument_list|,
name|maxFragments
argument_list|)
decl_stmt|;
return|return
name|frag
operator|!=
literal|null
condition|?
name|frag
operator|.
name|length
else|:
literal|0
return|;
block|}
DECL|method|getSort
specifier|protected
name|Sort
name|getSort
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Define the fields to highlight.  Base implementation returns all fields    * @param document The Document    * @return A Collection of Field names (Strings)    */
DECL|method|getFieldsToHighlight
specifier|protected
name|Collection
comment|/*<String>*/
name|getFieldsToHighlight
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|List
comment|/*<Fieldable>*/
name|fieldables
init|=
name|document
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|Set
comment|/*<String>*/
name|result
init|=
operator|new
name|HashSet
argument_list|(
name|fieldables
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|fieldables
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Fieldable
name|fieldable
init|=
operator|(
name|Fieldable
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|fieldable
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

