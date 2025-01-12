begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|concurrent
operator|.
name|TimeUnit
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
comment|// this class may be accessed by multiple threads, but only one at a time
end_comment

begin_class
DECL|class|ActionThrottle
specifier|public
class|class
name|ActionThrottle
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
DECL|field|lastActionStartedAt
specifier|private
specifier|volatile
name|Long
name|lastActionStartedAt
decl_stmt|;
DECL|field|minMsBetweenActions
specifier|private
specifier|volatile
name|Long
name|minMsBetweenActions
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|nanoTimeSource
specifier|private
specifier|final
name|NanoTimeSource
name|nanoTimeSource
decl_stmt|;
DECL|interface|NanoTimeSource
specifier|public
interface|interface
name|NanoTimeSource
block|{
DECL|method|getTime
name|long
name|getTime
parameter_list|()
function_decl|;
block|}
DECL|class|DefaultNanoTimeSource
specifier|private
specifier|static
class|class
name|DefaultNanoTimeSource
implements|implements
name|NanoTimeSource
block|{
annotation|@
name|Override
DECL|method|getTime
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
return|;
block|}
block|}
DECL|method|ActionThrottle
specifier|public
name|ActionThrottle
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|minMsBetweenActions
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|minMsBetweenActions
operator|=
name|minMsBetweenActions
expr_stmt|;
name|this
operator|.
name|nanoTimeSource
operator|=
operator|new
name|DefaultNanoTimeSource
argument_list|()
expr_stmt|;
block|}
DECL|method|ActionThrottle
specifier|public
name|ActionThrottle
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|minMsBetweenActions
parameter_list|,
name|NanoTimeSource
name|nanoTimeSource
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|minMsBetweenActions
operator|=
name|minMsBetweenActions
expr_stmt|;
name|this
operator|.
name|nanoTimeSource
operator|=
name|nanoTimeSource
expr_stmt|;
block|}
DECL|method|markAttemptingAction
specifier|public
name|void
name|markAttemptingAction
parameter_list|()
block|{
name|lastActionStartedAt
operator|=
name|nanoTimeSource
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
DECL|method|minimumWaitBetweenActions
specifier|public
name|void
name|minimumWaitBetweenActions
parameter_list|()
block|{
if|if
condition|(
name|lastActionStartedAt
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|long
name|diff
init|=
name|nanoTimeSource
operator|.
name|getTime
argument_list|()
operator|-
name|lastActionStartedAt
decl_stmt|;
name|int
name|diffMs
init|=
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|diff
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|long
name|minNsBetweenActions
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|minMsBetweenActions
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"The last {} attempt started {}ms ago."
argument_list|,
name|name
argument_list|,
name|diffMs
argument_list|)
expr_stmt|;
name|int
name|sleep
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|diffMs
operator|>
literal|0
operator|&&
name|diff
operator|<
name|minNsBetweenActions
condition|)
block|{
name|sleep
operator|=
operator|(
name|int
operator|)
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|minNsBetweenActions
operator|-
name|diff
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|diffMs
operator|==
literal|0
condition|)
block|{
name|sleep
operator|=
name|minMsBetweenActions
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sleep
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Throttling {} attempts - waiting for {}ms"
argument_list|,
name|name
argument_list|,
name|sleep
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

