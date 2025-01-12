begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.programmatic
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
name|programmatic
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|tasks
operator|.
name|AddDocTask
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
name|tasks
operator|.
name|CloseIndexTask
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
name|tasks
operator|.
name|CreateIndexTask
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
name|tasks
operator|.
name|RepSumByNameTask
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
name|tasks
operator|.
name|TaskSequence
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
comment|/**  * Sample performance test written programmatically - no algorithm file is needed here.  */
end_comment

begin_class
DECL|class|Sample
specifier|public
class|class
name|Sample
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|p
init|=
name|initProps
argument_list|()
decl_stmt|;
name|Config
name|conf
init|=
operator|new
name|Config
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
operator|new
name|PerfRunData
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// 1. top sequence
name|TaskSequence
name|top
init|=
operator|new
name|TaskSequence
argument_list|(
name|runData
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// top level, not parallel
comment|// 2. task to create the index
name|CreateIndexTask
name|create
init|=
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|top
operator|.
name|addTask
argument_list|(
name|create
argument_list|)
expr_stmt|;
comment|// 3. task seq to add 500 docs (order matters - top to bottom - add seq to top, only then add to seq)
name|TaskSequence
name|seq1
init|=
operator|new
name|TaskSequence
argument_list|(
name|runData
argument_list|,
literal|"AddDocs"
argument_list|,
name|top
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|seq1
operator|.
name|setRepetitions
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|seq1
operator|.
name|setNoChildReport
argument_list|()
expr_stmt|;
name|top
operator|.
name|addTask
argument_list|(
name|seq1
argument_list|)
expr_stmt|;
comment|// 4. task to add the doc
name|AddDocTask
name|addDoc
init|=
operator|new
name|AddDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
comment|//addDoc.setParams("1200"); // doc size limit if supported
name|seq1
operator|.
name|addTask
argument_list|(
name|addDoc
argument_list|)
expr_stmt|;
comment|// order matters 9see comment above)
comment|// 5. task to close the index
name|CloseIndexTask
name|close
init|=
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|top
operator|.
name|addTask
argument_list|(
name|close
argument_list|)
expr_stmt|;
comment|// task to report
name|RepSumByNameTask
name|rep
init|=
operator|new
name|RepSumByNameTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|top
operator|.
name|addTask
argument_list|(
name|rep
argument_list|)
expr_stmt|;
comment|// print algorithm
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|top
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// execute
name|top
operator|.
name|doLogic
argument_list|()
expr_stmt|;
block|}
comment|// Sample programmatic settings. Could also read from file.
DECL|method|initProps
specifier|private
specifier|static
name|Properties
name|initProps
parameter_list|()
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"task.max.depth.log"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"max.buffered"
argument_list|,
literal|"buf:10:10:100:100:10:10:100:100"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"doc.maker"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.ReutersContentSource"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"log.step"
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"doc.delete.step"
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"analyzer"
argument_list|,
literal|"org.apache.lucene.analysis.standard.StandardAnalyzer"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"doc.term.vector"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"directory"
argument_list|,
literal|"FSDirectory"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"query.maker"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.ReutersQueryMaker"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"doc.stored"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"docs.dir"
argument_list|,
literal|"reuters-out"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"compound"
argument_list|,
literal|"cmpnd:true:true:true:true:false:false:false:false"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"doc.tokenized"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"merge.factor"
argument_list|,
literal|"mrg:10:100:10:100:10:100:10:100"
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

