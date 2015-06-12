begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|codecs
operator|.
name|NormsConsumer
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
comment|/**  * Read-write version of 5.0 norms format for testing  * @deprecated for test purposes only  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene50RWNormsFormat
specifier|final
class|class
name|Lucene50RWNormsFormat
extends|extends
name|Lucene50NormsFormat
block|{
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|NormsConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene50NormsConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|METADATA_CODEC
argument_list|,
name|METADATA_EXTENSION
argument_list|)
return|;
block|}
block|}
end_class

end_unit

