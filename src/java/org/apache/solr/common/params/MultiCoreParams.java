begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** /**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * @since solr 1.3  */
end_comment

begin_interface
DECL|interface|MultiCoreParams
specifier|public
interface|interface
name|MultiCoreParams
block|{
comment|/** What Core are we talking about **/
DECL|field|CORE
specifier|public
specifier|final
specifier|static
name|String
name|CORE
init|=
literal|"core"
decl_stmt|;
comment|/** Persistent -- should it save the multicore state? **/
DECL|field|PERSISTENT
specifier|public
specifier|final
specifier|static
name|String
name|PERSISTENT
init|=
literal|"persistent"
decl_stmt|;
comment|/** What action **/
DECL|field|ACTION
specifier|public
specifier|final
specifier|static
name|String
name|ACTION
init|=
literal|"action"
decl_stmt|;
DECL|enum|MultiCoreAction
specifier|public
enum|enum
name|MultiCoreAction
block|{
DECL|enum constant|STATUS
name|STATUS
block|,
DECL|enum constant|STOP
name|STOP
block|,
DECL|enum constant|LOAD
name|LOAD
block|,
DECL|enum constant|RELOAD
name|RELOAD
block|,
DECL|enum constant|SETASDEFAULT
name|SETASDEFAULT
block|;
DECL|method|get
specifier|public
specifier|static
name|MultiCoreAction
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|MultiCoreAction
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_interface

end_unit

