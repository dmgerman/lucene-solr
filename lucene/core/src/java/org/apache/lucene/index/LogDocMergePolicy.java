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
comment|/** This is a {@link LogMergePolicy} that measures size of a  *  segment as the number of documents (not taking deletions  *  into account). */
end_comment

begin_class
DECL|class|LogDocMergePolicy
specifier|public
class|class
name|LogDocMergePolicy
extends|extends
name|LogMergePolicy
block|{
comment|/** Default minimum segment size.  @see setMinMergeDocs */
DECL|field|DEFAULT_MIN_MERGE_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_MERGE_DOCS
init|=
literal|1000
decl_stmt|;
comment|/** Sole constructor, setting all settings to their    *  defaults. */
DECL|method|LogDocMergePolicy
specifier|public
name|LogDocMergePolicy
parameter_list|()
block|{
name|minMergeSize
operator|=
name|DEFAULT_MIN_MERGE_DOCS
expr_stmt|;
comment|// maxMergeSize(ForForcedMerge) are never used by LogDocMergePolicy; set
comment|// it to Long.MAX_VALUE to disable it
name|maxMergeSize
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|maxMergeSizeForForcedMerge
operator|=
name|Long
operator|.
name|MAX_VALUE
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
name|sizeDocs
argument_list|(
name|info
argument_list|,
name|writer
argument_list|)
return|;
block|}
comment|/** Sets the minimum size for the lowest level segments.    * Any segments below this size are considered to be on    * the same level (even if they vary drastically in size)    * and will be merged whenever there are mergeFactor of    * them.  This effectively truncates the "long tail" of    * small segments that would otherwise be created into a    * single level.  If you set this too large, it could    * greatly increase the merging cost during indexing (if    * you flush many small segments). */
DECL|method|setMinMergeDocs
specifier|public
name|void
name|setMinMergeDocs
parameter_list|(
name|int
name|minMergeDocs
parameter_list|)
block|{
name|minMergeSize
operator|=
name|minMergeDocs
expr_stmt|;
block|}
comment|/** Get the minimum size for a segment to remain    *  un-merged.    *  @see #setMinMergeDocs **/
DECL|method|getMinMergeDocs
specifier|public
name|int
name|getMinMergeDocs
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|minMergeSize
return|;
block|}
block|}
end_class

end_unit

