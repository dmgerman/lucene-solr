begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|PrintStream
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
name|util
operator|.
name|BitVector
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentWriteState
specifier|public
class|class
name|SegmentWriteState
block|{
DECL|field|infoStream
specifier|public
specifier|final
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|directory
specifier|public
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segmentName
specifier|public
specifier|final
name|String
name|segmentName
decl_stmt|;
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|numDocs
specifier|public
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|hasVectors
specifier|public
name|boolean
name|hasVectors
decl_stmt|;
comment|// Deletes to apply while we are flushing the segment.  A
comment|// Term is enrolled in here if it was deleted at one
comment|// point, and it's mapped to the docIDUpto, meaning any
comment|// docID< docIDUpto containing this term should be
comment|// deleted.
DECL|field|segDeletes
specifier|public
specifier|final
name|BufferedDeletes
name|segDeletes
decl_stmt|;
comment|// Lazily created:
DECL|field|deletedDocs
specifier|public
name|BitVector
name|deletedDocs
decl_stmt|;
DECL|field|segmentCodecs
specifier|final
name|SegmentCodecs
name|segmentCodecs
decl_stmt|;
DECL|field|codecId
specifier|public
specifier|final
name|String
name|codecId
decl_stmt|;
comment|/** Expert: The fraction of terms in the "dictionary" which should be stored    * in RAM.  Smaller values use more memory, but make searching slightly    * faster, while larger values use less memory and make searching slightly    * slower.  Searching is typically not dominated by dictionary lookup, so    * tweaking this is rarely useful.*/
DECL|field|termIndexInterval
specifier|public
name|int
name|termIndexInterval
decl_stmt|;
comment|// TODO: this should be private to the codec, not settable here or in IWC
comment|/** Expert: The fraction of TermDocs entries stored in skip tables,    * used to accelerate {@link DocsEnum#advance(int)}.  Larger values result in    * smaller indexes, greater acceleration, but fewer accelerable cases, while    * smaller values result in bigger indexes, less acceleration and more    * accelerable cases. More detailed experiments would be useful here. */
DECL|field|skipInterval
specifier|public
specifier|final
name|int
name|skipInterval
init|=
literal|16
decl_stmt|;
comment|/** Expert: The maximum number of skip levels. Smaller values result in     * slightly smaller indexes, but slower skipping in big posting lists.    */
DECL|field|maxSkipLevels
specifier|public
specifier|final
name|int
name|maxSkipLevels
init|=
literal|10
decl_stmt|;
DECL|method|SegmentWriteState
specifier|public
name|SegmentWriteState
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|String
name|segmentName
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|termIndexInterval
parameter_list|,
name|SegmentCodecs
name|segmentCodecs
parameter_list|,
name|BufferedDeletes
name|segDeletes
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|this
operator|.
name|segDeletes
operator|=
name|segDeletes
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentName
operator|=
name|segmentName
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|termIndexInterval
operator|=
name|termIndexInterval
expr_stmt|;
name|this
operator|.
name|segmentCodecs
operator|=
name|segmentCodecs
expr_stmt|;
name|codecId
operator|=
literal|""
expr_stmt|;
block|}
comment|/**    * Create a shallow {@link SegmentWriteState} copy final a codec ID    */
DECL|method|SegmentWriteState
name|SegmentWriteState
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|codecId
parameter_list|)
block|{
name|infoStream
operator|=
name|state
operator|.
name|infoStream
expr_stmt|;
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
name|segmentName
operator|=
name|state
operator|.
name|segmentName
expr_stmt|;
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|numDocs
operator|=
name|state
operator|.
name|numDocs
expr_stmt|;
name|termIndexInterval
operator|=
name|state
operator|.
name|termIndexInterval
expr_stmt|;
name|segmentCodecs
operator|=
name|state
operator|.
name|segmentCodecs
expr_stmt|;
name|this
operator|.
name|codecId
operator|=
name|codecId
expr_stmt|;
name|segDeletes
operator|=
name|state
operator|.
name|segDeletes
expr_stmt|;
block|}
block|}
end_class

end_unit

