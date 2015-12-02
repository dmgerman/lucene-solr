begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|FieldType
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
name|IntValueFieldType
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

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
name|ParsePosition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  *<p>  * Attempts to mutate selected fields that have only CharSequence-typed values  * into Integer values.  Grouping separators (',' in the ROOT locale) are parsed.  *</p>  *<p>  * The default selection behavior is to mutate both those fields that don't match  * a schema field, as well as those fields that match a schema field with a field   * type that uses class solr.TrieIntField.  *</p>  *<p>  * If all values are parseable as int (or are already Integer), then the field  * will be mutated, replacing each value with its parsed Integer equivalent;  * otherwise, no mutation will occur.  *</p>  *<p>  * The locale to use when parsing field values, which will affect the recognized  * grouping separator character, may optionally be specified.  If no locale is  * configured, then {@link Locale#ROOT} will be used. The following configuration  * specifies the Russian/Russia locale, which will parse the string "12Â 345Â 899"  * as 12345899L (the grouping separator character is U+00AO NO-BREAK SPACE).  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.ParseIntFieldUpdateProcessorFactory"&gt;  *&lt;str name="locale"&gt;ru_RU&lt;/str&gt;  *&lt;/processor&gt;</pre>  *  *<p>  * See {@link Locale} for a description of acceptable language, country (optional)  * and variant (optional) values, joined with underscore(s).  *</p>  */
end_comment

begin_class
DECL|class|ParseIntFieldUpdateProcessorFactory
specifier|public
class|class
name|ParseIntFieldUpdateProcessorFactory
extends|extends
name|ParseNumericFieldUpdateProcessorFactory
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|ParseIntFieldUpdateProcessor
argument_list|(
name|getSelector
argument_list|()
argument_list|,
name|locale
argument_list|,
name|next
argument_list|)
return|;
block|}
DECL|class|ParseIntFieldUpdateProcessor
specifier|private
specifier|static
specifier|final
class|class
name|ParseIntFieldUpdateProcessor
extends|extends
name|AllValuesOrNoneFieldMutatingUpdateProcessor
block|{
DECL|field|locale
specifier|private
specifier|final
name|Locale
name|locale
decl_stmt|;
comment|// NumberFormat instances are not thread safe
DECL|field|numberFormat
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
name|numberFormat
init|=
operator|new
name|ThreadLocal
argument_list|<
name|NumberFormat
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|NumberFormat
name|initialValue
parameter_list|()
block|{
name|NumberFormat
name|format
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
name|format
operator|.
name|setParseIntegerOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|format
return|;
block|}
block|}
decl_stmt|;
DECL|method|ParseIntFieldUpdateProcessor
name|ParseIntFieldUpdateProcessor
parameter_list|(
name|FieldNameSelector
name|selector
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|selector
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mutateValue
specifier|protected
name|Object
name|mutateValue
parameter_list|(
name|Object
name|srcVal
parameter_list|)
block|{
if|if
condition|(
name|srcVal
operator|instanceof
name|CharSequence
condition|)
block|{
name|String
name|stringVal
init|=
name|srcVal
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ParsePosition
name|pos
init|=
operator|new
name|ParsePosition
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Number
name|number
init|=
name|numberFormat
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|stringVal
argument_list|,
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|.
name|getIndex
argument_list|()
operator|!=
name|stringVal
operator|.
name|length
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"value '{}' is not parseable, thus not mutated; unparsed chars: '{}'"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|srcVal
block|,
name|stringVal
operator|.
name|substring
argument_list|(
name|pos
operator|.
name|getIndex
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
name|int
name|intValue
init|=
name|number
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|number
operator|.
name|longValue
argument_list|()
operator|==
operator|(
name|long
operator|)
name|intValue
condition|)
block|{
comment|// If the high bits don't get truncated by number.intValue()
return|return
name|intValue
return|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"value '{}' doesn't fit into an Integer, thus was not mutated"
argument_list|,
name|srcVal
argument_list|)
expr_stmt|;
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
if|if
condition|(
name|srcVal
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|srcVal
return|;
block|}
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|isSchemaFieldTypeCompatible
specifier|protected
name|boolean
name|isSchemaFieldTypeCompatible
parameter_list|(
name|FieldType
name|type
parameter_list|)
block|{
return|return
name|type
operator|instanceof
name|IntValueFieldType
return|;
block|}
block|}
end_class

end_unit

