begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
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
name|BlockTermState
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
name|CodecUtil
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
name|PushPostingsWriterBase
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
name|TermState
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
name|DataOutput
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

begin_class
DECL|class|IDVersionPostingsWriter
specifier|public
specifier|final
class|class
name|IDVersionPostingsWriter
extends|extends
name|PushPostingsWriterBase
block|{
DECL|field|TERMS_CODEC
specifier|final
specifier|static
name|String
name|TERMS_CODEC
init|=
literal|"IDVersionPostingsWriterTerms"
decl_stmt|;
comment|// Increment version to change it
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|emptyState
specifier|final
specifier|static
name|IDVersionTermState
name|emptyState
init|=
operator|new
name|IDVersionTermState
argument_list|()
decl_stmt|;
DECL|field|lastState
name|IDVersionTermState
name|lastState
decl_stmt|;
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
decl_stmt|;
DECL|field|lastPosition
specifier|private
name|int
name|lastPosition
decl_stmt|;
DECL|field|lastVersion
specifier|private
name|long
name|lastVersion
decl_stmt|;
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|IDVersionTermState
name|newTermState
parameter_list|()
block|{
return|return
operator|new
name|IDVersionTermState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|termsOut
argument_list|,
name|TERMS_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setField
specifier|public
name|int
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|super
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must be index using IndexOptions.DOCS_AND_FREQS_AND_POSITIONS"
argument_list|)
throw|;
block|}
name|lastState
operator|=
name|emptyState
expr_stmt|;
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
name|lastDocID
operator|=
operator|-
literal|1
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
name|lastDocID
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// nocommit need test
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"term appears in more than one document"
argument_list|)
throw|;
block|}
if|if
condition|(
name|termDocFreq
operator|!=
literal|1
condition|)
block|{
comment|// nocommit need test
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"term appears more than once in the document"
argument_list|)
throw|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
name|lastPosition
operator|=
operator|-
literal|1
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
name|BytesRef
name|payload
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
if|if
condition|(
name|lastPosition
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// nocommit need test
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"term appears more than once in document"
argument_list|)
throw|;
block|}
name|lastPosition
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
comment|// nocommit need test
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"token doens't have a payload"
argument_list|)
throw|;
block|}
if|if
condition|(
name|payload
operator|.
name|length
operator|!=
literal|8
condition|)
block|{
comment|// nocommit need test
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"payload.length != 8 (got "
operator|+
name|payload
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|lastVersion
operator|=
name|IDVersionPostingsFormat
operator|.
name|bytesToLong
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastPosition
operator|==
operator|-
literal|1
condition|)
block|{
comment|// nocommit need test
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"missing addPosition"
argument_list|)
throw|;
block|}
block|}
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BlockTermState
name|_state
parameter_list|)
throws|throws
name|IOException
block|{
name|IDVersionTermState
name|state
init|=
operator|(
name|IDVersionTermState
operator|)
name|_state
decl_stmt|;
assert|assert
name|state
operator|.
name|docFreq
operator|>
literal|0
assert|;
assert|assert
name|lastDocID
operator|!=
operator|-
literal|1
assert|;
name|state
operator|.
name|docID
operator|=
name|lastDocID
expr_stmt|;
name|state
operator|.
name|idVersion
operator|=
name|lastVersion
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encodeTerm
specifier|public
name|void
name|encodeTerm
parameter_list|(
name|long
index|[]
name|longs
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_state
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
name|IDVersionTermState
name|state
init|=
operator|(
name|IDVersionTermState
operator|)
name|_state
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|docID
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|idVersion
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
block|{   }
block|}
end_class

end_unit

