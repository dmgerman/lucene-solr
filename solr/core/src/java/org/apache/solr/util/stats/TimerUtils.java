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
comment|/**  * Solr specific {@link Timer} utility functions.  */
end_comment

begin_class
DECL|class|TimerUtils
specifier|public
class|class
name|TimerUtils
block|{
DECL|field|RATE_FACTOR
specifier|private
specifier|static
specifier|final
name|double
name|RATE_FACTOR
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toSeconds
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
literal|"avgRequestsPerMinute"
argument_list|,
name|convertRateToPerMinute
argument_list|(
name|timer
operator|.
name|getMeanRate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"5minRateRequestsPerMinute"
argument_list|,
name|convertRateToPerMinute
argument_list|(
name|timer
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"15minRateRequestsPerMinute"
argument_list|,
name|convertRateToPerMinute
argument_list|(
name|timer
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
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
DECL|method|convertRateToPerMinute
specifier|static
name|double
name|convertRateToPerMinute
parameter_list|(
name|double
name|rate
parameter_list|)
block|{
return|return
name|rate
operator|*
name|RATE_FACTOR
return|;
block|}
block|}
end_class

end_unit

