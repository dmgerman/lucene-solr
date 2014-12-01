begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud.overseer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|overseer
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
name|cloud
operator|.
name|DocCollection
import|;
end_import

begin_class
DECL|class|ZkWriteCommand
specifier|public
class|class
name|ZkWriteCommand
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|collection
specifier|public
specifier|final
name|DocCollection
name|collection
decl_stmt|;
DECL|field|noop
specifier|public
specifier|final
name|boolean
name|noop
decl_stmt|;
DECL|method|ZkWriteCommand
specifier|public
name|ZkWriteCommand
parameter_list|(
name|String
name|name
parameter_list|,
name|DocCollection
name|collection
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
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|noop
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Returns a no-op    */
DECL|method|ZkWriteCommand
specifier|protected
name|ZkWriteCommand
parameter_list|()
block|{
name|this
operator|.
name|noop
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|name
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|collection
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|noop
specifier|public
specifier|static
name|ZkWriteCommand
name|noop
parameter_list|()
block|{
return|return
operator|new
name|ZkWriteCommand
argument_list|()
return|;
block|}
block|}
end_class

end_unit

