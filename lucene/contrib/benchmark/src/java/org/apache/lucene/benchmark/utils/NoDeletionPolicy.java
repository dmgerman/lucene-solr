begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|utils
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
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
name|index
operator|.
name|IndexDeletionPolicy
import|;
end_import

begin_class
DECL|class|NoDeletionPolicy
specifier|public
class|class
name|NoDeletionPolicy
implements|implements
name|IndexDeletionPolicy
block|{
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
throws|throws
name|IOException
block|{
comment|// TODO Auto-generated method stub
block|}
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
throws|throws
name|IOException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

