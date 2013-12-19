begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|tasks
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|FacetSource
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|FacetField
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
name|facet
operator|.
name|taxonomy
operator|.
name|FacetLabel
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
name|IndexDocument
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
name|IndexableField
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
name|StorableField
import|;
end_import

begin_comment
comment|/**  * Add a faceted document.  *<p>  * Config properties:  *<ul>  *<li><b>with.facets</b>=&lt;tells whether to actually add any facets to the  * document| Default: true&gt;<br>  * This config property allows to easily compare the performance of adding docs  * with and without facets. Note that facets are created even when this is  * false, just that they are not added to the document (nor to the taxonomy).  *</ul>  *<p>  * See {@link AddDocTask} for general document parameters and configuration.  *<p>  * Makes use of the {@link FacetSource} in effect - see {@link PerfRunData} for  * facet source settings.  */
end_comment

begin_class
DECL|class|AddFacetedDocTask
specifier|public
class|class
name|AddFacetedDocTask
extends|extends
name|AddDocTask
block|{
DECL|field|config
specifier|private
name|FacetsConfig
name|config
decl_stmt|;
DECL|method|AddFacetedDocTask
specifier|public
name|AddFacetedDocTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
name|boolean
name|withFacets
init|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"with.facets"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|withFacets
condition|)
block|{
name|FacetSource
name|facetsSource
init|=
name|getRunData
argument_list|()
operator|.
name|getFacetSource
argument_list|()
decl_stmt|;
name|config
operator|=
operator|new
name|FacetsConfig
argument_list|()
expr_stmt|;
name|facetsSource
operator|.
name|configure
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|getLogMessage
argument_list|(
name|recsCount
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getLogMessage
argument_list|(
name|recsCount
argument_list|)
operator|+
literal|" with facets"
return|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|FacetField
argument_list|>
name|facets
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
argument_list|()
decl_stmt|;
name|getRunData
argument_list|()
operator|.
name|getFacetSource
argument_list|()
operator|.
name|getNextFacets
argument_list|(
name|facets
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetField
name|ff
range|:
name|facets
control|)
block|{
operator|(
operator|(
name|Document
operator|)
name|doc
operator|)
operator|.
name|add
argument_list|(
name|ff
argument_list|)
expr_stmt|;
block|}
name|doc
operator|=
name|config
operator|.
name|build
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getTaxonomyWriter
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|doLogic
argument_list|()
return|;
block|}
block|}
end_class

end_unit

