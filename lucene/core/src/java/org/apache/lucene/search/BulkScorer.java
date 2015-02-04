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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/** This class is used to score a range of documents at  *  once, and is returned by {@link Weight#bulkScorer}.  Only  *  queries that have a more optimized means of scoring  *  across a range of documents need to override this.  *  Otherwise, a default implementation is wrapped around  *  the {@link Scorer} returned by {@link Weight#scorer}. */
end_comment

begin_class
DECL|class|BulkScorer
specifier|public
specifier|abstract
class|class
name|BulkScorer
block|{
comment|/** Scores and collects all matching documents.    * @param collector The collector to which all matching documents are passed.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|next
init|=
name|score
argument_list|(
name|collector
argument_list|,
literal|0
argument_list|,
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
decl_stmt|;
assert|assert
name|next
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
block|}
comment|/**    * Collects matching documents in a range and return an estimation of the    * next matching document which is on or after {@code max}.    *<p>The return value must be:</p><ul>    *<li>&gt;= {@code max},</li>    *<li>{@link DocIdSetIterator#NO_MORE_DOCS} if there are no more matches,</li>    *<li>&lt;= the first matching document that is&gt;= {@code max} otherwise.</li>    *</ul>    *<p>{@code min} is the minimum document to be considered for matching. All    * documents strictly before this value must be ignored.</p>    *<p>Although {@code max} would be a legal return value for this method, higher    * values might help callers skip more efficiently over non-matching portions    * of the docID space.</p>    *<p>For instance, a {@link Scorer}-based implementation could look like    * below:</p>    *<pre class="prettyprint">    * private final Scorer scorer; // set via constructor    *    * public int score(LeafCollector collector, int min, int max) throws IOException {    *   collector.setScorer(scorer);    *   int doc = scorer.docID();    *   if (doc&lt; min) {    *     doc = scorer.advance(min);    *   }    *   while (doc&lt; max) {    *     collector.collect(doc);    *     doc = scorer.nextDoc();    *   }    *   return doc;    * }    *</pre>    *    * @param  collector The collector to which all matching documents are passed.    * @param  min Score starting at, including, this document     * @param  max Score up to, but not including, this doc    * @return an under-estimation of the next matching doc after max    */
DECL|method|score
specifier|public
specifier|abstract
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Same as {@link Scorer#cost()} for bulk scorers.    */
DECL|method|cost
specifier|public
specifier|abstract
name|long
name|cost
parameter_list|()
function_decl|;
block|}
end_class

end_unit

