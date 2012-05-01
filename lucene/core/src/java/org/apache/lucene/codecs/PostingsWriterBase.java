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
name|index
operator|.
name|FieldInfo
import|;
end_import

begin_comment
comment|/**  * Extension of {@link PostingsConsumer} to support pluggable term dictionaries.  *<p>  * This class contains additional hooks to interact with the provided  * term dictionaries such as {@link BlockTreeTermsWriter} and   * {@link BlockTermsWriter}. If you want to re-use one of these existing  * implementations and are only interested in customizing the format of  * the postings list, extend this class instead.  *   * @see PostingsReaderBase  * @lucene.experimental  */
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
DECL|class|PostingsWriterBase
specifier|public
specifier|abstract
class|class
name|PostingsWriterBase
extends|extends
name|PostingsConsumer
implements|implements
name|Closeable
block|{
DECL|method|start
specifier|public
specifier|abstract
name|void
name|start
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|startTerm
specifier|public
specifier|abstract
name|void
name|startTerm
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Flush count terms starting at start "backwards", as a    *  block. start is a negative offset from the end of the    *  terms stack, ie bigger start means further back in    *  the stack. */
DECL|method|flushTermsBlock
specifier|public
specifier|abstract
name|void
name|flushTermsBlock
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Finishes the current term */
DECL|method|finishTerm
specifier|public
specifier|abstract
name|void
name|finishTerm
parameter_list|(
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setField
specifier|public
specifier|abstract
name|void
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
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
block|}
end_class

end_unit

