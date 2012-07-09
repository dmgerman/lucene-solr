begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|query
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|exception
operator|.
name|InvalidSpatialArgument
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_comment
comment|/**  * Principally holds the query {@link Shape} and the {@link SpatialOperation}.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialArgs
specifier|public
class|class
name|SpatialArgs
block|{
DECL|field|DEFAULT_DIST_PRECISION
specifier|public
specifier|static
specifier|final
name|double
name|DEFAULT_DIST_PRECISION
init|=
literal|0.025d
decl_stmt|;
DECL|field|operation
specifier|private
name|SpatialOperation
name|operation
decl_stmt|;
DECL|field|shape
specifier|private
name|Shape
name|shape
decl_stmt|;
DECL|field|distPrecision
specifier|private
name|double
name|distPrecision
init|=
name|DEFAULT_DIST_PRECISION
decl_stmt|;
comment|// Useful for 'distance' calculations
DECL|field|min
specifier|private
name|Double
name|min
decl_stmt|;
DECL|field|max
specifier|private
name|Double
name|max
decl_stmt|;
DECL|method|SpatialArgs
specifier|public
name|SpatialArgs
parameter_list|(
name|SpatialOperation
name|operation
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
DECL|method|SpatialArgs
specifier|public
name|SpatialArgs
parameter_list|(
name|SpatialOperation
name|operation
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
block|}
comment|/** Check if the arguments make sense -- throw an exception if not */
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|InvalidSpatialArgument
block|{
if|if
condition|(
name|operation
operator|.
name|isTargetNeedsArea
argument_list|()
operator|&&
operator|!
name|shape
operator|.
name|hasArea
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidSpatialArgument
argument_list|(
name|operation
operator|+
literal|" only supports geometry with area"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
name|operation
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
name|shape
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|!=
literal|null
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
literal|" min="
argument_list|)
operator|.
name|append
argument_list|(
name|min
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|max
operator|!=
literal|null
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
literal|" max="
argument_list|)
operator|.
name|append
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
literal|" distPrec="
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.2f%%"
argument_list|,
name|distPrecision
operator|/
literal|100d
argument_list|)
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//------------------------------------------------
comment|// Getters& Setters
comment|//------------------------------------------------
DECL|method|getOperation
specifier|public
name|SpatialOperation
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
DECL|method|setOperation
specifier|public
name|void
name|setOperation
parameter_list|(
name|SpatialOperation
name|operation
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
comment|/** Considers {@link SpatialOperation#BBoxWithin} in returning the shape. */
DECL|method|getShape
specifier|public
name|Shape
name|getShape
parameter_list|()
block|{
if|if
condition|(
name|shape
operator|!=
literal|null
operator|&&
operator|(
name|operation
operator|==
name|SpatialOperation
operator|.
name|BBoxWithin
operator|||
name|operation
operator|==
name|SpatialOperation
operator|.
name|BBoxIntersects
operator|)
condition|)
return|return
name|shape
operator|.
name|getBoundingBox
argument_list|()
return|;
return|return
name|shape
return|;
block|}
DECL|method|setShape
specifier|public
name|void
name|setShape
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
block|}
comment|/**    * The fraction of the distance from the center of the query shape to its nearest edge    * that is considered acceptable error. The algorithm for computing the distance to the    * nearest edge is actually a little different. It normalizes the shape to a square    * given it's bounding box area:    *<pre>sqrt(shape.bbox.area)/2</pre>    * And the error distance is beyond the shape such that the shape is a minimum shape.    */
DECL|method|getDistPrecision
specifier|public
name|Double
name|getDistPrecision
parameter_list|()
block|{
return|return
name|distPrecision
return|;
block|}
DECL|method|setDistPrecision
specifier|public
name|void
name|setDistPrecision
parameter_list|(
name|Double
name|distPrecision
parameter_list|)
block|{
if|if
condition|(
name|distPrecision
operator|!=
literal|null
condition|)
name|this
operator|.
name|distPrecision
operator|=
name|distPrecision
expr_stmt|;
block|}
DECL|method|getMin
specifier|public
name|Double
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
DECL|method|setMin
specifier|public
name|void
name|setMin
parameter_list|(
name|Double
name|min
parameter_list|)
block|{
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
block|}
DECL|method|getMax
specifier|public
name|Double
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
DECL|method|setMax
specifier|public
name|void
name|setMax
parameter_list|(
name|Double
name|max
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
block|}
end_class

end_unit

