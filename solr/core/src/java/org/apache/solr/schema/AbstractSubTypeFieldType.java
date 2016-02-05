begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|params
operator|.
name|MapSolrParams
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
name|params
operator|.
name|SolrParams
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
name|search
operator|.
name|QParser
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
name|Query
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
name|Map
import|;
end_import

begin_comment
comment|/**  * An abstract base class for FieldTypes that delegate work to another {@link org.apache.solr.schema.FieldType}.  * The sub type can be obtained by either specifying the subFieldType attribute or the subFieldSuffix.  In the former  * case, a new dynamic field will be injected into the schema automatically with the name of {@link #POLY_FIELD_SEPARATOR}.  * In the latter case, it will use an existing dynamic field definition to get the type.  See the example schema and the  * use of the {@link org.apache.solr.schema.PointType} for more details.  *  **/
end_comment

begin_class
DECL|class|AbstractSubTypeFieldType
specifier|public
specifier|abstract
class|class
name|AbstractSubTypeFieldType
extends|extends
name|FieldType
implements|implements
name|SchemaAware
block|{
DECL|field|subType
specifier|protected
name|FieldType
name|subType
decl_stmt|;
DECL|field|SUB_FIELD_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|SUB_FIELD_SUFFIX
init|=
literal|"subFieldSuffix"
decl_stmt|;
DECL|field|SUB_FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|SUB_FIELD_TYPE
init|=
literal|"subFieldType"
decl_stmt|;
DECL|field|suffix
specifier|protected
name|String
name|suffix
decl_stmt|;
DECL|field|dynFieldProps
specifier|protected
name|int
name|dynFieldProps
decl_stmt|;
DECL|field|suffixes
specifier|protected
name|String
index|[]
name|suffixes
decl_stmt|;
DECL|field|subFieldType
specifier|protected
name|String
name|subFieldType
init|=
literal|null
decl_stmt|;
DECL|field|subSuffix
specifier|protected
name|String
name|subSuffix
init|=
literal|null
decl_stmt|;
DECL|field|schema
specifier|protected
name|IndexSchema
name|schema
decl_stmt|;
comment|// needed for retrieving SchemaFields
DECL|method|getSubType
specifier|public
name|FieldType
name|getSubType
parameter_list|()
block|{
return|return
name|subType
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
comment|//it's not a first class citizen for the IndexSchema
name|SolrParams
name|p
init|=
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|subFieldType
operator|=
name|p
operator|.
name|get
argument_list|(
name|SUB_FIELD_TYPE
argument_list|)
expr_stmt|;
name|subSuffix
operator|=
name|p
operator|.
name|get
argument_list|(
name|SUB_FIELD_SUFFIX
argument_list|)
expr_stmt|;
if|if
condition|(
name|subFieldType
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|remove
argument_list|(
name|SUB_FIELD_TYPE
argument_list|)
expr_stmt|;
name|subType
operator|=
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
name|subFieldType
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|suffix
operator|=
name|POLY_FIELD_SEPARATOR
operator|+
name|subType
operator|.
name|typeName
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subSuffix
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|remove
argument_list|(
name|SUB_FIELD_SUFFIX
argument_list|)
expr_stmt|;
name|suffix
operator|=
name|subSuffix
expr_stmt|;
block|}
else|else
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
literal|"The field type: "
operator|+
name|typeName
operator|+
literal|" must specify the "
operator|+
name|SUB_FIELD_TYPE
operator|+
literal|" attribute or the "
operator|+
name|SUB_FIELD_SUFFIX
operator|+
literal|" attribute."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper method for creating a dynamic field SchemaField prototype.  Returns a {@link SchemaField} with    * the {@link FieldType} given and a name of "*" + {@link FieldType#POLY_FIELD_SEPARATOR} + {@link FieldType#typeName}    * and props of indexed=true, stored=false.    *    * @param schema the IndexSchema    * @param type   The {@link FieldType} of the prototype.    * @return The {@link SchemaField}    */
DECL|method|registerPolyFieldDynamicPrototype
specifier|static
name|SchemaField
name|registerPolyFieldDynamicPrototype
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|String
name|name
init|=
literal|"*"
operator|+
name|FieldType
operator|.
name|POLY_FIELD_SEPARATOR
operator|+
name|type
operator|.
name|typeName
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//Just set these, delegate everything else to the field type
name|props
operator|.
name|put
argument_list|(
literal|"indexed"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"stored"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"multiValued"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|int
name|p
init|=
name|SchemaField
operator|.
name|calcProps
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|SchemaField
name|proto
init|=
name|SchemaField
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|p
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|schema
operator|.
name|registerDynamicFields
argument_list|(
name|proto
argument_list|)
expr_stmt|;
return|return
name|proto
return|;
block|}
comment|/**    * Registers the polyfield dynamic prototype for this field type: : "*___(field type name)"     *     * {@inheritDoc}    *      * @param schema {@inheritDoc}    *    */
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
comment|//Can't do this until here b/c the Dynamic Fields are not initialized until here.
if|if
condition|(
name|subType
operator|!=
literal|null
condition|)
block|{
name|SchemaField
name|proto
init|=
name|registerPolyFieldDynamicPrototype
argument_list|(
name|schema
argument_list|,
name|subType
argument_list|)
decl_stmt|;
name|dynFieldProps
operator|=
name|proto
operator|.
name|getProperties
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Throws UnsupportedOperationException()    */
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|createSuffixCache
specifier|protected
name|void
name|createSuffixCache
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|suffixes
operator|=
operator|new
name|String
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|suffixes
index|[
name|i
index|]
operator|=
literal|"_"
operator|+
name|i
operator|+
name|suffix
expr_stmt|;
block|}
block|}
DECL|method|subField
specifier|protected
name|SchemaField
name|subField
parameter_list|(
name|SchemaField
name|base
parameter_list|,
name|int
name|i
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
return|return
name|schema
operator|.
name|getField
argument_list|(
name|base
operator|.
name|getName
argument_list|()
operator|+
name|suffixes
index|[
name|i
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

