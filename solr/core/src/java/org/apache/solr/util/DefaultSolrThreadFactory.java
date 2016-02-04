begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactory
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|DefaultSolrThreadFactory
specifier|public
class|class
name|DefaultSolrThreadFactory
implements|implements
name|ThreadFactory
block|{
DECL|field|poolNumber
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|poolNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|ThreadGroup
name|group
decl_stmt|;
DECL|field|threadNumber
specifier|private
specifier|final
name|AtomicInteger
name|threadNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|method|DefaultSolrThreadFactory
specifier|public
name|DefaultSolrThreadFactory
parameter_list|(
name|String
name|namePrefix
parameter_list|)
block|{
name|SecurityManager
name|s
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
name|group
operator|=
operator|(
name|s
operator|!=
literal|null
operator|)
condition|?
name|s
operator|.
name|getThreadGroup
argument_list|()
else|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
expr_stmt|;
name|prefix
operator|=
name|namePrefix
operator|+
literal|"-"
operator|+
name|poolNumber
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|"-thread-"
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newThread
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|group
argument_list|,
name|r
argument_list|,
name|prefix
operator|+
name|threadNumber
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|getPriority
argument_list|()
operator|!=
name|Thread
operator|.
name|NORM_PRIORITY
condition|)
name|t
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|NORM_PRIORITY
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

