begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.bkdtree3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|bkdtree3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/** Abstracts away whether OfflineSorter or simple arrays in heap are used. */
end_comment

begin_interface
DECL|interface|Writer
interface|interface
name|Writer
extends|extends
name|Closeable
block|{
DECL|method|append
name|void
name|append
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|,
name|int
name|z
parameter_list|,
name|long
name|ord
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getReader
name|Reader
name|getReader
parameter_list|(
name|long
name|start
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|destroy
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

