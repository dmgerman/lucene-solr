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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import

begin_comment
comment|/**  * Close taxonomy reader.  *<br>Other side effects: taxonomy reader in perfRunData is nullified.  */
end_comment

begin_class
DECL|class|CloseTaxonomyReaderTask
specifier|public
class|class
name|CloseTaxonomyReaderTask
extends|extends
name|PerfTask
block|{
DECL|method|CloseTaxonomyReaderTask
specifier|public
name|CloseTaxonomyReaderTask
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
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|TaxonomyReader
name|taxoReader
init|=
name|getRunData
argument_list|()
operator|.
name|getTaxonomyReader
argument_list|()
decl_stmt|;
name|getRunData
argument_list|()
operator|.
name|setTaxonomyReader
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|taxoReader
operator|.
name|getRefCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: CloseTaxonomyReader: reference count is currently "
operator|+
name|taxoReader
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

