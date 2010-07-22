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
name|ChecksumIndexOutput
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
name|IndexOutput
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link SegmentInfosWriter}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DefaultSegmentInfosWriter
specifier|public
class|class
name|DefaultSegmentInfosWriter
extends|extends
name|SegmentInfosWriter
block|{
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
comment|/** Each segment records whether its postings are written    *  in the new flex format */
DECL|field|FORMAT_4_0
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_4_0
init|=
operator|-
literal|10
decl_stmt|;
comment|/** This must always point to the most recent file format.    * whenever you add a new format, make it 1 smaller (negative version logic)! */
DECL|field|FORMAT_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_4_0
decl_stmt|;
comment|/** This must always point to the first supported file format. */
DECL|field|FORMAT_MINIMUM
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_MINIMUM
init|=
name|FORMAT_DIAGNOSTICS
decl_stmt|;
annotation|@
name|Override
DECL|method|writeInfos
specifier|public
name|IndexOutput
name|writeInfos
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentFileName
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|createOutput
argument_list|(
name|dir
argument_list|,
name|segmentFileName
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
comment|// write FORMAT
name|out
operator|.
name|writeLong
argument_list|(
operator|++
name|infos
operator|.
name|version
argument_list|)
expr_stmt|;
comment|// every write changes
comment|// the index
name|out
operator|.
name|writeInt
argument_list|(
name|infos
operator|.
name|counter
argument_list|)
expr_stmt|;
comment|// write counter
name|out
operator|.
name|writeInt
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// write infos
for|for
control|(
name|SegmentInfo
name|si
range|:
name|infos
control|)
block|{
name|si
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeStringStringMap
argument_list|(
name|infos
operator|.
name|getUserData
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
DECL|method|createOutput
specifier|protected
name|IndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|plainOut
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|segmentFileName
argument_list|)
decl_stmt|;
name|ChecksumIndexOutput
name|out
init|=
operator|new
name|ChecksumIndexOutput
argument_list|(
name|plainOut
argument_list|)
decl_stmt|;
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|(
name|IndexOutput
name|segmentOutput
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|ChecksumIndexOutput
operator|)
name|segmentOutput
operator|)
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishCommit
specifier|public
name|void
name|finishCommit
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|ChecksumIndexOutput
operator|)
name|out
operator|)
operator|.
name|finishCommit
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

