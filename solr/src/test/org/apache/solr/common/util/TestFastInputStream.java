begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
import|;
end_import

begin_comment
comment|/**  * Test for FastInputStream.  *  * @version $Id$  * @see org.apache.solr.common.util.FastInputStream  */
end_comment

begin_class
DECL|class|TestFastInputStream
specifier|public
class|class
name|TestFastInputStream
block|{
annotation|@
name|Test
DECL|method|testgzip
specifier|public
name|void
name|testgzip
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|b
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|FastOutputStream
name|fos
init|=
operator|new
name|FastOutputStream
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|GZIPOutputStream
name|gzos
init|=
operator|new
name|GZIPOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|String
name|ss
init|=
literal|"Helloooooooooooooooooooo"
decl_stmt|;
name|writeChars
argument_list|(
name|gzos
argument_list|,
name|ss
argument_list|,
literal|0
argument_list|,
name|ss
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|gzos
operator|.
name|close
argument_list|()
expr_stmt|;
name|NamedListCodec
operator|.
name|writeVInt
argument_list|(
literal|10
argument_list|,
name|fos
argument_list|)
expr_stmt|;
name|fos
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
name|GZIPInputStream
name|gzis
init|=
operator|new
name|GZIPInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|b
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
name|ss
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|readChars
argument_list|(
name|gzis
argument_list|,
name|cbuf
argument_list|,
literal|0
argument_list|,
name|ss
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|cbuf
argument_list|)
argument_list|,
name|ss
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"passes w/o FastInputStream"
argument_list|)
expr_stmt|;
name|ByteArrayInputStream
name|bis
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|b
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|gzis
operator|=
operator|new
name|GZIPInputStream
argument_list|(
operator|new
name|FastInputStream
argument_list|(
name|bis
argument_list|)
argument_list|)
expr_stmt|;
name|cbuf
operator|=
operator|new
name|char
index|[
name|ss
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
name|readChars
argument_list|(
name|gzis
argument_list|,
name|cbuf
argument_list|,
literal|0
argument_list|,
name|ss
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
name|cbuf
argument_list|)
argument_list|,
name|ss
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"passes w FastInputStream"
argument_list|)
expr_stmt|;
block|}
comment|//code copied from NamedListCodec#readChars
DECL|method|readChars
specifier|public
specifier|static
name|void
name|readChars
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|b
expr_stmt|;
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0xE0
operator|)
operator|!=
literal|0xE0
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|in
operator|.
name|read
argument_list|()
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// code copied rfrom NamedlistCode#writechars
DECL|method|writeChars
specifier|public
specifier|static
name|void
name|writeChars
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
name|os
operator|.
name|write
argument_list|(
name|code
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|os
operator|.
name|write
argument_list|(
literal|0xE0
operator||
operator|(
name|code
operator|>>>
literal|12
operator|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

