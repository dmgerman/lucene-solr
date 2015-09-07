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
name|IndexReader
import|;
end_import

begin_comment
comment|/** Implements a simple bounding box query on a GeoPoint field. This is inspired by  * {@link org.apache.lucene.search.NumericRangeQuery} and is implemented using a  * two phase approach. First, candidate terms are queried using a numeric  * range based on the morton codes of the min and max lat/lon pairs. Terms  * passing this initial filter are passed to a final check that verifies whether  * the decoded lat/lon falls within (or on the boundary) of the query bounding box.  * The value comparisons are subject to a precision tolerance defined in  * {@value org.apache.lucene.util.GeoUtils#TOLERANCE}  *  * NOTES:  *    1.  All latitude/longitude values must be in decimal degrees.  *    2.  Complex computational geometry (e.g., dateline wrapping) is not supported  *    3.  For more advanced GeoSpatial indexing and query operations see spatial module  *    4.  This is well suited for small rectangles, large bounding boxes may result  *        in many terms, depending whether the bounding box falls on the boundary of  *        many cells (degenerate case)  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointInBBoxQuery
specifier|public
class|class
name|GeoPointInBBoxQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|minLon
specifier|protected
specifier|final
name|double
name|minLon
decl_stmt|;
DECL|field|minLat
specifier|protected
specifier|final
name|double
name|minLat
decl_stmt|;
DECL|field|maxLon
specifier|protected
specifier|final
name|double
name|maxLon
decl_stmt|;
DECL|field|maxLat
specifier|protected
specifier|final
name|double
name|maxLat
decl_stmt|;
DECL|method|GeoPointInBBoxQuery
specifier|public
name|GeoPointInBBoxQuery
parameter_list|(
specifier|final
name|String
name|field
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
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|minLon
operator|=
name|minLon
expr_stmt|;
name|this
operator|.
name|minLat
operator|=
name|minLat
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|maxLon
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|maxLat
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
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
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
name|GeoPointInBBoxQueryImpl
name|left
init|=
operator|new
name|GeoPointInBBoxQueryImpl
argument_list|(
name|field
argument_list|,
operator|-
literal|180.0D
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|left
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|GeoPointInBBoxQueryImpl
name|right
init|=
operator|new
name|GeoPointInBBoxQueryImpl
argument_list|(
name|field
argument_list|,
name|minLon
argument_list|,
name|minLat
argument_list|,
literal|180.0D
argument_list|,
name|maxLat
argument_list|)
decl_stmt|;
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|right
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
return|return
operator|new
name|GeoPointInBBoxQueryImpl
argument_list|(
name|field
argument_list|,
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoPointInBBoxQuery
operator|)
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
name|GeoPointInBBoxQuery
name|that
init|=
operator|(
name|GeoPointInBBoxQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|maxLat
argument_list|,
name|maxLat
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|maxLon
argument_list|,
name|maxLon
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|minLat
argument_list|,
name|minLat
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|minLon
argument_list|,
name|minLon
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
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
name|long
name|temp
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|minLon
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|minLat
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|maxLon
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|maxLat
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getField
specifier|public
specifier|final
name|String
name|getField
parameter_list|()
block|{
return|return
name|this
operator|.
name|field
return|;
block|}
DECL|method|getMinLon
specifier|public
specifier|final
name|double
name|getMinLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|minLon
return|;
block|}
DECL|method|getMinLat
specifier|public
specifier|final
name|double
name|getMinLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|minLat
return|;
block|}
DECL|method|getMaxLon
specifier|public
specifier|final
name|double
name|getMaxLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxLon
return|;
block|}
DECL|method|getMaxLat
specifier|public
specifier|final
name|double
name|getMaxLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxLat
return|;
block|}
block|}
end_class

end_unit

