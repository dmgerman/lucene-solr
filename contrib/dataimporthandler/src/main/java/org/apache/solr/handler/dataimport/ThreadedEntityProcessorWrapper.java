begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|EntityProcessorBase
operator|.
name|ON_ERROR
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
name|EntityProcessorBase
operator|.
name|ABORT
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
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|HashMap
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

begin_comment
comment|/**  * Each Entity may have only a single EntityProcessor .  But the same entity can be run by  * multiple EntityProcessorWrapper (1 per thread) . this helps running transformations in multiple threads  * @since Solr 1.5  */
end_comment

begin_class
DECL|class|ThreadedEntityProcessorWrapper
specifier|public
class|class
name|ThreadedEntityProcessorWrapper
extends|extends
name|EntityProcessorWrapper
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ThreadedEntityProcessorWrapper
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|entityRunner
specifier|final
name|DocBuilder
operator|.
name|EntityRunner
name|entityRunner
decl_stmt|;
comment|/**For each child entity there is one EntityRunner    */
DECL|field|children
specifier|final
name|Map
argument_list|<
name|DataConfig
operator|.
name|Entity
argument_list|,
name|DocBuilder
operator|.
name|EntityRunner
argument_list|>
name|children
decl_stmt|;
DECL|method|ThreadedEntityProcessorWrapper
specifier|public
name|ThreadedEntityProcessorWrapper
parameter_list|(
name|EntityProcessor
name|delegate
parameter_list|,
name|DocBuilder
name|docBuilder
parameter_list|,
name|DocBuilder
operator|.
name|EntityRunner
name|entityRunner
parameter_list|,
name|VariableResolverImpl
name|resolver
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
name|docBuilder
argument_list|)
expr_stmt|;
name|this
operator|.
name|entityRunner
operator|=
name|entityRunner
expr_stmt|;
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
if|if
condition|(
name|entityRunner
operator|.
name|entity
operator|.
name|entities
operator|==
literal|null
condition|)
block|{
name|children
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|children
operator|=
operator|new
name|HashMap
argument_list|<
name|DataConfig
operator|.
name|Entity
argument_list|,
name|DocBuilder
operator|.
name|EntityRunner
argument_list|>
argument_list|(
name|entityRunner
operator|.
name|entity
operator|.
name|entities
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DataConfig
operator|.
name|Entity
name|e
range|:
name|entityRunner
operator|.
name|entity
operator|.
name|entities
control|)
block|{
name|DocBuilder
operator|.
name|EntityRunner
name|runner
init|=
name|docBuilder
operator|.
name|createRunner
argument_list|(
name|e
argument_list|,
name|entityRunner
argument_list|)
decl_stmt|;
name|children
operator|.
name|put
argument_list|(
name|e
argument_list|,
name|runner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|threadedInit
name|void
name|threadedInit
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|rowcache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|resolver
operator|=
operator|(
name|VariableResolverImpl
operator|)
name|context
operator|.
name|getVariableResolver
argument_list|()
expr_stmt|;
comment|//context has to be set correctly . keep the copy of the old one so that it can be restored in destroy
if|if
condition|(
name|entityName
operator|==
literal|null
condition|)
block|{
name|onError
operator|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|ON_ERROR
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|onError
operator|==
literal|null
condition|)
name|onError
operator|=
name|ABORT
expr_stmt|;
name|entityName
operator|=
name|context
operator|.
name|getEntityAttribute
argument_list|(
name|DataConfig
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|rowcache
operator|!=
literal|null
condition|)
block|{
return|return
name|getFromRowCache
argument_list|()
return|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|arow
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|delegate
init|)
block|{
if|if
condition|(
name|entityRunner
operator|.
name|entityEnded
operator|.
name|get
argument_list|()
condition|)
return|return
literal|null
return|;
try|try
block|{
name|arow
operator|=
name|delegate
operator|.
name|nextRow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|ABORT
operator|.
name|equals
argument_list|(
name|onError
argument_list|)
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//SKIP is not really possible. If this calls the nextRow() again the Entityprocessor would be in an inconistent state
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in entity : "
operator|+
name|entityName
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"arow : "
operator|+
name|arow
argument_list|)
expr_stmt|;
if|if
condition|(
name|arow
operator|==
literal|null
condition|)
name|entityRunner
operator|.
name|entityEnded
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|arow
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|arow
operator|=
name|applyTransformer
argument_list|(
name|arow
argument_list|)
expr_stmt|;
if|if
condition|(
name|arow
operator|!=
literal|null
condition|)
block|{
name|delegate
operator|.
name|postTransform
argument_list|(
name|arow
argument_list|)
expr_stmt|;
return|return
name|arow
return|;
block|}
block|}
block|}
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|DocBuilder
operator|.
name|EntityRow
name|rows
parameter_list|)
block|{
for|for
control|(
name|DocBuilder
operator|.
name|EntityRow
name|row
init|=
name|rows
init|;
name|row
operator|!=
literal|null
condition|;
name|row
operator|=
name|row
operator|.
name|tail
control|)
name|resolver
operator|.
name|addNamespace
argument_list|(
name|row
operator|.
name|name
argument_list|,
name|row
operator|.
name|row
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

