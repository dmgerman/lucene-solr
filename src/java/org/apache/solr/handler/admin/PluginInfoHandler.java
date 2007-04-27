begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

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
name|Map
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
name|core
operator|.
name|SolrInfoMBean
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
name|core
operator|.
name|SolrInfoRegistry
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
name|handler
operator|.
name|RequestHandlerBase
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
name|handler
operator|.
name|RequestHandlerUtils
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
name|request
operator|.
name|SolrParams
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryResponse
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
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_comment
comment|/**  * similar to "admin/registry.jsp"   *   * NOTE: the response format is still likely to change.  It should be designed so  * that it works nicely with an XSLT transformation.  Untill we have a nice  * XSLT frontend for /admin, the format is still open to change.  *   * @author ryan  * @version $Id$  * @since solr 1.2  */
end_comment

begin_class
DECL|class|PluginInfoHandler
specifier|public
class|class
name|PluginInfoHandler
extends|extends
name|RequestHandlerBase
block|{
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|RequestHandlerUtils
operator|.
name|addExperimentalFormatWarning
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|boolean
name|stats
init|=
name|params
operator|.
name|getBool
argument_list|(
literal|"stats"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"plugins"
argument_list|,
name|getSolrInfoBeans
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getSolrInfoBeans
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getSolrInfoBeans
parameter_list|(
name|boolean
name|stats
parameter_list|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrInfoMBean
operator|.
name|Category
name|cat
range|:
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|values
argument_list|()
control|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|category
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|cat
operator|.
name|name
argument_list|()
argument_list|,
name|category
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|reg
init|=
name|SolrInfoRegistry
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|reg
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|entry
range|:
name|reg
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SolrInfoMBean
name|m
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|getCategory
argument_list|()
operator|!=
name|cat
condition|)
continue|continue;
name|String
name|na
init|=
literal|"Not Declared"
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|category
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
operator|(
name|m
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|?
name|m
operator|.
name|getName
argument_list|()
else|:
name|na
operator|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"version"
argument_list|,
operator|(
name|m
operator|.
name|getVersion
argument_list|()
operator|!=
literal|null
condition|?
name|m
operator|.
name|getVersion
argument_list|()
else|:
name|na
operator|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"description"
argument_list|,
operator|(
name|m
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|?
name|m
operator|.
name|getDescription
argument_list|()
else|:
name|na
operator|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"sourceId"
argument_list|,
operator|(
name|m
operator|.
name|getSourceId
argument_list|()
operator|!=
literal|null
condition|?
name|m
operator|.
name|getSourceId
argument_list|()
else|:
name|na
operator|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"source"
argument_list|,
operator|(
name|m
operator|.
name|getSource
argument_list|()
operator|!=
literal|null
condition|?
name|m
operator|.
name|getSource
argument_list|()
else|:
name|na
operator|)
argument_list|)
expr_stmt|;
name|URL
index|[]
name|urls
init|=
name|m
operator|.
name|getDocs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|urls
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|urls
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|urls
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|URL
name|u
range|:
name|urls
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
name|u
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|add
argument_list|(
literal|"docs"
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stats
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"stats"
argument_list|,
name|m
operator|.
name|getStatistics
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|list
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Registry"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

