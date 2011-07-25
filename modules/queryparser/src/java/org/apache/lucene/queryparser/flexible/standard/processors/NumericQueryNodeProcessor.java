begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|messages
operator|.
name|MessageImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|FieldQueryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|NumericConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|NumericQueryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|NumericRangeQueryNode
import|;
end_import

begin_comment
comment|/**  * This processor is used to convert {@link FieldQueryNode}s to  * {@link NumericRangeQueryNode}s. It looks for  * {@link ConfigurationKeys#NUMERIC_CONFIG} set in the {@link FieldConfig} of  * every {@link FieldQueryNode} found. If  * {@link ConfigurationKeys#NUMERIC_CONFIG} is found, it considers that  * {@link FieldQueryNode} to be a numeric query and convert it to  * {@link NumericRangeQueryNode} with upper and lower inclusive and lower and  * upper equals to the value represented by the {@link FieldQueryNode} converted  * to {@link Number}. It means that<b>field:1</b> is converted to<b>field:[1 TO  * 1]</b>.<br/>  *<br/>  * Note that {@link ParametricQueryNode}s are ignored, even being a  * {@link FieldQueryNode}.  *   * @see ConfigurationKeys#NUMERIC_CONFIG  * @see FieldQueryNode  * @see NumericConfig  * @see NumericQueryNode  */
end_comment

begin_class
DECL|class|NumericQueryNodeProcessor
specifier|public
class|class
name|NumericQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
comment|/**    * Constructs a {@link NumericQueryNodeProcessor} object.    */
DECL|method|NumericQueryNodeProcessor
specifier|public
name|NumericQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|FieldQueryNode
operator|&&
operator|!
operator|(
name|node
operator|instanceof
name|ParametricQueryNode
operator|)
condition|)
block|{
name|QueryConfigHandler
name|config
init|=
name|getQueryConfigHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|FieldQueryNode
name|fieldNode
init|=
operator|(
name|FieldQueryNode
operator|)
name|node
decl_stmt|;
name|FieldConfig
name|fieldConfig
init|=
name|config
operator|.
name|getFieldConfig
argument_list|(
name|fieldNode
operator|.
name|getFieldAsString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldConfig
operator|!=
literal|null
condition|)
block|{
name|NumericConfig
name|numericConfig
init|=
name|fieldConfig
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|NUMERIC_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|numericConfig
operator|!=
literal|null
condition|)
block|{
name|NumberFormat
name|numberFormat
init|=
name|numericConfig
operator|.
name|getNumberFormat
argument_list|()
decl_stmt|;
name|Number
name|number
decl_stmt|;
try|try
block|{
name|number
operator|=
name|numberFormat
operator|.
name|parse
argument_list|(
name|fieldNode
operator|.
name|getTextAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryNodeParseException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|COULD_NOT_PARSE_NUMBER
argument_list|,
name|fieldNode
operator|.
name|getTextAsString
argument_list|()
argument_list|,
name|numberFormat
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|numericConfig
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|LONG
case|:
name|number
operator|=
name|number
operator|.
name|longValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|number
operator|=
name|number
operator|.
name|intValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|number
operator|=
name|number
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|number
operator|=
name|number
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
name|NumericQueryNode
name|lowerNode
init|=
operator|new
name|NumericQueryNode
argument_list|(
name|fieldNode
operator|.
name|getField
argument_list|()
argument_list|,
name|number
argument_list|,
name|numberFormat
argument_list|)
decl_stmt|;
name|NumericQueryNode
name|upperNode
init|=
operator|new
name|NumericQueryNode
argument_list|(
name|fieldNode
operator|.
name|getField
argument_list|()
argument_list|,
name|number
argument_list|,
name|numberFormat
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericRangeQueryNode
argument_list|(
name|lowerNode
argument_list|,
name|upperNode
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|numericConfig
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

