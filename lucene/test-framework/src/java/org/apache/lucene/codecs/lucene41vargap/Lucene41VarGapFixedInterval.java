begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene41vargap
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41vargap
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
name|codecs
operator|.
name|FieldsProducer
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
name|PostingsFormat
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
name|PostingsReaderBase
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
name|PostingsWriterBase
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
name|blockterms
operator|.
name|BlockTermsReader
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
name|blockterms
operator|.
name|BlockTermsWriter
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
name|blockterms
operator|.
name|FixedGapTermsIndexWriter
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
name|blockterms
operator|.
name|TermsIndexReaderBase
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
name|blockterms
operator|.
name|TermsIndexWriterBase
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
name|blockterms
operator|.
name|VariableGapTermsIndexReader
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
name|blockterms
operator|.
name|VariableGapTermsIndexWriter
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
name|lucene41
operator|.
name|Lucene41PostingsFormat
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|lucene41
operator|.
name|Lucene41PostingsReader
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
name|lucene41
operator|.
name|Lucene41PostingsWriter
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
name|SegmentReadState
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

begin_comment
comment|// TODO: we could make separate base class that can wrapp
end_comment

begin_comment
comment|// any PostingsBaseFormat and make it ord-able...
end_comment

begin_comment
comment|/**  * Customized version of {@link Lucene41PostingsFormat} that uses  * {@link VariableGapTermsIndexWriter} with a fixed interval.  */
end_comment

begin_class
DECL|class|Lucene41VarGapFixedInterval
specifier|public
specifier|final
class|class
name|Lucene41VarGapFixedInterval
extends|extends
name|PostingsFormat
block|{
DECL|field|termIndexInterval
specifier|final
name|int
name|termIndexInterval
decl_stmt|;
DECL|method|Lucene41VarGapFixedInterval
specifier|public
name|Lucene41VarGapFixedInterval
parameter_list|()
block|{
name|this
argument_list|(
name|FixedGapTermsIndexWriter
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
argument_list|)
expr_stmt|;
block|}
DECL|method|Lucene41VarGapFixedInterval
specifier|public
name|Lucene41VarGapFixedInterval
parameter_list|(
name|int
name|termIndexInterval
parameter_list|)
block|{
name|super
argument_list|(
literal|"Lucene41VarGapFixedInterval"
argument_list|)
expr_stmt|;
name|this
operator|.
name|termIndexInterval
operator|=
name|termIndexInterval
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsWriterBase
name|docs
init|=
operator|new
name|Lucene41PostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// TODO: should we make the terms index more easily
comment|// pluggable?  Ie so that this codec would record which
comment|// index impl was used, and switch on loading?
comment|// Or... you must make a new Codec for this?
name|TermsIndexWriterBase
name|indexWriter
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|indexWriter
operator|=
operator|new
name|VariableGapTermsIndexWriter
argument_list|(
name|state
argument_list|,
operator|new
name|VariableGapTermsIndexWriter
operator|.
name|EveryNTermSelector
argument_list|(
name|termIndexInterval
argument_list|)
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
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
comment|// Must use BlockTermsWriter (not BlockTree) because
comment|// BlockTree doens't support ords (yet)...
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTermsWriter
argument_list|(
name|indexWriter
argument_list|,
name|state
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|PostingsReaderBase
name|postings
init|=
operator|new
name|Lucene41PostingsReader
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|TermsIndexReaderBase
name|indexReader
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|indexReader
operator|=
operator|new
name|VariableGapTermsIndexReader
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|state
operator|.
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
name|postings
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|BlockTermsReader
argument_list|(
name|indexReader
argument_list|,
name|state
operator|.
name|directory
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|postings
argument_list|,
name|state
operator|.
name|context
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|postings
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Extension of freq postings file */
DECL|field|FREQ_EXTENSION
specifier|static
specifier|final
name|String
name|FREQ_EXTENSION
init|=
literal|"frq"
decl_stmt|;
comment|/** Extension of prox postings file */
DECL|field|PROX_EXTENSION
specifier|static
specifier|final
name|String
name|PROX_EXTENSION
init|=
literal|"prx"
decl_stmt|;
block|}
end_class

end_unit

