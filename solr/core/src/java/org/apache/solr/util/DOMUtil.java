begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|StrUtils
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
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DOMUtil
specifier|public
class|class
name|DOMUtil
block|{
DECL|field|XML_RESERVED_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|XML_RESERVED_PREFIX
init|=
literal|"xml"
decl_stmt|;
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
argument_list|<>
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
comment|// automatically exclude things in the xml namespace, ie: xml:base
if|if
condition|(
name|XML_RESERVED_PREFIX
operator|.
name|equals
argument_list|(
name|attr
operator|.
name|getPrefix
argument_list|()
argument_list|)
condition|)
continue|continue
name|outer
continue|;
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
DECL|method|getAttrOrDefault
specifier|public
specifier|static
name|String
name|getAttrOrDefault
parameter_list|(
name|Node
name|nd
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|attr
init|=
name|getAttr
argument_list|(
name|nd
operator|.
name|getAttributes
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|attr
operator|==
literal|null
condition|?
name|def
else|:
name|attr
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
argument_list|<
name|Object
argument_list|>
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
argument_list|<
name|Object
argument_list|>
name|nodesToNamedList
parameter_list|(
name|NodeList
name|nlst
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|clst
init|=
operator|new
name|NamedList
argument_list|<>
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
comment|/**    * Examines a Node from the DOM representation of a NamedList and adds the    * contents of that node to both the specified NamedList and List passed    * as arguments.    *    * @param nd The Node whose type will be used to determine how to parse the    *           text content.  If there is a 'name' attribute it will be used    *           when adding to the NamedList    * @param nlst A NamedList to add the item to with name if application.    *             If this param is null it will be ignored.    * @param arr A List to add the item to.    *             If this param is null it will be ignored.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
specifier|final
name|String
name|type
init|=
name|nd
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|getAttr
argument_list|(
name|nd
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|Object
name|val
init|=
literal|null
decl_stmt|;
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
else|else
block|{
specifier|final
name|String
name|textValue
init|=
name|getText
argument_list|(
name|nd
argument_list|)
decl_stmt|;
try|try
block|{
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
name|textValue
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
name|textValue
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
name|textValue
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
name|textValue
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
name|textValue
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
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|textValue
argument_list|)
expr_stmt|;
block|}
comment|// :NOTE: Unexpected Node names are ignored
comment|// :TODO: should we generate an error here?
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
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
literal|"Value "
operator|+
operator|(
literal|null
operator|!=
name|name
condition|?
operator|(
literal|"of '"
operator|+
name|name
operator|+
literal|"' "
operator|)
else|:
literal|""
operator|)
operator|+
literal|"can not be parsed as '"
operator|+
name|type
operator|+
literal|"': \""
operator|+
name|textValue
operator|+
literal|"\""
argument_list|,
name|nfe
argument_list|)
throw|;
block|}
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
comment|/* Putting Attribute nodes in this section does not exactly          match the definition of how textContent should behave          according to the DOM Level-3 Core documentation - which          specifies that the Attr's children should have their          textContent concated (Attr's can have a single child which          is either Text node or an EntityReference).  In practice,          DOM implementations do not seem to use child nodes of          Attributes, storing the "text" directly as the nodeValue.          Fortunately, the DOM Spec indicates that when Attr.nodeValue          is read, it should return the nodeValue from the child Node,          so this approach should work both for strict implementations,          and implementations actually encountered.       */
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
comment|/**    * Replaces ${system.property[:default value]} references in all attributes    * and text nodes of supplied node.  If the system property is not defined and no    * default value is provided, a runtime exception is thrown.    *    * @param node DOM node to walk for substitutions    */
DECL|method|substituteSystemProperties
specifier|public
specifier|static
name|void
name|substituteSystemProperties
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|substituteProperties
argument_list|(
name|node
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Replaces ${property[:default value]} references in all attributes    * and text nodes of supplied node.  If the property is not defined neither in the    * given Properties instance nor in System.getProperty and no    * default value is provided, a runtime exception is thrown.    *    * @param node DOM node to walk for substitutions    * @param properties the Properties instance from which a value can be looked up    */
DECL|method|substituteProperties
specifier|public
specifier|static
name|void
name|substituteProperties
parameter_list|(
name|Node
name|node
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
comment|// loop through child nodes
name|Node
name|child
decl_stmt|;
name|Node
name|next
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|child
operator|=
name|next
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// set next before we change anything
name|next
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
comment|// handle child by node type
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
name|child
operator|.
name|setNodeValue
argument_list|(
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|child
operator|.
name|getNodeValue
argument_list|()
argument_list|,
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
comment|// handle child elements with recursive call
name|NamedNodeMap
name|attributes
init|=
name|child
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
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|attribute
init|=
name|attributes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|attribute
operator|.
name|setNodeValue
argument_list|(
name|PropertiesUtil
operator|.
name|substituteProperty
argument_list|(
name|attribute
operator|.
name|getNodeValue
argument_list|()
argument_list|,
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|substituteProperties
argument_list|(
name|child
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|substituteProperty
specifier|public
specifier|static
name|String
name|substituteProperty
parameter_list|(
name|String
name|value
parameter_list|,
name|Properties
name|coreProperties
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|indexOf
argument_list|(
literal|'$'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|value
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|fragments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|propertyRefs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|parsePropertyString
argument_list|(
name|value
argument_list|,
name|fragments
argument_list|,
name|propertyRefs
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|fragments
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|j
init|=
name|propertyRefs
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|fragment
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
name|String
name|propertyName
init|=
name|j
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|defaultValue
init|=
literal|null
decl_stmt|;
name|int
name|colon_index
init|=
name|propertyName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colon_index
operator|>
operator|-
literal|1
condition|)
block|{
name|defaultValue
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
name|colon_index
operator|+
literal|1
argument_list|)
expr_stmt|;
name|propertyName
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colon_index
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|coreProperties
operator|!=
literal|null
condition|)
block|{
name|fragment
operator|=
name|coreProperties
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fragment
operator|==
literal|null
condition|)
block|{
name|fragment
operator|=
name|System
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|,
name|defaultValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fragment
operator|==
literal|null
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
literal|"No system property or default value specified for "
operator|+
name|propertyName
operator|+
literal|" value:"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
name|fragment
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/*    * This method borrowed from Ant's PropertyHelper.parsePropertyStringDefault:    *   http://svn.apache.org/repos/asf/ant/core/trunk/src/main/org/apache/tools/ant/PropertyHelper.java    */
DECL|method|parsePropertyString
specifier|private
specifier|static
name|void
name|parsePropertyString
parameter_list|(
name|String
name|value
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fragments
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|propertyRefs
parameter_list|)
block|{
name|int
name|prev
init|=
literal|0
decl_stmt|;
name|int
name|pos
decl_stmt|;
comment|//search for the next instance of $ from the 'prev' position
while|while
condition|(
operator|(
name|pos
operator|=
name|value
operator|.
name|indexOf
argument_list|(
literal|"$"
argument_list|,
name|prev
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
comment|//if there was any text before this, add it as a fragment
comment|//TODO, this check could be modified to go if pos>prev;
comment|//seems like this current version could stick empty strings
comment|//into the list
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|fragments
operator|.
name|add
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|prev
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//if we are at the end of the string, we tack on a $
comment|//then move past it
if|if
condition|(
name|pos
operator|==
operator|(
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
condition|)
block|{
name|fragments
operator|.
name|add
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|prev
operator|=
name|pos
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|!=
literal|'{'
condition|)
block|{
comment|//peek ahead to see if the next char is a property or not
comment|//not a property: insert the char as a literal
comment|/*               fragments.addElement(value.substring(pos + 1, pos + 2));               prev = pos + 2;               */
if|if
condition|(
name|value
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|==
literal|'$'
condition|)
block|{
comment|//backwards compatibility two $ map to one mode
name|fragments
operator|.
name|add
argument_list|(
literal|"$"
argument_list|)
expr_stmt|;
name|prev
operator|=
name|pos
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
comment|//new behaviour: $X maps to $X for all values of X!='$'
name|fragments
operator|.
name|add
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|prev
operator|=
name|pos
operator|+
literal|2
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//property found, extract its name or bail on a typo
name|int
name|endName
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|endName
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Syntax error in property: "
operator|+
name|value
argument_list|)
throw|;
block|}
name|String
name|propertyName
init|=
name|value
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|2
argument_list|,
name|endName
argument_list|)
decl_stmt|;
name|fragments
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|propertyRefs
operator|.
name|add
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|prev
operator|=
name|endName
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|//no more $ signs found
comment|//if there is any tail to the string, append it
if|if
condition|(
name|prev
operator|<
name|value
operator|.
name|length
argument_list|()
condition|)
block|{
name|fragments
operator|.
name|add
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|prev
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

