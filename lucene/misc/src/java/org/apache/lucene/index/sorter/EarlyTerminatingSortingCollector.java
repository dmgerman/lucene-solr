begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|AtomicReaderContext
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
name|search
operator|.
name|CollectionTerminatedException
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
name|TopDocsCollector
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
name|TotalHitCountCollector
import|;
end_import

begin_comment
comment|/**  * A {@link Collector} that early terminates collection of documents on a  * per-segment basis, if the segment was sorted according to the given  * {@link Sorter}.  *   *<p>  *<b>NOTE:</b> the {@link Collector} detects sorted segments according to  * {@link SortingMergePolicy}, so it's best used in conjunction with it. Also,  * it collects up to a specified num docs from each segment, and therefore is  * mostly suitable for use in conjunction with collectors such as  * {@link TopDocsCollector}, and not e.g. {@link TotalHitCountCollector}.  *<p>  *<b>NOTE</b>: If you wrap a {@link TopDocsCollector} that sorts in the same  * order as the index order, the returned {@link TopDocsCollector#topDocs()}  * will be correct. However the total of {@link TopDocsCollector#getTotalHits()  * hit count} will be underestimated since not all matching documents will have  * been collected.  *<p>  *<b>NOTE</b>: This {@link Collector} uses {@link Sorter#getID()} to detect  * whether a segment was sorted with the same {@link Sorter} as the one given in  * {@link #EarlyTerminatingSortingCollector(Collector, Sort, int)}. This has  * two implications:  *<ul>  *<li>if {@link Sorter#getID()} is not implemented correctly and returns  * different identifiers for equivalent {@link Sorter}s, this collector will not  * detect sorted segments,</li>  *<li>if you suddenly change the {@link IndexWriter}'s  * {@link SortingMergePolicy} to sort according to another criterion and if both  * the old and the new {@link Sorter}s have the same identifier, this  * {@link Collector} will incorrectly detect sorted segments.</li>  *</ul>  *   * @lucene.experimental  */
end_comment

begin_comment
comment|// nocommit: fix these javadocs to be about Sort
end_comment

begin_class
DECL|class|EarlyTerminatingSortingCollector
specifier|public
class|class
name|EarlyTerminatingSortingCollector
extends|extends
name|Collector
block|{
DECL|field|in
specifier|protected
specifier|final
name|Collector
name|in
decl_stmt|;
DECL|field|sort
specifier|protected
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|field|numDocsToCollect
specifier|protected
specifier|final
name|int
name|numDocsToCollect
decl_stmt|;
DECL|field|segmentTotalCollect
specifier|protected
name|int
name|segmentTotalCollect
decl_stmt|;
DECL|field|segmentSorted
specifier|protected
name|boolean
name|segmentSorted
decl_stmt|;
DECL|field|numCollected
specifier|private
name|int
name|numCollected
decl_stmt|;
comment|/**    * Create a new {@link EarlyTerminatingSortingCollector} instance.    *     * @param in    *          the collector to wrap    * @param sort    *          the sort you are sorting the search results on    * @param numDocsToCollect    *          the number of documents to collect on each segment. When wrapping    *          a {@link TopDocsCollector}, this number should be the number of    *          hits.    */
DECL|method|EarlyTerminatingSortingCollector
specifier|public
name|EarlyTerminatingSortingCollector
parameter_list|(
name|Collector
name|in
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|int
name|numDocsToCollect
parameter_list|)
block|{
if|if
condition|(
name|numDocsToCollect
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"numDocsToCollect must always be> 0, got "
operator|+
name|segmentTotalCollect
argument_list|)
throw|;
block|}
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|numDocsToCollect
operator|=
name|numDocsToCollect
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
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
name|in
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|numCollected
operator|>=
name|segmentTotalCollect
condition|)
block|{
throw|throw
operator|new
name|CollectionTerminatedException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|segmentSorted
operator|=
name|SortingMergePolicy
operator|.
name|isSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|segmentTotalCollect
operator|=
name|segmentSorted
condition|?
name|numDocsToCollect
else|:
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
name|numCollected
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
operator|!
name|segmentSorted
operator|&&
name|in
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

