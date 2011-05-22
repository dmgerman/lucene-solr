begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|SolrDocument
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
name|XML
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
name|search
operator|.
name|ReturnFields
import|;
end_import

begin_comment
comment|/**  * @lucene.internal  */
end_comment

begin_class
DECL|class|XMLWriter
specifier|public
class|class
name|XMLWriter
extends|extends
name|TextResponseWriter
block|{
DECL|field|CURRENT_VERSION
specifier|public
specifier|static
name|float
name|CURRENT_VERSION
init|=
literal|2.2f
decl_stmt|;
DECL|field|XML_START1
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|XML_START1
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|XML_STYLESHEET
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|XML_STYLESHEET
init|=
literal|"<?xml-stylesheet type=\"text/xsl\" href=\""
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|XML_STYLESHEET_END
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|XML_STYLESHEET_END
init|=
literal|"\"?>\n"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|/***   private static final char[] XML_START2_SCHEMA=(   "<response xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"   +" xsi:noNamespaceSchemaLocation=\"http://pi.cnet.com/cnet-search/response.xsd\">\n"           ).toCharArray();   ***/
DECL|field|XML_START2_NOSCHEMA
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|XML_START2_NOSCHEMA
init|=
operator|(
literal|"<response>\n"
operator|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|version
specifier|final
name|int
name|version
decl_stmt|;
DECL|method|writeResponse
specifier|public
specifier|static
name|void
name|writeResponse
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|XMLWriter
name|xmlWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|xmlWriter
operator|=
operator|new
name|XMLWriter
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|xmlWriter
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|xmlWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|XMLWriter
specifier|public
name|XMLWriter
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|String
name|version
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|)
decl_stmt|;
name|float
name|ver
init|=
name|version
operator|==
literal|null
condition|?
name|CURRENT_VERSION
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|this
operator|.
name|version
operator|=
call|(
name|int
call|)
argument_list|(
name|ver
operator|*
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|version
operator|<
literal|2200
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
literal|"XMLWriter does not support version: "
operator|+
name|version
argument_list|)
throw|;
block|}
block|}
DECL|method|writeResponse
specifier|public
name|void
name|writeResponse
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
name|XML_START1
argument_list|)
expr_stmt|;
name|String
name|stylesheet
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"stylesheet"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stylesheet
operator|!=
literal|null
operator|&&
name|stylesheet
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|XML_STYLESHEET
argument_list|)
expr_stmt|;
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|stylesheet
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|XML_STYLESHEET_END
argument_list|)
expr_stmt|;
block|}
comment|/***     String noSchema = req.getParams().get("noSchema");     // todo - change when schema becomes available?     if (false&& noSchema == null)       writer.write(XML_START2_SCHEMA);     else       writer.write(XML_START2_NOSCHEMA);      ***/
name|writer
operator|.
name|write
argument_list|(
name|XML_START2_NOSCHEMA
argument_list|)
expr_stmt|;
comment|// dump response values
name|NamedList
argument_list|<
name|?
argument_list|>
name|lst
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|Boolean
name|omitHeader
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|OMIT_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|omitHeader
operator|!=
literal|null
operator|&&
name|omitHeader
condition|)
name|lst
operator|.
name|remove
argument_list|(
literal|"responseHeader"
argument_list|)
expr_stmt|;
name|int
name|sz
init|=
name|lst
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|writeVal
argument_list|(
name|lst
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|lst
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"\n</response>\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Writes the XML attribute name/val. A null val means that the attribute is missing. */
DECL|method|writeAttr
specifier|private
name|void
name|writeAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeAttr
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|writeAttr
specifier|public
name|void
name|writeAttr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|escape
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|escape
condition|)
block|{
name|XML
operator|.
name|escapeAttributeValue
argument_list|(
name|val
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|startTag
name|void
name|startTag
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|closeTag
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|writeAttr
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|closeTag
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|closeTag
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"/>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|writeStartDocumentList
specifier|public
name|void
name|writeStartDocumentList
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|start
parameter_list|,
name|int
name|size
parameter_list|,
name|long
name|numFound
parameter_list|,
name|Float
name|maxScore
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"<result"
argument_list|)
expr_stmt|;
name|writeAttr
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|writeAttr
argument_list|(
literal|"numFound"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|numFound
argument_list|)
argument_list|)
expr_stmt|;
name|writeAttr
argument_list|(
literal|"start"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxScore
operator|!=
literal|null
condition|)
block|{
name|writeAttr
argument_list|(
literal|"maxScore"
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|maxScore
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|incLevel
argument_list|()
expr_stmt|;
block|}
comment|/**    * The SolrDocument should already have multivalued fields implemented as    * Collections -- this will not rewrite to<arr>    */
annotation|@
name|Override
DECL|method|writeSolrDocument
specifier|public
name|void
name|writeSolrDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrDocument
name|doc
parameter_list|,
name|ReturnFields
name|returnFields
parameter_list|,
name|int
name|idx
parameter_list|)
throws|throws
name|IOException
block|{
name|startTag
argument_list|(
literal|"doc"
argument_list|,
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|incLevel
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|fname
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|returnFields
operator|.
name|wantsField
argument_list|(
name|fname
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Object
name|val
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"_explain_"
operator|.
name|equals
argument_list|(
name|fname
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|writeVal
argument_list|(
name|fname
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|decLevel
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"</doc>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeEndDocumentList
specifier|public
name|void
name|writeEndDocumentList
parameter_list|()
throws|throws
name|IOException
block|{
name|decLevel
argument_list|()
expr_stmt|;
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"</result>"
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Generic compound types
comment|//
annotation|@
name|Override
DECL|method|writeNamedList
specifier|public
name|void
name|writeNamedList
parameter_list|(
name|String
name|name
parameter_list|,
name|NamedList
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|sz
init|=
name|val
operator|.
name|size
argument_list|()
decl_stmt|;
name|startTag
argument_list|(
literal|"lst"
argument_list|,
name|name
argument_list|,
name|sz
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|incLevel
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|writeVal
argument_list|(
name|val
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|val
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|decLevel
argument_list|()
expr_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"</lst>"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeMap
specifier|public
name|void
name|writeMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|map
parameter_list|,
name|boolean
name|excludeOuter
parameter_list|,
name|boolean
name|isFirstVal
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|sz
init|=
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|excludeOuter
condition|)
block|{
name|startTag
argument_list|(
literal|"lst"
argument_list|,
name|name
argument_list|,
name|sz
operator|<=
literal|0
argument_list|)
expr_stmt|;
name|incLevel
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
operator|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
operator|)
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|k
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|v
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// if (sz<indentThreshold) indent();
name|writeVal
argument_list|(
literal|null
operator|==
name|k
condition|?
literal|null
else|:
name|k
operator|.
name|toString
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|excludeOuter
condition|)
block|{
name|decLevel
argument_list|()
expr_stmt|;
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"</lst>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|writeArray
specifier|public
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
index|[]
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|val
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArray
specifier|public
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|startTag
argument_list|(
literal|"arr"
argument_list|,
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|incLevel
argument_list|()
expr_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|writeVal
argument_list|(
literal|null
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|decLevel
argument_list|()
expr_stmt|;
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|()
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"</arr>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|startTag
argument_list|(
literal|"arr"
argument_list|,
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|//
comment|// Primitive types
comment|//
annotation|@
name|Override
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"null"
argument_list|,
name|name
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStr
specifier|public
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|escape
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"str"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
name|escape
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"int"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"long"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"bool"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"float"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeFloat
argument_list|(
name|name
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"double"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeDouble
argument_list|(
name|name
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeDate
specifier|public
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writePrim
argument_list|(
literal|"date"
argument_list|,
name|name
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// OPT - specific writeInt, writeFloat, methods might be faster since
comment|// there would be less write calls (write("<int name=\"" + name + ... +</int>)
comment|//
DECL|method|writePrim
specifier|private
name|void
name|writePrim
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|escape
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|contentLen
init|=
name|val
operator|==
literal|null
condition|?
literal|0
else|:
name|val
operator|.
name|length
argument_list|()
decl_stmt|;
name|startTag
argument_list|(
name|tag
argument_list|,
name|name
argument_list|,
name|contentLen
operator|==
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentLen
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|escape
condition|)
block|{
name|XML
operator|.
name|escapeCharData
argument_list|(
name|val
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
name|contentLen
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

