begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.plugin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
package|;
end_package

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
name|common
operator|.
name|util
operator|.
name|DOMUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  *   * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|MapPluginLoader
specifier|public
class|class
name|MapPluginLoader
parameter_list|<
name|T
extends|extends
name|MapInitializedPlugin
parameter_list|>
extends|extends
name|AbstractPluginLoader
argument_list|<
name|T
argument_list|>
block|{
DECL|field|registry
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|registry
decl_stmt|;
DECL|method|MapPluginLoader
specifier|public
name|MapPluginLoader
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|map
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|registry
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|T
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|DOMUtil
operator|.
name|toMapExcept
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|,
literal|"name"
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|plugin
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|protected
name|T
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|plugin
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|registry
operator|!=
literal|null
condition|)
block|{
return|return
name|registry
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|plugin
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

