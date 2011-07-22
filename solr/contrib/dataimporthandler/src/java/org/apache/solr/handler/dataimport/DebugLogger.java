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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  *<p>  * Implements most of the interactive development functionality  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|DebugLogger
class|class
name|DebugLogger
block|{
DECL|field|debugStack
specifier|private
name|Stack
argument_list|<
name|DebugInfo
argument_list|>
name|debugStack
decl_stmt|;
DECL|field|output
name|NamedList
name|output
decl_stmt|;
comment|//  private final SolrWriter writer1;
DECL|field|LINE
specifier|private
specifier|static
specifier|final
name|String
name|LINE
init|=
literal|"---------------------------------------------"
decl_stmt|;
DECL|field|fmt
specifier|private
name|MessageFormat
name|fmt
init|=
operator|new
name|MessageFormat
argument_list|(
literal|"----------- row #{0}-------------"
argument_list|)
decl_stmt|;
DECL|field|enabled
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
DECL|method|DebugLogger
specifier|public
name|DebugLogger
parameter_list|()
block|{
comment|//    writer = solrWriter;
name|output
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|debugStack
operator|=
operator|new
name|Stack
argument_list|<
name|DebugInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DebugInfo
name|pop
parameter_list|()
block|{
if|if
condition|(
name|size
argument_list|()
operator|==
literal|1
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Stack is becoming empty"
argument_list|)
throw|;
return|return
name|super
operator|.
name|pop
argument_list|()
return|;
block|}
block|}
expr_stmt|;
name|debugStack
operator|.
name|push
argument_list|(
operator|new
name|DebugInfo
argument_list|(
literal|null
argument_list|,
name|DIHLogLevels
operator|.
name|NONE
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|=
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
expr_stmt|;
block|}
DECL|method|peekStack
specifier|private
name|DebugInfo
name|peekStack
parameter_list|()
block|{
return|return
name|debugStack
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|debugStack
operator|.
name|peek
argument_list|()
return|;
block|}
DECL|method|log
specifier|public
name|void
name|log
parameter_list|(
name|DIHLogLevels
name|event
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|row
parameter_list|)
block|{
if|if
condition|(
name|event
operator|==
name|DIHLogLevels
operator|.
name|DISABLE_LOGGING
condition|)
block|{
name|enabled
operator|=
literal|false
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|DIHLogLevels
operator|.
name|ENABLE_LOGGING
condition|)
block|{
name|enabled
operator|=
literal|true
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|enabled
operator|&&
name|event
operator|!=
name|DIHLogLevels
operator|.
name|START_ENTITY
operator|&&
name|event
operator|!=
name|DIHLogLevels
operator|.
name|END_ENTITY
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|event
operator|==
name|DIHLogLevels
operator|.
name|START_DOC
condition|)
block|{
name|debugStack
operator|.
name|push
argument_list|(
operator|new
name|DebugInfo
argument_list|(
literal|null
argument_list|,
name|DIHLogLevels
operator|.
name|START_DOC
argument_list|,
name|peekStack
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|START_ENTITY
operator|==
name|event
condition|)
block|{
name|debugStack
operator|.
name|push
argument_list|(
operator|new
name|DebugInfo
argument_list|(
name|name
argument_list|,
name|DIHLogLevels
operator|.
name|START_ENTITY
argument_list|,
name|peekStack
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|ENTITY_OUT
operator|==
name|event
operator|||
name|DIHLogLevels
operator|.
name|PRE_TRANSFORMER_ROW
operator|==
name|event
condition|)
block|{
if|if
condition|(
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|type
operator|==
name|DIHLogLevels
operator|.
name|START_ENTITY
operator|||
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|type
operator|==
name|DIHLogLevels
operator|.
name|START_DOC
condition|)
block|{
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Object
index|[]
block|{
operator|++
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|rowCount
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|addToNamedList
argument_list|(
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|LINE
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|DIHLogLevels
operator|.
name|ROW_END
condition|)
block|{
name|popAllTransformers
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|END_ENTITY
operator|==
name|event
condition|)
block|{
while|while
condition|(
name|debugStack
operator|.
name|pop
argument_list|()
operator|.
name|type
operator|!=
name|DIHLogLevels
operator|.
name|START_ENTITY
condition|)
empty_stmt|;
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|END_DOC
operator|==
name|event
condition|)
block|{
while|while
condition|(
name|debugStack
operator|.
name|pop
argument_list|()
operator|.
name|type
operator|!=
name|DIHLogLevels
operator|.
name|START_DOC
condition|)
empty_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|==
name|DIHLogLevels
operator|.
name|TRANSFORMER_EXCEPTION
condition|)
block|{
name|debugStack
operator|.
name|push
argument_list|(
operator|new
name|DebugInfo
argument_list|(
name|name
argument_list|,
name|event
argument_list|,
name|peekStack
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
literal|"EXCEPTION"
argument_list|,
name|getStacktraceString
argument_list|(
operator|(
name|Exception
operator|)
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|TRANSFORMED_ROW
operator|==
name|event
condition|)
block|{
name|debugStack
operator|.
name|push
argument_list|(
operator|new
name|DebugInfo
argument_list|(
name|name
argument_list|,
name|event
argument_list|,
name|peekStack
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|LINE
argument_list|)
expr_stmt|;
name|addToNamedList
argument_list|(
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|LINE
argument_list|)
expr_stmt|;
if|if
condition|(
name|row
operator|instanceof
name|DataImportHandlerException
condition|)
block|{
name|DataImportHandlerException
name|dataImportHandlerException
init|=
operator|(
name|DataImportHandlerException
operator|)
name|row
decl_stmt|;
name|dataImportHandlerException
operator|.
name|debugged
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|ENTITY_META
operator|==
name|event
condition|)
block|{
name|popAllTransformers
argument_list|()
expr_stmt|;
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|row
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DIHLogLevels
operator|.
name|ENTITY_EXCEPTION
operator|==
name|event
condition|)
block|{
if|if
condition|(
name|row
operator|instanceof
name|DataImportHandlerException
condition|)
block|{
name|DataImportHandlerException
name|dihe
init|=
operator|(
name|DataImportHandlerException
operator|)
name|row
decl_stmt|;
if|if
condition|(
name|dihe
operator|.
name|debugged
condition|)
return|return;
name|dihe
operator|.
name|debugged
operator|=
literal|true
expr_stmt|;
block|}
name|popAllTransformers
argument_list|()
expr_stmt|;
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|lst
operator|.
name|add
argument_list|(
literal|"EXCEPTION"
argument_list|,
name|getStacktraceString
argument_list|(
operator|(
name|Exception
operator|)
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|popAllTransformers
specifier|private
name|void
name|popAllTransformers
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|DIHLogLevels
name|type
init|=
name|debugStack
operator|.
name|peek
argument_list|()
operator|.
name|type
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DIHLogLevels
operator|.
name|START_DOC
operator|||
name|type
operator|==
name|DIHLogLevels
operator|.
name|START_ENTITY
condition|)
break|break;
name|debugStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addToNamedList
specifier|private
name|void
name|addToNamedList
parameter_list|(
name|NamedList
name|nl
parameter_list|,
name|Object
name|row
parameter_list|)
block|{
if|if
condition|(
name|row
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|row
decl_stmt|;
name|NamedList
name|l
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|l
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
name|nl
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|row
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|row
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
name|nl
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|wrapDs
name|DataSource
name|wrapDs
parameter_list|(
specifier|final
name|DataSource
name|ds
parameter_list|)
block|{
return|return
operator|new
name|DataSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{
name|ds
operator|.
name|init
argument_list|(
name|context
argument_list|,
name|initProps
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|ENTITY_META
argument_list|,
literal|"query"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|ds
operator|.
name|getData
argument_list|(
name|query
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DataImportHandlerException
name|de
parameter_list|)
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|ENTITY_EXCEPTION
argument_list|,
literal|null
argument_list|,
name|de
argument_list|)
expr_stmt|;
throw|throw
name|de
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|ENTITY_EXCEPTION
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|DataImportHandlerException
name|de
init|=
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|de
operator|.
name|debugged
operator|=
literal|true
expr_stmt|;
throw|throw
name|de
throw|;
block|}
finally|finally
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|ENTITY_META
argument_list|,
literal|"time-taken"
argument_list|,
name|DocBuilder
operator|.
name|getTimeElapsedSince
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|wrapTransformer
name|Transformer
name|wrapTransformer
parameter_list|(
specifier|final
name|Transformer
name|t
parameter_list|)
block|{
return|return
operator|new
name|Transformer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|PRE_TRANSFORMER_ROW
argument_list|,
literal|null
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|String
name|tName
init|=
name|getTransformerName
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|Object
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|t
operator|.
name|transformRow
argument_list|(
name|row
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|TRANSFORMED_ROW
argument_list|,
name|tName
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataImportHandlerException
name|de
parameter_list|)
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|TRANSFORMER_EXCEPTION
argument_list|,
name|tName
argument_list|,
name|de
argument_list|)
expr_stmt|;
name|de
operator|.
name|debugged
operator|=
literal|true
expr_stmt|;
throw|throw
name|de
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
argument_list|(
name|DIHLogLevels
operator|.
name|TRANSFORMER_EXCEPTION
argument_list|,
name|tName
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|DataImportHandlerException
name|de
init|=
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|de
operator|.
name|debugged
operator|=
literal|true
expr_stmt|;
throw|throw
name|de
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
return|;
block|}
DECL|method|getStacktraceString
specifier|public
specifier|static
name|String
name|getStacktraceString
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getTransformerName
specifier|static
name|String
name|getTransformerName
parameter_list|(
name|Transformer
name|t
parameter_list|)
block|{
name|Class
name|transClass
init|=
name|t
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|EntityProcessorWrapper
operator|.
name|ReflectionTransformer
condition|)
block|{
return|return
operator|(
operator|(
name|EntityProcessorWrapper
operator|.
name|ReflectionTransformer
operator|)
name|t
operator|)
operator|.
name|trans
return|;
block|}
if|if
condition|(
name|t
operator|instanceof
name|ScriptTransformer
condition|)
block|{
name|ScriptTransformer
name|scriptTransformer
init|=
operator|(
name|ScriptTransformer
operator|)
name|t
decl_stmt|;
return|return
literal|"script:"
operator|+
name|scriptTransformer
operator|.
name|getFunctionName
argument_list|()
return|;
block|}
if|if
condition|(
name|transClass
operator|.
name|getPackage
argument_list|()
operator|.
name|equals
argument_list|(
name|DebugLogger
operator|.
name|class
operator|.
name|getPackage
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|transClass
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|transClass
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
DECL|class|DebugInfo
specifier|private
specifier|static
class|class
name|DebugInfo
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|tCount
DECL|field|rowCount
name|int
name|tCount
decl_stmt|,
name|rowCount
decl_stmt|;
DECL|field|lst
name|NamedList
name|lst
decl_stmt|;
DECL|field|type
name|DIHLogLevels
name|type
decl_stmt|;
DECL|field|parent
name|DebugInfo
name|parent
decl_stmt|;
DECL|method|DebugInfo
specifier|public
name|DebugInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|DIHLogLevels
name|type
parameter_list|,
name|DebugInfo
name|parent
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|lst
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|String
name|displayName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DIHLogLevels
operator|.
name|START_ENTITY
condition|)
block|{
name|displayName
operator|=
literal|"entity:"
operator|+
name|name
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DIHLogLevels
operator|.
name|TRANSFORMED_ROW
operator|||
name|type
operator|==
name|DIHLogLevels
operator|.
name|TRANSFORMER_EXCEPTION
condition|)
block|{
name|displayName
operator|=
literal|"transformer:"
operator|+
name|name
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DIHLogLevels
operator|.
name|START_DOC
condition|)
block|{
name|this
operator|.
name|name
operator|=
name|displayName
operator|=
literal|"document#"
operator|+
name|SolrWriter
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
name|parent
operator|.
name|lst
operator|.
name|add
argument_list|(
name|displayName
argument_list|,
name|lst
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

