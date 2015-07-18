begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|util
operator|.
name|AttributeSource
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|GeoUtils
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
name|util
operator|.
name|NumericUtils
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
name|util
operator|.
name|ToStringUtils
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
name|Arrays
import|;
end_import

begin_comment
comment|/** Implements a simple point in polygon query on a GeoPoint field. This is based on  * {@code GeoPointInBBoxQueryImpl} and is implemented using a  * three phase approach. First, like {@code GeoPointInBBoxQueryImpl}  * candidate terms are queried using a numeric range based on the morton codes  * of the min and max lat/lon pairs. Terms passing this initial filter are passed  * to a secondary filter that verifies whether the decoded lat/lon point falls within  * (or on the boundary) of the bounding box query. Finally, the remaining candidate  * term is passed to the final point in polygon check. All value comparisons are subject  * to the same precision tolerance defined in {@value org.apache.lucene.util.GeoUtils#TOLERANCE}  *  * NOTES:  *    1.  The polygon coordinates need to be in either clockwise or counter-clockwise order.  *    2.  The polygon must not be self-crossing, otherwise the query may result in unexpected behavior  *    3.  All latitude/longitude values must be in decimal degrees.  *    4.  Complex computational geometry (e.g., dateline wrapping, polygon with holes) is not supported  *    5.  For more advanced GeoSpatial indexing and query operations see spatial module  *  *    @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointInPolygonQuery
specifier|public
specifier|final
class|class
name|GeoPointInPolygonQuery
extends|extends
name|GeoPointInBBoxQueryImpl
block|{
comment|// polygon position arrays - this avoids the use of any objects or
comment|// or geo library dependencies
DECL|field|x
specifier|private
specifier|final
name|double
index|[]
name|x
decl_stmt|;
DECL|field|y
specifier|private
specifier|final
name|double
index|[]
name|y
decl_stmt|;
comment|/**    * Constructs a new GeoPolygonQuery that will match encoded {@link org.apache.lucene.document.GeoPointField} terms    * that fall within or on the boundary of the polygon defined by the input parameters.    */
DECL|method|GeoPointInPolygonQuery
specifier|public
name|GeoPointInPolygonQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
index|[]
name|polyLons
parameter_list|,
specifier|final
name|double
index|[]
name|polyLats
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|computeBBox
argument_list|(
name|polyLons
argument_list|,
name|polyLats
argument_list|)
argument_list|,
name|polyLons
argument_list|,
name|polyLats
argument_list|)
expr_stmt|;
block|}
comment|/** Common constructor, used only internally. */
DECL|method|GeoPointInPolygonQuery
specifier|private
name|GeoPointInPolygonQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|GeoBoundingBox
name|bbox
parameter_list|,
specifier|final
name|double
index|[]
name|polyLons
parameter_list|,
specifier|final
name|double
index|[]
name|polyLats
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|bbox
operator|.
name|minLon
argument_list|,
name|bbox
operator|.
name|minLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|)
expr_stmt|;
if|if
condition|(
name|polyLats
operator|.
name|length
operator|!=
name|polyLons
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats and polyLons must be equal length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|<
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least 4 polygon points required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
index|[
literal|0
index|]
operator|!=
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLats[0]="
operator|+
name|polyLats
index|[
literal|0
index|]
operator|+
literal|" polyLats["
operator|+
operator|(
name|polyLats
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
index|[
literal|0
index|]
operator|!=
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLons[0]="
operator|+
name|polyLons
index|[
literal|0
index|]
operator|+
literal|" polyLons["
operator|+
operator|(
name|polyLons
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
comment|// convert polygon vertices to coordinates within tolerance
name|this
operator|.
name|x
operator|=
name|toleranceConversion
argument_list|(
name|polyLons
argument_list|)
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|toleranceConversion
argument_list|(
name|polyLats
argument_list|)
expr_stmt|;
block|}
DECL|method|toleranceConversion
specifier|private
name|double
index|[]
name|toleranceConversion
parameter_list|(
name|double
index|[]
name|vals
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
operator|(
call|(
name|int
call|)
argument_list|(
name|vals
index|[
name|i
index|]
operator|/
name|GeoUtils
operator|.
name|TOLERANCE
argument_list|)
operator|)
operator|*
name|GeoUtils
operator|.
name|TOLERANCE
expr_stmt|;
block|}
return|return
name|vals
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
specifier|final
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|GeoPolygonTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|,
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLon
argument_list|,
name|this
operator|.
name|maxLat
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setRewriteMethod
specifier|public
name|void
name|setRewriteMethod
parameter_list|(
name|RewriteMethod
name|method
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot change rewrite method"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|GeoPointInPolygonQuery
name|that
init|=
operator|(
name|GeoPointInPolygonQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|x
argument_list|,
name|that
operator|.
name|x
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|y
argument_list|,
name|that
operator|.
name|y
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|x
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|hashCode
argument_list|(
name|x
argument_list|)
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|y
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|hashCode
argument_list|(
name|y
argument_list|)
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
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
assert|assert
name|x
operator|.
name|length
operator|==
name|y
operator|.
name|length
assert|;
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
name|getField
argument_list|()
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
name|getField
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
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" Points: "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|x
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|x
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|y
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Custom {@link org.apache.lucene.index.TermsEnum} that computes morton hash ranges based on the defined edges of    * the provided polygon.    */
DECL|class|GeoPolygonTermsEnum
specifier|private
specifier|final
class|class
name|GeoPolygonTermsEnum
extends|extends
name|GeoPointTermsEnum
block|{
DECL|method|GeoPolygonTermsEnum
name|GeoPolygonTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|,
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cellCrosses
specifier|protected
name|boolean
name|cellCrosses
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoUtils
operator|.
name|rectCrossesPoly
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|minLon
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|minLat
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|maxLon
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|maxLat
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cellWithin
specifier|protected
name|boolean
name|cellWithin
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoUtils
operator|.
name|rectWithinPoly
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|minLon
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|minLat
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|maxLon
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|maxLat
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cellIntersects
specifier|protected
name|boolean
name|cellIntersects
parameter_list|(
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|minLat
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|,
specifier|final
name|double
name|maxLat
parameter_list|)
block|{
return|return
name|GeoUtils
operator|.
name|rectIntersects
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|minLon
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|minLat
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|maxLon
argument_list|,
name|GeoPointInPolygonQuery
operator|.
name|this
operator|.
name|maxLat
argument_list|)
return|;
block|}
comment|/**      * The two-phase query approach. The parent      * {@link org.apache.lucene.search.GeoPointTermsEnum#accept} method is called to match      * encoded terms that fall within the bounding box of the polygon. Those documents that pass the initial      * bounding box filter are then compared to the provided polygon using the      * {@link org.apache.lucene.util.GeoUtils#pointInPolygon} method.      *      * @param term term for candidate document      * @return match status      */
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
comment|// first filter by bounding box
name|AcceptStatus
name|status
init|=
name|super
operator|.
name|accept
argument_list|(
name|term
argument_list|)
decl_stmt|;
assert|assert
name|status
operator|!=
name|AcceptStatus
operator|.
name|YES_AND_SEEK
assert|;
if|if
condition|(
name|status
operator|!=
name|AcceptStatus
operator|.
name|YES
condition|)
block|{
return|return
name|status
return|;
block|}
specifier|final
name|long
name|val
init|=
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|term
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lon
init|=
name|GeoUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|val
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lat
init|=
name|GeoUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|val
argument_list|)
decl_stmt|;
comment|// post-filter by point in polygon
if|if
condition|(
operator|!
name|GeoUtils
operator|.
name|pointInPolygon
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
block|}
DECL|method|computeBBox
specifier|private
specifier|static
name|GeoBoundingBox
name|computeBBox
parameter_list|(
name|double
index|[]
name|polyLons
parameter_list|,
name|double
index|[]
name|polyLats
parameter_list|)
block|{
if|if
condition|(
name|polyLons
operator|.
name|length
operator|!=
name|polyLats
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLons and polyLats must be equal length"
argument_list|)
throw|;
block|}
name|double
name|minLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|minLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid polyLons["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|polyLons
index|[
name|i
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid polyLats["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|polyLats
index|[
name|i
index|]
argument_list|)
throw|;
block|}
name|minLon
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
name|minLat
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|minLat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GeoBoundingBox
argument_list|(
name|minLon
argument_list|,
name|maxLon
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
return|;
block|}
comment|/**    * API utility method for returning the array of longitudinal values for this GeoPolygon    * The returned array is not a copy so do not change it!    */
DECL|method|getLons
specifier|public
name|double
index|[]
name|getLons
parameter_list|()
block|{
return|return
name|this
operator|.
name|x
return|;
block|}
comment|/**    * API utility method for returning the array of latitudinal values for this GeoPolygon    * The returned array is not a copy so do not change it!    */
DECL|method|getLats
specifier|public
name|double
index|[]
name|getLats
parameter_list|()
block|{
return|return
name|this
operator|.
name|y
return|;
block|}
block|}
end_class

end_unit

