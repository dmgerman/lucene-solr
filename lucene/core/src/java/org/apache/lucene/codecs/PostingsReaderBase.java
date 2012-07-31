begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|store
operator|.
name|IndexInput
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

begin_comment
comment|/** The core terms dictionaries (BlockTermsReader,  *  BlockTreeTermsReader) interact with a single instance  *  of this class to manage creation of {@link DocsEnum} and  *  {@link DocsAndPositionsEnum} instances.  It provides an  *  IndexInput (termsIn) where this class may read any  *  previously stored data that it had written in its  *  corresponding {@link PostingsWriterBase} at indexing  *  time.   *  @lucene.experimental */
end_comment

begin_comment
comment|// TODO: find a better name; this defines the API that the
end_comment

begin_comment
comment|// terms dict impls use to talk to a postings impl.
end_comment

begin_comment
comment|// TermsDict + PostingsReader/WriterBase == PostingsConsumer/Producer
end_comment

begin_class
DECL|class|PostingsReaderBase
specifier|public
specifier|abstract
class|class
name|PostingsReaderBase
implements|implements
name|Closeable
block|{
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Return a newly created empty TermState */
DECL|method|newTermState
specifier|public
specifier|abstract
name|BlockTermState
name|newTermState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Actually decode metadata for next term */
DECL|method|nextTerm
specifier|public
specifier|abstract
name|void
name|nextTerm
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Must fully consume state, since after this call that    *  TermState may be reused. */
DECL|method|docs
specifier|public
specifier|abstract
name|DocsEnum
name|docs
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|state
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Must fully consume state, since after this call that    *  TermState may be reused. */
DECL|method|docsAndPositions
specifier|public
specifier|abstract
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|state
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Reads data for all terms in the next block; this    *  method should merely load the byte[] blob but not    *  decode, which is done in {@link #nextTerm}. */
DECL|method|readTermsBlock
specifier|public
specifier|abstract
name|void
name|readTermsBlock
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|termState
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

