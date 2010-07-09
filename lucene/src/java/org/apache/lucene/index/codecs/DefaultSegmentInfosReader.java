begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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
name|index
operator|.
name|SegmentInfos
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
name|ChecksumIndexInput
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
name|IndexInput
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link SegmentInfosReader}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DefaultSegmentInfosReader
specifier|public
class|class
name|DefaultSegmentInfosReader
extends|extends
name|SegmentInfosReader
block|{
annotation|@
name|Override
DECL|method|read
specifier|public
name|void
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segmentsFileName
parameter_list|,
name|CodecProvider
name|codecs
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
name|input
operator|=
name|openInput
argument_list|(
name|directory
argument_list|,
name|segmentsFileName
argument_list|)
expr_stmt|;
name|int
name|format
init|=
name|input
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// check that it is a format we can understand
if|if
condition|(
name|format
operator|<
name|SegmentInfos
operator|.
name|CURRENT_FORMAT
condition|)
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Unknown (newer than us?) format version: "
operator|+
name|format
argument_list|)
throw|;
name|infos
operator|.
name|version
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
comment|// read version
name|infos
operator|.
name|counter
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
comment|// read counter
for|for
control|(
name|int
name|i
init|=
name|input
operator|.
name|readInt
argument_list|()
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
comment|// read segmentInfos
name|infos
operator|.
name|add
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|directory
argument_list|,
name|format
argument_list|,
name|input
argument_list|,
name|codecs
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|infos
operator|.
name|userData
operator|=
name|input
operator|.
name|readStringStringMap
argument_list|()
expr_stmt|;
name|finalizeInput
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentsFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|segmentsFileName
argument_list|)
decl_stmt|;
return|return
operator|new
name|ChecksumIndexInput
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|finalizeInput
specifier|public
name|void
name|finalizeInput
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptIndexException
block|{
name|ChecksumIndexInput
name|cksumInput
init|=
operator|(
name|ChecksumIndexInput
operator|)
name|input
decl_stmt|;
specifier|final
name|long
name|checksumNow
init|=
name|cksumInput
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
specifier|final
name|long
name|checksumThen
init|=
name|cksumInput
operator|.
name|readLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|checksumNow
operator|!=
name|checksumThen
condition|)
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"checksum mismatch in segments file"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

