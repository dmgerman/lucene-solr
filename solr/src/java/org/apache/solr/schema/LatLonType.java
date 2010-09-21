begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Fieldable
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
name|BooleanClause
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
name|BooleanQuery
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
name|search
operator|.
name|SortField
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
name|lucene
operator|.
name|spatial
operator|.
name|tier
operator|.
name|InvalidGeoException
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
name|response
operator|.
name|TextResponseWriter
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
name|XMLWriter
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
name|solr
operator|.
name|search
operator|.
name|SpatialOptions
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
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|VectorValueSource
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

begin_comment
comment|/**  * Represents a Latitude/Longitude as a 2 dimensional point.  Latitude is<b>always</b> specified first.  * Can also, optionally, integrate in Spatial Tile capabilities.  The default is for tile fields from 4 - 15,  * just as in the SpatialTileField that we are extending.  */
end_comment

begin_class
DECL|class|LatLonType
specifier|public
class|class
name|LatLonType
extends|extends
name|AbstractSubTypeFieldType
implements|implements
name|SpatialQueryable
block|{
DECL|field|LAT
specifier|protected
specifier|static
specifier|final
name|int
name|LAT
init|=
literal|0
decl_stmt|;
DECL|field|LONG
specifier|protected
specifier|static
specifier|final
name|int
name|LONG
init|=
literal|1
decl_stmt|;
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
comment|//TODO: refactor this, as we are creating the suffix cache twice, since the super.init does it too
name|createSuffixCache
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|//we need three extra fields: one for the storage field, two for the lat/lon
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|Fieldable
index|[]
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
comment|//we could have tileDiff + 3 fields (two for the lat/lon, one for storage)
name|Fieldable
index|[]
name|f
init|=
operator|new
name|Fieldable
index|[
operator|(
name|field
operator|.
name|indexed
argument_list|()
condition|?
literal|2
else|:
literal|0
operator|)
operator|+
operator|(
name|field
operator|.
name|stored
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
index|]
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|double
index|[]
name|latLon
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|latLon
operator|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
name|externalVal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
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
name|e
argument_list|)
throw|;
block|}
comment|//latitude
name|f
index|[
name|i
index|]
operator|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
operator|.
name|createField
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|latLon
index|[
name|LAT
index|]
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
comment|//longitude
name|f
index|[
name|i
index|]
operator|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
operator|.
name|createField
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|latLon
index|[
name|LONG
index|]
argument_list|)
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
name|f
index|[
name|f
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|createField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|externalVal
argument_list|,
name|getFieldStore
argument_list|(
name|field
argument_list|,
name|externalVal
argument_list|)
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|createSpatialQuery
specifier|public
name|Query
name|createSpatialQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SpatialOptions
name|options
parameter_list|)
block|{
name|BooleanQuery
name|result
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|double
index|[]
name|point
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|point
operator|=
name|DistanceUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
name|options
operator|.
name|pointStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidGeoException
name|e
parameter_list|)
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
name|e
argument_list|)
throw|;
block|}
comment|//Get the distance
name|double
index|[]
name|ur
decl_stmt|;
name|double
index|[]
name|ll
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|measStr
operator|==
literal|null
operator|||
name|options
operator|.
name|measStr
operator|.
name|equals
argument_list|(
literal|"hsin"
argument_list|)
condition|)
block|{
name|ur
operator|=
name|DistanceUtils
operator|.
name|latLonCornerDegs
argument_list|(
name|point
index|[
name|LAT
index|]
argument_list|,
name|point
index|[
name|LONG
index|]
argument_list|,
name|options
operator|.
name|distance
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|options
operator|.
name|radius
argument_list|)
expr_stmt|;
name|ll
operator|=
name|DistanceUtils
operator|.
name|latLonCornerDegs
argument_list|(
name|point
index|[
name|LAT
index|]
argument_list|,
name|point
index|[
name|LONG
index|]
argument_list|,
name|options
operator|.
name|distance
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|options
operator|.
name|radius
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ur
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|point
argument_list|,
literal|null
argument_list|,
name|options
operator|.
name|distance
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ll
operator|=
name|DistanceUtils
operator|.
name|vectorBoxCorner
argument_list|(
name|point
argument_list|,
literal|null
argument_list|,
name|options
operator|.
name|distance
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|SchemaField
name|subSF
decl_stmt|;
name|Query
name|range
decl_stmt|;
name|double
name|angDistDegs
init|=
name|DistanceUtils
operator|.
name|angularDistance
argument_list|(
name|options
operator|.
name|distance
argument_list|,
name|options
operator|.
name|radius
argument_list|)
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
decl_stmt|;
comment|//for the poles, do something slightly different
if|if
condition|(
name|point
index|[
name|LAT
index|]
operator|+
name|angDistDegs
operator|>
literal|90.0
condition|)
block|{
comment|//we cross the north pole
comment|//we don't need a longitude boundary at all
name|double
name|minLat
init|=
name|Math
operator|.
name|min
argument_list|(
name|ll
index|[
name|LAT
index|]
argument_list|,
name|ur
index|[
name|LAT
index|]
argument_list|)
decl_stmt|;
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LAT
argument_list|)
expr_stmt|;
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|minLat
argument_list|)
argument_list|,
literal|"90"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|point
index|[
name|LAT
index|]
operator|-
name|angDistDegs
operator|<
operator|-
literal|90.0
condition|)
block|{
comment|//we cross the south pole
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LAT
argument_list|)
expr_stmt|;
name|double
name|maxLat
init|=
name|Math
operator|.
name|max
argument_list|(
name|ll
index|[
name|LAT
index|]
argument_list|,
name|ur
index|[
name|LAT
index|]
argument_list|)
decl_stmt|;
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
literal|"-90"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|maxLat
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//Latitude
comment|//we may need to generate multiple queries depending on the range
comment|//Are we crossing the 180 deg. longitude, if so, we need to do some special things
if|if
condition|(
name|ll
index|[
name|LONG
index|]
operator|>
literal|0.0
operator|&&
name|ur
index|[
name|LONG
index|]
operator|<
literal|0.0
condition|)
block|{
comment|//TODO: refactor into common code, etc.
comment|//Now check other side of the Equator
if|if
condition|(
name|ll
index|[
name|LAT
index|]
operator|<
literal|0.0
operator|&&
name|ur
index|[
name|LAT
index|]
operator|>
literal|0.0
condition|)
block|{
name|addEquatorialBoundary
argument_list|(
name|parser
argument_list|,
name|options
argument_list|,
name|result
argument_list|,
name|ur
index|[
name|LAT
index|]
argument_list|,
name|ll
index|[
name|LAT
index|]
argument_list|)
expr_stmt|;
block|}
comment|//check poles
else|else
block|{
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LAT
argument_list|)
expr_stmt|;
comment|//not crossing the equator
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ll
index|[
name|LAT
index|]
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ur
index|[
name|LAT
index|]
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|//Longitude
name|addMeridianBoundary
argument_list|(
name|parser
argument_list|,
name|options
argument_list|,
name|result
argument_list|,
name|ur
index|[
name|LONG
index|]
argument_list|,
name|ll
index|[
name|LONG
index|]
argument_list|,
literal|"180.0"
argument_list|,
literal|"-180.0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ll
index|[
name|LONG
index|]
operator|<
literal|0.0
operator|&&
name|ur
index|[
name|LONG
index|]
operator|>
literal|0.0
condition|)
block|{
comment|//prime meridian (0 degrees
comment|//Now check other side of the Equator
if|if
condition|(
name|ll
index|[
name|LAT
index|]
operator|<
literal|0.0
operator|&&
name|ur
index|[
name|LAT
index|]
operator|>
literal|0.0
condition|)
block|{
name|addEquatorialBoundary
argument_list|(
name|parser
argument_list|,
name|options
argument_list|,
name|result
argument_list|,
name|ur
index|[
name|LAT
index|]
argument_list|,
name|ll
index|[
name|LAT
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LAT
argument_list|)
expr_stmt|;
comment|//not crossing the equator
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ll
index|[
name|LAT
index|]
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ur
index|[
name|LAT
index|]
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|//Longitude
name|addMeridianBoundary
argument_list|(
name|parser
argument_list|,
name|options
argument_list|,
name|result
argument_list|,
name|ur
index|[
name|LONG
index|]
argument_list|,
name|ll
index|[
name|LONG
index|]
argument_list|,
literal|"0.0"
argument_list|,
literal|".0"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we are all in the Eastern or Western hemi
comment|//Now check other side of the Equator
if|if
condition|(
name|ll
index|[
name|LAT
index|]
operator|<
literal|0.0
operator|&&
name|ur
index|[
name|LAT
index|]
operator|>
literal|0.0
condition|)
block|{
name|addEquatorialBoundary
argument_list|(
name|parser
argument_list|,
name|options
argument_list|,
name|result
argument_list|,
name|ur
index|[
name|LAT
index|]
argument_list|,
name|ll
index|[
name|LAT
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//we are all in either the Northern or the Southern Hemi.
comment|//TODO: nice to move this up so that it is the first thing and we can avoid the extra checks since
comment|//this is actually the most likely case
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LAT
argument_list|)
expr_stmt|;
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ll
index|[
name|LAT
index|]
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ur
index|[
name|LAT
index|]
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|//Longitude, all in the same hemi
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ll
index|[
name|LONG
index|]
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ur
index|[
name|LONG
index|]
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Add a boundary condition around a meridian      * @param parser      * @param options      * @param result      * @param upperRightLon      * @param lowerLeftLon      * @param eastern      * @param western      */
DECL|method|addMeridianBoundary
specifier|private
name|void
name|addMeridianBoundary
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SpatialOptions
name|options
parameter_list|,
name|BooleanQuery
name|result
parameter_list|,
name|double
name|upperRightLon
parameter_list|,
name|double
name|lowerLeftLon
parameter_list|,
name|String
name|eastern
parameter_list|,
name|String
name|western
parameter_list|)
block|{
name|SchemaField
name|subSF
decl_stmt|;
name|Query
name|range
decl_stmt|;
name|BooleanQuery
name|lonQ
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
comment|//Eastern Hemisphere
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|lowerLeftLon
argument_list|)
argument_list|,
name|eastern
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lonQ
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|//Western hemi
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|western
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|upperRightLon
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lonQ
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|//One or the other must occur
name|result
operator|.
name|add
argument_list|(
name|lonQ
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add query conditions for boundaries like the equator, poles and meridians    *    * @param parser    * @param options    * @param result    * @param upperRight    * @param lowerLeft    */
DECL|method|addEquatorialBoundary
specifier|protected
name|void
name|addEquatorialBoundary
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SpatialOptions
name|options
parameter_list|,
name|BooleanQuery
name|result
parameter_list|,
name|double
name|upperRight
parameter_list|,
name|double
name|lowerLeft
parameter_list|)
block|{
name|SchemaField
name|subSF
decl_stmt|;
name|Query
name|range
decl_stmt|;
name|BooleanQuery
name|tmpQ
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|subSF
operator|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|LAT
argument_list|)
expr_stmt|;
comment|//southern hemi.
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|lowerLeft
argument_list|)
argument_list|,
literal|"0"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tmpQ
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|//northern hemi
name|range
operator|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
literal|"0"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|upperRight
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tmpQ
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|//One or the other must occur
name|result
operator|.
name|add
argument_list|(
name|tmpQ
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
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
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|ValueSource
argument_list|>
name|vs
init|=
operator|new
name|ArrayList
argument_list|<
name|ValueSource
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sub
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|vs
operator|.
name|add
argument_list|(
name|sub
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|sub
argument_list|,
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LatLonValueSource
argument_list|(
name|field
argument_list|,
name|vs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isPolyField
specifier|public
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
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
literal|"Sorting not supported on SpatialTileField "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|//It never makes sense to create a single field, so make it impossible to happen
annotation|@
name|Override
DECL|method|createField
specifier|public
name|Field
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"SpatialTileField uses multiple fields.  field="
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

begin_class
DECL|class|LatLonValueSource
class|class
name|LatLonValueSource
extends|extends
name|VectorValueSource
block|{
DECL|field|sf
specifier|private
specifier|final
name|SchemaField
name|sf
decl_stmt|;
DECL|method|LatLonValueSource
specifier|public
name|LatLonValueSource
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|)
block|{
name|super
argument_list|(
name|sources
argument_list|)
expr_stmt|;
name|this
operator|.
name|sf
operator|=
name|sf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"latlon"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|sf
operator|.
name|getName
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

