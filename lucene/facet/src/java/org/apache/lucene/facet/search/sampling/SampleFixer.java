begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.sampling
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
name|sampling
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
name|ScoredDocIDs
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
name|results
operator|.
name|FacetResult
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Fixer of sample facet accumulation results  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|SampleFixer
specifier|public
interface|interface
name|SampleFixer
block|{
comment|/**    * Alter the input result, fixing it to account for the sampling. This    * implementation can compute accurate or estimated counts for the sampled facets.     * For example, a faster correction could just multiply by a compensating factor.    *     * @param origDocIds    *          full set of matching documents.    * @param fres    *          sample result to be fixed.    * @throws IOException    */
DECL|method|fixResult
specifier|public
name|void
name|fixResult
parameter_list|(
name|ScoredDocIDs
name|origDocIds
parameter_list|,
name|FacetResult
name|fres
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

