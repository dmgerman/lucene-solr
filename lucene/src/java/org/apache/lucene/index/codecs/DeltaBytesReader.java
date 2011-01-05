begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|// Handles reading incremental UTF8 encoded terms
end_comment

begin_class
DECL|class|DeltaBytesReader
specifier|final
class|class
name|DeltaBytesReader
block|{
DECL|field|term
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|method|DeltaBytesReader
name|DeltaBytesReader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|term
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
literal|10
index|]
expr_stmt|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|(
name|BytesRef
name|text
parameter_list|)
block|{
name|term
operator|.
name|copy
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
DECL|method|read
name|boolean
name|read
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|start
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|==
name|DeltaBytesWriter
operator|.
name|TERM_EOF
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|suffix
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|start
operator|<=
name|term
operator|.
name|length
operator|:
literal|"start="
operator|+
name|start
operator|+
literal|" length="
operator|+
name|term
operator|.
name|length
assert|;
specifier|final
name|int
name|newLength
init|=
name|start
operator|+
name|suffix
decl_stmt|;
name|term
operator|.
name|grow
argument_list|(
name|newLength
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|start
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|term
operator|.
name|length
operator|=
name|newLength
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

