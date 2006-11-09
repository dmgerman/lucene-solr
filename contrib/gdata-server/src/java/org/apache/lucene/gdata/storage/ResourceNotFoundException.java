begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
package|;
end_package

begin_comment
comment|/**  * This exception will be thrown if an requested resource of a resource to modify can not be found  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|ResourceNotFoundException
specifier|public
class|class
name|ResourceNotFoundException
extends|extends
name|StorageException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8549987918130998249L
decl_stmt|;
comment|/**      * Constructs an empty ResourceNotFoundException      */
DECL|method|ResourceNotFoundException
specifier|public
name|ResourceNotFoundException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * Constructs a new ResourceNotFoundException with an exception message      * @param message - the exception message      */
DECL|method|ResourceNotFoundException
specifier|public
name|ResourceNotFoundException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * Constructs a new ResourceNotFoundException with an exception message and a root cause       * @param message - the exception message      * @param cause - the root cause of this exception      */
DECL|method|ResourceNotFoundException
specifier|public
name|ResourceNotFoundException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * Constructs a new ResourceNotFoundException with  a root cause      * @param cause - the root cause of this exception      *       */
DECL|method|ResourceNotFoundException
specifier|public
name|ResourceNotFoundException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
block|}
end_class

end_unit

