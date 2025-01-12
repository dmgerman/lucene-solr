begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
package|;
end_package

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

begin_comment
comment|/**  * A helper to parse the context of a variable name, which is the base variable, followed by the  * sequence of array (integer or string indexed) and member accesses.  */
end_comment

begin_class
DECL|class|VariableContext
specifier|public
class|class
name|VariableContext
block|{
comment|/**    * Represents what a piece of a variable does.    */
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
comment|/**      * A member of the previous context (ie "dot" access).      */
DECL|enum constant|MEMBER
name|MEMBER
block|,
comment|/**      * Brackets containing a string as the "index".      */
DECL|enum constant|STR_INDEX
name|STR_INDEX
block|,
comment|/**      * Brackets containing an integer index (ie an array).      */
DECL|enum constant|INT_INDEX
name|INT_INDEX
block|,
comment|/**      * Parenthesis represent a member method to be called.      */
DECL|enum constant|METHOD
name|METHOD
block|}
comment|/**    * The type of this piece of a variable.    */
DECL|field|type
specifier|public
specifier|final
name|Type
name|type
decl_stmt|;
comment|/**    * The text of this piece of the variable. Used for {@link Type#MEMBER} and {@link Type#STR_INDEX} types.    */
DECL|field|text
specifier|public
specifier|final
name|String
name|text
decl_stmt|;
comment|/**    * The integer value for this piece of the variable. Used for {@link Type#INT_INDEX}.    */
DECL|field|integer
specifier|public
specifier|final
name|int
name|integer
decl_stmt|;
DECL|method|VariableContext
specifier|private
name|VariableContext
parameter_list|(
name|Type
name|c
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|type
operator|=
name|c
expr_stmt|;
name|text
operator|=
name|s
expr_stmt|;
name|integer
operator|=
name|i
expr_stmt|;
block|}
comment|/**    * Parses a normalized javascript variable. All strings in the variable should be single quoted,    * and no spaces (except possibly within strings).    */
DECL|method|parse
specifier|public
specifier|static
specifier|final
name|VariableContext
index|[]
name|parse
parameter_list|(
name|String
name|variable
parameter_list|)
block|{
name|char
index|[]
name|text
init|=
name|variable
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|VariableContext
argument_list|>
name|contexts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|addMember
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|contexts
argument_list|)
decl_stmt|;
comment|// base variable is a "member" of the global namespace
while|while
condition|(
name|i
operator|<
name|text
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|text
index|[
name|i
index|]
operator|==
literal|'['
condition|)
block|{
if|if
condition|(
name|text
index|[
operator|++
name|i
index|]
operator|==
literal|'\''
condition|)
block|{
name|i
operator|=
name|addStringIndex
argument_list|(
name|text
argument_list|,
name|i
argument_list|,
name|contexts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|i
operator|=
name|addIntIndex
argument_list|(
name|text
argument_list|,
name|i
argument_list|,
name|contexts
argument_list|)
expr_stmt|;
block|}
operator|++
name|i
expr_stmt|;
comment|// move past end bracket
block|}
else|else
block|{
comment|// text[i] == '.', ie object member
name|i
operator|=
name|addMember
argument_list|(
name|text
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|contexts
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|contexts
operator|.
name|toArray
argument_list|(
operator|new
name|VariableContext
index|[
name|contexts
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|// i points to start of member name
DECL|method|addMember
specifier|private
specifier|static
name|int
name|addMember
parameter_list|(
specifier|final
name|char
index|[]
name|text
parameter_list|,
name|int
name|i
parameter_list|,
name|List
argument_list|<
name|VariableContext
argument_list|>
name|contexts
parameter_list|)
block|{
name|int
name|j
init|=
name|i
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|text
operator|.
name|length
operator|&&
name|text
index|[
name|j
index|]
operator|!=
literal|'['
operator|&&
name|text
index|[
name|j
index|]
operator|!=
literal|'.'
operator|&&
name|text
index|[
name|j
index|]
operator|!=
literal|'('
condition|)
operator|++
name|j
expr_stmt|;
comment|// find first array, member access, or method call
if|if
condition|(
name|j
operator|+
literal|1
operator|<
name|text
operator|.
name|length
operator|&&
name|text
index|[
name|j
index|]
operator|==
literal|'('
operator|&&
name|text
index|[
name|j
operator|+
literal|1
index|]
operator|==
literal|')'
condition|)
block|{
name|contexts
operator|.
name|add
argument_list|(
operator|new
name|VariableContext
argument_list|(
name|Type
operator|.
name|METHOD
argument_list|,
operator|new
name|String
argument_list|(
name|text
argument_list|,
name|i
argument_list|,
name|j
operator|-
name|i
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|j
operator|+=
literal|2
expr_stmt|;
comment|//move past the parenthesis
block|}
else|else
block|{
name|contexts
operator|.
name|add
argument_list|(
operator|new
name|VariableContext
argument_list|(
name|Type
operator|.
name|MEMBER
argument_list|,
operator|new
name|String
argument_list|(
name|text
argument_list|,
name|i
argument_list|,
name|j
operator|-
name|i
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|j
return|;
block|}
comment|// i points to start of single quoted index
DECL|method|addStringIndex
specifier|private
specifier|static
name|int
name|addStringIndex
parameter_list|(
specifier|final
name|char
index|[]
name|text
parameter_list|,
name|int
name|i
parameter_list|,
name|List
argument_list|<
name|VariableContext
argument_list|>
name|contexts
parameter_list|)
block|{
operator|++
name|i
expr_stmt|;
comment|// move past quote
name|int
name|j
init|=
name|i
decl_stmt|;
while|while
condition|(
name|text
index|[
name|j
index|]
operator|!=
literal|'\''
condition|)
block|{
comment|// find end of single quoted string
if|if
condition|(
name|text
index|[
name|j
index|]
operator|==
literal|'\\'
condition|)
operator|++
name|j
expr_stmt|;
comment|// skip over escapes
operator|++
name|j
expr_stmt|;
block|}
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|j
operator|-
name|i
argument_list|)
decl_stmt|;
comment|// space for string, without end quote
while|while
condition|(
name|i
operator|<
name|j
condition|)
block|{
comment|// copy string to buffer (without begin/end quotes)
if|if
condition|(
name|text
index|[
name|i
index|]
operator|==
literal|'\\'
condition|)
operator|++
name|i
expr_stmt|;
comment|// unescape escapes
name|buf
operator|.
name|append
argument_list|(
name|text
index|[
name|i
index|]
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
name|contexts
operator|.
name|add
argument_list|(
operator|new
name|VariableContext
argument_list|(
name|Type
operator|.
name|STR_INDEX
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|j
operator|+
literal|1
return|;
comment|// move past quote, return end bracket location
block|}
comment|// i points to start of integer index
DECL|method|addIntIndex
specifier|private
specifier|static
name|int
name|addIntIndex
parameter_list|(
specifier|final
name|char
index|[]
name|text
parameter_list|,
name|int
name|i
parameter_list|,
name|List
argument_list|<
name|VariableContext
argument_list|>
name|contexts
parameter_list|)
block|{
name|int
name|j
init|=
name|i
operator|+
literal|1
decl_stmt|;
while|while
condition|(
name|text
index|[
name|j
index|]
operator|!=
literal|']'
condition|)
operator|++
name|j
expr_stmt|;
comment|// find end of array access
name|int
name|index
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
operator|new
name|String
argument_list|(
name|text
argument_list|,
name|i
argument_list|,
name|j
operator|-
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|contexts
operator|.
name|add
argument_list|(
operator|new
name|VariableContext
argument_list|(
name|Type
operator|.
name|INT_INDEX
argument_list|,
literal|null
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|j
return|;
block|}
block|}
end_class

end_unit

