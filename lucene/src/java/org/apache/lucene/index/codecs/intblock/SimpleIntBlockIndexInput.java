begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.intblock
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
name|intblock
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Naive int block API that writes vInts.  This is  *  expected to give poor performance; it's really only for  *  testing the pluggability.  One should typically use pfor instead. */
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
name|store
operator|.
name|Directory
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Don't use this class!!  It naively encodes ints one vInt  * at a time.  Use it only for testing.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleIntBlockIndexInput
specifier|public
class|class
name|SimpleIntBlockIndexInput
extends|extends
name|FixedIntBlockIndexInput
block|{
DECL|method|SimpleIntBlockIndexInput
specifier|public
name|SimpleIntBlockIndexInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|int
name|readBufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|readBufferSize
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|SimpleIntBlockIndexOutput
operator|.
name|CODEC
argument_list|,
name|SimpleIntBlockIndexOutput
operator|.
name|VERSION_START
argument_list|,
name|SimpleIntBlockIndexOutput
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|class|BlockReader
specifier|private
specifier|static
class|class
name|BlockReader
implements|implements
name|FixedIntBlockIndexInput
operator|.
name|BlockReader
block|{
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|int
index|[]
name|buffer
decl_stmt|;
DECL|method|BlockReader
specifier|public
name|BlockReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
index|[]
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
DECL|method|readBlock
specifier|public
name|void
name|readBlock
parameter_list|()
throws|throws
name|IOException
block|{
comment|// silly impl
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getBlockReader
specifier|protected
name|BlockReader
name|getBlockReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
index|[]
name|buffer
parameter_list|)
block|{
return|return
operator|new
name|BlockReader
argument_list|(
name|in
argument_list|,
name|buffer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

