begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Map
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|DoubleFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|FloatFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|IntFieldSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LongFieldSource
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
name|search
operator|.
name|FieldCache
operator|.
name|DoubleParser
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
name|search
operator|.
name|FieldCache
operator|.
name|FloatParser
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
name|search
operator|.
name|FieldCache
operator|.
name|IntParser
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
name|search
operator|.
name|FieldCache
operator|.
name|LongParser
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
name|search
operator|.
name|SortField
import|;
end_import

begin_comment
comment|/**  * Simple class that binds expression variable names to {@link SortField}s  * or other {@link Expression}s.  *<p>  * Example usage:  *<pre class="prettyprint">  *   SimpleBindings bindings = new SimpleBindings();  *   // document's text relevance score  *   bindings.add(new SortField("_score", SortField.Type.SCORE));  *   // integer NumericDocValues field (or from FieldCache)   *   bindings.add(new SortField("popularity", SortField.Type.INT));  *   // another expression  *   bindings.add("recency", myRecencyExpression);  *     *   // create a sort field in reverse order  *   Sort sort = new Sort(expr.getSortField(bindings, true));  *</pre>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleBindings
specifier|public
specifier|final
class|class
name|SimpleBindings
extends|extends
name|Bindings
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
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Creates a new empty Bindings */
DECL|method|SimpleBindings
specifier|public
name|SimpleBindings
parameter_list|()
block|{}
comment|/**     * Adds a SortField to the bindings.    *<p>    * This can be used to reference a DocValuesField, a field from    * FieldCache, the document's score, etc.     */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|SortField
name|sortField
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|sortField
operator|.
name|getField
argument_list|()
argument_list|,
name|sortField
argument_list|)
expr_stmt|;
block|}
comment|/**     * Adds an Expression to the bindings.    *<p>    * This can be used to reference expressions from other expressions.     */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Object
name|o
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid reference '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Expression
condition|)
block|{
return|return
operator|(
operator|(
name|Expression
operator|)
name|o
operator|)
operator|.
name|getValueSource
argument_list|(
name|this
argument_list|)
return|;
block|}
name|SortField
name|field
init|=
operator|(
name|SortField
operator|)
name|o
decl_stmt|;
switch|switch
condition|(
name|field
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|INT
case|:
return|return
operator|new
name|IntFieldSource
argument_list|(
name|field
operator|.
name|getField
argument_list|()
argument_list|,
operator|(
name|IntParser
operator|)
name|field
operator|.
name|getParser
argument_list|()
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
operator|new
name|LongFieldSource
argument_list|(
name|field
operator|.
name|getField
argument_list|()
argument_list|,
operator|(
name|LongParser
operator|)
name|field
operator|.
name|getParser
argument_list|()
argument_list|)
return|;
case|case
name|FLOAT
case|:
return|return
operator|new
name|FloatFieldSource
argument_list|(
name|field
operator|.
name|getField
argument_list|()
argument_list|,
operator|(
name|FloatParser
operator|)
name|field
operator|.
name|getParser
argument_list|()
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
operator|new
name|DoubleFieldSource
argument_list|(
name|field
operator|.
name|getField
argument_list|()
argument_list|,
operator|(
name|DoubleParser
operator|)
name|field
operator|.
name|getParser
argument_list|()
argument_list|)
return|;
case|case
name|SCORE
case|:
return|return
operator|new
name|ScoreValueSource
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

