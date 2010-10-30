begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
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
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|HashMap
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
name|util
operator|.
name|ThreadInterruptedException
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
name|InputSource
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
name|XMLReader
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|XMLReaderFactory
import|;
end_import

begin_comment
comment|/**  * A {@link ContentSource} which reads the English Wikipedia dump. You can read  * the .bz2 file directly (it will be decompressed on the fly). Config  * properties:  *<ul>  *<li>keep.image.only.docs=false|true (default<b>true</b>).  *<li>docs.file=&lt;path to the file&gt;  *</ul>  */
end_comment

begin_class
DECL|class|EnwikiContentSource
specifier|public
class|class
name|EnwikiContentSource
extends|extends
name|ContentSource
block|{
DECL|class|Parser
specifier|private
class|class
name|Parser
extends|extends
name|DefaultHandler
implements|implements
name|Runnable
block|{
DECL|field|t
specifier|private
name|Thread
name|t
decl_stmt|;
DECL|field|threadDone
specifier|private
name|boolean
name|threadDone
decl_stmt|;
DECL|field|tuple
specifier|private
name|String
index|[]
name|tuple
decl_stmt|;
DECL|field|nmde
specifier|private
name|NoMoreDataException
name|nmde
decl_stmt|;
DECL|field|contents
specifier|private
name|StringBuffer
name|contents
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
DECL|field|title
specifier|private
name|String
name|title
decl_stmt|;
DECL|field|body
specifier|private
name|String
name|body
decl_stmt|;
DECL|field|time
specifier|private
name|String
name|time
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|method|next
name|String
index|[]
name|next
parameter_list|()
throws|throws
name|NoMoreDataException
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|threadDone
operator|=
literal|false
expr_stmt|;
name|t
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|result
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
name|tuple
operator|==
literal|null
operator|&&
name|nmde
operator|==
literal|null
operator|&&
operator|!
name|threadDone
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|nmde
operator|!=
literal|null
condition|)
block|{
comment|// Set to null so we will re-start thread in case
comment|// we are re-used:
name|t
operator|=
literal|null
expr_stmt|;
throw|throw
name|nmde
throw|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|threadDone
condition|)
block|{
comment|// The thread has exited yet did not hit end of
comment|// data, so this means it hit an exception.  We
comment|// throw NoMorDataException here to force
comment|// benchmark to stop the current alg:
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
name|result
operator|=
name|tuple
expr_stmt|;
name|tuple
operator|=
literal|null
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|time
name|String
name|time
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|8
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|months
index|[
name|Integer
operator|.
name|valueOf
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|original
operator|.
name|substring
argument_list|(
literal|11
argument_list|,
literal|19
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|".000"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
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
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|contents
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
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
name|namespace
parameter_list|,
name|String
name|simple
parameter_list|,
name|String
name|qualified
parameter_list|)
throws|throws
name|SAXException
block|{
name|int
name|elemType
init|=
name|getElementType
argument_list|(
name|qualified
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|elemType
condition|)
block|{
case|case
name|PAGE
case|:
comment|// the body must be null and we either are keeping image docs or the
comment|// title does not start with Image:
if|if
condition|(
name|body
operator|!=
literal|null
operator|&&
operator|(
name|keepImages
operator|||
operator|!
name|title
operator|.
name|startsWith
argument_list|(
literal|"Image:"
argument_list|)
operator|)
condition|)
block|{
name|String
index|[]
name|tmpTuple
init|=
operator|new
name|String
index|[
name|LENGTH
index|]
decl_stmt|;
name|tmpTuple
index|[
name|TITLE
index|]
operator|=
name|title
operator|.
name|replace
argument_list|(
literal|'\t'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|tmpTuple
index|[
name|DATE
index|]
operator|=
name|time
operator|.
name|replace
argument_list|(
literal|'\t'
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|tmpTuple
index|[
name|BODY
index|]
operator|=
name|body
operator|.
name|replaceAll
argument_list|(
literal|"[\t\n]"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|tmpTuple
index|[
name|ID
index|]
operator|=
name|id
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
name|tuple
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
name|tuple
operator|=
name|tmpTuple
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
break|break;
case|case
name|BODY
case|:
name|body
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//workaround that startswith doesn't have an ignore case option, get at least 20 chars.
name|String
name|startsWith
init|=
name|body
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|contents
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|startsWith
operator|.
name|startsWith
argument_list|(
literal|"#redirect"
argument_list|)
condition|)
block|{
name|body
operator|=
literal|null
expr_stmt|;
block|}
break|break;
case|case
name|DATE
case|:
name|time
operator|=
name|time
argument_list|(
name|contents
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|TITLE
case|:
name|title
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
case|case
name|ID
case|:
comment|//the doc id is the first one in the page.  All other ids after that one can be ignored according to the schema
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|id
operator|=
name|contents
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
break|break;
default|default:
comment|// this element should be discarded.
block|}
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|XMLReader
name|reader
init|=
name|XMLReaderFactory
operator|.
name|createXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setErrorHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|InputStream
name|localFileIS
init|=
name|is
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|localFileIS
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
synchronized|synchronized
init|(
name|EnwikiContentSource
operator|.
name|this
init|)
block|{
if|if
condition|(
name|localFileIS
operator|!=
name|is
condition|)
block|{
comment|// fileIS was closed on us, so, just fall
comment|// through
block|}
else|else
comment|// Exception is real
throw|throw
name|ioe
throw|;
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|forever
condition|)
block|{
name|nmde
operator|=
operator|new
name|NoMoreDataException
argument_list|()
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|localFileIS
operator|==
name|is
condition|)
block|{
comment|// If file is not already re-opened then re-open it now
name|is
operator|=
name|getInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|sae
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|sae
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|threadDone
operator|=
literal|true
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|simple
parameter_list|,
name|String
name|qualified
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
name|int
name|elemType
init|=
name|getElementType
argument_list|(
name|qualified
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|elemType
condition|)
block|{
case|case
name|PAGE
case|:
name|title
operator|=
literal|null
expr_stmt|;
name|body
operator|=
literal|null
expr_stmt|;
name|time
operator|=
literal|null
expr_stmt|;
name|id
operator|=
literal|null
expr_stmt|;
break|break;
comment|// intentional fall-through.
case|case
name|BODY
case|:
case|case
name|DATE
case|:
case|case
name|TITLE
case|:
case|case
name|ID
case|:
name|contents
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// this element should be discarded.
block|}
block|}
block|}
DECL|field|ELEMENTS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ELEMENTS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|TITLE
specifier|private
specifier|static
specifier|final
name|int
name|TITLE
init|=
literal|0
decl_stmt|;
DECL|field|DATE
specifier|private
specifier|static
specifier|final
name|int
name|DATE
init|=
name|TITLE
operator|+
literal|1
decl_stmt|;
DECL|field|BODY
specifier|private
specifier|static
specifier|final
name|int
name|BODY
init|=
name|DATE
operator|+
literal|1
decl_stmt|;
DECL|field|ID
specifier|private
specifier|static
specifier|final
name|int
name|ID
init|=
name|BODY
operator|+
literal|1
decl_stmt|;
DECL|field|LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|LENGTH
init|=
name|ID
operator|+
literal|1
decl_stmt|;
comment|// LENGTH is used as the size of the tuple, so whatever constants we need that
comment|// should not be part of the tuple, we should define them after LENGTH.
DECL|field|PAGE
specifier|private
specifier|static
specifier|final
name|int
name|PAGE
init|=
name|LENGTH
operator|+
literal|1
decl_stmt|;
DECL|field|months
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|months
init|=
block|{
literal|"JAN"
block|,
literal|"FEB"
block|,
literal|"MAR"
block|,
literal|"APR"
block|,
literal|"MAY"
block|,
literal|"JUN"
block|,
literal|"JUL"
block|,
literal|"AUG"
block|,
literal|"SEP"
block|,
literal|"OCT"
block|,
literal|"NOV"
block|,
literal|"DEC"
block|}
decl_stmt|;
static|static
block|{
name|ELEMENTS
operator|.
name|put
argument_list|(
literal|"page"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|PAGE
argument_list|)
argument_list|)
expr_stmt|;
name|ELEMENTS
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|BODY
argument_list|)
argument_list|)
expr_stmt|;
name|ELEMENTS
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|ELEMENTS
operator|.
name|put
argument_list|(
literal|"title"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|ELEMENTS
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the type of the element if defined, otherwise returns -1. This    * method is useful in startElement and endElement, by not needing to compare    * the element qualified name over and over.    */
DECL|method|getElementType
specifier|private
specifier|final
specifier|static
name|int
name|getElementType
parameter_list|(
name|String
name|elem
parameter_list|)
block|{
name|Integer
name|val
init|=
name|ELEMENTS
operator|.
name|get
argument_list|(
name|elem
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|val
operator|.
name|intValue
argument_list|()
return|;
block|}
DECL|field|file
specifier|private
name|File
name|file
decl_stmt|;
DECL|field|keepImages
specifier|private
name|boolean
name|keepImages
init|=
literal|true
decl_stmt|;
DECL|field|is
specifier|private
name|InputStream
name|is
decl_stmt|;
DECL|field|parser
specifier|private
name|Parser
name|parser
init|=
operator|new
name|Parser
argument_list|()
decl_stmt|;
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
synchronized|synchronized
init|(
name|EnwikiContentSource
operator|.
name|this
init|)
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
specifier|synchronized
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
name|String
index|[]
name|tuple
init|=
name|parser
operator|.
name|next
argument_list|()
decl_stmt|;
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
name|tuple
index|[
name|ID
index|]
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|tuple
index|[
name|BODY
index|]
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
name|tuple
index|[
name|DATE
index|]
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
name|tuple
index|[
name|TITLE
index|]
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|is
operator|=
name|getInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|keepImages
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"keep.image.only.docs"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"docs.file"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docs.file must be set"
argument_list|)
throw|;
block|}
name|file
operator|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

