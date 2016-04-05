begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package

begin_comment
comment|/**  * Class which constructs a GeoPath representing an arbitrary path.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPathFactory
specifier|public
class|class
name|GeoPathFactory
block|{
DECL|method|GeoPathFactory
specifier|private
name|GeoPathFactory
parameter_list|()
block|{   }
comment|/**    * Create a GeoPath of the right kind given the specified information.    * @param planetModel is the planet model.    * @param maxCutoffAngle is the width of the path, measured as an angle.    * @param pathPoints are the points in the path.    * @return a GeoPath corresponding to what was specified.    */
DECL|method|makeGeoPath
specifier|public
specifier|static
name|GeoPath
name|makeGeoPath
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|maxCutoffAngle
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|pathPoints
parameter_list|)
block|{
return|return
operator|new
name|GeoStandardPath
argument_list|(
name|planetModel
argument_list|,
name|maxCutoffAngle
argument_list|,
name|pathPoints
argument_list|)
return|;
block|}
block|}
end_class

end_unit

