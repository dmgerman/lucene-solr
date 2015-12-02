begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport.config
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
operator|.
name|config
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
name|Collections
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
name|DataImporter
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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

begin_class
DECL|class|ConfigParseUtil
specifier|public
class|class
name|ConfigParseUtil
block|{
DECL|method|getStringAttribute
specifier|public
specifier|static
name|String
name|getStringAttribute
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|r
init|=
name|e
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|r
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
name|r
operator|=
name|def
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|getAllAttributes
specifier|public
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllAttributes
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|nnm
init|=
name|e
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
name|m
operator|.
name|put
argument_list|(
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
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
return|return
name|m
return|;
block|}
DECL|method|getText
specifier|public
specifier|static
name|String
name|getText
parameter_list|(
name|Node
name|elem
parameter_list|,
name|StringBuilder
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|elem
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
name|NodeList
name|childs
init|=
name|elem
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
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|elem
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getChildNodes
specifier|public
specifier|static
name|List
argument_list|<
name|Element
argument_list|>
name|getChildNodes
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|byName
parameter_list|)
block|{
name|List
argument_list|<
name|Element
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|NodeList
name|l
init|=
name|e
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
name|l
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|e
operator|.
name|equals
argument_list|(
name|l
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getParentNode
argument_list|()
argument_list|)
operator|&&
name|byName
operator|.
name|equals
argument_list|(
name|l
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
operator|(
name|Element
operator|)
name|l
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

