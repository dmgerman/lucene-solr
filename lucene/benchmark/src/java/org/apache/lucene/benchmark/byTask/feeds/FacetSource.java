begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
operator|.
name|CategoryAssociationsContainer
import|;
end_import

begin_comment
comment|/**  * Source items for facets.  *<p>  * For supported configuration parameters see {@link ContentItemsSource}.  */
end_comment

begin_class
DECL|class|FacetSource
specifier|public
specifier|abstract
class|class
name|FacetSource
extends|extends
name|ContentItemsSource
block|{
comment|/**    * Returns the next {@link CategoryAssociationsContainer facets content item}.    * Implementations must account for multi-threading, as multiple threads can    * call this method simultaneously.    */
DECL|method|getNextFacets
specifier|public
specifier|abstract
name|CategoryAssociationsContainer
name|getNextFacets
parameter_list|(
name|CategoryAssociationsContainer
name|facets
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|printStatistics
argument_list|(
literal|"facets"
argument_list|)
expr_stmt|;
comment|// re-initiate since properties by round may have changed.
name|setConfig
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

