begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*   * This algorithm is updated based on code located at:  * http://members.unine.ch/jacques.savoy/clef/  *   * Full copyright for that code follows:  */
end_comment

begin_comment
comment|/*  * Copyright (c) 2005, Jacques Savoy  * All rights reserved.  *  * Redistribution and use in source and binary forms, with or without   * modification, are permitted provided that the following conditions are met:  *  * Redistributions of source code must retain the above copyright notice, this   * list of conditions and the following disclaimer. Redistributions in binary   * form must reproduce the above copyright notice, this list of conditions and  * the following disclaimer in the documentation and/or other materials   * provided with the distribution. Neither the name of the author nor the names   * of its contributors may be used to endorse or promote products derived from   * this software without specific prior written permission.  *   * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"   * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE   * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE   * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE   * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR   * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF   * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS   * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN   * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)   * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  * POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|StemmerUtil
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Light Stemmer for French.  *<p>  * This stemmer implements the "UniNE" algorithm in:  *<i>Light Stemming Approaches for the French, Portuguese, German and Hungarian Languages</i>  * Jacques Savoy  */
end_comment

begin_class
DECL|class|FrenchLightStemmer
specifier|public
class|class
name|FrenchLightStemmer
block|{
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'x'
condition|)
block|{
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|==
literal|'a'
operator|&&
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|==
literal|'u'
operator|&&
name|s
index|[
name|len
operator|-
literal|4
index|]
operator|!=
literal|'e'
condition|)
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'l'
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>
literal|3
operator|&&
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'x'
condition|)
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|3
operator|&&
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'s'
condition|)
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|9
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"issement"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|6
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"issant"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|4
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|6
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ement"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|4
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|3
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ive"
argument_list|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'f'
expr_stmt|;
block|}
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|11
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ficatrice"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|5
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|10
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ficateur"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|4
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|9
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"catrice"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|3
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|4
index|]
operator|=
literal|'q'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'u'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
comment|//s[len-1] = 'r'<-- unnecessary, already 'r'.
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"cateur"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|2
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|4
index|]
operator|=
literal|'q'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'u'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"atrice"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|4
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|7
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ateur"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|3
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|6
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"trice"
argument_list|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|3
index|]
operator|=
literal|'e'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'u'
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"iÃ¨me"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|4
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|7
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"teuse"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|2
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|6
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"teur"
argument_list|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'r'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"euse"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|2
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã¨re"
argument_list|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|7
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ive"
argument_list|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'f'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|4
operator|&&
operator|(
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"folle"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"molle"
argument_list|)
operator|)
condition|)
block|{
name|len
operator|-=
literal|2
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|=
literal|'u'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|9
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nnelle"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|5
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|9
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"nnel"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|3
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"Ã¨te"
argument_list|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ique"
argument_list|)
condition|)
name|len
operator|-=
literal|4
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"esse"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|3
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|7
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"inage"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|3
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|9
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"isation"
argument_list|)
condition|)
block|{
name|len
operator|-=
literal|7
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|5
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ual"
argument_list|)
condition|)
name|s
index|[
name|len
operator|-
literal|2
index|]
operator|=
literal|'e'
expr_stmt|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
if|if
condition|(
name|len
operator|>
literal|9
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"isateur"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|7
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ation"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|5
argument_list|)
return|;
if|if
condition|(
name|len
operator|>
literal|8
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ition"
argument_list|)
condition|)
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
operator|-
literal|5
argument_list|)
return|;
return|return
name|norm
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|norm
specifier|private
name|int
name|norm
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>
literal|4
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
switch|switch
condition|(
name|s
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'Ã '
case|:
case|case
literal|'Ã¡'
case|:
case|case
literal|'Ã¢'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'Ã´'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'Ã¨'
case|:
case|case
literal|'Ã©'
case|:
case|case
literal|'Ãª'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'Ã¹'
case|:
case|case
literal|'Ã»'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'Ã®'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'Ã§'
case|:
name|s
index|[
name|i
index|]
operator|=
literal|'c'
expr_stmt|;
break|break;
block|}
name|char
name|ch
init|=
name|s
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|s
index|[
name|i
index|]
operator|==
name|ch
condition|)
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
operator|--
argument_list|,
name|len
argument_list|)
expr_stmt|;
else|else
name|ch
operator|=
name|s
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|len
operator|>
literal|4
operator|&&
name|endsWith
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
literal|"ie"
argument_list|)
condition|)
name|len
operator|-=
literal|2
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|4
condition|)
block|{
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'r'
condition|)
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'e'
condition|)
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
literal|'e'
condition|)
name|len
operator|--
expr_stmt|;
if|if
condition|(
name|s
index|[
name|len
operator|-
literal|1
index|]
operator|==
name|s
index|[
name|len
operator|-
literal|2
index|]
condition|)
name|len
operator|--
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

