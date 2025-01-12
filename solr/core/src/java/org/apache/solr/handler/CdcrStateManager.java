begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|List
import|;
end_import

begin_comment
comment|/**  * A state manager which implements an observer pattern to notify observers  * of a state change.  */
end_comment

begin_class
DECL|class|CdcrStateManager
specifier|abstract
class|class
name|CdcrStateManager
block|{
DECL|field|observers
specifier|private
name|List
argument_list|<
name|CdcrStateObserver
argument_list|>
name|observers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|register
name|void
name|register
parameter_list|(
name|CdcrStateObserver
name|observer
parameter_list|)
block|{
name|this
operator|.
name|observers
operator|.
name|add
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
DECL|method|callback
name|void
name|callback
parameter_list|()
block|{
for|for
control|(
name|CdcrStateObserver
name|observer
range|:
name|observers
control|)
block|{
name|observer
operator|.
name|stateUpdate
argument_list|()
expr_stmt|;
block|}
block|}
DECL|interface|CdcrStateObserver
interface|interface
name|CdcrStateObserver
block|{
DECL|method|stateUpdate
name|void
name|stateUpdate
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

