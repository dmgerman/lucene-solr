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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  *<p>Expert: a MergePolicy determines the sequence of  * primitive merge operations to be used for overall merge  * and optimize operations.</p>  *   *<p>Whenever the segments in an index have been altered by  * {@link IndexWriter}, either the addition of a newly  * flushed segment, addition of many segments from  * addIndexes* calls, or a previous merge that may now need  * to cascade, {@link IndexWriter} invokes {@link  * #findMerges} to give the MergePolicy a chance to pick  * merges that are now required.  This method returns a  * {@link MergeSpecification} instance describing the set of  * merges that should be done, or null if no merges are  * necessary.  When IndexWriter.optimize is called, it calls  * {@link #findMergesForOptimize} and the MergePolicy should  * then return the necessary merges.</p>  *  *<p>Note that the policy can return more than one merge at  * a time.  In this case, if the writer is using {@link  * SerialMergeScheduler}, the merges will be run  * sequentially but if it is using {@link  * ConcurrentMergeScheduler} they will be run concurrently.</p>  *   *<p>The default MergePolicy is {@link  * LogByteSizeMergePolicy}.</p>  *  *<p><b>NOTE:</b> This API is new and still experimental  * (subject to change suddenly in the next release)</p>  *  *<p><b>NOTE</b>: This class typically requires access to  * package-private APIs (e.g.<code>SegmentInfos</code>) to do its job;  * if you implement your own MergePolicy, you'll need to put  * it in package org.apache.lucene.index in order to use  * these APIs.  */
end_comment

begin_class
DECL|class|MergePolicy
specifier|public
specifier|abstract
class|class
name|MergePolicy
block|{
comment|/** OneMerge provides the information necessary to perform    *  an individual primitive merge operation, resulting in    *  a single new segment.  The merge spec includes the    *  subset of segments to be merged as well as whether the    *  new segment should use the compound file format. */
DECL|class|OneMerge
specifier|public
specifier|static
class|class
name|OneMerge
block|{
DECL|field|info
name|SegmentInfo
name|info
decl_stmt|;
comment|// used by IndexWriter
DECL|field|mergeDocStores
name|boolean
name|mergeDocStores
decl_stmt|;
comment|// used by IndexWriter
DECL|field|optimize
name|boolean
name|optimize
decl_stmt|;
comment|// used by IndexWriter
DECL|field|increfDone
name|boolean
name|increfDone
decl_stmt|;
comment|// used by IndexWriter
DECL|field|registerDone
name|boolean
name|registerDone
decl_stmt|;
comment|// used by IndexWriter
DECL|field|mergeGen
name|long
name|mergeGen
decl_stmt|;
comment|// used by IndexWriter
DECL|field|isExternal
name|boolean
name|isExternal
decl_stmt|;
comment|// used by IndexWriter
DECL|field|maxNumSegmentsOptimize
name|int
name|maxNumSegmentsOptimize
decl_stmt|;
comment|// used by IndexWriter
DECL|field|readers
name|SegmentReader
index|[]
name|readers
decl_stmt|;
comment|// used by IndexWriter
DECL|field|readersClone
name|SegmentReader
index|[]
name|readersClone
decl_stmt|;
comment|// used by IndexWriter
DECL|field|segments
specifier|final
name|SegmentInfos
name|segments
decl_stmt|;
DECL|field|useCompoundFile
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|field|aborted
name|boolean
name|aborted
decl_stmt|;
DECL|field|error
name|Throwable
name|error
decl_stmt|;
DECL|method|OneMerge
specifier|public
name|OneMerge
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|==
name|segments
operator|.
name|size
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"segments must include at least one segment"
argument_list|)
throw|;
name|this
operator|.
name|segments
operator|=
name|segments
expr_stmt|;
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
comment|/** Record that an exception occurred while executing      *  this merge */
DECL|method|setException
specifier|synchronized
name|void
name|setException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
comment|/** Retrieve previous exception set by {@link      *  #setException}. */
DECL|method|getException
specifier|synchronized
name|Throwable
name|getException
parameter_list|()
block|{
return|return
name|error
return|;
block|}
comment|/** Mark this merge as aborted.  If this is called      *  before the merge is committed then the merge will      *  not be committed. */
DECL|method|abort
specifier|synchronized
name|void
name|abort
parameter_list|()
block|{
name|aborted
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Returns true if this merge was aborted. */
DECL|method|isAborted
specifier|synchronized
name|boolean
name|isAborted
parameter_list|()
block|{
return|return
name|aborted
return|;
block|}
DECL|method|checkAborted
specifier|synchronized
name|void
name|checkAborted
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|MergeAbortedException
block|{
if|if
condition|(
name|aborted
condition|)
throw|throw
operator|new
name|MergeAbortedException
argument_list|(
literal|"merge is aborted: "
operator|+
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|segString
name|String
name|segString
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numSegments
init|=
name|segments
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|segments
operator|.
name|info
argument_list|(
name|i
argument_list|)
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
name|b
operator|.
name|append
argument_list|(
literal|" into "
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|optimize
condition|)
name|b
operator|.
name|append
argument_list|(
literal|" [optimize]"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * A MergeSpecification instance provides the information    * necessary to perform multiple merges.  It simply    * contains a list of {@link OneMerge} instances.    */
DECL|class|MergeSpecification
specifier|public
specifier|static
class|class
name|MergeSpecification
block|{
comment|/**      * The subset of segments to be included in the primitive merge.      */
DECL|field|merges
specifier|public
name|List
name|merges
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|OneMerge
name|merge
parameter_list|)
block|{
name|merges
operator|.
name|add
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
DECL|method|segString
specifier|public
name|String
name|segString
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"MergeSpec:\n"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|merges
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
literal|1
operator|+
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
operator|(
operator|(
name|OneMerge
operator|)
name|merges
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|segString
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** Exception thrown if there are any problems while    *  executing a merge. */
DECL|class|MergeException
specifier|public
specifier|static
class|class
name|MergeException
extends|extends
name|RuntimeException
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|/** @deprecated      *  Use {@link #MergePolicy.MergeException(String,Directory)} instead */
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|String
name|message
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** @deprecated      *  Use {@link #MergePolicy.MergeException(Throwable,Directory)} instead */
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|)
block|{
name|super
argument_list|(
name|exc
argument_list|)
expr_stmt|;
block|}
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|exc
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** Returns the {@link Directory} of the index that hit      *  the exception. */
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
block|}
DECL|class|MergeAbortedException
specifier|public
specifier|static
class|class
name|MergeAbortedException
extends|extends
name|IOException
block|{
DECL|method|MergeAbortedException
specifier|public
name|MergeAbortedException
parameter_list|()
block|{
name|super
argument_list|(
literal|"merge is aborted"
argument_list|)
expr_stmt|;
block|}
DECL|method|MergeAbortedException
specifier|public
name|MergeAbortedException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Determine what set of merge operations are now    * necessary on the index.  The IndexWriter calls this    * whenever there is a change to the segments.  This call    * is always synchronized on the IndexWriter instance so    * only one thread at a time will call this method.    *    * @param segmentInfos the total set of segments in the index    * @param writer IndexWriter instance    */
DECL|method|findMerges
specifier|abstract
name|MergeSpecification
name|findMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
comment|/**    * Determine what set of merge operations is necessary in    * order to optimize the index.  The IndexWriter calls    * this when its optimize() method is called.  This call    * is always synchronized on the IndexWriter instance so    * only one thread at a time will call this method.    *    * @param segmentInfos the total set of segments in the index    * @param writer IndexWriter instance    * @param maxSegmentCount requested maximum number of    *   segments in the index (currently this is always 1)    * @param segmentsToOptimize contains the specific    *   SegmentInfo instances that must be merged away.  This    *   may be a subset of all SegmentInfos.    */
DECL|method|findMergesForOptimize
specifier|abstract
name|MergeSpecification
name|findMergesForOptimize
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Set
name|segmentsToOptimize
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
comment|/**    * Determine what set of merge operations is necessary in    * order to expunge all deletes from the index.    * @param segmentInfos the total set of segments in the index    * @param writer IndexWriter instance    */
DECL|method|findMergesToExpungeDeletes
name|MergeSpecification
name|findMergesToExpungeDeletes
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
comment|/**    * Release all resources for the policy.    */
DECL|method|close
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Returns true if a newly flushed (not from merge)    * segment should use the compound file format.    */
DECL|method|useCompoundFile
specifier|abstract
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|SegmentInfo
name|newSegment
parameter_list|)
function_decl|;
comment|/**    * Returns true if the doc store files should use the    * compound file format.    */
DECL|method|useCompoundDocStore
specifier|abstract
name|boolean
name|useCompoundDocStore
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|)
function_decl|;
block|}
end_class

end_unit

