begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.results
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
operator|.
name|results
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
name|search
operator|.
name|FacetResultsHandler
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
name|params
operator|.
name|FacetRequest
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
name|sampling
operator|.
name|SampleFixer
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
name|taxonomy
operator|.
name|CategoryPath
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
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Result of faceted search for a certain taxonomy node.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|FacetResultNode
specifier|public
interface|interface
name|FacetResultNode
block|{
comment|/**    * String representation of this facet result node.    * Use with caution: might return a very long string.    * @param prefix prefix for each result line    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|prefix
parameter_list|)
function_decl|;
comment|/**    * Ordinal of the category of this result.    */
DECL|method|getOrdinal
specifier|public
name|int
name|getOrdinal
parameter_list|()
function_decl|;
comment|/**    * Category path of the category of this result, or null if not computed,     * because the application did not request to compute it.     * To force computing the label in case not yet computed use    * {@link #getLabel(TaxonomyReader)}.    * @see FacetRequest#getNumLabel()    * @see #getLabel(TaxonomyReader)    */
DECL|method|getLabel
specifier|public
name|CategoryPath
name|getLabel
parameter_list|()
function_decl|;
comment|/**    * Category path of the category of this result.    * If not already computed, will be computed now.     *<p>     * Use with<b>caution</b>: loading a label for results is costly, performance wise.    * Therefore force labels loading only when really needed.       * @param taxonomyReader taxonomy reader for forcing (lazy) labeling of this result.     * @throws IOException on error    * @see FacetRequest#getNumLabel()    */
DECL|method|getLabel
specifier|public
name|CategoryPath
name|getLabel
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Value of this result - usually either count or a value derived from some    * computing on the association of it.    */
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
function_decl|;
comment|/**    * Value of screened out sub results.    *<p>    * If only part of valid results are returned, e.g. because top K were requested,    * provide info on "what else is there under this result node".    */
DECL|method|getResidue
specifier|public
name|double
name|getResidue
parameter_list|()
function_decl|;
comment|/**    * Contained sub results.    * These are either child facets, if a tree result was requested, or simply descendants, in case    * tree result was not requested. In the first case, all returned are both descendants of     * this node in the taxonomy and siblings of each other in the taxonomy.    * In the latter case they are only guaranteed to be descendants of     * this node in the taxonomy.      */
DECL|method|getSubResults
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|FacetResultNode
argument_list|>
name|getSubResults
parameter_list|()
function_decl|;
comment|/**    * Number of sub results    */
DECL|method|getNumSubResults
specifier|public
name|int
name|getNumSubResults
parameter_list|()
function_decl|;
comment|/**    * Expert: Set a new value for this result node.    *<p>    * Allows to modify the value of this facet node.     * Used for example to tune a sampled value, e.g. by     * {@link SampleFixer#fixResult(org.apache.lucene.facet.search.ScoredDocIDs, FacetResult)}      * @param value the new value to set    * @see #getValue()    * @see FacetResultsHandler#rearrangeFacetResult(FacetResult)    */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|double
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

