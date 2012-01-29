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
name|Closeable
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
name|Set
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
name|DocValuesConsumer
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
name|PerDocConsumer
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
name|DocValues
operator|.
name|Type
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
name|DocValues
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
name|AtomicReader
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
name|index
operator|.
name|SegmentInfo
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Writes plain-text norms  *<p>  *<b><font color="red">FOR RECREATIONAL USE ONLY</font></B>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleTextNormsConsumer
specifier|public
class|class
name|SimpleTextNormsConsumer
extends|extends
name|PerDocConsumer
block|{
comment|/** Extension of norms file */
DECL|field|NORMS_EXTENSION
specifier|static
specifier|final
name|String
name|NORMS_EXTENSION
init|=
literal|"len"
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
DECL|field|FIELD
specifier|final
specifier|static
name|BytesRef
name|FIELD
init|=
operator|new
name|BytesRef
argument_list|(
literal|"field "
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
literal|"  doc "
argument_list|)
decl_stmt|;
DECL|field|NORM
specifier|final
specifier|static
name|BytesRef
name|NORM
init|=
operator|new
name|BytesRef
argument_list|(
literal|"    norm "
argument_list|)
decl_stmt|;
DECL|field|writer
specifier|private
name|NormsWriter
name|writer
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
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|method|SimpleTextNormsConsumer
specifier|public
name|SimpleTextNormsConsumer
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
name|this
operator|.
name|context
operator|=
name|context
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
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|finish
argument_list|()
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDocValuesForMerge
specifier|protected
name|DocValues
name|getDocValuesForMerge
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|FieldInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|normValues
argument_list|(
name|info
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|canMerge
specifier|protected
name|boolean
name|canMerge
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|normsPresent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getNormType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|Type
name|type
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|!=
name|Type
operator|.
name|FIXED_INTS_8
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Codec only supports single byte norm values. Type give: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
operator|new
name|SimpleTextNormsDocValuesConsumer
argument_list|(
name|fieldInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|class|SimpleTextNormsDocValuesConsumer
specifier|private
class|class
name|SimpleTextNormsDocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
comment|// Holds all docID/norm pairs we've seen
DECL|field|docIDs
name|int
index|[]
name|docIDs
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|field|norms
name|byte
index|[]
name|norms
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|fi
specifier|private
specifier|final
name|FieldInfo
name|fi
decl_stmt|;
DECL|method|SimpleTextNormsDocValuesConsumer
specifier|public
name|SimpleTextNormsDocValuesConsumer
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|fi
operator|=
name|fieldInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|IndexableField
name|docValue
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|docValue
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|docIDs
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
assert|assert
name|docIDs
operator|.
name|length
operator|==
name|upto
assert|;
name|docIDs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docIDs
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|norms
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
assert|assert
name|norms
operator|.
name|length
operator|==
name|upto
assert|;
name|norms
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|norms
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
name|norms
index|[
name|upto
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
name|docIDs
index|[
name|upto
index|]
operator|=
name|docID
expr_stmt|;
name|upto
operator|++
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
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NormsWriter
name|normsWriter
init|=
name|getNormsWriter
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|int
name|uptoDoc
init|=
literal|0
decl_stmt|;
name|normsWriter
operator|.
name|setNumTotalDocs
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|upto
operator|>
literal|0
condition|)
block|{
name|normsWriter
operator|.
name|startField
argument_list|(
name|fi
argument_list|)
expr_stmt|;
name|int
name|docID
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|docID
operator|<
name|docCount
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|uptoDoc
operator|<
name|upto
operator|&&
name|docIDs
index|[
name|uptoDoc
index|]
operator|==
name|docID
condition|)
block|{
name|normsWriter
operator|.
name|writeNorm
argument_list|(
name|norms
index|[
name|uptoDoc
index|]
argument_list|)
expr_stmt|;
name|uptoDoc
operator|++
expr_stmt|;
block|}
else|else
block|{
name|normsWriter
operator|.
name|writeNorm
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we should have consumed every norm
assert|assert
name|uptoDoc
operator|==
name|upto
assert|;
block|}
else|else
block|{
comment|// Fill entire field with default norm:
name|normsWriter
operator|.
name|startField
argument_list|(
name|fi
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|upto
operator|<
name|docCount
condition|;
name|upto
operator|++
control|)
name|normsWriter
operator|.
name|writeNorm
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
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
name|normsWriter
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getNormsWriter
specifier|public
name|NormsWriter
name|getNormsWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
operator|new
name|NormsWriter
argument_list|(
name|directory
argument_list|,
name|segment
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|writer
return|;
block|}
DECL|class|NormsWriter
specifier|private
specifier|static
class|class
name|NormsWriter
implements|implements
name|Closeable
block|{
DECL|field|output
specifier|private
specifier|final
name|IndexOutput
name|output
decl_stmt|;
DECL|field|numTotalDocs
specifier|private
name|int
name|numTotalDocs
init|=
literal|0
decl_stmt|;
DECL|field|docid
specifier|private
name|int
name|docid
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
DECL|method|NormsWriter
specifier|public
name|NormsWriter
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
specifier|final
name|String
name|normsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|NORMS_EXTENSION
argument_list|)
decl_stmt|;
name|output
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|normsFileName
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|startField
specifier|public
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|info
operator|.
name|omitNorms
operator|==
literal|false
assert|;
name|docid
operator|=
literal|0
expr_stmt|;
name|write
argument_list|(
name|FIELD
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
block|}
DECL|method|writeNorm
specifier|public
name|void
name|writeNorm
parameter_list|(
name|byte
name|norm
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
name|docid
argument_list|)
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|NORM
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|norm
argument_list|)
expr_stmt|;
name|newLine
argument_list|()
expr_stmt|;
name|docid
operator|++
expr_stmt|;
block|}
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
name|docid
operator|!=
name|numDocs
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"mergeNorms produced an invalid result: docCount is "
operator|+
name|numDocs
operator|+
literal|" but only saw "
operator|+
name|docid
operator|+
literal|" file="
operator|+
name|output
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
name|output
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
name|output
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|scratch
operator|.
name|grow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|scratch
operator|.
name|bytes
index|[
name|scratch
operator|.
name|offset
index|]
operator|=
name|b
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
literal|1
expr_stmt|;
name|SimpleTextUtil
operator|.
name|write
argument_list|(
name|output
argument_list|,
name|scratch
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
name|output
argument_list|)
expr_stmt|;
block|}
DECL|method|setNumTotalDocs
specifier|public
name|void
name|setNumTotalDocs
parameter_list|(
name|int
name|numTotalDocs
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|numTotalDocs
operator|==
literal|0
operator|||
name|numTotalDocs
operator|==
name|this
operator|.
name|numTotalDocs
assert|;
name|this
operator|.
name|numTotalDocs
operator|=
name|numTotalDocs
expr_stmt|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|finish
argument_list|(
name|numTotalDocs
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
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldInfos
name|fieldInfos
init|=
name|info
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|normsPresent
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|NORMS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

