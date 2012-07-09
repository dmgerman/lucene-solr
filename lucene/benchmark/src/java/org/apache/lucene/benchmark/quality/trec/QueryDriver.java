begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.quality.trec
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
operator|.
name|trec
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|benchmark
operator|.
name|quality
operator|.
name|*
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
name|DirectoryReader
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
name|IndexReader
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|OutputStreamWriter
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Command-line tool for doing a TREC evaluation run.  **/
end_comment

begin_class
DECL|class|QueryDriver
specifier|public
class|class
name|QueryDriver
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
if|if
condition|(
name|args
operator|.
name|length
argument_list|<
literal|4
operator|||
name|args
operator|.
name|length
argument_list|>
literal|5
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: QueryDriver<topicsFile><qrelsFile><submissionFile><indexDir> [querySpec]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"topicsFile: input file containing queries"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"qrelsFile: input file containing relevance judgements"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"submissionFile: output submission file for trec_eval"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"indexDir: index directory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"querySpec: string composed of fields to use in query consisting of T=title,D=description,N=narrative:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"\texample: TD (query on Title + Description). The default is T (title only)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|File
name|topicsFile
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|File
name|qrelsFile
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|SubmissionReport
name|submitLog
init|=
operator|new
name|SubmissionReport
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"lucene"
argument_list|)
decl_stmt|;
name|FSDirectory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|fieldSpec
init|=
name|args
operator|.
name|length
operator|==
literal|5
condition|?
name|args
index|[
literal|4
index|]
else|:
literal|"T"
decl_stmt|;
comment|// default to Title-only if not specified.
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|maxResults
init|=
literal|1000
decl_stmt|;
name|String
name|docNameField
init|=
literal|"docname"
decl_stmt|;
name|PrintWriter
name|logger
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// use trec utilities to read trec topics into quality queries
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
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|topicsFile
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// prepare judge, with trec utilities that read from a QRels file
name|Judge
name|judge
init|=
operator|new
name|TrecJudge
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|qrelsFile
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
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
name|Set
argument_list|<
name|String
argument_list|>
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldSpec
operator|.
name|indexOf
argument_list|(
literal|'T'
argument_list|)
operator|>=
literal|0
condition|)
name|fieldSet
operator|.
name|add
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldSpec
operator|.
name|indexOf
argument_list|(
literal|'D'
argument_list|)
operator|>=
literal|0
condition|)
name|fieldSet
operator|.
name|add
argument_list|(
literal|"description"
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldSpec
operator|.
name|indexOf
argument_list|(
literal|'N'
argument_list|)
operator|>=
literal|0
condition|)
name|fieldSet
operator|.
name|add
argument_list|(
literal|"narrative"
argument_list|)
expr_stmt|;
comment|// set the parsing of quality queries into Lucene queries.
name|QualityQueryParser
name|qqParser
init|=
operator|new
name|SimpleQQParser
argument_list|(
name|fieldSet
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|"body"
argument_list|)
decl_stmt|;
comment|// run the benchmark
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
comment|// print an avarage sum of the results
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
name|avg
operator|.
name|log
argument_list|(
literal|"SUMMARY"
argument_list|,
literal|2
argument_list|,
name|logger
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

