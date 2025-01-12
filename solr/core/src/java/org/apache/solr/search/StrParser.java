begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
comment|/**    * Simple class to help with parsing a string.    *<b>Note: This API is experimental and may change in non backward-compatible ways in the future</b>    */
end_comment

begin_class
DECL|class|StrParser
specifier|public
class|class
name|StrParser
block|{
DECL|field|val
specifier|public
name|String
name|val
decl_stmt|;
DECL|field|pos
specifier|public
name|int
name|pos
decl_stmt|;
DECL|field|end
specifier|public
name|int
name|end
decl_stmt|;
DECL|method|StrParser
specifier|public
name|StrParser
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|this
argument_list|(
name|val
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|StrParser
specifier|public
name|StrParser
parameter_list|(
name|String
name|val
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
DECL|method|eatws
specifier|public
name|void
name|eatws
parameter_list|()
block|{
while|while
condition|(
name|pos
operator|<
name|end
operator|&&
name|Character
operator|.
name|isWhitespace
argument_list|(
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
argument_list|)
condition|)
name|pos
operator|++
expr_stmt|;
block|}
DECL|method|ch
specifier|public
name|char
name|ch
parameter_list|()
block|{
return|return
name|pos
operator|<
name|end
condition|?
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
else|:
literal|0
return|;
block|}
DECL|method|skip
specifier|public
name|void
name|skip
parameter_list|(
name|int
name|nChars
parameter_list|)
block|{
name|pos
operator|=
name|Math
operator|.
name|max
argument_list|(
name|pos
operator|+
name|nChars
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
DECL|method|opt
specifier|public
name|boolean
name|opt
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|slen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|regionMatches
argument_list|(
name|pos
argument_list|,
name|s
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|)
condition|)
block|{
name|pos
operator|+=
name|slen
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|opt
specifier|public
name|boolean
name|opt
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
name|eatws
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|<
name|end
operator|&&
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
operator|==
name|ch
condition|)
block|{
name|pos
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|expect
specifier|public
name|void
name|expect
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|slen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|regionMatches
argument_list|(
name|pos
argument_list|,
name|s
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|)
condition|)
block|{
name|pos
operator|+=
name|slen
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"Expected '"
operator|+
name|s
operator|+
literal|"' at position "
operator|+
name|pos
operator|+
literal|" in '"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|end
operator|-
name|pos
index|]
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|)
operator|||
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
operator|||
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'e'
operator|||
name|ch
operator|==
literal|'E'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
operator|new
name|String
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getNumber
specifier|public
name|Number
name|getNumber
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|start
init|=
name|pos
decl_stmt|;
name|boolean
name|flt
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|)
operator|||
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'e'
operator|||
name|ch
operator|==
literal|'E'
condition|)
block|{
name|flt
operator|=
literal|true
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|String
name|v
init|=
name|val
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|flt
condition|)
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|v
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|v
argument_list|)
return|;
block|}
block|}
DECL|method|getDouble
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|end
operator|-
name|pos
index|]
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|)
operator|||
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
operator|||
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'e'
operator|||
name|ch
operator|==
literal|'E'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
operator|new
name|String
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|end
operator|-
name|pos
index|]
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|)
operator|||
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
operator|new
name|String
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
throws|throws
name|SyntaxError
block|{
return|return
name|getId
argument_list|(
literal|"Expected identifier"
argument_list|)
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|(
name|String
name|errMessage
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|id_start
init|=
name|pos
decl_stmt|;
name|char
name|ch
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|end
operator|&&
operator|(
name|ch
operator|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
operator|)
operator|!=
literal|'$'
operator|&&
name|Character
operator|.
name|isJavaIdentifierStart
argument_list|(
name|ch
argument_list|)
condition|)
block|{
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|ch
operator|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
comment|//          if (!Character.isJavaIdentifierPart(ch)&& ch != '.'&& ch != ':') {
if|if
condition|(
operator|!
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|ch
argument_list|)
operator|&&
name|ch
operator|!=
literal|'.'
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|val
operator|.
name|substring
argument_list|(
name|id_start
argument_list|,
name|pos
argument_list|)
return|;
block|}
if|if
condition|(
name|errMessage
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
name|errMessage
operator|+
literal|" at pos "
operator|+
name|pos
operator|+
literal|" str='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getGlobbedId
specifier|public
name|String
name|getGlobbedId
parameter_list|(
name|String
name|errMessage
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|id_start
init|=
name|pos
decl_stmt|;
name|char
name|ch
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|end
operator|&&
operator|(
name|ch
operator|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
operator|)
operator|!=
literal|'$'
operator|&&
operator|(
name|Character
operator|.
name|isJavaIdentifierStart
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|'?'
operator|||
name|ch
operator|==
literal|'*'
operator|)
condition|)
block|{
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|ch
operator|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|'?'
operator|||
name|ch
operator|==
literal|'*'
operator|)
operator|&&
name|ch
operator|!=
literal|'.'
condition|)
block|{
break|break;
block|}
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|val
operator|.
name|substring
argument_list|(
name|id_start
argument_list|,
name|pos
argument_list|)
return|;
block|}
if|if
condition|(
name|errMessage
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
name|errMessage
operator|+
literal|" at pos "
operator|+
name|pos
operator|+
literal|" str='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Skips leading whitespace and returns whatever sequence of non       * whitespace it can find (or hte empty string)      */
DECL|method|getSimpleString
specifier|public
name|String
name|getSimpleString
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|startPos
init|=
name|pos
decl_stmt|;
name|char
name|ch
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|ch
operator|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|ch
argument_list|)
condition|)
break|break;
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|val
operator|.
name|substring
argument_list|(
name|startPos
argument_list|,
name|pos
argument_list|)
return|;
block|}
comment|/**      * Sort direction or null if current position does not indicate a       * sort direction. (True is desc, False is asc).        * Position is advanced to after the comma (or end) when result is non null       */
DECL|method|getSortDirection
specifier|public
name|Boolean
name|getSortDirection
parameter_list|()
throws|throws
name|SyntaxError
block|{
specifier|final
name|int
name|startPos
init|=
name|pos
decl_stmt|;
specifier|final
name|String
name|order
init|=
name|getId
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Boolean
name|top
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|order
condition|)
block|{
specifier|final
name|String
name|orderLowerCase
init|=
name|order
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"desc"
operator|.
name|equals
argument_list|(
name|orderLowerCase
argument_list|)
operator|||
literal|"top"
operator|.
name|equals
argument_list|(
name|orderLowerCase
argument_list|)
condition|)
block|{
name|top
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"asc"
operator|.
name|equals
argument_list|(
name|orderLowerCase
argument_list|)
operator|||
literal|"bottom"
operator|.
name|equals
argument_list|(
name|orderLowerCase
argument_list|)
condition|)
block|{
name|top
operator|=
literal|false
expr_stmt|;
block|}
comment|// it's not a legal direction if more stuff comes after it
name|eatws
argument_list|()
expr_stmt|;
specifier|final
name|char
name|c
init|=
name|ch
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|c
condition|)
block|{
comment|// :NOOP
block|}
elseif|else
if|if
condition|(
literal|','
operator|==
name|c
condition|)
block|{
name|pos
operator|++
expr_stmt|;
block|}
else|else
block|{
name|top
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|top
condition|)
name|pos
operator|=
name|startPos
expr_stmt|;
comment|// no direction, reset
return|return
name|top
return|;
block|}
comment|// return null if not a string
DECL|method|getQuotedString
specifier|public
name|String
name|getQuotedString
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|eatws
argument_list|()
expr_stmt|;
name|char
name|delim
init|=
name|peekChar
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|delim
operator|==
literal|'\"'
operator|||
name|delim
operator|==
literal|'\''
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|val_start
init|=
operator|++
name|pos
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// needed for escaping
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"Missing end quote for string at pos "
operator|+
operator|(
name|val_start
operator|-
literal|1
operator|)
operator|+
literal|" str='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\\'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
if|if
condition|(
name|pos
operator|>=
name|end
condition|)
break|break;
name|ch
operator|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'n'
case|:
name|ch
operator|=
literal|'\n'
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|ch
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|ch
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|ch
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|ch
operator|=
literal|'\f'
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
if|if
condition|(
name|pos
operator|+
literal|4
operator|>=
name|end
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"bad unicode escape \\uxxxx at pos"
operator|+
operator|(
name|val_start
operator|-
literal|1
operator|)
operator|+
literal|" str='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|ch
operator|=
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|,
name|pos
operator|+
literal|5
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|pos
operator|+=
literal|4
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
name|delim
condition|)
block|{
name|pos
operator|++
expr_stmt|;
comment|// skip over the quote
break|break;
block|}
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// next non-whitespace char
DECL|method|peek
specifier|public
name|char
name|peek
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
return|return
name|pos
operator|<
name|end
condition|?
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
else|:
literal|0
return|;
block|}
comment|// next char
DECL|method|peekChar
specifier|public
name|char
name|peekChar
parameter_list|()
block|{
return|return
name|pos
operator|<
name|end
condition|?
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
else|:
literal|0
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
literal|"'"
operator|+
name|val
operator|+
literal|"'"
operator|+
literal|", pos="
operator|+
name|pos
return|;
block|}
block|}
end_class

end_unit

