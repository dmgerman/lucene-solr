begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ga
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ga
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Normalises token text to lower case, handling t-prothesis  * and n-eclipsis (i.e., that 'nAthair' should become 'n-athair')  */
end_comment

begin_class
DECL|class|IrishLowerCaseFilter
specifier|public
specifier|final
class|class
name|IrishLowerCaseFilter
extends|extends
name|TokenFilter
block|{
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
comment|/**    * Create an IrishLowerCaseFilter that normalises Irish token text.    */
DECL|method|IrishLowerCaseFilter
specifier|public
name|IrishLowerCaseFilter
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
annotation|@
name|Override
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
name|char
index|[]
name|chArray
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|chLen
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|chLen
operator|>
literal|1
operator|&&
operator|(
name|chArray
index|[
literal|0
index|]
operator|==
literal|'n'
operator|||
name|chArray
index|[
literal|0
index|]
operator|==
literal|'t'
operator|)
operator|&&
name|isUpperVowel
argument_list|(
name|chArray
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
name|chArray
operator|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
name|chLen
operator|+
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|chLen
init|;
name|i
operator|>
literal|1
condition|;
name|i
operator|--
control|)
block|{
name|chArray
index|[
name|i
index|]
operator|=
name|chArray
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|chArray
index|[
literal|1
index|]
operator|=
literal|'-'
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|chLen
operator|+
literal|1
argument_list|)
expr_stmt|;
name|idx
operator|=
literal|2
expr_stmt|;
name|chLen
operator|=
name|chLen
operator|+
literal|1
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|idx
init|;
name|i
operator|<
name|chLen
condition|;
control|)
block|{
name|i
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|chArray
index|[
name|i
index|]
argument_list|)
argument_list|,
name|chArray
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
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
DECL|method|isUpperVowel
specifier|private
name|boolean
name|isUpperVowel
parameter_list|(
name|int
name|v
parameter_list|)
block|{
switch|switch
condition|(
name|v
condition|)
block|{
case|case
literal|'A'
case|:
case|case
literal|'E'
case|:
case|case
literal|'I'
case|:
case|case
literal|'O'
case|:
case|case
literal|'U'
case|:
comment|// vowels with acute accent (fada)
case|case
literal|'\u00c1'
case|:
case|case
literal|'\u00c9'
case|:
case|case
literal|'\u00cd'
case|:
case|case
literal|'\u00d3'
case|:
case|case
literal|'\u00da'
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

