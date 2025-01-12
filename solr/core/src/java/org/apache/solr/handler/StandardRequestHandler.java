begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|component
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  *  * All of the following options may be configured for this handler  * in the solrconfig as defaults, and may be overridden as request parameters.  * (TODO: complete documentation of request parameters here, rather than only  * on the wiki).  *  *<ul>  *<li> highlight - Set to any value not .equal() to "false" to enable highlight  * generation</li>  *<li> highlightFields - Set to a comma- or space-delimited list of fields to  * highlight.  If unspecified, uses the default query field</li>  *<li> maxSnippets - maximum number of snippets to generate per field-highlight.  *</li>  *</ul>  *  */
end_comment

begin_class
DECL|class|StandardRequestHandler
specifier|public
class|class
name|StandardRequestHandler
extends|extends
name|SearchHandler
block|{
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
literal|"The standard Solr request handler"
return|;
block|}
block|}
end_class

end_unit

