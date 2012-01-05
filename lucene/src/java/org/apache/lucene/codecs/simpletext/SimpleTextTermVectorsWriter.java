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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TermVectorsWriter
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Writes plain-text term vectors.  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextTermVectorsWriter
specifier|public
class|class
name|SimpleTextTermVectorsWriter
extends|extends
name|TermVectorsWriter
block|{
DECL|field|END
specifier|static
specifier|final
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
specifier|static
specifier|final
name|BytesRef
name|DOC
init|=
operator|new
name|BytesRef
argument_list|(
literal|"doc "
argument_list|)
decl_stmt|;
DECL|field|NUMFIELDS
specifier|static
specifier|final
name|BytesRef
name|NUMFIELDS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  numfields "
argument_list|)
decl_stmt|;
DECL|field|FIELD
specifier|static
specifier|final
name|BytesRef
name|FIELD
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  field "
argument_list|)
decl_stmt|;
DECL|field|FIELDNAME
specifier|static
specifier|final
name|BytesRef
name|FIELDNAME
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    name "
argument_list|)
decl_stmt|;
DECL|field|FIELDPOSITIONS
specifier|static
specifier|final
name|BytesRef
name|FIELDPOSITIONS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    positions "
argument_list|)
decl_stmt|;
DECL|field|FIELDOFFSETS
specifier|static
specifier|final
name|BytesRef
name|FIELDOFFSETS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    offsets   "
argument_list|)
decl_stmt|;
DECL|field|FIELDTERMCOUNT
specifier|static
specifier|final
name|BytesRef
name|FIELDTERMCOUNT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    numterms "
argument_list|)
decl_stmt|;
DECL|field|TERMTEXT
specifier|static
specifier|final
name|BytesRef
name|TERMTEXT
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    term "
argument_list|)
decl_stmt|;
DECL|field|TERMFREQ
specifier|static
specifier|final
name|BytesRef
name|TERMFREQ
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      freq "
argument_list|)
decl_stmt|;
DECL|field|POSITION
specifier|static
specifier|final
name|BytesRef
name|POSITION
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      position "
argument_list|)
decl_stmt|;
DECL|field|STARTOFFSET
specifier|static
specifier|final
name|BytesRef
name|STARTOFFSET
init|=
operator|new
name|BytesRef
argument_list|(
literal|"        startoffset "
argument_list|)
decl_stmt|;
DECL|field|ENDOFFSET
specifier|static
specifier|final
name|BytesRef
name|ENDOFFSET
init|=
operator|new
name|BytesRef
argument_list|(
literal|"        endoffset "
argument_list|)
decl_stmt|;
DECL|field|VECTORS_EXTENSION
specifier|static
specifier|final
name|String
name|VECTORS_EXTENSION
init|=
literal|"vec"
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
DECL|field|numDocsWritten
specifier|private
name|int
name|numDocsWritten
init|=
literal|0
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|offsets
specifier|private
name|boolean
name|offsets
decl_stmt|;
DECL|field|positions
specifier|private
name|boolean
name|positions
decl_stmt|;
DECL|method|SimpleTextTermVectorsWriter
specifier|public
name|SimpleTextTermVectorsWriter
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
name|VECTORS_EXTENSION
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
name|abort
argument_list|()
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
parameter_list|(
name|int
name|numVectorFields
parameter_list|)
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
name|write
argument_list|(
name|NUMFIELDS
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|numVectorFields
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
DECL|method|startField
specifier|public
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|positions
parameter_list|,
name|boolean
name|offsets
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
name|FIELDNAME
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|FIELDPOSITIONS
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|positions
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|FIELDOFFSETS
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|offsets
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|FIELDTERMCOUNT
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|numTerms
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|TERMTEXT
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|TERMFREQ
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|freq
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|positions
operator|||
name|offsets
assert|;
if|if
condition|(
name|positions
condition|)
block|{
name|write
argument_list|(
name|POSITION
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|position
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|offsets
condition|)
block|{
name|write
argument_list|(
name|STARTOFFSET
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|startOffset
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|ENDOFFSET
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|endOffset
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|directory
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|VECTORS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
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
literal|"mergeVectors produced an invalid result: mergedDocs is "
operator|+
name|numDocs
operator|+
literal|" but vec numDocs is "
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

