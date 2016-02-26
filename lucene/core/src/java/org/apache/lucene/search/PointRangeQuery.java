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
name|BinaryPoint
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
name|document
operator|.
name|DoublePoint
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
name|document
operator|.
name|FloatPoint
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
name|document
operator|.
name|LongPoint
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
name|FieldInfo
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
name|NumericUtils
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
comment|/**   * Abstract class for range queries against single or multidimensional points such as  * {@link IntPoint}.  *<p>  * This is for subclasses and works on the underlying binary encoding: to  * create range queries for lucene's standard {@code Point} types, refer to factory  * methods on those classes, e.g. {@link IntPoint#newRangeQuery IntPoint.newRangeQuery()} for   * fields indexed with {@link IntPoint}.  *<p>  * For a single-dimensional field this query is a simple range query; in a multi-dimensional field it's a box shape.  * @see IntPoint  * @see LongPoint  * @see FloatPoint  * @see DoublePoint  * @see BinaryPoint   *  * @lucene.experimental  */
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
DECL|field|lowerPoint
specifier|final
name|byte
index|[]
index|[]
name|lowerPoint
decl_stmt|;
DECL|field|lowerInclusive
specifier|final
name|boolean
index|[]
name|lowerInclusive
decl_stmt|;
DECL|field|upperPoint
specifier|final
name|byte
index|[]
index|[]
name|upperPoint
decl_stmt|;
DECL|field|upperInclusive
specifier|final
name|boolean
index|[]
name|upperInclusive
decl_stmt|;
comment|// This is null only in the "fully open range" case
DECL|field|bytesPerDim
specifier|final
name|Integer
name|bytesPerDim
decl_stmt|;
comment|/**     * Expert: create a multidimensional range query for point values.    *<p>    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting a {@code lowerValue} element or {@code upperValue} element to {@code null}.     *<p>    * By setting a dimension's inclusive ({@code lowerInclusive} or {@code upperInclusive}) to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    *    * @param field field name. must not be {@code null}.    * @param lowerPoint lower portion of the range. {@code null} values mean "open" for that dimension.    * @param lowerInclusive {@code true} if the lower portion of the range is inclusive, {@code false} if it should be excluded.    * @param upperPoint upper portion of the range. {@code null} values mean "open" for that dimension.    * @param upperInclusive {@code true} if the upper portion of the range is inclusive, {@code false} if it should be excluded.    * @throws IllegalArgumentException if {@code field} is null, or if {@code lowerValue.length != upperValue.length}    */
DECL|method|PointRangeQuery
specifier|protected
name|PointRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
index|[]
name|lowerPoint
parameter_list|,
name|boolean
index|[]
name|lowerInclusive
parameter_list|,
name|byte
index|[]
index|[]
name|upperPoint
parameter_list|,
name|boolean
index|[]
name|upperInclusive
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
name|numDims
operator|=
name|lowerPoint
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|upperPoint
operator|.
name|length
operator|!=
name|numDims
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerPoint has length="
operator|+
name|numDims
operator|+
literal|" but upperPoint has different length="
operator|+
name|upperPoint
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerInclusive
operator|.
name|length
operator|!=
name|numDims
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lowerInclusive has length="
operator|+
name|lowerInclusive
operator|.
name|length
operator|+
literal|" but expected="
operator|+
name|numDims
argument_list|)
throw|;
block|}
if|if
condition|(
name|upperInclusive
operator|.
name|length
operator|!=
name|numDims
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"upperInclusive has length="
operator|+
name|upperInclusive
operator|.
name|length
operator|+
literal|" but expected="
operator|+
name|numDims
argument_list|)
throw|;
block|}
name|this
operator|.
name|lowerPoint
operator|=
name|lowerPoint
expr_stmt|;
name|this
operator|.
name|lowerInclusive
operator|=
name|lowerInclusive
expr_stmt|;
name|this
operator|.
name|upperPoint
operator|=
name|upperPoint
expr_stmt|;
name|this
operator|.
name|upperInclusive
operator|=
name|upperInclusive
expr_stmt|;
name|int
name|bytesPerDim
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|value
range|:
name|lowerPoint
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
name|bytesPerDim
operator|=
name|value
operator|.
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|length
operator|!=
name|bytesPerDim
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all dimensions must have same bytes length, but saw "
operator|+
name|bytesPerDim
operator|+
literal|" and "
operator|+
name|value
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
block|}
for|for
control|(
name|byte
index|[]
name|value
range|:
name|upperPoint
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
name|bytesPerDim
operator|=
name|value
operator|.
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|length
operator|!=
name|bytesPerDim
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all dimensions must have same bytes length, but saw "
operator|+
name|bytesPerDim
operator|+
literal|" and "
operator|+
name|value
operator|.
name|length
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|bytesPerDim
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|bytesPerDim
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|bytesPerDim
operator|=
name|bytesPerDim
expr_stmt|;
block|}
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
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
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
argument_list|)
block|{
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
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed any points
return|return
literal|null
return|;
block|}
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed this field at all
return|return
literal|null
return|;
block|}
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
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
name|fieldInfo
operator|.
name|getPointDimensionCount
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
literal|null
operator|&&
name|bytesPerDim
operator|.
name|intValue
argument_list|()
operator|!=
name|fieldInfo
operator|.
name|getPointNumBytes
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
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
operator|+
literal|" but this query has bytesPerDim="
operator|+
name|bytesPerDim
argument_list|)
throw|;
block|}
name|int
name|bytesPerDim
init|=
name|fieldInfo
operator|.
name|getPointNumBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|packedLowerIncl
init|=
operator|new
name|byte
index|[
name|numDims
operator|*
name|bytesPerDim
index|]
decl_stmt|;
name|byte
index|[]
name|packedUpperIncl
init|=
operator|new
name|byte
index|[
name|numDims
operator|*
name|bytesPerDim
index|]
decl_stmt|;
name|byte
index|[]
name|minValue
init|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
decl_stmt|;
name|byte
index|[]
name|maxValue
init|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|maxValue
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|)
expr_stmt|;
name|byte
index|[]
name|one
init|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
decl_stmt|;
name|one
index|[
name|bytesPerDim
operator|-
literal|1
index|]
operator|=
literal|1
expr_stmt|;
comment|// Carefully pack lower and upper bounds, taking care of per-dim inclusive:
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
if|if
condition|(
name|lowerPoint
index|[
name|dim
index|]
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|lowerInclusive
index|[
name|dim
index|]
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|lowerPoint
index|[
name|dim
index|]
argument_list|,
name|maxValue
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|add
argument_list|(
name|bytesPerDim
argument_list|,
literal|0
argument_list|,
name|lowerPoint
index|[
name|dim
index|]
argument_list|,
name|one
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|packedLowerIncl
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|lowerPoint
index|[
name|dim
index|]
argument_list|,
literal|0
argument_list|,
name|packedLowerIncl
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Open-ended range: we just leave 0s in this packed dim for the lower value
block|}
if|if
condition|(
name|upperPoint
index|[
name|dim
index|]
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|upperInclusive
index|[
name|dim
index|]
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|upperPoint
index|[
name|dim
index|]
argument_list|,
name|minValue
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|subtract
argument_list|(
name|bytesPerDim
argument_list|,
literal|0
argument_list|,
name|upperPoint
index|[
name|dim
index|]
argument_list|,
name|one
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|packedUpperIncl
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|upperPoint
index|[
name|dim
index|]
argument_list|,
literal|0
argument_list|,
name|packedUpperIncl
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Open-ended range: fill with max point for this dim:
name|System
operator|.
name|arraycopy
argument_list|(
name|maxValue
argument_list|,
literal|0
argument_list|,
name|packedUpperIncl
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now packedLowerIncl and packedUpperIncl are inclusive, and non-empty space:
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
argument_list|)
decl_stmt|;
name|int
index|[]
name|hitCount
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|values
operator|.
name|intersect
argument_list|(
name|field
argument_list|,
operator|new
name|IntersectVisitor
argument_list|()
block|{
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
name|hitCount
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|result
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
name|packedLowerIncl
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
name|packedUpperIncl
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
name|hitCount
index|[
literal|0
index|]
operator|++
expr_stmt|;
name|result
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
name|packedUpperIncl
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
name|packedLowerIncl
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
name|packedLowerIncl
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
name|packedUpperIncl
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
argument_list|)
expr_stmt|;
comment|// NOTE: hitCount[0] will be over-estimate in multi-valued case
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|result
operator|.
name|build
argument_list|(
name|hitCount
index|[
literal|0
index|]
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|lowerInclusive
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
name|upperInclusive
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
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
block|{
specifier|final
name|PointRangeQuery
name|q
init|=
operator|(
name|PointRangeQuery
operator|)
name|other
decl_stmt|;
return|return
name|q
operator|.
name|numDims
operator|==
name|numDims
operator|&&
name|q
operator|.
name|bytesPerDim
operator|==
name|bytesPerDim
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|lowerPoint
argument_list|,
name|q
operator|.
name|lowerPoint
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|lowerInclusive
argument_list|,
name|q
operator|.
name|lowerInclusive
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|upperPoint
argument_list|,
name|q
operator|.
name|upperPoint
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|upperInclusive
argument_list|,
name|q
operator|.
name|upperInclusive
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
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
if|if
condition|(
name|lowerInclusive
index|[
name|i
index|]
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lowerPoint
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|i
argument_list|,
name|lowerPoint
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
if|if
condition|(
name|upperPoint
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|i
argument_list|,
name|upperPoint
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upperInclusive
index|[
name|i
index|]
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
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

