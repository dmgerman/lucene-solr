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
name|File
import|;
end_import

begin_comment
comment|/**  * Base class for file system based locking implementation.  */
end_comment

begin_class
DECL|class|FSLockFactory
specifier|public
specifier|abstract
class|class
name|FSLockFactory
extends|extends
name|LockFactory
block|{
comment|/**    * Directory for the lock files.    */
DECL|field|lockDir
specifier|protected
name|File
name|lockDir
init|=
literal|null
decl_stmt|;
comment|/**    * Set the lock directory. This method can be only called    * once to initialize the lock directory. It is used by {@link FSDirectory}    * to set the lock directory to itsself.    * Subclasses can also use this method to set the directory    * in the constructor.    */
DECL|method|setLockDir
specifier|protected
name|void
name|setLockDir
parameter_list|(
name|File
name|lockDir
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|lockDir
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"You can set the lock directory for this factory only once."
argument_list|)
throw|;
name|this
operator|.
name|lockDir
operator|=
name|lockDir
expr_stmt|;
block|}
comment|/**    * Retrieve the lock directory.    */
DECL|method|getLockDir
specifier|public
name|File
name|getLockDir
parameter_list|()
block|{
return|return
name|lockDir
return|;
block|}
block|}
end_class

end_unit

