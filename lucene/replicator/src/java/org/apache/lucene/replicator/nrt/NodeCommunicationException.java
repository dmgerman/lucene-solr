begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Should be thrown by subclasses of {@link PrimaryNode} and {@link ReplicaNode} if a non-fatal exception  * occurred while communicating between nodes.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NodeCommunicationException
specifier|public
class|class
name|NodeCommunicationException
extends|extends
name|RuntimeException
block|{
DECL|method|NodeCommunicationException
specifier|public
name|NodeCommunicationException
parameter_list|(
name|String
name|when
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|when
argument_list|)
expr_stmt|;
assert|assert
name|cause
operator|!=
literal|null
assert|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

