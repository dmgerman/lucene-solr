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
name|LongFieldSource
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

begin_comment
comment|/** {@link Facets} implementation that computes counts for  *  dynamic long ranges from a provided {@link ValueSource},  *  using {@link FunctionValues#longVal}.  Use  *  this for dimensions that change in real-time (e.g. a  *  relative time based dimension like "Past day", "Past 2  *  days", etc.) or that change for each request (e.g.   *  distance from the user's location, "< 1 km", "< 2 km",  *  etc.).  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|LongRangeFacetCounts
specifier|public
class|class
name|LongRangeFacetCounts
extends|extends
name|RangeFacetCounts
block|{
comment|/** Create {@code LongRangeFacetCounts}, using {@link    *  LongFieldSource} from the specified field. */
DECL|method|LongRangeFacetCounts
specifier|public
name|LongRangeFacetCounts
parameter_list|(
name|String
name|field
parameter_list|,
name|FacetsCollector
name|hits
parameter_list|,
name|LongRange
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
name|LongFieldSource
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
DECL|method|LongRangeFacetCounts
specifier|public
name|LongRangeFacetCounts
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
name|LongRange
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
name|LongRange
index|[]
name|ranges
init|=
operator|(
name|LongRange
index|[]
operator|)
name|this
operator|.
name|ranges
decl_stmt|;
name|LongRangeCounter
name|counter
init|=
operator|new
name|LongRangeCounter
argument_list|(
name|ranges
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
name|fv
operator|.
name|longVal
argument_list|(
name|doc
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
name|int
name|x
init|=
name|counter
operator|.
name|fillCounts
argument_list|(
name|counts
argument_list|)
decl_stmt|;
name|missingCount
operator|+=
name|x
expr_stmt|;
comment|//System.out.println("totCount " + totCount + " missingCount " + counter.missingCount);
name|totCount
operator|-=
name|missingCount
expr_stmt|;
block|}
block|}
end_class

end_unit

