begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
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
name|document
operator|.
name|Field
operator|.
name|Index
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
operator|.
name|ContentType
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
name|gdata
operator|.
name|search
operator|.
name|index
operator|.
name|GdataIndexerException
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
name|gdata
operator|.
name|utils
operator|.
name|ReflectionUtils
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

begin_comment
comment|/**  * Creating Indexable document requires processing of incoming entities as  * GData Entries. Entries in the GData protocol might have very different  * structures and content. They all have on thing in common as they are atom xml  * format. To retrieve the configured elements of the atom format and process the  * actual content might differ from element to element.  *<p>  * Each predefined ContentStrategy can be used to retrieve certain content from  * the defined element. Which element to process is defined using a XPath  * expression in the gdata-config.xml file.  *</p>  *<p>  *<tt>ContentStrategy</tt> implementation should not be accessed directly. To  * get a<tt>ContentStrategy</tt> for a specific  * {@link org.apache.lucene.gdata.search.config.IndexSchemaField.ContentType}  * use the {@link ContentStrategy#getFieldStrategy} factory method. This method  * expects a IndexSchemaField instance with a set<tt>ContentType</tt>. The  * return value is a new<tt>ContentStrategy</tt> instance for the defined  *<tt>ContentType</tt>.  *</p>  *   * @see org.apache.lucene.gdata.search.config.IndexSchemaField.ContentType  * @see org.apache.lucene.gdata.search.index.IndexDocumentBuilder  *   *  */
end_comment

begin_class
DECL|class|ContentStrategy
specifier|public
specifier|abstract
class|class
name|ContentStrategy
block|{
DECL|field|store
specifier|protected
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|index
specifier|protected
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|config
specifier|protected
specifier|final
name|IndexSchemaField
name|config
decl_stmt|;
DECL|field|content
specifier|protected
name|String
name|content
decl_stmt|;
DECL|field|fieldName
specifier|protected
name|String
name|fieldName
decl_stmt|;
DECL|method|ContentStrategy
specifier|protected
name|ContentStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfiguration
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|fieldConfiguration
argument_list|)
expr_stmt|;
block|}
DECL|method|ContentStrategy
specifier|protected
name|ContentStrategy
parameter_list|(
name|Index
name|index
parameter_list|,
name|Store
name|store
parameter_list|,
name|IndexSchemaField
name|fieldConfig
parameter_list|)
block|{
if|if
condition|(
name|fieldConfig
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"IndexSchemaField must not be null"
argument_list|)
throw|;
name|this
operator|.
name|config
operator|=
name|fieldConfig
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldConfig
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|index
operator|=
name|fieldConfig
operator|.
name|getIndex
argument_list|()
operator|==
literal|null
condition|?
name|IndexSchemaField
operator|.
name|DEFAULT_INDEX_STRATEGY
else|:
name|fieldConfig
operator|.
name|getIndex
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|store
operator|=
name|fieldConfig
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|?
name|IndexSchemaField
operator|.
name|DEFAULT_STORE_STRATEGY
else|:
name|fieldConfig
operator|.
name|getStore
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @param indexable      * @throws NotIndexableException      */
DECL|method|processIndexable
specifier|public
specifier|abstract
name|void
name|processIndexable
parameter_list|(
name|Indexable
argument_list|<
name|?
extends|extends
name|Node
argument_list|,
name|?
extends|extends
name|ServerBaseEntry
argument_list|>
name|indexable
parameter_list|)
throws|throws
name|NotIndexableException
function_decl|;
comment|/**      * This method creates a lucene field from the retrieved content of the      * entity. Values for Field.Index, Field.Store, the field name and the boost      * factor are configured in the<tt>IndexSchemaField</tt> passed by the      * constructor e.g the factory method. This method might be overwritten by      * subclasses.      *       * @return the Lucene {@link Field}      */
DECL|method|createLuceneField
specifier|public
name|Field
index|[]
name|createLuceneField
parameter_list|()
block|{
comment|/*          * should I test the content for being empty?!          * does that make any difference if empty fields are indexed?!          */
if|if
condition|(
name|this
operator|.
name|fieldName
operator|==
literal|null
operator|||
name|this
operator|.
name|content
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GdataIndexerException
argument_list|(
literal|"Required field not set fieldName: "
operator|+
name|this
operator|.
name|fieldName
operator|+
literal|" content: "
operator|+
name|this
operator|.
name|content
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|content
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|Field
index|[
literal|0
index|]
return|;
block|}
name|Field
name|retValue
init|=
operator|new
name|Field
argument_list|(
name|this
operator|.
name|fieldName
argument_list|,
name|this
operator|.
name|content
argument_list|,
name|this
operator|.
name|store
argument_list|,
name|this
operator|.
name|index
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
name|this
operator|.
name|config
operator|.
name|getBoost
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
name|retValue
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
operator|new
name|Field
index|[]
block|{
name|retValue
block|}
return|;
block|}
comment|/**      * This factory method creates the<tt>ContentStrategy</tt> corresponding      * to the set<tt>ContentType</tt> value in the<tt>IndexSchemaField</tt>      * passed to the method as the single parameter.      *<p>      * The ContentType must not be null      *</p>      *       * @param fieldConfig -      *            the field config to use to identify the corresponding      *<tt>ContentStrategy</tt>      * @return - a new<tt>ContentStrategy</tt> instance      */
DECL|method|getFieldStrategy
specifier|public
specifier|static
name|ContentStrategy
name|getFieldStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfig
parameter_list|)
block|{
if|if
condition|(
name|fieldConfig
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field configuration must not be null"
argument_list|)
throw|;
name|ContentType
name|type
init|=
name|fieldConfig
operator|.
name|getContentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ContentType in IndexSchemaField must not be null"
argument_list|)
throw|;
name|fieldConfig
operator|.
name|getAnalyzerClass
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|CATEGORY
case|:
return|return
operator|new
name|GdataCategoryStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
case|case
name|HTML
case|:
return|return
operator|new
name|HTMLStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
case|case
name|XHTML
case|:
return|return
operator|new
name|XHtmlStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
case|case
name|GDATADATE
case|:
return|return
operator|new
name|GdataDateStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
case|case
name|TEXT
case|:
return|return
operator|new
name|PlainTextStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
case|case
name|KEYWORD
case|:
return|return
operator|new
name|KeywordStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
case|case
name|CUSTOM
case|:
comment|/*              * check if this class can be created with default constructor is checked              * in IndexSchemaField#setFieldClass and throws RuntimeEx if not. So              * server can not start up.              */
return|return
name|ReflectionUtils
operator|.
name|getDefaultInstance
argument_list|(
name|fieldConfig
operator|.
name|getFieldClass
argument_list|()
argument_list|)
return|;
case|case
name|MIXED
case|:
return|return
operator|new
name|MixedContentStrategy
argument_list|(
name|fieldConfig
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No content strategy found for "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

