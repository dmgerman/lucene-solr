begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
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
name|params
operator|.
name|CommonParams
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
name|rest
operator|.
name|GETable
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
name|rest
operator|.
name|POSTable
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
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|MediaType
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashMap
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
name|Map
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

begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/copyfields  *<p/>  *  * To restrict the set of copyFields in the response, specify one or both  * of the following as query parameters, with values as space and/or comma  * separated dynamic or explicit field names:  *  *<ul>  *<li>dest.fl: include copyFields that have one of these as a destination</li>  *<li>source.fl: include copyFields that have one of these as a source</li>  *</ul>  *  * If both dest.fl and source.fl are given as query parameters, the copyfields  * in the response will be restricted to those that match any of the destinations  * in dest.fl and also match any of the sources in source.fl.  */
end_comment

begin_class
DECL|class|CopyFieldCollectionResource
specifier|public
class|class
name|CopyFieldCollectionResource
extends|extends
name|BaseFieldResource
implements|implements
name|GETable
implements|,
name|POSTable
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
name|CopyFieldCollectionResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOURCE_FIELD_LIST
specifier|private
specifier|static
specifier|final
name|String
name|SOURCE_FIELD_LIST
init|=
name|IndexSchema
operator|.
name|SOURCE
operator|+
literal|"."
operator|+
name|CommonParams
operator|.
name|FL
decl_stmt|;
DECL|field|DESTINATION_FIELD_LIST
specifier|private
specifier|static
specifier|final
name|String
name|DESTINATION_FIELD_LIST
init|=
name|IndexSchema
operator|.
name|DESTINATION
operator|+
literal|"."
operator|+
name|CommonParams
operator|.
name|FL
decl_stmt|;
DECL|field|requestedSourceFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|requestedSourceFields
decl_stmt|;
DECL|field|requestedDestinationFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|requestedDestinationFields
decl_stmt|;
DECL|method|CopyFieldCollectionResource
specifier|public
name|CopyFieldCollectionResource
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
name|String
name|sourceFieldListParam
init|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|SOURCE_FIELD_LIST
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|sourceFieldListParam
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|sourceFieldListParam
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"[,\\s]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|requestedSourceFields
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|requestedSourceFields
operator|.
name|remove
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// Remove empty values, if any
block|}
block|}
name|String
name|destinationFieldListParam
init|=
name|getSolrRequest
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|DESTINATION_FIELD_LIST
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|destinationFieldListParam
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|destinationFieldListParam
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"[,\\s]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|requestedDestinationFields
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|requestedDestinationFields
operator|.
name|remove
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// Remove empty values, if any
block|}
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
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|COPY_FIELDS
argument_list|,
name|getSchema
argument_list|()
operator|.
name|getCopyFieldProperties
argument_list|(
literal|true
argument_list|,
name|requestedSourceFields
argument_list|,
name|requestedDestinationFields
argument_list|)
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|post
specifier|public
name|Representation
name|post
parameter_list|(
name|Representation
name|entity
parameter_list|)
throws|throws
name|ResourceException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|getSchema
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
name|SolrException
operator|.
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
if|if
condition|(
operator|!
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Only media type "
operator|+
name|MediaType
operator|.
name|APPLICATION_JSON
operator|.
name|toString
argument_list|()
operator|+
literal|" is accepted."
operator|+
literal|"  Request has media type "
operator|+
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
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
name|Object
name|object
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|entity
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|object
operator|instanceof
name|List
operator|)
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid JSON type "
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", expected List of the form"
operator|+
literal|" (ignore the backslashes): [{\"source\":\"foo\",\"dest\":\"comma-separated list of targets\"}, {...}, ...]"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
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
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|object
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldsToCopy
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ManagedIndexSchema
name|oldSchema
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|getSchema
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|malformed
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
range|:
name|list
control|)
block|{
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|SOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fieldName
condition|)
block|{
name|String
name|message
init|=
literal|"Missing '"
operator|+
name|IndexSchema
operator|.
name|SOURCE
operator|+
literal|"' mapping."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|String
name|destinations
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|DESTINATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinations
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing '"
operator|+
name|IndexSchema
operator|.
name|DESTINATION
operator|+
literal|"' mapping."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|String
index|[]
name|splits
init|=
name|destinations
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|destinationSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|splits
operator|!=
literal|null
operator|&&
name|splits
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|splits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|destinationSet
operator|.
name|add
argument_list|(
name|splits
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fieldsToCopy
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|destinationSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|malformed
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|malformed
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Malformed destination(s) for: "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|malformed
control|)
block|{
name|message
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|length
argument_list|()
operator|>
literal|2
condition|)
block|{
name|message
operator|.
name|setLength
argument_list|(
name|message
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|//drop the last ,
block|}
name|log
operator|.
name|error
argument_list|(
name|message
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
throw|;
block|}
name|IndexSchema
name|newSchema
init|=
name|oldSchema
operator|.
name|addCopyFields
argument_list|(
name|fieldsToCopy
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSchema
operator|!=
literal|null
condition|)
block|{
name|getSolrCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|newSchema
argument_list|)
expr_stmt|;
block|}
block|}
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

