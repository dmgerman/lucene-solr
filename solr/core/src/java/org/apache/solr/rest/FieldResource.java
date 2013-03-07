begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SolrException
operator|.
name|ErrorCode
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
name|restlet
operator|.
name|representation
operator|.
name|Representation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
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
name|UnsupportedEncodingException
import|;
end_import

begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/fields/fieldname  * where "fieldname" is the name of a field.  *<p/>  * The GET method returns properties for the given fieldname.  * The "includeDynamic" query parameter, if specified, will cause the  * dynamic field matching the given fieldname to be returned if fieldname  * is not explicitly declared in the schema.  */
end_comment

begin_class
DECL|class|FieldResource
specifier|public
class|class
name|FieldResource
extends|extends
name|BaseFieldResource
implements|implements
name|GETable
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
name|FieldResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|includeDynamic
specifier|private
name|boolean
name|includeDynamic
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|FieldResource
specifier|public
name|FieldResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
if|if
condition|(
name|isExisting
argument_list|()
condition|)
block|{
name|includeDynamic
operator|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|INCLUDE_DYNAMIC_PARAM
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fieldName
operator|=
operator|(
name|String
operator|)
name|getRequestAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|SchemaRestApi
operator|.
name|NAME_VARIABLE
argument_list|)
expr_stmt|;
try|try
block|{
name|fieldName
operator|=
literal|null
operator|==
name|fieldName
condition|?
literal|""
else|:
name|urlDecode
argument_list|(
name|fieldName
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Representation
name|get
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|fieldName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Field name is missing"
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|SchemaField
name|field
decl_stmt|;
if|if
condition|(
name|includeDynamic
condition|)
block|{
name|field
operator|=
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|field
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Field '"
operator|+
name|fieldName
operator|+
literal|"' not found."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|FIELD
argument_list|,
name|getFieldProperties
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
block|}
end_class

end_unit

