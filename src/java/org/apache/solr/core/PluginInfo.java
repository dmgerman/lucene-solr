begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

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
name|DOMUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_comment
comment|/**  * An Object which represents a Plugin of any type   * @version $Id$  */
end_comment

begin_class
DECL|class|PluginInfo
specifier|public
class|class
name|PluginInfo
block|{
DECL|field|name
DECL|field|className
DECL|field|type
specifier|public
specifier|final
name|String
name|name
decl_stmt|,
name|className
decl_stmt|,
name|type
decl_stmt|;
DECL|field|initArgs
specifier|public
specifier|final
name|NamedList
name|initArgs
decl_stmt|;
DECL|field|attributes
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|field|children
specifier|public
specifier|final
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|children
decl_stmt|;
DECL|method|PluginInfo
specifier|public
name|PluginInfo
parameter_list|(
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
parameter_list|,
name|NamedList
name|initArgs
parameter_list|,
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|children
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|attrs
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|attrs
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
expr_stmt|;
name|this
operator|.
name|initArgs
operator|=
name|initArgs
expr_stmt|;
name|attributes
operator|=
name|attrs
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|emptyMap
argument_list|()
operator|:
name|unmodifiableMap
argument_list|(
name|attrs
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|PluginInfo
operator|>
name|emptyList
argument_list|()
else|:
name|unmodifiableList
argument_list|(
name|children
argument_list|)
expr_stmt|;
block|}
DECL|method|PluginInfo
specifier|public
name|PluginInfo
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|err
parameter_list|,
name|boolean
name|requireName
parameter_list|,
name|boolean
name|requireClass
parameter_list|)
block|{
name|type
operator|=
name|node
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
name|name
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"name"
argument_list|,
name|requireName
condition|?
name|err
else|:
literal|null
argument_list|)
expr_stmt|;
name|className
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"class"
argument_list|,
name|requireClass
condition|?
name|err
else|:
literal|null
argument_list|)
expr_stmt|;
name|initArgs
operator|=
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|nnm
init|=
name|node
operator|.
name|getAttributes
argument_list|()
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
name|nnm
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attributes
operator|=
name|unmodifiableMap
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|children
operator|=
name|loadSubPlugins
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
DECL|method|loadSubPlugins
specifier|private
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|loadSubPlugins
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|children
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//if there is another sub tag with a 'class' attribute that has to be another plugin
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|Config
operator|.
name|xpathFactory
operator|.
name|newXPath
argument_list|()
operator|.
name|evaluate
argument_list|(
literal|"*[@class]"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|children
operator|=
operator|new
name|ArrayList
argument_list|<
name|PluginInfo
argument_list|>
argument_list|(
name|nodes
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|PluginInfo
name|pluginInfo
init|=
operator|new
name|PluginInfo
argument_list|(
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|pluginInfo
operator|.
name|isEnabled
argument_list|()
condition|)
name|children
operator|.
name|add
argument_list|(
name|pluginInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{ }
return|return
name|children
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|PluginInfo
operator|>
name|emptyList
argument_list|()
else|:
name|unmodifiableList
argument_list|(
name|children
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"type = "
operator|+
name|type
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"name = "
operator|+
name|name
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"class = "
operator|+
name|className
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|initArgs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"args = "
operator|+
name|initArgs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
name|String
name|enable
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"enable"
argument_list|)
decl_stmt|;
return|return
name|enable
operator|==
literal|null
operator|||
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|enable
argument_list|)
return|;
block|}
DECL|method|isDefault
specifier|public
name|boolean
name|isDefault
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
literal|"default"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

