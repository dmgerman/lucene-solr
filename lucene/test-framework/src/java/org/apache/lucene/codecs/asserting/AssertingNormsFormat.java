begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|DocValuesConsumer
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
name|DocValuesProducer
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
name|asserting
operator|.
name|AssertingDocValuesFormat
operator|.
name|AssertingNormsConsumer
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
name|asserting
operator|.
name|AssertingDocValuesFormat
operator|.
name|AssertingDocValuesProducer
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
name|lucene42
operator|.
name|Lucene42NormsFormat
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
comment|/**  * Just like {@link Lucene42NormsFormat} but with additional asserts.  */
end_comment

begin_class
DECL|class|AssertingNormsFormat
specifier|public
class|class
name|AssertingNormsFormat
extends|extends
name|NormsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|NormsFormat
name|in
init|=
operator|new
name|Lucene42NormsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|normsConsumer
specifier|public
name|DocValuesConsumer
name|normsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesConsumer
name|consumer
init|=
name|in
operator|.
name|normsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
assert|assert
name|consumer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingNormsConsumer
argument_list|(
name|consumer
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normsProducer
specifier|public
name|DocValuesProducer
name|normsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|.
name|fieldInfos
operator|.
name|hasNorms
argument_list|()
assert|;
name|DocValuesProducer
name|producer
init|=
name|in
operator|.
name|normsProducer
argument_list|(
name|state
argument_list|)
decl_stmt|;
assert|assert
name|producer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingDocValuesProducer
argument_list|(
name|producer
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

