begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|lucene
operator|.
name|index
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
name|codecs
operator|.
name|TermsConsumer
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
name|codecs
operator|.
name|PostingsConsumer
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
name|codecs
operator|.
name|TermStats
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|store
operator|.
name|IndexOutput
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
name|Comparator
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
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|NEWLINE
specifier|final
specifier|static
name|byte
name|NEWLINE
init|=
literal|10
decl_stmt|;
DECL|field|ESCAPE
specifier|final
specifier|static
name|byte
name|ESCAPE
init|=
literal|92
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
name|state
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
name|state
operator|.
name|segmentName
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
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
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|write
argument_list|(
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|byte
name|bx
init|=
name|b
operator|.
name|bytes
index|[
name|b
operator|.
name|offset
operator|+
name|i
index|]
decl_stmt|;
if|if
condition|(
name|bx
operator|==
name|NEWLINE
operator|||
name|bx
operator|==
name|ESCAPE
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|ESCAPE
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
name|bx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newline
specifier|private
name|void
name|newline
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|NEWLINE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
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
name|field
operator|.
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|NEWLINE
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimpleTextTermsWriter
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|class|SimpleTextTermsWriter
specifier|private
class|class
name|SimpleTextTermsWriter
extends|extends
name|TermsConsumer
block|{
DECL|field|postingsWriter
specifier|private
specifier|final
name|SimpleTextPostingsWriter
name|postingsWriter
decl_stmt|;
DECL|method|SimpleTextTermsWriter
specifier|public
name|SimpleTextTermsWriter
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
name|postingsWriter
operator|=
operator|new
name|SimpleTextPostingsWriter
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|postingsWriter
operator|.
name|reset
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
block|}
DECL|class|SimpleTextPostingsWriter
specifier|private
class|class
name|SimpleTextPostingsWriter
extends|extends
name|PostingsConsumer
block|{
DECL|field|term
specifier|private
name|BytesRef
name|term
decl_stmt|;
DECL|field|wroteTerm
specifier|private
name|boolean
name|wroteTerm
decl_stmt|;
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|method|SimpleTextPostingsWriter
specifier|public
name|SimpleTextPostingsWriter
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|field
operator|.
name|indexOptions
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|termDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|wroteTerm
condition|)
block|{
comment|// we lazily do this, in case the term had zero docs
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
name|docID
argument_list|)
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
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
name|termDocFreq
argument_list|)
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|reset
specifier|public
name|PostingsConsumer
name|reset
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|wroteTerm
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
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
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
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
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
block|{     }
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
name|write
argument_list|(
name|END
argument_list|)
expr_stmt|;
name|newline
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

