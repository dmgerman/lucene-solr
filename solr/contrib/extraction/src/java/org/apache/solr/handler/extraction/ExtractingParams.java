begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package

begin_comment
comment|/**  * The various Solr Parameters names to use when extracting content.  *  **/
end_comment

begin_interface
DECL|interface|ExtractingParams
specifier|public
interface|interface
name|ExtractingParams
block|{
comment|/**    * Map all generated attribute names to field names with lowercase and underscores.    */
DECL|field|LOWERNAMES
specifier|public
specifier|static
specifier|final
name|String
name|LOWERNAMES
init|=
literal|"lowernames"
decl_stmt|;
comment|/**    * if true, ignore TikaException (give up to extract text but index meta data)    */
DECL|field|IGNORE_TIKA_EXCEPTION
specifier|public
specifier|static
specifier|final
name|String
name|IGNORE_TIKA_EXCEPTION
init|=
literal|"ignoreTikaException"
decl_stmt|;
comment|/**    * The param prefix for mapping Tika metadata to Solr fields.    *<p>    * To map a field, add a name like:    *<pre>fmap.title=solr.title</pre>    *    * In this example, the tika "title" metadata value will be added to a Solr field named "solr.title"    *    *    */
DECL|field|MAP_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MAP_PREFIX
init|=
literal|"fmap."
decl_stmt|;
comment|/**    * The boost value for the name of the field.  The boost can be specified by a name mapping.    *<p>    * For example    *<pre>    * map.title=solr.title    * boost.solr.title=2.5    *</pre>    * will boost the solr.title field for this document by 2.5    *    */
DECL|field|BOOST_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BOOST_PREFIX
init|=
literal|"boost."
decl_stmt|;
comment|/**    * Pass in literal values to be added to the document, as in    *<pre>    *  literal.myField=Foo     *</pre>    *    */
DECL|field|LITERALS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|LITERALS_PREFIX
init|=
literal|"literal."
decl_stmt|;
comment|/**    * Restrict the extracted parts of a document to be indexed    *  by passing in an XPath expression.  All content that satisfies the XPath expr.    * will be passed to the {@link SolrContentHandler}.    *<p>    * See Tika's docs for what the extracted document looks like.    * @see #CAPTURE_ELEMENTS    */
DECL|field|XPATH_EXPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|XPATH_EXPRESSION
init|=
literal|"xpath"
decl_stmt|;
comment|/**    * Only extract and return the content, do not index it.    */
DECL|field|EXTRACT_ONLY
specifier|public
specifier|static
specifier|final
name|String
name|EXTRACT_ONLY
init|=
literal|"extractOnly"
decl_stmt|;
comment|/**    * Content output format if extractOnly is true. Default is "xml", alternative is "text".    */
DECL|field|EXTRACT_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|EXTRACT_FORMAT
init|=
literal|"extractFormat"
decl_stmt|;
comment|/**    * Capture attributes separately according to the name of the element, instead of just adding them to the string buffer    */
DECL|field|CAPTURE_ATTRIBUTES
specifier|public
specifier|static
specifier|final
name|String
name|CAPTURE_ATTRIBUTES
init|=
literal|"captureAttr"
decl_stmt|;
comment|/**    * Literal field values will by default override other values such as metadata and content. Set this to false to revert to pre-4.0 behaviour    */
DECL|field|LITERALS_OVERRIDE
specifier|public
specifier|static
specifier|final
name|String
name|LITERALS_OVERRIDE
init|=
literal|"literalsOverride"
decl_stmt|;
comment|/**    * Capture the specified fields (and everything included below it that isn't capture by some other capture field) separately from the default.  This is different    * then the case of passing in an XPath expression.    *<p>    * The Capture field is based on the localName returned to the {@link SolrContentHandler}    * by Tika, not to be confused by the mapped field.  The field name can then    * be mapped into the index schema.    *<p>    * For instance, a Tika document may look like:    *<pre>    *&lt;html&gt;    *    ...    *&lt;body&gt;    *&lt;p&gt;some text here.&lt;div&gt;more text&lt;/div&gt;&lt;/p&gt;    *      Some more text    *&lt;/body&gt;    *</pre>    * By passing in the p tag, you could capture all P tags separately from the rest of the t    * Thus, in the example, the capture of the P tag would be: "some text here.  more text"    *    */
DECL|field|CAPTURE_ELEMENTS
specifier|public
specifier|static
specifier|final
name|String
name|CAPTURE_ELEMENTS
init|=
literal|"capture"
decl_stmt|;
comment|/**    * The type of the stream.  If not specified, Tika will use mime type detection.    */
DECL|field|STREAM_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|STREAM_TYPE
init|=
literal|"stream.type"
decl_stmt|;
comment|/**    * Optional.  The file name. If specified, Tika can take this into account while    * guessing the MIME type.    */
DECL|field|RESOURCE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_NAME
init|=
literal|"resource.name"
decl_stmt|;
comment|/**    * Optional. The password for this resource. Will be used instead of the rule based password lookup mechanisms     */
DECL|field|RESOURCE_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_PASSWORD
init|=
literal|"resource.password"
decl_stmt|;
comment|/**    * Optional.  If specified, the prefix will be prepended to all Metadata, such that it would be possible    * to setup a dynamic field to automatically capture it    */
DECL|field|UNKNOWN_FIELD_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|UNKNOWN_FIELD_PREFIX
init|=
literal|"uprefix"
decl_stmt|;
comment|/**    * Optional.  If specified and the name of a potential field cannot be determined, the default Field specified    * will be used instead.    */
DECL|field|DEFAULT_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FIELD
init|=
literal|"defaultField"
decl_stmt|;
comment|/**    * Optional. If specified, loads the file as a source for password lookups for Tika encrypted documents.    *<p>    * File format is Java properties format with one key=value per line.    * The key is evaluated as a regex against the file name, and the value is the password    * The rules are evaluated top-bottom, i.e. the first match will be used    * If you want a fallback password to be always used, supply a .*=&lt;defaultmypassword&gt; at the end      */
DECL|field|PASSWORD_MAP_FILE
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD_MAP_FILE
init|=
literal|"passwordsFile"
decl_stmt|;
block|}
end_interface

end_unit

