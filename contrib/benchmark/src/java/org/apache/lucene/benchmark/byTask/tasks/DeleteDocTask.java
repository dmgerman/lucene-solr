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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Delete a document by docid.  * Other side effects: none.  */
end_comment

begin_class
DECL|class|DeleteDocTask
specifier|public
class|class
name|DeleteDocTask
extends|extends
name|PerfTask
block|{
DECL|method|DeleteDocTask
specifier|public
name|DeleteDocTask
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
DECL|field|logStep
specifier|private
specifier|static
name|int
name|logStep
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|deleteStep
specifier|private
specifier|static
name|int
name|deleteStep
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|numDeleted
specifier|private
specifier|static
name|int
name|numDeleted
init|=
literal|0
decl_stmt|;
DECL|field|lastDeleted
specifier|private
specifier|static
name|int
name|lastDeleted
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docid
specifier|private
name|int
name|docid
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|byStep
specifier|private
name|boolean
name|byStep
init|=
literal|true
decl_stmt|;
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|getRunData
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|deleteDocument
argument_list|(
name|docid
argument_list|)
expr_stmt|;
name|lastDeleted
operator|=
name|docid
expr_stmt|;
return|return
literal|1
return|;
comment|// one work item done here
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#setup()    */
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|// one time static initializations
if|if
condition|(
name|logStep
operator|<
literal|0
condition|)
block|{
name|logStep
operator|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"doc.delete.log.step"
argument_list|,
literal|500
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteStep
operator|<
literal|0
condition|)
block|{
name|deleteStep
operator|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"doc.delete.step"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
comment|// set the docid to be deleted
name|docid
operator|=
operator|(
name|byStep
condition|?
name|lastDeleted
operator|+
name|deleteStep
else|:
name|docid
operator|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see PerfTask#tearDown()    */
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|log
argument_list|(
operator|++
name|numDeleted
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|log
specifier|private
name|void
name|log
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|logStep
operator|>
literal|0
operator|&&
operator|(
name|count
operator|%
name|logStep
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> processed "
operator|+
name|count
operator|+
literal|" docs, last deleted: "
operator|+
name|lastDeleted
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the params (docid only)    * @param params docid to delete, or -1 for deleting by delete gap settings.    */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|docid
operator|=
operator|(
name|int
operator|)
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|byStep
operator|=
operator|(
name|docid
operator|<
literal|0
operator|)
expr_stmt|;
block|}
block|}
end_class

end_unit

