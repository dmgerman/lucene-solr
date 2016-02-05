begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.expr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
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
name|Arrays
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
comment|/**  * Takes a prefix notation expression and returns a tokenized expression  */
end_comment

begin_class
DECL|class|StreamExpressionParser
specifier|public
class|class
name|StreamExpressionParser
block|{
DECL|field|wordChars
specifier|static
name|char
index|[]
name|wordChars
init|=
block|{
literal|'_'
block|,
literal|'.'
block|,
literal|'-'
block|}
decl_stmt|;
static|static
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|wordChars
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|StreamExpression
name|parse
parameter_list|(
name|String
name|clause
parameter_list|)
block|{
name|StreamExpressionParameter
name|expr
init|=
name|generateStreamExpression
argument_list|(
name|clause
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|expr
operator|&&
name|expr
operator|instanceof
name|StreamExpression
condition|)
block|{
return|return
operator|(
name|StreamExpression
operator|)
name|expr
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|generateStreamExpression
specifier|private
specifier|static
name|StreamExpressionParameter
name|generateStreamExpression
parameter_list|(
name|String
name|clause
parameter_list|)
block|{
name|String
name|working
init|=
name|clause
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isExpressionClause
argument_list|(
name|working
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"'%s' is not a proper expression clause"
argument_list|,
name|working
argument_list|)
argument_list|)
throw|;
block|}
comment|// Get functionName
name|int
name|firstOpenParen
init|=
name|findNextClear
argument_list|(
name|working
argument_list|,
literal|0
argument_list|,
literal|'('
argument_list|)
decl_stmt|;
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|working
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|firstOpenParen
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
comment|// strip off functionName and ()
name|working
operator|=
name|working
operator|.
name|substring
argument_list|(
name|firstOpenParen
operator|+
literal|1
argument_list|,
name|working
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|splitOn
argument_list|(
name|working
argument_list|,
literal|','
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|parts
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|String
name|part
init|=
name|parts
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|isExpressionClause
argument_list|(
name|part
argument_list|)
condition|)
block|{
name|StreamExpressionParameter
name|parameter
init|=
name|generateStreamExpression
argument_list|(
name|part
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|parameter
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isNamedParameterClause
argument_list|(
name|part
argument_list|)
condition|)
block|{
name|StreamExpressionNamedParameter
name|parameter
init|=
name|generateNamedParameterExpression
argument_list|(
name|part
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|parameter
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionValue
argument_list|(
name|part
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|expression
return|;
block|}
DECL|method|generateNamedParameterExpression
specifier|private
specifier|static
name|StreamExpressionNamedParameter
name|generateNamedParameterExpression
parameter_list|(
name|String
name|clause
parameter_list|)
block|{
name|String
name|working
init|=
name|clause
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// might be overkill as the only place this is called from does this check already
if|if
condition|(
operator|!
name|isNamedParameterClause
argument_list|(
name|working
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"'%s' is not a proper named parameter clause"
argument_list|,
name|working
argument_list|)
argument_list|)
throw|;
block|}
comment|// Get name
name|int
name|firstOpenEquals
init|=
name|findNextClear
argument_list|(
name|working
argument_list|,
literal|0
argument_list|,
literal|'='
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|namedParameter
init|=
operator|new
name|StreamExpressionNamedParameter
argument_list|(
name|working
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|firstOpenEquals
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
comment|// we know this is ok because of the check in isNamedParameter
name|String
name|parameter
init|=
name|working
operator|.
name|substring
argument_list|(
name|firstOpenEquals
operator|+
literal|1
argument_list|,
name|working
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isExpressionClause
argument_list|(
name|parameter
argument_list|)
condition|)
block|{
name|namedParameter
operator|.
name|setParameter
argument_list|(
name|generateStreamExpression
argument_list|(
name|parameter
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if wrapped in quotes, remove them
if|if
condition|(
name|parameter
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|&&
name|parameter
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
name|parameter
operator|=
name|parameter
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|parameter
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|parameter
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"'%s' is not a proper named parameter clause"
argument_list|,
name|working
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|// if contain \" replace with "
if|if
condition|(
name|parameter
operator|.
name|contains
argument_list|(
literal|"\\\""
argument_list|)
condition|)
block|{
name|parameter
operator|=
name|parameter
operator|.
name|replace
argument_list|(
literal|"\\\""
argument_list|,
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|parameter
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"'%s' is not a proper named parameter clause"
argument_list|,
name|working
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|namedParameter
operator|.
name|setParameter
argument_list|(
operator|new
name|StreamExpressionValue
argument_list|(
name|parameter
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|namedParameter
return|;
block|}
comment|/* Returns true if the clause is a valid expression clause. This is defined to    * mean it begins with ( and ends with )    * Expects that the passed in clause has already been trimmed of leading and    * trailing spaces*/
DECL|method|isExpressionClause
specifier|private
specifier|static
name|boolean
name|isExpressionClause
parameter_list|(
name|String
name|clause
parameter_list|)
block|{
comment|// operator(.....something.....)
comment|// must be balanced
if|if
condition|(
operator|!
name|isBalanced
argument_list|(
name|clause
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// find first (, then check from start to that location and only accept alphanumeric
name|int
name|firstOpenParen
init|=
name|findNextClear
argument_list|(
name|clause
argument_list|,
literal|0
argument_list|,
literal|'('
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstOpenParen
operator|<=
literal|0
operator|||
name|firstOpenParen
operator|==
name|clause
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|functionName
init|=
name|clause
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|firstOpenParen
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|wordToken
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Must end with )
return|return
name|clause
operator|.
name|endsWith
argument_list|(
literal|")"
argument_list|)
return|;
block|}
DECL|method|isNamedParameterClause
specifier|private
specifier|static
name|boolean
name|isNamedParameterClause
parameter_list|(
name|String
name|clause
parameter_list|)
block|{
comment|// name=thing
comment|// find first = then check from start to that location and only accept alphanumeric
name|int
name|firstOpenEquals
init|=
name|findNextClear
argument_list|(
name|clause
argument_list|,
literal|0
argument_list|,
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstOpenEquals
operator|<=
literal|0
operator|||
name|firstOpenEquals
operator|==
name|clause
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|name
init|=
name|clause
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|firstOpenEquals
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|wordToken
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/* Finds index of the next char equal to findThis that is not within a quote or set of parens    * Does not work with the following values of findThis: " ' \ ) -- well, it might but wouldn't    * really give you what you want. Don't call with those characters */
DECL|method|findNextClear
specifier|private
specifier|static
name|int
name|findNextClear
parameter_list|(
name|String
name|clause
parameter_list|,
name|int
name|startingIdx
parameter_list|,
name|char
name|findThis
parameter_list|)
block|{
name|int
name|openParens
init|=
literal|0
decl_stmt|;
name|boolean
name|isDoubleQuote
init|=
literal|false
decl_stmt|;
name|boolean
name|isSingleQuote
init|=
literal|false
decl_stmt|;
name|boolean
name|isEscaped
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|startingIdx
init|;
name|idx
operator|<
name|clause
operator|.
name|length
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|char
name|c
init|=
name|clause
operator|.
name|charAt
argument_list|(
name|idx
argument_list|)
decl_stmt|;
comment|// if we're not in a non-escaped quote or paren state, then we've found the space we want
if|if
condition|(
name|c
operator|==
name|findThis
operator|&&
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
operator|&&
operator|!
name|isDoubleQuote
operator|&&
literal|0
operator|==
name|openParens
condition|)
block|{
return|return
name|idx
return|;
block|}
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
comment|// We invert to support situations where \\ exists
name|isEscaped
operator|=
operator|!
name|isEscaped
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
comment|// if we're not in a non-escaped single quote state, then invert the double quote state
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
condition|)
block|{
name|isDoubleQuote
operator|=
operator|!
name|isDoubleQuote
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
comment|// if we're not in a non-escaped double quote state, then invert the single quote state
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isDoubleQuote
condition|)
block|{
name|isSingleQuote
operator|=
operator|!
name|isSingleQuote
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|'('
case|:
comment|// if we're not in a non-escaped quote state, then increment the # of open parens
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
operator|&&
operator|!
name|isDoubleQuote
condition|)
block|{
name|openParens
operator|+=
literal|1
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|')'
case|:
comment|// if we're not in a non-escaped quote state, then decrement the # of open parens
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
operator|&&
operator|!
name|isDoubleQuote
condition|)
block|{
name|openParens
operator|-=
literal|1
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
default|default:
name|isEscaped
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// Not found
return|return
operator|-
literal|1
return|;
block|}
comment|/* Returns a list of the tokens found. Assumed to be of the form    * 'foo bar baz' and not of the for '(foo bar baz)'    * 'foo bar (baz jaz)' is ok and will return three tokens of    * 'foo', 'bar', and '(baz jaz)'    */
DECL|method|splitOn
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|splitOn
parameter_list|(
name|String
name|clause
parameter_list|,
name|char
name|splitOnThis
parameter_list|)
block|{
name|String
name|working
init|=
name|clause
operator|.
name|trim
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// will break when next splitOnThis isn't found
name|int
name|nextIdx
init|=
name|findNextClear
argument_list|(
name|working
argument_list|,
literal|0
argument_list|,
name|splitOnThis
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextIdx
operator|<
literal|0
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|working
argument_list|)
expr_stmt|;
break|break;
block|}
name|parts
operator|.
name|add
argument_list|(
name|working
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|nextIdx
argument_list|)
argument_list|)
expr_stmt|;
comment|// handle ending splitOnThis
if|if
condition|(
name|nextIdx
operator|+
literal|1
operator|==
name|working
operator|.
name|length
argument_list|()
condition|)
block|{
break|break;
block|}
name|working
operator|=
name|working
operator|.
name|substring
argument_list|(
name|nextIdx
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
return|return
name|parts
return|;
block|}
comment|/* Returns true if the clause has balanced parenthesis */
DECL|method|isBalanced
specifier|private
specifier|static
name|boolean
name|isBalanced
parameter_list|(
name|String
name|clause
parameter_list|)
block|{
name|int
name|openParens
init|=
literal|0
decl_stmt|;
name|boolean
name|isDoubleQuote
init|=
literal|false
decl_stmt|;
name|boolean
name|isSingleQuote
init|=
literal|false
decl_stmt|;
name|boolean
name|isEscaped
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|clause
operator|.
name|length
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|char
name|c
init|=
name|clause
operator|.
name|charAt
argument_list|(
name|idx
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
comment|// We invert to support situations where \\ exists
name|isEscaped
operator|=
operator|!
name|isEscaped
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
comment|// if we're not in a non-escaped single quote state, then invert the double quote state
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
condition|)
block|{
name|isDoubleQuote
operator|=
operator|!
name|isDoubleQuote
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
comment|// if we're not in a non-escaped double quote state, then invert the single quote state
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isDoubleQuote
condition|)
block|{
name|isSingleQuote
operator|=
operator|!
name|isSingleQuote
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|'('
case|:
comment|// if we're not in a non-escaped quote state, then increment the # of open parens
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
operator|&&
operator|!
name|isDoubleQuote
condition|)
block|{
name|openParens
operator|+=
literal|1
expr_stmt|;
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|')'
case|:
comment|// if we're not in a non-escaped quote state, then decrement the # of open parens
if|if
condition|(
operator|!
name|isEscaped
operator|&&
operator|!
name|isSingleQuote
operator|&&
operator|!
name|isDoubleQuote
condition|)
block|{
name|openParens
operator|-=
literal|1
expr_stmt|;
comment|// If we're ever< 0 then we know we're not balanced
if|if
condition|(
name|openParens
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|isEscaped
operator|=
literal|false
expr_stmt|;
break|break;
default|default:
name|isEscaped
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
operator|(
literal|0
operator|==
name|openParens
operator|)
return|;
block|}
DECL|method|wordToken
specifier|public
specifier|static
name|boolean
name|wordToken
parameter_list|(
name|String
name|token
parameter_list|)
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
name|token
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|token
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
operator|&&
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|wordChars
argument_list|,
name|c
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

