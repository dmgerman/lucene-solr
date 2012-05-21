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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|DocValues
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|PerDocWriteState
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
name|SegmentInfo
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

begin_comment
comment|/**  * Encodes/decodes {@link DocValues}  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesFormat
specifier|public
specifier|abstract
class|class
name|DocValuesFormat
block|{
comment|/** Consumes (writes) doc values during indexing. */
DECL|method|docsConsumer
specifier|public
specifier|abstract
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Produces (reads) doc values during reading/searching. */
DECL|method|docsProducer
specifier|public
specifier|abstract
name|PerDocProducer
name|docsProducer
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

