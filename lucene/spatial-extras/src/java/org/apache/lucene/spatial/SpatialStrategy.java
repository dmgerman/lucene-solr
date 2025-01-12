begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Point
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
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
name|document
operator|.
name|Field
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ReciprocalFloatFunction
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
name|query
operator|.
name|SpatialArgs
import|;
end_import

begin_comment
comment|/**  * The SpatialStrategy encapsulates an approach to indexing and searching based  * on shapes.  *<p>  * Different implementations will support different features. A strategy should  * document these common elements:  *<ul>  *<li>Can it index more than one shape per field?</li>  *<li>What types of shapes can be indexed?</li>  *<li>What types of query shapes can be used?</li>  *<li>What types of query operations are supported?  *   This might vary per shape.</li>  *<li>Does it use some type of cache?  When?  *</ul>  * If a strategy only supports certain shapes at index or query time, then in  * general it will throw an exception if given an incompatible one.  It will not  * be coerced into compatibility.  *<p>  * Note that a SpatialStrategy is not involved with the Lucene stored field  * values of shapes, which is immaterial to indexing and search.  *<p>  * Thread-safe.  *<p>  * This API is marked as experimental, however it is quite stable.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialStrategy
specifier|public
specifier|abstract
class|class
name|SpatialStrategy
block|{
DECL|field|ctx
specifier|protected
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
comment|/**    * Constructs the spatial strategy with its mandatory arguments.    */
DECL|method|SpatialStrategy
specifier|public
name|SpatialStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ctx is required"
argument_list|)
throw|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
operator|||
name|fieldName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName is required"
argument_list|)
throw|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
DECL|method|getSpatialContext
specifier|public
name|SpatialContext
name|getSpatialContext
parameter_list|()
block|{
return|return
name|ctx
return|;
block|}
comment|/**    * The name of the field or the prefix of them if there are multiple    * fields needed internally.    * @return Not null.    */
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
comment|/**    * Returns the IndexableField(s) from the {@code shape} that are to be    * added to the {@link org.apache.lucene.document.Document}.  These fields    * are expected to be marked as indexed and not stored.    *<p>    * Note: If you want to<i>store</i> the shape as a string for retrieval in    * search results, you could add it like this:    *<pre>document.add(new StoredField(fieldName,ctx.toString(shape)));</pre>    * The particular string representation used doesn't matter to the Strategy    * since it doesn't use it.    *    * @return Not null nor will it have null elements.    * @throws UnsupportedOperationException if given a shape incompatible with the strategy    */
DECL|method|createIndexableFields
specifier|public
specifier|abstract
name|Field
index|[]
name|createIndexableFields
parameter_list|(
name|Shape
name|shape
parameter_list|)
function_decl|;
comment|/**    * See {@link #makeDistanceValueSource(org.locationtech.spatial4j.shape.Point, double)} called with    * a multiplier of 1.0 (i.e. units of degrees).    */
DECL|method|makeDistanceValueSource
specifier|public
name|ValueSource
name|makeDistanceValueSource
parameter_list|(
name|Point
name|queryPoint
parameter_list|)
block|{
return|return
name|makeDistanceValueSource
argument_list|(
name|queryPoint
argument_list|,
literal|1.0
argument_list|)
return|;
block|}
comment|/**    * Make a ValueSource returning the distance between the center of the    * indexed shape and {@code queryPoint}.  If there are multiple indexed shapes    * then the closest one is chosen. The result is multiplied by {@code multiplier}, which    * conveniently is used to get the desired units.    */
DECL|method|makeDistanceValueSource
specifier|public
specifier|abstract
name|ValueSource
name|makeDistanceValueSource
parameter_list|(
name|Point
name|queryPoint
parameter_list|,
name|double
name|multiplier
parameter_list|)
function_decl|;
comment|/**    * Make a Query based principally on {@link org.apache.lucene.spatial.query.SpatialOperation}    * and {@link Shape} from the supplied {@code args}.  It should be constant scoring of 1.    *    * @throws UnsupportedOperationException If the strategy does not support the shape in {@code args}    * @throws org.apache.lucene.spatial.query.UnsupportedSpatialOperation If the strategy does not support the {@link    * org.apache.lucene.spatial.query.SpatialOperation} in {@code args}.    */
DECL|method|makeQuery
specifier|public
specifier|abstract
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
function_decl|;
comment|/**    * Returns a ValueSource with values ranging from 1 to 0, depending inversely    * on the distance from {@link #makeDistanceValueSource(org.locationtech.spatial4j.shape.Point,double)}.    * The formula is {@code c/(d + c)} where 'd' is the distance and 'c' is    * one tenth the distance to the farthest edge from the center. Thus the    * scores will be 1 for indexed points at the center of the query shape and as    * low as ~0.1 at its furthest edges.    */
DECL|method|makeRecipDistanceValueSource
specifier|public
specifier|final
name|ValueSource
name|makeRecipDistanceValueSource
parameter_list|(
name|Shape
name|queryShape
parameter_list|)
block|{
name|Rectangle
name|bbox
init|=
name|queryShape
operator|.
name|getBoundingBox
argument_list|()
decl_stmt|;
name|double
name|diagonalDist
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|ctx
operator|.
name|makePoint
argument_list|(
name|bbox
operator|.
name|getMinX
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMinY
argument_list|()
argument_list|)
argument_list|,
name|bbox
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|distToEdge
init|=
name|diagonalDist
operator|*
literal|0.5
decl_stmt|;
name|float
name|c
init|=
operator|(
name|float
operator|)
name|distToEdge
operator|*
literal|0.1f
decl_stmt|;
comment|//one tenth
return|return
operator|new
name|ReciprocalFloatFunction
argument_list|(
name|makeDistanceValueSource
argument_list|(
name|queryShape
operator|.
name|getCenter
argument_list|()
argument_list|,
literal|1.0
argument_list|)
argument_list|,
literal|1f
argument_list|,
name|c
argument_list|,
name|c
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" field:"
operator|+
name|fieldName
operator|+
literal|" ctx="
operator|+
name|ctx
return|;
block|}
block|}
end_class

end_unit

