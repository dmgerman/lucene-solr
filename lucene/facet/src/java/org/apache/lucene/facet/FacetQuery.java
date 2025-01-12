begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|index
operator|.
name|Term
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
name|Query
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
name|TermQuery
import|;
end_import

begin_comment
comment|/**  * A term {@link Query} over a {@link FacetField}.  *<p>  *<b>NOTE:</b>This helper class is an alternative to {@link DrillDownQuery}  * especially in cases where you don't intend to use {@link DrillSideways}  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FacetQuery
specifier|public
class|class
name|FacetQuery
extends|extends
name|TermQuery
block|{
comment|/**    * Creates a new {@code FacetQuery} filtering the query on the given dimension.    */
DECL|method|FacetQuery
specifier|public
name|FacetQuery
parameter_list|(
specifier|final
name|FacetsConfig
name|facetsConfig
parameter_list|,
specifier|final
name|String
name|dimension
parameter_list|,
specifier|final
name|String
modifier|...
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|toTerm
argument_list|(
name|facetsConfig
operator|.
name|getDimConfig
argument_list|(
name|dimension
argument_list|)
argument_list|,
name|dimension
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@code FacetQuery} filtering the query on the given dimension.    *<p>    *<b>NOTE:</b>Uses FacetsConfig.DEFAULT_DIM_CONFIG.    */
DECL|method|FacetQuery
specifier|public
name|FacetQuery
parameter_list|(
specifier|final
name|String
name|dimension
parameter_list|,
specifier|final
name|String
modifier|...
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|toTerm
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_DIM_CONFIG
argument_list|,
name|dimension
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toTerm
specifier|static
name|Term
name|toTerm
parameter_list|(
specifier|final
name|FacetsConfig
operator|.
name|DimConfig
name|dimConfig
parameter_list|,
specifier|final
name|String
name|dimension
parameter_list|,
specifier|final
name|String
modifier|...
name|path
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|dimConfig
operator|.
name|indexFieldName
argument_list|,
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|dimension
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

