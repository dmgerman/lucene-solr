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
name|SolrInputDocument
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
name|SolrInputField
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
name|core
operator|.
name|SolrCore
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
name|ManagedIndexSchema
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
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|AddUpdateCommand
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
name|FieldMutatingUpdateProcessorFactory
operator|.
name|SelectorParams
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
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
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
name|plugin
operator|.
name|SolrCoreAware
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
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashSet
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
name|Set
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
import|;
end_import

begin_comment
comment|/**  *<p>  * This processor will dynamically add fields to the schema if an input document contains  * one or more fields that don't match any field or dynamic field in the schema.  *</p>  *<p>  * By default, this processor selects all fields that don't match a schema field or  * dynamic field.  The "fieldName" and "fieldRegex" selectors may be specified to further  * restrict the selected fields, but the other selectors ("typeName", "typeClass", and  * "fieldNameMatchesSchemaField") may not be specified.  *</p>  *<p>  * This processor is configured to map from each field's values' class(es) to the schema  * field type that will be used when adding the new field to the schema.  All new fields  * are then added to the schema in a single batch.  If schema addition fails for any  * field, addition is re-attempted only for those that donât match any schema  * field.  This process is repeated, either until all new fields are successfully added,  * or until there are no new fields (presumably because the fields that were new when  * this processor started its work were subsequently added by a different update  * request, possibly on a different node).  *</p>  *<p>  * This processor takes as configuration a sequence of zero or more "typeMapping"-s from  * one or more "valueClass"-s, specified as either an&lt;arr&gt; of&lt;str&gt;, or  * multiple&lt;str&gt; with the same name, to an existing schema "fieldType".  *</p>  *<p>  * If more than one "valueClass" is specified in a "typeMapping", field values with any  * of the specified "valueClass"-s will be mapped to the specified target "fieldType".  * The "typeMapping"-s are attempted in the specified order; if a field value's class  * is not specified in a "valueClass", the next "typeMapping" is attempted. If no  * "typeMapping" succeeds, then the specified "defaultFieldType" is used.   *</p>  *<p>  * Example configuration:  *</p>  *   *<pre class="prettyprint">  *&lt;processor class="solr.AddSchemaFieldsUpdateProcessorFactory"&gt;  *&lt;str name="defaultFieldType"&gt;text_general&lt;/str&gt;  *&lt;lst name="typeMapping"&gt;  *&lt;str name="valueClass"&gt;Boolean&lt;/str&gt;  *&lt;str name="fieldType"&gt;boolean&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="typeMapping"&gt;  *&lt;str name="valueClass"&gt;Integer&lt;/str&gt;  *&lt;str name="fieldType"&gt;tint&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="typeMapping"&gt;  *&lt;str name="valueClass"&gt;Float&lt;/str&gt;  *&lt;str name="fieldType"&gt;tfloat&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="typeMapping"&gt;  *&lt;str name="valueClass"&gt;Date&lt;/str&gt;  *&lt;str name="fieldType"&gt;tdate&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="typeMapping"&gt;  *&lt;str name="valueClass"&gt;Long&lt;/str&gt;  *&lt;str name="valueClass"&gt;Integer&lt;/str&gt;  *&lt;str name="fieldType"&gt;tlong&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="typeMapping"&gt;  *&lt;arr name="valueClass"&gt;  *&lt;str&gt;Double&lt;/str&gt;  *&lt;str&gt;Float&lt;/str&gt;  *&lt;/arr&gt;  *&lt;str name="fieldType"&gt;tdouble&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/processor&gt;</pre>  */
end_comment

begin_class
DECL|class|AddSchemaFieldsUpdateProcessorFactory
specifier|public
class|class
name|AddSchemaFieldsUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|public
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AddSchemaFieldsUpdateProcessorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TYPE_MAPPING_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_MAPPING_PARAM
init|=
literal|"typeMapping"
decl_stmt|;
DECL|field|VALUE_CLASS_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|VALUE_CLASS_PARAM
init|=
literal|"valueClass"
decl_stmt|;
DECL|field|FIELD_TYPE_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_TYPE_PARAM
init|=
literal|"fieldType"
decl_stmt|;
DECL|field|DEFAULT_FIELD_TYPE_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_FIELD_TYPE_PARAM
init|=
literal|"defaultFieldType"
decl_stmt|;
DECL|field|typeMappings
specifier|private
name|List
argument_list|<
name|TypeMapping
argument_list|>
name|typeMappings
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
DECL|field|inclusions
specifier|private
name|SelectorParams
name|inclusions
init|=
operator|new
name|SelectorParams
argument_list|()
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Collection
argument_list|<
name|SelectorParams
argument_list|>
name|exclusions
init|=
operator|new
name|ArrayList
argument_list|<
name|SelectorParams
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|selector
specifier|private
name|FieldNameSelector
name|selector
init|=
literal|null
decl_stmt|;
DECL|field|defaultFieldType
specifier|private
name|String
name|defaultFieldType
decl_stmt|;
DECL|method|getSelector
specifier|protected
specifier|final
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
name|getSelector
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|selector
condition|)
return|return
name|selector
return|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"selector was never initialized, inform(SolrCore) never called???"
argument_list|)
throw|;
block|}
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
name|AddSchemaFieldsUpdateProcessor
argument_list|(
name|next
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|inclusions
operator|=
name|FieldMutatingUpdateProcessorFactory
operator|.
name|parseSelectorParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|validateSelectorParams
argument_list|(
name|inclusions
argument_list|)
expr_stmt|;
name|inclusions
operator|.
name|fieldNameMatchesSchemaField
operator|=
literal|false
expr_stmt|;
comment|// Explicitly (non-configurably) require unknown field names
name|exclusions
operator|=
name|FieldMutatingUpdateProcessorFactory
operator|.
name|parseSelectorExclusionParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
for|for
control|(
name|SelectorParams
name|exclusion
range|:
name|exclusions
control|)
block|{
name|validateSelectorParams
argument_list|(
name|exclusion
argument_list|)
expr_stmt|;
block|}
name|Object
name|defaultFieldTypeParam
init|=
name|args
operator|.
name|remove
argument_list|(
name|DEFAULT_FIELD_TYPE_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|defaultFieldTypeParam
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Missing required init param '"
operator|+
name|DEFAULT_FIELD_TYPE_PARAM
operator|+
literal|"'"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|defaultFieldTypeParam
operator|instanceof
name|CharSequence
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|DEFAULT_FIELD_TYPE_PARAM
operator|+
literal|"' must be a<str>"
argument_list|)
throw|;
block|}
block|}
name|defaultFieldType
operator|=
name|defaultFieldTypeParam
operator|.
name|toString
argument_list|()
expr_stmt|;
name|typeMappings
operator|=
name|parseTypeMappings
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|selector
operator|=
name|FieldMutatingUpdateProcessor
operator|.
name|createFieldNameSelector
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|core
argument_list|,
name|inclusions
argument_list|,
name|getDefaultSelector
argument_list|(
name|core
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SelectorParams
name|exc
range|:
name|exclusions
control|)
block|{
name|selector
operator|=
name|FieldMutatingUpdateProcessor
operator|.
name|wrap
argument_list|(
name|selector
argument_list|,
name|FieldMutatingUpdateProcessor
operator|.
name|createFieldNameSelector
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|core
argument_list|,
name|exc
argument_list|,
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TypeMapping
name|typeMapping
range|:
name|typeMappings
control|)
block|{
name|typeMapping
operator|.
name|populateValueClasses
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDefaultSelector
specifier|private
name|FieldNameSelector
name|getDefaultSelector
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
return|return
operator|new
name|FieldNameSelector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|null
operator|==
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|parseTypeMappings
specifier|private
specifier|static
name|List
argument_list|<
name|TypeMapping
argument_list|>
name|parseTypeMappings
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|List
argument_list|<
name|TypeMapping
argument_list|>
name|typeMappings
init|=
operator|new
name|ArrayList
argument_list|<
name|TypeMapping
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|typeMappingsParams
init|=
name|args
operator|.
name|getAll
argument_list|(
name|TYPE_MAPPING_PARAM
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|typeMappingObj
range|:
name|typeMappingsParams
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|typeMappingObj
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'"
operator|+
name|TYPE_MAPPING_PARAM
operator|+
literal|"' init param cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|typeMappingObj
operator|instanceof
name|NamedList
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'"
operator|+
name|TYPE_MAPPING_PARAM
operator|+
literal|"' init param must be a<lst>"
argument_list|)
throw|;
block|}
name|NamedList
name|typeMappingNamedList
init|=
operator|(
name|NamedList
operator|)
name|typeMappingObj
decl_stmt|;
name|Object
name|fieldTypeObj
init|=
name|typeMappingNamedList
operator|.
name|remove
argument_list|(
name|FIELD_TYPE_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fieldTypeObj
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Each '"
operator|+
name|TYPE_MAPPING_PARAM
operator|+
literal|"'<lst/> must contain a '"
operator|+
name|FIELD_TYPE_PARAM
operator|+
literal|"'<str>"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|fieldTypeObj
operator|instanceof
name|CharSequence
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'"
operator|+
name|FIELD_TYPE_PARAM
operator|+
literal|"' init param must be a<str>"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|!=
name|typeMappingNamedList
operator|.
name|get
argument_list|(
name|FIELD_TYPE_PARAM
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Each '"
operator|+
name|TYPE_MAPPING_PARAM
operator|+
literal|"'<lst/> may contain only one '"
operator|+
name|FIELD_TYPE_PARAM
operator|+
literal|"'<str>"
argument_list|)
throw|;
block|}
name|String
name|fieldType
init|=
name|fieldTypeObj
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|valueClasses
init|=
name|FieldMutatingUpdateProcessorFactory
operator|.
name|oneOrMany
argument_list|(
name|typeMappingNamedList
argument_list|,
name|VALUE_CLASS_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueClasses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Each '"
operator|+
name|TYPE_MAPPING_PARAM
operator|+
literal|"'<lst/> must contain at least one '"
operator|+
name|VALUE_CLASS_PARAM
operator|+
literal|"'<str>"
argument_list|)
throw|;
block|}
name|typeMappings
operator|.
name|add
argument_list|(
operator|new
name|TypeMapping
argument_list|(
name|fieldType
argument_list|,
name|valueClasses
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|typeMappingNamedList
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Unexpected '"
operator|+
name|TYPE_MAPPING_PARAM
operator|+
literal|"' init sub-param(s): '"
operator|+
name|typeMappingNamedList
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|args
operator|.
name|remove
argument_list|(
name|TYPE_MAPPING_PARAM
argument_list|)
expr_stmt|;
block|}
return|return
name|typeMappings
return|;
block|}
DECL|method|validateSelectorParams
specifier|private
name|void
name|validateSelectorParams
parameter_list|(
name|SelectorParams
name|params
parameter_list|)
block|{
if|if
condition|(
operator|!
name|params
operator|.
name|typeName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'typeName' init param is not allowed in this processor"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|params
operator|.
name|typeClass
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'typeClass' init param is not allowed in this processor"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|!=
name|params
operator|.
name|fieldNameMatchesSchemaField
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"'fieldNameMatchesSchemaField' init param is not allowed in this processor"
argument_list|)
throw|;
block|}
block|}
DECL|class|TypeMapping
specifier|private
specifier|static
class|class
name|TypeMapping
block|{
DECL|field|fieldTypeName
specifier|public
name|String
name|fieldTypeName
decl_stmt|;
DECL|field|valueClassNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|valueClassNames
decl_stmt|;
DECL|field|valueClasses
specifier|public
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|valueClasses
decl_stmt|;
DECL|method|TypeMapping
specifier|public
name|TypeMapping
parameter_list|(
name|String
name|fieldTypeName
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|valueClassNames
parameter_list|)
block|{
name|this
operator|.
name|fieldTypeName
operator|=
name|fieldTypeName
expr_stmt|;
name|this
operator|.
name|valueClassNames
operator|=
name|valueClassNames
expr_stmt|;
comment|// this.valueClasses population is delayed until the schema is available
block|}
DECL|method|populateValueClasses
specifier|public
name|void
name|populateValueClasses
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|fieldTypeName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"fieldType '"
operator|+
name|fieldTypeName
operator|+
literal|"' not found in the schema"
argument_list|)
throw|;
block|}
name|valueClasses
operator|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|valueClassName
range|:
name|valueClassNames
control|)
block|{
try|try
block|{
name|valueClasses
operator|.
name|add
argument_list|(
name|loader
operator|.
name|loadClass
argument_list|(
name|valueClassName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"valueClass '"
operator|+
name|valueClassName
operator|+
literal|"' not found for fieldType '"
operator|+
name|fieldTypeName
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|class|AddSchemaFieldsUpdateProcessor
specifier|private
class|class
name|AddSchemaFieldsUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|method|AddSchemaFieldsUpdateProcessor
specifier|public
name|AddSchemaFieldsUpdateProcessor
parameter_list|(
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|isMutable
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"This IndexSchema is not mutable."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
specifier|final
name|SolrInputDocument
name|doc
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
specifier|final
name|SolrCore
name|core
init|=
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getCore
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|IndexSchema
name|oldSchema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SchemaField
argument_list|>
name|newFields
init|=
operator|new
name|ArrayList
argument_list|<
name|SchemaField
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fieldName
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
if|if
condition|(
name|selector
operator|.
name|shouldMutate
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|fieldTypeName
init|=
name|mapValueClassesToFieldType
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
argument_list|)
decl_stmt|;
name|newFields
operator|.
name|add
argument_list|(
name|oldSchema
operator|.
name|newField
argument_list|(
name|fieldName
argument_list|,
name|fieldTypeName
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|newFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// nothing to do - no fields will be added - exit from the retry loop
name|log
operator|.
name|debug
argument_list|(
literal|"No fields to add to the schema."
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Fields to be added to the schema: ["
argument_list|)
expr_stmt|;
name|boolean
name|isFirst
init|=
literal|true
decl_stmt|;
for|for
control|(
name|SchemaField
name|field
range|:
name|newFields
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|isFirst
condition|?
literal|""
else|:
literal|","
argument_list|)
expr_stmt|;
name|isFirst
operator|=
literal|false
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"{type="
argument_list|)
operator|.
name|append
argument_list|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addFields
argument_list|(
name|newFields
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|getReq
argument_list|()
operator|.
name|updateSchemaToLatest
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Successfully added field(s) to the schema."
argument_list|)
expr_stmt|;
break|break;
comment|// success - exit from the retry loop
block|}
catch|catch
parameter_list|(
name|ManagedIndexSchema
operator|.
name|FieldExistsException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"At least one field to be added already exists in the schema - retrying."
argument_list|)
expr_stmt|;
comment|// No action: at least one field to be added already exists in the schema, so retry
block|}
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|mapValueClassesToFieldType
specifier|private
name|String
name|mapValueClassesToFieldType
parameter_list|(
name|SolrInputField
name|field
parameter_list|)
block|{
name|NEXT_TYPE_MAPPING
label|:
for|for
control|(
name|TypeMapping
name|typeMapping
range|:
name|typeMappings
control|)
block|{
name|NEXT_FIELD_VALUE
label|:
for|for
control|(
name|Object
name|fieldValue
range|:
name|field
operator|.
name|getValues
argument_list|()
control|)
block|{
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|valueClass
range|:
name|typeMapping
operator|.
name|valueClasses
control|)
block|{
if|if
condition|(
name|valueClass
operator|.
name|isInstance
argument_list|(
name|fieldValue
argument_list|)
condition|)
block|{
continue|continue
name|NEXT_FIELD_VALUE
continue|;
block|}
block|}
comment|// This fieldValue is not an instance of any of this fieldType's valueClass-s
continue|continue
name|NEXT_TYPE_MAPPING
continue|;
block|}
comment|// Success! Each of this field's values is an instance of one of this fieldType's valueClass-s
return|return
name|typeMapping
operator|.
name|fieldTypeName
return|;
block|}
comment|// At least one of this field's values is not an instance of any configured fieldType's valueClass-s
return|return
name|defaultFieldType
return|;
block|}
block|}
block|}
end_class

end_unit

