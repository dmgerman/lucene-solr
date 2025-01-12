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

begin_class
DECL|class|TestLogMergePolicy
specifier|public
class|class
name|TestLogMergePolicy
extends|extends
name|BaseMergePolicyTestCase
block|{
DECL|method|mergePolicy
specifier|public
name|MergePolicy
name|mergePolicy
parameter_list|()
block|{
return|return
name|newLogMergePolicy
argument_list|(
name|random
argument_list|()
argument_list|)
return|;
block|}
DECL|method|testDefaultForcedMergeMB
specifier|public
name|void
name|testDefaultForcedMergeMB
parameter_list|()
block|{
name|LogByteSizeMergePolicy
name|mp
init|=
operator|new
name|LogByteSizeMergePolicy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|mp
operator|.
name|getMaxMergeMBForForcedMerge
argument_list|()
operator|>
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

