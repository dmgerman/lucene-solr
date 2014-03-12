begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|lang
operator|.
name|System
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Thread
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/** A recursive timer.  *   * RTimers are started automatically when instantiated; subtimers are also  * started automatically when created.  *  * @since solr 1.3  *  */
end_comment

begin_class
DECL|class|RTimer
specifier|public
class|class
name|RTimer
block|{
DECL|field|STARTED
specifier|public
specifier|static
specifier|final
name|int
name|STARTED
init|=
literal|0
decl_stmt|;
DECL|field|STOPPED
specifier|public
specifier|static
specifier|final
name|int
name|STOPPED
init|=
literal|1
decl_stmt|;
DECL|field|PAUSED
specifier|public
specifier|static
specifier|final
name|int
name|PAUSED
init|=
literal|2
decl_stmt|;
DECL|field|state
specifier|protected
name|int
name|state
decl_stmt|;
DECL|field|startTime
specifier|protected
name|double
name|startTime
decl_stmt|;
DECL|field|time
specifier|protected
name|double
name|time
decl_stmt|;
DECL|field|culmTime
specifier|protected
name|double
name|culmTime
decl_stmt|;
DECL|field|children
specifier|protected
name|SimpleOrderedMap
argument_list|<
name|RTimer
argument_list|>
name|children
decl_stmt|;
DECL|method|RTimer
specifier|public
name|RTimer
parameter_list|()
block|{
name|time
operator|=
literal|0
expr_stmt|;
name|culmTime
operator|=
literal|0
expr_stmt|;
name|children
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|startTime
operator|=
name|now
argument_list|()
expr_stmt|;
name|state
operator|=
name|STARTED
expr_stmt|;
block|}
comment|/** Get current time    *    * May override to implement a different timer (CPU time, etc).    */
DECL|method|now
specifier|protected
name|double
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
comment|/** Recursively stop timer and sub timers */
DECL|method|stop
specifier|public
name|double
name|stop
parameter_list|()
block|{
assert|assert
name|state
operator|==
name|STARTED
operator|||
name|state
operator|==
name|PAUSED
assert|;
name|time
operator|=
name|culmTime
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|STARTED
condition|)
name|time
operator|+=
name|now
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|state
operator|=
name|STOPPED
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RTimer
argument_list|>
name|entry
range|:
name|children
control|)
block|{
name|RTimer
name|child
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|state
operator|==
name|STARTED
operator|||
name|child
operator|.
name|state
operator|==
name|PAUSED
condition|)
name|child
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
return|return
name|time
return|;
block|}
DECL|method|pause
specifier|public
name|void
name|pause
parameter_list|()
block|{
assert|assert
name|state
operator|==
name|STARTED
assert|;
name|culmTime
operator|+=
name|now
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|state
operator|=
name|PAUSED
expr_stmt|;
block|}
DECL|method|resume
specifier|public
name|void
name|resume
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
name|STARTED
condition|)
return|return;
assert|assert
name|state
operator|==
name|PAUSED
assert|;
name|state
operator|=
name|STARTED
expr_stmt|;
name|startTime
operator|=
name|now
argument_list|()
expr_stmt|;
block|}
comment|/** Get total elapsed time for this timer.    *    * Timer must be STOPped.    */
DECL|method|getTime
specifier|public
name|double
name|getTime
parameter_list|()
block|{
assert|assert
name|state
operator|==
name|STOPPED
assert|;
return|return
name|time
return|;
block|}
comment|/** Create new subtimer with given name    *    * Subtimer will be started.    */
DECL|method|sub
specifier|public
name|RTimer
name|sub
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|RTimer
name|child
init|=
name|children
operator|.
name|get
argument_list|(
name|desc
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|child
operator|=
operator|new
name|RTimer
argument_list|()
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|desc
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|child
return|;
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
name|asNamedList
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|asNamedList
specifier|public
name|NamedList
name|asNamedList
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|m
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|m
operator|.
name|add
argument_list|(
literal|"time"
argument_list|,
name|time
argument_list|)
expr_stmt|;
if|if
condition|(
name|children
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RTimer
argument_list|>
name|entry
range|:
name|children
control|)
block|{
name|m
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|asNamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|m
return|;
block|}
comment|/**    * Manipulating this map may have undefined results.    */
DECL|method|getChildren
specifier|public
name|SimpleOrderedMap
argument_list|<
name|RTimer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
comment|/*************** Testing *******/
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|RTimer
name|rt
init|=
operator|new
name|RTimer
argument_list|()
decl_stmt|,
name|subt
decl_stmt|,
name|st
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|subt
operator|=
name|rt
operator|.
name|sub
argument_list|(
literal|"sub1"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|st
operator|=
name|subt
operator|.
name|sub
argument_list|(
literal|"sub1.1"
argument_list|)
expr_stmt|;
name|st
operator|.
name|resume
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|st
operator|.
name|pause
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|st
operator|.
name|resume
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|st
operator|.
name|pause
argument_list|()
expr_stmt|;
name|subt
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rt
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

