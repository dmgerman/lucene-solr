begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|core
operator|.
name|SolrCore
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
operator|.
name|Category
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
name|search
operator|.
name|SolrCache
operator|.
name|State
import|;
end_import

begin_comment
comment|/**  * Common base class of reusable functionality for SolrCaches  */
end_comment

begin_class
DECL|class|SolrCacheBase
specifier|public
specifier|abstract
class|class
name|SolrCacheBase
block|{
DECL|field|regenerator
specifier|protected
name|CacheRegenerator
name|regenerator
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|autowarm
specifier|protected
name|AutoWarmCountRef
name|autowarm
decl_stmt|;
comment|/**    * Decides how many things to autowarm based on the size of another cache    */
DECL|class|AutoWarmCountRef
specifier|public
specifier|static
class|class
name|AutoWarmCountRef
block|{
DECL|field|autoWarmCount
specifier|private
specifier|final
name|int
name|autoWarmCount
decl_stmt|;
DECL|field|autoWarmPercentage
specifier|private
specifier|final
name|int
name|autoWarmPercentage
decl_stmt|;
DECL|field|autoWarmByPercentage
specifier|private
specifier|final
name|boolean
name|autoWarmByPercentage
decl_stmt|;
DECL|field|doAutoWarming
specifier|private
specifier|final
name|boolean
name|doAutoWarming
decl_stmt|;
DECL|field|strVal
specifier|private
specifier|final
name|String
name|strVal
decl_stmt|;
DECL|method|AutoWarmCountRef
specifier|public
name|AutoWarmCountRef
parameter_list|(
specifier|final
name|String
name|configValue
parameter_list|)
block|{
try|try
block|{
name|String
name|input
init|=
operator|(
literal|null
operator|==
name|configValue
operator|)
condition|?
literal|"0"
else|:
name|configValue
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// odd undocumented legacy behavior, -1 meant "all" (now "100%")
name|strVal
operator|=
operator|(
literal|"-1"
operator|.
name|equals
argument_list|(
name|input
argument_list|)
operator|)
condition|?
literal|"100%"
else|:
name|input
expr_stmt|;
if|if
condition|(
name|strVal
operator|.
name|indexOf
argument_list|(
literal|"%"
argument_list|)
operator|==
operator|(
name|strVal
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
condition|)
block|{
name|autoWarmCount
operator|=
literal|0
expr_stmt|;
name|autoWarmPercentage
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|strVal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|strVal
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|autoWarmByPercentage
operator|=
literal|true
expr_stmt|;
name|doAutoWarming
operator|=
operator|(
literal|0
operator|<
name|autoWarmPercentage
operator|)
expr_stmt|;
block|}
else|else
block|{
name|autoWarmCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|strVal
argument_list|)
expr_stmt|;
name|autoWarmPercentage
operator|=
literal|0
expr_stmt|;
name|autoWarmByPercentage
operator|=
literal|false
expr_stmt|;
name|doAutoWarming
operator|=
operator|(
literal|0
operator|<
name|autoWarmCount
operator|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't parse autoWarm value: "
operator|+
name|configValue
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|strVal
return|;
block|}
DECL|method|isAutoWarmingOn
specifier|public
name|boolean
name|isAutoWarmingOn
parameter_list|()
block|{
return|return
name|doAutoWarming
return|;
block|}
DECL|method|getWarmCount
specifier|public
name|int
name|getWarmCount
parameter_list|(
specifier|final
name|int
name|previousCacheSize
parameter_list|)
block|{
return|return
name|autoWarmByPercentage
condition|?
operator|(
name|previousCacheSize
operator|*
name|autoWarmPercentage
operator|)
operator|/
literal|100
else|:
name|Math
operator|.
name|min
argument_list|(
name|previousCacheSize
argument_list|,
name|autoWarmCount
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns a "Hit Ratio" (ie: max of 1.00, not a percentage) suitable for     * display purposes.    */
DECL|method|calcHitRatio
specifier|protected
specifier|static
name|String
name|calcHitRatio
parameter_list|(
name|long
name|lookups
parameter_list|,
name|long
name|hits
parameter_list|)
block|{
if|if
condition|(
name|lookups
operator|==
literal|0
condition|)
return|return
literal|"0.00"
return|;
if|if
condition|(
name|lookups
operator|==
name|hits
condition|)
return|return
literal|"1.00"
return|;
name|int
name|hundredths
init|=
call|(
name|int
call|)
argument_list|(
name|hits
operator|*
literal|100
operator|/
name|lookups
argument_list|)
decl_stmt|;
comment|// rounded down
if|if
condition|(
name|hundredths
operator|<
literal|10
condition|)
return|return
literal|"0.0"
operator|+
name|hundredths
return|;
return|return
literal|"0."
operator|+
name|hundredths
return|;
comment|/*** code to produce a percent, if we want it...     int ones = (int)(hits*100 / lookups);     int tenths = (int)(hits*1000 / lookups) - ones*10;     return Integer.toString(ones) + '.' + tenths;     ***/
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|CACHE
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|,
name|CacheRegenerator
name|regenerator
parameter_list|)
block|{
name|this
operator|.
name|regenerator
operator|=
name|regenerator
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|CREATED
expr_stmt|;
name|name
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|autowarm
operator|=
operator|new
name|AutoWarmCountRef
argument_list|(
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"autowarmCount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getAutowarmDescription
specifier|protected
name|String
name|getAutowarmDescription
parameter_list|()
block|{
return|return
literal|"autowarmCount="
operator|+
name|autowarm
operator|+
literal|", regenerator="
operator|+
name|regenerator
return|;
block|}
DECL|method|isAutowarmingOn
specifier|protected
name|boolean
name|isAutowarmingOn
parameter_list|()
block|{
return|return
name|autowarm
operator|.
name|isAutoWarmingOn
argument_list|()
return|;
block|}
DECL|method|setState
specifier|public
name|void
name|setState
parameter_list|(
name|State
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
block|}
end_class

end_unit

