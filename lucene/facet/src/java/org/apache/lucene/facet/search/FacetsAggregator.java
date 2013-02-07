begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
operator|.
name|OrdinalPolicy
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
name|search
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Aggregates categories that were found in result documents (specified by  * {@link MatchingDocs}). If the aggregator requires document scores too, it  * should return {@code true} from {@link #requiresDocScores()}.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|FacetsAggregator
specifier|public
interface|interface
name|FacetsAggregator
block|{
comment|/** Aggregate the facets found in the given matching documents. */
DECL|method|aggregate
specifier|public
name|void
name|aggregate
parameter_list|(
name|MatchingDocs
name|matchingDocs
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Rollup the values of the given ordinal. This method is called when a    * category was indexed with {@link OrdinalPolicy#NO_PARENTS}. The given    * ordinal is the requested category, and you should use the children and    * siblings arrays to traverse its sub-tree.    */
DECL|method|rollupValues
specifier|public
name|void
name|rollupValues
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|children
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
function_decl|;
comment|/** Returns {@code true} if this aggregator requires document scores. */
DECL|method|requiresDocScores
specifier|public
name|boolean
name|requiresDocScores
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

