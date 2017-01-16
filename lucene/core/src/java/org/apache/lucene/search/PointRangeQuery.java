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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|document
operator|.
name|IntPoint
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|LeafReader
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
name|LeafReaderContext
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
name|DocIdSetBuilder
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

begin_comment
comment|/**   * Abstract class for range queries against single or multidimensional points such as  * {@link IntPoint}.  *<p>  * This is for subclasses and works on the underlying binary encoding: to  * create range queries for lucene's standard {@code Point} types, refer to factory  * methods on those classes, e.g. {@link IntPoint#newRangeQuery IntPoint.newRangeQuery()} for   * fields indexed with {@link IntPoint}.  *<p>  * For a single-dimensional field this query is a simple range query; in a multi-dimensional field it's a box shape.  * @see PointValues  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PointRangeQuery
specifier|public
specifier|abstract
class|class
name|PointRangeQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|numDims
specifier|final
name|int
name|numDims
decl_stmt|;
DECL|field|bytesPerDim
specifier|final
name|int
name|bytesPerDim
decl_stmt|;
DECL|field|lowerPoint
specifier|final
name|byte
index|[]
name|lowerPoint
decl_stmt|;
DECL|field|upperPoint
specifier|final
name|byte
index|[]
name|upperPoint
decl_stmt|;
comment|/**     * Expert: create a multidimensional range query for point values.    *    * @param field field name. must not be {@code null}.    * @param lowerPoint lower portion of the range (inclusive).    * @param upperPoint upper portion of the range (inclusive).    * @param numDims number of dimensions.    * @throws IllegalArgumentException if {@code field} is null, or if {@code lowerValue.length != upperValue.length}    */
DECL|method|PointRangeQuery
specifier|protected
name|PointRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|lowerPoint
parameter_list|,
name|byte
index|[]
name|upperPoint
parameter_list|,
name|int
name|numDims
parameter_list|)
block|{
name|checkArgs
argument_list|(
name|field
argument_list|,
name|lowerPoint
argument_list|,
name|upperPoint
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
if|if
condition|(
name|numDims
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numDims must be positive, got "
operator|+
name|numDims
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerPoint
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerPoint has length of zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerPoint
operator|.
name|length
operator|%
name|numDims
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerPoint is not a fixed multiple of numDims"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerPoint
operator|.
name|length
operator|!=
name|upperPoint
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerPoint has length="
operator|+
name|lowerPoint
operator|.
name|length
operator|+
literal|" but upperPoint has different length="
operator|+
name|upperPoint
operator|.
name|length
argument_list|)
throw|;
block|}
name|this
operator|.
name|numDims
operator|=
name|numDims
expr_stmt|;
name|this
operator|.
name|bytesPerDim
operator|=
name|lowerPoint
operator|.
name|length
operator|/
name|numDims
expr_stmt|;
name|this
operator|.
name|lowerPoint
operator|=
name|lowerPoint
expr_stmt|;
name|this
operator|.
name|upperPoint
operator|=
name|upperPoint
expr_stmt|;
block|}
comment|/**     * Check preconditions for all factory methods    * @throws IllegalArgumentException if {@code field}, {@code lowerPoint} or {@code upperPoint} are null.    */
DECL|method|checkArgs
specifier|public
specifier|static
name|void
name|checkArgs
parameter_list|(
name|String
name|field
parameter_list|,
name|Object
name|lowerPoint
parameter_list|,
name|Object
name|upperPoint
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerPoint
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerPoint must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|upperPoint
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"upperPoint must not be null"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
specifier|final
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We don't use RandomAccessWeight here: it's no good to approximate with "match all docs".
comment|// This is an inverted structure and should be used in the first pass:
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|,
name|boost
argument_list|)
block|{
specifier|private
name|IntersectVisitor
name|getIntersectVisitor
parameter_list|(
name|DocIdSetBuilder
name|result
parameter_list|)
block|{
return|return
operator|new
name|IntersectVisitor
argument_list|()
block|{
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|adder
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|adder
operator|=
name|result
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|adder
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|offset
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|packedValue
argument_list|,
name|offset
argument_list|,
name|lowerPoint
argument_list|,
name|offset
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// Doc's value is too low, in this dimension
return|return;
block|}
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|packedValue
argument_list|,
name|offset
argument_list|,
name|upperPoint
argument_list|,
name|offset
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// Doc's value is too high, in this dimension
return|return;
block|}
block|}
comment|// Doc is in-bounds
name|adder
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|boolean
name|crosses
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
name|int
name|offset
init|=
name|dim
operator|*
name|bytesPerDim
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|minPackedValue
argument_list|,
name|offset
argument_list|,
name|upperPoint
argument_list|,
name|offset
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|maxPackedValue
argument_list|,
name|offset
argument_list|,
name|lowerPoint
argument_list|,
name|offset
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
name|crosses
operator||=
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|minPackedValue
argument_list|,
name|offset
argument_list|,
name|lowerPoint
argument_list|,
name|offset
argument_list|)
operator|<
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|maxPackedValue
argument_list|,
name|offset
argument_list|,
name|upperPoint
argument_list|,
name|offset
argument_list|)
operator|>
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|crosses
condition|)
block|{
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
else|else
block|{
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ScorerSupplier
name|scorerSupplier
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|PointValues
name|values
init|=
name|reader
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
comment|// No docs in this segment/field indexed any points
return|return
literal|null
return|;
block|}
if|if
condition|(
name|values
operator|.
name|getNumDimensions
argument_list|()
operator|!=
name|numDims
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|field
operator|+
literal|"\" was indexed with numDims="
operator|+
name|values
operator|.
name|getNumDimensions
argument_list|()
operator|+
literal|" but this query has numDims="
operator|+
name|numDims
argument_list|)
throw|;
block|}
if|if
condition|(
name|bytesPerDim
operator|!=
name|values
operator|.
name|getBytesPerDimension
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|field
operator|+
literal|"\" was indexed with bytesPerDim="
operator|+
name|values
operator|.
name|getBytesPerDimension
argument_list|()
operator|+
literal|" but this query has bytesPerDim="
operator|+
name|bytesPerDim
argument_list|)
throw|;
block|}
name|boolean
name|allDocsMatch
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|getDocCount
argument_list|()
operator|==
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
specifier|final
name|byte
index|[]
name|fieldPackedLower
init|=
name|values
operator|.
name|getMinPackedValue
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|fieldPackedUpper
init|=
name|values
operator|.
name|getMaxPackedValue
argument_list|()
decl_stmt|;
name|allDocsMatch
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDims
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
name|bytesPerDim
decl_stmt|;
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|lowerPoint
argument_list|,
name|offset
argument_list|,
name|fieldPackedLower
argument_list|,
name|offset
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|upperPoint
argument_list|,
name|offset
argument_list|,
name|fieldPackedUpper
argument_list|,
name|offset
argument_list|)
operator|<
literal|0
condition|)
block|{
name|allDocsMatch
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
name|allDocsMatch
operator|=
literal|false
expr_stmt|;
block|}
specifier|final
name|Weight
name|weight
init|=
name|this
decl_stmt|;
if|if
condition|(
name|allDocsMatch
condition|)
block|{
comment|// all docs have a value and all points are within bounds, so everything matches
return|return
operator|new
name|ScorerSupplier
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|get
parameter_list|(
name|boolean
name|randomAccess
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|weight
argument_list|,
name|score
argument_list|()
argument_list|,
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|reader
operator|.
name|maxDoc
argument_list|()
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|ScorerSupplier
argument_list|()
block|{
specifier|final
name|DocIdSetBuilder
name|result
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|values
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|IntersectVisitor
name|visitor
init|=
name|getIntersectVisitor
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|long
name|cost
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Scorer
name|get
parameter_list|(
name|boolean
name|randomAccess
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|.
name|intersect
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iterator
init|=
name|result
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|weight
argument_list|,
name|score
argument_list|()
argument_list|,
name|iterator
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
if|if
condition|(
name|cost
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Computing the cost may be expensive, so only do it if necessary
name|cost
operator|=
name|values
operator|.
name|estimatePointCount
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
assert|assert
name|cost
operator|>=
literal|0
assert|;
block|}
return|return
name|cost
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ScorerSupplier
name|scorerSupplier
init|=
name|scorerSupplier
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorerSupplier
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|scorerSupplier
operator|.
name|get
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getNumDims
specifier|public
name|int
name|getNumDims
parameter_list|()
block|{
return|return
name|numDims
return|;
block|}
DECL|method|getBytesPerDim
specifier|public
name|int
name|getBytesPerDim
parameter_list|()
block|{
return|return
name|bytesPerDim
return|;
block|}
DECL|method|getLowerPoint
specifier|public
name|byte
index|[]
name|getLowerPoint
parameter_list|()
block|{
return|return
name|lowerPoint
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getUpperPoint
specifier|public
name|byte
index|[]
name|getUpperPoint
parameter_list|()
block|{
return|return
name|upperPoint
operator|.
name|clone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|classHash
argument_list|()
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|lowerPoint
argument_list|)
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|upperPoint
argument_list|)
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|numDims
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
name|Objects
operator|.
name|hashCode
argument_list|(
name|bytesPerDim
argument_list|)
expr_stmt|;
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|o
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|o
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|PointRangeQuery
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|field
argument_list|,
name|other
operator|.
name|field
argument_list|)
operator|&&
name|numDims
operator|==
name|other
operator|.
name|numDims
operator|&&
name|bytesPerDim
operator|==
name|other
operator|.
name|bytesPerDim
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|lowerPoint
argument_list|,
name|other
operator|.
name|lowerPoint
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|upperPoint
argument_list|,
name|other
operator|.
name|upperPoint
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
comment|// print ourselves as "range per dimension"
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDims
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|int
name|startOffset
init|=
name|bytesPerDim
operator|*
name|i
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|lowerPoint
argument_list|,
name|startOffset
argument_list|,
name|startOffset
operator|+
name|bytesPerDim
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|upperPoint
argument_list|,
name|startOffset
argument_list|,
name|startOffset
operator|+
name|bytesPerDim
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a string of a single value in a human-readable format for debugging.    * This is used by {@link #toString()}.    *    * @param dimension dimension of the particular value    * @param value single value, never null    * @return human readable value for debugging    */
DECL|method|toString
specifier|protected
specifier|abstract
name|String
name|toString
parameter_list|(
name|int
name|dimension
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
function_decl|;
block|}
end_class

end_unit

