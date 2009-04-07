begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
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
name|ConstantScoreQuery
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
name|Filter
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
name|QueryWrapperFilter
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
name|SerialChainFilter
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
name|geohash
operator|.
name|GeoHashDistanceFilter
import|;
end_import

begin_class
DECL|class|DistanceQueryBuilder
specifier|public
class|class
name|DistanceQueryBuilder
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|latFilter
specifier|public
name|BoundaryBoxFilter
name|latFilter
decl_stmt|;
DECL|field|lngFilter
specifier|public
name|BoundaryBoxFilter
name|lngFilter
decl_stmt|;
DECL|field|distanceFilter
specifier|public
name|DistanceFilter
name|distanceFilter
decl_stmt|;
DECL|field|lat
specifier|private
specifier|final
name|double
name|lat
decl_stmt|;
DECL|field|lng
specifier|private
specifier|final
name|double
name|lng
decl_stmt|;
DECL|field|miles
specifier|private
specifier|final
name|double
name|miles
decl_stmt|;
DECL|field|cartesianFilter
specifier|private
name|Filter
name|cartesianFilter
decl_stmt|;
DECL|field|needPrecision
specifier|private
name|boolean
name|needPrecision
init|=
literal|true
decl_stmt|;
comment|/**    * Create a distance query using    * a boundary box wrapper around a more precise    * DistanceFilter.    *     * @see SerialChainFilter    * @param lat    * @param lng    * @param miles    */
DECL|method|DistanceQueryBuilder
specifier|public
name|DistanceQueryBuilder
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lng
parameter_list|,
name|double
name|miles
parameter_list|,
name|String
name|latField
parameter_list|,
name|String
name|lngField
parameter_list|,
name|String
name|tierFieldPrefix
parameter_list|,
name|boolean
name|needPrecise
parameter_list|)
block|{
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lng
operator|=
name|lng
expr_stmt|;
name|this
operator|.
name|miles
operator|=
name|miles
expr_stmt|;
name|this
operator|.
name|needPrecision
operator|=
name|needPrecise
expr_stmt|;
name|CartesianPolyFilterBuilder
name|cpf
init|=
operator|new
name|CartesianPolyFilterBuilder
argument_list|(
name|tierFieldPrefix
argument_list|)
decl_stmt|;
name|cartesianFilter
operator|=
name|cpf
operator|.
name|getBoundingArea
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
operator|(
name|int
operator|)
name|miles
argument_list|)
expr_stmt|;
comment|/* create precise distance filter */
if|if
condition|(
name|needPrecise
condition|)
name|distanceFilter
operator|=
operator|new
name|LatLongDistanceFilter
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|miles
argument_list|,
name|latField
argument_list|,
name|lngField
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a distance query using    * a boundary box wrapper around a more precise    * DistanceFilter.    *     * @see SerialChainFilter    * @param lat    * @param lng    * @param miles    */
DECL|method|DistanceQueryBuilder
specifier|public
name|DistanceQueryBuilder
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lng
parameter_list|,
name|double
name|miles
parameter_list|,
name|String
name|geoHashFieldPrefix
parameter_list|,
name|String
name|tierFieldPrefix
parameter_list|,
name|boolean
name|needPrecise
parameter_list|)
block|{
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lng
operator|=
name|lng
expr_stmt|;
name|this
operator|.
name|miles
operator|=
name|miles
expr_stmt|;
name|this
operator|.
name|needPrecision
operator|=
name|needPrecise
expr_stmt|;
name|CartesianPolyFilterBuilder
name|cpf
init|=
operator|new
name|CartesianPolyFilterBuilder
argument_list|(
name|tierFieldPrefix
argument_list|)
decl_stmt|;
name|cartesianFilter
operator|=
name|cpf
operator|.
name|getBoundingArea
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
operator|(
name|int
operator|)
name|miles
argument_list|)
expr_stmt|;
comment|/* create precise distance filter */
if|if
condition|(
name|needPrecise
condition|)
name|distanceFilter
operator|=
operator|new
name|GeoHashDistanceFilter
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|miles
argument_list|,
name|geoHashFieldPrefix
argument_list|)
expr_stmt|;
block|}
comment|/**   * Create a distance query using   * a boundary box wrapper around a more precise   * DistanceFilter.   *    * @see SerialChainFilter   * @param lat   * @param lng   * @param miles   */
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
name|Filter
index|[]
name|f
decl_stmt|;
name|int
index|[]
name|chain
decl_stmt|;
if|if
condition|(
name|needPrecision
condition|)
block|{
name|f
operator|=
operator|new
name|Filter
index|[]
block|{
name|cartesianFilter
block|,
name|distanceFilter
block|}
expr_stmt|;
name|chain
operator|=
operator|new
name|int
index|[]
block|{
name|SerialChainFilter
operator|.
name|AND
block|,
name|SerialChainFilter
operator|.
name|SERIALAND
block|}
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
operator|new
name|Filter
index|[]
block|{
name|cartesianFilter
block|}
expr_stmt|;
name|chain
operator|=
operator|new
name|int
index|[]
block|{
name|SerialChainFilter
operator|.
name|AND
block|}
expr_stmt|;
block|}
return|return
operator|new
name|SerialChainFilter
argument_list|(
name|f
argument_list|,
name|chain
argument_list|)
return|;
block|}
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|QueryWrapperFilter
name|qf
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Filter
index|[]
name|f
decl_stmt|;
name|int
index|[]
name|chain
decl_stmt|;
if|if
condition|(
name|needPrecision
condition|)
block|{
name|f
operator|=
operator|new
name|Filter
index|[]
block|{
name|cartesianFilter
block|,
name|qf
block|,
name|distanceFilter
block|}
expr_stmt|;
name|chain
operator|=
operator|new
name|int
index|[]
block|{
name|SerialChainFilter
operator|.
name|AND
block|,
name|SerialChainFilter
operator|.
name|AND
block|,
name|SerialChainFilter
operator|.
name|SERIALAND
block|}
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
operator|new
name|Filter
index|[]
block|{
name|cartesianFilter
block|,
name|qf
block|}
expr_stmt|;
name|chain
operator|=
operator|new
name|int
index|[]
block|{
name|SerialChainFilter
operator|.
name|AND
block|,
name|SerialChainFilter
operator|.
name|AND
block|}
expr_stmt|;
block|}
return|return
operator|new
name|SerialChainFilter
argument_list|(
name|f
argument_list|,
name|chain
argument_list|)
return|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|getFilter
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|getFilter
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getLat
specifier|public
name|double
name|getLat
parameter_list|()
block|{
return|return
name|lat
return|;
block|}
DECL|method|getLng
specifier|public
name|double
name|getLng
parameter_list|()
block|{
return|return
name|lng
return|;
block|}
DECL|method|getMiles
specifier|public
name|double
name|getMiles
parameter_list|()
block|{
return|return
name|miles
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DistanceQuery lat: "
operator|+
name|lat
operator|+
literal|" lng: "
operator|+
name|lng
operator|+
literal|" miles: "
operator|+
name|miles
return|;
block|}
block|}
end_class

end_unit

