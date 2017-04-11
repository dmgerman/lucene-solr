begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|metrics
operator|.
name|SolrMetricManager
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
name|stats
operator|.
name|MetricUtils
import|;
end_import

begin_comment
comment|/**  * Interface for getting various ui friendly strings  * for use by objects which are 'pluggable' to make server administration  * easier.  */
end_comment

begin_interface
DECL|interface|SolrInfoBean
specifier|public
interface|interface
name|SolrInfoBean
block|{
comment|/**    * Category of Solr component.    */
DECL|enum|Category
DECL|enum constant|CONTAINER
DECL|enum constant|ADMIN
DECL|enum constant|CORE
DECL|enum constant|QUERY
DECL|enum constant|UPDATE
DECL|enum constant|CACHE
DECL|enum constant|HIGHLIGHTER
DECL|enum constant|QUERYPARSER
DECL|enum constant|SPELLCHECKER
enum|enum
name|Category
block|{
name|CONTAINER
block|,
name|ADMIN
block|,
name|CORE
block|,
name|QUERY
block|,
name|UPDATE
block|,
name|CACHE
block|,
name|HIGHLIGHTER
block|,
name|QUERYPARSER
block|,
name|SPELLCHECKER
block|,
DECL|enum constant|SEARCHER
DECL|enum constant|REPLICATION
DECL|enum constant|TLOG
DECL|enum constant|INDEX
DECL|enum constant|DIRECTORY
DECL|enum constant|HTTP
DECL|enum constant|OTHER
name|SEARCHER
block|,
name|REPLICATION
block|,
name|TLOG
block|,
name|INDEX
block|,
name|DIRECTORY
block|,
name|HTTP
block|,
name|OTHER
block|}
comment|/**    * Top-level group of beans or metrics for a subsystem.    */
DECL|enum|Group
DECL|enum constant|jvm
DECL|enum constant|jetty
DECL|enum constant|node
DECL|enum constant|core
DECL|enum constant|collection
DECL|enum constant|shard
DECL|enum constant|cluster
DECL|enum constant|overseer
enum|enum
name|Group
block|{
name|jvm
block|,
name|jetty
block|,
name|node
block|,
name|core
block|,
name|collection
block|,
name|shard
block|,
name|cluster
block|,
name|overseer
block|}
comment|/**    * Simple common usage name, e.g. BasicQueryHandler,    * or fully qualified class name.    */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
comment|/** Simple one or two line description */
DECL|method|getDescription
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/** Category of this component */
DECL|method|getCategory
name|Category
name|getCategory
parameter_list|()
function_decl|;
comment|/** Optionally return a snapshot of metrics that this component reports, or null.    * Default implementation requires that both {@link #getMetricNames()} and    * {@link #getMetricRegistry()} return non-null values.    */
DECL|method|getMetricsSnapshot
specifier|default
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getMetricsSnapshot
parameter_list|()
block|{
if|if
condition|(
name|getMetricRegistry
argument_list|()
operator|==
literal|null
operator|||
name|getMetricNames
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|MetricUtils
operator|.
name|convertMetrics
argument_list|(
name|getMetricRegistry
argument_list|()
argument_list|,
name|getMetricNames
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Modifiable set of metric names that this component reports (default is null,    * which means none). If not null then this set is used by {@link #registerMetricName(String)}    * to capture what metrics names are reported from this component.    */
DECL|method|getMetricNames
specifier|default
name|Set
argument_list|<
name|String
argument_list|>
name|getMetricNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * An instance of {@link MetricRegistry} that this component uses for metrics reporting    * (default is null, which means no registry).    */
DECL|method|getMetricRegistry
specifier|default
name|MetricRegistry
name|getMetricRegistry
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Register a metric name that this component reports. This method is called by various    * metric registration methods in {@link org.apache.solr.metrics.SolrMetricManager} in order    * to capture what metric names are reported from this component (which in turn is called    * from {@link org.apache.solr.metrics.SolrMetricProducer#initializeMetrics(SolrMetricManager, String, String)}).    *<p>Default implementation registers all metrics added by a component. Implementations may    * override this to avoid reporting some or all metrics returned by {@link #getMetricsSnapshot()}</p>    */
DECL|method|registerMetricName
specifier|default
name|void
name|registerMetricName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|getMetricNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|names
operator|!=
literal|null
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

