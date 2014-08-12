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
name|FieldsConsumer
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|Fields
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
name|SegmentWriteState
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
name|Terms
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
name|TermsEnum
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

begin_class
DECL|class|SimpleTextFieldsWriter
class|class
name|SimpleTextFieldsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|out
specifier|private
name|IndexOutput
name|out
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
DECL|field|writeState
specifier|private
specifier|final
name|SegmentWriteState
name|writeState
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
DECL|field|TERM
specifier|final
specifier|static
name|BytesRef
name|TERM
init|=
operator|new
name|BytesRef
argument_list|(
literal|"  term "
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
literal|"    doc "
argument_list|)
decl_stmt|;
DECL|field|FREQ
specifier|final
specifier|static
name|BytesRef
name|FREQ
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      freq "
argument_list|)
decl_stmt|;
DECL|field|POS
specifier|final
specifier|static
name|BytesRef
name|POS
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      pos "
argument_list|)
decl_stmt|;
DECL|field|START_OFFSET
specifier|final
specifier|static
name|BytesRef
name|START_OFFSET
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      startOffset "
argument_list|)
decl_stmt|;
DECL|field|END_OFFSET
specifier|final
specifier|static
name|BytesRef
name|END_OFFSET
init|=
operator|new
name|BytesRef
argument_list|(
literal|"      endOffset "
argument_list|)
decl_stmt|;
DECL|field|PAYLOAD
specifier|final
specifier|static
name|BytesRef
name|PAYLOAD
init|=
operator|new
name|BytesRef
argument_list|(
literal|"        payload "
argument_list|)
decl_stmt|;
DECL|method|SimpleTextFieldsWriter
specifier|public
name|SimpleTextFieldsWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|SimpleTextPostingsFormat
operator|.
name|getPostingsFileName
argument_list|(
name|writeState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|writeState
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|out
operator|=
name|writeState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|writeState
operator|.
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeState
operator|=
name|writeState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
name|writeState
operator|.
name|fieldInfos
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
comment|// for each field
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// Annoyingly, this can happen!
continue|continue;
block|}
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|boolean
name|wroteField
init|=
literal|false
decl_stmt|;
name|boolean
name|hasPositions
init|=
name|terms
operator|.
name|hasPositions
argument_list|()
decl_stmt|;
name|boolean
name|hasFreqs
init|=
name|terms
operator|.
name|hasFreqs
argument_list|()
decl_stmt|;
name|boolean
name|hasPayloads
init|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
decl_stmt|;
name|boolean
name|hasOffsets
init|=
name|terms
operator|.
name|hasOffsets
argument_list|()
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|hasPositions
condition|)
block|{
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|flags
operator|=
name|flags
operator||
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
expr_stmt|;
block|}
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|flags
operator|=
name|flags
operator||
name|DocsAndPositionsEnum
operator|.
name|FLAG_OFFSETS
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|hasFreqs
condition|)
block|{
name|flags
operator|=
name|flags
operator||
name|DocsEnum
operator|.
name|FLAG_FREQS
expr_stmt|;
block|}
block|}
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|posEnum
init|=
literal|null
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
comment|// for each term in field
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|hasPositions
condition|)
block|{
name|posEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posEnum
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|docsEnum
operator|=
name|posEnum
expr_stmt|;
block|}
else|else
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
assert|assert
name|docsEnum
operator|!=
literal|null
operator|:
literal|"termsEnum="
operator|+
name|termsEnum
operator|+
literal|" hasPos="
operator|+
name|hasPositions
operator|+
literal|" flags="
operator|+
name|flags
assert|;
name|boolean
name|wroteTerm
init|=
literal|false
decl_stmt|;
comment|// for each doc in field+term
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|doc
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
if|if
condition|(
operator|!
name|wroteTerm
condition|)
block|{
if|if
condition|(
operator|!
name|wroteField
condition|)
block|{
comment|// we lazily do this, in case the field had
comment|// no terms
name|write
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
name|wroteField
operator|=
literal|true
expr_stmt|;
block|}
comment|// we lazily do this, in case the term had
comment|// zero docs
name|write
argument_list|(
name|TERM
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
name|wroteTerm
operator|=
literal|true
expr_stmt|;
block|}
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
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasFreqs
condition|)
block|{
name|int
name|freq
init|=
name|docsEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
name|write
argument_list|(
name|FREQ
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
name|newline
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasPositions
condition|)
block|{
comment|// for assert:
name|int
name|lastStartOffset
init|=
literal|0
decl_stmt|;
comment|// for each pos in field+term+doc
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|int
name|position
init|=
name|posEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|write
argument_list|(
name|POS
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
name|newline
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|posEnum
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|posEnum
operator|.
name|endOffset
argument_list|()
decl_stmt|;
assert|assert
name|endOffset
operator|>=
name|startOffset
assert|;
assert|assert
name|startOffset
operator|>=
name|lastStartOffset
operator|:
literal|"startOffset="
operator|+
name|startOffset
operator|+
literal|" lastStartOffset="
operator|+
name|lastStartOffset
assert|;
name|lastStartOffset
operator|=
name|startOffset
expr_stmt|;
name|write
argument_list|(
name|START_OFFSET
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
name|newline
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|END_OFFSET
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
name|newline
argument_list|()
expr_stmt|;
block|}
name|BytesRef
name|payload
init|=
name|posEnum
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|>
literal|0
condition|)
block|{
assert|assert
name|payload
operator|.
name|length
operator|!=
literal|0
assert|;
name|write
argument_list|(
name|PAYLOAD
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
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
name|b
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
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|newline
specifier|private
name|void
name|newline
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
name|out
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|write
argument_list|(
name|END
argument_list|)
expr_stmt|;
name|newline
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
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

