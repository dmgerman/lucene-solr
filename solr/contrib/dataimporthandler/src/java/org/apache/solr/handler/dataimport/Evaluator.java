begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import

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
name|Date
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DateMathParser
import|;
end_import

begin_comment
comment|/**  *<p>  * Pluggable functions for resolving variables  *</p>  *<p>  * Implementations of this abstract class must provide a public no-arg constructor.  *</p>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|Evaluator
specifier|public
specifier|abstract
class|class
name|Evaluator
block|{
comment|/**    * Return a String after processing an expression and a {@link VariableResolver}    *    * @see VariableResolver    * @param expression string to be evaluated    * @param context instance    * @return the value of the given expression evaluated using the resolver    */
DECL|method|evaluate
specifier|public
specifier|abstract
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
function_decl|;
comment|/**    * Parses a string of expression into separate params. The values are separated by commas. each value will be    * translated into one of the following:    *&lt;ol&gt;    *&lt;li&gt;If it is in single quotes the value will be translated to a String&lt;/li&gt;    *&lt;li&gt;If is is not in quotes and is a number a it will be translated into a Double&lt;/li&gt;    *&lt;li&gt;else it is a variable which can be resolved and it will be put in as an instance of VariableWrapper&lt;/li&gt;    *&lt;/ol&gt;    *    * @param expression the expression to be parsed    * @param vr the VariableResolver instance for resolving variables    *    * @return a List of objects which can either be a string, number or a variable wrapper    */
DECL|method|parseParams
name|List
argument_list|<
name|Object
argument_list|>
name|parseParams
parameter_list|(
name|String
name|expression
parameter_list|,
name|VariableResolver
name|vr
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|expression
operator|=
name|expression
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
index|[]
name|ss
init|=
name|expression
operator|.
name|split
argument_list|(
literal|","
argument_list|)
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
name|ss
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ss
index|[
name|i
index|]
operator|=
name|ss
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|ss
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
block|{
comment|//a string param has started
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|ss
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
break|break;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|ss
operator|.
name|length
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"invalid string at "
operator|+
name|ss
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|" in function params: "
operator|+
name|expression
argument_list|)
throw|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|String
name|s
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|s
operator|=
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\\\\'"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|ss
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
try|try
block|{
name|Double
name|doub
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|doub
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
name|vr
operator|.
name|resolve
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
operator|==
literal|null
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Invalid number :"
operator|+
name|ss
index|[
name|i
index|]
operator|+
literal|"in parameters  "
operator|+
name|expression
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|VariableWrapper
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|,
name|vr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|class|VariableWrapper
specifier|static
class|class
name|VariableWrapper
block|{
DECL|field|varName
name|String
name|varName
decl_stmt|;
DECL|field|vr
name|VariableResolver
name|vr
decl_stmt|;
DECL|method|VariableWrapper
specifier|public
name|VariableWrapper
parameter_list|(
name|String
name|s
parameter_list|,
name|VariableResolver
name|vr
parameter_list|)
block|{
name|this
operator|.
name|varName
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|vr
operator|=
name|vr
expr_stmt|;
block|}
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|()
block|{
return|return
name|vr
operator|.
name|resolve
argument_list|(
name|varName
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
name|Object
name|o
init|=
name|vr
operator|.
name|resolve
argument_list|(
name|varName
argument_list|)
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|null
else|:
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|field|IN_SINGLE_QUOTES
specifier|static
name|Pattern
name|IN_SINGLE_QUOTES
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^'(.*?)'$"
argument_list|)
decl_stmt|;
DECL|field|DATE_FORMAT_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FORMAT_EVALUATOR
init|=
literal|"formatDate"
decl_stmt|;
DECL|field|URL_ENCODE_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|URL_ENCODE_EVALUATOR
init|=
literal|"encodeUrl"
decl_stmt|;
DECL|field|ESCAPE_SOLR_QUERY_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|ESCAPE_SOLR_QUERY_CHARS
init|=
literal|"escapeQueryChars"
decl_stmt|;
DECL|field|SQL_ESCAPE_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|SQL_ESCAPE_EVALUATOR
init|=
literal|"escapeSql"
decl_stmt|;
block|}
end_class

end_unit

