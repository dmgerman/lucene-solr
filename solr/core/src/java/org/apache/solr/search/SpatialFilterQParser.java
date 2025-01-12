begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|search
operator|.
name|Query
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
name|common
operator|.
name|params
operator|.
name|SpatialParams
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
name|schema
operator|.
name|SpatialQueryable
import|;
end_import

begin_comment
comment|/**  * @see SpatialFilterQParserPlugin  */
end_comment

begin_class
DECL|class|SpatialFilterQParser
specifier|public
class|class
name|SpatialFilterQParser
extends|extends
name|QParser
block|{
DECL|field|bbox
name|boolean
name|bbox
decl_stmt|;
comment|// do bounding box only
DECL|method|SpatialFilterQParser
specifier|public
name|SpatialFilterQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|boolean
name|bbox
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|this
operator|.
name|bbox
operator|=
name|bbox
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
comment|//if more than one, we need to treat them as a point...
comment|//TODO: Should we accept multiple fields
name|String
index|[]
name|fields
init|=
name|localParams
operator|.
name|getParams
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|String
name|field
init|=
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
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
literal|" missing sfield for spatial request"
argument_list|)
throw|;
name|fields
operator|=
operator|new
name|String
index|[]
block|{
name|field
block|}
expr_stmt|;
block|}
name|String
name|pointStr
init|=
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|POINT
argument_list|)
decl_stmt|;
if|if
condition|(
name|pointStr
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
name|BAD_REQUEST
argument_list|,
name|SpatialParams
operator|.
name|POINT
operator|+
literal|" missing."
argument_list|)
throw|;
block|}
name|double
name|dist
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|distS
init|=
name|getParam
argument_list|(
name|SpatialParams
operator|.
name|DISTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|distS
operator|!=
literal|null
condition|)
name|dist
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|distS
argument_list|)
expr_stmt|;
if|if
condition|(
name|dist
operator|<
literal|0
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
name|BAD_REQUEST
argument_list|,
name|SpatialParams
operator|.
name|DISTANCE
operator|+
literal|" must be>= 0"
argument_list|)
throw|;
block|}
name|String
name|measStr
init|=
name|localParams
operator|.
name|get
argument_list|(
name|SpatialParams
operator|.
name|MEASURE
argument_list|)
decl_stmt|;
comment|//TODO: Need to do something with Measures
name|Query
name|result
init|=
literal|null
decl_stmt|;
comment|//fields is valid at this point
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|SchemaField
name|sf
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|FieldType
name|type
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|SpatialQueryable
condition|)
block|{
name|SpatialQueryable
name|queryable
init|=
operator|(
operator|(
name|SpatialQueryable
operator|)
name|type
operator|)
decl_stmt|;
name|double
name|radius
init|=
name|localParams
operator|.
name|getDouble
argument_list|(
name|SpatialParams
operator|.
name|SPHERE_RADIUS
argument_list|,
name|queryable
operator|.
name|getSphereRadius
argument_list|()
argument_list|)
decl_stmt|;
name|SpatialOptions
name|opts
init|=
operator|new
name|SpatialOptions
argument_list|(
name|pointStr
argument_list|,
name|dist
argument_list|,
name|sf
argument_list|,
name|measStr
argument_list|,
name|radius
argument_list|)
decl_stmt|;
name|opts
operator|.
name|bbox
operator|=
name|bbox
expr_stmt|;
name|result
operator|=
name|queryable
operator|.
name|createSpatialQuery
argument_list|(
name|this
argument_list|,
name|opts
argument_list|)
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
literal|"The field "
operator|+
name|fields
index|[
literal|0
index|]
operator|+
literal|" does not support spatial filtering"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// fields.length> 1
comment|//TODO: Not sure about this just yet, is there a way to delegate, or do we just have a helper class?
comment|//Seems like we could just use FunctionQuery, but then what about scoring
comment|/*List<ValueSource> sources = new ArrayList<ValueSource>(fields.length);       for (String field : fields) {         SchemaField sf = schema.getField(field);         sources.add(sf.getType().getValueSource(sf, this));       }       MultiValueSource vs = new VectorValueSource(sources);       ValueSourceRangeFilter rf = new ValueSourceRangeFilter(vs, "0", String.valueOf(dist), true, true);       result = new SolrConstantScoreQuery(rf);*/
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

