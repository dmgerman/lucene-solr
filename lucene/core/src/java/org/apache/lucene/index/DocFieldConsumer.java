begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|DocFieldConsumer
specifier|abstract
class|class
name|DocFieldConsumer
block|{
comment|/** Called when DocumentsWriterPerThread decides to create a new    *  segment */
DECL|method|flush
specifier|abstract
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|DocFieldConsumerPerField
argument_list|>
name|fieldsToFlush
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when an aborting exception is hit */
DECL|method|abort
specifier|abstract
name|void
name|abort
parameter_list|()
function_decl|;
comment|/** Called when DocumentsWriterPerThread is using too much RAM.    *  The consumer should free RAM, if possible, returning    *  true if any RAM was in fact freed. */
DECL|method|freeRAM
specifier|abstract
name|boolean
name|freeRAM
parameter_list|()
function_decl|;
DECL|method|startDocument
specifier|abstract
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|addField
specifier|abstract
name|DocFieldConsumerPerField
name|addField
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
function_decl|;
DECL|method|finishDocument
specifier|abstract
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

