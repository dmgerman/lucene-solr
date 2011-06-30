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
name|LuceneTestCase
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
name|*
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
name|codecs
operator|.
name|sep
operator|.
name|*
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
name|codecs
operator|.
name|mockintblock
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestIntBlockCodec
specifier|public
class|class
name|TestIntBlockCodec
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimpleIntBlocks
specifier|public
name|void
name|testSimpleIntBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IntStreamFactory
name|f
init|=
operator|new
name|MockFixedIntBlockCodec
argument_list|(
literal|128
argument_list|)
operator|.
name|getIntFactory
argument_list|()
decl_stmt|;
name|IntIndexOutput
name|out
init|=
name|f
operator|.
name|createOutput
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
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
literal|11777
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IntIndexInput
name|in
init|=
name|f
operator|.
name|openInput
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|IntIndexInput
operator|.
name|Reader
name|r
init|=
name|in
operator|.
name|reader
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
literal|11777
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|r
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmptySimpleIntBlocks
specifier|public
name|void
name|testEmptySimpleIntBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IntStreamFactory
name|f
init|=
operator|new
name|MockFixedIntBlockCodec
argument_list|(
literal|128
argument_list|)
operator|.
name|getIntFactory
argument_list|()
decl_stmt|;
name|IntIndexOutput
name|out
init|=
name|f
operator|.
name|createOutput
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
comment|// write no ints
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IntIndexInput
name|in
init|=
name|f
operator|.
name|openInput
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|.
name|reader
argument_list|()
expr_stmt|;
comment|// read no ints
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

