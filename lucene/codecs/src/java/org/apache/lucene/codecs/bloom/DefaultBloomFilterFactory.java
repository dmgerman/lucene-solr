begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|bloom
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FieldInfo
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
comment|/**  * Default policy is to allocate a bitset with 10% saturation given a unique term per document.  * Bits are set via MurmurHash2 hashing function.  *  @lucene.experimental  */
end_comment

begin_class
DECL|class|DefaultBloomFilterFactory
specifier|public
class|class
name|DefaultBloomFilterFactory
extends|extends
name|BloomFilterFactory
block|{
annotation|@
name|Override
DECL|method|getSetForField
specifier|public
name|FuzzySet
name|getSetForField
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|FieldInfo
name|info
parameter_list|)
block|{
comment|//Assume all of the docs have a unique term (e.g. a primary key) and we hope to maintain a set with 10% of bits set
return|return
name|FuzzySet
operator|.
name|createSetBasedOnQuality
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|0.10f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isSaturated
specifier|public
name|boolean
name|isSaturated
parameter_list|(
name|FuzzySet
name|bloomFilter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|// Don't bother saving bitsets if>90% of bits are set - we don't want to
comment|// throw any more memory at this problem.
return|return
name|bloomFilter
operator|.
name|getSaturation
argument_list|()
operator|>
literal|0.9f
return|;
block|}
block|}
end_class

end_unit

