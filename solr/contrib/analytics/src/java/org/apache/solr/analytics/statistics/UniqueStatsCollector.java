begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.statistics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|statistics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Set
import|;
end_import

begin_comment
comment|/**  *<code>UniqueValueCounter</code> computes the number of unique values.  */
end_comment

begin_class
DECL|class|UniqueStatsCollector
specifier|public
class|class
name|UniqueStatsCollector
extends|extends
name|AbstractDelegatingStatsCollector
block|{
DECL|field|uniqueValues
specifier|private
specifier|final
name|Set
argument_list|<
name|Object
argument_list|>
name|uniqueValues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|UniqueStatsCollector
specifier|public
name|UniqueStatsCollector
parameter_list|(
name|StatsCollector
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|exists
condition|)
block|{
name|uniqueValues
operator|.
name|add
argument_list|(
name|value
operator|.
name|toObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getStat
specifier|public
name|Comparable
name|getStat
parameter_list|(
name|String
name|stat
parameter_list|)
block|{
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"unique"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
return|return
name|delegate
operator|.
name|getStat
argument_list|(
name|stat
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compute
specifier|public
name|void
name|compute
parameter_list|()
block|{
name|delegate
operator|.
name|compute
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

