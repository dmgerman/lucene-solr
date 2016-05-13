begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene54
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene54
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|Codec
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
name|CompoundFormat
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
name|DocValuesFormat
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
name|FieldInfosFormat
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
name|FilterCodec
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
name|LiveDocsFormat
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
name|PointsFormat
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
name|PostingsFormat
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
name|StoredFieldsFormat
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
name|TermVectorsFormat
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
name|lucene50
operator|.
name|Lucene50CompoundFormat
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
name|lucene50
operator|.
name|Lucene50FieldInfosFormat
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
name|lucene50
operator|.
name|Lucene50LiveDocsFormat
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
name|lucene50
operator|.
name|Lucene50SegmentInfoFormat
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
name|lucene50
operator|.
name|Lucene50StoredFieldsFormat
operator|.
name|Mode
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
name|lucene50
operator|.
name|Lucene50StoredFieldsFormat
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
name|lucene50
operator|.
name|Lucene50TermVectorsFormat
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
name|lucene53
operator|.
name|Lucene53NormsFormat
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
name|perfield
operator|.
name|PerFieldDocValuesFormat
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
name|perfield
operator|.
name|PerFieldPostingsFormat
import|;
end_import

begin_comment
comment|/**  * Implements the Lucene 5.4 index format, with configurable per-field postings  * and docvalues formats.  *<p>  * If you want to reuse functionality of this codec in another codec, extend  * {@link FilterCodec}.  *  * @see org.apache.lucene.codecs.lucene54 package documentation for file format details.  * @lucene.experimental  * @deprecated Only for 5.x back compat  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene54Codec
specifier|public
class|class
name|Lucene54Codec
extends|extends
name|Codec
block|{
DECL|field|vectorsFormat
specifier|private
specifier|final
name|TermVectorsFormat
name|vectorsFormat
init|=
operator|new
name|Lucene50TermVectorsFormat
argument_list|()
decl_stmt|;
DECL|field|fieldInfosFormat
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
init|=
operator|new
name|Lucene50FieldInfosFormat
argument_list|()
decl_stmt|;
DECL|field|segmentInfosFormat
specifier|private
specifier|final
name|SegmentInfoFormat
name|segmentInfosFormat
init|=
operator|new
name|Lucene50SegmentInfoFormat
argument_list|()
decl_stmt|;
DECL|field|liveDocsFormat
specifier|private
specifier|final
name|LiveDocsFormat
name|liveDocsFormat
init|=
operator|new
name|Lucene50LiveDocsFormat
argument_list|()
decl_stmt|;
DECL|field|compoundFormat
specifier|private
specifier|final
name|CompoundFormat
name|compoundFormat
init|=
operator|new
name|Lucene50CompoundFormat
argument_list|()
decl_stmt|;
DECL|field|postingsFormat
specifier|private
specifier|final
name|PostingsFormat
name|postingsFormat
init|=
operator|new
name|PerFieldPostingsFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|Lucene54Codec
operator|.
name|this
operator|.
name|getPostingsFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|docValuesFormat
specifier|private
specifier|final
name|DocValuesFormat
name|docValuesFormat
init|=
operator|new
name|PerFieldDocValuesFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|Lucene54Codec
operator|.
name|this
operator|.
name|getDocValuesFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|storedFieldsFormat
specifier|private
specifier|final
name|StoredFieldsFormat
name|storedFieldsFormat
decl_stmt|;
comment|/**    * Instantiates a new codec.    */
DECL|method|Lucene54Codec
specifier|public
name|Lucene54Codec
parameter_list|()
block|{
name|this
argument_list|(
name|Mode
operator|.
name|BEST_SPEED
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiates a new codec, specifying the stored fields compression    * mode to use.    * @param mode stored fields compression mode to use for newly    *             flushed/merged segments.    */
DECL|method|Lucene54Codec
specifier|public
name|Lucene54Codec
parameter_list|(
name|Mode
name|mode
parameter_list|)
block|{
name|super
argument_list|(
literal|"Lucene54"
argument_list|)
expr_stmt|;
name|this
operator|.
name|storedFieldsFormat
operator|=
operator|new
name|Lucene50StoredFieldsFormat
argument_list|(
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
specifier|final
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|storedFieldsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
specifier|final
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|vectorsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
specifier|final
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|postingsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|fieldInfosFormat
return|;
block|}
annotation|@
name|Override
DECL|method|segmentInfoFormat
specifier|public
specifier|final
name|SegmentInfoFormat
name|segmentInfoFormat
parameter_list|()
block|{
return|return
name|segmentInfosFormat
return|;
block|}
annotation|@
name|Override
DECL|method|liveDocsFormat
specifier|public
specifier|final
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
block|{
return|return
name|liveDocsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|compoundFormat
specifier|public
specifier|final
name|CompoundFormat
name|compoundFormat
parameter_list|()
block|{
return|return
name|compoundFormat
return|;
block|}
comment|/** Returns the postings format that should be used for writing    *  new segments of<code>field</code>.    *    *  The default implementation always returns "Lucene50".    *<p>    *<b>WARNING:</b> if you subclass, you are responsible for index    *  backwards compatibility: future version of Lucene are only    *  guaranteed to be able to read the default implementation.    */
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|defaultFormat
return|;
block|}
comment|/** Returns the docvalues format that should be used for writing    *  new segments of<code>field</code>.    *    *  The default implementation always returns "Lucene54".    *<p>    *<b>WARNING:</b> if you subclass, you are responsible for index    *  backwards compatibility: future version of Lucene are only    *  guaranteed to be able to read the default implementation.    */
DECL|method|getDocValuesFormatForField
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|defaultDVFormat
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
specifier|final
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|docValuesFormat
return|;
block|}
annotation|@
name|Override
DECL|method|pointsFormat
specifier|public
specifier|final
name|PointsFormat
name|pointsFormat
parameter_list|()
block|{
return|return
name|PointsFormat
operator|.
name|EMPTY
return|;
block|}
DECL|field|defaultFormat
specifier|private
specifier|final
name|PostingsFormat
name|defaultFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
literal|"Lucene50"
argument_list|)
decl_stmt|;
DECL|field|defaultDVFormat
specifier|private
specifier|final
name|DocValuesFormat
name|defaultDVFormat
init|=
name|DocValuesFormat
operator|.
name|forName
argument_list|(
literal|"Lucene54"
argument_list|)
decl_stmt|;
DECL|field|normsFormat
specifier|private
specifier|final
name|NormsFormat
name|normsFormat
init|=
operator|new
name|Lucene53NormsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
specifier|final
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|normsFormat
return|;
block|}
block|}
end_class

end_unit

