begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|IndexWriter
import|;
end_import

begin_comment
comment|/**  * Commits the IndexWriter.  *  */
end_comment

begin_class
DECL|class|CommitIndexTask
specifier|public
class|class
name|CommitIndexTask
extends|extends
name|PerfTask
block|{
DECL|field|commitUserData
name|String
name|commitUserData
init|=
literal|null
decl_stmt|;
DECL|method|CommitIndexTask
specifier|public
name|CommitIndexTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|commitUserData
operator|=
name|params
expr_stmt|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|iw
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|iw
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|commitUserData
operator|==
literal|null
condition|)
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
else|else
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|OpenReaderTask
operator|.
name|USER_DATA
argument_list|,
name|commitUserData
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

