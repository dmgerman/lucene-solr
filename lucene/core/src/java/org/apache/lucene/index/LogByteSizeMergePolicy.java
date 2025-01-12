begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** This is a {@link LogMergePolicy} that measures size of a  *  segment as the total byte size of the segment's files. */
end_comment

begin_class
DECL|class|LogByteSizeMergePolicy
specifier|public
class|class
name|LogByteSizeMergePolicy
extends|extends
name|LogMergePolicy
block|{
comment|/** Default minimum segment size.  @see setMinMergeMB */
DECL|field|DEFAULT_MIN_MERGE_MB
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_MIN_MERGE_MB
init|=
literal|1.6
decl_stmt|;
comment|/** Default maximum segment size.  A segment of this size    *  or larger will never be merged.  @see setMaxMergeMB */
DECL|field|DEFAULT_MAX_MERGE_MB
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_MAX_MERGE_MB
init|=
literal|2048
decl_stmt|;
comment|/** Default maximum segment size.  A segment of this size    *  or larger will never be merged during forceMerge.  @see setMaxMergeMBForForceMerge */
DECL|field|DEFAULT_MAX_MERGE_MB_FOR_FORCED_MERGE
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_MAX_MERGE_MB_FOR_FORCED_MERGE
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** Sole constructor, setting all settings to their    *  defaults. */
DECL|method|LogByteSizeMergePolicy
specifier|public
name|LogByteSizeMergePolicy
parameter_list|()
block|{
name|minMergeSize
operator|=
call|(
name|long
call|)
argument_list|(
name|DEFAULT_MIN_MERGE_MB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|maxMergeSize
operator|=
call|(
name|long
call|)
argument_list|(
name|DEFAULT_MAX_MERGE_MB
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// NOTE: in Java, if you cast a too-large double to long, as we are doing here, then it becomes Long.MAX_VALUE
name|maxMergeSizeForForcedMerge
operator|=
call|(
name|long
call|)
argument_list|(
name|DEFAULT_MAX_MERGE_MB_FOR_FORCED_MERGE
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|protected
name|long
name|size
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sizeBytes
argument_list|(
name|info
argument_list|,
name|writer
argument_list|)
return|;
block|}
comment|/**<p>Determines the largest segment (measured by total    *  byte size of the segment's files, in MB) that may be    *  merged with other segments.  Small values (e.g., less    *  than 50 MB) are best for interactive indexing, as this    *  limits the length of pauses while indexing to a few    *  seconds.  Larger values are best for batched indexing    *  and speedier searches.</p>    *    *<p>Note that {@link #setMaxMergeDocs} is also    *  used to check whether a segment is too large for    *  merging (it's either or).</p>*/
DECL|method|setMaxMergeMB
specifier|public
name|void
name|setMaxMergeMB
parameter_list|(
name|double
name|mb
parameter_list|)
block|{
name|maxMergeSize
operator|=
call|(
name|long
call|)
argument_list|(
name|mb
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the largest segment (measured by total byte    *  size of the segment's files, in MB) that may be merged    *  with other segments.    *  @see #setMaxMergeMB */
DECL|method|getMaxMergeMB
specifier|public
name|double
name|getMaxMergeMB
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|maxMergeSize
operator|)
operator|/
literal|1024
operator|/
literal|1024
return|;
block|}
comment|/**<p>Determines the largest segment (measured by total    *  byte size of the segment's files, in MB) that may be    *  merged with other segments during forceMerge. Setting    *  it low will leave the index with more than 1 segment,    *  even if {@link IndexWriter#forceMerge} is called.*/
DECL|method|setMaxMergeMBForForcedMerge
specifier|public
name|void
name|setMaxMergeMBForForcedMerge
parameter_list|(
name|double
name|mb
parameter_list|)
block|{
name|maxMergeSizeForForcedMerge
operator|=
call|(
name|long
call|)
argument_list|(
name|mb
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the largest segment (measured by total byte    *  size of the segment's files, in MB) that may be merged    *  with other segments during forceMerge.    *  @see #setMaxMergeMBForForcedMerge */
DECL|method|getMaxMergeMBForForcedMerge
specifier|public
name|double
name|getMaxMergeMBForForcedMerge
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|maxMergeSizeForForcedMerge
operator|)
operator|/
literal|1024
operator|/
literal|1024
return|;
block|}
comment|/** Sets the minimum size for the lowest level segments.    * Any segments below this size are considered to be on    * the same level (even if they vary drastically in size)    * and will be merged whenever there are mergeFactor of    * them.  This effectively truncates the "long tail" of    * small segments that would otherwise be created into a    * single level.  If you set this too large, it could    * greatly increase the merging cost during indexing (if    * you flush many small segments). */
DECL|method|setMinMergeMB
specifier|public
name|void
name|setMinMergeMB
parameter_list|(
name|double
name|mb
parameter_list|)
block|{
name|minMergeSize
operator|=
call|(
name|long
call|)
argument_list|(
name|mb
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
comment|/** Get the minimum size for a segment to remain    *  un-merged.    *  @see #setMinMergeMB **/
DECL|method|getMinMergeMB
specifier|public
name|double
name|getMinMergeMB
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|minMergeSize
operator|)
operator|/
literal|1024
operator|/
literal|1024
return|;
block|}
block|}
end_class

end_unit

