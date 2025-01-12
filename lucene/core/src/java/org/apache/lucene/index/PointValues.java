begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|BinaryPoint
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
name|DoublePoint
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
name|document
operator|.
name|FloatPoint
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
name|IntPoint
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
name|LongPoint
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
name|DocIdSetIterator
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
name|StringHelper
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
name|BKDWriter
import|;
end_import

begin_comment
comment|/**   * Access to indexed numeric values.  *<p>  * Points represent numeric values and are indexed differently than ordinary text. Instead of an inverted index,   * points are indexed with datastructures such as<a href="https://en.wikipedia.org/wiki/K-d_tree">KD-trees</a>.   * These structures are optimized for operations such as<i>range</i>,<i>distance</i>,<i>nearest-neighbor</i>,   * and<i>point-in-polygon</i> queries.   *<h1>Basic Point Types</h1>  *<table summary="Basic point types in Java and Lucene">  *<tr><th>Java type</th><th>Lucene class</th></tr>  *<tr><td>{@code int}</td><td>{@link IntPoint}</td></tr>  *<tr><td>{@code long}</td><td>{@link LongPoint}</td></tr>  *<tr><td>{@code float}</td><td>{@link FloatPoint}</td></tr>  *<tr><td>{@code double}</td><td>{@link DoublePoint}</td></tr>  *<tr><td>{@code byte[]}</td><td>{@link BinaryPoint}</td></tr>  *<tr><td>{@link BigInteger}</td><td><a href="{@docRoot}/../sandbox/org/apache/lucene/document/BigIntegerPoint.html">BigIntegerPoint</a>*</td></tr>  *<tr><td>{@link InetAddress}</td><td><a href="{@docRoot}/../misc/org/apache/lucene/document/InetAddressPoint.html">InetAddressPoint</a>*</td></tr>  *</table>  * * in the<i>lucene-sandbox</i> jar<br>  *<p>  * Basic Lucene point types behave like their java peers: for example {@link IntPoint} represents a signed 32-bit   * {@link Integer}, supporting values ranging from {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}, ordered  * consistent with {@link Integer#compareTo(Integer)}. In addition to indexing support, point classes also contain   * static methods (such as {@link IntPoint#newRangeQuery(String, int, int)}) for creating common queries. For example:  *<pre class="prettyprint">  *   // add year 1970 to document  *   document.add(new IntPoint("year", 1970));  *   // index document  *   writer.addDocument(document);  *   ...  *   // issue range query of 1960-1980  *   Query query = IntPoint.newRangeQuery("year", 1960, 1980);  *   TopDocs docs = searcher.search(query, ...);  *</pre>  *<h1>Geospatial Point Types</h1>  * Although basic point types such as {@link DoublePoint} support points in multi-dimensional space too, Lucene has  * specialized classes for location data. These classes are optimized for location data: they are more space-efficient and   * support special operations such as<i>distance</i> and<i>polygon</i> queries. There are currently two implementations:  *<br>  *<ol>  *<li><a href="{@docRoot}/../sandbox/org/apache/lucene/document/LatLonPoint.html">LatLonPoint</a> in<i>lucene-sandbox</i>: indexes {@code (latitude,longitude)} as {@code (x,y)} in two-dimensional space.  *<li><a href="{@docRoot}/../spatial3d/org/apache/lucene/spatial3d/Geo3DPoint.html">Geo3DPoint</a>* in<i>lucene-spatial3d</i>: indexes {@code (latitude,longitude)} as {@code (x,y,z)} in three-dimensional space.  *</ol>  * * does<b>not</b> support altitude, 3D here means "uses three dimensions under-the-hood"<br>  *<h1>Advanced usage</h1>  * Custom structures can be created on top of single- or multi- dimensional basic types, on top of   * {@link BinaryPoint} for more flexibility, or via custom {@link Field} subclasses.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|PointValues
specifier|public
specifier|abstract
class|class
name|PointValues
block|{
comment|/** Maximum number of bytes for each dimension */
DECL|field|MAX_NUM_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|MAX_NUM_BYTES
init|=
literal|16
decl_stmt|;
comment|/** Maximum number of dimensions */
DECL|field|MAX_DIMENSIONS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_DIMENSIONS
init|=
name|BKDWriter
operator|.
name|MAX_DIMS
decl_stmt|;
comment|/** Return the cumulated number of points across all leaves of the given    * {@link IndexReader}. Leaves that do not have points for the given field    * are ignored.    *  @see PointValues#size() */
DECL|method|size
specifier|public
specifier|static
name|long
name|size
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|PointValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|size
operator|+=
name|values
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
comment|/** Return the cumulated number of docs that have points across all leaves    * of the given {@link IndexReader}. Leaves that do not have points for the    * given field are ignored.    *  @see PointValues#getDocCount() */
DECL|method|getDocCount
specifier|public
specifier|static
name|int
name|getDocCount
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|PointValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|count
operator|+=
name|values
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/** Return the minimum packed values across all leaves of the given    * {@link IndexReader}. Leaves that do not have points for the given field    * are ignored.    *  @see PointValues#getMinPackedValue() */
DECL|method|getMinPackedValue
specifier|public
specifier|static
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|minValue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|PointValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|byte
index|[]
name|leafMinValue
init|=
name|values
operator|.
name|getMinPackedValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|leafMinValue
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|minValue
operator|==
literal|null
condition|)
block|{
name|minValue
operator|=
name|leafMinValue
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|numDimensions
init|=
name|values
operator|.
name|getNumDimensions
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numBytesPerDimension
init|=
name|values
operator|.
name|getBytesPerDimension
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
name|numDimensions
condition|;
operator|++
name|i
control|)
block|{
name|int
name|offset
init|=
name|i
operator|*
name|numBytesPerDimension
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|numBytesPerDimension
argument_list|,
name|leafMinValue
argument_list|,
name|offset
argument_list|,
name|minValue
argument_list|,
name|offset
argument_list|)
operator|<
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|leafMinValue
argument_list|,
name|offset
argument_list|,
name|minValue
argument_list|,
name|offset
argument_list|,
name|numBytesPerDimension
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|minValue
return|;
block|}
comment|/** Return the maximum packed values across all leaves of the given    * {@link IndexReader}. Leaves that do not have points for the given field    * are ignored.    *  @see PointValues#getMaxPackedValue() */
DECL|method|getMaxPackedValue
specifier|public
specifier|static
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|maxValue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|PointValues
name|values
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getPointValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|byte
index|[]
name|leafMaxValue
init|=
name|values
operator|.
name|getMaxPackedValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|leafMaxValue
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|maxValue
operator|==
literal|null
condition|)
block|{
name|maxValue
operator|=
name|leafMaxValue
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|numDimensions
init|=
name|values
operator|.
name|getNumDimensions
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numBytesPerDimension
init|=
name|values
operator|.
name|getBytesPerDimension
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
name|numDimensions
condition|;
operator|++
name|i
control|)
block|{
name|int
name|offset
init|=
name|i
operator|*
name|numBytesPerDimension
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|numBytesPerDimension
argument_list|,
name|leafMaxValue
argument_list|,
name|offset
argument_list|,
name|maxValue
argument_list|,
name|offset
argument_list|)
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|leafMaxValue
argument_list|,
name|offset
argument_list|,
name|maxValue
argument_list|,
name|offset
argument_list|,
name|numBytesPerDimension
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|maxValue
return|;
block|}
comment|/** Default constructor */
DECL|method|PointValues
specifier|protected
name|PointValues
parameter_list|()
block|{   }
comment|/** Used by {@link #intersect} to check how each recursive cell corresponds to the query. */
DECL|enum|Relation
specifier|public
enum|enum
name|Relation
block|{
comment|/** Return this if the cell is fully contained by the query */
DECL|enum constant|CELL_INSIDE_QUERY
name|CELL_INSIDE_QUERY
block|,
comment|/** Return this if the cell and query do not overlap */
DECL|enum constant|CELL_OUTSIDE_QUERY
name|CELL_OUTSIDE_QUERY
block|,
comment|/** Return this if the cell partially overlaps the query */
DECL|enum constant|CELL_CROSSES_QUERY
name|CELL_CROSSES_QUERY
block|}
empty_stmt|;
comment|/** We recurse the BKD tree, using a provided instance of this to guide the recursion.    *    * @lucene.experimental */
DECL|interface|IntersectVisitor
specifier|public
interface|interface
name|IntersectVisitor
block|{
comment|/** Called for all documents in a leaf cell that's fully contained by the query.  The      *  consumer should blindly accept the docID. */
DECL|method|visit
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called for all documents in a leaf cell that crosses the query.  The consumer      *  should scrutinize the packedValue to decide whether to accept it.  In the 1D case,      *  values are visited in increasing order, and in the case of ties, in increasing      *  docID order. */
DECL|method|visit
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
throws|throws
name|IOException
function_decl|;
comment|/** Called for non-leaf cells to test how the cell relates to the query, to      *  determine how to further recurse down the tree. */
DECL|method|compare
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
function_decl|;
comment|/** Notifies the caller that this many documents are about to be visited */
DECL|method|grow
specifier|default
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{}
empty_stmt|;
block|}
comment|/** Finds all documents and points matching the provided visitor.    *  This method does not enforce live documents, so it's up to the caller    *  to test whether each document is deleted, if necessary. */
DECL|method|intersect
specifier|public
specifier|abstract
name|void
name|intersect
parameter_list|(
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Estimate the number of points that would be visited by {@link #intersect}    * with the given {@link IntersectVisitor}. This should run many times faster    * than {@link #intersect(IntersectVisitor)}.    * @see DocIdSetIterator#cost */
DECL|method|estimatePointCount
specifier|public
specifier|abstract
name|long
name|estimatePointCount
parameter_list|(
name|IntersectVisitor
name|visitor
parameter_list|)
function_decl|;
comment|/** Returns minimum value for each dimension, packed, or null if {@link #size} is<code>0</code> */
DECL|method|getMinPackedValue
specifier|public
specifier|abstract
name|byte
index|[]
name|getMinPackedValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns maximum value for each dimension, packed, or null if {@link #size} is<code>0</code> */
DECL|method|getMaxPackedValue
specifier|public
specifier|abstract
name|byte
index|[]
name|getMaxPackedValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns how many dimensions were indexed */
DECL|method|getNumDimensions
specifier|public
specifier|abstract
name|int
name|getNumDimensions
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of bytes per dimension */
DECL|method|getBytesPerDimension
specifier|public
specifier|abstract
name|int
name|getBytesPerDimension
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the total number of indexed points across all documents. */
DECL|method|size
specifier|public
specifier|abstract
name|long
name|size
parameter_list|()
function_decl|;
comment|/** Returns the total number of documents that have indexed at least one point. */
DECL|method|getDocCount
specifier|public
specifier|abstract
name|int
name|getDocCount
parameter_list|()
function_decl|;
block|}
end_class

end_unit

