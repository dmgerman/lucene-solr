begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
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
name|facet
operator|.
name|FacetsConfig
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
name|AtomicReaderContext
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
name|index
operator|.
name|DocValues
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
name|ArrayUtil
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
name|IntsRef
import|;
end_import

begin_comment
comment|/** Decodes ordinals previously indexed into a BinaryDocValues field */
end_comment

begin_class
DECL|class|DocValuesOrdinalsReader
specifier|public
class|class
name|DocValuesOrdinalsReader
extends|extends
name|OrdinalsReader
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
comment|/** Default constructor. */
DECL|method|DocValuesOrdinalsReader
specifier|public
name|DocValuesOrdinalsReader
parameter_list|()
block|{
name|this
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
expr_stmt|;
block|}
comment|/** Create this, with the specified indexed field name. */
DECL|method|DocValuesOrdinalsReader
specifier|public
name|DocValuesOrdinalsReader
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|OrdinalsSegmentReader
name|getReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|values0
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values0
operator|==
literal|null
condition|)
block|{
name|values0
operator|=
name|DocValues
operator|.
name|EMPTY_BINARY
expr_stmt|;
block|}
specifier|final
name|BinaryDocValues
name|values
init|=
name|values0
decl_stmt|;
return|return
operator|new
name|OrdinalsSegmentReader
argument_list|()
block|{
specifier|private
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|decode
argument_list|(
name|bytes
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexFieldName
specifier|public
name|String
name|getIndexFieldName
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Subclass& override if you change the encoding. */
DECL|method|decode
specifier|protected
name|void
name|decode
parameter_list|(
name|BytesRef
name|buf
parameter_list|,
name|IntsRef
name|ordinals
parameter_list|)
block|{
comment|// grow the buffer up front, even if by a large number of values (buf.length)
comment|// that saves the need to check inside the loop for every decoded value if
comment|// the buffer needs to grow.
if|if
condition|(
name|ordinals
operator|.
name|ints
operator|.
name|length
operator|<
name|buf
operator|.
name|length
condition|)
block|{
name|ordinals
operator|.
name|ints
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ordinals
operator|.
name|ints
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|ordinals
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|ordinals
operator|.
name|length
operator|=
literal|0
expr_stmt|;
comment|// it is better if the decoding is inlined like so, and not e.g.
comment|// in a utility method
name|int
name|upto
init|=
name|buf
operator|.
name|offset
operator|+
name|buf
operator|.
name|length
decl_stmt|;
name|int
name|value
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
name|buf
operator|.
name|offset
decl_stmt|;
name|int
name|prev
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|upto
condition|)
block|{
name|byte
name|b
init|=
name|buf
operator|.
name|bytes
index|[
name|offset
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
name|ordinals
operator|.
name|ints
index|[
name|ordinals
operator|.
name|length
index|]
operator|=
operator|(
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
name|b
operator|)
operator|+
name|prev
expr_stmt|;
name|value
operator|=
literal|0
expr_stmt|;
name|prev
operator|=
name|ordinals
operator|.
name|ints
index|[
name|ordinals
operator|.
name|length
index|]
expr_stmt|;
name|ordinals
operator|.
name|length
operator|++
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
operator|(
name|b
operator|&
literal|0x7F
operator|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

