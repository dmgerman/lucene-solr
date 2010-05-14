begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Bits
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

begin_comment
comment|/** Implements flex API (FieldsEnum/TermsEnum) on top of  *  pre-flex API.  Used only for IndexReader impls outside  *  Lucene's core.  *  *  @deprecated Migrate the external reader to the flex API */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyFieldsEnum
class|class
name|LegacyFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|r
specifier|private
specifier|final
name|IndexReader
name|r
decl_stmt|;
DECL|field|terms
specifier|private
name|TermEnum
name|terms
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|init
specifier|private
name|boolean
name|init
decl_stmt|;
DECL|method|LegacyFieldsEnum
specifier|public
name|LegacyFieldsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
name|terms
operator|=
name|r
operator|.
name|terms
argument_list|()
expr_stmt|;
name|init
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|terms
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// jump to end of the current field:
name|terms
operator|=
name|r
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"\uFFFF"
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|terms
operator|.
name|term
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|terms
operator|.
name|term
argument_list|()
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
assert|;
block|}
if|if
condition|(
name|init
condition|)
block|{
name|init
operator|=
literal|false
expr_stmt|;
if|if
condition|(
operator|!
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|terms
operator|.
name|term
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|newField
init|=
name|terms
operator|.
name|term
argument_list|()
operator|.
name|field
decl_stmt|;
assert|assert
name|field
operator|==
literal|null
operator|||
operator|!
name|newField
operator|.
name|equals
argument_list|(
name|field
argument_list|)
assert|;
name|field
operator|=
name|newField
expr_stmt|;
return|return
name|field
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|LegacyTermsEnum
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|class|LegacyTermsEnum
specifier|static
class|class
name|LegacyTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|r
specifier|private
specifier|final
name|IndexReader
name|r
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
name|TermEnum
name|terms
decl_stmt|;
DECL|field|current
specifier|private
name|BytesRef
name|current
decl_stmt|;
DECL|field|tr
specifier|private
specifier|final
name|BytesRef
name|tr
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|LegacyTermsEnum
name|LegacyTermsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
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
comment|// Pre-flex indexes always sorted in UTF16 order
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|terms
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|terms
operator|=
name|r
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Term
name|t
init|=
name|terms
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|current
operator|=
literal|null
expr_stmt|;
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|tr
operator|.
name|copy
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|=
name|tr
expr_stmt|;
if|if
condition|(
name|text
operator|.
name|bytesEquals
argument_list|(
name|tr
argument_list|)
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|FOUND
return|;
block|}
else|else
block|{
return|return
name|SeekStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
else|else
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|SeekStatus
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// first next -- seek to start of field
name|terms
operator|=
name|r
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Term
name|t
init|=
name|terms
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
operator|||
name|t
operator|.
name|field
operator|!=
name|field
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|tr
operator|.
name|copy
argument_list|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|current
operator|=
name|tr
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|field
operator|==
name|field
condition|)
block|{
name|tr
operator|.
name|copy
argument_list|(
name|terms
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|current
operator|=
name|tr
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|terms
operator|.
name|docFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
operator|(
name|LegacyDocsEnum
operator|)
name|reuse
operator|)
operator|.
name|reset
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|skipDocs
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
operator|new
name|LegacyDocsEnum
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
operator|)
operator|.
name|reset
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|skipDocs
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
operator|(
name|LegacyDocsAndPositionsEnum
operator|)
name|reuse
operator|)
operator|.
name|reset
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|skipDocs
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
operator|new
name|LegacyDocsAndPositionsEnum
argument_list|(
name|r
argument_list|,
name|field
argument_list|)
operator|)
operator|.
name|reset
argument_list|(
name|terms
operator|.
name|term
argument_list|()
argument_list|,
name|skipDocs
argument_list|)
return|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|terms
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Emulates flex on top of legacy API
DECL|class|LegacyDocsEnum
specifier|private
specifier|static
class|class
name|LegacyDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|r
specifier|private
specifier|final
name|IndexReader
name|r
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|td
specifier|private
specifier|final
name|TermDocs
name|td
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|LegacyDocsEnum
name|LegacyDocsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|td
operator|=
name|r
operator|.
name|termDocs
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|DocsEnum
name|reset
parameter_list|(
name|Term
name|term
parameter_list|,
name|Bits
name|skipDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipDocs
operator|!=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|r
argument_list|)
condition|)
block|{
comment|// An external reader's TermDocs/Positions will
comment|// silently skip deleted docs, so, we can't allow
comment|// arbitrary skipDocs here:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"external IndexReader requires skipDocs == MultiFields.getDeletedDocs()"
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|td
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
name|doc
operator|=
name|td
operator|.
name|doc
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|td
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|doc
operator|=
name|td
operator|.
name|doc
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|td
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
block|}
comment|// Emulates flex on top of legacy API
DECL|class|LegacyDocsAndPositionsEnum
specifier|private
specifier|static
class|class
name|LegacyDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|r
specifier|private
specifier|final
name|IndexReader
name|r
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|tp
specifier|private
specifier|final
name|TermPositions
name|tp
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|LegacyDocsAndPositionsEnum
name|LegacyDocsAndPositionsEnum
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|r
operator|=
name|r
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|tp
operator|=
name|r
operator|.
name|termPositions
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|DocsAndPositionsEnum
name|reset
parameter_list|(
name|Term
name|term
parameter_list|,
name|Bits
name|skipDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|tp
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
name|skipDocs
operator|!=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|r
argument_list|)
condition|)
block|{
comment|// An external reader's TermDocs/Positions will
comment|// silently skip deleted docs, so, we can't allow
comment|// arbitrary skipDocs here:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"external IndexReader requires skipDocs == MultiFields.getDeletedDocs() skipDocs="
operator|+
name|skipDocs
operator|+
literal|" MultiFields.getDeletedDocs="
operator|+
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|r
argument_list|)
operator|+
literal|" r="
operator|+
name|r
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tp
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
name|doc
operator|=
name|tp
operator|.
name|doc
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tp
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|doc
operator|=
name|tp
operator|.
name|doc
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|tp
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
comment|// NOTE: we don't override bulk-read (docs& freqs) API
comment|// -- leave it to base class, because TermPositions
comment|// can't do bulk read
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|tp
operator|.
name|nextPosition
argument_list|()
return|;
block|}
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|tp
operator|.
name|getPayloadLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|payload
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|payload
operator|.
name|bytes
operator|.
name|length
operator|<
name|len
condition|)
block|{
name|payload
operator|.
name|grow
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
block|}
name|payload
operator|.
name|bytes
operator|=
name|tp
operator|.
name|getPayload
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|len
expr_stmt|;
return|return
name|payload
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
parameter_list|()
block|{
return|return
name|tp
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

