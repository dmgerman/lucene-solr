begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DateUtil
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
name|DateField
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
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The class responsible for handling Tika events and translating them into {@link org.apache.solr.common.SolrInputDocument}s.  *<B>This class is not thread-safe.</B>  *<p/>  *<p/>  * User's may wish to override this class to provide their own functionality.  *  * @see org.apache.solr.handler.extraction.SolrContentHandlerFactory  * @see org.apache.solr.handler.extraction.ExtractingRequestHandler  * @see org.apache.solr.handler.extraction.ExtractingDocumentLoader  */
end_comment

begin_class
DECL|class|SolrContentHandler
specifier|public
class|class
name|SolrContentHandler
extends|extends
name|DefaultHandler
implements|implements
name|ExtractingParams
block|{
DECL|field|log
specifier|private
specifier|transient
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrContentHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|document
specifier|private
name|SolrInputDocument
name|document
decl_stmt|;
DECL|field|dateFormats
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
init|=
name|DateUtil
operator|.
name|DEFAULT_DATE_FORMATS
decl_stmt|;
DECL|field|metadata
specifier|private
name|Metadata
name|metadata
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|catchAllBuilder
specifier|private
name|StringBuilder
name|catchAllBuilder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|2048
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|fieldBuilders
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|StringBuilder
argument_list|>
name|fieldBuilders
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|bldrStack
specifier|private
name|LinkedList
argument_list|<
name|StringBuilder
argument_list|>
name|bldrStack
init|=
operator|new
name|LinkedList
argument_list|<
name|StringBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|captureAttribs
specifier|private
name|boolean
name|captureAttribs
decl_stmt|;
DECL|field|lowerNames
specifier|private
name|boolean
name|lowerNames
decl_stmt|;
DECL|field|contentFieldName
specifier|private
name|String
name|contentFieldName
init|=
literal|"content"
decl_stmt|;
DECL|field|unknownFieldPrefix
specifier|private
name|String
name|unknownFieldPrefix
init|=
literal|""
decl_stmt|;
DECL|method|SolrContentHandler
specifier|public
name|SolrContentHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
argument_list|(
name|metadata
argument_list|,
name|params
argument_list|,
name|schema
argument_list|,
name|DateUtil
operator|.
name|DEFAULT_DATE_FORMATS
argument_list|)
expr_stmt|;
block|}
DECL|method|SolrContentHandler
specifier|public
name|SolrContentHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
parameter_list|)
block|{
name|document
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|dateFormats
operator|=
name|dateFormats
expr_stmt|;
name|this
operator|.
name|lowerNames
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|LOWERNAMES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|captureAttribs
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|CAPTURE_ATTRIBUTES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|unknownFieldPrefix
operator|=
name|params
operator|.
name|get
argument_list|(
name|UNKNOWN_FIELD_PREFIX
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
index|[]
name|captureFields
init|=
name|params
operator|.
name|getParams
argument_list|(
name|CAPTURE_ELEMENTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|captureFields
operator|!=
literal|null
operator|&&
name|captureFields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|fieldBuilders
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|StringBuilder
argument_list|>
argument_list|()
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
name|captureFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fieldBuilders
operator|.
name|put
argument_list|(
name|captureFields
index|[
name|i
index|]
argument_list|,
operator|new
name|StringBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|bldrStack
operator|.
name|add
argument_list|(
name|catchAllBuilder
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is called by a consumer when it is ready to deal with a new SolrInputDocument.  Overriding    * classes can use this hook to add in or change whatever they deem fit for the document at that time.    * The base implementation adds the metadata as fields, allowing for potential remapping.    *    * @return The {@link org.apache.solr.common.SolrInputDocument}.    */
DECL|method|newDocument
specifier|public
name|SolrInputDocument
name|newDocument
parameter_list|()
block|{
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|//handle the metadata extracted from the document
for|for
control|(
name|String
name|name
range|:
name|metadata
operator|.
name|names
argument_list|()
control|)
block|{
name|String
index|[]
name|vals
init|=
name|metadata
operator|.
name|getValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|addField
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
comment|//handle the literals from the params
name|Iterator
argument_list|<
name|String
argument_list|>
name|paramNames
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|paramNames
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|pname
init|=
name|paramNames
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|pname
operator|.
name|startsWith
argument_list|(
name|LITERALS_PREFIX
argument_list|)
condition|)
continue|continue;
name|String
name|name
init|=
name|pname
operator|.
name|substring
argument_list|(
name|LITERALS_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|addField
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|pname
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//add in the content
name|addField
argument_list|(
name|contentFieldName
argument_list|,
name|catchAllBuilder
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//add in the captured content
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|StringBuilder
argument_list|>
name|entry
range|:
name|fieldBuilders
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|addField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Doc: "
operator|+
name|document
argument_list|)
expr_stmt|;
block|}
return|return
name|document
return|;
block|}
comment|// Naming rules:
comment|// 1) optionally map names to nicenames (lowercase+underscores)
comment|// 2) execute "map" commands
comment|// 3) if resulting field is unknown, map it to a common prefix
DECL|method|addField
specifier|private
name|void
name|addField
parameter_list|(
name|String
name|fname
parameter_list|,
name|String
name|fval
parameter_list|,
name|String
index|[]
name|vals
parameter_list|)
block|{
if|if
condition|(
name|lowerNames
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|fname
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|fname
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|ch
argument_list|)
condition|)
name|ch
operator|=
literal|'_'
expr_stmt|;
else|else
name|ch
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
name|fname
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|name
init|=
name|findMappedName
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|==
literal|null
operator|&&
name|unknownFieldPrefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|name
operator|=
name|unknownFieldPrefix
operator|+
name|name
expr_stmt|;
name|sf
operator|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|// Arguably we should handle this as a special case. Why? Because unlike basically
comment|// all the other fields in metadata, this one was probably set not by Tika by in
comment|// ExtractingDocumentLoader.load(). You shouldn't have to define a mapping for this
comment|// field just because you specified a resource.name parameter to the handler, should
comment|// you?
if|if
condition|(
name|sf
operator|==
literal|null
operator|&&
name|unknownFieldPrefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|name
operator|==
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
condition|)
block|{
return|return;
block|}
comment|// normalize val params so vals.length>1
if|if
condition|(
name|vals
operator|!=
literal|null
operator|&&
name|vals
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|fval
operator|=
name|vals
index|[
literal|0
index|]
expr_stmt|;
name|vals
operator|=
literal|null
expr_stmt|;
block|}
comment|// single valued field with multiple values... catenate them.
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
operator|!
name|sf
operator|.
name|multiValued
argument_list|()
operator|&&
name|vals
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|String
name|val
range|:
name|vals
control|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|fval
operator|=
name|builder
operator|.
name|toString
argument_list|()
expr_stmt|;
name|vals
operator|=
literal|null
expr_stmt|;
block|}
name|float
name|boost
init|=
name|getBoost
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fval
operator|!=
literal|null
condition|)
block|{
name|document
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|transformValue
argument_list|(
name|fval
argument_list|,
name|sf
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|val
range|:
name|vals
control|)
block|{
name|document
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|transformValue
argument_list|(
name|val
argument_list|,
name|sf
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no value set - throw exception for debugging
comment|// if (vals==null&& fval==null) throw new RuntimeException(name + " has no non-null value ");
block|}
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|document
operator|.
name|clear
argument_list|()
expr_stmt|;
name|catchAllBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|StringBuilder
name|builder
range|:
name|fieldBuilders
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|bldrStack
operator|.
name|clear
argument_list|()
expr_stmt|;
name|bldrStack
operator|.
name|add
argument_list|(
name|catchAllBuilder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
name|StringBuilder
name|theBldr
init|=
name|fieldBuilders
operator|.
name|get
argument_list|(
name|localName
argument_list|)
decl_stmt|;
if|if
condition|(
name|theBldr
operator|!=
literal|null
condition|)
block|{
comment|//we need to switch the currentBuilder
name|bldrStack
operator|.
name|add
argument_list|(
name|theBldr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|captureAttribs
operator|==
literal|true
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|addField
argument_list|(
name|localName
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|bldrStack
operator|.
name|getLast
argument_list|()
operator|.
name|append
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
name|bldrStack
operator|.
name|getLast
argument_list|()
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|StringBuilder
name|theBldr
init|=
name|fieldBuilders
operator|.
name|get
argument_list|(
name|localName
argument_list|)
decl_stmt|;
if|if
condition|(
name|theBldr
operator|!=
literal|null
condition|)
block|{
comment|//pop the stack
name|bldrStack
operator|.
name|removeLast
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|bldrStack
operator|.
name|size
argument_list|()
operator|>=
literal|1
operator|)
assert|;
block|}
name|bldrStack
operator|.
name|getLast
argument_list|()
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|characters
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|bldrStack
operator|.
name|getLast
argument_list|()
operator|.
name|append
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Can be used to transform input values based on their {@link org.apache.solr.schema.SchemaField}    *<p/>    * This implementation only formats dates using the {@link org.apache.solr.common.util.DateUtil}.    *    * @param val    The value to transform    * @param schFld The {@link org.apache.solr.schema.SchemaField}    * @return The potentially new value.    */
DECL|method|transformValue
specifier|protected
name|String
name|transformValue
parameter_list|(
name|String
name|val
parameter_list|,
name|SchemaField
name|schFld
parameter_list|)
block|{
name|String
name|result
init|=
name|val
decl_stmt|;
if|if
condition|(
name|schFld
operator|!=
literal|null
operator|&&
name|schFld
operator|.
name|getType
argument_list|()
operator|instanceof
name|DateField
condition|)
block|{
comment|//try to transform the date
try|try
block|{
name|Date
name|date
init|=
name|DateUtil
operator|.
name|parseDate
argument_list|(
name|val
argument_list|,
name|dateFormats
argument_list|)
decl_stmt|;
name|DateFormat
name|df
init|=
name|DateUtil
operator|.
name|getThreadLocalDateFormat
argument_list|()
decl_stmt|;
name|result
operator|=
name|df
operator|.
name|format
argument_list|(
name|date
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Let the specific fieldType handle errors
comment|// throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Invalid value: " + val + " for field: " + schFld, e);
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Get the value of any boost factor for the mapped name.    *    * @param name The name of the field to see if there is a boost specified    * @return The boost value    */
DECL|method|getBoost
specifier|protected
name|float
name|getBoost
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|params
operator|.
name|getFloat
argument_list|(
name|BOOST_PREFIX
operator|+
name|name
argument_list|,
literal|1.0f
argument_list|)
return|;
block|}
comment|/**    * Get the name mapping    *    * @param name The name to check to see if there is a mapping    * @return The new name, if there is one, else<code>name</code>    */
DECL|method|findMappedName
specifier|protected
name|String
name|findMappedName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|MAP_PREFIX
operator|+
name|name
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

