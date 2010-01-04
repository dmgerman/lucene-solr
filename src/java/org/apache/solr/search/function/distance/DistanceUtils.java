begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
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
name|distance
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Useful distance utiltities.  * solr-internal: subject to change w/o notification.  */
end_comment

begin_class
DECL|class|DistanceUtils
specifier|public
class|class
name|DistanceUtils
block|{
DECL|field|DEGREES_TO_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|DEGREES_TO_RADIANS
init|=
name|Math
operator|.
name|PI
operator|/
literal|180.0
decl_stmt|;
DECL|field|RADIANS_TO_DEGREES
specifier|public
specifier|static
specifier|final
name|double
name|RADIANS_TO_DEGREES
init|=
literal|180.0
operator|/
name|Math
operator|.
name|PI
decl_stmt|;
comment|/**    * @param x1     The x coordinate of the first point    * @param y1     The y coordinate of the first point    * @param x2     The x coordinate of the second point    * @param y2     The y coordinate of the second point    * @param radius The radius of the sphere    * @return The distance between the two points, as determined by the Haversine formula.    * @see org.apache.solr.search.function.distance.HaversineFunction    */
DECL|method|haversine
specifier|public
specifier|static
name|double
name|haversine
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|x2
parameter_list|,
name|double
name|y2
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
comment|//make sure they aren't all the same, as then we can just return 0
if|if
condition|(
operator|(
name|x1
operator|!=
name|x2
operator|)
operator|||
operator|(
name|y1
operator|!=
name|y2
operator|)
condition|)
block|{
name|double
name|diffX
init|=
name|x1
operator|-
name|x2
decl_stmt|;
name|double
name|diffY
init|=
name|y1
operator|-
name|y2
decl_stmt|;
name|double
name|hsinX
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffX
operator|*
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|hsinY
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffY
operator|*
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|h
init|=
name|hsinX
operator|*
name|hsinX
operator|+
operator|(
name|Math
operator|.
name|cos
argument_list|(
name|x1
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|x2
argument_list|)
operator|*
name|hsinY
operator|*
name|hsinY
operator|)
decl_stmt|;
name|result
operator|=
operator|(
name|radius
operator|*
literal|2
operator|*
name|Math
operator|.
name|atan2
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
name|h
argument_list|)
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|1
operator|-
name|h
argument_list|)
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Given a string containing<i>dimension</i> values encoded in it, separated by commas, return a String array of length<i>dimension</i>    * containing the values.    *    * @param out         A preallocated array.  Must be size dimension.  If it is not it will be resized.    * @param externalVal The value to parse    * @param dimension   The expected number of values for the point    * @return An array of the values that make up the point (aka vector)    * @throws {@link SolrException} if the dimension specified does not match the number of values in the externalValue.    */
DECL|method|parsePoint
specifier|public
specifier|static
name|String
index|[]
name|parsePoint
parameter_list|(
name|String
index|[]
name|out
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|int
name|dimension
parameter_list|)
block|{
comment|//TODO: Should we support sparse vectors?
if|if
condition|(
name|out
operator|==
literal|null
operator|||
name|out
operator|.
name|length
operator|!=
name|dimension
condition|)
name|out
operator|=
operator|new
name|String
index|[
name|dimension
index|]
expr_stmt|;
name|int
name|idx
init|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|idx
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
operator|&&
name|dimension
operator|==
literal|1
operator|&&
name|externalVal
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//we have a single point, dimension better be 1
name|out
index|[
literal|0
index|]
operator|=
name|externalVal
operator|.
name|trim
argument_list|()
expr_stmt|;
name|i
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|//if it is zero, that is an error
comment|//Parse out a comma separated list of point values, as in: 73.5,89.2,7773.4
for|for
control|(
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|==
literal|' '
condition|)
name|start
operator|++
expr_stmt|;
while|while
condition|(
name|end
operator|>
name|start
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|end
operator|-
literal|1
argument_list|)
operator|==
literal|' '
condition|)
name|end
operator|--
expr_stmt|;
name|out
index|[
name|i
index|]
operator|=
name|externalVal
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|start
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
name|end
operator|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|end
operator|==
operator|-
literal|1
condition|)
block|{
name|end
operator|=
name|externalVal
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|!=
name|dimension
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
literal|"incompatible dimension ("
operator|+
name|dimension
operator|+
literal|") and values ("
operator|+
name|externalVal
operator|+
literal|").  Only "
operator|+
name|i
operator|+
literal|" values specified"
argument_list|)
throw|;
block|}
return|return
name|out
return|;
block|}
comment|/**    * extract (by calling {@link #parsePoint(String[], String, int)} and validate the latitude and longitude contained    * in the String by making sure the latitude is between 90& -90 and longitude is between -180 and 180.    *<p/>    * The latitude is assumed to be the first part of the string and the longitude the second part.    *    * @param latLon    A preallocated array to hold the result    * @param latLonStr The string to parse    * @return The lat long    */
DECL|method|parseLatitudeLongitude
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|parseLatitudeLongitude
parameter_list|(
name|double
index|[]
name|latLon
parameter_list|,
name|String
name|latLonStr
parameter_list|)
block|{
if|if
condition|(
name|latLon
operator|==
literal|null
condition|)
block|{
name|latLon
operator|=
operator|new
name|double
index|[
literal|2
index|]
expr_stmt|;
block|}
name|String
index|[]
name|toks
init|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
name|latLonStr
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|latLon
index|[
literal|0
index|]
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|toks
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|latLon
index|[
literal|0
index|]
operator|<
operator|-
literal|90.0
operator|||
name|latLon
index|[
literal|0
index|]
operator|>
literal|90.0
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
literal|"Invalid latitude: latitudes are range -90 to 90: provided lat: ["
operator|+
name|latLon
index|[
literal|0
index|]
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|latLon
index|[
literal|1
index|]
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|toks
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|latLon
index|[
literal|1
index|]
operator|<
operator|-
literal|180.0
operator|||
name|latLon
index|[
literal|1
index|]
operator|>
literal|180.0
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
literal|"Invalid longitude: longitudes are range -180 to 180: provided lon: ["
operator|+
name|latLon
index|[
literal|1
index|]
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|latLon
return|;
block|}
block|}
end_class

end_unit

