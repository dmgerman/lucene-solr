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
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|OperatingSystemMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|MetricSet
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
comment|/**  * This is an extended replacement for {@link com.codahale.metrics.jvm.FileDescriptorRatioGauge}  * - that class uses reflection and doesn't work under Java 9. This implementation tries to retrieve  * bean properties from known implementations of {@link java.lang.management.OperatingSystemMXBean}.  */
end_comment

begin_class
DECL|class|OperatingSystemMetricSet
specifier|public
class|class
name|OperatingSystemMetricSet
implements|implements
name|MetricSet
block|{
annotation|@
name|Override
DECL|method|getMetrics
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|getMetrics
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|metrics
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|OperatingSystemMXBean
name|os
init|=
name|ManagementFactory
operator|.
name|getOperatingSystemMXBean
argument_list|()
decl_stmt|;
name|MetricUtils
operator|.
name|addMXBeanMetrics
argument_list|(
name|os
argument_list|,
name|MetricUtils
operator|.
name|OS_MXBEAN_CLASSES
argument_list|,
literal|null
argument_list|,
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
if|if
condition|(
operator|!
name|metrics
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
condition|)
block|{
name|metrics
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|metrics
return|;
block|}
block|}
end_class

end_unit

