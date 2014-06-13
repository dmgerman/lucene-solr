begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/** A DocIdSet in Elias-Fano encoding.  * @lucene.internal  */
end_comment

begin_class
DECL|class|EliasFanoDocIdSet
specifier|public
class|class
name|EliasFanoDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|efEncoder
specifier|final
name|EliasFanoEncoder
name|efEncoder
decl_stmt|;
comment|/**    * Construct an EliasFanoDocIdSet. For efficient encoding, the parameters should be chosen as low as possible.    * @param numValues At least the number of document ids that will be encoded.    * @param upperBound  At least the highest document id that will be encoded.    */
DECL|method|EliasFanoDocIdSet
specifier|public
name|EliasFanoDocIdSet
parameter_list|(
name|int
name|numValues
parameter_list|,
name|int
name|upperBound
parameter_list|)
block|{
name|efEncoder
operator|=
operator|new
name|EliasFanoEncoder
argument_list|(
name|numValues
argument_list|,
name|upperBound
argument_list|)
expr_stmt|;
block|}
comment|/** Provide an indication that is better to use an {@link EliasFanoDocIdSet} than a {@link FixedBitSet}    *  to encode document identifiers.    *  @param numValues The number of document identifiers that is to be encoded. Should be non negative.    *  @param upperBound The maximum possible value for a document identifier. Should be at least<code>numValues</code>.    *  @return See {@link EliasFanoEncoder#sufficientlySmallerThanBitSet(long, long)}    */
DECL|method|sufficientlySmallerThanBitSet
specifier|public
specifier|static
name|boolean
name|sufficientlySmallerThanBitSet
parameter_list|(
name|long
name|numValues
parameter_list|,
name|long
name|upperBound
parameter_list|)
block|{
return|return
name|EliasFanoEncoder
operator|.
name|sufficientlySmallerThanBitSet
argument_list|(
name|numValues
argument_list|,
name|upperBound
argument_list|)
return|;
block|}
comment|/** Encode the document ids from a DocIdSetIterator.    *  @param disi This DocIdSetIterator should provide document ids that are consistent    *              with<code>numValues</code> and<code>upperBound</code> as provided to the constructor.      */
DECL|method|encodeFromDisi
specifier|public
name|void
name|encodeFromDisi
parameter_list|(
name|DocIdSetIterator
name|disi
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|efEncoder
operator|.
name|numEncoded
operator|<
name|efEncoder
operator|.
name|numValues
condition|)
block|{
name|int
name|x
init|=
name|disi
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"disi: "
operator|+
name|disi
operator|.
name|toString
argument_list|()
operator|+
literal|"\nhas "
operator|+
name|efEncoder
operator|.
name|numEncoded
operator|+
literal|" docs, but at least "
operator|+
name|efEncoder
operator|.
name|numValues
operator|+
literal|" are required."
argument_list|)
throw|;
block|}
name|efEncoder
operator|.
name|encodeNext
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Provides a {@link DocIdSetIterator} to access encoded document ids.    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|efEncoder
operator|.
name|lastEncoded
operator|>=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Highest encoded value too high for DocIdSetIterator.NO_MORE_DOCS: "
operator|+
name|efEncoder
operator|.
name|lastEncoded
argument_list|)
throw|;
block|}
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
specifier|private
name|int
name|curDocId
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|final
name|EliasFanoDecoder
name|efDecoder
init|=
name|efEncoder
operator|.
name|getDecoder
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|curDocId
return|;
block|}
specifier|private
name|int
name|setCurDocID
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|curDocId
operator|=
operator|(
name|value
operator|==
name|EliasFanoDecoder
operator|.
name|NO_MORE_VALUES
operator|)
condition|?
name|NO_MORE_DOCS
else|:
operator|(
name|int
operator|)
name|value
expr_stmt|;
return|return
name|curDocId
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|setCurDocID
argument_list|(
name|efDecoder
operator|.
name|nextValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|setCurDocID
argument_list|(
name|efDecoder
operator|.
name|advanceToValue
argument_list|(
name|target
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|efDecoder
operator|.
name|numEncoded
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** This DocIdSet implementation is cacheable.    * @return<code>true</code>    */
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
operator|(
operator|(
name|other
operator|instanceof
name|EliasFanoDocIdSet
operator|)
operator|)
operator|&&
name|efEncoder
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|EliasFanoDocIdSet
operator|)
name|other
operator|)
operator|.
name|efEncoder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|efEncoder
operator|.
name|hashCode
argument_list|()
operator|^
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
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
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
operator|+
name|efEncoder
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
block|}
end_class

end_unit

