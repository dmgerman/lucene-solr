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
name|javax
operator|.
name|management
operator|.
name|JMException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanAttributeInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|JmxAttributeGauge
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
comment|/**  * This is an extended replacement for {@link com.codahale.metrics.jvm.FileDescriptorRatioGauge}  * - that class uses reflection and doesn't work under Java 9. We can also get much more  * information about OS environment once we have to go through MBeanServer anyway.  */
end_comment

begin_class
DECL|class|OperatingSystemMetricSet
specifier|public
class|class
name|OperatingSystemMetricSet
implements|implements
name|MetricSet
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
comment|/** Metric names - these correspond to known numeric MBean attributes. Depending on the OS and    * Java implementation only some of them may be actually present.    */
DECL|field|METRICS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|METRICS
init|=
block|{
literal|"AvailableProcessors"
block|,
literal|"CommittedVirtualMemorySize"
block|,
literal|"FreePhysicalMemorySize"
block|,
literal|"FreeSwapSpaceSize"
block|,
literal|"MaxFileDescriptorCount"
block|,
literal|"OpenFileDescriptorCount"
block|,
literal|"ProcessCpuLoad"
block|,
literal|"ProcessCpuTime"
block|,
literal|"SystemLoadAverage"
block|,
literal|"TotalPhysicalMemorySize"
block|,
literal|"TotalSwapSpaceSize"
block|}
decl_stmt|;
DECL|field|mBeanServer
specifier|private
specifier|final
name|MBeanServer
name|mBeanServer
decl_stmt|;
DECL|method|OperatingSystemMetricSet
specifier|public
name|OperatingSystemMetricSet
parameter_list|(
name|MBeanServer
name|mBeanServer
parameter_list|)
block|{
name|this
operator|.
name|mBeanServer
operator|=
name|mBeanServer
expr_stmt|;
block|}
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
try|try
block|{
specifier|final
name|ObjectName
name|on
init|=
operator|new
name|ObjectName
argument_list|(
literal|"java.lang:type=OperatingSystem"
argument_list|)
decl_stmt|;
comment|// verify that it exists
name|MBeanInfo
name|info
init|=
name|mBeanServer
operator|.
name|getMBeanInfo
argument_list|(
name|on
argument_list|)
decl_stmt|;
comment|// collect valid attributes
name|Set
argument_list|<
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MBeanAttributeInfo
name|ai
range|:
name|info
operator|.
name|getAttributes
argument_list|()
control|)
block|{
name|attributes
operator|.
name|add
argument_list|(
name|ai
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|metric
range|:
name|METRICS
control|)
block|{
comment|// verify that an attribute exists before attempting to add it
if|if
condition|(
name|attributes
operator|.
name|contains
argument_list|(
name|metric
argument_list|)
condition|)
block|{
name|metrics
operator|.
name|put
argument_list|(
name|metric
argument_list|,
operator|new
name|JmxAttributeGauge
argument_list|(
name|mBeanServer
argument_list|,
name|on
argument_list|,
name|metric
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMException
name|ignored
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to load OperatingSystem MBean"
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
return|return
name|metrics
return|;
block|}
block|}
end_class

end_unit

