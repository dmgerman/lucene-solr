begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_comment
comment|/**  * Interface for DocumentBuilders  *  * @param<T> IndexDocument implementation  *  */
end_comment

begin_interface
DECL|interface|IndexDocumentBuilder
specifier|public
interface|interface
name|IndexDocumentBuilder
parameter_list|<
name|T
extends|extends
name|IndexDocument
parameter_list|>
extends|extends
name|Callable
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * @see java.util.concurrent.Callable#call()      */
DECL|method|call
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|GdataIndexerException
function_decl|;
block|}
end_interface

end_unit

