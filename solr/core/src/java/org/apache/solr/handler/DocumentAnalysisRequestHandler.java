begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|DocumentAnalysisRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|AnalysisParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ContentStreamBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|XMLErrorLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|EmptyEntityResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An analysis handler that provides a breakdown of the analysis process of provided documents. This handler expects a  * (single) content stream of the following format:  *<p/>  *<pre><code>  *&lt;docs&gt;  *&lt;doc&gt;  *&lt;field name="id"&gt;1&lt;/field&gt;  *&lt;field name="name"&gt;The Name&lt;/field&gt;  *&lt;field name="text"&gt;The Text Value&lt;/field&gt;  *&lt;doc&gt;  *&lt;doc&gt;...&lt;/doc&gt;  *&lt;doc&gt;...&lt;/doc&gt;  *      ...  *&lt;/docs&gt;  *</code></pre>  *<p/>  *<em><b>Note: Each document must contain a field which serves as the unique key. This key is used in the returned  * response to associate an analysis breakdown to the analyzed document.</b></em>  *<p/>  *<p/>  *<p/>  * Like the {@link org.apache.solr.handler.FieldAnalysisRequestHandler}, this handler also supports query analysis by  * sending either an "analysis.query" or "q" request parameter that holds the query text to be analyzed. It also  * supports the "analysis.showmatch" parameter which when set to {@code true}, all field tokens that match the query  * tokens will be marked as a "match".  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|DocumentAnalysisRequestHandler
specifier|public
class|class
name|DocumentAnalysisRequestHandler
extends|extends
name|AnalysisRequestHandlerBase
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DocumentAnalysisRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|xmllog
specifier|private
specifier|static
specifier|final
name|XMLErrorLogger
name|xmllog
init|=
operator|new
name|XMLErrorLogger
argument_list|(
name|log
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_BOOST
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_BOOST
init|=
literal|1.0f
decl_stmt|;
DECL|field|inputFactory
specifier|private
name|XMLInputFactory
name|inputFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inputFactory
operator|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|EmptyEntityResolver
operator|.
name|configureXMLInputFactory
argument_list|(
name|inputFactory
argument_list|)
expr_stmt|;
name|inputFactory
operator|.
name|setXMLReporter
argument_list|(
name|xmllog
argument_list|)
expr_stmt|;
try|try
block|{
comment|// The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
comment|// XMLInputFactory, as that implementation tries to cache and reuse the
comment|// XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
comment|// prevents this.
comment|// All other known open-source stax parsers (and the bea ref impl)
comment|// have thread-safe factories.
name|inputFactory
operator|.
name|setProperty
argument_list|(
literal|"reuse-instance"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// Other implementations will likely throw this exception since "reuse-instance"
comment|// isimplementation specific.
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to set the 'reuse-instance' property for the input factory: "
operator|+
name|inputFactory
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|doAnalysis
specifier|protected
name|NamedList
name|doAnalysis
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentAnalysisRequest
name|analysisRequest
init|=
name|resolveAnalysisRequest
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|handleAnalysisRequest
argument_list|(
name|analysisRequest
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Provides a breakdown of the analysis process of provided documents"
return|;
block|}
comment|//================================================ Helper Methods ==================================================
comment|/**    * Resolves the {@link DocumentAnalysisRequest} from the given solr request.    *    * @param req The solr request.    *    * @return The resolved document analysis request.    *    * @throws IOException        Thrown when reading/parsing the content stream of the request fails.    * @throws XMLStreamException Thrown when reading/parsing the content stream of the request fails.    */
DECL|method|resolveAnalysisRequest
name|DocumentAnalysisRequest
name|resolveAnalysisRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|IOException
throws|,
name|XMLStreamException
block|{
name|DocumentAnalysisRequest
name|request
init|=
operator|new
name|DocumentAnalysisRequest
argument_list|()
decl_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|query
init|=
name|params
operator|.
name|get
argument_list|(
name|AnalysisParams
operator|.
name|QUERY
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|request
operator|.
name|setQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|boolean
name|showMatch
init|=
name|params
operator|.
name|getBool
argument_list|(
name|AnalysisParams
operator|.
name|SHOW_MATCH
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|request
operator|.
name|setShowMatch
argument_list|(
name|showMatch
argument_list|)
expr_stmt|;
name|ContentStream
name|stream
init|=
name|extractSingleContentStream
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|XMLStreamReader
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|stream
operator|.
name|getStream
argument_list|()
expr_stmt|;
specifier|final
name|String
name|charset
init|=
name|ContentStreamBase
operator|.
name|getCharsetFromContentType
argument_list|(
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|(
name|charset
operator|==
literal|null
operator|)
condition|?
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|is
argument_list|)
else|:
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
name|is
argument_list|,
name|charset
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|XMLStreamConstants
operator|.
name|END_DOCUMENT
case|:
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|request
return|;
block|}
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
block|{
name|String
name|currTag
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|currTag
argument_list|)
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Reading doc..."
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|document
init|=
name|readDocument
argument_list|(
name|parser
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
name|request
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handles the resolved {@link DocumentAnalysisRequest} and returns the analysis response as a named list.    *    * @param request The {@link DocumentAnalysisRequest} to be handled.    * @param schema  The index schema.    *    * @return The analysis response as a named list.    */
DECL|method|handleAnalysisRequest
name|NamedList
argument_list|<
name|Object
argument_list|>
name|handleAnalysisRequest
parameter_list|(
name|DocumentAnalysisRequest
name|request
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|SchemaField
name|uniqueKeyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrInputDocument
name|document
range|:
name|request
operator|.
name|getDocuments
argument_list|()
control|)
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|theTokens
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|document
operator|.
name|getFieldValue
argument_list|(
name|uniqueKeyField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|theTokens
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|document
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
comment|// there's no point of providing analysis to unindexed fields.
name|SchemaField
name|field
init|=
name|schema
operator|.
name|getField
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|fieldTokens
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|theTokens
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|fieldTokens
argument_list|)
expr_stmt|;
name|FieldType
name|fieldType
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|String
name|queryValue
init|=
name|request
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termsToMatch
decl_stmt|;
try|try
block|{
name|termsToMatch
operator|=
operator|(
name|queryValue
operator|!=
literal|null
operator|&&
name|request
operator|.
name|isShowMatch
argument_list|()
operator|)
condition|?
name|getQueryTokenSet
argument_list|(
name|queryValue
argument_list|,
name|fieldType
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
else|:
name|EMPTY_BYTES_SET
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore analysis exceptions since we are applying arbitrary text to all fields
name|termsToMatch
operator|=
name|EMPTY_BYTES_SET
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|AnalysisContext
name|analysisContext
init|=
operator|new
name|AnalysisContext
argument_list|(
name|fieldType
argument_list|,
name|fieldType
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|,
name|EMPTY_BYTES_SET
argument_list|)
decl_stmt|;
name|fieldTokens
operator|.
name|add
argument_list|(
literal|"query"
argument_list|,
name|analyzeValue
argument_list|(
name|request
operator|.
name|getQuery
argument_list|()
argument_list|,
name|analysisContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore analysis exceptions since we are applying arbitrary text to all fields
block|}
block|}
name|Analyzer
name|analyzer
init|=
name|fieldType
operator|.
name|getIndexAnalyzer
argument_list|()
decl_stmt|;
name|AnalysisContext
name|analysisContext
init|=
operator|new
name|AnalysisContext
argument_list|(
name|fieldType
argument_list|,
name|analyzer
argument_list|,
name|termsToMatch
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|fieldValues
init|=
name|document
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|indexTokens
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|fieldValue
range|:
name|fieldValues
control|)
block|{
name|indexTokens
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|fieldValue
argument_list|)
argument_list|,
name|analyzeValue
argument_list|(
name|fieldValue
operator|.
name|toString
argument_list|()
argument_list|,
name|analysisContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldTokens
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|indexTokens
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Reads the document from the given xml stream reader. The following document format is expected:    *<p/>    *<pre><code>    *&lt;doc&gt;    *&lt;field name="id"&gt;1&lt;/field&gt;    *&lt;field name="name"&gt;The Name&lt;/field&gt;    *&lt;field name="text"&gt;The Text Value&lt;/field&gt;    *&lt;/doc&gt;    *</code></pre>    *<p/>    *<p/>    *<em>NOTE: each read document is expected to have at least one field which serves as the unique key.</em>    *    * @param reader The {@link XMLStreamReader} from which the document will be read.    * @param schema The index schema. The schema is used to validate that the read document has a unique key field.    *    * @return The read document.    *    * @throws XMLStreamException When reading of the document fails.    */
DECL|method|readDocument
name|SolrInputDocument
name|readDocument
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|XMLStreamException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|uniqueKeyField
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|boolean
name|hasId
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|event
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
comment|// Add everything to the text
case|case
name|XMLStreamConstants
operator|.
name|SPACE
case|:
case|case
name|XMLStreamConstants
operator|.
name|CDATA
case|:
case|case
name|XMLStreamConstants
operator|.
name|CHARACTERS
case|:
name|text
operator|.
name|append
argument_list|(
name|reader
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|XMLStreamConstants
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|hasId
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"All documents must contain a unique key value: '"
operator|+
name|doc
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|doc
return|;
block|}
elseif|else
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|text
operator|.
name|toString
argument_list|()
argument_list|,
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
if|if
condition|(
name|uniqueKeyField
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|hasId
operator|=
literal|true
expr_stmt|;
block|}
block|}
break|break;
case|case
name|XMLStreamConstants
operator|.
name|START_ELEMENT
case|:
name|text
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|localName
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"field"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"unexpected XML tag doc/"
operator|+
name|localName
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reader
operator|.
name|getAttributeCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|attrName
init|=
name|reader
operator|.
name|getAttributeLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
block|{
name|fieldName
operator|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
block|}
comment|/**    * Extracts the only content stream from the request. {@link org.apache.solr.common.SolrException.ErrorCode#BAD_REQUEST}    * error is thrown if the request doesn't hold any content stream or holds more than one.    *    * @param req The solr request.    *    * @return The single content stream which holds the documents to be analyzed.    */
DECL|method|extractSingleContentStream
specifier|private
name|ContentStream
name|extractSingleContentStream
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
name|req
operator|.
name|getContentStreams
argument_list|()
decl_stmt|;
name|String
name|exceptionMsg
init|=
literal|"DocumentAnalysisRequestHandler expects a single content stream with documents to analyze"
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|exceptionMsg
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|ContentStream
argument_list|>
name|iter
init|=
name|streams
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|exceptionMsg
argument_list|)
throw|;
block|}
name|ContentStream
name|stream
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|exceptionMsg
argument_list|)
throw|;
block|}
return|return
name|stream
return|;
block|}
block|}
end_class

end_unit

