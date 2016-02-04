begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Class used to create index-time {@link FuzzySet} appropriately configured for  * each field. Also called to right-size bitsets for serialization.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BloomFilterFactory
specifier|public
specifier|abstract
class|class
name|BloomFilterFactory
block|{
comment|/**    *     * @param state  The content to be indexed    * @param info    *          the field requiring a BloomFilter    * @return An appropriately sized set or null if no BloomFiltering required    */
DECL|method|getSetForField
specifier|public
specifier|abstract
name|FuzzySet
name|getSetForField
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|FieldInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * Called when downsizing bitsets for serialization    *     * @param fieldInfo    *          The field with sparse set bits    * @param initialSet    *          The bits accumulated    * @return null or a hopefully more densely packed, smaller bitset    */
DECL|method|downsize
specifier|public
name|FuzzySet
name|downsize
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|FuzzySet
name|initialSet
parameter_list|)
block|{
comment|// Aim for a bitset size that would have 10% of bits set (so 90% of searches
comment|// would fail-fast)
name|float
name|targetMaxSaturation
init|=
literal|0.1f
decl_stmt|;
return|return
name|initialSet
operator|.
name|downsize
argument_list|(
name|targetMaxSaturation
argument_list|)
return|;
block|}
comment|/**    * Used to determine if the given filter has reached saturation and should be retired i.e. not saved any more    * @param bloomFilter The bloomFilter being tested    * @param fieldInfo The field with which this filter is associated    * @return true if the set has reached saturation and should be retired    */
DECL|method|isSaturated
specifier|public
specifier|abstract
name|boolean
name|isSaturated
parameter_list|(
name|FuzzySet
name|bloomFilter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
function_decl|;
block|}
end_class

end_unit

