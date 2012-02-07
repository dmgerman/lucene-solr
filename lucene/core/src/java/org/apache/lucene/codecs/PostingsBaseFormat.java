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
name|SegmentReadState
import|;
end_import

begin_comment
comment|/**   * Provides a {@link PostingsReaderBase} and {@link  * PostingsWriterBase}.  *  * @lucene.experimental */
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

begin_comment
comment|// can we clean this up and do this some other way?
end_comment

begin_comment
comment|// refactor some of these classes and use covariant return?
end_comment

begin_class
DECL|class|PostingsBaseFormat
specifier|public
specifier|abstract
class|class
name|PostingsBaseFormat
block|{
comment|/** Unique name that's used to retrieve this codec when    *  reading the index */
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|PostingsBaseFormat
specifier|protected
name|PostingsBaseFormat
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|postingsReaderBase
specifier|public
specifier|abstract
name|PostingsReaderBase
name|postingsReaderBase
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|postingsWriterBase
specifier|public
specifier|abstract
name|PostingsWriterBase
name|postingsWriterBase
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|files
specifier|public
specifier|abstract
name|void
name|files
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

