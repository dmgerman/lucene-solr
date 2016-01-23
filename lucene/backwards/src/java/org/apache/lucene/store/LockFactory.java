begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *<p>Base class for Locking implementation.  {@link Directory} uses  * instances of this class to implement locking.</p>  *  *<p>Note that there are some useful tools to verify that  * your LockFactory is working correctly: {@link  * VerifyingLockFactory}, {@link LockStressTest}, {@link  * LockVerifyServer}.</p>  *  * @see LockVerifyServer  * @see LockStressTest  * @see VerifyingLockFactory  */
end_comment

begin_class
DECL|class|LockFactory
specifier|public
specifier|abstract
class|class
name|LockFactory
block|{
DECL|field|lockPrefix
specifier|protected
name|String
name|lockPrefix
init|=
literal|null
decl_stmt|;
comment|/**    * Set the prefix in use for all locks created in this    * LockFactory.  This is normally called once, when a    * Directory gets this LockFactory instance.  However, you    * can also call this (after this instance is assigned to    * a Directory) to override the prefix in use.  This    * is helpful if you're running Lucene on machines that    * have different mount points for the same shared    * directory.    */
DECL|method|setLockPrefix
specifier|public
name|void
name|setLockPrefix
parameter_list|(
name|String
name|lockPrefix
parameter_list|)
block|{
name|this
operator|.
name|lockPrefix
operator|=
name|lockPrefix
expr_stmt|;
block|}
comment|/**    * Get the prefix in use for all locks created in this LockFactory.    */
DECL|method|getLockPrefix
specifier|public
name|String
name|getLockPrefix
parameter_list|()
block|{
return|return
name|this
operator|.
name|lockPrefix
return|;
block|}
comment|/**    * Return a new Lock instance identified by lockName.    * @param lockName name of the lock to be created.    */
DECL|method|makeLock
specifier|public
specifier|abstract
name|Lock
name|makeLock
parameter_list|(
name|String
name|lockName
parameter_list|)
function_decl|;
comment|/**    * Attempt to clear (forcefully unlock and remove) the    * specified lock.  Only call this at a time when you are    * certain this lock is no longer in use.    * @param lockName name of the lock to be cleared.    */
DECL|method|clearLock
specifier|abstract
specifier|public
name|void
name|clearLock
parameter_list|(
name|String
name|lockName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

