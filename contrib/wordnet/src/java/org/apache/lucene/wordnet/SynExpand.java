begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.wordnet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|wordnet
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
name|StringReader
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
name|LinkedList
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|TermAttribute
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
name|Collector
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
name|Scorer
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
name|Searcher
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
name|FSDirectory
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
comment|/**  * Expand a query by looking up synonyms for every term.  * You need to invoke {@link Syns2Index} first to build the synonym index.  *  * @see Syns2Index  */
end_comment

begin_class
DECL|class|SynExpand
specifier|public
specifier|final
class|class
name|SynExpand
block|{
comment|/** 	 * Test driver for synonym expansion. 	 * Uses boost factor of 0.9 for illustrative purposes. 	 * 	 * If you pass in the query "big dog" then it prints out: 	 * 	 *<code><pre> 	 * Query: big adult^0.9 bad^0.9 bighearted^0.9 boastful^0.9 boastfully^0.9 bounteous^0.9 bountiful^0.9 braggy^0.9 crowing^0.9 freehanded^0.9 giving^0.9 grown^0.9 grownup^0.9 handsome^0.9 large^0.9 liberal^0.9 magnanimous^0.9 momentous^0.9 openhanded^0.9 prominent^0.9 swelled^0.9 vainglorious^0.9 vauntingly^0.9 	 * dog andiron^0.9 blackguard^0.9 bounder^0.9 cad^0.9 chase^0.9 click^0.9 detent^0.9 dogtooth^0.9 firedog^0.9 frank^0.9 frankfurter^0.9 frump^0.9 heel^0.9 hotdog^0.9 hound^0.9 pawl^0.9 tag^0.9 tail^0.9 track^0.9 trail^0.9 weenie^0.9 wiener^0.9 wienerwurst^0.9 	 *</pre></code> 	 */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"java org.apache.lucene.wordnet.SynExpand<index path><query>"
argument_list|)
expr_stmt|;
block|}
name|FSDirectory
name|directory
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|query
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|String
name|field
init|=
literal|"contents"
decl_stmt|;
name|Query
name|q
init|=
name|expand
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|field
argument_list|,
literal|0.9f
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|q
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Perform synonym expansion on a query. 	 * 	 * @param query users query that is assumed to not have any "special" query syntax, thus it should be just normal words, so "big dog" makes sense, but a query like "title:foo^1.2" doesn't as this should presumably be passed directly to the default query parser. 	 * 	 * @param syns a opened to the Lucene index you previously created with {@link Syns2Index}. The searcher is not closed or otherwise altered. 	 * 	 * @param a optional analyzer used to parse the users query else {@link StandardAnalyzer} is used 	 * 	 * @param field optional field name to search in or null if you want the default of "contents" 	 * 	 * @param boost optional boost applied to synonyms else no boost is applied 	 * 	 * @return the expanded Query 	 */
DECL|method|expand
specifier|public
specifier|static
name|Query
name|expand
parameter_list|(
name|String
name|query
parameter_list|,
name|Searcher
name|syns
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|String
name|f
parameter_list|,
specifier|final
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Set
name|already
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|// avoid dups
name|List
name|top
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
comment|// needs to be separately listed..
specifier|final
name|String
name|field
init|=
operator|(
name|f
operator|==
literal|null
operator|)
condition|?
literal|"contents"
else|:
name|f
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
name|a
operator|=
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
expr_stmt|;
comment|// [1] Parse query into separate words so that when we expand we can avoid dups
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|word
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|already
operator|.
name|add
argument_list|(
name|word
argument_list|)
condition|)
name|top
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BooleanQuery
name|tmp
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|// [2] form query
name|Iterator
name|it
init|=
name|top
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// [2a] add to level words in
name|String
name|word
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|word
argument_list|)
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|syns
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|Syns2Index
operator|.
name|F_WORD
argument_list|,
name|word
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
name|IndexReader
name|reader
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|d
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
name|d
operator|.
name|getValues
argument_list|(
name|Syns2Index
operator|.
name|F_SYN
argument_list|)
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
name|values
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|syn
init|=
name|values
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|already
operator|.
name|add
argument_list|(
name|syn
argument_list|)
condition|)
comment|// avoid dups of top level words and synonyms
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|syn
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|boost
operator|>
literal|0
condition|)
comment|// else keep normal 1.0
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
argument_list|)
expr_stmt|;
comment|// [2b] add in unique synonums
block|}
return|return
name|tmp
return|;
block|}
block|}
end_class

end_unit

