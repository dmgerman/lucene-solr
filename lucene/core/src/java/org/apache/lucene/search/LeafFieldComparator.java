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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Expert: comparator that gets instantiated on each leaf  * from a top-level {@link FieldComparator} instance.  *  *<p>A leaf comparator must define these functions:</p>  *  *<ul>  *  *<li> {@link #setBottom} This method is called by  *       {@link FieldValueHitQueue} to notify the  *       FieldComparator of the current weakest ("bottom")  *       slot.  Note that this slot may not hold the weakest  *       value according to your comparator, in cases where  *       your comparator is not the primary one (ie, is only  *       used to break ties from the comparators before it).  *  *<li> {@link #compareBottom} Compare a new hit (docID)  *       against the "weakest" (bottom) entry in the queue.  *  *<li> {@link #compareBottom} Compare a new hit (docID)  *       against the "weakest" (bottom) entry in the queue.  *  *<li> {@link #compareTop} Compare a new hit (docID)  *       against the top value previously set by a call to  *       {@link FieldComparator#setTopValue}.  *  *<li> {@link #copy} Installs a new hit into the  *       priority queue.  The {@link FieldValueHitQueue}  *       calls this method when a new hit is competitive.  *  *</ul>  *  * @see FieldComparator  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|LeafFieldComparator
specifier|public
interface|interface
name|LeafFieldComparator
block|{
comment|/**    * Set the bottom slot, ie the "weakest" (sorted last)    * entry in the queue.  When {@link #compareBottom} is    * called, you should compare against this slot.  This    * will always be called before {@link #compareBottom}.    *     * @param slot the currently weakest (sorted last) slot in the queue    */
DECL|method|setBottom
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|slot
parameter_list|)
function_decl|;
comment|/**    * Compare the bottom of the queue with this doc.  This will    * only invoked after setBottom has been called.  This    * should return the same result as {@link    * FieldComparator#compare(int,int)}} as if bottom were slot1 and the new    * document were slot 2.    *        *<p>For a search that hits many results, this method    * will be the hotspot (invoked by far the most    * frequently).</p>    *     * @param doc that was hit    * @return any {@code N< 0} if the doc's value is sorted after    * the bottom entry (not competitive), any {@code N> 0} if the    * doc's value is sorted before the bottom entry and {@code 0} if    * they are equal.    */
DECL|method|compareBottom
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Compare the top value with this doc.  This will    * only invoked after setTopValue has been called.  This    * should return the same result as {@link    * FieldComparator#compare(int,int)}} as if topValue were slot1 and the new    * document were slot 2.  This is only called for searches that    * use searchAfter (deep paging).    *        * @param doc that was hit    * @return any {@code N< 0} if the doc's value is sorted after    * the bottom entry (not competitive), any {@code N> 0} if the    * doc's value is sorted before the bottom entry and {@code 0} if    * they are equal.    */
DECL|method|compareTop
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method is called when a new hit is competitive.    * You should copy any state associated with this document    * that will be required for future comparisons, into the    * specified slot.    *     * @param slot which slot to copy the hit to    * @param doc docID relative to current reader    */
DECL|method|copy
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Sets the Scorer to use in case a document's score is    *  needed.    *     * @param scorer Scorer instance that you should use to    * obtain the current hit's score, if necessary. */
DECL|method|setScorer
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

