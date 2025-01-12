begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|ThreadInfo
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
name|ThreadMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|handler
operator|.
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|ID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_comment
comment|/**  *   * @since solr 1.2  */
end_comment

begin_class
DECL|class|ThreadDumpHandler
specifier|public
class|class
name|ThreadDumpHandler
extends|extends
name|RequestHandlerBase
block|{
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|system
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"system"
argument_list|,
name|system
argument_list|)
expr_stmt|;
name|ThreadMXBean
name|tmbean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
comment|// Thread Count
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|nl
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"current"
argument_list|,
name|tmbean
operator|.
name|getThreadCount
argument_list|()
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"peak"
argument_list|,
name|tmbean
operator|.
name|getPeakThreadCount
argument_list|()
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"daemon"
argument_list|,
name|tmbean
operator|.
name|getDaemonThreadCount
argument_list|()
argument_list|)
expr_stmt|;
name|system
operator|.
name|add
argument_list|(
literal|"threadCount"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
comment|// Deadlocks
name|ThreadInfo
index|[]
name|tinfos
decl_stmt|;
name|long
index|[]
name|tids
init|=
name|tmbean
operator|.
name|findMonitorDeadlockedThreads
argument_list|()
decl_stmt|;
if|if
condition|(
name|tids
operator|!=
literal|null
condition|)
block|{
name|tinfos
operator|=
name|tmbean
operator|.
name|getThreadInfo
argument_list|(
name|tids
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|lst
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|ti
range|:
name|tinfos
control|)
block|{
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"thread"
argument_list|,
name|getThreadInfo
argument_list|(
name|ti
argument_list|,
name|tmbean
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|system
operator|.
name|add
argument_list|(
literal|"deadlocks"
argument_list|,
name|lst
argument_list|)
expr_stmt|;
block|}
comment|// Now show all the threads....
name|tids
operator|=
name|tmbean
operator|.
name|getAllThreadIds
argument_list|()
expr_stmt|;
name|tinfos
operator|=
name|tmbean
operator|.
name|getThreadInfo
argument_list|(
name|tids
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|lst
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|ti
range|:
name|tinfos
control|)
block|{
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|"thread"
argument_list|,
name|getThreadInfo
argument_list|(
name|ti
argument_list|,
name|tmbean
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|system
operator|.
name|add
argument_list|(
literal|"threadDump"
argument_list|,
name|lst
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------------
comment|//--------------------------------------------------------------------------------
DECL|method|getThreadInfo
specifier|private
specifier|static
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getThreadInfo
parameter_list|(
name|ThreadInfo
name|ti
parameter_list|,
name|ThreadMXBean
name|tmbean
parameter_list|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|tid
init|=
name|ti
operator|.
name|getThreadId
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
name|ID
argument_list|,
name|tid
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|ti
operator|.
name|getThreadName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"state"
argument_list|,
name|ti
operator|.
name|getThreadState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|getLockName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"lock"
argument_list|,
name|ti
operator|.
name|getLockName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ti
operator|.
name|isSuspended
argument_list|()
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"suspended"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ti
operator|.
name|isInNative
argument_list|()
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"native"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tmbean
operator|.
name|isThreadCpuTimeSupported
argument_list|()
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"cpuTime"
argument_list|,
name|formatNanos
argument_list|(
name|tmbean
operator|.
name|getThreadCpuTime
argument_list|(
name|tid
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"userTime"
argument_list|,
name|formatNanos
argument_list|(
name|tmbean
operator|.
name|getThreadUserTime
argument_list|(
name|tid
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ti
operator|.
name|getLockOwnerName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|owner
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|owner
operator|.
name|add
argument_list|(
name|NAME
argument_list|,
name|ti
operator|.
name|getLockOwnerName
argument_list|()
argument_list|)
expr_stmt|;
name|owner
operator|.
name|add
argument_list|(
name|ID
argument_list|,
name|ti
operator|.
name|getLockOwnerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Add the stack trace
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|trace
init|=
operator|new
name|String
index|[
name|ti
operator|.
name|getStackTrace
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|ste
range|:
name|ti
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|trace
index|[
name|i
operator|++
index|]
operator|=
name|ste
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|add
argument_list|(
literal|"stackTrace"
argument_list|,
name|trace
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|formatNanos
specifier|private
specifier|static
name|String
name|formatNanos
parameter_list|(
name|long
name|ns
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.4fms"
argument_list|,
name|ns
operator|/
operator|(
name|double
operator|)
literal|1000000
argument_list|)
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Thread Dump"
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|ADMIN
return|;
block|}
block|}
end_class

end_unit

