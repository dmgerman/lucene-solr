begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Reusable geo-relation utility methods  */
end_comment

begin_class
DECL|class|GeoRelationUtils
specifier|public
class|class
name|GeoRelationUtils
block|{
comment|// No instance:
DECL|method|GeoRelationUtils
specifier|private
name|GeoRelationUtils
parameter_list|()
block|{   }
comment|/**    * Determine if a bbox (defined by minLat, maxLat, minLon, maxLon) contains the provided point (defined by lat, lon)    * NOTE: this is a basic method that does not handle dateline or pole crossing. Unwrapping must be done before    * calling this method.    */
DECL|method|pointInRectPrecise
specifier|public
specifier|static
name|boolean
name|pointInRectPrecise
parameter_list|(
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
return|return
name|lat
operator|>=
name|minLat
operator|&&
name|lat
operator|<=
name|maxLat
operator|&&
name|lon
operator|>=
name|minLon
operator|&&
name|lon
operator|<=
name|maxLon
return|;
block|}
comment|/////////////////////////
comment|// Rectangle relations
comment|/////////////////////////
comment|/**    * Computes whether two rectangles are disjoint    */
DECL|method|rectDisjoint
specifier|private
specifier|static
name|boolean
name|rectDisjoint
parameter_list|(
specifier|final
name|double
name|aMinLat
parameter_list|,
specifier|final
name|double
name|aMaxLat
parameter_list|,
specifier|final
name|double
name|aMinLon
parameter_list|,
specifier|final
name|double
name|aMaxLon
parameter_list|,
specifier|final
name|double
name|bMinLat
parameter_list|,
specifier|final
name|double
name|bMaxLat
parameter_list|,
specifier|final
name|double
name|bMinLon
parameter_list|,
specifier|final
name|double
name|bMaxLon
parameter_list|)
block|{
return|return
operator|(
name|aMaxLon
argument_list|<
name|bMinLon
operator|||
name|aMinLon
argument_list|>
name|bMaxLon
operator|||
name|aMaxLat
argument_list|<
name|bMinLat
operator|||
name|aMinLat
argument_list|>
name|bMaxLat
operator|)
return|;
block|}
comment|/**    * Computes whether the first (a) rectangle is wholly within another (b) rectangle (shared boundaries allowed)    */
DECL|method|rectWithin
specifier|public
specifier|static
name|boolean
name|rectWithin
parameter_list|(
specifier|final
name|double
name|aMinLat
parameter_list|,
specifier|final
name|double
name|aMaxLat
parameter_list|,
specifier|final
name|double
name|aMinLon
parameter_list|,
specifier|final
name|double
name|aMaxLon
parameter_list|,
specifier|final
name|double
name|bMinLat
parameter_list|,
specifier|final
name|double
name|bMaxLat
parameter_list|,
specifier|final
name|double
name|bMinLon
parameter_list|,
specifier|final
name|double
name|bMaxLon
parameter_list|)
block|{
return|return
operator|!
operator|(
name|aMinLon
argument_list|<
name|bMinLon
operator|||
name|aMinLat
argument_list|<
name|bMinLat
operator|||
name|aMaxLon
argument_list|>
name|bMaxLon
operator|||
name|aMaxLat
argument_list|>
name|bMaxLat
operator|)
return|;
block|}
comment|/**    * Computes whether two rectangles cross    */
DECL|method|rectCrosses
specifier|public
specifier|static
name|boolean
name|rectCrosses
parameter_list|(
specifier|final
name|double
name|aMinLat
parameter_list|,
specifier|final
name|double
name|aMaxLat
parameter_list|,
specifier|final
name|double
name|aMinLon
parameter_list|,
specifier|final
name|double
name|aMaxLon
parameter_list|,
specifier|final
name|double
name|bMinLat
parameter_list|,
specifier|final
name|double
name|bMaxLat
parameter_list|,
specifier|final
name|double
name|bMinLon
parameter_list|,
specifier|final
name|double
name|bMaxLon
parameter_list|)
block|{
return|return
operator|!
operator|(
name|rectDisjoint
argument_list|(
name|aMinLat
argument_list|,
name|aMaxLat
argument_list|,
name|aMinLon
argument_list|,
name|aMaxLon
argument_list|,
name|bMinLat
argument_list|,
name|bMaxLat
argument_list|,
name|bMinLon
argument_list|,
name|bMaxLon
argument_list|)
operator|||
name|rectWithin
argument_list|(
name|aMinLat
argument_list|,
name|aMaxLat
argument_list|,
name|aMinLon
argument_list|,
name|aMaxLon
argument_list|,
name|bMinLat
argument_list|,
name|bMaxLat
argument_list|,
name|bMinLon
argument_list|,
name|bMaxLon
argument_list|)
operator|)
return|;
block|}
comment|/**    * Computes whether a rectangle intersects another rectangle (crosses, within, touching, etc)    */
DECL|method|rectIntersects
specifier|public
specifier|static
name|boolean
name|rectIntersects
parameter_list|(
specifier|final
name|double
name|aMinLat
parameter_list|,
specifier|final
name|double
name|aMaxLat
parameter_list|,
specifier|final
name|double
name|aMinLon
parameter_list|,
specifier|final
name|double
name|aMaxLon
parameter_list|,
specifier|final
name|double
name|bMinLat
parameter_list|,
specifier|final
name|double
name|bMaxLat
parameter_list|,
specifier|final
name|double
name|bMinLon
parameter_list|,
specifier|final
name|double
name|bMaxLon
parameter_list|)
block|{
return|return
operator|!
operator|(
operator|(
name|aMaxLon
argument_list|<
name|bMinLon
operator|||
name|aMinLon
argument_list|>
name|bMaxLon
operator|||
name|aMaxLat
argument_list|<
name|bMinLat
operator|||
name|aMinLat
argument_list|>
name|bMaxLat
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

