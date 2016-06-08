begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|NodeList
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
name|Arrays
operator|.
name|asList
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

begin_import
import|import static
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
name|CoreAdminParams
operator|.
name|NAME
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
name|schema
operator|.
name|FieldType
operator|.
name|CLASS_NAME
import|;
end_import

begin_comment
comment|/**  * An Object which represents a Plugin of any type   *  */
end_comment

begin_class
DECL|class|PluginInfo
specifier|public
class|class
name|PluginInfo
implements|implements
name|MapSerializable
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
DECL|field|isFromSolrConfig
specifier|private
name|boolean
name|isFromSolrConfig
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
name|NAME
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
name|CLASS_NAME
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
name|isFromSolrConfig
operator|=
literal|false
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
name|NAME
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
name|CLASS_NAME
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
name|attributes
operator|=
name|unmodifiableMap
argument_list|(
name|DOMUtil
operator|.
name|toMap
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|children
operator|=
name|loadSubPlugins
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|isFromSolrConfig
operator|=
literal|true
expr_stmt|;
block|}
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
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|LinkedHashMap
name|m
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|initArgs
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
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
block|{
if|if
condition|(
name|NAME
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
name|CLASS_NAME
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
continue|continue;
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
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
name|value
decl_stmt|;
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
operator|&&
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|Map
condition|)
block|{
comment|//this is a subcomponent
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
name|o
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|)
expr_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|initArgs
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
name|value
operator|=
operator|new
name|NamedList
argument_list|(
operator|(
name|Map
operator|)
name|value
argument_list|)
expr_stmt|;
name|initArgs
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
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
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|className
operator|=
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
name|CLASS_NAME
argument_list|)
expr_stmt|;
name|attributes
operator|=
name|unmodifiableMap
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|Collections
operator|.
expr|<
name|PluginInfo
operator|>
name|emptyList
argument_list|()
expr_stmt|;
name|isFromSolrConfig
operator|=
literal|true
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
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//if there is another sub tag with a non namedlist tag that has to be another plugin
name|NodeList
name|nlst
init|=
name|node
operator|.
name|getChildNodes
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
name|nlst
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|nd
init|=
name|nlst
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|nd
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
continue|continue;
if|if
condition|(
name|NL_TAGS
operator|.
name|contains
argument_list|(
name|nd
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
continue|continue;
name|PluginInfo
name|pluginInfo
init|=
operator|new
name|PluginInfo
argument_list|(
name|nd
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
return|return
name|children
operator|.
name|isEmpty
argument_list|()
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
operator|!=
literal|null
operator|&&
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
DECL|method|getChild
specifier|public
name|PluginInfo
name|getChild
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|l
init|=
name|getChildren
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|l
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|toMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|toMap
parameter_list|()
block|{
name|LinkedHashMap
name|m
init|=
operator|new
name|LinkedHashMap
argument_list|(
name|attributes
argument_list|)
decl_stmt|;
if|if
condition|(
name|initArgs
operator|!=
literal|null
condition|)
name|m
operator|.
name|putAll
argument_list|(
name|initArgs
operator|.
name|asMap
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PluginInfo
name|child
range|:
name|children
control|)
block|{
name|Object
name|old
init|=
name|m
operator|.
name|get
argument_list|(
name|child
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|m
operator|.
name|put
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|child
operator|.
name|toMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|old
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
name|old
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|child
operator|.
name|toMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|child
operator|.
name|toMap
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|m
return|;
block|}
comment|/**Filter children by type    * @param type The type name. must not be null    * @return The mathcing children    */
DECL|method|getChildren
specifier|public
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|getChildren
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|children
return|;
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PluginInfo
name|child
range|:
name|children
control|)
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|child
operator|.
name|type
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|field|EMPTY_INFO
specifier|public
specifier|static
specifier|final
name|PluginInfo
name|EMPTY_INFO
init|=
operator|new
name|PluginInfo
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|NamedList
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|PluginInfo
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|NL_TAGS
specifier|private
specifier|static
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|NL_TAGS
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|asList
argument_list|(
literal|"lst"
argument_list|,
literal|"arr"
argument_list|,
literal|"bool"
argument_list|,
literal|"str"
argument_list|,
literal|"int"
argument_list|,
literal|"long"
argument_list|,
literal|"float"
argument_list|,
literal|"double"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|DEFAULTS
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULTS
init|=
literal|"defaults"
decl_stmt|;
DECL|field|APPENDS
specifier|public
specifier|static
specifier|final
name|String
name|APPENDS
init|=
literal|"appends"
decl_stmt|;
DECL|field|INVARIANTS
specifier|public
specifier|static
specifier|final
name|String
name|INVARIANTS
init|=
literal|"invariants"
decl_stmt|;
DECL|method|isFromSolrConfig
specifier|public
name|boolean
name|isFromSolrConfig
parameter_list|()
block|{
return|return
name|isFromSolrConfig
return|;
block|}
DECL|method|copy
specifier|public
name|PluginInfo
name|copy
parameter_list|()
block|{
name|PluginInfo
name|result
init|=
operator|new
name|PluginInfo
argument_list|(
name|type
argument_list|,
name|attributes
argument_list|,
name|initArgs
operator|.
name|clone
argument_list|()
argument_list|,
name|children
argument_list|)
decl_stmt|;
name|result
operator|.
name|isFromSolrConfig
operator|=
name|isFromSolrConfig
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

