begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SegmentReadState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SegmentWriteState
import|;
end_import

begin_comment
comment|/**  * Encodes/decodes per-document score normalization values.  */
end_comment

begin_class
DECL|class|NormsFormat
specifier|public
specifier|abstract
class|class
name|NormsFormat
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|NormsFormat
specifier|protected
name|NormsFormat
parameter_list|()
block|{   }
comment|/** Returns a {@link NormsConsumer} to write norms to the    *  index. */
DECL|method|normsConsumer
specifier|public
specifier|abstract
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Returns a {@link NormsProducer} to read norms from the index.     *<p>    * NOTE: by the time this call returns, it must hold open any files it will     * need to use; else, those files may be deleted. Additionally, required files     * may be deleted during the execution of this call before there is a chance     * to open them. Under these circumstances an IOException should be thrown by     * the implementation. IOExceptions are expected and will automatically cause     * a retry of the segment opening logic with the newly revised segments.    */
DECL|method|normsProducer
specifier|public
specifier|abstract
name|NormsProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

