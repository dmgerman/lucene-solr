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
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * Extension of {@link SolrInfoMBean} for use by objects that  * expose metrics through {@link SolrCoreMetricManager}.  */
end_comment

begin_interface
DECL|interface|SolrMetricProducer
specifier|public
interface|interface
name|SolrMetricProducer
extends|extends
name|SolrInfoMBean
block|{
comment|/**    * Initializes metrics specific to this producer    * @param manager an instance of {@link SolrMetricManager}    * @param registry registry name where metrics are registered    * @param scope scope of the metrics (eg. handler name) to separate metrics of    *              instances of the same component executing in different contexts    * @return registered (or existing) unqualified names of metrics specific to this producer.    */
DECL|method|initializeMetrics
name|Collection
argument_list|<
name|String
argument_list|>
name|initializeMetrics
parameter_list|(
name|SolrMetricManager
name|manager
parameter_list|,
name|String
name|registry
parameter_list|,
name|String
name|scope
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

