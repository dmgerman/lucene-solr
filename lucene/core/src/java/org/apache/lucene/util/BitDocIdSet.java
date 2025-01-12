begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|search
operator|.
name|DocIdSet
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
name|search
operator|.
name|DocIdSetIterator
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@link DocIdSet} interface on top of a {@link BitSet}.  * @lucene.internal  */
end_comment

begin_class
DECL|class|BitDocIdSet
specifier|public
class|class
name|BitDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|BitDocIdSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|set
specifier|private
specifier|final
name|BitSet
name|set
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
comment|/**    * Wrap the given {@link BitSet} as a {@link DocIdSet}. The provided    * {@link BitSet} must not be modified afterwards.    */
DECL|method|BitDocIdSet
specifier|public
name|BitDocIdSet
parameter_list|(
name|BitSet
name|set
parameter_list|,
name|long
name|cost
parameter_list|)
block|{
if|if
condition|(
name|cost
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cost must be>= 0, got "
operator|+
name|cost
argument_list|)
throw|;
block|}
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
comment|/**    * Same as {@link #BitDocIdSet(BitSet, long)} but uses the set's    * {@link BitSet#approximateCardinality() approximate cardinality} as a cost.    */
DECL|method|BitDocIdSet
specifier|public
name|BitDocIdSet
parameter_list|(
name|BitSet
name|set
parameter_list|)
block|{
name|this
argument_list|(
name|set
argument_list|,
name|set
operator|.
name|approximateCardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|BitSetIterator
argument_list|(
name|set
argument_list|,
name|cost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|()
block|{
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|BASE_RAM_BYTES_USED
operator|+
name|set
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(set="
operator|+
name|set
operator|+
literal|",cost="
operator|+
name|cost
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

