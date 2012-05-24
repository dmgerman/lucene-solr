begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SegmentInfoFormat
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
name|SegmentInfoReader
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
name|SegmentInfoWriter
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

begin_comment
comment|/**  * Lucene3x ReadOnly SegmentInfoFormat implementation  * @deprecated (4.0) This is only used to read indexes created  * before 4.0.  * @lucene.experimental  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene3xSegmentInfoFormat
specifier|public
class|class
name|Lucene3xSegmentInfoFormat
extends|extends
name|SegmentInfoFormat
block|{
DECL|field|reader
specifier|private
specifier|final
name|SegmentInfoReader
name|reader
init|=
operator|new
name|Lucene3xSegmentInfoReader
argument_list|()
decl_stmt|;
comment|/** This format adds optional per-segment String    *  diagnostics storage, and switches userData to Map */
DECL|field|FORMAT_DIAGNOSTICS
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_DIAGNOSTICS
init|=
operator|-
literal|9
decl_stmt|;
comment|/** Each segment records whether it has term vectors */
DECL|field|FORMAT_HAS_VECTORS
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_HAS_VECTORS
init|=
operator|-
literal|10
decl_stmt|;
comment|/** Each segment records the Lucene version that created it. */
DECL|field|FORMAT_3_1
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_3_1
init|=
operator|-
literal|11
decl_stmt|;
comment|/** Each segment records whether its postings are written    *  in the new flex format */
DECL|field|FORMAT_4X_UPGRADE
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_4X_UPGRADE
init|=
operator|-
literal|12
decl_stmt|;
comment|/** Extension used for saving each SegmentInfo, once a 3.x    *  index is first committed to with 4.0. */
DECL|field|SI_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|SI_EXTENSION
init|=
literal|"si"
decl_stmt|;
annotation|@
name|Override
DECL|method|getSegmentInfosReader
specifier|public
name|SegmentInfoReader
name|getSegmentInfosReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfoWriter
name|getSegmentInfosWriter
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
comment|// only for backwards compat
DECL|field|DS_OFFSET_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DS_OFFSET_KEY
init|=
name|Lucene3xSegmentInfoFormat
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".dsoffset"
decl_stmt|;
DECL|field|DS_NAME_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DS_NAME_KEY
init|=
name|Lucene3xSegmentInfoFormat
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".dsname"
decl_stmt|;
DECL|field|DS_COMPOUND_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DS_COMPOUND_KEY
init|=
name|Lucene3xSegmentInfoFormat
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".dscompound"
decl_stmt|;
comment|/**     * @return if this segment shares stored fields& vectors, this    *         offset is where in that file this segment's docs begin     */
DECL|method|getDocStoreOffset
specifier|public
specifier|static
name|int
name|getDocStoreOffset
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
block|{
name|String
name|v
init|=
name|si
operator|.
name|getAttribute
argument_list|(
name|DS_OFFSET_KEY
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|v
argument_list|)
return|;
block|}
comment|/** @return name used to derive fields/vectors file we share with other segments */
DECL|method|getDocStoreSegment
specifier|public
specifier|static
name|String
name|getDocStoreSegment
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
block|{
name|String
name|v
init|=
name|si
operator|.
name|getAttribute
argument_list|(
name|DS_NAME_KEY
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
name|si
operator|.
name|name
else|:
name|v
return|;
block|}
comment|/** @return whether doc store files are stored in compound file (*.cfx) */
DECL|method|getDocStoreIsCompoundFile
specifier|public
specifier|static
name|boolean
name|getDocStoreIsCompoundFile
parameter_list|(
name|SegmentInfo
name|si
parameter_list|)
block|{
name|String
name|v
init|=
name|si
operator|.
name|getAttribute
argument_list|(
name|DS_COMPOUND_KEY
argument_list|)
decl_stmt|;
return|return
name|v
operator|==
literal|null
condition|?
literal|false
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|v
argument_list|)
return|;
block|}
block|}
end_class

end_unit

