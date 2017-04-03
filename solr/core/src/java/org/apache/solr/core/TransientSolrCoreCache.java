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
name|List
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
name|Set
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

begin_comment
comment|/**  * The base class for custom transient core maintenance. Any custom plugin that want's to take control of transient  * caches (i.e. any core defined with transient=true) should override this class.  *  * Register your plugin in solr.xml similarly to:  *  *&lt;transientCoreCacheFactory name="transientCoreCacheFactory" class="TransientSolrCoreCacheFactoryDefault"&gt;  *&lt;int name="transientCacheSize"&gt;4&lt;/int&gt;  *&lt;/transientCoreCacheFactory&gt;  *  *  * WARNING: There is quite a bit of higher-level locking done by the CoreContainer to avoid various race conditions  *          etc. You should _only_ manipulate them within the method calls designed to change them. E.g.  *          only add to the transient core descriptors in addTransientDescriptor etc.  *            *          Trust the higher-level code (mainly SolrCores and CoreContainer) to call the appropriate operations when  *          necessary and to coordinate shutting down cores, manipulating the internal structures and the like..  *            *          The only real action you should _initiate_ is to close a core for whatever reason, and do that by   *          calling notifyObservers(coreToClose); The observer will call back to removeCore(name) at the appropriate   *          time. There is no need to directly remove the core _at that time_ from the transientCores list, a call  *          will come back to this class when CoreContainer is closing this core.  *            *          CoreDescriptors are read-once. During "core discovery" all valid descriptors are enumerated and added to  *          the appropriate list. Thereafter, they are NOT re-read from disk. In those situations where you want  *          to re-define the coreDescriptor, maintain a "side list" of changed core descriptors. Then override  *          getTransientDescriptor to return your new core descriptor. NOTE: assuming you've already closed the  *          core, the _next_ time that core is required getTransientDescriptor will be called and if you return the  *          new core descriptor your re-definition should be honored. You'll have to maintain this list for the  *          duration of this Solr instance running. If you persist the coreDescriptor, then next time Solr starts  *          up the new definition will be read.  *            *  *  If you need to manipulate the return, for instance block a core from being loaded for some period of time, override  *  say getTransientDescriptor and return null.  *    *  In particular, DO NOT reach into the transientCores structure from a method called to manipulate core descriptors  *  or vice-versa.  */
end_comment

begin_class
DECL|class|TransientSolrCoreCache
specifier|public
specifier|abstract
class|class
name|TransientSolrCoreCache
extends|extends
name|Observable
block|{
comment|// Gets the core container that encloses this cache.
DECL|method|getContainer
specifier|public
specifier|abstract
name|CoreContainer
name|getContainer
parameter_list|()
function_decl|;
comment|// Add the newly-opened core to the list of open cores.
DECL|method|addCore
specifier|public
specifier|abstract
name|SolrCore
name|addCore
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrCore
name|core
parameter_list|)
function_decl|;
comment|// Return the names of all possible cores, whether they are currently loaded or not.
DECL|method|getAllCoreNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getAllCoreNames
parameter_list|()
function_decl|;
comment|// Return the names of all currently loaded cores
DECL|method|getLoadedCoreNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getLoadedCoreNames
parameter_list|()
function_decl|;
comment|// Remove a core from the internal structures, presumably it
comment|// being closed. If the core is re-opened, it will be readded by CoreContainer.
DECL|method|removeCore
specifier|public
specifier|abstract
name|SolrCore
name|removeCore
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|// Get the core associated with the name. Return null if you don't want this core to be used.
DECL|method|getCore
specifier|public
specifier|abstract
name|SolrCore
name|getCore
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|// reutrn true if the cache contains the named core.
DECL|method|containsCore
specifier|public
specifier|abstract
name|boolean
name|containsCore
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|// This method will be called when the container is to be shut down. It should return all
comment|// transient solr cores and clear any internal structures that hold them.
DECL|method|prepareForShutdown
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|SolrCore
argument_list|>
name|prepareForShutdown
parameter_list|()
function_decl|;
comment|// These methods allow the implementation to maintain control over the core descriptors.
comment|// This method will only be called during core discovery at startup.
DECL|method|addTransientDescriptor
specifier|public
specifier|abstract
name|void
name|addTransientDescriptor
parameter_list|(
name|String
name|rawName
parameter_list|,
name|CoreDescriptor
name|cd
parameter_list|)
function_decl|;
comment|// This method is used when opening cores and the like. If you want to change a core's descriptor, override this
comment|// method and return the current core descriptor.
DECL|method|getTransientDescriptor
specifier|public
specifier|abstract
name|CoreDescriptor
name|getTransientDescriptor
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|// Remove the core descriptor from your list of transient descriptors.
DECL|method|removeTransientDescriptor
specifier|public
specifier|abstract
name|CoreDescriptor
name|removeTransientDescriptor
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|// Find all the names a specific core is mapped to. Should not return null, return empty set instead.
annotation|@
name|Experimental
DECL|method|getNamesForCore
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getNamesForCore
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**    * Must be called in order to free resources!    */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|// These two methods allow custom implementations to communicate arbitrary information as necessary.
DECL|method|getStatus
specifier|public
specifier|abstract
name|int
name|getStatus
parameter_list|(
name|String
name|coreName
parameter_list|)
function_decl|;
DECL|method|setStatus
specifier|public
specifier|abstract
name|void
name|setStatus
parameter_list|(
name|String
name|coreName
parameter_list|,
name|int
name|status
parameter_list|)
function_decl|;
block|}
end_class

end_unit

