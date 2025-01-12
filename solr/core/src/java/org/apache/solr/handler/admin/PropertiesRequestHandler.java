begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Enumeration
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
name|SimpleOrderedMap
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
name|response
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
name|RedactionUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_comment
comment|/**  *  * @since solr 1.2  */
end_comment

begin_class
DECL|class|PropertiesRequestHandler
specifier|public
class|class
name|PropertiesRequestHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|REDACT_STRING
specifier|public
specifier|static
specifier|final
name|String
name|REDACT_STRING
init|=
name|RedactionUtils
operator|.
name|getRedactString
argument_list|()
decl_stmt|;
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
name|IOException
block|{
name|NamedList
argument_list|<
name|String
argument_list|>
name|props
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|String
name|property
init|=
name|getSecuredPropertyValue
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|props
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|propertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|name
operator|=
operator|(
name|String
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|props
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|getSecuredPropertyValue
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"system.properties"
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getSecuredPropertyValue
specifier|private
name|String
name|getSecuredPropertyValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|RedactionUtils
operator|.
name|isSystemPropertySensitive
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|REDACT_STRING
return|;
block|}
return|return
name|System
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
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
literal|"Get System Properties"
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|ADMIN
return|;
block|}
block|}
end_class

end_unit

