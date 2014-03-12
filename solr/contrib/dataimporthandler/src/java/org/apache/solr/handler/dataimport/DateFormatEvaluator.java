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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|WeakHashMap
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
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|EntityField
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *<p>Formats values using a given date format.</p>  *<p>Pass three parameters:  *<ul>  *<li>An {@link EntityField} or a date expression to be parsed with   *      the {@link DateMathParser} class  If the value is in a String,   *      then it is assumed to be a datemath expression, otherwise it   *      resolved using a {@link VariableResolver} instance</li>  *<li>A date format see {@link SimpleDateFormat} for the syntax.</li>  *<li>The {@link Locale} to parse.    *      (optional. Defaults to the Root Locale)</li>  *</ul>  *</p>  */
end_comment

begin_class
DECL|class|DateFormatEvaluator
specifier|public
class|class
name|DateFormatEvaluator
extends|extends
name|Evaluator
block|{
DECL|field|DEFAULT_DATE_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DATE_FORMAT
init|=
literal|"yyyy-MM-dd HH:mm:ss"
decl_stmt|;
DECL|field|cache
name|Map
argument_list|<
name|DateFormatCacheKey
argument_list|,
name|SimpleDateFormat
argument_list|>
name|cache
init|=
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|availableLocales
name|Map
argument_list|<
name|String
argument_list|,
name|Locale
argument_list|>
name|availableLocales
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|availableTimezones
name|Set
argument_list|<
name|String
argument_list|>
name|availableTimezones
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|DateFormatCacheKey
class|class
name|DateFormatCacheKey
block|{
DECL|method|DateFormatCacheKey
name|DateFormatCacheKey
parameter_list|(
name|Locale
name|l
parameter_list|,
name|TimeZone
name|tz
parameter_list|,
name|String
name|df
parameter_list|)
block|{
name|this
operator|.
name|locale
operator|=
name|l
expr_stmt|;
name|this
operator|.
name|timezone
operator|=
name|tz
expr_stmt|;
name|this
operator|.
name|dateFormat
operator|=
name|df
expr_stmt|;
block|}
DECL|field|locale
name|Locale
name|locale
decl_stmt|;
DECL|field|timezone
name|TimeZone
name|timezone
decl_stmt|;
DECL|field|dateFormat
name|String
name|dateFormat
decl_stmt|;
block|}
DECL|method|DateFormatEvaluator
specifier|public
name|DateFormatEvaluator
parameter_list|()
block|{
for|for
control|(
name|Locale
name|locale
range|:
name|Locale
operator|.
name|getAvailableLocales
argument_list|()
control|)
block|{
name|availableLocales
operator|.
name|put
argument_list|(
name|locale
operator|.
name|toString
argument_list|()
argument_list|,
name|locale
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|tz
range|:
name|TimeZone
operator|.
name|getAvailableIDs
argument_list|()
control|)
block|{
name|availableTimezones
operator|.
name|add
argument_list|(
name|tz
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDateFormat
specifier|private
name|SimpleDateFormat
name|getDateFormat
parameter_list|(
name|String
name|pattern
parameter_list|,
name|TimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|DateFormatCacheKey
name|dfck
init|=
operator|new
name|DateFormatCacheKey
argument_list|(
name|locale
argument_list|,
name|timezone
argument_list|,
name|pattern
argument_list|)
decl_stmt|;
name|SimpleDateFormat
name|sdf
init|=
name|cache
operator|.
name|get
argument_list|(
name|dfck
argument_list|)
decl_stmt|;
if|if
condition|(
name|sdf
operator|==
literal|null
condition|)
block|{
name|sdf
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|pattern
argument_list|,
name|locale
argument_list|)
expr_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|timezone
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|dfck
argument_list|,
name|sdf
argument_list|)
expr_stmt|;
block|}
return|return
name|sdf
return|;
block|}
annotation|@
name|Override
DECL|method|evaluate
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|l
init|=
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|<
literal|2
operator|||
name|l
operator|.
name|size
argument_list|()
operator|>
literal|4
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'formatDate()' must have two, three or four parameters "
argument_list|)
throw|;
block|}
name|Object
name|o
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Object
name|format
init|=
name|l
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|instanceof
name|VariableWrapper
condition|)
block|{
name|VariableWrapper
name|wrapper
init|=
operator|(
name|VariableWrapper
operator|)
name|format
decl_stmt|;
name|o
operator|=
name|wrapper
operator|.
name|resolve
argument_list|()
expr_stmt|;
name|format
operator|=
name|o
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|Locale
name|locale
init|=
name|Locale
operator|.
name|ROOT
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|Object
name|localeObj
init|=
name|l
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|localeStr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|localeObj
operator|instanceof
name|VariableWrapper
condition|)
block|{
name|localeStr
operator|=
operator|(
operator|(
name|VariableWrapper
operator|)
name|localeObj
operator|)
operator|.
name|resolve
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|localeStr
operator|=
name|localeObj
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|locale
operator|=
name|availableLocales
operator|.
name|get
argument_list|(
name|localeStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|locale
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Unsupported locale: "
operator|+
name|localeStr
argument_list|)
throw|;
block|}
block|}
name|TimeZone
name|tz
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|==
literal|4
condition|)
block|{
name|Object
name|tzObj
init|=
name|l
operator|.
name|get
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|String
name|tzStr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tzObj
operator|instanceof
name|VariableWrapper
condition|)
block|{
name|tzStr
operator|=
operator|(
operator|(
name|VariableWrapper
operator|)
name|tzObj
operator|)
operator|.
name|resolve
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tzStr
operator|=
name|tzObj
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|availableTimezones
operator|.
name|contains
argument_list|(
name|tzStr
argument_list|)
condition|)
block|{
name|tz
operator|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|tzStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Unsupported Timezone: "
operator|+
name|tzStr
argument_list|)
throw|;
block|}
block|}
name|String
name|dateFmt
init|=
name|format
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|fmt
init|=
name|getDateFormat
argument_list|(
name|dateFmt
argument_list|,
name|tz
argument_list|,
name|locale
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|VariableWrapper
condition|)
block|{
name|VariableWrapper
name|variableWrapper
init|=
operator|(
name|VariableWrapper
operator|)
name|o
decl_stmt|;
name|Object
name|variableval
init|=
name|variableWrapper
operator|.
name|resolve
argument_list|()
decl_stmt|;
if|if
condition|(
name|variableval
operator|instanceof
name|Date
condition|)
block|{
name|date
operator|=
operator|(
name|Date
operator|)
name|variableval
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
name|variableval
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|date
operator|=
name|getDateFormat
argument_list|(
name|DEFAULT_DATE_FORMAT
argument_list|,
name|tz
argument_list|,
name|locale
argument_list|)
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
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|exp
argument_list|,
literal|"Invalid expression for date"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|String
name|datemathfmt
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
name|datemathfmt
operator|=
name|datemathfmt
operator|.
name|replaceAll
argument_list|(
literal|"NOW"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|date
operator|=
name|getDateMathParser
argument_list|(
name|locale
argument_list|,
name|tz
argument_list|)
operator|.
name|parseMath
argument_list|(
name|datemathfmt
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Invalid expression for date"
argument_list|)
expr_stmt|;
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
DECL|method|getDateMathParser
specifier|static
name|DateMathParser
name|getDateMathParser
parameter_list|(
name|Locale
name|l
parameter_list|,
name|TimeZone
name|tz
parameter_list|)
block|{
return|return
operator|new
name|DateMathParser
argument_list|(
name|tz
argument_list|,
name|l
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Date
name|getNow
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

