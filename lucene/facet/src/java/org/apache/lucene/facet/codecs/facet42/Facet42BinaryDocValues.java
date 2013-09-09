begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.codecs.facet42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|codecs
operator|.
name|facet42
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
name|index
operator|.
name|BinaryDocValues
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
name|DataInput
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
name|RamUsageEstimator
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_class
DECL|class|Facet42BinaryDocValues
class|class
name|Facet42BinaryDocValues
extends|extends
name|BinaryDocValues
block|{
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|addresses
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|addresses
decl_stmt|;
DECL|method|Facet42BinaryDocValues
name|Facet42BinaryDocValues
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|totBytes
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|bytes
operator|=
operator|new
name|byte
index|[
name|totBytes
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|totBytes
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
block|{
name|int
name|start
init|=
operator|(
name|int
operator|)
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|ret
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|ret
operator|.
name|offset
operator|=
name|start
expr_stmt|;
name|ret
operator|.
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|addresses
operator|.
name|get
argument_list|(
name|docID
operator|+
literal|1
argument_list|)
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
comment|/** Returns approximate RAM bytes used */
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|bytes
argument_list|)
operator|+
name|addresses
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
block|}
end_class

end_unit

