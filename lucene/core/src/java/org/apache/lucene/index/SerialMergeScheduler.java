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
comment|/** A {@link MergeScheduler} that simply does each merge  *  sequentially, using the current thread. */
end_comment

begin_class
DECL|class|SerialMergeScheduler
specifier|public
class|class
name|SerialMergeScheduler
extends|extends
name|MergeScheduler
block|{
comment|/** Sole constructor. */
DECL|method|SerialMergeScheduler
specifier|public
name|SerialMergeScheduler
parameter_list|()
block|{   }
comment|/** Just do the merges in sequence. We do this    * "synchronized" so that even if the application is using    * multiple threads, only one merge may run at a time. */
annotation|@
name|Override
DECL|method|merge
specifier|synchronized
specifier|public
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergeTrigger
name|trigger
parameter_list|,
name|boolean
name|newMergesFound
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|writer
operator|.
name|getNextMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
break|break;
name|writer
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
block|}
end_class

end_unit

