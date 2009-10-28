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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
comment|/**  *<p>Expert: represents a single commit into an index as seen by the  * {@link IndexDeletionPolicy} or {@link IndexReader}.</p>  *  *<p> Changes to the content of an index are made visible  * only after the writer who made that change commits by  * writing a new segments file  * (<code>segments_N</code>). This point in time, when the  * action of writing of a new segments file to the directory  * is completed, is an index commit.</p>  *  *<p>Each index commit point has a unique segments file  * associated with it. The segments file associated with a  * later index commit point would have a larger N.</p>  *  *<p><b>WARNING</b>: This API is a new and experimental and  * may suddenly change.</p> */
end_comment

begin_class
DECL|class|IndexCommit
specifier|public
specifier|abstract
class|class
name|IndexCommit
block|{
comment|/**    * Get the segments file (<code>segments_N</code>) associated     * with this commit point.    */
DECL|method|getSegmentsFileName
specifier|public
specifier|abstract
name|String
name|getSegmentsFileName
parameter_list|()
function_decl|;
comment|/**    * Returns all index files referenced by this commit point.    */
DECL|method|getFileNames
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the {@link Directory} for the index.    */
DECL|method|getDirectory
specifier|public
specifier|abstract
name|Directory
name|getDirectory
parameter_list|()
function_decl|;
comment|/**    * Delete this commit point.  This only applies when using    * the commit point in the context of IndexWriter's    * IndexDeletionPolicy.    *<p>    * Upon calling this, the writer is notified that this commit     * point should be deleted.     *<p>    * Decision that a commit-point should be deleted is taken by the {@link IndexDeletionPolicy} in effect    * and therefore this should only be called by its {@link IndexDeletionPolicy#onInit onInit()} or     * {@link IndexDeletionPolicy#onCommit onCommit()} methods.   */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support this method."
argument_list|)
throw|;
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support this method."
argument_list|)
throw|;
block|}
comment|/**    * Returns true if this commit is an optimized index.    */
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support this method."
argument_list|)
throw|;
block|}
comment|/**    * Two IndexCommits are equal if both their Directory and versions are equal.    */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|IndexCommit
condition|)
block|{
name|IndexCommit
name|otherCommit
init|=
operator|(
name|IndexCommit
operator|)
name|other
decl_stmt|;
return|return
name|otherCommit
operator|.
name|getDirectory
argument_list|()
operator|.
name|equals
argument_list|(
name|getDirectory
argument_list|()
argument_list|)
operator|&&
name|otherCommit
operator|.
name|getVersion
argument_list|()
operator|==
name|getVersion
argument_list|()
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getDirectory
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|+
name|getSegmentsFileName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns the version for this IndexCommit.  This is the    *  same value that {@link IndexReader#getVersion} would    *  return if it were opened on this commit. */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support this method."
argument_list|)
throw|;
block|}
comment|/** Returns the generation (the _N in segments_N) for this    *  IndexCommit */
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support this method."
argument_list|)
throw|;
block|}
comment|/** Convenience method that returns the last modified time    *  of the segments_N file corresponding to this index    *  commit, equivalent to    *  getDirectory().fileModified(getSegmentsFileName()). */
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|()
operator|.
name|fileModified
argument_list|(
name|getSegmentsFileName
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns userData, previously passed to {@link    *  IndexWriter#commit(Map)} for this commit.  Map is    *  String -> String. */
DECL|method|getUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserData
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This IndexCommit does not support this method."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

