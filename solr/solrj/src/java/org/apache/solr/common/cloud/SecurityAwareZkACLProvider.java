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
name|data
operator|.
name|ACL
import|;
end_import

begin_comment
comment|/**  * {@link ZkACLProvider} capable of returning a different set of  * {@link ACL}s for security-related znodes (default: subtree under /security)  * vs non-security-related znodes.  */
end_comment

begin_class
DECL|class|SecurityAwareZkACLProvider
specifier|public
specifier|abstract
class|class
name|SecurityAwareZkACLProvider
implements|implements
name|ZkACLProvider
block|{
DECL|field|SECURITY_ZNODE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|SECURITY_ZNODE_PATH
init|=
literal|"/security"
decl_stmt|;
DECL|field|nonSecurityACLsToAdd
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|nonSecurityACLsToAdd
decl_stmt|;
DECL|field|securityACLsToAdd
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|securityACLsToAdd
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
if|if
condition|(
name|isSecurityZNodePath
argument_list|(
name|zNodePath
argument_list|)
condition|)
block|{
return|return
name|getSecurityACLsToAdd
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getNonSecurityACLsToAdd
argument_list|()
return|;
block|}
block|}
DECL|method|isSecurityZNodePath
specifier|protected
name|boolean
name|isSecurityZNodePath
parameter_list|(
name|String
name|zNodePath
parameter_list|)
block|{
if|if
condition|(
name|zNodePath
operator|!=
literal|null
operator|&&
operator|(
name|zNodePath
operator|.
name|equals
argument_list|(
name|SECURITY_ZNODE_PATH
argument_list|)
operator|||
name|zNodePath
operator|.
name|startsWith
argument_list|(
name|SECURITY_ZNODE_PATH
operator|+
literal|"/"
argument_list|)
operator|)
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
comment|/**    * @return Set of ACLs to return for non-security related znodes    */
DECL|method|createNonSecurityACLsToAdd
specifier|protected
specifier|abstract
name|List
argument_list|<
name|ACL
argument_list|>
name|createNonSecurityACLsToAdd
parameter_list|()
function_decl|;
comment|/**    * @return Set of ACLs to return security-related znodes    */
DECL|method|createSecurityACLsToAdd
specifier|protected
specifier|abstract
name|List
argument_list|<
name|ACL
argument_list|>
name|createSecurityACLsToAdd
parameter_list|()
function_decl|;
DECL|method|getNonSecurityACLsToAdd
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|getNonSecurityACLsToAdd
parameter_list|()
block|{
if|if
condition|(
name|nonSecurityACLsToAdd
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
name|nonSecurityACLsToAdd
operator|==
literal|null
condition|)
name|nonSecurityACLsToAdd
operator|=
name|createNonSecurityACLsToAdd
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|nonSecurityACLsToAdd
return|;
block|}
DECL|method|getSecurityACLsToAdd
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|getSecurityACLsToAdd
parameter_list|()
block|{
if|if
condition|(
name|securityACLsToAdd
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
name|securityACLsToAdd
operator|==
literal|null
condition|)
name|securityACLsToAdd
operator|=
name|createSecurityACLsToAdd
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|securityACLsToAdd
return|;
block|}
block|}
end_class

end_unit

