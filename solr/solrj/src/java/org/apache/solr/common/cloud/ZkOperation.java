begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|IOException
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
name|KeeperException
import|;
end_import

begin_comment
comment|/**  * A callback object which can be used for implementing retry-able operations.  *  */
end_comment

begin_class
DECL|class|ZkOperation
specifier|public
specifier|abstract
class|class
name|ZkOperation
block|{
comment|/**      * Performs the operation - which may be involved multiple times if the connection      * to ZooKeeper closes during this operation      *      * @return the result of the operation or null      */
DECL|method|execute
specifier|public
specifier|abstract
name|Object
name|execute
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
function_decl|;
block|}
end_class

end_unit

