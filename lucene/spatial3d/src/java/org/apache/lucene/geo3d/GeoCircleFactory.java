begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/**  * Class which constructs a GeoCircle representing an arbitrary circle.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoCircleFactory
specifier|public
class|class
name|GeoCircleFactory
block|{
DECL|method|GeoCircleFactory
specifier|private
name|GeoCircleFactory
parameter_list|()
block|{   }
comment|/**    * Create a GeoCircle of the right kind given the specified bounds.    * @param planetModel is the planet model.    * @param latitude is the center latitude.    * @param longitude is the center longitude.    * @param radius is the radius angle.    * @return a GeoCircle corresponding to what was specified.    */
DECL|method|makeGeoCircle
specifier|public
specifier|static
name|GeoCircle
name|makeGeoCircle
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|double
name|radius
parameter_list|)
block|{
if|if
condition|(
name|radius
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
return|return
operator|new
name|GeoDegeneratePoint
argument_list|(
name|planetModel
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|)
return|;
block|}
return|return
operator|new
name|GeoStandardCircle
argument_list|(
name|planetModel
argument_list|,
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radius
argument_list|)
return|;
block|}
block|}
end_class

end_unit

