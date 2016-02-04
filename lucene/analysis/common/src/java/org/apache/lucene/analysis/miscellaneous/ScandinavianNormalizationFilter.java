begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|util
operator|.
name|StemmerUtil
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
comment|/**  * This filter normalize use of the interchangeable Scandinavian characters Ã¦ÃÃ¤ÃÃ¶ÃÃ¸Ã  * and folded variants (aa, ao, ae, oe and oo) by transforming them to Ã¥ÃÃ¦ÃÃ¸Ã.  *<p>  * It's a semantically less destructive solution than {@link ScandinavianFoldingFilter},  * most useful when a person with a Norwegian or Danish keyboard queries a Swedish index  * and vice versa. This filter does<b>not</b>  the common Swedish folds of Ã¥ and Ã¤ to a nor Ã¶ to o.  *<p>  * blÃ¥bÃ¦rsyltetÃ¸j == blÃ¥bÃ¤rsyltetÃ¶j == blaabaarsyltetoej but not blabarsyltetoj  * rÃ¤ksmÃ¶rgÃ¥s == rÃ¦ksmÃ¸rgÃ¥s == rÃ¦ksmÃ¶rgaos == raeksmoergaas but not raksmorgas  * @see ScandinavianFoldingFilter  */
end_comment

begin_class
DECL|class|ScandinavianNormalizationFilter
specifier|public
specifier|final
class|class
name|ScandinavianNormalizationFilter
extends|extends
name|TokenFilter
block|{
DECL|method|ScandinavianNormalizationFilter
specifier|public
name|ScandinavianNormalizationFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|field|charTermAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|charTermAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|AA
specifier|private
specifier|static
specifier|final
name|char
name|AA
init|=
literal|'\u00C5'
decl_stmt|;
comment|// Ã
DECL|field|aa
specifier|private
specifier|static
specifier|final
name|char
name|aa
init|=
literal|'\u00E5'
decl_stmt|;
comment|// Ã¥
DECL|field|AE
specifier|private
specifier|static
specifier|final
name|char
name|AE
init|=
literal|'\u00C6'
decl_stmt|;
comment|// Ã
DECL|field|ae
specifier|private
specifier|static
specifier|final
name|char
name|ae
init|=
literal|'\u00E6'
decl_stmt|;
comment|// Ã¦
DECL|field|AE_se
specifier|private
specifier|static
specifier|final
name|char
name|AE_se
init|=
literal|'\u00C4'
decl_stmt|;
comment|// Ã
DECL|field|ae_se
specifier|private
specifier|static
specifier|final
name|char
name|ae_se
init|=
literal|'\u00E4'
decl_stmt|;
comment|// Ã¤
DECL|field|OE
specifier|private
specifier|static
specifier|final
name|char
name|OE
init|=
literal|'\u00D8'
decl_stmt|;
comment|// Ã
DECL|field|oe
specifier|private
specifier|static
specifier|final
name|char
name|oe
init|=
literal|'\u00F8'
decl_stmt|;
comment|// Ã¸
DECL|field|OE_se
specifier|private
specifier|static
specifier|final
name|char
name|OE_se
init|=
literal|'\u00D6'
decl_stmt|;
comment|// Ã
DECL|field|oe_se
specifier|private
specifier|static
specifier|final
name|char
name|oe_se
init|=
literal|'\u00F6'
decl_stmt|;
comment|//Ã¶
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
name|char
index|[]
name|buffer
init|=
name|charTermAttribute
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|charTermAttribute
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
name|ae_se
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|ae
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
name|AE_se
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|AE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
name|oe_se
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|oe
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
name|OE_se
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|OE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|-
literal|1
operator|>
name|i
condition|)
block|{
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
literal|'a'
operator|&&
operator|(
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'a'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'o'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'A'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'O'
operator|)
condition|)
block|{
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|aa
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
literal|'A'
operator|&&
operator|(
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'a'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'A'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'o'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'O'
operator|)
condition|)
block|{
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|AA
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
literal|'a'
operator|&&
operator|(
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'e'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'E'
operator|)
condition|)
block|{
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|ae
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
literal|'A'
operator|&&
operator|(
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'e'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'E'
operator|)
condition|)
block|{
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|AE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
literal|'o'
operator|&&
operator|(
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'e'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'E'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'o'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'O'
operator|)
condition|)
block|{
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|oe
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
literal|'O'
operator|&&
operator|(
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'e'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'E'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'o'
operator|||
name|buffer
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'O'
operator|)
condition|)
block|{
name|length
operator|=
name|StemmerUtil
operator|.
name|delete
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|OE
expr_stmt|;
block|}
block|}
block|}
name|charTermAttribute
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

