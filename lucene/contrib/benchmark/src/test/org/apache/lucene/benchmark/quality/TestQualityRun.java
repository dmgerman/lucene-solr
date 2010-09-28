begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.quality
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BenchmarkTestCase
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
name|quality
operator|.
name|trec
operator|.
name|TrecJudge
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
name|quality
operator|.
name|trec
operator|.
name|TrecTopicsReader
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
name|quality
operator|.
name|utils
operator|.
name|SimpleQQParser
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
name|quality
operator|.
name|utils
operator|.
name|SubmissionReport
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
name|search
operator|.
name|IndexSearcher
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
name|FSDirectory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_comment
comment|/**  * Test that quality run does its job.  *<p>  * NOTE: if the default scoring or StandardAnalyzer is changed, then  * this test will not work correctly, as it does not dynamically  * generate its test trec topics/qrels!  */
end_comment

begin_class
DECL|class|TestQualityRun
specifier|public
class|class
name|TestQualityRun
extends|extends
name|BenchmarkTestCase
block|{
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|copyToWorkDir
argument_list|(
literal|"reuters.578.lines.txt.bz2"
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrecQuality
specifier|public
name|void
name|testTrecQuality
parameter_list|()
throws|throws
name|Exception
block|{
comment|// first create the partial reuters index
name|createReutersIndex
argument_list|()
expr_stmt|;
name|int
name|maxResults
init|=
literal|1000
decl_stmt|;
name|String
name|docNameField
init|=
literal|"doctitle"
decl_stmt|;
comment|// orig docID is in the linedoc format title
name|PrintWriter
name|logger
init|=
name|VERBOSE
condition|?
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
literal|true
argument_list|)
else|:
literal|null
decl_stmt|;
comment|// prepare topics
name|InputStream
name|topics
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"trecTopics.txt"
argument_list|)
decl_stmt|;
name|TrecTopicsReader
name|qReader
init|=
operator|new
name|TrecTopicsReader
argument_list|()
decl_stmt|;
name|QualityQuery
name|qqs
index|[]
init|=
name|qReader
operator|.
name|readQueries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|topics
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// prepare judge
name|InputStream
name|qrels
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"trecQRels.txt"
argument_list|)
decl_stmt|;
name|Judge
name|judge
init|=
operator|new
name|TrecJudge
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|qrels
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// validate topics& judgments match each other
name|judge
operator|.
name|validateData
argument_list|(
name|qqs
argument_list|,
name|logger
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"index"
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|QualityQueryParser
name|qqParser
init|=
operator|new
name|SimpleQQParser
argument_list|(
literal|"title"
argument_list|,
literal|"body"
argument_list|)
decl_stmt|;
name|QualityBenchmark
name|qrun
init|=
operator|new
name|QualityBenchmark
argument_list|(
name|qqs
argument_list|,
name|qqParser
argument_list|,
name|searcher
argument_list|,
name|docNameField
argument_list|)
decl_stmt|;
name|SubmissionReport
name|submitLog
init|=
name|VERBOSE
condition|?
operator|new
name|SubmissionReport
argument_list|(
name|logger
argument_list|,
literal|"TestRun"
argument_list|)
else|:
literal|null
decl_stmt|;
name|qrun
operator|.
name|setMaxResults
argument_list|(
name|maxResults
argument_list|)
expr_stmt|;
name|QualityStats
name|stats
index|[]
init|=
name|qrun
operator|.
name|execute
argument_list|(
name|judge
argument_list|,
name|submitLog
argument_list|,
name|logger
argument_list|)
decl_stmt|;
comment|// --------- verify by the way judgments were altered for this test:
comment|// for some queries, depending on m = qnum % 8
comment|// m==0: avg_precision and recall are hurt, by marking fake docs as relevant
comment|// m==1: precision_at_n and avg_precision are hurt, by unmarking relevant docs
comment|// m==2: all precision, precision_at_n and recall are hurt.
comment|// m>=3: these queries remain perfect
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|QualityStats
name|s
init|=
name|stats
index|[
name|i
index|]
decl_stmt|;
switch|switch
condition|(
name|i
operator|%
literal|8
condition|)
block|{
case|case
literal|0
case|:
name|assertTrue
argument_list|(
literal|"avg-p should be hurt: "
operator|+
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"recall should be hurt: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getRecall
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be perfect: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1E
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|1
case|:
name|assertTrue
argument_list|(
literal|"avg-p should be hurt"
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"recall should be perfect: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1E
operator|-
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be hurt: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
name|assertTrue
argument_list|(
literal|"avg-p should be hurt: "
operator|+
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"recall should be hurt: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getRecall
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be hurt: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
block|{
name|assertEquals
argument_list|(
literal|"avg-p should be perfect: "
operator|+
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1E
operator|-
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"recall should be perfect: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1E
operator|-
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be perfect: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1E
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|QualityStats
name|avg
init|=
name|QualityStats
operator|.
name|average
argument_list|(
name|stats
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|avg
operator|.
name|log
argument_list|(
literal|"Average statistis:"
argument_list|,
literal|1
argument_list|,
name|logger
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"mean avg-p should be hurt: "
operator|+
name|avg
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
operator|>
name|avg
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"avg recall should be hurt: "
operator|+
name|avg
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
operator|>
name|avg
operator|.
name|getRecall
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"avg p_at_"
operator|+
name|j
operator|+
literal|" should be hurt: "
operator|+
name|avg
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
operator|>
name|avg
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTrecTopicsReader
specifier|public
name|void
name|testTrecTopicsReader
parameter_list|()
throws|throws
name|Exception
block|{
comment|// prepare topics
name|InputStream
name|topicsFile
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"trecTopics.txt"
argument_list|)
decl_stmt|;
name|TrecTopicsReader
name|qReader
init|=
operator|new
name|TrecTopicsReader
argument_list|()
decl_stmt|;
name|QualityQuery
name|qqs
index|[]
init|=
name|qReader
operator|.
name|readQueries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|topicsFile
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|qqs
operator|.
name|length
argument_list|)
expr_stmt|;
name|QualityQuery
name|qq
init|=
name|qqs
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"statement months  total 1987"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Topic 0 Description Line 1 Topic 0 Description Line 2"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"description"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Topic 0 Narrative Line 1 Topic 0 Narrative Line 2"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"narrative"
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|=
name|qqs
index|[
literal|1
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|"agreed 15  against five"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Topic 1 Description Line 1 Topic 1 Description Line 2"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"description"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Topic 1 Narrative Line 1 Topic 1 Narrative Line 2"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"narrative"
argument_list|)
argument_list|)
expr_stmt|;
name|qq
operator|=
name|qqs
index|[
literal|19
index|]
expr_stmt|;
name|assertEquals
argument_list|(
literal|"20 while  common week"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Topic 19 Description Line 1 Topic 19 Description Line 2"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"description"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Topic 19 Narrative Line 1 Topic 19 Narrative Line 2"
argument_list|,
name|qq
operator|.
name|getValue
argument_list|(
literal|"narrative"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// use benchmark logic to create the mini Reuters index
DECL|method|createReutersIndex
specifier|private
name|void
name|createReutersIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 1. alg definition
name|String
name|algLines
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"content.source=org.apache.lucene.benchmark.byTask.feeds.LineDocSource"
block|,
literal|"analyzer=org.apache.lucene.analysis.standard.ClassicAnalyzer"
block|,
literal|"docs.file="
operator|+
name|getWorkDirResourcePath
argument_list|(
literal|"reuters.578.lines.txt.bz2"
argument_list|)
block|,
literal|"content.source.log.step=2500"
block|,
literal|"doc.term.vector=false"
block|,
literal|"content.source.forever=false"
block|,
literal|"directory=FSDirectory"
block|,
literal|"doc.stored=true"
block|,
literal|"doc.tokenized=true"
block|,
literal|"# ----- alg "
block|,
literal|"ResetSystemErase"
block|,
literal|"CreateIndex"
block|,
literal|"{ AddDoc } : *"
block|,
literal|"CloseIndex"
block|,     }
decl_stmt|;
comment|// 2. execute the algorithm  (required in every "logic" test)
name|execBenchmark
argument_list|(
name|algLines
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

