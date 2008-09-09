begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|Matcher
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

begin_comment
comment|/**  *<p>  * Holds definitions for evaluators provided by DataImportHandler  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|EvaluatorBag
specifier|public
class|class
name|EvaluatorBag
block|{
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
DECL|field|SQL_ESCAPE_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|SQL_ESCAPE_EVALUATOR
init|=
literal|"escapeSql"
decl_stmt|;
DECL|field|FORMAT_METHOD
specifier|static
specifier|final
name|Pattern
name|FORMAT_METHOD
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(\\w*?)\\((.*?)\\)$"
argument_list|)
decl_stmt|;
comment|/**    *<p>    * Returns an<code>Evaluator</code> instance meant to be used for escaping    * values in SQL queries.    *</p>    *<p>    * It escapes the value of the given expression by replacing all occurrences    * of single-quotes by two single-quotes and similarily for double-quotes    *</p>    *    * @return an<code>Evaluator</code> instance capable of SQL-escaping    *         expressions.    */
DECL|method|getSqlEscapingEvaluator
specifier|public
specifier|static
name|Evaluator
name|getSqlEscapingEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
specifier|public
name|String
name|evaluate
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
name|Object
name|o
init|=
name|resolver
operator|.
name|resolve
argument_list|(
name|expression
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|o
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"''"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\"\""
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    *<p>    * Returns an<code>Evaluator</code> instance capable of URL-encoding    * expressions. The expressions are evaluated using a    *<code>VariableResolver</code>    *</p>    *    * @return an<code>Evaluator</code> instance capable of URL-encoding    *         expressions.    */
DECL|method|getUrlEvaluator
specifier|public
specifier|static
name|Evaluator
name|getUrlEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
specifier|public
name|String
name|evaluate
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
name|Object
name|value
init|=
literal|null
decl_stmt|;
try|try
block|{
name|value
operator|=
name|resolver
operator|.
name|resolve
argument_list|(
name|expression
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|URLEncoder
operator|.
name|encode
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Unable to encode expression: "
operator|+
name|expression
operator|+
literal|" with value: "
operator|+
name|value
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**    *<p>    * Returns an<code>Evaluator</code> instance capable of formatting values    * using a given date format.    *</p>    *<p>    * The value to be formatted can be a entity.field or a date expression parsed    * with<code>DateMathParser</code> class. If the value is in single quotes,    * then it is assumed to be a datemath expression, otherwise it resolved using    * a<code>VariableResolver</code> instance    *</p>    *    * @return an Evaluator instance capable of formatting values to a given date    *         format    * @see DateMathParser    */
DECL|method|getDateFormatEvaluator
specifier|public
specifier|static
name|Evaluator
name|getDateFormatEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
specifier|public
name|String
name|evaluate
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|,
name|String
name|expression
parameter_list|)
block|{
name|CacheEntry
name|e
init|=
name|getCachedData
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|String
name|expr
init|=
name|e
operator|.
name|key
decl_stmt|;
name|SimpleDateFormat
name|fmt
init|=
name|e
operator|.
name|format
decl_stmt|;
name|Matcher
name|m
init|=
name|IN_SINGLE_QUOTES
operator|.
name|matcher
argument_list|(
name|expr
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|datemathExpr
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|Date
name|date
init|=
name|dateMathParser
operator|.
name|parseMath
argument_list|(
name|datemathExpr
argument_list|)
decl_stmt|;
return|return
name|fmt
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|exp
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Invalid expression for date"
argument_list|,
name|exp
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|Object
name|o
init|=
name|resolver
operator|.
name|resolve
argument_list|(
name|expr
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|Date
name|date
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Date
condition|)
block|{
name|date
operator|=
operator|(
name|Date
operator|)
name|o
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|date
operator|=
name|DataImporter
operator|.
name|DATE_TIME_FORMAT
operator|.
name|parse
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|exp
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Invalid expression for date"
argument_list|,
name|exp
argument_list|)
throw|;
block|}
block|}
return|return
name|fmt
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
specifier|private
name|CacheEntry
name|getCachedData
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|CacheEntry
name|result
init|=
name|cache
operator|.
name|get
argument_list|(
name|str
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
name|result
return|;
name|Matcher
name|m
init|=
name|FORMAT_METHOD
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|String
name|expr
decl_stmt|,
name|pattern
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|expr
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|IN_SINGLE_QUOTES
operator|.
name|matcher
argument_list|(
name|expr
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
name|expr
operator|=
name|expr
operator|.
name|replaceAll
argument_list|(
literal|"NOW"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|pattern
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|str
argument_list|,
operator|new
name|CacheEntry
argument_list|(
name|expr
argument_list|,
operator|new
name|SimpleDateFormat
argument_list|(
name|pattern
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cache
operator|.
name|get
argument_list|(
name|str
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Invalid format String : "
operator|+
literal|"${dataimporter.functions."
operator|+
name|str
operator|+
literal|"}"
argument_list|)
throw|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|CacheEntry
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CacheEntry
argument_list|>
argument_list|()
decl_stmt|;
name|Pattern
name|FORMAT_METHOD
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(.*?),(.*?)$"
argument_list|)
decl_stmt|;
block|}
return|;
block|}
DECL|method|getFunctionsNamespace
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFunctionsNamespace
parameter_list|(
specifier|final
name|VariableResolver
name|resolver
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Evaluator
argument_list|>
name|evaluators
parameter_list|)
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Matcher
name|m
init|=
name|FORMAT_METHOD
operator|.
name|matcher
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
return|return
literal|null
return|;
name|String
name|fname
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Evaluator
name|evaluator
init|=
name|evaluators
operator|.
name|get
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|evaluator
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|evaluator
operator|.
name|evaluate
argument_list|(
name|resolver
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|class|CacheEntry
specifier|static
class|class
name|CacheEntry
block|{
DECL|field|key
specifier|public
name|String
name|key
decl_stmt|;
DECL|field|format
specifier|public
name|SimpleDateFormat
name|format
decl_stmt|;
DECL|method|CacheEntry
specifier|public
name|CacheEntry
parameter_list|(
name|String
name|key
parameter_list|,
name|SimpleDateFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
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
DECL|field|dateMathParser
specifier|static
name|DateMathParser
name|dateMathParser
init|=
operator|new
name|DateMathParser
argument_list|(
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

