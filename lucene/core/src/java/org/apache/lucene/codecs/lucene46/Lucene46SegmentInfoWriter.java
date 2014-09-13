begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene46
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene46
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
name|CodecUtil
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
name|FieldInfos
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
name|store
operator|.
name|Directory
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
name|IOContext
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
name|IndexOutput
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
name|IOUtils
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Lucene 4.0 implementation of {@link SegmentInfoWriter}.  *   * @see Lucene46SegmentInfoFormat  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene46SegmentInfoWriter
specifier|public
class|class
name|Lucene46SegmentInfoWriter
extends|extends
name|SegmentInfoWriter
block|{
comment|/** Sole constructor. */
DECL|method|Lucene46SegmentInfoWriter
specifier|public
name|Lucene46SegmentInfoWriter
parameter_list|()
block|{   }
comment|/** Save a single segment's info. */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fis
parameter_list|,
name|IOContext
name|ioContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|si
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|SI_EXTENSION
argument_list|)
decl_stmt|;
name|si
operator|.
name|addFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
specifier|final
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|ioContext
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene46SegmentInfoFormat
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|Version
name|version
init|=
name|si
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|major
argument_list|<
literal|4
operator|||
name|version
operator|.
name|major
argument_list|>
literal|5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid major version: should be 4 or 5 but got: "
operator|+
name|version
operator|.
name|major
operator|+
literal|" segment="
operator|+
name|si
argument_list|)
throw|;
block|}
comment|// Write the Lucene version that created this segment, since 3.1
name|output
operator|.
name|writeString
argument_list|(
name|version
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|si
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|si
operator|.
name|getUseCompoundFile
argument_list|()
condition|?
name|SegmentInfo
operator|.
name|YES
else|:
name|SegmentInfo
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringStringMap
argument_list|(
name|si
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeStringSet
argument_list|(
name|si
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|si
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|output
argument_list|)
expr_stmt|;
comment|// TODO: are we doing this outside of the tracking wrapper? why must SIWriter cleanup like this?
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|si
operator|.
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

