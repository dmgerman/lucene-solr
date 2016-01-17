begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|StoredFieldsWriter
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
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|IndexableField
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
name|IOContext
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
name|IndexOutput
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
name|BytesRefBuilder
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
comment|/**  * Writes plain-text stored fields.  *<p>  *<b>FOR RECREATIONAL USE ONLY</b>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextStoredFieldsWriter
specifier|public
class|class
name|SimpleTextStoredFieldsWriter
extends|extends
name|StoredFieldsWriter
block|{
DECL|field|numDocsWritten
specifier|private
name|int
name|numDocsWritten
init|=
literal|0
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|out
specifier|private
name|IndexOutput
name|out
decl_stmt|;
DECL|field|FIELDS_EXTENSION
specifier|final
specifier|static
name|String
name|FIELDS_EXTENSION
init|=
literal|"fld"
decl_stmt|;
DECL|field|TYPE_STRING
specifier|final
specifier|static
name|BytesRef
name|TYPE_STRING
init|=
operator|new
name|BytesRef
argument_list|(
literal|"string"
argument_list|)
decl_stmt|;
DECL|field|TYPE_BINARY
specifier|final
specifier|static
name|BytesRef
name|TYPE_BINARY
init|=
operator|new
name|BytesRef
argument_list|(
literal|"binary"
argument_list|)
decl_stmt|;
DECL|field|TYPE_INT
specifier|final
specifier|static
name|BytesRef
name|TYPE_INT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"int"
argument_list|)
decl_stmt|;
DECL|field|TYPE_LONG
specifier|final
specifier|static
name|BytesRef
name|TYPE_LONG
init|=
operator|new
name|BytesRef
argument_list|(
literal|"long"
argument_list|)
decl_stmt|;
DECL|field|TYPE_FLOAT
specifier|final
specifier|static
name|BytesRef
name|TYPE_FLOAT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"float"
argument_list|)
decl_stmt|;
DECL|field|TYPE_DOUBLE
specifier|final
specifier|static
name|BytesRef
name|TYPE_DOUBLE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"double"
argument_list|)
decl_stmt|;
DECL|field|END
specifier|final
specifier|static
name|BytesRef
name|END
init|=
operator|new
name|BytesRef
argument_list|(
literal|"END"
argument_list|)
decl_stmt|;
DECL|field|DOC
specifier|final
specifier|static
name|BytesRef
name|DOC
init|=
operator|new
name|BytesRef
argument_list|(
literal|"doc "
argument_list|)
decl_stmt|;
DECL|field|FIELD
specifier|final
specifier|static
name|BytesRef
name|FIELD
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  field "
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|final
specifier|static
name|BytesRef
name|NAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    name "
argument_list|)
decl_stmt|;
DECL|field|TYPE
specifier|final
specifier|static
name|BytesRef
name|TYPE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    type "
argument_list|)
decl_stmt|;
DECL|field|VALUE
specifier|final
specifier|static
name|BytesRef
name|VALUE
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    value "
argument_list|)
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|method|SimpleTextStoredFieldsWriter
specifier|public
name|SimpleTextStoredFieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|out
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|DOC
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|numDocsWritten
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|numDocsWritten
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|IndexableField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|info
operator|.
name|number
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|TYPE
argument_list|)
expr_stmt|;
specifier|final
name|Number
name|n
init|=
name|field
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|instanceof
name|Byte
operator|||
name|n
operator|instanceof
name|Short
operator|||
name|n
operator|instanceof
name|Integer
condition|)
block|{
name|write
argument_list|(
name|TYPE_INT
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|n
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|instanceof
name|Long
condition|)
block|{
name|write
argument_list|(
name|TYPE_LONG
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|instanceof
name|Float
condition|)
block|{
name|write
argument_list|(
name|TYPE_FLOAT
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|n
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|instanceof
name|Double
condition|)
block|{
name|write
argument_list|(
name|TYPE_DOUBLE
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|n
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot store numeric type "
operator|+
name|n
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|BytesRef
name|bytes
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|write
argument_list|(
name|TYPE_BINARY
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|stringValue
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field "
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|" is stored but does not have binaryValue, stringValue nor numericValue"
argument_list|)
throw|;
block|}
else|else
block|{
name|write
argument_list|(
name|TYPE_STRING
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|VALUE
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numDocsWritten
operator|!=
name|numDocs
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"mergeFields produced an invalid result: docCount is "
operator|+
name|numDocs
operator|+
literal|" but only saw "
operator|+
name|numDocsWritten
operator|+
literal|" file="
operator|+
name|out
operator|.
name|toString
argument_list|()
operator|+
literal|"; now aborting this merge to prevent index corruption"
argument_list|)
throw|;
block|}
name|write
argument_list|(
name|END
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|SimpleTextUtil
operator|.
name|writeChecksum
argument_list|(
name|out
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
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
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|s
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|newLine
specifier|private
name|void
name|newLine
parameter_list|()
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|writeNewline
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

