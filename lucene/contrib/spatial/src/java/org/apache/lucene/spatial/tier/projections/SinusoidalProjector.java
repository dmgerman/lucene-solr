begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.tier.projections
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
operator|.
name|projections
package|;
end_package

begin_comment
comment|/**  * Based on Sinusoidal Projections  * Project a latitude / longitude on a 2D cartesian map  *<p/>  * THIS PROJECTION IS WRONG, but it's not going to be fixed b/c it will break a lot of existing tests, plus we are deprecating  * most of the existing spatial and replacing with a more reliable approach.  *  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  *  * @deprecated Until we can put in place proper tests and a proper fix.   */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SinusoidalProjector
specifier|public
class|class
name|SinusoidalProjector
implements|implements
name|IProjector
block|{
DECL|method|coordsAsString
specifier|public
name|String
name|coordsAsString
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|coords
specifier|public
name|double
index|[]
name|coords
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
name|double
name|rlat
init|=
name|Math
operator|.
name|toRadians
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
name|double
name|rlong
init|=
name|Math
operator|.
name|toRadians
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
name|double
name|nlat
init|=
name|rlong
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|rlat
argument_list|)
decl_stmt|;
name|double
name|r
index|[]
init|=
block|{
name|nlat
block|,
name|rlong
block|}
decl_stmt|;
return|return
name|r
return|;
block|}
block|}
end_class

begin_comment
comment|/* This whole file should really be:*/
end_comment

begin_comment
comment|/**  * Based on Sinusoidal Projections  * Project a latitude / longitude on a 2D cartesian map using the Prime Meridian as the "central meridian"  *  * See http://en.wikipedia.org/wiki/Sinusoidal_projection  *  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_comment
comment|/* public class SinusoidalProjector implements IProjector {     public String coordsAsString(double latitude, double longitude) {     double [] coords = coords(latitude, longitude);     return coords[0] + "," + coords[1];   }    public double[] coords(double latitude, double longitude) {     double rlat = latitude * DistanceUtils.DEGREES_TO_RADIANS;     double rlong = longitude * DistanceUtils.DEGREES_TO_RADIANS;     double x = rlong * Math.cos(rlat);     return new double[]{x, rlat};    }  } */
end_comment

end_unit

