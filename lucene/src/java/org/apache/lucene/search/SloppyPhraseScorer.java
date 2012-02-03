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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_class
DECL|class|SloppyPhraseScorer
specifier|final
class|class
name|SloppyPhraseScorer
extends|extends
name|PhraseScorer
block|{
DECL|field|slop
specifier|private
name|int
name|slop
decl_stmt|;
DECL|field|checkedRepeats
specifier|private
name|boolean
name|checkedRepeats
decl_stmt|;
comment|// flag to only check in first candidate doc in case there are no repeats
DECL|field|hasRepeats
specifier|private
name|boolean
name|hasRepeats
decl_stmt|;
comment|// flag indicating that there are repeats (already checked in first candidate doc)
DECL|field|pq
specifier|private
name|PhraseQueue
name|pq
decl_stmt|;
comment|// for advancing min position
DECL|field|nrPps
specifier|private
name|PhrasePositions
index|[]
name|nrPps
decl_stmt|;
comment|// non repeating pps ordered by their query offset
DECL|method|SloppyPhraseScorer
name|SloppyPhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|PhraseQuery
operator|.
name|PostingsAndFreq
index|[]
name|postings
parameter_list|,
name|int
name|slop
parameter_list|,
name|Similarity
operator|.
name|SloppySimScorer
name|docScorer
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|postings
argument_list|,
name|docScorer
argument_list|)
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
block|}
comment|/**    * Score a candidate doc for all slop-valid position-combinations (matches)     * encountered while traversing/hopping the PhrasePositions.    *<br> The score contribution of a match depends on the distance:     *<br> - highest score for distance=0 (exact match).    *<br> - score gets lower as distance gets higher.    *<br>Example: for query "a b"~2, a document "x a b a y" can be scored twice:     * once for "a b" (distance=0), and once for "b a" (distance=2).    *<br>Possibly not all valid combinations are encountered, because for efficiency      * we always propagate the least PhrasePosition. This allows to base on     * PriorityQueue and move forward faster.     * As result, for example, document "a b c b a"    * would score differently for queries "a b c"~4 and "c b a"~4, although     * they really are equivalent.     * Similarly, for doc "a b c b a f g", query "c b"~2     * would get same score as "g f"~2, although "c b"~2 could be matched twice.    * We may want to fix this in the future (currently not, for performance reasons).    */
annotation|@
name|Override
DECL|method|phraseFreq
specifier|protected
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|end
init|=
name|initPhrasePositions
argument_list|()
decl_stmt|;
comment|//printPositions(System.err, "INIT DONE:");
if|if
condition|(
name|end
operator|==
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
return|return
literal|0.0f
return|;
block|}
name|float
name|freq
init|=
literal|0.0f
decl_stmt|;
name|PhrasePositions
name|pp
init|=
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|int
name|matchLength
init|=
name|end
operator|-
name|pp
operator|.
name|position
decl_stmt|;
name|int
name|next
init|=
name|pq
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|pq
operator|.
name|top
argument_list|()
operator|.
name|position
else|:
name|pp
operator|.
name|position
decl_stmt|;
comment|//printQueue(System.err, pp, "Bef Loop: next="+next+" mlen="+end+"-"+pp.position+"="+matchLength);
while|while
condition|(
name|pp
operator|.
name|nextPosition
argument_list|()
operator|&&
operator|(
name|end
operator|=
name|advanceRepeats
argument_list|(
name|pp
argument_list|,
name|end
argument_list|)
operator|)
operator|!=
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|next
condition|)
block|{
comment|//printQueue(System.err, pp, "A:>next="+next+" matchLength="+matchLength);
if|if
condition|(
name|matchLength
operator|<=
name|slop
condition|)
block|{
name|freq
operator|+=
name|docScorer
operator|.
name|computeSlopFactor
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
comment|// score match
block|}
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|pp
operator|=
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
name|next
operator|=
name|pq
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|pq
operator|.
name|top
argument_list|()
operator|.
name|position
else|:
name|pp
operator|.
name|position
expr_stmt|;
name|matchLength
operator|=
name|end
operator|-
name|pp
operator|.
name|position
expr_stmt|;
comment|//printQueue(System.err, pp, "B:>next="+next+" matchLength="+matchLength);
block|}
else|else
block|{
name|int
name|matchLength2
init|=
name|end
operator|-
name|pp
operator|.
name|position
decl_stmt|;
comment|//printQueue(System.err, pp, "C: mlen2<mlen: next="+next+" matchLength="+matchLength+" matchLength2="+matchLength2);
if|if
condition|(
name|matchLength2
operator|<
name|matchLength
condition|)
block|{
name|matchLength
operator|=
name|matchLength2
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|matchLength
operator|<=
name|slop
condition|)
block|{
name|freq
operator|+=
name|docScorer
operator|.
name|computeSlopFactor
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
comment|// score match
block|}
return|return
name|freq
return|;
block|}
comment|/**    * Advance repeating pps of an input (non-repeating) pp.    * Return a modified 'end' in case pp or its repeats exceeds original 'end'.    * "Dirty" trick: when there are repeats, modifies pp's position to that of     * least repeater of pp (needed when due to holes repeaters' positions are "back").    */
DECL|method|advanceRepeats
specifier|private
name|int
name|advanceRepeats
parameter_list|(
name|PhrasePositions
name|pp
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|repeatsEnd
init|=
name|end
decl_stmt|;
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|repeatsEnd
condition|)
block|{
name|repeatsEnd
operator|=
name|pp
operator|.
name|position
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hasRepeats
condition|)
block|{
return|return
name|repeatsEnd
return|;
block|}
name|int
name|tpPos
init|=
name|tpPos
argument_list|(
name|pp
argument_list|)
decl_stmt|;
for|for
control|(
name|PhrasePositions
name|pp2
init|=
name|pp
operator|.
name|nextRepeating
init|;
name|pp2
operator|!=
literal|null
condition|;
name|pp2
operator|=
name|pp2
operator|.
name|nextRepeating
control|)
block|{
while|while
condition|(
name|tpPos
argument_list|(
name|pp2
argument_list|)
operator|<=
name|tpPos
condition|)
block|{
if|if
condition|(
operator|!
name|pp2
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
return|return
name|Integer
operator|.
name|MIN_VALUE
return|;
block|}
block|}
name|tpPos
operator|=
name|tpPos
argument_list|(
name|pp2
argument_list|)
expr_stmt|;
if|if
condition|(
name|pp2
operator|.
name|position
operator|>
name|repeatsEnd
condition|)
block|{
name|repeatsEnd
operator|=
name|pp2
operator|.
name|position
expr_stmt|;
block|}
comment|// "dirty" trick: with holes, given a pp, its repeating pp2 might have smaller position.
comment|// so in order to have the right "start" in matchLength computation we fake pp.position.
comment|// this relies on pp.nextPosition() not using pp.position.
if|if
condition|(
name|pp2
operator|.
name|position
operator|<
name|pp
operator|.
name|position
condition|)
block|{
name|pp
operator|.
name|position
operator|=
name|pp2
operator|.
name|position
expr_stmt|;
block|}
block|}
return|return
name|repeatsEnd
return|;
block|}
comment|/**    * Initialize PhrasePositions in place.    * There is a one time initialization for this scorer (taking place at the first doc that matches all terms):    *<ul>    *<li>Detect groups of repeating pps: those with same tpPos (tpPos==position in the doc) but different offsets in query.    *<li>For each such group:    *<ul>    *<li>form an inner linked list of the repeating ones.    *<li>propagate all group members but first so that they land on different tpPos().    *</ul>    *<li>Mark whether there are repetitions at all, so that scoring queries with no repetitions has no overhead due to this computation.    *<li>Insert to pq only non repeating PPs, or PPs that are the first in a repeating group.    *</ul>    * Examples:    *<ol>    *<li>no repetitions:<b>"ho my"~2</b>    *<li>repetitions:<b>"ho my my"~2</b>    *<li>repetitions:<b>"my ho my"~2</b>    *</ol>    * @return end (max position), or Integer.MIN_VALUE if any term ran out (i.e. done)     */
DECL|method|initPhrasePositions
specifier|private
name|int
name|initPhrasePositions
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|end
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
comment|// no repeats at all (most common case is also the simplest one)
if|if
condition|(
name|checkedRepeats
operator|&&
operator|!
name|hasRepeats
condition|)
block|{
comment|// build queue from list
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|min
init|,
name|prev
init|=
literal|null
init|;
name|prev
operator|!=
name|max
condition|;
name|pp
operator|=
operator|(
name|prev
operator|=
name|pp
operator|)
operator|.
name|next
control|)
block|{
comment|// iterate cyclic list: done once handled max
name|pp
operator|.
name|firstPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
block|{
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
block|}
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// build pq from list
block|}
return|return
name|end
return|;
block|}
comment|//printPositions(System.err, "Init: 1: Bef position");
comment|// position the pp's
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|min
init|,
name|prev
init|=
literal|null
init|;
name|prev
operator|!=
name|max
condition|;
name|pp
operator|=
operator|(
name|prev
operator|=
name|pp
operator|)
operator|.
name|next
control|)
block|{
comment|// iterate cyclic list: done once handled max
name|pp
operator|.
name|firstPosition
argument_list|()
expr_stmt|;
block|}
comment|//printPositions(System.err, "Init: 2: Aft position");
comment|// one time initialization for this scorer (done only for the first candidate doc)
if|if
condition|(
operator|!
name|checkedRepeats
condition|)
block|{
name|checkedRepeats
operator|=
literal|true
expr_stmt|;
name|ArrayList
argument_list|<
name|PhrasePositions
argument_list|>
name|ppsA
init|=
operator|new
name|ArrayList
argument_list|<
name|PhrasePositions
argument_list|>
argument_list|()
decl_stmt|;
name|PhrasePositions
name|dummyPP
init|=
operator|new
name|PhrasePositions
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// check for repeats
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|min
init|,
name|prev
init|=
literal|null
init|;
name|prev
operator|!=
name|max
condition|;
name|pp
operator|=
operator|(
name|prev
operator|=
name|pp
operator|)
operator|.
name|next
control|)
block|{
comment|// iterate cyclic list: done once handled max
if|if
condition|(
name|pp
operator|.
name|nextRepeating
operator|!=
literal|null
condition|)
block|{
continue|continue;
comment|// a repetition of an earlier pp
block|}
name|ppsA
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|int
name|tpPos
init|=
name|tpPos
argument_list|(
name|pp
argument_list|)
decl_stmt|;
for|for
control|(
name|PhrasePositions
name|prevB
init|=
name|pp
init|,
name|pp2
init|=
name|pp
operator|.
name|next
init|;
name|pp2
operator|!=
name|min
condition|;
name|pp2
operator|=
name|pp2
operator|.
name|next
control|)
block|{
if|if
condition|(
name|pp2
operator|.
name|nextRepeating
operator|!=
literal|null
comment|// already detected as a repetition of an earlier pp
operator|||
name|pp
operator|.
name|offset
operator|==
name|pp2
operator|.
name|offset
comment|// not a repetition: the two PPs are originally in same offset in the query!
operator|||
name|tpPos
argument_list|(
name|pp2
argument_list|)
operator|!=
name|tpPos
condition|)
block|{
comment|// not a repetition
continue|continue;
block|}
comment|// a repetition
name|hasRepeats
operator|=
literal|true
expr_stmt|;
name|prevB
operator|.
name|nextRepeating
operator|=
name|pp2
expr_stmt|;
comment|// add pp2 to the repeats linked list
name|pp2
operator|.
name|nextRepeating
operator|=
name|dummyPP
expr_stmt|;
comment|// allows not to handle the last pp in a sub-list
name|prevB
operator|=
name|pp2
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasRepeats
condition|)
block|{
comment|// clean dummy markers
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|min
init|,
name|prev
init|=
literal|null
init|;
name|prev
operator|!=
name|max
condition|;
name|pp
operator|=
operator|(
name|prev
operator|=
name|pp
operator|)
operator|.
name|next
control|)
block|{
comment|// iterate cyclic list: done once handled max
if|if
condition|(
name|pp
operator|.
name|nextRepeating
operator|==
name|dummyPP
condition|)
block|{
name|pp
operator|.
name|nextRepeating
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
name|nrPps
operator|=
name|ppsA
operator|.
name|toArray
argument_list|(
operator|new
name|PhrasePositions
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|pq
operator|=
operator|new
name|PhraseQueue
argument_list|(
name|nrPps
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|//printPositions(System.err, "Init: 3: Aft check-repeats");
comment|// with repeats must advance some repeating pp's so they all start with differing tp's
if|if
condition|(
name|hasRepeats
condition|)
block|{
for|for
control|(
name|PhrasePositions
name|pp
range|:
name|nrPps
control|)
block|{
if|if
condition|(
operator|(
name|end
operator|=
name|advanceRepeats
argument_list|(
name|pp
argument_list|,
name|end
argument_list|)
operator|)
operator|==
name|Integer
operator|.
name|MIN_VALUE
condition|)
block|{
return|return
name|Integer
operator|.
name|MIN_VALUE
return|;
comment|// ran out of a term -- done (no valid matches in current doc)
block|}
block|}
block|}
comment|//printPositions(System.err, "Init: 4: Aft advance-repeats");
comment|// build queue from non repeating pps
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
range|:
name|nrPps
control|)
block|{
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
block|{
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
block|}
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
block|}
return|return
name|end
return|;
block|}
comment|/** Actual position in doc of a PhrasePosition, relies on that position = tpPos - offset) */
DECL|method|tpPos
specifier|private
specifier|final
name|int
name|tpPos
parameter_list|(
name|PhrasePositions
name|pp
parameter_list|)
block|{
return|return
name|pp
operator|.
name|position
operator|+
name|pp
operator|.
name|offset
return|;
block|}
comment|//  private void printPositions(PrintStream ps, String title) {
comment|//    ps.println();
comment|//    ps.println("---- "+title);
comment|//    int k = 0;
comment|//    if (nrPps!=null) {
comment|//      for (PhrasePositions pp: nrPps) {
comment|//        ps.println("  " + k++ + "  " + pp);
comment|//      }
comment|//    } else {
comment|//      for (PhrasePositions pp=min; 0==k || pp!=min; pp = pp.next) {
comment|//        ps.println("  " + k++ + "  " + pp);
comment|//      }
comment|//    }
comment|//  }
comment|//  private void printQueue(PrintStream ps, PhrasePositions ext, String title) {
comment|//    ps.println();
comment|//    ps.println("---- "+title);
comment|//    ps.println("EXT: "+ext);
comment|//    PhrasePositions[] t = new PhrasePositions[pq.size()];
comment|//    if (pq.size()>0) {
comment|//      t[0] = pq.pop();
comment|//      ps.println("  " + 0 + "  " + t[0]);
comment|//      for (int i=1; i<t.length; i++) {
comment|//        t[i] = pq.pop();
comment|//        assert t[i-1].position<= t[i].position : "PQ is out of order: "+(i-1)+"::"+t[i-1]+" "+i+"::"+t[i];
comment|//        ps.println("  " + i + "  " + t[i]);
comment|//      }
comment|//      // add them back
comment|//      for (int i=t.length-1; i>=0; i--) {
comment|//        pq.add(t[i]);
comment|//      }
comment|//    }
comment|//  }
block|}
end_class

end_unit

