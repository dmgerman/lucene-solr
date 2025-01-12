begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
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
name|facet
operator|.
name|FacetsConfig
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
name|search
operator|.
name|DoubleValues
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
name|DoubleValuesSource
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
name|IntsRef
import|;
end_import

begin_comment
comment|/** Aggregates sum of values from {@link  *  FunctionValues#doubleVal}, for each facet label.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|TaxonomyFacetSumValueSource
specifier|public
class|class
name|TaxonomyFacetSumValueSource
extends|extends
name|FloatTaxonomyFacets
block|{
DECL|field|ordinalsReader
specifier|private
specifier|final
name|OrdinalsReader
name|ordinalsReader
decl_stmt|;
comment|/**    * Aggreggates double facet values from the provided    * {@link DoubleValuesSource}, pulling ordinals using {@link    * DocValuesOrdinalsReader} against the default indexed    * facet field {@link FacetsConfig#DEFAULT_INDEX_FIELD_NAME}.    */
DECL|method|TaxonomyFacetSumValueSource
specifier|public
name|TaxonomyFacetSumValueSource
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|,
name|DoubleValuesSource
name|valueSource
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
operator|new
name|DocValuesOrdinalsReader
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|,
name|valueSource
argument_list|)
expr_stmt|;
block|}
comment|/**    * Aggreggates float facet values from the provided    *  {@link DoubleValuesSource}, and pulls ordinals from the    *  provided {@link OrdinalsReader}.    */
DECL|method|TaxonomyFacetSumValueSource
specifier|public
name|TaxonomyFacetSumValueSource
parameter_list|(
name|OrdinalsReader
name|ordinalsReader
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|,
name|DoubleValuesSource
name|vs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ordinalsReader
operator|.
name|getIndexFieldName
argument_list|()
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinalsReader
operator|=
name|ordinalsReader
expr_stmt|;
name|sumValues
argument_list|(
name|fc
operator|.
name|getMatchingDocs
argument_list|()
argument_list|,
name|fc
operator|.
name|getKeepScores
argument_list|()
argument_list|,
name|vs
argument_list|)
expr_stmt|;
block|}
DECL|method|scores
specifier|private
specifier|static
name|DoubleValues
name|scores
parameter_list|(
name|MatchingDocs
name|hits
parameter_list|)
block|{
return|return
operator|new
name|DoubleValues
argument_list|()
block|{
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|double
name|doubleValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|hits
operator|.
name|scores
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
DECL|method|sumValues
specifier|private
name|void
name|sumValues
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|,
name|boolean
name|keepScores
parameter_list|,
name|DoubleValuesSource
name|valueSource
parameter_list|)
throws|throws
name|IOException
block|{
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|OrdinalsReader
operator|.
name|OrdinalsSegmentReader
name|ords
init|=
name|ordinalsReader
operator|.
name|getReader
argument_list|(
name|hits
operator|.
name|context
argument_list|)
decl_stmt|;
name|DoubleValues
name|scores
init|=
name|keepScores
condition|?
name|scores
argument_list|(
name|hits
argument_list|)
else|:
literal|null
decl_stmt|;
name|DoubleValues
name|functionValues
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|hits
operator|.
name|context
argument_list|,
name|scores
argument_list|)
decl_stmt|;
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
name|ords
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
if|if
condition|(
name|functionValues
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|float
name|value
init|=
operator|(
name|float
operator|)
name|functionValues
operator|.
name|doubleValue
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
name|scratch
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|scratch
operator|.
name|ints
index|[
name|i
index|]
index|]
operator|+=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
name|rollup
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

