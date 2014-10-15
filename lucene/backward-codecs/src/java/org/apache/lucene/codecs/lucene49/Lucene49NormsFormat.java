begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|codecs
operator|.
name|NormsFormat
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
name|NormsProducer
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
name|UndeadNormsProducer
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
comment|/**  * Lucene 4.9 Score normalization format.  * @deprecated only for reading 4.9/4.10 indexes  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene49NormsFormat
specifier|public
class|class
name|Lucene49NormsFormat
extends|extends
name|NormsFormat
block|{
comment|/** Sole Constructor */
DECL|method|Lucene49NormsFormat
specifier|public
name|Lucene49NormsFormat
parameter_list|()
block|{}
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
specifier|final
name|NormsProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|UndeadNormsProducer
operator|.
name|isUndeadArmy
argument_list|(
name|state
operator|.
name|fieldInfos
argument_list|)
condition|)
block|{
return|return
name|UndeadNormsProducer
operator|.
name|INSTANCE
return|;
block|}
else|else
block|{
return|return
operator|new
name|Lucene49NormsProducer
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
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene49NormsData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"nvd"
decl_stmt|;
DECL|field|METADATA_CODEC
specifier|static
specifier|final
name|String
name|METADATA_CODEC
init|=
literal|"Lucene49NormsMetadata"
decl_stmt|;
DECL|field|METADATA_EXTENSION
specifier|static
specifier|final
name|String
name|METADATA_EXTENSION
init|=
literal|"nvm"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
block|}
end_class

end_unit

