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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Intermediate {@link FacetResult} of faceted search.  *<p>  * This is an empty interface on purpose.  *<p>  * It allows {@link FacetResultsHandler} to return intermediate result objects   * that only it knows how to interpret, and so the handler has maximal freedom  * in defining what an intermediate result is, depending on its specific logic.    *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|IntermediateFacetResult
specifier|public
interface|interface
name|IntermediateFacetResult
block|{
comment|/**    * Facet request for which this temporary result was created.    */
DECL|method|getFacetRequest
name|FacetRequest
name|getFacetRequest
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

