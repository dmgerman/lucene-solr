begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.xml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
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
name|MockAnalyzer
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
name|util
operator|.
name|Constants
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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

begin_comment
comment|/**  * This class illustrates how form input (such as from a web page or Swing gui) can be  * turned into Lucene queries using a choice of XSL templates for different styles of queries.  */
end_comment

begin_class
DECL|class|TestQueryTemplateManager
specifier|public
class|class
name|TestQueryTemplateManager
extends|extends
name|LuceneTestCase
block|{
DECL|field|builder
specifier|private
name|CoreParser
name|builder
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|//A collection of documents' field values for use in our tests
DECL|field|docFieldValues
name|String
name|docFieldValues
index|[]
init|=
block|{
literal|"artist=Jeff Buckley \talbum=Grace \treleaseDate=1999 \tgenre=rock"
block|,
literal|"artist=Fugazi \talbum=Repeater \treleaseDate=1990 \tgenre=alternative"
block|,
literal|"artist=Fugazi \talbum=Red Medicine \treleaseDate=1995 \tgenre=alternative"
block|,
literal|"artist=Peeping Tom \talbum=Peeping Tom \treleaseDate=2006 \tgenre=rock"
block|,
literal|"artist=Red Snapper \talbum=Prince Blimey \treleaseDate=1996 \tgenre=electronic"
block|}
decl_stmt|;
comment|//A collection of example queries, consisting of name/value pairs representing form content plus
comment|// a choice of query style template to use in the test, with expected number of hits
DECL|field|queryForms
name|String
name|queryForms
index|[]
init|=
block|{
literal|"artist=Fugazi \texpectedMatches=2 \ttemplate=albumBooleanQuery"
block|,
literal|"artist=Fugazi \treleaseDate=1990 \texpectedMatches=1 \ttemplate=albumBooleanQuery"
block|,
literal|"artist=Buckley \tgenre=rock \texpectedMatches=1 \ttemplate=albumFilteredQuery"
block|,
literal|"artist=Buckley \tgenre=electronic \texpectedMatches=0 \ttemplate=albumFilteredQuery"
block|,
literal|"queryString=artist:buckly~ NOT genre:electronic \texpectedMatches=1 \ttemplate=albumLuceneClassicQuery"
block|}
decl_stmt|;
DECL|method|testFormTransforms
specifier|public
name|void
name|testFormTransforms
parameter_list|()
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|TransformerException
throws|,
name|ParserException
block|{
name|assumeFalse
argument_list|(
literal|"test temporarily disabled on J9, see https://issues.apache.org/jira/browse/LUCENE-6556"
argument_list|,
name|Constants
operator|.
name|JAVA_VENDOR
operator|.
name|startsWith
argument_list|(
literal|"IBM"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Cache all the query templates we will be referring to.
name|QueryTemplateManager
name|qtm
init|=
operator|new
name|QueryTemplateManager
argument_list|()
decl_stmt|;
name|qtm
operator|.
name|addQueryTemplate
argument_list|(
literal|"albumBooleanQuery"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"albumBooleanQuery.xsl"
argument_list|)
argument_list|)
expr_stmt|;
name|qtm
operator|.
name|addQueryTemplate
argument_list|(
literal|"albumFilteredQuery"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"albumFilteredQuery.xsl"
argument_list|)
argument_list|)
expr_stmt|;
name|qtm
operator|.
name|addQueryTemplate
argument_list|(
literal|"albumLuceneClassicQuery"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"albumLuceneClassicQuery.xsl"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Run all of our test queries
for|for
control|(
name|String
name|queryForm
range|:
name|queryForms
control|)
block|{
name|Properties
name|queryFormProperties
init|=
name|getPropsFromString
argument_list|(
name|queryForm
argument_list|)
decl_stmt|;
comment|//Get the required query XSL template for this test
comment|//      Templates template=getTemplate(queryFormProperties.getProperty("template"));
comment|//Transform the queryFormProperties into a Lucene XML query
name|Document
name|doc
init|=
name|qtm
operator|.
name|getQueryAsDOM
argument_list|(
name|queryFormProperties
argument_list|,
name|queryFormProperties
operator|.
name|getProperty
argument_list|(
literal|"template"
argument_list|)
argument_list|)
decl_stmt|;
comment|//Parse the XML query using the XML parser
name|Query
name|q
init|=
name|builder
operator|.
name|getQuery
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
decl_stmt|;
comment|//Run the query
name|int
name|h
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
comment|//Check we have the expected number of results
name|int
name|expectedHits
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|queryFormProperties
operator|.
name|getProperty
argument_list|(
literal|"expectedMatches"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of results should match for query "
operator|+
name|queryForm
argument_list|,
name|expectedHits
argument_list|,
name|h
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Helper method to construct Lucene query forms used in our test
DECL|method|getPropsFromString
name|Properties
name|getPropsFromString
parameter_list|(
name|String
name|nameValuePairs
parameter_list|)
block|{
name|Properties
name|result
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|nameValuePairs
argument_list|,
literal|"\t="
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|value
init|=
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|result
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|//Helper method to construct Lucene documents used in our tests
DECL|method|getDocumentFromString
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|getDocumentFromString
parameter_list|(
name|String
name|nameValuePairs
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|result
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|nameValuePairs
argument_list|,
literal|"\t="
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|value
init|=
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*     * @see TestCase#setUp()     */
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
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
comment|//Create an index
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|docFieldValue
range|:
name|docFieldValues
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|getDocumentFromString
argument_list|(
name|docFieldValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|//initialize the parser
name|builder
operator|=
operator|new
name|CorePlusExtensionsParser
argument_list|(
literal|"artist"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

