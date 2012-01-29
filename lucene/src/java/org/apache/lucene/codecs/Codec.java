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
name|IndexFileNames
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
name|util
operator|.
name|NamedSPILoader
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
name|StringHelper
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
name|store
operator|.
name|CompoundFileDirectory
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * Encodes/decodes an inverted index segment  */
end_comment

begin_class
DECL|class|Codec
specifier|public
specifier|abstract
class|class
name|Codec
implements|implements
name|NamedSPILoader
operator|.
name|NamedSPI
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|NamedSPILoader
argument_list|<
name|Codec
argument_list|>
name|loader
init|=
operator|new
name|NamedSPILoader
argument_list|<
name|Codec
argument_list|>
argument_list|(
name|Codec
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|Codec
specifier|public
name|Codec
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** Populates<code>files</code> with all filenames needed for     * the<code>info</code> segment.    */
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
comment|// nocommit: get this out of here: 3.x codec should override this
name|String
name|version
init|=
name|info
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|getVersionComparator
argument_list|()
operator|.
name|compare
argument_list|(
literal|"4.0"
argument_list|,
name|version
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|postingsFormat
argument_list|()
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
literal|""
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|storedFieldsFormat
argument_list|()
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|termVectorsFormat
argument_list|()
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|fieldInfosFormat
argument_list|()
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
comment|// TODO: segmentInfosFormat should be allowed to declare additional files
comment|// if it wants, in addition to segments_N
name|docValuesFormat
argument_list|()
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|normsFormat
argument_list|()
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Populates<code>files</code> with any filenames that are    * stored outside of CFS for the<code>info</code> segment.    */
DECL|method|separateFiles
specifier|public
name|void
name|separateFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|liveDocsFormat
argument_list|()
operator|.
name|separateFiles
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|normsFormat
argument_list|()
operator|.
name|separateFiles
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
comment|/** Encodes/decodes postings */
DECL|method|postingsFormat
specifier|public
specifier|abstract
name|PostingsFormat
name|postingsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes docvalues */
DECL|method|docValuesFormat
specifier|public
specifier|abstract
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes stored fields */
DECL|method|storedFieldsFormat
specifier|public
specifier|abstract
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes term vectors */
DECL|method|termVectorsFormat
specifier|public
specifier|abstract
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes field infos file */
DECL|method|fieldInfosFormat
specifier|public
specifier|abstract
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes segments file */
DECL|method|segmentInfosFormat
specifier|public
specifier|abstract
name|SegmentInfosFormat
name|segmentInfosFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes document normalization values */
DECL|method|normsFormat
specifier|public
specifier|abstract
name|NormsFormat
name|normsFormat
parameter_list|()
function_decl|;
comment|/** Encodes/decodes live docs */
DECL|method|liveDocsFormat
specifier|public
specifier|abstract
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
function_decl|;
comment|/** looks up a codec by name */
DECL|method|forName
specifier|public
specifier|static
name|Codec
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|loader
operator|.
name|lookup
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available codec names */
DECL|method|availableCodecs
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availableCodecs
parameter_list|()
block|{
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
DECL|field|defaultCodec
specifier|private
specifier|static
name|Codec
name|defaultCodec
init|=
name|Codec
operator|.
name|forName
argument_list|(
literal|"Lucene40"
argument_list|)
decl_stmt|;
comment|// TODO: should we use this, or maybe a system property is better?
DECL|method|getDefault
specifier|public
specifier|static
name|Codec
name|getDefault
parameter_list|()
block|{
return|return
name|defaultCodec
return|;
block|}
DECL|method|setDefault
specifier|public
specifier|static
name|void
name|setDefault
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
name|defaultCodec
operator|=
name|codec
expr_stmt|;
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
name|name
return|;
block|}
block|}
end_class

end_unit

