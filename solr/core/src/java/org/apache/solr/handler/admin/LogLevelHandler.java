begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|List
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
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|impl
operator|.
name|StaticLoggerBinder
import|;
end_import

begin_comment
comment|/**  * A request handler to show which loggers are registered and allows you to set them  *  * @since 4.0  */
end_comment

begin_class
DECL|class|LogLevelHandler
specifier|public
class|class
name|LogLevelHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|ROOT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_NAME
init|=
literal|"root"
decl_stmt|;
comment|//-------------------------------------------------------------------------------------------------
comment|//
comment|//   Logger wrapper classes
comment|//
comment|//-------------------------------------------------------------------------------------------------
DECL|class|LoggerWrapper
specifier|public
specifier|abstract
specifier|static
class|class
name|LoggerWrapper
implements|implements
name|Comparable
argument_list|<
name|LoggerWrapper
argument_list|>
block|{
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|level
specifier|protected
name|String
name|level
decl_stmt|;
DECL|method|LoggerWrapper
specifier|public
name|LoggerWrapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getLevel
specifier|public
name|String
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|isSet
specifier|public
specifier|abstract
name|boolean
name|isSet
parameter_list|()
function_decl|;
DECL|method|getInfo
specifier|public
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
name|getInfo
parameter_list|()
block|{
name|SimpleOrderedMap
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"level"
argument_list|,
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"set"
argument_list|,
name|isSet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|LoggerWrapper
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
return|return
literal|0
return|;
name|String
name|tN
init|=
name|this
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|oN
init|=
name|other
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|tN
argument_list|)
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|oN
argument_list|)
condition|)
return|return
literal|1
return|;
return|return
name|tN
operator|.
name|compareTo
argument_list|(
name|oN
argument_list|)
return|;
block|}
block|}
DECL|interface|LoggerFactoryWrapper
specifier|public
specifier|static
interface|interface
name|LoggerFactoryWrapper
block|{
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getAllLevels
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllLevels
parameter_list|()
function_decl|;
DECL|method|setLogLevel
specifier|public
name|void
name|setLogLevel
parameter_list|(
name|String
name|category
parameter_list|,
name|String
name|level
parameter_list|)
function_decl|;
DECL|method|getLoggers
specifier|public
name|Collection
argument_list|<
name|LoggerWrapper
argument_list|>
name|getLoggers
parameter_list|()
function_decl|;
block|}
comment|//-------------------------------------------------------------------------------------------------
comment|//
comment|//   java.util.logging
comment|//
comment|//-------------------------------------------------------------------------------------------------
DECL|class|LoggerFactoryWrapperJUL
specifier|public
specifier|static
class|class
name|LoggerFactoryWrapperJUL
implements|implements
name|LoggerFactoryWrapper
block|{
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"java.util.logging"
return|;
block|}
annotation|@
name|Override
DECL|method|getAllLevels
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllLevels
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|Level
operator|.
name|FINEST
operator|.
name|getName
argument_list|()
argument_list|,
name|Level
operator|.
name|FINE
operator|.
name|getName
argument_list|()
argument_list|,
name|Level
operator|.
name|CONFIG
operator|.
name|getName
argument_list|()
argument_list|,
name|Level
operator|.
name|INFO
operator|.
name|getName
argument_list|()
argument_list|,
name|Level
operator|.
name|WARNING
operator|.
name|getName
argument_list|()
argument_list|,
name|Level
operator|.
name|SEVERE
operator|.
name|getName
argument_list|()
argument_list|,
name|Level
operator|.
name|OFF
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setLogLevel
specifier|public
name|void
name|setLogLevel
parameter_list|(
name|String
name|category
parameter_list|,
name|String
name|level
parameter_list|)
block|{
if|if
condition|(
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|category
argument_list|)
condition|)
block|{
name|category
operator|=
literal|""
expr_stmt|;
block|}
name|Logger
name|log
init|=
name|LogManager
operator|.
name|getLogManager
argument_list|()
operator|.
name|getLogger
argument_list|(
name|category
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|null
operator|||
literal|"unset"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
operator|||
literal|"null"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
if|if
condition|(
name|log
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|setLevel
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|log
operator|==
literal|null
condition|)
block|{
name|log
operator|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|category
argument_list|)
expr_stmt|;
comment|// create it
block|}
name|log
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|parse
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLoggers
specifier|public
name|Collection
argument_list|<
name|LoggerWrapper
argument_list|>
name|getLoggers
parameter_list|()
block|{
name|LogManager
name|manager
init|=
name|LogManager
operator|.
name|getLogManager
argument_list|()
decl_stmt|;
name|Logger
name|root
init|=
name|manager
operator|.
name|getLogger
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LoggerWrapper
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LoggerWrapper
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|names
init|=
name|manager
operator|.
name|getLoggerNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|==
name|root
condition|)
block|{
continue|continue;
block|}
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|LoggerWrapperJUL
argument_list|(
name|name
argument_list|,
name|logger
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|dot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|dot
operator|<
literal|0
condition|)
break|break;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|LoggerWrapperJUL
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|map
operator|.
name|put
argument_list|(
name|ROOT_NAME
argument_list|,
operator|new
name|LoggerWrapperJUL
argument_list|(
name|ROOT_NAME
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
operator|.
name|values
argument_list|()
return|;
block|}
block|}
DECL|class|LoggerWrapperJUL
specifier|public
specifier|static
class|class
name|LoggerWrapperJUL
extends|extends
name|LoggerWrapper
block|{
DECL|field|LEVELS
specifier|private
specifier|static
specifier|final
name|Level
index|[]
name|LEVELS
init|=
block|{
literal|null
block|,
comment|// aka unset
name|Level
operator|.
name|FINEST
block|,
name|Level
operator|.
name|FINE
block|,
name|Level
operator|.
name|CONFIG
block|,
name|Level
operator|.
name|INFO
block|,
name|Level
operator|.
name|WARNING
block|,
name|Level
operator|.
name|SEVERE
block|,
name|Level
operator|.
name|OFF
comment|// Level.ALL -- ignore. It is useless.
block|}
decl_stmt|;
DECL|field|logger
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|method|LoggerWrapperJUL
specifier|public
name|LoggerWrapperJUL
parameter_list|(
name|String
name|name
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLevel
specifier|public
name|String
name|getLevel
parameter_list|()
block|{
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Level
name|level
init|=
name|logger
operator|.
name|getLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|level
operator|!=
literal|null
condition|)
block|{
return|return
name|level
operator|.
name|getName
argument_list|()
return|;
block|}
for|for
control|(
name|Level
name|l
range|:
name|LEVELS
control|)
block|{
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
comment|// avoid NPE
continue|continue;
block|}
if|if
condition|(
name|logger
operator|.
name|isLoggable
argument_list|(
name|l
argument_list|)
condition|)
block|{
comment|// return first level loggable
return|return
name|l
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
return|return
name|Level
operator|.
name|OFF
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isSet
specifier|public
name|boolean
name|isSet
parameter_list|()
block|{
return|return
operator|(
name|logger
operator|!=
literal|null
operator|&&
name|logger
operator|.
name|getLevel
argument_list|()
operator|!=
literal|null
operator|)
return|;
block|}
block|}
comment|//-------------------------------------------------------------------------------------------------
comment|//
comment|//   Log4j
comment|//
comment|//-------------------------------------------------------------------------------------------------
DECL|class|LoggerWrapperLog4j
specifier|public
specifier|static
class|class
name|LoggerWrapperLog4j
extends|extends
name|LoggerWrapper
block|{
DECL|field|logger
specifier|final
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|logger
decl_stmt|;
DECL|method|LoggerWrapperLog4j
specifier|public
name|LoggerWrapperLog4j
parameter_list|(
name|String
name|name
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|logger
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLevel
specifier|public
name|String
name|getLevel
parameter_list|()
block|{
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Object
name|level
init|=
name|logger
operator|.
name|getLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|level
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|isSet
specifier|public
name|boolean
name|isSet
parameter_list|()
block|{
return|return
operator|(
name|logger
operator|!=
literal|null
operator|&&
name|logger
operator|.
name|getLevel
argument_list|()
operator|!=
literal|null
operator|)
return|;
block|}
block|}
DECL|class|LoggerFactoryWrapperLog4j
specifier|public
specifier|static
class|class
name|LoggerFactoryWrapperLog4j
implements|implements
name|LoggerFactoryWrapper
block|{
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"log4j"
return|;
block|}
annotation|@
name|Override
DECL|method|getAllLevels
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAllLevels
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|ALL
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|TRACE
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|DEBUG
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|INFO
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|WARN
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|ERROR
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|FATAL
operator|.
name|toString
argument_list|()
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|OFF
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setLogLevel
specifier|public
name|void
name|setLogLevel
parameter_list|(
name|String
name|category
parameter_list|,
name|String
name|level
parameter_list|)
block|{
if|if
condition|(
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|category
argument_list|)
condition|)
block|{
name|category
operator|=
literal|""
expr_stmt|;
block|}
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|category
argument_list|)
decl_stmt|;
if|if
condition|(
name|level
operator|==
literal|null
operator|||
literal|"unset"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
operator|||
literal|"null"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
condition|)
block|{
name|setLevelWithReflection
argument_list|(
name|log
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setLevelWithReflection
argument_list|(
name|log
argument_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
operator|.
name|toLevel
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * log.setLevel(level);      */
DECL|method|setLevelWithReflection
specifier|private
name|void
name|setLevelWithReflection
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|log
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
name|level
parameter_list|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|logclass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.log4j.Logger"
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|levelclass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.log4j.Level"
argument_list|)
decl_stmt|;
name|Method
name|method
init|=
name|logclass
operator|.
name|getMethod
argument_list|(
literal|"setLevel"
argument_list|,
name|levelclass
argument_list|)
decl_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|log
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to set Log4j Level"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLoggers
specifier|public
name|Collection
argument_list|<
name|LoggerWrapper
argument_list|>
name|getLoggers
parameter_list|()
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|root
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LoggerWrapper
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LoggerWrapper
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|loggers
init|=
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
operator|.
name|getCurrentLoggers
argument_list|()
decl_stmt|;
while|while
condition|(
name|loggers
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
name|logger
init|=
operator|(
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|)
name|loggers
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|logger
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|logger
operator|==
name|root
condition|)
block|{
continue|continue;
block|}
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|LoggerWrapperLog4j
argument_list|(
name|name
argument_list|,
name|logger
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|dot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
if|if
condition|(
name|dot
operator|<
literal|0
condition|)
break|break;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dot
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|LoggerWrapperJUL
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|map
operator|.
name|put
argument_list|(
name|ROOT_NAME
argument_list|,
operator|new
name|LoggerWrapperLog4j
argument_list|(
name|ROOT_NAME
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
operator|.
name|values
argument_list|()
return|;
block|}
block|}
comment|//-------------------------------------------------------------------------------------------------
comment|//
comment|//   The Request Handler
comment|//
comment|//-------------------------------------------------------------------------------------------------
DECL|field|factory
name|LoggerFactoryWrapper
name|factory
decl_stmt|;
DECL|field|slf4jImpl
name|String
name|slf4jImpl
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|String
name|fname
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"logger.factory"
argument_list|)
decl_stmt|;
try|try
block|{
name|slf4jImpl
operator|=
name|StaticLoggerBinder
operator|.
name|getSingleton
argument_list|()
operator|.
name|getLoggerFactoryClassStr
argument_list|()
expr_stmt|;
if|if
condition|(
name|fname
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|slf4jImpl
operator|.
name|indexOf
argument_list|(
literal|"Log4j"
argument_list|)
operator|>
literal|0
condition|)
block|{
name|fname
operator|=
literal|"Log4j"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|slf4jImpl
operator|.
name|indexOf
argument_list|(
literal|"JDK"
argument_list|)
operator|>
literal|0
condition|)
block|{
name|fname
operator|=
literal|"JUL"
expr_stmt|;
block|}
else|else
block|{
return|return;
comment|// unsuppored
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
if|if
condition|(
literal|"JUL"
operator|.
name|equalsIgnoreCase
argument_list|(
name|fname
argument_list|)
condition|)
block|{
name|factory
operator|=
operator|new
name|LoggerFactoryWrapperJUL
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Log4j"
operator|.
name|equals
argument_list|(
name|fname
argument_list|)
condition|)
block|{
name|factory
operator|=
operator|new
name|LoggerFactoryWrapperLog4j
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|factory
operator|=
operator|(
name|LoggerFactoryWrapper
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|fname
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Don't do anything if the framework is unknown
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"Unsupported Logging Framework: "
operator|+
name|slf4jImpl
argument_list|)
expr_stmt|;
return|return;
block|}
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
index|[]
name|set
init|=
name|params
operator|.
name|getParams
argument_list|(
literal|"set"
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|pair
range|:
name|set
control|)
block|{
name|String
index|[]
name|split
init|=
name|pair
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|split
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid format, expected level:value, got "
operator|+
name|pair
argument_list|)
throw|;
block|}
name|String
name|category
init|=
name|split
index|[
literal|0
index|]
decl_stmt|;
name|String
name|level
init|=
name|split
index|[
literal|1
index|]
decl_stmt|;
name|factory
operator|.
name|setLogLevel
argument_list|(
name|category
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"framework"
argument_list|,
name|factory
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"slfj4"
argument_list|,
name|slf4jImpl
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"levels"
argument_list|,
name|factory
operator|.
name|getAllLevels
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LoggerWrapper
argument_list|>
name|loggers
init|=
operator|new
name|ArrayList
argument_list|<
name|LogLevelHandler
operator|.
name|LoggerWrapper
argument_list|>
argument_list|(
name|factory
operator|.
name|getLoggers
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|loggers
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
argument_list|>
name|info
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|LoggerWrapper
name|wrap
range|:
name|loggers
control|)
block|{
name|info
operator|.
name|add
argument_list|(
name|wrap
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"loggers"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// ////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Lucene Log Level info"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

