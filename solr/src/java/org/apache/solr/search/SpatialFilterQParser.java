begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geometry
operator|.
name|DistanceUnits
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
name|spatial
operator|.
name|DistanceUtils
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
comment|/**  * Creates a spatial Filter based on the type of spatial point used.  *<p/>  * The field must implement XXXX  *<p/>  *<p/>  *<p/>  * Syntax:  *<pre>{!sfilt fl=location [units=[K|M]] [meas=[0-INF|hsin|sqe]] }&pt=49.32,-79.0&d=20</pre>  *<p/>  * Parameters:  *<ul>  *<li>fl - The fields to filter on.  Must implement XXXX. Required.  If more than one, XXXX</li>  *<li>pt - The point to use as a reference.  Must match the dimension of the field. Required.</li>  *<li>d - The distance in the units specified. Required.</li>  *<li>units - The units of the distance.  K - kilometers, M - Miles.  Optional.  Default is miles.</li>  *<li>meas - The distance measure to use.  Default is Euclidean (2-norm).  If a number between 0-INF is used, then the Vector Distance is used.  hsin = Haversine, sqe = Squared Euclidean</li>  *</ul>  */
end_comment

begin_class
DECL|class|SpatialFilterQParser
specifier|public
class|class
name|SpatialFilterQParser
extends|extends
name|QParser
block|{
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
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
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
name|CommonParams
operator|.
name|FL
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
name|CommonParams
operator|.
name|FL
operator|+
literal|" is not properly specified"
argument_list|)
throw|;
block|}
name|String
name|pointStr
init|=
name|params
operator|.
name|get
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
literal|" is not properly specified"
argument_list|)
throw|;
block|}
name|double
name|dist
init|=
name|params
operator|.
name|getDouble
argument_list|(
name|SpatialParams
operator|.
name|DISTANCE
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
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
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
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
name|schema
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
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
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
argument_list|,
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|)
decl_stmt|;
name|result
operator|=
operator|(
operator|(
name|SpatialQueryable
operator|)
name|type
operator|)
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

