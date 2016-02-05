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

begin_comment
comment|/**  * A {@link MergeScheduler} which never executes any merges. It is also a  * singleton and can be accessed through {@link NoMergeScheduler#INSTANCE}. Use  * it if you want to prevent an {@link IndexWriter} from ever executing merges,  * regardless of the {@link MergePolicy} used. Note that you can achieve the  * same thing by using {@link NoMergePolicy}, however with  * {@link NoMergeScheduler} you also ensure that no unnecessary code of any  * {@link MergeScheduler} implementation is ever executed. Hence it is  * recommended to use both if you want to disable merges from ever happening.  */
end_comment

begin_class
DECL|class|NoMergeScheduler
specifier|public
specifier|final
class|class
name|NoMergeScheduler
extends|extends
name|MergeScheduler
block|{
comment|/** The single instance of {@link NoMergeScheduler} */
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|MergeScheduler
name|INSTANCE
init|=
operator|new
name|NoMergeScheduler
argument_list|()
decl_stmt|;
DECL|method|NoMergeScheduler
specifier|private
name|NoMergeScheduler
parameter_list|()
block|{
comment|// prevent instantiation
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|merge
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
block|{}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MergeScheduler
name|clone
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

