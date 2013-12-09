begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.demo.xmlparser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|xmlparser
package|;
end_package

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
name|IOException
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
name|util
operator|.
name|Enumeration
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|RequestDispatcher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|document
operator|.
name|FieldType
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
name|TextField
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
name|index
operator|.
name|IndexWriterConfig
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
name|StoredDocument
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
name|queryparser
operator|.
name|xml
operator|.
name|CorePlusExtensionsParser
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
name|queryparser
operator|.
name|xml
operator|.
name|QueryTemplateManager
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
name|search
operator|.
name|ScoreDoc
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
name|TopDocs
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Example servlet that uses the XML queryparser.  *<p>  * NOTE: you must provide CSV data in<code>/WEB-INF/data.tsv</code>  * for the demo to work!  */
end_comment

begin_class
DECL|class|FormBasedXmlQueryDemo
specifier|public
class|class
name|FormBasedXmlQueryDemo
extends|extends
name|HttpServlet
block|{
DECL|field|queryTemplateManager
specifier|private
name|QueryTemplateManager
name|queryTemplateManager
decl_stmt|;
DECL|field|xmlParser
specifier|private
name|CorePlusExtensionsParser
name|xmlParser
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
comment|/** for instantiation by the servlet container */
DECL|method|FormBasedXmlQueryDemo
specifier|public
name|FormBasedXmlQueryDemo
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
try|try
block|{
name|openExampleIndex
argument_list|()
expr_stmt|;
comment|//load servlet configuration settings
name|String
name|xslFile
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"xslFile"
argument_list|)
decl_stmt|;
name|String
name|defaultStandardQueryParserField
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"defaultStandardQueryParserField"
argument_list|)
decl_stmt|;
comment|//Load and cache choice of XSL query template using QueryTemplateManager
name|queryTemplateManager
operator|=
operator|new
name|QueryTemplateManager
argument_list|(
name|getServletContext
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/WEB-INF/"
operator|+
name|xslFile
argument_list|)
argument_list|)
expr_stmt|;
comment|//initialize an XML Query Parser for use by all threads
name|xmlParser
operator|=
operator|new
name|CorePlusExtensionsParser
argument_list|(
name|defaultStandardQueryParserField
argument_list|,
name|analyzer
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
name|ServletException
argument_list|(
literal|"Error loading query template"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doPost
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|//Take all completed form fields and add to a Properties object
name|Properties
name|completedFormFields
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|pNames
init|=
name|request
operator|.
name|getParameterNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|pNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|propName
init|=
operator|(
name|String
operator|)
name|pNames
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|propName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|value
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|value
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|completedFormFields
operator|.
name|setProperty
argument_list|(
name|propName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
comment|//Create an XML query by populating template with given user criteria
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|xmlQuery
init|=
name|queryTemplateManager
operator|.
name|getQueryAsDOM
argument_list|(
name|completedFormFields
argument_list|)
decl_stmt|;
comment|//Parse the XML to produce a Lucene query
name|Query
name|query
init|=
name|xmlParser
operator|.
name|getQuery
argument_list|(
name|xmlQuery
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
comment|//Run the query
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|//and package the results and forward to JSP
if|if
condition|(
name|topDocs
operator|!=
literal|null
condition|)
block|{
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|StoredDocument
index|[]
name|results
init|=
operator|new
name|StoredDocument
index|[
name|sd
operator|.
name|length
index|]
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|results
index|[
name|i
index|]
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
literal|"results"
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
block|}
name|RequestDispatcher
name|dispatcher
init|=
name|getServletContext
argument_list|()
operator|.
name|getRequestDispatcher
argument_list|(
literal|"/index.jsp"
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|forward
argument_list|(
name|request
argument_list|,
name|response
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
name|ServletException
argument_list|(
literal|"Error processing query"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|openExampleIndex
specifier|private
name|void
name|openExampleIndex
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Create a RAM-based index from our test data file
name|RAMDirectory
name|rd
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd
argument_list|,
name|iwConfig
argument_list|)
decl_stmt|;
name|InputStream
name|dataIn
init|=
name|getServletContext
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/WEB-INF/data.tsv"
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|dataIn
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|textNoNorms
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|textNoNorms
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//parse row and create a document
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|,
literal|"\t"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"location"
argument_list|,
name|st
operator|.
name|nextToken
argument_list|()
argument_list|,
name|textNoNorms
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"salary"
argument_list|,
name|st
operator|.
name|nextToken
argument_list|()
argument_list|,
name|textNoNorms
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"type"
argument_list|,
name|st
operator|.
name|nextToken
argument_list|()
argument_list|,
name|textNoNorms
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"description"
argument_list|,
name|st
operator|.
name|nextToken
argument_list|()
argument_list|,
name|textNoNorms
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//open searcher
comment|// this example never closes it reader!
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd
argument_list|)
decl_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

