begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|Comparator
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
name|PriorityQueue
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
name|geo
operator|.
name|Rectangle
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
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|Bits
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
name|SloppyMath
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
name|bkd
operator|.
name|BKDReader
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|decodeLatitude
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
operator|.
name|decodeLongitude
import|;
end_import

begin_comment
comment|/**  * KNN search on top of 2D lat/lon indexed points.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NearestNeighbor
class|class
name|NearestNeighbor
block|{
DECL|class|Cell
specifier|static
class|class
name|Cell
implements|implements
name|Comparable
argument_list|<
name|Cell
argument_list|>
block|{
DECL|field|readerIndex
specifier|final
name|int
name|readerIndex
decl_stmt|;
DECL|field|nodeID
specifier|final
name|int
name|nodeID
decl_stmt|;
DECL|field|minPacked
specifier|final
name|byte
index|[]
name|minPacked
decl_stmt|;
DECL|field|maxPacked
specifier|final
name|byte
index|[]
name|maxPacked
decl_stmt|;
comment|/** The closest possible distance of all points in this cell */
DECL|field|distanceMeters
specifier|final
name|double
name|distanceMeters
decl_stmt|;
DECL|method|Cell
specifier|public
name|Cell
parameter_list|(
name|int
name|readerIndex
parameter_list|,
name|int
name|nodeID
parameter_list|,
name|byte
index|[]
name|minPacked
parameter_list|,
name|byte
index|[]
name|maxPacked
parameter_list|,
name|double
name|distanceMeters
parameter_list|)
block|{
name|this
operator|.
name|readerIndex
operator|=
name|readerIndex
expr_stmt|;
name|this
operator|.
name|nodeID
operator|=
name|nodeID
expr_stmt|;
name|this
operator|.
name|minPacked
operator|=
name|minPacked
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxPacked
operator|=
name|maxPacked
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|distanceMeters
operator|=
name|distanceMeters
expr_stmt|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Cell
name|other
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|distanceMeters
argument_list|,
name|other
operator|.
name|distanceMeters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|double
name|minLat
init|=
name|decodeLatitude
argument_list|(
name|minPacked
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|minLon
init|=
name|decodeLongitude
argument_list|(
name|minPacked
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|maxLat
init|=
name|decodeLatitude
argument_list|(
name|maxPacked
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|maxLon
init|=
name|decodeLongitude
argument_list|(
name|maxPacked
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
return|return
literal|"Cell(readerIndex="
operator|+
name|readerIndex
operator|+
literal|" lat="
operator|+
name|minLat
operator|+
literal|" TO "
operator|+
name|maxLat
operator|+
literal|", lon="
operator|+
name|minLon
operator|+
literal|" TO "
operator|+
name|maxLon
operator|+
literal|"; distanceMeters="
operator|+
name|distanceMeters
operator|+
literal|")"
return|;
block|}
block|}
DECL|class|NearestVisitor
specifier|private
specifier|static
class|class
name|NearestVisitor
implements|implements
name|IntersectVisitor
block|{
DECL|field|curDocBase
specifier|public
name|int
name|curDocBase
decl_stmt|;
DECL|field|curLiveDocs
specifier|public
name|Bits
name|curLiveDocs
decl_stmt|;
DECL|field|topN
specifier|final
name|int
name|topN
decl_stmt|;
DECL|field|hitQueue
specifier|final
name|PriorityQueue
argument_list|<
name|NearestHit
argument_list|>
name|hitQueue
decl_stmt|;
DECL|field|pointLat
specifier|final
name|double
name|pointLat
decl_stmt|;
DECL|field|pointLon
specifier|final
name|double
name|pointLon
decl_stmt|;
DECL|field|setBottomCounter
specifier|private
name|int
name|setBottomCounter
decl_stmt|;
DECL|field|minLon
specifier|private
name|double
name|minLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|maxLon
specifier|private
name|double
name|maxLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|minLat
specifier|private
name|double
name|minLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|maxLat
specifier|private
name|double
name|maxLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
comment|// second set of longitude ranges to check (for cross-dateline case)
DECL|field|minLon2
specifier|private
name|double
name|minLon2
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|method|NearestVisitor
specifier|public
name|NearestVisitor
parameter_list|(
name|PriorityQueue
argument_list|<
name|NearestHit
argument_list|>
name|hitQueue
parameter_list|,
name|int
name|topN
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
name|this
operator|.
name|hitQueue
operator|=
name|hitQueue
expr_stmt|;
name|this
operator|.
name|topN
operator|=
name|topN
expr_stmt|;
name|this
operator|.
name|pointLat
operator|=
name|pointLat
expr_stmt|;
name|this
operator|.
name|pointLon
operator|=
name|pointLon
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
DECL|method|maybeUpdateBBox
specifier|private
name|void
name|maybeUpdateBBox
parameter_list|()
block|{
if|if
condition|(
name|setBottomCounter
operator|<
literal|1024
operator|||
operator|(
name|setBottomCounter
operator|&
literal|0x3F
operator|)
operator|==
literal|0x3F
condition|)
block|{
name|NearestHit
name|hit
init|=
name|hitQueue
operator|.
name|peek
argument_list|()
decl_stmt|;
name|Rectangle
name|box
init|=
name|Rectangle
operator|.
name|fromPointDistance
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|hit
operator|.
name|distanceMeters
argument_list|)
decl_stmt|;
comment|//System.out.println("    update bbox to " + box);
name|minLat
operator|=
name|box
operator|.
name|minLat
expr_stmt|;
name|maxLat
operator|=
name|box
operator|.
name|maxLat
expr_stmt|;
if|if
condition|(
name|box
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
comment|// box1
name|minLon
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
name|maxLon
operator|=
name|box
operator|.
name|maxLon
expr_stmt|;
comment|// box2
name|minLon2
operator|=
name|box
operator|.
name|minLon
expr_stmt|;
block|}
else|else
block|{
name|minLon
operator|=
name|box
operator|.
name|minLon
expr_stmt|;
name|maxLon
operator|=
name|box
operator|.
name|maxLon
expr_stmt|;
comment|// disable box2
name|minLon2
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
block|}
name|setBottomCounter
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
block|{
comment|//System.out.println("visit docID=" + docID + " liveDocs=" + curLiveDocs);
if|if
condition|(
name|curLiveDocs
operator|!=
literal|null
operator|&&
name|curLiveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return;
block|}
comment|// TODO: work in int space, use haversinSortKey
name|double
name|docLatitude
init|=
name|decodeLatitude
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|docLongitude
init|=
name|decodeLongitude
argument_list|(
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
comment|// test bounding box
if|if
condition|(
name|docLatitude
argument_list|<
name|minLat
operator|||
name|docLatitude
argument_list|>
name|maxLat
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|(
name|docLongitude
argument_list|<
name|minLon
operator|||
name|docLongitude
argument_list|>
name|maxLon
operator|)
operator|&&
operator|(
name|docLongitude
operator|<
name|minLon2
operator|)
condition|)
block|{
return|return;
block|}
name|double
name|distanceMeters
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
decl_stmt|;
comment|//System.out.println("    visit docID=" + docID + " distanceMeters=" + distanceMeters + " docLat=" + docLatitude + " docLon=" + docLongitude);
name|int
name|fullDocID
init|=
name|curDocBase
operator|+
name|docID
decl_stmt|;
if|if
condition|(
name|hitQueue
operator|.
name|size
argument_list|()
operator|==
name|topN
condition|)
block|{
comment|// queue already full
name|NearestHit
name|hit
init|=
name|hitQueue
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|//System.out.println("      bottom distanceMeters=" + hit.distanceMeters);
comment|// we don't collect docs in order here, so we must also test the tie-break case ourselves:
if|if
condition|(
name|distanceMeters
operator|<
name|hit
operator|.
name|distanceMeters
operator|||
operator|(
name|distanceMeters
operator|==
name|hit
operator|.
name|distanceMeters
operator|&&
name|fullDocID
operator|<
name|hit
operator|.
name|docID
operator|)
condition|)
block|{
name|hitQueue
operator|.
name|poll
argument_list|()
expr_stmt|;
name|hit
operator|.
name|docID
operator|=
name|fullDocID
expr_stmt|;
name|hit
operator|.
name|distanceMeters
operator|=
name|distanceMeters
expr_stmt|;
name|hitQueue
operator|.
name|offer
argument_list|(
name|hit
argument_list|)
expr_stmt|;
comment|//System.out.println("      ** keep2, now bottom=" + hit);
name|maybeUpdateBBox
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|NearestHit
name|hit
init|=
operator|new
name|NearestHit
argument_list|()
decl_stmt|;
name|hit
operator|.
name|docID
operator|=
name|fullDocID
expr_stmt|;
name|hit
operator|.
name|distanceMeters
operator|=
name|distanceMeters
expr_stmt|;
name|hitQueue
operator|.
name|offer
argument_list|(
name|hit
argument_list|)
expr_stmt|;
comment|//System.out.println("      ** keep1, now bottom=" + hit);
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
comment|/** Holds one hit from {@link LatLonPoint#nearest} */
DECL|class|NearestHit
specifier|static
class|class
name|NearestHit
block|{
DECL|field|docID
specifier|public
name|int
name|docID
decl_stmt|;
DECL|field|distanceMeters
specifier|public
name|double
name|distanceMeters
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NearestHit(docID="
operator|+
name|docID
operator|+
literal|" distanceMeters="
operator|+
name|distanceMeters
operator|+
literal|")"
return|;
block|}
block|}
comment|// TODO: can we somehow share more with, or simply directly use, the LatLonPointDistanceComparator?  It's really doing the same thing as
comment|// our hitQueue...
DECL|method|nearest
specifier|public
specifier|static
name|NearestHit
index|[]
name|nearest
parameter_list|(
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|,
name|List
argument_list|<
name|BKDReader
argument_list|>
name|readers
parameter_list|,
name|List
argument_list|<
name|Bits
argument_list|>
name|liveDocs
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|docBases
parameter_list|,
specifier|final
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("NEAREST: readers=" + readers + " liveDocs=" + liveDocs + " pointLat=" + pointLat + " pointLon=" + pointLon);
comment|// Holds closest collected points seen so far:
comment|// TODO: if we used lucene's PQ we could just updateTop instead of poll/offer:
specifier|final
name|PriorityQueue
argument_list|<
name|NearestHit
argument_list|>
name|hitQueue
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|(
name|n
argument_list|,
operator|new
name|Comparator
argument_list|<
name|NearestHit
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NearestHit
name|a
parameter_list|,
name|NearestHit
name|b
parameter_list|)
block|{
comment|// sort by opposite distanceMeters natural order
name|int
name|cmp
init|=
name|Double
operator|.
name|compare
argument_list|(
name|a
operator|.
name|distanceMeters
argument_list|,
name|b
operator|.
name|distanceMeters
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
operator|-
name|cmp
return|;
block|}
comment|// tie-break by higher docID:
return|return
name|b
operator|.
name|docID
operator|-
name|a
operator|.
name|docID
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Holds all cells, sorted by closest to the point:
name|PriorityQueue
argument_list|<
name|Cell
argument_list|>
name|cellQueue
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|()
decl_stmt|;
name|NearestVisitor
name|visitor
init|=
operator|new
name|NearestVisitor
argument_list|(
name|hitQueue
argument_list|,
name|n
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BKDReader
operator|.
name|IntersectState
argument_list|>
name|states
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Add root cell for each reader into the queue:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BKDReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|byte
index|[]
name|minPackedValue
init|=
name|reader
operator|.
name|getMinPackedValue
argument_list|()
decl_stmt|;
name|byte
index|[]
name|maxPackedValue
init|=
name|reader
operator|.
name|getMaxPackedValue
argument_list|()
decl_stmt|;
name|states
operator|.
name|add
argument_list|(
name|reader
operator|.
name|getIntersectState
argument_list|(
name|visitor
argument_list|)
argument_list|)
expr_stmt|;
name|cellQueue
operator|.
name|offer
argument_list|(
operator|new
name|Cell
argument_list|(
name|i
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|getMinPackedValue
argument_list|()
argument_list|,
name|reader
operator|.
name|getMaxPackedValue
argument_list|()
argument_list|,
name|approxBestDistance
argument_list|(
name|minPackedValue
argument_list|,
name|maxPackedValue
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|cellQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Cell
name|cell
init|=
name|cellQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
comment|//System.out.println("  visit " + cell);
comment|// TODO: if we replace approxBestDistance with actualBestDistance, we can put an opto here to break once this "best" cell is fully outside of the hitQueue bottom's radius:
name|BKDReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|cell
operator|.
name|readerIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|isLeafNode
argument_list|(
name|cell
operator|.
name|nodeID
argument_list|)
condition|)
block|{
comment|//System.out.println("    leaf");
comment|// Leaf block: visit all points and possibly collect them:
name|visitor
operator|.
name|curDocBase
operator|=
name|docBases
operator|.
name|get
argument_list|(
name|cell
operator|.
name|readerIndex
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|curLiveDocs
operator|=
name|liveDocs
operator|.
name|get
argument_list|(
name|cell
operator|.
name|readerIndex
argument_list|)
expr_stmt|;
name|reader
operator|.
name|visitLeafBlockValues
argument_list|(
name|cell
operator|.
name|nodeID
argument_list|,
name|states
operator|.
name|get
argument_list|(
name|cell
operator|.
name|readerIndex
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println("    now " + hitQueue.size() + " hits");
block|}
else|else
block|{
comment|//System.out.println("    non-leaf");
comment|// Non-leaf block: split into two cells and put them back into the queue:
name|double
name|cellMinLat
init|=
name|decodeLatitude
argument_list|(
name|cell
operator|.
name|minPacked
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|cellMinLon
init|=
name|decodeLongitude
argument_list|(
name|cell
operator|.
name|minPacked
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|cellMaxLat
init|=
name|decodeLatitude
argument_list|(
name|cell
operator|.
name|maxPacked
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|cellMaxLon
init|=
name|decodeLongitude
argument_list|(
name|cell
operator|.
name|maxPacked
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
name|cellMaxLat
operator|<
name|visitor
operator|.
name|minLat
operator|||
name|visitor
operator|.
name|maxLat
operator|<
name|cellMinLat
operator|||
operator|(
operator|(
name|cellMaxLon
operator|<
name|visitor
operator|.
name|minLon
operator|||
name|visitor
operator|.
name|maxLon
operator|<
name|cellMinLon
operator|)
operator|&&
name|cellMaxLon
operator|<
name|visitor
operator|.
name|minLon2
operator|)
condition|)
block|{
comment|// this cell is outside our search bbox; don't bother exploring any more
continue|continue;
block|}
name|byte
index|[]
name|splitPackedValue
init|=
name|cell
operator|.
name|maxPacked
operator|.
name|clone
argument_list|()
decl_stmt|;
name|reader
operator|.
name|copySplitValue
argument_list|(
name|cell
operator|.
name|nodeID
argument_list|,
name|splitPackedValue
argument_list|)
expr_stmt|;
name|cellQueue
operator|.
name|offer
argument_list|(
operator|new
name|Cell
argument_list|(
name|cell
operator|.
name|readerIndex
argument_list|,
literal|2
operator|*
name|cell
operator|.
name|nodeID
argument_list|,
name|cell
operator|.
name|minPacked
argument_list|,
name|splitPackedValue
argument_list|,
name|approxBestDistance
argument_list|(
name|cell
operator|.
name|minPacked
argument_list|,
name|splitPackedValue
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|splitPackedValue
operator|=
name|cell
operator|.
name|minPacked
operator|.
name|clone
argument_list|()
expr_stmt|;
name|reader
operator|.
name|copySplitValue
argument_list|(
name|cell
operator|.
name|nodeID
argument_list|,
name|splitPackedValue
argument_list|)
expr_stmt|;
name|cellQueue
operator|.
name|offer
argument_list|(
operator|new
name|Cell
argument_list|(
name|cell
operator|.
name|readerIndex
argument_list|,
literal|2
operator|*
name|cell
operator|.
name|nodeID
operator|+
literal|1
argument_list|,
name|splitPackedValue
argument_list|,
name|cell
operator|.
name|maxPacked
argument_list|,
name|approxBestDistance
argument_list|(
name|splitPackedValue
argument_list|,
name|cell
operator|.
name|maxPacked
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|NearestHit
index|[]
name|hits
init|=
operator|new
name|NearestHit
index|[
name|hitQueue
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|downTo
init|=
name|hitQueue
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|hitQueue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|hits
index|[
name|downTo
index|]
operator|=
name|hitQueue
operator|.
name|poll
argument_list|()
expr_stmt|;
name|downTo
operator|--
expr_stmt|;
block|}
return|return
name|hits
return|;
block|}
comment|// NOTE: incoming args never cross the dateline, since they are a BKD cell
DECL|method|approxBestDistance
specifier|private
specifier|static
name|double
name|approxBestDistance
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
name|double
name|minLat
init|=
name|decodeLatitude
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|minLon
init|=
name|decodeLongitude
argument_list|(
name|minPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|maxLat
init|=
name|decodeLatitude
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|maxLon
init|=
name|decodeLongitude
argument_list|(
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
return|return
name|approxBestDistance
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|,
name|pointLat
argument_list|,
name|pointLon
argument_list|)
return|;
block|}
comment|// NOTE: incoming args never cross the dateline, since they are a BKD cell
DECL|method|approxBestDistance
specifier|private
specifier|static
name|double
name|approxBestDistance
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|,
name|double
name|pointLat
parameter_list|,
name|double
name|pointLon
parameter_list|)
block|{
comment|// TODO: can we make this the trueBestDistance?  I.e., minimum distance between the point and ANY point on the box?  we can speed things
comment|// up if so, but not enrolling any BKD cell whose true best distance is> bottom of the current hit queue
if|if
condition|(
name|pointLat
operator|>=
name|minLat
operator|&&
name|pointLat
operator|<=
name|maxLat
operator|&&
name|pointLon
operator|>=
name|minLon
operator|&&
name|pointLon
operator|<=
name|maxLon
condition|)
block|{
comment|// point is inside the cell!
return|return
literal|0.0
return|;
block|}
name|double
name|d1
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|minLat
argument_list|,
name|minLon
argument_list|)
decl_stmt|;
name|double
name|d2
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
name|double
name|d3
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|maxLat
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
name|double
name|d4
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|pointLat
argument_list|,
name|pointLon
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d1
argument_list|,
name|d2
argument_list|)
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|d3
argument_list|,
name|d4
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

