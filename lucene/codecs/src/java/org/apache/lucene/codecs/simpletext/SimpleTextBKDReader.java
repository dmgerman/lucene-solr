begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.simpletext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|DimensionalValues
operator|.
name|IntersectVisitor
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
name|BytesRefBuilder
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
name|StringHelper
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
name|bkd
operator|.
name|BKDReader
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextDimensionalWriter
operator|.
name|BLOCK_COUNT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextDimensionalWriter
operator|.
name|BLOCK_DOC_ID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextDimensionalWriter
operator|.
name|BLOCK_VALUE
import|;
end_import

begin_class
DECL|class|SimpleTextBKDReader
class|class
name|SimpleTextBKDReader
extends|extends
name|BKDReader
block|{
DECL|method|SimpleTextBKDReader
specifier|public
name|SimpleTextBKDReader
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|int
name|numDims
parameter_list|,
name|int
name|maxPointsInLeafNode
parameter_list|,
name|int
name|bytesPerDim
parameter_list|,
name|long
index|[]
name|leafBlockFPs
parameter_list|,
name|byte
index|[]
name|splitPackedValues
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|numDims
argument_list|,
name|maxPointsInLeafNode
argument_list|,
name|bytesPerDim
argument_list|,
name|leafBlockFPs
argument_list|,
name|splitPackedValues
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitDocIDs
specifier|protected
name|void
name|visitDocIDs
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|long
name|blockFP
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|blockFP
argument_list|)
expr_stmt|;
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|parseInt
argument_list|(
name|scratch
argument_list|,
name|BLOCK_COUNT
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|parseInt
argument_list|(
name|scratch
argument_list|,
name|BLOCK_DOC_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readDocIDs
specifier|protected
name|int
name|readDocIDs
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|long
name|blockFP
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|blockFP
argument_list|)
expr_stmt|;
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|parseInt
argument_list|(
name|scratch
argument_list|,
name|BLOCK_COUNT
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|docIDs
index|[
name|i
index|]
operator|=
name|parseInt
argument_list|(
name|scratch
argument_list|,
name|BLOCK_DOC_ID
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|visitDocValues
specifier|protected
name|void
name|visitDocValues
parameter_list|(
name|byte
index|[]
name|scratchPackedValue
parameter_list|,
name|IndexInput
name|in
parameter_list|,
name|int
index|[]
name|docIDs
parameter_list|,
name|int
name|count
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|scratchPackedValue
operator|.
name|length
operator|==
name|packedBytesLength
assert|;
name|BytesRefBuilder
name|scratch
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
assert|assert
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|BLOCK_VALUE
argument_list|)
assert|;
name|BytesRef
name|br
init|=
name|SimpleTextUtil
operator|.
name|fromBytesRefString
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|BLOCK_VALUE
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|br
operator|.
name|length
operator|==
name|packedBytesLength
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|br
operator|.
name|bytes
argument_list|,
name|br
operator|.
name|offset
argument_list|,
name|scratchPackedValue
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|docIDs
index|[
name|i
index|]
argument_list|,
name|scratchPackedValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseInt
specifier|private
name|int
name|parseInt
parameter_list|(
name|BytesRefBuilder
name|scratch
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
assert|assert
name|startsWith
argument_list|(
name|scratch
argument_list|,
name|prefix
argument_list|)
assert|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|stripPrefix
argument_list|(
name|scratch
argument_list|,
name|prefix
argument_list|)
argument_list|)
return|;
block|}
DECL|method|stripPrefix
specifier|private
name|String
name|stripPrefix
parameter_list|(
name|BytesRefBuilder
name|scratch
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|scratch
operator|.
name|bytes
argument_list|()
argument_list|,
name|prefix
operator|.
name|length
argument_list|,
name|scratch
operator|.
name|length
argument_list|()
operator|-
name|prefix
operator|.
name|length
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
DECL|method|startsWith
specifier|private
name|boolean
name|startsWith
parameter_list|(
name|BytesRefBuilder
name|scratch
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
name|StringHelper
operator|.
name|startsWith
argument_list|(
name|scratch
operator|.
name|get
argument_list|()
argument_list|,
name|prefix
argument_list|)
return|;
block|}
DECL|method|readLine
specifier|private
name|void
name|readLine
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|BytesRefBuilder
name|scratch
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleTextUtil
operator|.
name|readLine
argument_list|(
name|in
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
