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
name|analysis
operator|.
name|Analyzer
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * Open an index writer.  * Other side effects: index writer object in perfRunData is set.  */
end_comment

begin_class
DECL|class|OpenIndexTask
specifier|public
class|class
name|OpenIndexTask
extends|extends
name|PerfTask
block|{
DECL|method|OpenIndexTask
specifier|public
name|OpenIndexTask
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
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|getRunData
argument_list|()
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|getRunData
argument_list|()
operator|.
name|getAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Config
name|config
init|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|boolean
name|cmpnd
init|=
name|config
operator|.
name|get
argument_list|(
literal|"compound"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|mrgf
init|=
name|config
operator|.
name|get
argument_list|(
literal|"merge.factor"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|mxbf
init|=
name|config
operator|.
name|get
argument_list|(
literal|"max.buffered"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// must update params for newly opened writer
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
name|mxbf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
name|mrgf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
name|cmpnd
argument_list|)
expr_stmt|;
comment|// this one redundant?
name|getRunData
argument_list|()
operator|.
name|setIndexWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
end_class

end_unit

