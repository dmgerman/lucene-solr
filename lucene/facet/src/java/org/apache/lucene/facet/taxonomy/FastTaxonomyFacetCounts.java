begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|index
operator|.
name|BinaryDocValues
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
name|BytesRef
import|;
end_import

begin_comment
comment|/** Computes facets counts, assuming the default encoding  *  into DocValues was used.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|FastTaxonomyFacetCounts
specifier|public
class|class
name|FastTaxonomyFacetCounts
extends|extends
name|IntTaxonomyFacets
block|{
comment|/** Create {@code FastTaxonomyFacetCounts}, which also    *  counts all facet labels. */
DECL|method|FastTaxonomyFacetCounts
specifier|public
name|FastTaxonomyFacetCounts
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
expr_stmt|;
block|}
comment|/** Create {@code FastTaxonomyFacetCounts}, using the    *  specified {@code indexFieldName} for ordinals.  Use    *  this if you had set {@link    *  FacetsConfig#setIndexFieldName} to change the index    *  field name for certain dimensions. */
DECL|method|FastTaxonomyFacetCounts
specifier|public
name|FastTaxonomyFacetCounts
parameter_list|(
name|String
name|indexFieldName
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|indexFieldName
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|count
argument_list|(
name|fc
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|count
specifier|private
specifier|final
name|void
name|count
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|BinaryDocValues
name|dv
init|=
name|hits
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|indexFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
comment|// this reader does not have DocValues for the requested category list
continue|continue;
block|}
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
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
name|dv
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|scratch
operator|.
name|bytes
decl_stmt|;
name|int
name|end
init|=
name|scratch
operator|.
name|offset
operator|+
name|scratch
operator|.
name|length
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
name|scratch
operator|.
name|offset
decl_stmt|;
name|int
name|prev
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|end
condition|)
block|{
name|byte
name|b
init|=
name|bytes
index|[
name|offset
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
name|prev
operator|=
name|ord
operator|=
operator|(
operator|(
name|ord
operator|<<
literal|7
operator|)
operator||
name|b
operator|)
operator|+
name|prev
expr_stmt|;
operator|++
name|values
index|[
name|ord
index|]
expr_stmt|;
name|ord
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
operator|(
name|ord
operator|<<
literal|7
operator|)
operator||
operator|(
name|b
operator|&
literal|0x7F
operator|)
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

