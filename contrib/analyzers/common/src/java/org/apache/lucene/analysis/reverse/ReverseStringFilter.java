begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.reverse
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|reverse
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermAttribute
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
comment|/**  * Reverse token string e.g. "country" => "yrtnuoc".  *  * @version $Id$  */
end_comment

begin_class
DECL|class|ReverseStringFilter
specifier|public
specifier|final
class|class
name|ReverseStringFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|ReverseStringFilter
specifier|public
name|ReverseStringFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|reverse
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|reverse
specifier|public
specifier|static
name|String
name|reverse
parameter_list|(
specifier|final
name|String
name|input
parameter_list|)
block|{
name|char
index|[]
name|charInput
init|=
name|input
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|reverse
argument_list|(
name|charInput
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|charInput
argument_list|)
return|;
block|}
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|)
block|{
name|reverse
argument_list|(
name|buffer
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<=
literal|1
condition|)
return|return;
name|int
name|num
init|=
name|len
operator|>>
literal|1
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
operator|(
name|start
operator|+
name|num
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|buffer
index|[
name|i
index|]
decl_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|buffer
index|[
name|start
operator|*
literal|2
operator|+
name|len
operator|-
name|i
operator|-
literal|1
index|]
expr_stmt|;
name|buffer
index|[
name|start
operator|*
literal|2
operator|+
name|len
operator|-
name|i
operator|-
literal|1
index|]
operator|=
name|c
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

