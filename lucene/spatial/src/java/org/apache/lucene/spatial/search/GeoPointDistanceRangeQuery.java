begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
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
name|index
operator|.
name|IndexReader
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
name|spatial
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|TermEncoding
import|;
end_import

begin_comment
comment|/** Implements a point distance range query on a GeoPoint field. This is based on  * {@code org.apache.lucene.spatial.search.GeoPointDistanceQuery} and is implemented using a  * {@code org.apache.lucene.search.BooleanClause.MUST_NOT} clause to exclude any points that fall within  * minRadiusMeters from the provided point.  *  *    @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointDistanceRangeQuery
specifier|public
specifier|final
class|class
name|GeoPointDistanceRangeQuery
extends|extends
name|GeoPointDistanceQuery
block|{
DECL|field|minRadiusMeters
specifier|protected
specifier|final
name|double
name|minRadiusMeters
decl_stmt|;
comment|/**    * Constructs a query for all {@link org.apache.lucene.spatial.document.GeoPointField} types within a minimum / maximum    * distance (in meters) range from a given point    */
DECL|method|GeoPointDistanceRangeQuery
specifier|public
name|GeoPointDistanceRangeQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|minRadiusMeters
parameter_list|,
specifier|final
name|double
name|maxRadiusMeters
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|minRadiusMeters
argument_list|,
name|maxRadiusMeters
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoPointDistanceRangeQuery
specifier|public
name|GeoPointDistanceRangeQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|TermEncoding
name|termEncoding
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|minRadiusMeters
parameter_list|,
specifier|final
name|double
name|maxRadius
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|maxRadius
argument_list|)
expr_stmt|;
name|this
operator|.
name|minRadiusMeters
operator|=
name|minRadiusMeters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|Query
name|q
init|=
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|minRadiusMeters
operator|==
literal|0.0
condition|)
block|{
return|return
name|q
return|;
block|}
comment|// add an exclusion query
name|BooleanQuery
operator|.
name|Builder
name|bqb
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// create a new exclusion query
name|GeoPointDistanceQuery
name|exclude
init|=
operator|new
name|GeoPointDistanceQuery
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|minRadiusMeters
argument_list|)
decl_stmt|;
comment|// full map search
comment|//    if (radiusMeters>= GeoProjectionUtils.SEMIMINOR_AXIS) {
comment|//      bqb.add(new BooleanClause(new GeoPointInBBoxQuery(this.field, -180.0, -90.0, 180.0, 90.0), BooleanClause.Occur.MUST));
comment|//    } else {
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
comment|//    }
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|exclude
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bqb
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|" Center: ["
argument_list|)
operator|.
name|append
argument_list|(
name|centerLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|centerLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" From Distance: "
argument_list|)
operator|.
name|append
argument_list|(
name|minRadiusMeters
argument_list|)
operator|.
name|append
argument_list|(
literal|" m"
argument_list|)
operator|.
name|append
argument_list|(
literal|" To Distance: "
argument_list|)
operator|.
name|append
argument_list|(
name|radiusMeters
argument_list|)
operator|.
name|append
argument_list|(
literal|" m"
argument_list|)
operator|.
name|append
argument_list|(
literal|" Lower Left: ["
argument_list|)
operator|.
name|append
argument_list|(
name|minLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|minLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Upper Right: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|maxLat
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** getter method for minimum distance */
DECL|method|getMinRadiusMeters
specifier|public
name|double
name|getMinRadiusMeters
parameter_list|()
block|{
return|return
name|this
operator|.
name|minRadiusMeters
return|;
block|}
comment|/** getter method for maximum distance */
DECL|method|getMaxRadiusMeters
specifier|public
name|double
name|getMaxRadiusMeters
parameter_list|()
block|{
return|return
name|this
operator|.
name|radiusMeters
return|;
block|}
block|}
end_class

end_unit

