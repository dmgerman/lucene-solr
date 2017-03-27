begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|Counter
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
name|Gauge
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
name|Histogram
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
name|InstrumentedExecutorService
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
name|Meter
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
name|Metric
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
name|MetricFilter
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Snapshot
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
name|Timer
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
name|SolrInputDocument
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
name|metrics
operator|.
name|AggregateMetric
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Metrics specific utility functions.  */
end_comment

begin_class
DECL|class|MetricUtils
specifier|public
class|class
name|MetricUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|METRIC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|METRIC_NAME
init|=
literal|"metric"
decl_stmt|;
DECL|field|VALUE
specifier|public
specifier|static
specifier|final
name|String
name|VALUE
init|=
literal|"value"
decl_stmt|;
DECL|field|VALUES
specifier|public
specifier|static
specifier|final
name|String
name|VALUES
init|=
literal|"values"
decl_stmt|;
DECL|field|MS
specifier|static
specifier|final
name|String
name|MS
init|=
literal|"_ms"
decl_stmt|;
DECL|field|MIN
specifier|static
specifier|final
name|String
name|MIN
init|=
literal|"min"
decl_stmt|;
DECL|field|MIN_MS
specifier|static
specifier|final
name|String
name|MIN_MS
init|=
name|MIN
operator|+
name|MS
decl_stmt|;
DECL|field|MAX
specifier|static
specifier|final
name|String
name|MAX
init|=
literal|"max"
decl_stmt|;
DECL|field|MAX_MS
specifier|static
specifier|final
name|String
name|MAX_MS
init|=
name|MAX
operator|+
name|MS
decl_stmt|;
DECL|field|MEAN
specifier|static
specifier|final
name|String
name|MEAN
init|=
literal|"mean"
decl_stmt|;
DECL|field|MEAN_MS
specifier|static
specifier|final
name|String
name|MEAN_MS
init|=
name|MEAN
operator|+
name|MS
decl_stmt|;
DECL|field|MEDIAN
specifier|static
specifier|final
name|String
name|MEDIAN
init|=
literal|"median"
decl_stmt|;
DECL|field|MEDIAN_MS
specifier|static
specifier|final
name|String
name|MEDIAN_MS
init|=
name|MEDIAN
operator|+
name|MS
decl_stmt|;
DECL|field|STDDEV
specifier|static
specifier|final
name|String
name|STDDEV
init|=
literal|"stddev"
decl_stmt|;
DECL|field|STDDEV_MS
specifier|static
specifier|final
name|String
name|STDDEV_MS
init|=
name|STDDEV
operator|+
name|MS
decl_stmt|;
DECL|field|SUM
specifier|static
specifier|final
name|String
name|SUM
init|=
literal|"sum"
decl_stmt|;
DECL|field|P75
specifier|static
specifier|final
name|String
name|P75
init|=
literal|"p75"
decl_stmt|;
DECL|field|P75_MS
specifier|static
specifier|final
name|String
name|P75_MS
init|=
name|P75
operator|+
name|MS
decl_stmt|;
DECL|field|P95
specifier|static
specifier|final
name|String
name|P95
init|=
literal|"p95"
decl_stmt|;
DECL|field|P95_MS
specifier|static
specifier|final
name|String
name|P95_MS
init|=
name|P95
operator|+
name|MS
decl_stmt|;
DECL|field|P99
specifier|static
specifier|final
name|String
name|P99
init|=
literal|"p99"
decl_stmt|;
DECL|field|P99_MS
specifier|static
specifier|final
name|String
name|P99_MS
init|=
name|P99
operator|+
name|MS
decl_stmt|;
DECL|field|P999
specifier|static
specifier|final
name|String
name|P999
init|=
literal|"p999"
decl_stmt|;
DECL|field|P999_MS
specifier|static
specifier|final
name|String
name|P999_MS
init|=
name|P999
operator|+
name|MS
decl_stmt|;
comment|/**    * Adds metrics from a Timer to a NamedList, using well-known back-compat names.    * @param lst The NamedList to add the metrics data to    * @param timer The Timer to extract the metrics from    */
DECL|method|addMetrics
specifier|public
specifier|static
name|void
name|addMetrics
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|lst
parameter_list|,
name|Timer
name|timer
parameter_list|)
block|{
name|Snapshot
name|snapshot
init|=
name|timer
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"avgRequestsPerSecond"
argument_list|,
name|timer
operator|.
name|getMeanRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"5minRateRequestsPerSecond"
argument_list|,
name|timer
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"15minRateRequestsPerSecond"
argument_list|,
name|timer
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"avgTimePerRequest"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|getMean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"medianRequestTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|getMedian
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"75thPcRequestTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get75thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"95thPcRequestTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get95thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"99thPcRequestTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get99thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"999thPcRequestTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|get999thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts a double representing nanoseconds to a double representing milliseconds.    *    * @param ns the amount of time in nanoseconds    * @return the amount of time in milliseconds    */
DECL|method|nsToMs
specifier|public
specifier|static
name|double
name|nsToMs
parameter_list|(
name|double
name|ns
parameter_list|)
block|{
return|return
name|ns
operator|/
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
literal|1
argument_list|)
return|;
block|}
comment|/**    * Returns a NamedList representation of the given metric registry. Only those metrics    * are converted to NamedList which match at least one of the given MetricFilter instances.    *    * @param registry      the {@link MetricRegistry} to be converted to NamedList    * @param shouldMatchFilters a list of {@link MetricFilter} instances.    *                           A metric must match<em>any one</em> of the filters from this list to be    *                           included in the output    * @param mustMatchFilter a {@link MetricFilter}.    *                        A metric<em>must</em> match this filter to be included in the output.    * @param skipHistograms discard any {@link Histogram}-s and histogram parts of {@link Timer}-s.    * @param compact use compact representation for counters and gauges.    * @param metadata optional metadata. If not null and not empty then this map will be added under a    *                 {@code _metadata_} key.    * @return a {@link NamedList}    */
DECL|method|toNamedList
specifier|public
specifier|static
name|NamedList
name|toNamedList
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|,
name|List
argument_list|<
name|MetricFilter
argument_list|>
name|shouldMatchFilters
parameter_list|,
name|MetricFilter
name|mustMatchFilter
parameter_list|,
name|boolean
name|skipHistograms
parameter_list|,
name|boolean
name|skipAggregateValues
parameter_list|,
name|boolean
name|compact
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
parameter_list|)
block|{
name|NamedList
name|result
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|toMaps
argument_list|(
name|registry
argument_list|,
name|shouldMatchFilters
argument_list|,
name|mustMatchFilter
argument_list|,
name|skipHistograms
argument_list|,
name|skipAggregateValues
argument_list|,
name|compact
argument_list|,
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
name|result
operator|.
name|add
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|metadata
operator|!=
literal|null
operator|&&
operator|!
name|metadata
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
literal|"_metadata_"
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Returns a representation of the given metric registry as a list of {@link SolrInputDocument}-s.    Only those metrics    * are converted to NamedList which match at least one of the given MetricFilter instances.    *    * @param registry      the {@link MetricRegistry} to be converted to NamedList    * @param shouldMatchFilters a list of {@link MetricFilter} instances.    *                           A metric must match<em>any one</em> of the filters from this list to be    *                           included in the output    * @param mustMatchFilter a {@link MetricFilter}.    *                        A metric<em>must</em> match this filter to be included in the output.    * @param skipHistograms discard any {@link Histogram}-s and histogram parts of {@link Timer}-s.    * @param compact use compact representation for counters and gauges.    * @param metadata optional metadata. If not null and not empty then this map will be added under a    *                 {@code _metadata_} key.    * @return a list of {@link SolrInputDocument}-s    */
DECL|method|toSolrInputDocuments
specifier|public
specifier|static
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|toSolrInputDocuments
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|,
name|List
argument_list|<
name|MetricFilter
argument_list|>
name|shouldMatchFilters
parameter_list|,
name|MetricFilter
name|mustMatchFilter
parameter_list|,
name|boolean
name|skipHistograms
parameter_list|,
name|boolean
name|skipAggregateValues
parameter_list|,
name|boolean
name|compact
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
parameter_list|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|toSolrInputDocuments
argument_list|(
name|registry
argument_list|,
name|shouldMatchFilters
argument_list|,
name|mustMatchFilter
argument_list|,
name|skipHistograms
argument_list|,
name|skipAggregateValues
argument_list|,
name|compact
argument_list|,
name|metadata
argument_list|,
name|doc
lambda|->
block|{
name|result
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|toSolrInputDocuments
specifier|public
specifier|static
name|void
name|toSolrInputDocuments
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|,
name|List
argument_list|<
name|MetricFilter
argument_list|>
name|shouldMatchFilters
parameter_list|,
name|MetricFilter
name|mustMatchFilter
parameter_list|,
name|boolean
name|skipHistograms
parameter_list|,
name|boolean
name|skipAggregateValues
parameter_list|,
name|boolean
name|compact
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
parameter_list|,
name|Consumer
argument_list|<
name|SolrInputDocument
argument_list|>
name|consumer
parameter_list|)
block|{
name|boolean
name|addMetadata
init|=
name|metadata
operator|!=
literal|null
operator|&&
operator|!
name|metadata
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|toMaps
argument_list|(
name|registry
argument_list|,
name|shouldMatchFilters
argument_list|,
name|mustMatchFilter
argument_list|,
name|skipHistograms
argument_list|,
name|skipAggregateValues
argument_list|,
name|compact
argument_list|,
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|METRIC_NAME
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|toSolrInputDocument
argument_list|(
literal|null
argument_list|,
name|doc
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|addMetadata
condition|)
block|{
name|toSolrInputDocument
argument_list|(
literal|null
argument_list|,
name|doc
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
name|consumer
operator|.
name|accept
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|toSolrInputDocument
specifier|public
specifier|static
name|void
name|toSolrInputDocument
parameter_list|(
name|String
name|prefix
parameter_list|,
name|SolrInputDocument
name|doc
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Map
operator|)
condition|)
block|{
name|String
name|key
init|=
name|prefix
operator|!=
literal|null
condition|?
name|prefix
else|:
name|VALUE
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|key
argument_list|,
name|o
argument_list|)
expr_stmt|;
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
comment|// flatten recursively
name|toSolrInputDocument
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|doc
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|key
init|=
name|prefix
operator|!=
literal|null
condition|?
name|prefix
operator|+
literal|"."
operator|+
name|entry
operator|.
name|getKey
argument_list|()
else|:
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|toMaps
specifier|public
specifier|static
name|void
name|toMaps
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|,
name|List
argument_list|<
name|MetricFilter
argument_list|>
name|shouldMatchFilters
parameter_list|,
name|MetricFilter
name|mustMatchFilter
parameter_list|,
name|boolean
name|skipHistograms
parameter_list|,
name|boolean
name|skipAggregateValues
parameter_list|,
name|boolean
name|compact
parameter_list|,
name|BiConsumer
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|consumer
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|metrics
init|=
name|registry
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|SortedSet
argument_list|<
name|String
argument_list|>
name|names
init|=
name|registry
operator|.
name|getNames
argument_list|()
decl_stmt|;
name|names
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|s
lambda|->
name|shouldMatchFilters
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|metricFilter
lambda|->
name|metricFilter
operator|.
name|matches
argument_list|(
name|s
argument_list|,
name|metrics
operator|.
name|get
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|s
lambda|->
name|mustMatchFilter
operator|.
name|matches
argument_list|(
name|s
argument_list|,
name|metrics
operator|.
name|get
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|n
lambda|->
block|{
name|Metric
name|metric
operator|=
name|metrics
operator|.
name|get
argument_list|(
name|n
argument_list|)
argument_list|;           if
operator|(
name|metric
operator|instanceof
name|Counter
operator|)
block|{
name|Counter
name|counter
operator|=
operator|(
name|Counter
operator|)
name|metric
block|;
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
name|convertCounter
argument_list|(
name|counter
argument_list|,
name|compact
argument_list|)
argument_list|)
block|;           }
elseif|else
if|if
condition|(
name|metric
operator|instanceof
name|Gauge
condition|)
block|{
name|Gauge
name|gauge
init|=
operator|(
name|Gauge
operator|)
name|metric
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
name|convertGauge
argument_list|(
name|gauge
argument_list|,
name|compact
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InternalError
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error converting gauge '"
operator|+
name|n
operator|+
literal|"', possible JDK bug: SOLR-10362"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|else if
operator|(
name|metric
operator|instanceof
name|Meter
operator|)
block|{
name|Meter
name|meter
operator|=
operator|(
name|Meter
operator|)
name|metric
block|;
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
name|convertMeter
argument_list|(
name|meter
argument_list|)
argument_list|)
block|;           }
elseif|else
if|if
condition|(
name|metric
operator|instanceof
name|Timer
condition|)
block|{
name|Timer
name|timer
init|=
operator|(
name|Timer
operator|)
name|metric
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
name|convertTimer
argument_list|(
name|timer
argument_list|,
name|skipHistograms
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|else if
operator|(
name|metric
operator|instanceof
name|Histogram
operator|)
block|{
if|if
condition|(
operator|!
name|skipHistograms
condition|)
block|{
name|Histogram
name|histogram
init|=
operator|(
name|Histogram
operator|)
name|metric
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
name|convertHistogram
argument_list|(
name|histogram
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|metric
operator|instanceof
name|AggregateMetric
condition|)
block|{
name|consumer
operator|.
name|accept
argument_list|(
name|n
argument_list|,
name|convertAggregateMetric
argument_list|(
operator|(
name|AggregateMetric
operator|)
name|metric
argument_list|,
name|skipAggregateValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_function
unit|}    static
DECL|method|convertAggregateMetric
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertAggregateMetric
parameter_list|(
name|AggregateMetric
name|metric
parameter_list|,
name|boolean
name|skipAggregateValues
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|metric
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
name|MAX
argument_list|,
name|metric
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
name|MIN
argument_list|,
name|metric
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
name|MEAN
argument_list|,
name|metric
operator|.
name|getMean
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
name|STDDEV
argument_list|,
name|metric
operator|.
name|getStdDev
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
name|SUM
argument_list|,
name|metric
operator|.
name|getSum
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|metric
operator|.
name|isEmpty
argument_list|()
operator|||
name|skipAggregateValues
operator|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
name|VALUES
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|metric
operator|.
name|getValues
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
name|v
operator|.
name|value
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"updateCount"
argument_list|,
name|v
operator|.
name|updateCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
end_function

begin_function
DECL|method|convertHistogram
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertHistogram
parameter_list|(
name|Histogram
name|histogram
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Snapshot
name|snapshot
init|=
name|histogram
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|histogram
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// non-time based values
name|addSnapshot
argument_list|(
name|response
argument_list|,
name|snapshot
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
end_function

begin_comment
comment|// optionally convert ns to ms
end_comment

begin_function
DECL|method|nsToMs
specifier|static
name|double
name|nsToMs
parameter_list|(
name|boolean
name|convert
parameter_list|,
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|convert
condition|)
block|{
return|return
name|nsToMs
argument_list|(
name|value
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|value
return|;
block|}
block|}
end_function

begin_comment
comment|// some snapshots represent time in ns, other snapshots represent raw values (eg. chunk size)
end_comment

begin_function
DECL|method|addSnapshot
specifier|static
name|void
name|addSnapshot
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|,
name|boolean
name|ms
parameter_list|)
block|{
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|MIN_MS
else|:
name|MIN
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|MAX_MS
else|:
name|MAX
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|MEAN_MS
else|:
name|MEAN
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|getMean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|MEDIAN_MS
else|:
name|MEDIAN
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|getMedian
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|STDDEV_MS
else|:
name|STDDEV
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|getStdDev
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|P75_MS
else|:
name|P75
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|get75thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|P95_MS
else|:
name|P95
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|get95thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|P99_MS
else|:
name|P99
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|get99thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
operator|(
name|ms
condition|?
name|P999_MS
else|:
name|P999
operator|)
argument_list|,
name|nsToMs
argument_list|(
name|ms
argument_list|,
name|snapshot
operator|.
name|get999thPercentile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|convertTimer
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertTimer
parameter_list|(
name|Timer
name|timer
parameter_list|,
name|boolean
name|skipHistograms
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|timer
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"meanRate"
argument_list|,
name|timer
operator|.
name|getMeanRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"1minRate"
argument_list|,
name|timer
operator|.
name|getOneMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"5minRate"
argument_list|,
name|timer
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"15minRate"
argument_list|,
name|timer
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|skipHistograms
condition|)
block|{
comment|// time-based values in nanoseconds
name|addSnapshot
argument_list|(
name|response
argument_list|,
name|timer
operator|.
name|getSnapshot
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
end_function

begin_function
DECL|method|convertMeter
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertMeter
parameter_list|(
name|Meter
name|meter
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|meter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"meanRate"
argument_list|,
name|meter
operator|.
name|getMeanRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"1minRate"
argument_list|,
name|meter
operator|.
name|getOneMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"5minRate"
argument_list|,
name|meter
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"15minRate"
argument_list|,
name|meter
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
end_function

begin_function
DECL|method|convertGauge
specifier|static
name|Object
name|convertGauge
parameter_list|(
name|Gauge
name|gauge
parameter_list|,
name|boolean
name|compact
parameter_list|)
block|{
if|if
condition|(
name|compact
condition|)
block|{
return|return
name|gauge
operator|.
name|getValue
argument_list|()
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
name|gauge
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
end_function

begin_function
DECL|method|convertCounter
specifier|static
name|Object
name|convertCounter
parameter_list|(
name|Counter
name|counter
parameter_list|,
name|boolean
name|compact
parameter_list|)
block|{
if|if
condition|(
name|compact
condition|)
block|{
return|return
name|counter
operator|.
name|getCount
argument_list|()
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|response
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|response
operator|.
name|put
argument_list|(
literal|"count"
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
end_function

begin_comment
comment|/**    * Returns an instrumented wrapper over the given executor service.    */
end_comment

begin_function
DECL|method|instrumentedExecutorService
specifier|public
specifier|static
name|ExecutorService
name|instrumentedExecutorService
parameter_list|(
name|ExecutorService
name|delegate
parameter_list|,
name|MetricRegistry
name|metricRegistry
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
return|return
operator|new
name|InstrumentedExecutorService
argument_list|(
name|delegate
argument_list|,
name|metricRegistry
argument_list|,
name|scope
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

