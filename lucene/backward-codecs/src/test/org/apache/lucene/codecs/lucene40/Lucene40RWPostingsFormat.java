begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|blocktree
operator|.
name|BlockTreeTermsWriter
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
comment|/**  * Read-write version of 4.0 postings format for testing  * @deprecated for test purposes only  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40RWPostingsFormat
specifier|public
specifier|final
class|class
name|Lucene40RWPostingsFormat
extends|extends
name|Lucene40PostingsFormat
block|{
comment|/** minimum items (terms or sub-blocks) per block for 4.0 BlockTree */
DECL|field|MIN_BLOCK_SIZE
specifier|final
specifier|static
name|int
name|MIN_BLOCK_SIZE
init|=
literal|25
decl_stmt|;
comment|/** maximum items (terms or sub-blocks) per block for 4.0 BlockTree */
DECL|field|MAX_BLOCK_SIZE
specifier|final
specifier|static
name|int
name|MAX_BLOCK_SIZE
init|=
literal|48
decl_stmt|;
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
name|Lucene40PostingsWriter
argument_list|(
name|state
argument_list|)
decl_stmt|;
comment|// TODO: should we make the terms index more easily
comment|// pluggable?  Ie so that this codec would record which
comment|// index impl was used, and switch on loading?
comment|// Or... you must make a new Codec for this?
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|BlockTreeTermsWriter
argument_list|(
name|state
argument_list|,
name|docs
argument_list|,
name|MIN_BLOCK_SIZE
argument_list|,
name|MAX_BLOCK_SIZE
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
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

