begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.range
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|range
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DoubleDocValuesField
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
name|FloatDocValuesField
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
name|facet
operator|.
name|Facets
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
name|facet
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|FacetsCollector
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DoubleFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|FloatFieldSource
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
name|search
operator|.
name|DocIdSet
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
name|Filter
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
name|NumericUtils
import|;
end_import

begin_comment
comment|/** {@link Facets} implementation that computes counts for  *  dynamic double ranges from a provided {@link  *  ValueSource}, using {@link FunctionValues#doubleVal}.  Use  *  this for dimensions that change in real-time (e.g. a  *  relative time based dimension like "Past day", "Past 2  *  days", etc.) or that change for each request (e.g.  *  distance from the user's location, "< 1 km", "< 2 km",  *  etc.).  *  *<p> If you had indexed your field using {@link  *  FloatDocValuesField} then pass {@link FloatFieldSource}  *  as the {@link ValueSource}; if you used {@link  *  DoubleDocValuesField} then pass {@link  *  DoubleFieldSource} (this is the default used when you  *  pass just a the field name).  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|DoubleRangeFacetCounts
specifier|public
class|class
name|DoubleRangeFacetCounts
extends|extends
name|RangeFacetCounts
block|{
comment|/** Create {@code RangeFacetCounts}, using {@link    *  DoubleFieldSource} from the specified field. */
DECL|method|DoubleRangeFacetCounts
specifier|public
name|DoubleRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|DoubleRange
modifier|...
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|new
name|DoubleFieldSource
argument_list|(
name|field
argument_list|)
argument_list|,
name|hits
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
block|}
comment|/** Create {@code RangeFacetCounts}, using the provided    *  {@link ValueSource}. */
DECL|method|DoubleRangeFacetCounts
specifier|public
name|DoubleRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|DoubleRange
modifier|...
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|field
argument_list|,
name|valueSource
argument_list|,
name|hits
argument_list|,
literal|null
argument_list|,
name|ranges
argument_list|)
expr_stmt|;
block|}
comment|/** Create {@code RangeFacetCounts}, using the provided    *  {@link ValueSource}, and using the provided Filter as    *  a fastmatch: only documents passing the filter are    *  checked for the matching ranges.  The filter must be    *  random access (implement {@link DocIdSet#bits}). */
DECL|method|DoubleRangeFacetCounts
specifier|public
name|DoubleRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|Filter
name|fastMatchFilter
parameter_list|,
name|DoubleRange
modifier|...
name|ranges
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|field
argument_list|,
name|ranges
argument_list|,
name|fastMatchFilter
argument_list|)
expr_stmt|;
name|count
argument_list|(
name|valueSource
argument_list|,
name|hits
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|count
specifier|private
name|void
name|count
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|,
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|DoubleRange
index|[]
name|ranges
init|=
operator|(
name|DoubleRange
index|[]
operator|)
name|this
operator|.
name|ranges
decl_stmt|;
name|LongRange
index|[]
name|longRanges
init|=
operator|new
name|LongRange
index|[
name|ranges
operator|.
name|length
index|]
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
name|ranges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DoubleRange
name|range
init|=
name|ranges
index|[
name|i
index|]
decl_stmt|;
name|longRanges
index|[
name|i
index|]
operator|=
operator|new
name|LongRange
argument_list|(
name|range
operator|.
name|label
argument_list|,
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|range
operator|.
name|minIncl
argument_list|)
argument_list|,
literal|true
argument_list|,
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|range
operator|.
name|maxIncl
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|LongRangeCounter
name|counter
init|=
operator|new
name|LongRangeCounter
argument_list|(
name|longRanges
argument_list|)
decl_stmt|;
name|int
name|missingCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|FunctionValues
name|fv
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|hits
operator|.
name|context
argument_list|)
decl_stmt|;
name|totCount
operator|+=
name|hits
operator|.
name|totalHits
expr_stmt|;
name|Bits
name|bits
decl_stmt|;
if|if
condition|(
name|fastMatchFilter
operator|!=
literal|null
condition|)
block|{
name|DocIdSet
name|dis
init|=
name|fastMatchFilter
operator|.
name|getDocIdSet
argument_list|(
name|hits
operator|.
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|==
literal|null
condition|)
block|{
comment|// No documents match
continue|continue;
block|}
name|bits
operator|=
name|dis
operator|.
name|bits
argument_list|()
expr_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fastMatchFilter does not implement DocIdSet.bits"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|bits
operator|=
literal|null
expr_stmt|;
block|}
name|DocIdSetIterator
name|docs
init|=
name|hits
operator|.
name|bits
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
operator|&&
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
name|doc
operator|++
expr_stmt|;
continue|continue;
block|}
comment|// Skip missing docs:
if|if
condition|(
name|fv
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|counter
operator|.
name|add
argument_list|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|fv
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missingCount
operator|++
expr_stmt|;
block|}
block|}
block|}
name|missingCount
operator|+=
name|counter
operator|.
name|fillCounts
argument_list|(
name|counts
argument_list|)
expr_stmt|;
name|totCount
operator|-=
name|missingCount
expr_stmt|;
block|}
block|}
end_class

end_unit

