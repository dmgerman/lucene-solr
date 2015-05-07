begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Factory for {@link org.apache.lucene.spatial.spatial4j.geo3d.GeoArea}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoAreaFactory
specifier|public
class|class
name|GeoAreaFactory
block|{
DECL|method|GeoAreaFactory
specifier|private
name|GeoAreaFactory
parameter_list|()
block|{   }
comment|/**    * Create a GeoArea of the right kind given the specified bounds.    *    * @param topLat    is the top latitude    * @param bottomLat is the bottom latitude    * @param leftLon   is the left longitude    * @param rightLon  is the right longitude    * @return a GeoArea corresponding to what was specified.    */
DECL|method|makeGeoArea
specifier|public
specifier|static
name|GeoArea
name|makeGeoArea
parameter_list|(
name|double
name|topLat
parameter_list|,
name|double
name|bottomLat
parameter_list|,
name|double
name|leftLon
parameter_list|,
name|double
name|rightLon
parameter_list|)
block|{
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|topLat
argument_list|,
name|bottomLat
argument_list|,
name|leftLon
argument_list|,
name|rightLon
argument_list|)
return|;
block|}
block|}
end_class

end_unit
