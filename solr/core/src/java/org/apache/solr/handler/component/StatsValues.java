begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|Map
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_comment
comment|/**  * StatsValue defines the interface for the collection of statistical values about fields and facets.  */
end_comment

begin_comment
comment|// TODO: should implement Collector?
end_comment

begin_interface
DECL|interface|StatsValues
specifier|public
interface|interface
name|StatsValues
block|{
comment|/**    * Accumulate the values based on those in the given NamedList    *    * @param stv NamedList whose values will be used to accumulate the current values    */
DECL|method|accumulate
name|void
name|accumulate
parameter_list|(
name|NamedList
name|stv
parameter_list|)
function_decl|;
comment|/** Accumulate the value associated with<code>docID</code>.    *  @see #setNextReader(org.apache.lucene.index.LeafReaderContext) */
DECL|method|accumulate
name|void
name|accumulate
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/**    * Accumulate the values based on the given value    *    * @param value Value to use to accumulate the current values    * @param count number of times to accumulate this value    */
DECL|method|accumulate
name|void
name|accumulate
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|/**    * Updates the statistics when a document is missing a value    */
DECL|method|missing
name|void
name|missing
parameter_list|()
function_decl|;
comment|/**    * Updates the statistics when multiple documents are missing a value    *    * @param count number of times to count a missing value    */
DECL|method|addMissing
name|void
name|addMissing
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**    * Adds the facet statistics for the facet with the given name    *    * @param facetName Name of the facet    * @param facetValues Facet statistics on a per facet value basis    */
DECL|method|addFacet
name|void
name|addFacet
parameter_list|(
name|String
name|facetName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StatsValues
argument_list|>
name|facetValues
parameter_list|)
function_decl|;
comment|/**    * Translates the values into a NamedList representation    *    * @return NamedList representation of the current values    */
DECL|method|getStatsValues
name|NamedList
argument_list|<
name|?
argument_list|>
name|getStatsValues
parameter_list|()
function_decl|;
comment|/** Set the context for {@link #accumulate(int)}. */
DECL|method|setNextReader
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

