begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|DocMaker
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
name|feeds
operator|.
name|HTMLParser
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
name|feeds
operator|.
name|QueryMaker
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
name|stats
operator|.
name|Points
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
name|ReadTask
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
name|SearchTask
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
name|FileUtils
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
name|store
operator|.
name|RAMDirectory
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Data maintained by a performance test run.  *<p>  * Data includes:  *<ul>  *<li>Configuration.  *<li>Directory, Writer, Reader.  *<li>Docmaker and a few instances of QueryMaker.  *<li>Analyzer.  *<li>Statistics data which updated during the run.  *</ul>  * Config properties: work.dir=&lt;path to root of docs and index dirs| Default: work&gt;  *</ul>  */
end_comment

begin_class
DECL|class|PerfRunData
specifier|public
class|class
name|PerfRunData
block|{
DECL|field|points
specifier|private
name|Points
name|points
decl_stmt|;
comment|// objects used during performance test run
comment|// directory, analyzer, docMaker - created at startup.
comment|// reader, writer, searcher - maintained by basic tasks.
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|docMaker
specifier|private
name|DocMaker
name|docMaker
decl_stmt|;
DECL|field|htmlParser
specifier|private
name|HTMLParser
name|htmlParser
decl_stmt|;
comment|// we use separate (identical) instances for each "read" task type, so each can iterate the quries separately.
DECL|field|readTaskQueryMaker
specifier|private
name|HashMap
name|readTaskQueryMaker
decl_stmt|;
DECL|field|qmkrClass
specifier|private
name|Class
name|qmkrClass
decl_stmt|;
DECL|field|indexReader
specifier|private
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|indexSearcher
specifier|private
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|IndexWriter
name|indexWriter
decl_stmt|;
DECL|field|config
specifier|private
name|Config
name|config
decl_stmt|;
DECL|field|startTimeMillis
specifier|private
name|long
name|startTimeMillis
decl_stmt|;
comment|// constructor
DECL|method|PerfRunData
specifier|public
name|PerfRunData
parameter_list|(
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
comment|// analyzer (default is standard analyzer)
name|analyzer
operator|=
operator|(
name|Analyzer
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"analyzer"
argument_list|,
literal|"org.apache.lucene.analysis.standard.StandardAnalyzer"
argument_list|)
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
comment|// doc maker
name|docMaker
operator|=
operator|(
name|DocMaker
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"doc.maker"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.SimpleDocMaker"
argument_list|)
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|docMaker
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// query makers
name|readTaskQueryMaker
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|qmkrClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"query.maker"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.SimpleQueryMaker"
argument_list|)
argument_list|)
expr_stmt|;
comment|// html parser, used for some doc makers
name|htmlParser
operator|=
operator|(
name|HTMLParser
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"html.parser"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.DemoHTMLParser"
argument_list|)
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|docMaker
operator|.
name|setHTMLParser
argument_list|(
name|htmlParser
argument_list|)
expr_stmt|;
comment|// index stuff
name|reinit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// statistic points
name|points
operator|=
operator|new
name|Points
argument_list|(
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"log.queries"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> queries:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getQueryMaker
argument_list|(
operator|new
name|SearchTask
argument_list|(
name|this
argument_list|)
argument_list|)
operator|.
name|printQueries
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// clean old stuff, reopen
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|boolean
name|eraseIndex
parameter_list|)
throws|throws
name|Exception
block|{
comment|// cleanup index
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// directory (default is ram-dir).
if|if
condition|(
literal|"FSDirectory"
operator|.
name|equals
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"directory"
argument_list|,
literal|"RAMDirectory"
argument_list|)
argument_list|)
condition|)
block|{
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"work.dir"
argument_list|,
literal|"work"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
if|if
condition|(
name|eraseIndex
operator|&&
name|indexDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
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
name|directory
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
block|}
comment|// inputs
name|resetInputs
argument_list|()
expr_stmt|;
comment|// release unused stuff
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
comment|// Re-init clock
name|setStartTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|setStartTimeMillis
specifier|public
name|long
name|setStartTimeMillis
parameter_list|()
block|{
name|startTimeMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
return|return
name|startTimeMillis
return|;
block|}
comment|/**    * @return Start time in milliseconds    */
DECL|method|getStartTimeMillis
specifier|public
name|long
name|getStartTimeMillis
parameter_list|()
block|{
return|return
name|startTimeMillis
return|;
block|}
comment|/**    * @return Returns the points.    */
DECL|method|getPoints
specifier|public
name|Points
name|getPoints
parameter_list|()
block|{
return|return
name|points
return|;
block|}
comment|/**    * @return Returns the directory.    */
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|directory
return|;
block|}
comment|/**    * @param directory The directory to set.    */
DECL|method|setDirectory
specifier|public
name|void
name|setDirectory
parameter_list|(
name|Directory
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
comment|/**    * @return Returns the indexReader.    */
DECL|method|getIndexReader
specifier|public
name|IndexReader
name|getIndexReader
parameter_list|()
block|{
return|return
name|indexReader
return|;
block|}
comment|/**    * @return Returns the indexSearcher.    */
DECL|method|getIndexSearcher
specifier|public
name|IndexSearcher
name|getIndexSearcher
parameter_list|()
block|{
return|return
name|indexSearcher
return|;
block|}
comment|/**    * @param indexReader The indexReader to set.    */
DECL|method|setIndexReader
specifier|public
name|void
name|setIndexReader
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
block|{
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexSearcher
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * @return Returns the indexWriter.    */
DECL|method|getIndexWriter
specifier|public
name|IndexWriter
name|getIndexWriter
parameter_list|()
block|{
return|return
name|indexWriter
return|;
block|}
comment|/**    * @param indexWriter The indexWriter to set.    */
DECL|method|setIndexWriter
specifier|public
name|void
name|setIndexWriter
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|)
block|{
name|this
operator|.
name|indexWriter
operator|=
name|indexWriter
expr_stmt|;
block|}
comment|/**    * @return Returns the anlyzer.    */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/**    * @return Returns the docMaker.    */
DECL|method|getDocMaker
specifier|public
name|DocMaker
name|getDocMaker
parameter_list|()
block|{
return|return
name|docMaker
return|;
block|}
comment|/**    * @return Returns the config.    */
DECL|method|getConfig
specifier|public
name|Config
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
block|{
name|docMaker
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|Iterator
name|it
init|=
name|readTaskQueryMaker
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
operator|(
operator|(
name|QueryMaker
operator|)
name|it
operator|.
name|next
argument_list|()
operator|)
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @return Returns the queryMaker by read task type (class)    */
DECL|method|getQueryMaker
specifier|synchronized
specifier|public
name|QueryMaker
name|getQueryMaker
parameter_list|(
name|ReadTask
name|readTask
parameter_list|)
block|{
comment|// mapping the query maker by task class allows extending/adding new search/read tasks
comment|// without needing to modify this class.
name|Class
name|readTaskClass
init|=
name|readTask
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|QueryMaker
name|qm
init|=
operator|(
name|QueryMaker
operator|)
name|readTaskQueryMaker
operator|.
name|get
argument_list|(
name|readTaskClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|qm
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|qm
operator|=
operator|(
name|QueryMaker
operator|)
name|qmkrClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|qm
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|readTaskQueryMaker
operator|.
name|put
argument_list|(
name|readTaskClass
argument_list|,
name|qm
argument_list|)
expr_stmt|;
block|}
return|return
name|qm
return|;
block|}
comment|/**    * @return Returns the htmlParser.    */
DECL|method|getHtmlParser
specifier|public
name|HTMLParser
name|getHtmlParser
parameter_list|()
block|{
return|return
name|htmlParser
return|;
block|}
block|}
end_class

end_unit

