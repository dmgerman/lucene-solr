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
name|Properties
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|TemplateUpdateProcessorFactory
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
name|update
operator|.
name|processor
operator|.
name|TemplateUpdateProcessorFactory
operator|.
name|Resolved
import|;
end_import

begin_comment
comment|/**  *<p>  * A set of nested maps that can resolve variables by namespaces. Variables are  * enclosed with a dollar sign then an opening curly brace, ending with a  * closing curly brace. Namespaces are delimited with '.' (period).  *</p>  *<p>  * This class also has special logic to resolve evaluator calls by recognizing  * the reserved function namespace: dataimporter.functions.xxx  *</p>  *<p>  * This class caches strings that have already been resolved from the current  * dih import.  *</p>  *<b>This API is experimental and may change in the future.</b>  *   *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|VariableResolver
specifier|public
class|class
name|VariableResolver
block|{
DECL|field|DOT_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|DOT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[.]"
argument_list|)
decl_stmt|;
DECL|field|EVALUATOR_FORMAT_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|EVALUATOR_FORMAT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(\\w*?)\\((.*?)\\)$"
argument_list|)
decl_stmt|;
DECL|field|rootNamespace
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rootNamespace
decl_stmt|;
DECL|field|evaluators
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Evaluator
argument_list|>
name|evaluators
decl_stmt|;
DECL|field|cache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Resolved
argument_list|>
name|cache
init|=
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|fun
specifier|private
name|Function
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fun
init|=
name|this
operator|::
name|resolve
decl_stmt|;
DECL|field|FUNCTIONS_NAMESPACE
specifier|public
specifier|static
specifier|final
name|String
name|FUNCTIONS_NAMESPACE
init|=
literal|"dataimporter.functions."
decl_stmt|;
DECL|field|FUNCTIONS_NAMESPACE_SHORT
specifier|public
specifier|static
specifier|final
name|String
name|FUNCTIONS_NAMESPACE_SHORT
init|=
literal|"dih.functions."
decl_stmt|;
DECL|method|VariableResolver
specifier|public
name|VariableResolver
parameter_list|()
block|{
name|rootNamespace
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|VariableResolver
specifier|public
name|VariableResolver
parameter_list|(
name|Properties
name|defaults
parameter_list|)
block|{
name|rootNamespace
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|defaults
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|rootNamespace
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
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
DECL|method|VariableResolver
specifier|public
name|VariableResolver
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|defaults
parameter_list|)
block|{
name|rootNamespace
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|defaults
argument_list|)
expr_stmt|;
block|}
comment|/**    * Resolves a given value with a name    *     * @param name    *          the String to be resolved    * @return an Object which is the result of evaluation of given name    */
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Object
name|r
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|nameParts
init|=
name|DOT_PATTERN
operator|.
name|split
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|CurrentLevel
name|cr
init|=
name|currentLevelMap
argument_list|(
name|nameParts
argument_list|,
name|rootNamespace
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|currentLevel
init|=
name|cr
operator|.
name|map
decl_stmt|;
name|r
operator|=
name|currentLevel
operator|.
name|get
argument_list|(
name|nameParts
index|[
name|nameParts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
name|FUNCTIONS_NAMESPACE
argument_list|)
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
name|FUNCTIONS_NAMESPACE
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|resolveEvaluator
argument_list|(
name|FUNCTIONS_NAMESPACE
argument_list|,
name|name
argument_list|)
return|;
block|}
if|if
condition|(
name|r
operator|==
literal|null
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
name|FUNCTIONS_NAMESPACE_SHORT
argument_list|)
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
name|FUNCTIONS_NAMESPACE_SHORT
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|resolveEvaluator
argument_list|(
name|FUNCTIONS_NAMESPACE_SHORT
argument_list|,
name|name
argument_list|)
return|;
block|}
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
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
name|cr
operator|.
name|level
init|;
name|i
operator|<
name|nameParts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|nameParts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|cr
operator|.
name|map
operator|.
name|get
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
operator|==
literal|null
condition|?
literal|""
else|:
name|r
return|;
block|}
DECL|method|resolveEvaluator
specifier|private
name|Object
name|resolveEvaluator
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|evaluators
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|Matcher
name|m
init|=
name|EVALUATOR_FORMAT_PATTERN
operator|.
name|matcher
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|namespace
operator|.
name|length
argument_list|()
argument_list|)
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
literal|""
return|;
name|ContextImpl
name|ctx
init|=
operator|new
name|ContextImpl
argument_list|(
literal|null
argument_list|,
name|this
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|g2
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
name|evaluator
operator|.
name|evaluate
argument_list|(
name|g2
argument_list|,
name|ctx
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
comment|/**    * Given a String with place holders, replace them with the value tokens.    *     * @return the string with the placeholders replaced with their values    */
DECL|method|replaceTokens
specifier|public
name|String
name|replaceTokens
parameter_list|(
name|String
name|template
parameter_list|)
block|{
return|return
name|TemplateUpdateProcessorFactory
operator|.
name|replaceTokens
argument_list|(
name|template
argument_list|,
name|cache
argument_list|,
name|fun
argument_list|)
return|;
block|}
DECL|method|addNamespace
specifier|public
name|void
name|addNamespace
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newMap
parameter_list|)
block|{
if|if
condition|(
name|newMap
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|nameParts
init|=
name|DOT_PATTERN
operator|.
name|split
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nameResolveLevel
init|=
name|currentLevelMap
argument_list|(
name|nameParts
argument_list|,
name|rootNamespace
argument_list|,
literal|false
argument_list|)
operator|.
name|map
decl_stmt|;
name|nameResolveLevel
operator|.
name|put
argument_list|(
name|nameParts
index|[
name|nameParts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|newMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|newMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
index|[]
name|keyParts
init|=
name|DOT_PATTERN
operator|.
name|split
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|currentLevel
init|=
name|rootNamespace
decl_stmt|;
name|currentLevel
operator|=
name|currentLevelMap
argument_list|(
name|keyParts
argument_list|,
name|currentLevel
argument_list|,
literal|false
argument_list|)
operator|.
name|map
expr_stmt|;
name|currentLevel
operator|.
name|put
argument_list|(
name|keyParts
index|[
name|keyParts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getVariables
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getVariables
parameter_list|(
name|String
name|expr
parameter_list|)
block|{
return|return
name|TemplateUpdateProcessorFactory
operator|.
name|getVariables
argument_list|(
name|expr
argument_list|,
name|cache
argument_list|)
return|;
block|}
DECL|class|CurrentLevel
class|class
name|CurrentLevel
block|{
DECL|field|map
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
DECL|field|level
specifier|final
name|int
name|level
decl_stmt|;
DECL|method|CurrentLevel
name|CurrentLevel
parameter_list|(
name|int
name|level
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
block|}
DECL|method|currentLevelMap
specifier|private
name|CurrentLevel
name|currentLevelMap
parameter_list|(
name|String
index|[]
name|keyParts
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|currentLevel
parameter_list|,
name|boolean
name|includeLastLevel
parameter_list|)
block|{
name|int
name|j
init|=
name|includeLastLevel
condition|?
name|keyParts
operator|.
name|length
else|:
name|keyParts
operator|.
name|length
operator|-
literal|1
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
name|j
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|o
init|=
name|currentLevel
operator|.
name|get
argument_list|(
name|keyParts
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|j
operator|-
literal|1
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextLevel
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|currentLevel
operator|.
name|put
argument_list|(
name|keyParts
index|[
name|i
index|]
argument_list|,
name|nextLevel
argument_list|)
expr_stmt|;
name|currentLevel
operator|=
name|nextLevel
expr_stmt|;
block|}
else|else
block|{
return|return
operator|new
name|CurrentLevel
argument_list|(
name|i
argument_list|,
name|currentLevel
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextLevel
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
name|currentLevel
operator|=
name|nextLevel
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Non-leaf nodes should be of type java.util.Map"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|CurrentLevel
argument_list|(
name|j
operator|-
literal|1
argument_list|,
name|currentLevel
argument_list|)
return|;
block|}
DECL|method|removeNamespace
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|rootNamespace
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|setEvaluators
specifier|public
name|void
name|setEvaluators
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Evaluator
argument_list|>
name|evaluators
parameter_list|)
block|{
name|this
operator|.
name|evaluators
operator|=
name|evaluators
expr_stmt|;
block|}
block|}
end_class

end_unit

