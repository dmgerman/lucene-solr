begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
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
name|CharTermAttribute
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
name|TypeAttribute
import|;
end_import

begin_comment
comment|/** Normalizes tokens extracted with {@link ClassicTokenizer}. */
end_comment

begin_class
DECL|class|ClassicFilter
specifier|public
class|class
name|ClassicFilter
extends|extends
name|TokenFilter
block|{
comment|/** Construct filtering<i>in</i>. */
DECL|method|ClassicFilter
specifier|public
name|ClassicFilter
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
block|}
DECL|field|APOSTROPHE_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|APOSTROPHE_TYPE
init|=
name|ClassicTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|ClassicTokenizer
operator|.
name|APOSTROPHE
index|]
decl_stmt|;
DECL|field|ACRONYM_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|ACRONYM_TYPE
init|=
name|ClassicTokenizer
operator|.
name|TOKEN_TYPES
index|[
name|ClassicTokenizer
operator|.
name|ACRONYM
index|]
decl_stmt|;
comment|// this filters uses attribute type
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Returns the next token in the stream, or null at EOS.    *<p>Removes<tt>'s</tt> from the end of words.    *<p>Removes dots from acronyms.    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bufferLength
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|typeAtt
operator|.
name|type
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|APOSTROPHE_TYPE
operator|&&
comment|// remove 's
name|bufferLength
operator|>=
literal|2
operator|&&
name|buffer
index|[
name|bufferLength
operator|-
literal|2
index|]
operator|==
literal|'\''
operator|&&
operator|(
name|buffer
index|[
name|bufferLength
operator|-
literal|1
index|]
operator|==
literal|'s'
operator|||
name|buffer
index|[
name|bufferLength
operator|-
literal|1
index|]
operator|==
literal|'S'
operator|)
condition|)
block|{
comment|// Strip last 2 characters off
name|termAtt
operator|.
name|setLength
argument_list|(
name|bufferLength
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ACRONYM_TYPE
condition|)
block|{
comment|// remove dots
name|int
name|upto
init|=
literal|0
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
name|bufferLength
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
if|if
condition|(
name|c
operator|!=
literal|'.'
condition|)
name|buffer
index|[
name|upto
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
name|termAtt
operator|.
name|setLength
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

