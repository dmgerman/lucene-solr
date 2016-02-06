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
name|util
operator|.
name|List
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
comment|/**  *<p>Expert: policy for deletion of stale {@link IndexCommit index commits}.   *   *<p>Implement this interface, and pass it to one  * of the {@link IndexWriter} or {@link IndexReader}  * constructors, to customize when older  * {@link IndexCommit point-in-time commits}  * are deleted from the index directory.  The default deletion policy  * is {@link KeepOnlyLastCommitDeletionPolicy}, which always  * removes old commits as soon as a new commit is done (this  * matches the behavior before 2.2).</p>  *  *<p>One expected use case for this (and the reason why it  * was first created) is to work around problems with an  * index directory accessed via filesystems like NFS because  * NFS does not provide the "delete on last close" semantics  * that Lucene's "point in time" search normally relies on.  * By implementing a custom deletion policy, such as "a  * commit is only removed once it has been stale for more  * than X minutes", you can give your readers time to  * refresh to the new commit before {@link IndexWriter}  * removes the old commits.  Note that doing so will  * increase the storage requirements of the index.  See<a  * target="top"  * href="http://issues.apache.org/jira/browse/LUCENE-710">LUCENE-710</a>  * for details.</p>  *  *<p>Implementers of sub-classes should make sure that {@link #clone()}  * returns an independent instance able to work with any other {@link IndexWriter}  * or {@link Directory} instance.</p>  */
end_comment

begin_class
DECL|class|IndexDeletionPolicy
specifier|public
specifier|abstract
class|class
name|IndexDeletionPolicy
block|{
comment|/** Sole constructor, typically called by sub-classes constructors. */
DECL|method|IndexDeletionPolicy
specifier|protected
name|IndexDeletionPolicy
parameter_list|()
block|{}
comment|/**    *<p>This is called once when a writer is first    * instantiated to give the policy a chance to remove old    * commit points.</p>    *     *<p>The writer locates all index commits present in the     * index directory and calls this method.  The policy may     * choose to delete some of the commit points, doing so by    * calling method {@link IndexCommit#delete delete()}     * of {@link IndexCommit}.</p>    *     *<p><u>Note:</u> the last CommitPoint is the most recent one,    * i.e. the "front index state". Be careful not to delete it,    * unless you know for sure what you are doing, and unless     * you can afford to lose the index content while doing that.     *    * @param commits List of current     * {@link IndexCommit point-in-time commits},    *  sorted by age (the 0th one is the oldest commit).    *  Note that for a new index this method is invoked with    *  an empty list.    */
DECL|method|onInit
specifier|public
specifier|abstract
name|void
name|onInit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *<p>This is called each time the writer completed a commit.    * This gives the policy a chance to remove old commit points    * with each commit.</p>    *    *<p>The policy may now choose to delete old commit points     * by calling method {@link IndexCommit#delete delete()}     * of {@link IndexCommit}.</p>    *     *<p>This method is only called when {@link    * IndexWriter#commit} or {@link IndexWriter#close} is    * called, or possibly not at all if the {@link    * IndexWriter#rollback} is called.    *    *<p><u>Note:</u> the last CommitPoint is the most recent one,    * i.e. the "front index state". Be careful not to delete it,    * unless you know for sure what you are doing, and unless     * you can afford to lose the index content while doing that.    *      * @param commits List of {@link IndexCommit},    *  sorted by age (the 0th one is the oldest commit).    */
DECL|method|onCommit
specifier|public
specifier|abstract
name|void
name|onCommit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

