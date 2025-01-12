begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import

begin_class
DECL|class|DefaultZkACLProvider
specifier|public
class|class
name|DefaultZkACLProvider
implements|implements
name|ZkACLProvider
block|{
DECL|field|globalACLsToAdd
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|globalACLsToAdd
decl_stmt|;
annotation|@
name|Override
DECL|method|getACLsToAdd
specifier|public
name|List
argument_list|<
name|ACL
argument_list|>
name|getACLsToAdd
parameter_list|(
name|String
name|zNodePath
parameter_list|)
block|{
comment|// In default (simple) implementation use the same set of ACLs for all znodes
if|if
condition|(
name|globalACLsToAdd
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|globalACLsToAdd
operator|==
literal|null
condition|)
name|globalACLsToAdd
operator|=
name|createGlobalACLsToAdd
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|globalACLsToAdd
return|;
block|}
DECL|method|createGlobalACLsToAdd
specifier|protected
name|List
argument_list|<
name|ACL
argument_list|>
name|createGlobalACLsToAdd
parameter_list|()
block|{
return|return
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
return|;
block|}
block|}
end_class

end_unit

