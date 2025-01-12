begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * CharsSequence with escaped chars information.  */
end_comment

begin_class
DECL|class|UnescapedCharSequence
specifier|public
specifier|final
class|class
name|UnescapedCharSequence
implements|implements
name|CharSequence
block|{
DECL|field|chars
specifier|private
name|char
index|[]
name|chars
decl_stmt|;
DECL|field|wasEscaped
specifier|private
name|boolean
index|[]
name|wasEscaped
decl_stmt|;
comment|/**    * Create a escaped CharSequence    */
DECL|method|UnescapedCharSequence
specifier|public
name|UnescapedCharSequence
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|boolean
index|[]
name|wasEscaped
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|chars
operator|=
operator|new
name|char
index|[
name|length
index|]
expr_stmt|;
name|this
operator|.
name|wasEscaped
operator|=
operator|new
name|boolean
index|[
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|this
operator|.
name|chars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|wasEscaped
argument_list|,
name|offset
argument_list|,
name|this
operator|.
name|wasEscaped
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a non-escaped CharSequence    */
DECL|method|UnescapedCharSequence
specifier|public
name|UnescapedCharSequence
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|this
operator|.
name|chars
operator|=
operator|new
name|char
index|[
name|text
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|wasEscaped
operator|=
operator|new
name|boolean
index|[
name|text
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|chars
index|[
name|i
index|]
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|this
operator|.
name|wasEscaped
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * Create a copy of an existent UnescapedCharSequence    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|UnescapedCharSequence
specifier|private
name|UnescapedCharSequence
parameter_list|(
name|UnescapedCharSequence
name|text
parameter_list|)
block|{
name|this
operator|.
name|chars
operator|=
operator|new
name|char
index|[
name|text
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|wasEscaped
operator|=
operator|new
name|boolean
index|[
name|text
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|chars
index|[
name|i
index|]
operator|=
name|text
operator|.
name|chars
index|[
name|i
index|]
expr_stmt|;
name|this
operator|.
name|wasEscaped
index|[
name|i
index|]
operator|=
name|text
operator|.
name|wasEscaped
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|this
operator|.
name|chars
index|[
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|chars
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|subSequence
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|int
name|newLength
init|=
name|end
operator|-
name|start
decl_stmt|;
return|return
operator|new
name|UnescapedCharSequence
argument_list|(
name|this
operator|.
name|chars
argument_list|,
name|this
operator|.
name|wasEscaped
argument_list|,
name|start
argument_list|,
name|newLength
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|this
operator|.
name|chars
argument_list|)
return|;
block|}
comment|/**    * Return a escaped String    *     * @return a escaped String    */
DECL|method|toStringEscaped
specifier|public
name|String
name|toStringEscaped
parameter_list|()
block|{
comment|// non efficient implementation
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
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
operator|>=
name|this
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|this
operator|.
name|chars
index|[
name|i
index|]
operator|==
literal|'\\'
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|wasEscaped
index|[
name|i
index|]
condition|)
name|result
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|this
operator|.
name|chars
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Return a escaped String    *     * @param enabledChars    *          - array of chars to be escaped    * @return a escaped String    */
DECL|method|toStringEscaped
specifier|public
name|String
name|toStringEscaped
parameter_list|(
name|char
index|[]
name|enabledChars
parameter_list|)
block|{
comment|// TODO: non efficient implementation, refactor this code
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
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
name|this
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|this
operator|.
name|chars
index|[
name|i
index|]
operator|==
literal|'\\'
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|char
name|character
range|:
name|enabledChars
control|)
block|{
if|if
condition|(
name|this
operator|.
name|chars
index|[
name|i
index|]
operator|==
name|character
operator|&&
name|this
operator|.
name|wasEscaped
index|[
name|i
index|]
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|result
operator|.
name|append
argument_list|(
name|this
operator|.
name|chars
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|wasEscaped
specifier|public
name|boolean
name|wasEscaped
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|this
operator|.
name|wasEscaped
index|[
name|index
index|]
return|;
block|}
DECL|method|wasEscaped
specifier|static
specifier|final
specifier|public
name|boolean
name|wasEscaped
parameter_list|(
name|CharSequence
name|text
parameter_list|,
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|text
operator|instanceof
name|UnescapedCharSequence
condition|)
return|return
operator|(
operator|(
name|UnescapedCharSequence
operator|)
name|text
operator|)
operator|.
name|wasEscaped
index|[
name|index
index|]
return|;
else|else
return|return
literal|false
return|;
block|}
DECL|method|toLowerCase
specifier|public
specifier|static
name|CharSequence
name|toLowerCase
parameter_list|(
name|CharSequence
name|text
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
if|if
condition|(
name|text
operator|instanceof
name|UnescapedCharSequence
condition|)
block|{
name|char
index|[]
name|chars
init|=
name|text
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|locale
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|boolean
index|[]
name|wasEscaped
init|=
operator|(
operator|(
name|UnescapedCharSequence
operator|)
name|text
operator|)
operator|.
name|wasEscaped
decl_stmt|;
return|return
operator|new
name|UnescapedCharSequence
argument_list|(
name|chars
argument_list|,
name|wasEscaped
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
return|;
block|}
else|else
return|return
operator|new
name|UnescapedCharSequence
argument_list|(
name|text
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|locale
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

