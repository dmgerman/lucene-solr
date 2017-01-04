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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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

begin_class
DECL|class|SolrMetricTestUtils
specifier|public
specifier|final
class|class
name|SolrMetricTestUtils
block|{
DECL|field|MAX_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_ITERATIONS
init|=
literal|100
decl_stmt|;
DECL|field|CATEGORIES
specifier|private
specifier|static
specifier|final
name|SolrInfoMBean
operator|.
name|Category
name|CATEGORIES
index|[]
init|=
name|SolrInfoMBean
operator|.
name|Category
operator|.
name|values
argument_list|()
decl_stmt|;
DECL|method|getRandomScope
specifier|public
specifier|static
name|String
name|getRandomScope
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|getRandomScope
argument_list|(
name|random
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRandomScope
specifier|public
specifier|static
name|String
name|getRandomScope
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|shouldDefineScope
parameter_list|)
block|{
return|return
name|shouldDefineScope
condition|?
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
else|:
literal|null
return|;
comment|// must be simple string for JMX publishing
block|}
DECL|method|getRandomCategory
specifier|public
specifier|static
name|SolrInfoMBean
operator|.
name|Category
name|getRandomCategory
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|getRandomCategory
argument_list|(
name|random
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRandomCategory
specifier|public
specifier|static
name|SolrInfoMBean
operator|.
name|Category
name|getRandomCategory
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|shouldDefineCategory
parameter_list|)
block|{
return|return
name|shouldDefineCategory
condition|?
name|CATEGORIES
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
name|CATEGORIES
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
else|:
literal|null
return|;
block|}
DECL|method|getRandomMetrics
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|getRandomMetrics
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|getRandomMetrics
argument_list|(
name|random
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRandomMetrics
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|getRandomMetrics
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|shouldDefineMetrics
parameter_list|)
block|{
return|return
name|shouldDefineMetrics
condition|?
name|getRandomMetricsWithReplacements
argument_list|(
name|random
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
DECL|field|SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|SUFFIX
init|=
literal|"_testing"
decl_stmt|;
DECL|method|getRandomMetricsWithReplacements
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|getRandomMetricsWithReplacements
parameter_list|(
name|Random
name|random
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|existing
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|metrics
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|existingKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|existing
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numMetrics
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|MAX_ITERATIONS
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMetrics
condition|;
operator|++
name|i
control|)
block|{
name|boolean
name|shouldReplaceMetric
init|=
operator|!
name|existing
operator|.
name|isEmpty
argument_list|()
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|shouldReplaceMetric
condition|?
name|existingKeys
operator|.
name|get
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|0
argument_list|,
name|existingKeys
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
else|:
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
operator|+
name|SUFFIX
decl_stmt|;
comment|// must be simple string for JMX publishing
name|Counter
name|counter
init|=
operator|new
name|Counter
argument_list|()
decl_stmt|;
name|counter
operator|.
name|inc
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
return|return
name|metrics
return|;
block|}
DECL|method|getProducerOf
specifier|public
specifier|static
name|SolrMetricProducer
name|getProducerOf
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|SolrInfoMBean
operator|.
name|Category
name|category
parameter_list|,
name|String
name|scope
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|metrics
parameter_list|)
block|{
return|return
operator|new
name|SolrMetricProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
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
block|{
if|if
condition|(
name|category
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null category"
argument_list|)
throw|;
block|}
if|if
condition|(
name|metrics
operator|==
literal|null
operator|||
name|metrics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|entry
range|:
name|metrics
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|manager
operator|.
name|counter
argument_list|(
name|registry
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|category
operator|.
name|toString
argument_list|()
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SolrMetricProducer.of{"
operator|+
literal|"\ncategory="
operator|+
name|category
operator|+
literal|"\nscope="
operator|+
name|scope
operator|+
literal|"\nmetrics="
operator|+
name|metrics
operator|+
literal|"\n}"
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

