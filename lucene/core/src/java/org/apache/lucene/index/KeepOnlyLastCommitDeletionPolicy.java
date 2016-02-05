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

begin_comment
comment|/**  * This {@link IndexDeletionPolicy} implementation that  * keeps only the most recent commit and immediately removes  * all prior commits after a new commit is done.  This is  * the default deletion policy.  */
end_comment

begin_class
DECL|class|KeepOnlyLastCommitDeletionPolicy
specifier|public
specifier|final
class|class
name|KeepOnlyLastCommitDeletionPolicy
extends|extends
name|IndexDeletionPolicy
block|{
comment|/** Sole constructor. */
DECL|method|KeepOnlyLastCommitDeletionPolicy
specifier|public
name|KeepOnlyLastCommitDeletionPolicy
parameter_list|()
block|{   }
comment|/**    * Deletes all commits except the most recent one.    */
annotation|@
name|Override
DECL|method|onInit
specifier|public
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
block|{
comment|// Note that commits.size() should normally be 1:
name|onCommit
argument_list|(
name|commits
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes all commits except the most recent one.    */
annotation|@
name|Override
DECL|method|onCommit
specifier|public
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
block|{
comment|// Note that commits.size() should normally be 2 (if not
comment|// called by onInit above):
name|int
name|size
init|=
name|commits
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
name|size
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|commits
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

