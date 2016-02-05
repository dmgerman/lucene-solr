begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/**  * A base class for all collectors that return a {@link TopDocs} output. This  * collector allows easy extension by providing a single constructor which  * accepts a {@link PriorityQueue} as well as protected members for that  * priority queue and a counter of the number of total hits.<br>  * Extending classes can override any of the methods to provide their own  * implementation, as well as avoid the use of the priority queue entirely by  * passing null to {@link #TopDocsCollector(PriorityQueue)}. In that case  * however, you might want to consider overriding all methods, in order to avoid  * a NullPointerException.  */
end_comment

begin_class
DECL|class|TopDocsCollector
specifier|public
specifier|abstract
class|class
name|TopDocsCollector
parameter_list|<
name|T
extends|extends
name|ScoreDoc
parameter_list|>
implements|implements
name|Collector
block|{
comment|/** This is used in case topDocs() is called with illegal parameters, or there    *  simply aren't (enough) results. */
DECL|field|EMPTY_TOPDOCS
specifier|protected
specifier|static
specifier|final
name|TopDocs
name|EMPTY_TOPDOCS
init|=
operator|new
name|TopDocs
argument_list|(
literal|0
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
decl_stmt|;
comment|/**    * The priority queue which holds the top documents. Note that different    * implementations of PriorityQueue give different meaning to 'top documents'.    * HitQueue for example aggregates the top scoring documents, while other PQ    * implementations may hold documents sorted by other criteria.    */
DECL|field|pq
specifier|protected
name|PriorityQueue
argument_list|<
name|T
argument_list|>
name|pq
decl_stmt|;
comment|/** The total number of documents that the collector encountered. */
DECL|field|totalHits
specifier|protected
name|int
name|totalHits
decl_stmt|;
DECL|method|TopDocsCollector
specifier|protected
name|TopDocsCollector
parameter_list|(
name|PriorityQueue
argument_list|<
name|T
argument_list|>
name|pq
parameter_list|)
block|{
name|this
operator|.
name|pq
operator|=
name|pq
expr_stmt|;
block|}
comment|/**    * Populates the results array with the ScoreDoc instances. This can be    * overridden in case a different ScoreDoc type should be returned.    */
DECL|method|populateResults
specifier|protected
name|void
name|populateResults
parameter_list|(
name|ScoreDoc
index|[]
name|results
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|howMany
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
name|results
index|[
name|i
index|]
operator|=
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a {@link TopDocs} instance containing the given results. If    *<code>results</code> is null it means there are no results to return,    * either because there were 0 calls to collect() or because the arguments to    * topDocs were invalid.    */
DECL|method|newTopDocs
specifier|protected
name|TopDocs
name|newTopDocs
parameter_list|(
name|ScoreDoc
index|[]
name|results
parameter_list|,
name|int
name|start
parameter_list|)
block|{
return|return
name|results
operator|==
literal|null
condition|?
name|EMPTY_TOPDOCS
else|:
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|results
argument_list|)
return|;
block|}
comment|/** The total number of documents that matched this query. */
DECL|method|getTotalHits
specifier|public
name|int
name|getTotalHits
parameter_list|()
block|{
return|return
name|totalHits
return|;
block|}
comment|/** The number of valid PQ entries */
DECL|method|topDocsSize
specifier|protected
name|int
name|topDocsSize
parameter_list|()
block|{
comment|// In case pq was populated with sentinel values, there might be less
comment|// results than pq.size(). Therefore return all results until either
comment|// pq.size() or totalHits.
return|return
name|totalHits
operator|<
name|pq
operator|.
name|size
argument_list|()
condition|?
name|totalHits
else|:
name|pq
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** Returns the top docs that were collected by this collector. */
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|()
block|{
comment|// In case pq was populated with sentinel values, there might be less
comment|// results than pq.size(). Therefore return all results until either
comment|// pq.size() or totalHits.
return|return
name|topDocs
argument_list|(
literal|0
argument_list|,
name|topDocsSize
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the documents in the range [start .. pq.size()) that were collected    * by this collector. Note that if {@code start>= pq.size()}, an empty TopDocs is    * returned.<br>    * This method is convenient to call if the application always asks for the    * last results, starting from the last 'page'.<br>    *<b>NOTE:</b> you cannot call this method more than once for each search    * execution. If you need to call it more than once, passing each time a    * different<code>start</code>, you should call {@link #topDocs()} and work    * with the returned {@link TopDocs} object, which will contain all the    * results this search execution collected.    */
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|)
block|{
comment|// In case pq was populated with sentinel values, there might be less
comment|// results than pq.size(). Therefore return all results until either
comment|// pq.size() or totalHits.
return|return
name|topDocs
argument_list|(
name|start
argument_list|,
name|topDocsSize
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the documents in the range [start .. start+howMany) that were    * collected by this collector. Note that if {@code start>= pq.size()}, an empty    * TopDocs is returned, and if pq.size() - start&lt; howMany, then only the    * available documents in [start .. pq.size()) are returned.<br>    * This method is useful to call in case pagination of search results is    * allowed by the search application, as well as it attempts to optimize the    * memory used by allocating only as much as requested by howMany.<br>    *<b>NOTE:</b> you cannot call this method more than once for each search    * execution. If you need to call it more than once, passing each time a    * different range, you should call {@link #topDocs()} and work with the    * returned {@link TopDocs} object, which will contain all the results this    * search execution collected.    */
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
comment|// In case pq was populated with sentinel values, there might be less
comment|// results than pq.size(). Therefore return all results until either
comment|// pq.size() or totalHits.
name|int
name|size
init|=
name|topDocsSize
argument_list|()
decl_stmt|;
comment|// Don't bother to throw an exception, just return an empty TopDocs in case
comment|// the parameters are invalid or out of range.
comment|// TODO: shouldn't we throw IAE if apps give bad params here so they dont
comment|// have sneaky silent bugs?
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|start
operator|>=
name|size
operator|||
name|howMany
operator|<=
literal|0
condition|)
block|{
return|return
name|newTopDocs
argument_list|(
literal|null
argument_list|,
name|start
argument_list|)
return|;
block|}
comment|// We know that start< pqsize, so just fix howMany.
name|howMany
operator|=
name|Math
operator|.
name|min
argument_list|(
name|size
operator|-
name|start
argument_list|,
name|howMany
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|results
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
comment|// pq's pop() returns the 'least' element in the queue, therefore need
comment|// to discard the first ones, until we reach the requested range.
comment|// Note that this loop will usually not be executed, since the common usage
comment|// should be that the caller asks for the last howMany results. However it's
comment|// needed here for completeness.
for|for
control|(
name|int
name|i
init|=
name|pq
operator|.
name|size
argument_list|()
operator|-
name|start
operator|-
name|howMany
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|// Get the requested results from pq.
name|populateResults
argument_list|(
name|results
argument_list|,
name|howMany
argument_list|)
expr_stmt|;
return|return
name|newTopDocs
argument_list|(
name|results
argument_list|,
name|start
argument_list|)
return|;
block|}
block|}
end_class

end_unit

