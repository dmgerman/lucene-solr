begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
comment|/**This is an interface to stream data out using a push API  *  */
end_comment

begin_interface
DECL|interface|PushWriter
specifier|public
interface|interface
name|PushWriter
extends|extends
name|Closeable
block|{
comment|/**Write a Map. The map is opened in the beginning of the method    * and closed at the end. All map entries MUST be written before this    * method returns    */
DECL|method|writeMap
name|void
name|writeMap
parameter_list|(
name|MapWriter
name|mw
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**Write an array. The array is opened at the beginning of this method    * and closed at the end. All array entries must be returned before this    * method returns    *    */
DECL|method|writeIterator
name|void
name|writeIterator
parameter_list|(
name|IteratorWriter
name|iw
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

