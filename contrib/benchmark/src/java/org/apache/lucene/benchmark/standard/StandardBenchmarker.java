begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|standard
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|AbstractBenchmarker
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
name|BenchmarkOptions
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
name|Benchmarker
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
name|stats
operator|.
name|QueryData
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
name|stats
operator|.
name|TestData
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
name|stats
operator|.
name|TestRunData
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
name|stats
operator|.
name|TimeData
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
name|document
operator|.
name|DateTools
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|queryParser
operator|.
name|QueryParser
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
name|Hits
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
name|search
operator|.
name|Query
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  Reads in the Reuters Collection, downloaded from http://www.daviddlewis.com/resources/testcollections/reuters21578/reuters21578.tar.gz  * in the workingDir/reuters and indexes them using the {@link org.apache.lucene.analysis.standard.StandardAnalyzer}  *<p/>  * Runs a standard set of documents through an Indexer and then runs a standard set of queries against the index.  *  * @see org.apache.lucene.benchmark.standard.StandardBenchmarker#benchmark(java.io.File, org.apache.lucene.benchmark.BenchmarkOptions)  *  * @deprecated use the byTask code instead. See http://lucene.zones.apache.org:8080/hudson/job/Lucene-Nightly/javadoc/org/apache/lucene/benchmark/byTask/package-summary.html .  **/
end_comment

begin_class
DECL|class|StandardBenchmarker
specifier|public
class|class
name|StandardBenchmarker
extends|extends
name|AbstractBenchmarker
implements|implements
name|Benchmarker
block|{
DECL|field|SOURCE_DIR
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_DIR
init|=
literal|"reuters-out"
decl_stmt|;
DECL|field|INDEX_DIR
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DIR
init|=
literal|"index"
decl_stmt|;
comment|//30-MAR-1987 14:22:36.87
DECL|field|format
specifier|private
specifier|static
name|DateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd-MMM-yyyy kk:mm:ss.SSS"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
comment|//DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
static|static
block|{
name|format
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|StandardBenchmarker
specifier|public
name|StandardBenchmarker
parameter_list|()
block|{     }
DECL|method|benchmark
specifier|public
name|TestData
index|[]
name|benchmark
parameter_list|(
name|File
name|workingDir
parameter_list|,
name|BenchmarkOptions
name|opts
parameter_list|)
throws|throws
name|Exception
block|{
name|StandardOptions
name|options
init|=
operator|(
name|StandardOptions
operator|)
name|opts
decl_stmt|;
name|workingDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|sourceDir
init|=
name|getSourceDirectory
argument_list|(
name|workingDir
argument_list|)
decl_stmt|;
name|sourceDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|workingDir
argument_list|,
name|INDEX_DIR
argument_list|)
decl_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|List
name|queryList
init|=
operator|new
name|ArrayList
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|queryList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ReutersQueries
operator|.
name|STANDARD_QUERIES
argument_list|)
argument_list|)
expr_stmt|;
name|queryList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ReutersQueries
operator|.
name|getPrebuiltQueries
argument_list|(
literal|"body"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Query
index|[]
name|qs
init|=
name|createQueries
argument_list|(
name|queryList
argument_list|,
name|a
argument_list|)
decl_stmt|;
comment|// Here you can limit the set of query benchmarks
name|QueryData
index|[]
name|qds
init|=
name|QueryData
operator|.
name|getAll
argument_list|(
name|qs
argument_list|)
decl_stmt|;
comment|// Here you can narrow down the set of test parameters
name|TestData
index|[]
name|params
init|=
name|TestData
operator|.
name|getTestDataMinMaxMergeAndMaxBuffered
argument_list|(
operator|new
name|File
index|[]
block|{
name|sourceDir
comment|/*, jumboDir*/
block|}
argument_list|,
operator|new
name|Analyzer
index|[]
block|{
name|a
block|}
argument_list|)
decl_stmt|;
comment|//TestData.getAll(new File[]{sourceDir, jumboDir}, new Analyzer[]{a});
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Testing "
operator|+
name|params
operator|.
name|length
operator|+
literal|" different permutations."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|reset
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|params
index|[
name|i
index|]
operator|.
name|setDirectory
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
name|i
index|]
operator|.
name|setQueries
argument_list|(
name|qds
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|params
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|runBenchmark
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|options
argument_list|)
expr_stmt|;
comment|// Here you can collect and output the runData for further processing.
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|params
index|[
name|i
index|]
operator|.
name|showRunData
argument_list|(
name|params
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//bench.runSearchBenchmark(queries, dir);
name|params
index|[
name|i
index|]
operator|.
name|getDirectory
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"EXCEPTION: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|//break;
block|}
block|}
return|return
name|params
return|;
block|}
DECL|method|getSourceDirectory
specifier|protected
name|File
name|getSourceDirectory
parameter_list|(
name|File
name|workingDir
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|workingDir
argument_list|,
name|SOURCE_DIR
argument_list|)
return|;
block|}
comment|/**      * Run benchmark using supplied parameters.      *      * @param params benchmark parameters      * @throws Exception      */
DECL|method|runBenchmark
specifier|protected
name|void
name|runBenchmark
parameter_list|(
name|TestData
name|params
parameter_list|,
name|StandardOptions
name|options
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Start Time: "
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|runCount
init|=
name|options
operator|.
name|getRunCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|runCount
condition|;
name|i
operator|++
control|)
block|{
name|TestRunData
name|trd
init|=
operator|new
name|TestRunData
argument_list|()
decl_stmt|;
name|trd
operator|.
name|startRun
argument_list|()
expr_stmt|;
name|trd
operator|.
name|setId
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|params
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|params
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|iw
operator|.
name|setMergeFactor
argument_list|(
name|params
operator|.
name|getMergeFactor
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setMaxBufferedDocs
argument_list|(
name|params
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setUseCompoundFile
argument_list|(
name|params
operator|.
name|isCompound
argument_list|()
argument_list|)
expr_stmt|;
name|makeIndex
argument_list|(
name|trd
argument_list|,
name|params
operator|.
name|getSource
argument_list|()
argument_list|,
name|iw
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|isOptimize
argument_list|()
condition|)
block|{
name|TimeData
name|td
init|=
operator|new
name|TimeData
argument_list|(
literal|"optimize"
argument_list|)
decl_stmt|;
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
name|td
operator|.
name|start
argument_list|()
expr_stmt|;
name|iw
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|td
operator|.
name|stop
argument_list|()
expr_stmt|;
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueryData
index|[]
name|queries
init|=
name|params
operator|.
name|getQueries
argument_list|()
decl_stmt|;
if|if
condition|(
name|queries
operator|!=
literal|null
condition|)
block|{
name|IndexReader
name|ir
init|=
literal|null
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|queries
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|QueryData
name|qd
init|=
name|queries
index|[
name|k
index|]
decl_stmt|;
if|if
condition|(
name|ir
operator|!=
literal|null
operator|&&
name|qd
operator|.
name|reopen
condition|)
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
literal|null
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|ir
operator|==
literal|null
condition|)
block|{
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|params
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ir
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|qd
operator|.
name|warmup
condition|)
block|{
name|TimeData
name|td
init|=
operator|new
name|TimeData
argument_list|(
name|qd
operator|.
name|id
operator|+
literal|"-warm"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|ir
operator|.
name|maxDoc
argument_list|()
condition|;
name|m
operator|++
control|)
block|{
name|td
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|ir
operator|.
name|isDeleted
argument_list|(
name|m
argument_list|)
condition|)
block|{
name|td
operator|.
name|stop
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|td
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
name|TimeData
name|td
init|=
operator|new
name|TimeData
argument_list|(
name|qd
operator|.
name|id
operator|+
literal|"-srch"
argument_list|)
decl_stmt|;
name|td
operator|.
name|start
argument_list|()
expr_stmt|;
name|Hits
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|qd
operator|.
name|q
argument_list|)
decl_stmt|;
comment|//System.out.println("Hits Size: " + h.length() + " Query: " + qd.q);
name|td
operator|.
name|stop
argument_list|()
expr_stmt|;
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
name|td
operator|=
operator|new
name|TimeData
argument_list|(
name|qd
operator|.
name|id
operator|+
literal|"-trav"
argument_list|)
expr_stmt|;
if|if
condition|(
name|h
operator|!=
literal|null
operator|&&
name|h
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|h
operator|.
name|length
argument_list|()
condition|;
name|m
operator|++
control|)
block|{
name|td
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|id
init|=
name|h
operator|.
name|id
argument_list|(
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|qd
operator|.
name|retrieve
condition|)
block|{
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|td
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
empty_stmt|;
try|try
block|{
if|if
condition|(
name|ir
operator|!=
literal|null
condition|)
block|{
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
empty_stmt|;
block|}
name|trd
operator|.
name|endRun
argument_list|()
expr_stmt|;
name|params
operator|.
name|getRunData
argument_list|()
operator|.
name|add
argument_list|(
name|trd
argument_list|)
expr_stmt|;
comment|//System.out.println(params[i].showRunData(params[i].getId()));
comment|//params.showRunData(params.getId());
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"End Time: "
operator|+
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parse the Reuters SGML and index:      * Date, Title, Dateline, Body      *      *      *      * @param in        input file      * @return Lucene document      */
DECL|method|makeDocument
specifier|protected
name|Document
name|makeDocument
parameter_list|(
name|File
name|in
parameter_list|,
name|String
index|[]
name|tags
parameter_list|,
name|boolean
name|stored
parameter_list|,
name|boolean
name|tokenized
parameter_list|,
name|boolean
name|tfv
parameter_list|)
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// tag this document
if|if
condition|(
name|tags
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tags
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"tag"
operator|+
name|i
argument_list|,
name|tags
index|[
name|i
index|]
argument_list|,
name|stored
operator|==
literal|true
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|tokenized
operator|==
literal|true
condition|?
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|tfv
operator|==
literal|true
condition|?
name|Field
operator|.
name|TermVector
operator|.
name|YES
else|:
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"file"
argument_list|,
name|in
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|stored
operator|==
literal|true
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|tokenized
operator|==
literal|true
condition|?
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|tfv
operator|==
literal|true
condition|?
name|Field
operator|.
name|TermVector
operator|.
name|YES
else|:
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
comment|//First line is the date, 3rd is the title, rest is body
name|String
name|dateStr
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
comment|//skip an empty line
name|String
name|title
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
comment|//skip an empty line
name|StringBuffer
name|body
init|=
operator|new
name|StringBuffer
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|body
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|Date
name|date
init|=
name|format
operator|.
name|parse
argument_list|(
name|dateStr
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"date"
argument_list|,
name|DateTools
operator|.
name|dateToString
argument_list|(
name|date
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|SECOND
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
name|title
argument_list|,
name|stored
operator|==
literal|true
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|tokenized
operator|==
literal|true
condition|?
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|tfv
operator|==
literal|true
condition|?
name|Field
operator|.
name|TermVector
operator|.
name|YES
else|:
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
name|body
operator|.
name|toString
argument_list|()
argument_list|,
name|stored
operator|==
literal|true
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|tokenized
operator|==
literal|true
condition|?
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|,
name|tfv
operator|==
literal|true
condition|?
name|Field
operator|.
name|TermVector
operator|.
name|YES
else|:
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
comment|/**      * Make index, and collect time data.      *      * @param trd       run data to populate      * @param srcDir    directory with source files      * @param iw        index writer, already open      * @param stored    store values of fields      * @param tokenized tokenize fields      * @param tfv       store term vectors      * @throws Exception      */
DECL|method|makeIndex
specifier|protected
name|void
name|makeIndex
parameter_list|(
name|TestRunData
name|trd
parameter_list|,
name|File
name|srcDir
parameter_list|,
name|IndexWriter
name|iw
parameter_list|,
name|boolean
name|stored
parameter_list|,
name|boolean
name|tokenized
parameter_list|,
name|boolean
name|tfv
parameter_list|,
name|StandardOptions
name|options
parameter_list|)
throws|throws
name|Exception
block|{
comment|//File[] groups = srcDir.listFiles();
name|List
name|files
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|getAllFiles
argument_list|(
name|srcDir
argument_list|,
literal|null
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|long
name|cnt
init|=
literal|0L
decl_stmt|;
name|TimeData
name|td
init|=
operator|new
name|TimeData
argument_list|()
decl_stmt|;
name|td
operator|.
name|name
operator|=
literal|"addDocument"
expr_stmt|;
name|int
name|scaleUp
init|=
name|options
operator|.
name|getScaleUp
argument_list|()
decl_stmt|;
name|int
name|logStep
init|=
name|options
operator|.
name|getLogStep
argument_list|()
decl_stmt|;
name|int
name|max
init|=
name|Math
operator|.
name|min
argument_list|(
name|files
operator|.
name|size
argument_list|()
argument_list|,
name|options
operator|.
name|getMaximumDocumentsToIndex
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
literal|0
init|;
name|s
operator|<
name|scaleUp
condition|;
name|s
operator|++
control|)
block|{
name|String
index|[]
name|tags
init|=
operator|new
name|String
index|[]
block|{
name|srcDir
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|s
block|}
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|files
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
operator|(
name|File
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|doc
operator|=
name|makeDocument
argument_list|(
name|file
argument_list|,
name|tags
argument_list|,
name|stored
argument_list|,
name|tokenized
argument_list|,
name|tfv
argument_list|)
expr_stmt|;
name|td
operator|.
name|start
argument_list|()
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|td
operator|.
name|stop
argument_list|()
expr_stmt|;
name|cnt
operator|++
expr_stmt|;
if|if
condition|(
name|cnt
operator|%
name|logStep
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|" - processed "
operator|+
name|cnt
operator|+
literal|", run id="
operator|+
name|trd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
name|td
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|trd
operator|.
name|addData
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
DECL|method|getAllFiles
specifier|public
specifier|static
name|void
name|getAllFiles
parameter_list|(
name|File
name|srcDir
parameter_list|,
name|FileFilter
name|filter
parameter_list|,
name|List
name|allFiles
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|srcDir
operator|.
name|listFiles
argument_list|(
name|filter
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|getAllFiles
argument_list|(
name|file
argument_list|,
name|filter
argument_list|,
name|allFiles
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allFiles
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Parse the strings containing Lucene queries.      *      * @param qs array of strings containing query expressions      * @param a  analyzer to use when parsing queries      * @return array of Lucene queries      */
DECL|method|createQueries
specifier|public
specifier|static
name|Query
index|[]
name|createQueries
parameter_list|(
name|List
name|qs
parameter_list|,
name|Analyzer
name|a
parameter_list|)
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
literal|"body"
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|List
name|queries
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|qs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Object
name|query
init|=
name|qs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|String
condition|)
block|{
name|q
operator|=
name|qp
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|query
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|Query
condition|)
block|{
name|q
operator|=
operator|(
name|Query
operator|)
name|query
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unsupported Query Type: "
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|q
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|(
name|Query
index|[]
operator|)
name|queries
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**      * Remove existing index.      *      * @throws Exception      */
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|(
name|File
name|indexDir
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|indexDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|fullyDelete
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
comment|/**      * Save a stream to a file.      *      * @param is         input stream      * @param out        output file      * @param closeInput if true, close the input stream when done.      * @throws Exception      */
DECL|method|saveStream
specifier|protected
name|void
name|saveStream
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|File
name|out
parameter_list|,
name|boolean
name|closeInput
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
name|long
name|total
init|=
literal|0L
decl_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|delta
init|=
name|time
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|total
operator|+=
name|len
expr_stmt|;
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|time
operator|-
name|delta
operator|>
literal|5000
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|" - copied "
operator|+
name|total
operator|/
literal|1024
operator|+
literal|" kB..."
argument_list|)
expr_stmt|;
name|delta
operator|=
name|time
expr_stmt|;
block|}
block|}
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|closeInput
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

