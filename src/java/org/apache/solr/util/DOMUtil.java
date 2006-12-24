begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|List
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

begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|DOMUtil
specifier|public
class|class
name|DOMUtil
block|{
DECL|method|toMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toMap
parameter_list|(
name|NamedNodeMap
name|attrs
parameter_list|)
block|{
return|return
name|toMapExcept
argument_list|(
name|attrs
argument_list|)
return|;
block|}
DECL|method|toMapExcept
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toMapExcept
parameter_list|(
name|NamedNodeMap
name|attrs
parameter_list|,
name|String
modifier|...
name|exclusions
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|outer
label|:
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|attr
init|=
name|attrs
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|String
name|attrName
init|=
name|attr
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|ex
range|:
name|exclusions
control|)
if|if
condition|(
name|ex
operator|.
name|equals
argument_list|(
name|attrName
argument_list|)
condition|)
continue|continue
name|outer
continue|;
name|String
name|val
init|=
name|attr
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|attrName
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|args
return|;
block|}
DECL|method|getChild
specifier|public
specifier|static
name|Node
name|getChild
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|hasChildNodes
argument_list|()
condition|)
return|return
literal|null
return|;
name|NodeList
name|lst
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|lst
operator|==
literal|null
condition|)
return|return
literal|null
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lst
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|lst
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
return|return
name|child
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getAttr
specifier|public
specifier|static
name|String
name|getAttr
parameter_list|(
name|NamedNodeMap
name|attrs
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|getAttr
argument_list|(
name|attrs
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getAttr
specifier|public
specifier|static
name|String
name|getAttr
parameter_list|(
name|Node
name|nd
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|getAttr
argument_list|(
name|nd
operator|.
name|getAttributes
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|getAttr
specifier|public
specifier|static
name|String
name|getAttr
parameter_list|(
name|NamedNodeMap
name|attrs
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|missing_err
parameter_list|)
block|{
name|Node
name|attr
init|=
name|attrs
operator|==
literal|null
condition|?
literal|null
else|:
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|missing_err
operator|==
literal|null
condition|)
return|return
literal|null
return|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|missing_err
operator|+
literal|": missing mandatory attribute '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|String
name|val
init|=
name|attr
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
return|return
name|val
return|;
block|}
DECL|method|getAttr
specifier|public
specifier|static
name|String
name|getAttr
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|missing_err
parameter_list|)
block|{
return|return
name|getAttr
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|,
name|name
argument_list|,
name|missing_err
argument_list|)
return|;
block|}
comment|//////////////////////////////////////////////////////////
comment|// Routines to parse XML in the syntax of the Solr query
comment|// response schema.
comment|// Should these be moved to Config?  Should all of these things?
comment|//////////////////////////////////////////////////////////
DECL|method|childNodesToNamedList
specifier|public
specifier|static
name|NamedList
name|childNodesToNamedList
parameter_list|(
name|Node
name|nd
parameter_list|)
block|{
return|return
name|nodesToNamedList
argument_list|(
name|nd
operator|.
name|getChildNodes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|childNodesToList
specifier|public
specifier|static
name|List
name|childNodesToList
parameter_list|(
name|Node
name|nd
parameter_list|)
block|{
return|return
name|nodesToList
argument_list|(
name|nd
operator|.
name|getChildNodes
argument_list|()
argument_list|)
return|;
block|}
DECL|method|nodesToNamedList
specifier|public
specifier|static
name|NamedList
name|nodesToNamedList
parameter_list|(
name|NodeList
name|nlst
parameter_list|)
block|{
name|NamedList
name|clst
init|=
operator|new
name|NamedList
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
name|addToNamedList
argument_list|(
name|nlst
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
name|clst
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|clst
return|;
block|}
DECL|method|nodesToList
specifier|public
specifier|static
name|List
name|nodesToList
parameter_list|(
name|NodeList
name|nlst
parameter_list|)
block|{
name|List
name|lst
init|=
operator|new
name|ArrayList
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
name|addToNamedList
argument_list|(
name|nlst
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
literal|null
argument_list|,
name|lst
argument_list|)
expr_stmt|;
block|}
return|return
name|lst
return|;
block|}
DECL|method|addToNamedList
specifier|public
specifier|static
name|void
name|addToNamedList
parameter_list|(
name|Node
name|nd
parameter_list|,
name|NamedList
name|nlst
parameter_list|,
name|List
name|arr
parameter_list|)
block|{
comment|// Nodes often include whitespace, etc... so just return if this
comment|// is not an Element.
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
return|return;
name|String
name|type
init|=
name|nd
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nd
operator|.
name|hasAttributes
argument_list|()
condition|)
block|{
name|NamedNodeMap
name|attrs
init|=
name|nd
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Node
name|nameNd
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nameNd
operator|!=
literal|null
condition|)
name|name
operator|=
name|nameNd
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
name|Object
name|val
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"str"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|getText
argument_list|(
name|nd
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"int"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|getText
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"long"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|getText
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"float"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|getText
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"double"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|getText
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"bool"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|getText
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"lst"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|childNodesToNamedList
argument_list|(
name|nd
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"arr"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|val
operator|=
name|childNodesToList
argument_list|(
name|nd
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nlst
operator|!=
literal|null
condition|)
name|nlst
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
if|if
condition|(
name|arr
operator|!=
literal|null
condition|)
name|arr
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Drop in replacement for Node.getTextContent().    *    *<p>    * This method is provided to support the same functionality as    * Node.getTextContent() but in a way that is DOM Level 2 compatible.    *</p>    *    * @see<a href="http://www.w3.org/TR/DOM-Level-3-Core/core.html#Node3-textContent">DOM Object Model Core</a>    */
DECL|method|getText
specifier|public
specifier|static
name|String
name|getText
parameter_list|(
name|Node
name|nd
parameter_list|)
block|{
name|short
name|type
init|=
name|nd
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
comment|// for most node types, we can defer to the recursive helper method,
comment|// but when asked for the text of these types, we must return null
comment|// (Not the empty string)
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|NOTATION_NODE
case|:
comment|/* fall through */
return|return
literal|null
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|getText
argument_list|(
name|nd
argument_list|,
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** @see #getText(Node) */
DECL|method|getText
specifier|private
specifier|static
name|void
name|getText
parameter_list|(
name|Node
name|nd
parameter_list|,
name|StringBuilder
name|buf
parameter_list|)
block|{
name|short
name|type
init|=
name|nd
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|ENTITY_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|ENTITY_REFERENCE_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|DOCUMENT_FRAGMENT_NODE
case|:
name|NodeList
name|childs
init|=
name|nd
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
name|childs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|childs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|short
name|childType
init|=
name|child
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|childType
operator|!=
name|Node
operator|.
name|COMMENT_NODE
operator|&&
name|childType
operator|!=
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
condition|)
block|{
name|getText
argument_list|(
name|child
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
comment|/* fall through */
comment|/* Putting Attribute nodes in this section does not exactly           match the definition of how textContent should behave           according to the DOM Level-3 Core documentation - which           specifies that the Attr's children should have their           textContent concated (Attr's can have a single child which           is either Text node or an EntityRefrence).  In practice,          DOM implementations do not seem to use child nodes of           Attributes, storing the "text" directly as the nodeValue.          Fortunately, the DOM Spec indicates that when Attr.nodeValue           is read, it should return the nodeValue from the child Node,           so this approach should work both for strict implementations,           and implementations actually encountered.       */
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
comment|/* fall through */
name|buf
operator|.
name|append
argument_list|(
name|nd
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
case|:
comment|/* fall through */
case|case
name|Node
operator|.
name|NOTATION_NODE
case|:
comment|/* fall through */
default|default:
comment|/* :NOOP: */
block|}
block|}
block|}
end_class

end_unit

