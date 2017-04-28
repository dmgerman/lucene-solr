begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|annotation
operator|.
name|Experimental
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
name|SolrException
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
name|ExecutorUtil
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
name|logging
operator|.
name|MDCLoggingContext
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
name|DefaultSolrThreadFactory
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Observable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observer
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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|ConcurrentHashMap
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
name|ExecutorService
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

begin_class
DECL|class|SolrCores
class|class
name|SolrCores
implements|implements
name|Observer
block|{
DECL|field|modifyLock
specifier|private
specifier|static
name|Object
name|modifyLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// for locking around manipulating any of the core maps.
DECL|field|cores
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrCore
argument_list|>
name|cores
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// For "permanent" cores
comment|// These descriptors, once loaded, will _not_ be unloaded, i.e. they are not "transient".
DECL|field|residentDesciptors
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CoreDescriptor
argument_list|>
name|residentDesciptors
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|CoreContainer
name|container
decl_stmt|;
DECL|field|currentlyLoadingCores
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|currentlyLoadingCores
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
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
comment|// This map will hold objects that are being currently operated on. The core (value) may be null in the case of
comment|// initial load. The rule is, never to any operation on a core that is currently being operated upon.
DECL|field|pendingCoreOps
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|pendingCoreOps
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Due to the fact that closes happen potentially whenever anything is _added_ to the transient core list, we need
comment|// to essentially queue them up to be handled via pendingCoreOps.
DECL|field|pendingCloses
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|SolrCore
argument_list|>
name|pendingCloses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SolrCores
name|SolrCores
parameter_list|(
name|CoreContainer
name|container
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
block|}
DECL|method|addCoreDescriptor
specifier|protected
name|void
name|addCoreDescriptor
parameter_list|(
name|CoreDescriptor
name|p
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|p
operator|.
name|isTransient
argument_list|()
condition|)
block|{
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|addTransientDescriptor
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|residentDesciptors
operator|.
name|put
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|removeCoreDescriptor
specifier|protected
name|void
name|removeCoreDescriptor
parameter_list|(
name|CoreDescriptor
name|p
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|p
operator|.
name|isTransient
argument_list|()
condition|)
block|{
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|removeTransientDescriptor
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|residentDesciptors
operator|.
name|remove
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// We are shutting down. You can't hold the lock on the various lists of cores while they shut down, so we need to
comment|// make a temporary copy of the names and shut them down outside the lock.
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|()
block|{
name|waitForLoadingCoresToFinish
argument_list|(
literal|30
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|SolrCore
argument_list|>
name|coreList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|TransientSolrCoreCache
name|transientSolrCoreCache
init|=
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
decl_stmt|;
comment|// Release observer
if|if
condition|(
name|transientSolrCoreCache
operator|!=
literal|null
condition|)
block|{
name|transientSolrCoreCache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// It might be possible for one of the cores to move from one list to another while we're closing them. So
comment|// loop through the lists until they're all empty. In particular, the core could have moved from the transient
comment|// list to the pendingCloses list.
do|do
block|{
name|coreList
operator|.
name|clear
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
comment|// make a copy of the cores then clear the map so the core isn't handed out to a request again
name|coreList
operator|.
name|addAll
argument_list|(
name|cores
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|cores
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|transientSolrCoreCache
operator|!=
literal|null
condition|)
block|{
name|coreList
operator|.
name|addAll
argument_list|(
name|transientSolrCoreCache
operator|.
name|prepareForShutdown
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|coreList
operator|.
name|addAll
argument_list|(
name|pendingCloses
argument_list|)
expr_stmt|;
name|pendingCloses
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|ExecutorService
name|coreCloseExecutor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareFixedThreadPool
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"coreCloseExecutor"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|SolrCore
name|core
range|:
name|coreList
control|)
block|{
name|coreCloseExecutor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
name|MDCLoggingContext
operator|.
name|setCore
argument_list|(
name|core
argument_list|)
expr_stmt|;
try|try
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error shutting down core"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|MDCLoggingContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|core
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|coreCloseExecutor
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|coreList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
do|;
block|}
comment|//WARNING! This should be the _only_ place you put anything into the list of transient cores!
DECL|method|putTransientCore
specifier|protected
name|SolrCore
name|putTransientCore
parameter_list|(
name|NodeConfig
name|cfg
parameter_list|,
name|String
name|name
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
name|SolrCore
name|retCore
init|=
literal|null
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Opening transient core {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|retCore
operator|=
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|addCore
argument_list|(
name|name
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|retCore
return|;
block|}
comment|// Returns the old core if there was a core of the same name.
DECL|method|putCore
specifier|protected
name|SolrCore
name|putCore
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|cd
operator|.
name|isTransient
argument_list|()
condition|)
block|{
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|addCore
argument_list|(
name|cd
operator|.
name|getName
argument_list|()
argument_list|,
name|core
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|cores
operator|.
name|put
argument_list|(
name|cd
operator|.
name|getName
argument_list|()
argument_list|,
name|core
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    *    * @return A list of "permanent" cores, i.e. cores that  may not be swapped out and are currently loaded.    *     * A core may be non-transient but still lazily loaded. If it is "permanent" and lazy-load _and_    * not yet loaded it will _not_ be returned by this call.    *     * Note: This is one of the places where SolrCloud is incompatible with Transient Cores. This call is used in     * cancelRecoveries, transient cores don't participate.    */
DECL|method|getCores
name|List
argument_list|<
name|SolrCore
argument_list|>
name|getCores
parameter_list|()
block|{
name|List
argument_list|<
name|SolrCore
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|lst
operator|.
name|addAll
argument_list|(
name|cores
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
comment|/**    * Gets the cores that are currently loaded, i.e. cores that have    * 1> loadOnStartup=true and are either not-transient or, if transient, have been loaded and have not been swapped out    * 2> loadOnStartup=false and have been loaded but either non-transient or have not been swapped out.    *     * Put another way, this will not return any names of cores that are lazily loaded but have not been called for yet    * or are transient and either not loaded or have been swapped out.    *     * @return List of currently loaded cores.    */
DECL|method|getLoadedCoreNames
name|Set
argument_list|<
name|String
argument_list|>
name|getLoadedCoreNames
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|cores
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|getLoadedCoreNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
comment|/** This method is currently experimental.    * @return a Collection of the names that a specific core is mapped to.    *     * Note: this implies that the core is loaded    */
annotation|@
name|Experimental
DECL|method|getCoreNames
name|List
argument_list|<
name|String
argument_list|>
name|getCoreNames
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrCore
argument_list|>
name|entry
range|:
name|cores
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|core
operator|==
name|entry
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|lst
operator|.
name|addAll
argument_list|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|getNamesForCore
argument_list|(
name|core
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|lst
return|;
block|}
comment|/**    * Gets a list of all cores, loaded and unloaded     *    * @return all cores names, whether loaded or unloaded, transient or permenent.    */
DECL|method|getAllCoreNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getAllCoreNames
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|cores
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|getAllCoreNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|addAll
argument_list|(
name|residentDesciptors
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|getCore
name|SolrCore
name|getCore
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
return|return
name|cores
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
DECL|method|swap
specifier|protected
name|void
name|swap
parameter_list|(
name|String
name|n0
parameter_list|,
name|String
name|n1
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|SolrCore
name|c0
init|=
name|cores
operator|.
name|get
argument_list|(
name|n0
argument_list|)
decl_stmt|;
name|SolrCore
name|c1
init|=
name|cores
operator|.
name|get
argument_list|(
name|n1
argument_list|)
decl_stmt|;
if|if
condition|(
name|c0
operator|==
literal|null
condition|)
block|{
comment|// Might be an unloaded transient core
name|c0
operator|=
name|container
operator|.
name|getCore
argument_list|(
name|n0
argument_list|)
expr_stmt|;
if|if
condition|(
name|c0
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No such core: "
operator|+
name|n0
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|c1
operator|==
literal|null
condition|)
block|{
comment|// Might be an unloaded transient core
name|c1
operator|=
name|container
operator|.
name|getCore
argument_list|(
name|n1
argument_list|)
expr_stmt|;
if|if
condition|(
name|c1
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No such core: "
operator|+
name|n1
argument_list|)
throw|;
block|}
block|}
comment|// When we swap the cores, we also need to swap the associated core descriptors. Note, this changes the
comment|// name of the coreDescriptor by virtue of the c-tor
name|CoreDescriptor
name|cd1
init|=
name|c1
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|addCoreDescriptor
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
name|n1
argument_list|,
name|c0
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addCoreDescriptor
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
name|n0
argument_list|,
name|cd1
argument_list|)
argument_list|)
expr_stmt|;
name|cores
operator|.
name|put
argument_list|(
name|n0
argument_list|,
name|c1
argument_list|)
expr_stmt|;
name|cores
operator|.
name|put
argument_list|(
name|n1
argument_list|,
name|c0
argument_list|)
expr_stmt|;
name|c0
operator|.
name|setName
argument_list|(
name|n1
argument_list|)
expr_stmt|;
name|c1
operator|.
name|setName
argument_list|(
name|n0
argument_list|)
expr_stmt|;
name|container
operator|.
name|getMetricManager
argument_list|()
operator|.
name|swapRegistries
argument_list|(
name|c0
operator|.
name|getCoreMetricManager
argument_list|()
operator|.
name|getRegistryName
argument_list|()
argument_list|,
name|c1
operator|.
name|getCoreMetricManager
argument_list|()
operator|.
name|getRegistryName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|remove
specifier|protected
name|SolrCore
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|SolrCore
name|ret
init|=
name|cores
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// It could have been a newly-created core. It could have been a transient core. The newly-created cores
comment|// in particular should be checked. It could have been a dynamic core.
name|TransientSolrCoreCache
name|transientHandler
init|=
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
operator|&&
name|transientHandler
operator|!=
literal|null
condition|)
block|{
name|ret
operator|=
name|transientHandler
operator|.
name|removeCore
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
comment|/* If you don't increment the reference count, someone could close the core before you use it. */
DECL|method|getCoreFromAnyList
name|SolrCore
name|getCoreFromAnyList
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|incRefCount
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|SolrCore
name|core
init|=
name|cores
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
operator|&&
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|core
operator|=
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|getCore
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|core
operator|!=
literal|null
operator|&&
name|incRefCount
condition|)
block|{
name|core
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
return|return
name|core
return|;
block|}
block|}
comment|// See SOLR-5366 for why the UNLOAD command needs to know whether a core is actually loaded or not, it might have
comment|// to close the core. However, there's a race condition. If the core happens to be in the pending "to close" queue,
comment|// we should NOT close it in unload core.
DECL|method|isLoadedNotPendingClose
specifier|protected
name|boolean
name|isLoadedNotPendingClose
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// Just all be synchronized
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|cores
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
operator|&&
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|containsCore
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// Check pending
for|for
control|(
name|SolrCore
name|core
range|:
name|pendingCloses
control|)
block|{
if|if
condition|(
name|core
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|isLoaded
specifier|protected
name|boolean
name|isLoaded
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|cores
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|!=
literal|null
operator|&&
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|containsCore
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|getUnloadedCoreDescriptor
specifier|protected
name|CoreDescriptor
name|getUnloadedCoreDescriptor
parameter_list|(
name|String
name|cname
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|CoreDescriptor
name|desc
init|=
name|residentDesciptors
operator|.
name|get
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|desc
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|desc
operator|=
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|getTransientDescriptor
argument_list|(
name|cname
argument_list|)
expr_stmt|;
if|if
condition|(
name|desc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
operator|new
name|CoreDescriptor
argument_list|(
name|cname
argument_list|,
name|desc
argument_list|)
return|;
block|}
block|}
comment|// Wait here until any pending operations (load, unload or reload) are completed on this core.
DECL|method|waitAddPendingCoreOps
specifier|protected
name|SolrCore
name|waitAddPendingCoreOps
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// Keep multiple threads from operating on a core at one time.
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|boolean
name|pending
decl_stmt|;
do|do
block|{
comment|// Are we currently doing anything to this core? Loading, unloading, reloading?
name|pending
operator|=
name|pendingCoreOps
operator|.
name|contains
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// wait for the core to be done being operated upon
if|if
condition|(
operator|!
name|pending
condition|)
block|{
comment|// Linear list, but shouldn't be too long
for|for
control|(
name|SolrCore
name|core
range|:
name|pendingCloses
control|)
block|{
if|if
condition|(
name|core
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|pending
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|container
operator|.
name|isShutDown
argument_list|()
condition|)
return|return
literal|null
return|;
comment|// Just stop already.
if|if
condition|(
name|pending
condition|)
block|{
try|try
block|{
name|modifyLock
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// Seems best not to do anything at all if the thread is interrupted
block|}
block|}
block|}
do|while
condition|(
name|pending
condition|)
do|;
comment|// We _really_ need to do this within the synchronized block!
if|if
condition|(
operator|!
name|container
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|pendingCoreOps
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Replaced an entry in pendingCoreOps {}, we should not be doing this"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|getCoreFromAnyList
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
return|;
comment|// we might have been _unloading_ the core, so return the core if it was loaded.
block|}
block|}
return|return
literal|null
return|;
block|}
comment|// We should always be removing the first thing in the list with our name! The idea here is to NOT do anything n
comment|// any core while some other operation is working on that core.
DECL|method|removeFromPendingOps
specifier|protected
name|void
name|removeFromPendingOps
parameter_list|(
name|String
name|name
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
operator|!
name|pendingCoreOps
operator|.
name|remove
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Tried to remove core {} from pendingCoreOps and it wasn't there. "
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|modifyLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getModifyLock
specifier|protected
name|Object
name|getModifyLock
parameter_list|()
block|{
return|return
name|modifyLock
return|;
block|}
comment|// Be a little careful. We don't want to either open or close a core unless it's _not_ being opened or closed by
comment|// another thread. So within this lock we'll walk along the list of pending closes until we find something NOT in
comment|// the list of threads currently being loaded or reloaded. The "usual" case will probably return the very first
comment|// one anyway..
DECL|method|getCoreToClose
specifier|protected
name|SolrCore
name|getCoreToClose
parameter_list|()
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
for|for
control|(
name|SolrCore
name|core
range|:
name|pendingCloses
control|)
block|{
if|if
condition|(
operator|!
name|pendingCoreOps
operator|.
name|contains
argument_list|(
name|core
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|pendingCoreOps
operator|.
name|add
argument_list|(
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|pendingCloses
operator|.
name|remove
argument_list|(
name|core
argument_list|)
expr_stmt|;
return|return
name|core
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Return the CoreDescriptor corresponding to a given core name.    * Blocks if the SolrCore is still loading until it is ready.    * @param coreName the name of the core    * @return the CoreDescriptor    */
DECL|method|getCoreDescriptor
specifier|public
name|CoreDescriptor
name|getCoreDescriptor
parameter_list|(
name|String
name|coreName
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
if|if
condition|(
name|residentDesciptors
operator|.
name|containsKey
argument_list|(
name|coreName
argument_list|)
condition|)
return|return
name|residentDesciptors
operator|.
name|get
argument_list|(
name|coreName
argument_list|)
return|;
return|return
name|container
operator|.
name|getTransientCacheHandler
argument_list|()
operator|.
name|getTransientDescriptor
argument_list|(
name|coreName
argument_list|)
return|;
block|}
block|}
comment|/**    * Get the CoreDescriptors for every SolrCore managed here    * @return a List of CoreDescriptors    */
DECL|method|getCoreDescriptors
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCoreDescriptors
parameter_list|()
block|{
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|cds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
for|for
control|(
name|String
name|coreName
range|:
name|getAllCoreNames
argument_list|()
control|)
block|{
comment|// TODO: This null check is a bit suspicious - it seems that
comment|// getAllCoreNames might return deleted cores as well?
name|CoreDescriptor
name|cd
init|=
name|getCoreDescriptor
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|cd
operator|!=
literal|null
condition|)
name|cds
operator|.
name|add
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|cds
return|;
block|}
comment|// cores marked as loading will block on getCore
DECL|method|markCoreAsLoading
specifier|public
name|void
name|markCoreAsLoading
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|currentlyLoadingCores
operator|.
name|add
argument_list|(
name|cd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//cores marked as loading will block on getCore
DECL|method|markCoreAsNotLoading
specifier|public
name|void
name|markCoreAsNotLoading
parameter_list|(
name|CoreDescriptor
name|cd
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|currentlyLoadingCores
operator|.
name|remove
argument_list|(
name|cd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// returns when no cores are marked as loading
DECL|method|waitForLoadingCoresToFinish
specifier|public
name|void
name|waitForLoadingCoresToFinish
parameter_list|(
name|long
name|timeoutMs
parameter_list|)
block|{
name|long
name|time
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|timeout
init|=
name|time
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
while|while
condition|(
operator|!
name|currentlyLoadingCores
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|modifyLock
operator|.
name|wait
argument_list|(
literal|500
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
if|if
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|>=
name|timeout
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Timed out waiting for SolrCores to finish loading."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|// returns when core is finished loading, throws exception if no such core loading or loaded
DECL|method|waitForLoadingCoreToFinish
specifier|public
name|void
name|waitForLoadingCoreToFinish
parameter_list|(
name|String
name|core
parameter_list|,
name|long
name|timeoutMs
parameter_list|)
block|{
name|long
name|time
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|timeout
init|=
name|time
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
while|while
condition|(
name|isCoreLoading
argument_list|(
name|core
argument_list|)
condition|)
block|{
try|try
block|{
name|modifyLock
operator|.
name|wait
argument_list|(
literal|500
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
if|if
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|>=
name|timeout
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Timed out waiting for SolrCore, {},  to finish loading."
argument_list|,
name|core
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
DECL|method|isCoreLoading
specifier|public
name|boolean
name|isCoreLoading
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|currentlyLoadingCores
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|// Let transient cache implementation tell us when it ages out a corel
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|Observable
name|o
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
synchronized|synchronized
init|(
name|modifyLock
init|)
block|{
name|pendingCloses
operator|.
name|add
argument_list|(
operator|(
name|SolrCore
operator|)
name|arg
argument_list|)
expr_stmt|;
comment|// Essentially just queue this core up for closing.
name|modifyLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
comment|// Wakes up closer thread too
block|}
block|}
block|}
end_class

end_unit

