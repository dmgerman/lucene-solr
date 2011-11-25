begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_class
DECL|class|TestByteBlockPool
specifier|public
class|class
name|TestByteBlockPool
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCopyRefAndWrite
specifier|public
name|void
name|testCopyRefAndWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|maxLength
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|ByteBlockPool
name|pool
init|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectAllocator
argument_list|()
argument_list|)
decl_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numValues
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|value
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|ref
operator|.
name|copyChars
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|pool
operator|.
name|copy
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|stream
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo.txt"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|pool
operator|.
name|writePool
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"foo.txt"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pool
operator|.
name|byteOffset
operator|+
name|pool
operator|.
name|byteUpto
argument_list|,
name|stream
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|list
control|)
block|{
name|expected
operator|.
name|copyChars
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|actual
operator|.
name|grow
argument_list|(
name|expected
operator|.
name|length
argument_list|)
expr_stmt|;
name|actual
operator|.
name|length
operator|=
name|expected
operator|.
name|length
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|actual
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|input
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be EOF"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected - read past EOF
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

