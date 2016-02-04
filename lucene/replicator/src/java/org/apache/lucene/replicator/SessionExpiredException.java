begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
package|;
end_package

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
comment|/**  * Exception indicating that a revision update session was expired due to lack  * of activity.  *   * @see LocalReplicator#DEFAULT_SESSION_EXPIRATION_THRESHOLD  * @see LocalReplicator#setExpirationThreshold(long)  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SessionExpiredException
specifier|public
class|class
name|SessionExpiredException
extends|extends
name|IOException
block|{
comment|/**    * @see IOException#IOException(String, Throwable)    */
DECL|method|SessionExpiredException
specifier|public
name|SessionExpiredException
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
block|}
comment|/**    * @see IOException#IOException(String)    */
DECL|method|SessionExpiredException
specifier|public
name|SessionExpiredException
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
block|}
comment|/**    * @see IOException#IOException(Throwable)    */
DECL|method|SessionExpiredException
specifier|public
name|SessionExpiredException
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
block|}
block|}
end_class

end_unit

