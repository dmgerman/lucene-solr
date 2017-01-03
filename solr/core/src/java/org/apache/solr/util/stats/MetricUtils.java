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
name|util
operator|.
name|NamedList
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
comment|/**    * Adds metrics from a Timer to a NamedList, using well-known names.    * @param lst The NamedList to add the metrics data to    * @param timer The Timer to extract the metrics from    */
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
comment|/**    * Returns a NamedList respresentation of the given metric registry. Only those metrics    * are converted to NamedList which match at least one of the given MetricFilter instances.    *    * @param registry      the {@link MetricRegistry} to be converted to NamedList    * @param metricFilters a list of {@link MetricFilter} instances    * @return a {@link NamedList}    */
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
name|metricFilters
parameter_list|)
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
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
name|metricFilters
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
argument_list|;       if
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
name|response
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|counterToNamedList
argument_list|(
name|counter
argument_list|)
argument_list|)
block|;       }
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
name|response
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|gaugeToNamedList
argument_list|(
name|gauge
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|meterToNamedList
argument_list|(
name|meter
argument_list|)
argument_list|)
block|;       }
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
name|response
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|timerToNamedList
argument_list|(
name|timer
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
name|Histogram
name|histogram
operator|=
operator|(
name|Histogram
operator|)
name|metric
block|;
name|response
operator|.
name|add
argument_list|(
name|n
argument_list|,
name|histogramToNamedList
argument_list|(
name|histogram
argument_list|)
argument_list|)
block|;       }
block|}
block|)
class|;
end_class

begin_return
return|return
name|response
return|;
end_return

begin_function
unit|}    static
DECL|method|histogramToNamedList
name|NamedList
name|histogramToNamedList
parameter_list|(
name|Histogram
name|histogram
parameter_list|)
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
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
name|add
argument_list|(
literal|"requests"
argument_list|,
name|histogram
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"minTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"maxTime"
argument_list|,
name|nsToMs
argument_list|(
name|snapshot
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
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
name|response
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
name|response
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
name|response
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
name|response
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
name|response
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
return|return
name|response
return|;
block|}
end_function

begin_function
DECL|method|timerToNamedList
specifier|static
name|NamedList
name|timerToNamedList
parameter_list|(
name|Timer
name|timer
parameter_list|)
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|addMetrics
argument_list|(
name|response
argument_list|,
name|timer
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
end_function

begin_function
DECL|method|meterToNamedList
specifier|static
name|NamedList
name|meterToNamedList
parameter_list|(
name|Meter
name|meter
parameter_list|)
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|meter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"avgRequestsPerSecond"
argument_list|,
name|meter
operator|.
name|getMeanRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"1minRateRequestsPerSecond"
argument_list|,
name|meter
operator|.
name|getOneMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"5minRateRequestsPerSecond"
argument_list|,
name|meter
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"15minRateRequestsPerSecond"
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
DECL|method|gaugeToNamedList
specifier|static
name|NamedList
name|gaugeToNamedList
parameter_list|(
name|Gauge
name|gauge
parameter_list|)
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|response
operator|.
name|add
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
end_function

begin_function
DECL|method|counterToNamedList
specifier|static
name|NamedList
name|counterToNamedList
parameter_list|(
name|Counter
name|counter
parameter_list|)
block|{
name|NamedList
name|response
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
literal|"requests"
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

