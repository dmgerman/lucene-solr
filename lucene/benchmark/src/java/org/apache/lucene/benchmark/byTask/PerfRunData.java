begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Locale
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
name|ContentSource
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
name|FacetSource
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
name|NewAnalyzerTask
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
name|PerfTask
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
name|AnalyzerFactory
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|TaxonomyWriter
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

begin_comment
comment|/**  * Data maintained by a performance test run.  *<p>  * Data includes:  *<ul>  *<li>Configuration.  *<li>Directory, Writer, Reader.  *<li>Taxonomy Directory, Writer, Reader.  *<li>DocMaker, FacetSource and a few instances of QueryMaker.  *<li>Named AnalysisFactories.  *<li>Analyzer.  *<li>Statistics data which updated during the run.  *</ul>  * Config properties:  *<ul>  *<li><b>work.dir</b>=&lt;path to root of docs and index dirs| Default: work&gt;  *<li><b>analyzer</b>=&lt;class name for analyzer| Default: StandardAnalyzer&gt;  *<li><b>doc.maker</b>=&lt;class name for doc-maker| Default: DocMaker&gt;  *<li><b>facet.source</b>=&lt;class name for facet-source| Default: RandomFacetSource&gt;  *<li><b>query.maker</b>=&lt;class name for query-maker| Default: SimpleQueryMaker&gt;  *<li><b>log.queries</b>=&lt;whether queries should be printed| Default: false&gt;  *<li><b>directory</b>=&lt;type of directory to use for the index| Default: RAMDirectory&gt;  *<li><b>taxonomy.directory</b>=&lt;type of directory for taxonomy index| Default: RAMDirectory&gt;  *</ul>  */
end_comment

begin_class
DECL|class|PerfRunData
specifier|public
class|class
name|PerfRunData
implements|implements
name|Closeable
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
DECL|field|analyzerFactories
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyzerFactory
argument_list|>
name|analyzerFactories
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
DECL|field|contentSource
specifier|private
name|ContentSource
name|contentSource
decl_stmt|;
DECL|field|facetSource
specifier|private
name|FacetSource
name|facetSource
decl_stmt|;
DECL|field|locale
specifier|private
name|Locale
name|locale
decl_stmt|;
DECL|field|taxonomyDir
specifier|private
name|Directory
name|taxonomyDir
decl_stmt|;
DECL|field|taxonomyWriter
specifier|private
name|TaxonomyWriter
name|taxonomyWriter
decl_stmt|;
DECL|field|taxonomyReader
specifier|private
name|TaxonomyReader
name|taxonomyReader
decl_stmt|;
comment|// we use separate (identical) instances for each "read" task type, so each can iterate the quries separately.
DECL|field|readTaskQueryMaker
specifier|private
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|ReadTask
argument_list|>
argument_list|,
name|QueryMaker
argument_list|>
name|readTaskQueryMaker
decl_stmt|;
DECL|field|qmkrClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|QueryMaker
argument_list|>
name|qmkrClass
decl_stmt|;
DECL|field|indexReader
specifier|private
name|DirectoryReader
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
DECL|field|perfObjects
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|perfObjects
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|NewAnalyzerTask
operator|.
name|createAnalyzer
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
expr_stmt|;
comment|// content source
name|String
name|sourceClass
init|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.SingleDocSource"
argument_list|)
decl_stmt|;
name|contentSource
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|sourceClass
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|ContentSource
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|contentSource
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|// doc maker
name|docMaker
operator|=
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
literal|"org.apache.lucene.benchmark.byTask.feeds.DocMaker"
argument_list|)
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|DocMaker
operator|.
name|class
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
argument_list|,
name|contentSource
argument_list|)
expr_stmt|;
comment|// facet source
name|facetSource
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"facet.source"
argument_list|,
literal|"org.apache.lucene.benchmark.byTask.feeds.RandomFacetSource"
argument_list|)
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|FacetSource
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|facetSource
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
argument_list|<>
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
operator|.
name|asSubclass
argument_list|(
name|QueryMaker
operator|.
name|class
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
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
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
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|directory
argument_list|,
name|taxonomyWriter
argument_list|,
name|taxonomyReader
argument_list|,
name|taxonomyDir
argument_list|,
name|docMaker
argument_list|,
name|facetSource
argument_list|,
name|contentSource
argument_list|)
expr_stmt|;
comment|// close all perf objects that are closeable.
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
name|perfObjectsToClose
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|obj
range|:
name|perfObjects
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Closeable
condition|)
block|{
name|perfObjectsToClose
operator|.
name|add
argument_list|(
operator|(
name|Closeable
operator|)
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|perfObjectsToClose
argument_list|)
expr_stmt|;
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
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|directory
argument_list|)
expr_stmt|;
name|indexWriter
operator|=
literal|null
expr_stmt|;
name|indexReader
operator|=
literal|null
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxonomyWriter
argument_list|,
name|taxonomyReader
argument_list|,
name|taxonomyDir
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|=
literal|null
expr_stmt|;
name|taxonomyReader
operator|=
literal|null
expr_stmt|;
comment|// directory (default is ram-dir).
name|directory
operator|=
name|createDirectory
argument_list|(
name|eraseIndex
argument_list|,
literal|"index"
argument_list|,
literal|"directory"
argument_list|)
expr_stmt|;
name|taxonomyDir
operator|=
name|createDirectory
argument_list|(
name|eraseIndex
argument_list|,
literal|"taxo"
argument_list|,
literal|"taxonomy.directory"
argument_list|)
expr_stmt|;
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
DECL|method|createDirectory
specifier|private
name|Directory
name|createDirectory
parameter_list|(
name|boolean
name|eraseIndex
parameter_list|,
name|String
name|dirName
parameter_list|,
name|String
name|dirParam
parameter_list|)
throws|throws
name|IOException
block|{
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
name|dirParam
argument_list|,
literal|"RAMDirectory"
argument_list|)
argument_list|)
condition|)
block|{
name|Path
name|workDir
init|=
name|Paths
operator|.
name|get
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
name|Path
name|indexDir
init|=
name|workDir
operator|.
name|resolve
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
if|if
condition|(
name|eraseIndex
operator|&&
name|Files
operator|.
name|exists
argument_list|(
name|indexDir
argument_list|)
condition|)
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
block|}
name|Files
operator|.
name|createDirectories
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
return|;
block|}
return|return
operator|new
name|RAMDirectory
argument_list|()
return|;
block|}
comment|/** Returns an object that was previously set by {@link #setPerfObject(String, Object)}. */
DECL|method|getPerfObject
specifier|public
specifier|synchronized
name|Object
name|getPerfObject
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|perfObjects
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Sets an object that is required by {@link PerfTask}s, keyed by the given    * {@code key}. If the object implements {@link Closeable}, it will be closed    * by {@link #close()}.    */
DECL|method|setPerfObject
specifier|public
specifier|synchronized
name|void
name|setPerfObject
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|perfObjects
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|obj
argument_list|)
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
comment|/**    * @return Returns the taxonomy directory    */
DECL|method|getTaxonomyDir
specifier|public
name|Directory
name|getTaxonomyDir
parameter_list|()
block|{
return|return
name|taxonomyDir
return|;
block|}
comment|/**    * Set the taxonomy reader. Takes ownership of that taxonomy reader, that is,    * internally performs taxoReader.incRef() (If caller no longer needs that     * reader it should decRef()/close() it after calling this method, otherwise,     * the reader will remain open).     * @param taxoReader The taxonomy reader to set.    */
DECL|method|setTaxonomyReader
specifier|public
specifier|synchronized
name|void
name|setTaxonomyReader
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|taxoReader
operator|==
name|this
operator|.
name|taxonomyReader
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|taxonomyReader
operator|!=
literal|null
condition|)
block|{
name|taxonomyReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|taxoReader
operator|!=
literal|null
condition|)
block|{
name|taxoReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|taxonomyReader
operator|=
name|taxoReader
expr_stmt|;
block|}
comment|/**    * @return Returns the taxonomyReader.  NOTE: this returns a    * reference.  You must call TaxonomyReader.decRef() when    * you're done.    */
DECL|method|getTaxonomyReader
specifier|public
specifier|synchronized
name|TaxonomyReader
name|getTaxonomyReader
parameter_list|()
block|{
if|if
condition|(
name|taxonomyReader
operator|!=
literal|null
condition|)
block|{
name|taxonomyReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
return|return
name|taxonomyReader
return|;
block|}
comment|/**    * @param taxoWriter The taxonomy writer to set.    */
DECL|method|setTaxonomyWriter
specifier|public
name|void
name|setTaxonomyWriter
parameter_list|(
name|TaxonomyWriter
name|taxoWriter
parameter_list|)
block|{
name|this
operator|.
name|taxonomyWriter
operator|=
name|taxoWriter
expr_stmt|;
block|}
DECL|method|getTaxonomyWriter
specifier|public
name|TaxonomyWriter
name|getTaxonomyWriter
parameter_list|()
block|{
return|return
name|taxonomyWriter
return|;
block|}
comment|/**    * @return Returns the indexReader.  NOTE: this returns a    * reference.  You must call IndexReader.decRef() when    * you're done.    */
DECL|method|getIndexReader
specifier|public
specifier|synchronized
name|DirectoryReader
name|getIndexReader
parameter_list|()
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
return|return
name|indexReader
return|;
block|}
comment|/**    * @return Returns the indexSearcher.  NOTE: this returns    * a reference to the underlying IndexReader.  You must    * call IndexReader.decRef() when you're done.    */
DECL|method|getIndexSearcher
specifier|public
specifier|synchronized
name|IndexSearcher
name|getIndexSearcher
parameter_list|()
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
return|return
name|indexSearcher
return|;
block|}
comment|/**    * Set the index reader. Takes ownership of that index reader, that is,    * internally performs indexReader.incRef() (If caller no longer needs that     * reader it should decRef()/close() it after calling this method, otherwise,     * the reader will remain open).     * @param indexReader The indexReader to set.    */
DECL|method|setIndexReader
specifier|public
specifier|synchronized
name|void
name|setIndexReader
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexReader
operator|==
name|this
operator|.
name|indexReader
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|this
operator|.
name|indexReader
operator|!=
literal|null
condition|)
block|{
comment|// Release current IR
name|this
operator|.
name|indexReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
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
comment|// Hold reference to new IR
name|indexReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
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
comment|/**    * @return Returns the analyzer.    */
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
comment|/** Returns the ContentSource. */
DECL|method|getContentSource
specifier|public
name|ContentSource
name|getContentSource
parameter_list|()
block|{
return|return
name|contentSource
return|;
block|}
comment|/** Returns the DocMaker. */
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
comment|/** Returns the facet source. */
DECL|method|getFacetSource
specifier|public
name|FacetSource
name|getFacetSource
parameter_list|()
block|{
return|return
name|facetSource
return|;
block|}
comment|/**    * @return the locale    */
DECL|method|getLocale
specifier|public
name|Locale
name|getLocale
parameter_list|()
block|{
return|return
name|locale
return|;
block|}
comment|/**    * @param locale the locale to set    */
DECL|method|setLocale
specifier|public
name|void
name|setLocale
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
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
throws|throws
name|IOException
block|{
name|contentSource
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|docMaker
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|facetSource
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|QueryMaker
name|queryMaker
range|:
name|readTaskQueryMaker
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|queryMaker
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
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
argument_list|<
name|?
extends|extends
name|ReadTask
argument_list|>
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
DECL|method|getAnalyzerFactories
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|AnalyzerFactory
argument_list|>
name|getAnalyzerFactories
parameter_list|()
block|{
return|return
name|analyzerFactories
return|;
block|}
block|}
end_class

end_unit

