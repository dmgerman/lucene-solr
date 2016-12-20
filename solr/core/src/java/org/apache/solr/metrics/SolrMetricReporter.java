begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|PluginInfo
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
name|SolrPluginUtils
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
name|plugin
operator|.
name|PluginInfoInitialized
import|;
end_import

begin_comment
comment|/**  * Interface for 'pluggable' metric reporters.  */
end_comment

begin_class
DECL|class|SolrMetricReporter
specifier|public
specifier|abstract
class|class
name|SolrMetricReporter
implements|implements
name|Closeable
implements|,
name|PluginInfoInitialized
block|{
DECL|field|registryName
specifier|protected
specifier|final
name|String
name|registryName
decl_stmt|;
DECL|field|metricManager
specifier|protected
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|pluginInfo
specifier|protected
name|PluginInfo
name|pluginInfo
decl_stmt|;
comment|/**    * Create a reporter for metrics managed in a named registry.    * @param registryName registry to use, one of registries managed by    *                     {@link SolrMetricManager}    */
DECL|method|SolrMetricReporter
specifier|protected
name|SolrMetricReporter
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registryName
parameter_list|)
block|{
name|this
operator|.
name|registryName
operator|=
name|registryName
expr_stmt|;
name|this
operator|.
name|metricManager
operator|=
name|metricManager
expr_stmt|;
block|}
comment|/**    * Initializes a {@link SolrMetricReporter} with the plugin's configuration.    *    * @param pluginInfo the plugin's configuration    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|pluginInfo
parameter_list|)
block|{
if|if
condition|(
name|pluginInfo
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|pluginInfo
operator|=
name|pluginInfo
operator|.
name|copy
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|pluginInfo
operator|.
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|SolrPluginUtils
operator|.
name|invokeSetters
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|pluginInfo
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
block|}
name|validate
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the effective {@link PluginInfo} instance that was used for    * initialization of this plugin.    * @return plugin info, or null if not yet initialized.    */
DECL|method|getPluginInfo
specifier|public
name|PluginInfo
name|getPluginInfo
parameter_list|()
block|{
return|return
name|pluginInfo
return|;
block|}
comment|/**    * Validates that the reporter has been correctly configured.    *    * @throws IllegalStateException if the reporter is not properly configured    */
DECL|method|validate
specifier|protected
specifier|abstract
name|void
name|validate
parameter_list|()
throws|throws
name|IllegalStateException
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"{"
operator|+
literal|"registryName='"
operator|+
name|registryName
operator|+
literal|'\''
operator|+
literal|", pluginInfo="
operator|+
name|pluginInfo
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

