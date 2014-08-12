begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|LinkedHashMap
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
name|ArrayUtil
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
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
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
name|search
operator|.
name|ReturnFields
import|;
end_import

begin_comment
comment|/**  * A description of the PHP serialization format can be found here:  * http://www.hurring.com/scott/code/perl/serialize/  */
end_comment

begin_class
DECL|class|PHPSerializedResponseWriter
specifier|public
class|class
name|PHPSerializedResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|field|CONTENT_TYPE_PHP_UTF8
specifier|static
name|String
name|CONTENT_TYPE_PHP_UTF8
init|=
literal|"text/x-php-serialized;charset=UTF-8"
decl_stmt|;
DECL|field|contentType
specifier|private
name|String
name|contentType
init|=
name|CONTENT_TYPE_PHP_UTF8
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|namedList
parameter_list|)
block|{
name|String
name|contentType
init|=
operator|(
name|String
operator|)
name|namedList
operator|.
name|get
argument_list|(
literal|"content-type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
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
name|PHPSerializedWriter
name|w
init|=
operator|new
name|PHPSerializedWriter
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
try|try
block|{
name|w
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|contentType
return|;
block|}
block|}
end_class

begin_class
DECL|class|PHPSerializedWriter
class|class
name|PHPSerializedWriter
extends|extends
name|JSONWriter
block|{
DECL|field|utf8
name|byte
index|[]
name|utf8
decl_stmt|;
DECL|method|PHPSerializedWriter
specifier|public
name|PHPSerializedWriter
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
name|this
operator|.
name|utf8
operator|=
name|BytesRef
operator|.
name|EMPTY_BYTES
expr_stmt|;
comment|// never indent serialized PHP data
name|doIndent
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeResponse
specifier|public
name|void
name|writeResponse
parameter_list|()
throws|throws
name|IOException
block|{
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
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|remove
argument_list|(
literal|"responseHeader"
argument_list|)
expr_stmt|;
name|writeNamedList
argument_list|(
literal|null
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|writeNamedListAsMapMangled
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
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
name|writeMapOpener
argument_list|(
operator|(
name|maxScore
operator|==
literal|null
operator|)
condition|?
literal|3
else|:
literal|4
argument_list|)
expr_stmt|;
name|writeKey
argument_list|(
literal|"numFound"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
literal|null
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
name|writeKey
argument_list|(
literal|"start"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
literal|null
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxScore
operator|!=
literal|null
condition|)
block|{
name|writeKey
argument_list|(
literal|"maxScore"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeFloat
argument_list|(
literal|null
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
block|}
name|writeKey
argument_list|(
literal|"docs"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeArrayOpener
argument_list|(
name|size
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
name|writeArrayCloser
argument_list|()
expr_stmt|;
comment|// doc list
name|writeMapCloser
argument_list|()
expr_stmt|;
block|}
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
name|writeKey
argument_list|(
name|idx
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|single
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|multi
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
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
name|val
operator|instanceof
name|Collection
condition|)
block|{
name|multi
operator|.
name|put
argument_list|(
name|fname
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|single
operator|.
name|put
argument_list|(
name|fname
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|writeMapOpener
argument_list|(
name|single
operator|.
name|size
argument_list|()
operator|+
name|multi
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|fname
range|:
name|single
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|single
operator|.
name|get
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|writeKey
argument_list|(
name|fname
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeVal
argument_list|(
name|fname
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|fname
range|:
name|multi
operator|.
name|keySet
argument_list|()
control|)
block|{
name|writeKey
argument_list|(
name|fname
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Object
name|val
init|=
name|multi
operator|.
name|get
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|val
operator|instanceof
name|Collection
operator|)
condition|)
block|{
comment|// should never be reached if multivalued fields are stored as a Collection
comment|// so I'm assuming a size of 1 just to wrap the single value
name|writeArrayOpener
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writeVal
argument_list|(
name|fname
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|writeArrayCloser
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|writeVal
argument_list|(
name|fname
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|writeMapCloser
argument_list|()
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
name|Object
index|[]
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeMapOpener
argument_list|(
name|val
operator|.
name|length
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
name|val
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeKey
argument_list|(
name|i
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writeVal
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|val
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|writeMapCloser
argument_list|()
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
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
name|vals
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|val
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|vals
operator|.
name|add
argument_list|(
name|val
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeArray
argument_list|(
name|name
argument_list|,
name|vals
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapOpener
specifier|public
name|void
name|writeMapOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
comment|// negative size value indicates that something has gone wrong
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Map size must not be negative"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"a:"
operator|+
name|size
operator|+
literal|":{"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapSeparator
specifier|public
name|void
name|writeMapSeparator
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|writeMapCloser
specifier|public
name|void
name|writeMapCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayOpener
specifier|public
name|void
name|writeArrayOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
comment|// negative size value indicates that something has gone wrong
if|if
condition|(
name|size
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Array size must not be negative"
argument_list|)
throw|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"a:"
operator|+
name|size
operator|+
literal|":{"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArraySeparator
specifier|public
name|void
name|writeArraySeparator
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* NOOP */
block|}
annotation|@
name|Override
DECL|method|writeArrayCloser
specifier|public
name|void
name|writeArrayCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
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
name|writer
operator|.
name|write
argument_list|(
literal|"N;"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeKey
specifier|protected
name|void
name|writeKey
parameter_list|(
name|String
name|fname
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|writeStr
argument_list|(
literal|null
argument_list|,
name|fname
argument_list|,
name|needsEscaping
argument_list|)
expr_stmt|;
block|}
DECL|method|writeKey
name|void
name|writeKey
parameter_list|(
name|int
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
literal|null
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
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
name|boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
name|val
condition|?
literal|"b:1;"
else|:
literal|"b:0;"
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
name|writeBool
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'t'
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
name|writer
operator|.
name|write
argument_list|(
literal|"i:"
operator|+
name|val
operator|+
literal|";"
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
name|writeInt
argument_list|(
name|name
argument_list|,
name|val
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
name|writeDouble
argument_list|(
name|name
argument_list|,
name|val
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
name|writer
operator|.
name|write
argument_list|(
literal|"d:"
operator|+
name|val
operator|+
literal|";"
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
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
comment|// serialized PHP strings don't need to be escaped at all, however the
comment|// string size reported needs be the number of bytes rather than chars.
name|utf8
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|utf8
argument_list|,
name|val
operator|.
name|length
argument_list|()
operator|*
name|UnicodeUtil
operator|.
name|MAX_UTF8_BYTES_PER_CHAR
argument_list|)
expr_stmt|;
specifier|final
name|int
name|nBytes
init|=
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|,
name|utf8
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"s:"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|nBytes
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|":\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\";"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

