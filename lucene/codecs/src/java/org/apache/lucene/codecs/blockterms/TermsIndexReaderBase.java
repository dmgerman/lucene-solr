begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.blockterms
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blockterms
package|;
end_package

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
name|util
operator|.
name|Accountable
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
comment|// TODO
end_comment

begin_comment
comment|//   - allow for non-regular index intervals?  eg with a
end_comment

begin_comment
comment|//     long string of rare terms, you don't need such
end_comment

begin_comment
comment|//     frequent indexing
end_comment

begin_comment
comment|/**  * {@link BlockTermsReader} interacts with an instance of this class  * to manage its terms index.  The writer must accept  * indexed terms (many pairs of BytesRef text + long  * fileOffset), and then this reader must be able to  * retrieve the nearest index term to a provided term  * text.   * @lucene.experimental */
end_comment

begin_class
DECL|class|TermsIndexReaderBase
specifier|public
specifier|abstract
class|class
name|TermsIndexReaderBase
implements|implements
name|Closeable
implements|,
name|Accountable
block|{
DECL|method|getFieldEnum
specifier|public
specifier|abstract
name|FieldIndexEnum
name|getFieldEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|supportsOrd
specifier|public
specifier|abstract
name|boolean
name|supportsOrd
parameter_list|()
function_decl|;
comment|/**     * Similar to TermsEnum, except, the only "metadata" it    * reports for a given indexed term is the long fileOffset    * into the main terms dictionary file.    */
DECL|class|FieldIndexEnum
specifier|public
specifier|static
specifier|abstract
class|class
name|FieldIndexEnum
block|{
comment|/** Seeks to "largest" indexed term that's&lt;=      *  term; returns file pointer index (into the main      *  terms index file) for that term */
DECL|method|seek
specifier|public
specifier|abstract
name|long
name|seek
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns -1 at end */
DECL|method|next
specifier|public
specifier|abstract
name|long
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|term
specifier|public
specifier|abstract
name|BytesRef
name|term
parameter_list|()
function_decl|;
comment|/** Only implemented if {@link TermsIndexReaderBase#supportsOrd()} returns true. */
DECL|method|seek
specifier|public
specifier|abstract
name|long
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Only implemented if {@link TermsIndexReaderBase#supportsOrd()} returns true. */
DECL|method|ord
specifier|public
specifier|abstract
name|long
name|ord
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

