begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.logging
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
package|;
end_package

begin_comment
comment|/**  * Defines the configuration of a {@link LogWatcher}  */
end_comment

begin_class
DECL|class|LogWatcherConfig
specifier|public
class|class
name|LogWatcherConfig
block|{
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|field|loggingClass
specifier|private
specifier|final
name|String
name|loggingClass
decl_stmt|;
DECL|field|watcherSize
specifier|private
specifier|final
name|int
name|watcherSize
decl_stmt|;
DECL|field|watcherThreshold
specifier|private
specifier|final
name|String
name|watcherThreshold
decl_stmt|;
DECL|method|LogWatcherConfig
specifier|public
name|LogWatcherConfig
parameter_list|(
name|boolean
name|enabled
parameter_list|,
name|String
name|loggingClass
parameter_list|,
name|String
name|watcherThreshold
parameter_list|,
name|int
name|watcherSize
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|this
operator|.
name|loggingClass
operator|=
name|loggingClass
expr_stmt|;
name|this
operator|.
name|watcherThreshold
operator|=
name|watcherThreshold
expr_stmt|;
name|this
operator|.
name|watcherSize
operator|=
name|watcherSize
expr_stmt|;
block|}
comment|/**    * @return true if the LogWatcher is enabled    */
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
comment|/**    * Get the implementation of the LogWatcher to use.  May be "JUL" or "log4j" for the default    * java.util.logging or log4j implementations, or the fully-qualified name of a class extending    * {@link LogWatcher}.    * @return the LogWatcher class to use    */
DECL|method|getLoggingClass
specifier|public
name|String
name|getLoggingClass
parameter_list|()
block|{
return|return
name|loggingClass
return|;
block|}
comment|/**    * @return the size of the LogWatcher queue    */
DECL|method|getWatcherSize
specifier|public
name|int
name|getWatcherSize
parameter_list|()
block|{
return|return
name|watcherSize
return|;
block|}
comment|/**    * @return the threshold above which logging events will be recorded    */
DECL|method|getWatcherThreshold
specifier|public
name|String
name|getWatcherThreshold
parameter_list|()
block|{
return|return
name|watcherThreshold
return|;
block|}
comment|/**    * @return a {@link ListenerConfig} object using this config's settings.    */
DECL|method|asListenerConfig
specifier|public
name|ListenerConfig
name|asListenerConfig
parameter_list|()
block|{
return|return
operator|new
name|ListenerConfig
argument_list|(
name|watcherSize
argument_list|,
name|watcherThreshold
argument_list|)
return|;
block|}
block|}
end_class

end_unit

