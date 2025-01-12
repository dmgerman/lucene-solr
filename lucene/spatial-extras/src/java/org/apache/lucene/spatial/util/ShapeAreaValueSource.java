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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|index
operator|.
name|LeafReaderContext
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
name|FunctionValues
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
name|docvalues
operator|.
name|DoubleDocValues
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
name|Explanation
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
name|IndexSearcher
import|;
end_import

begin_comment
comment|/**  * The area of a Shape retrieved from a ValueSource via  * {@link org.apache.lucene.queries.function.FunctionValues#objectVal(int)}.  *  * @see Shape#getArea(org.locationtech.spatial4j.context.SpatialContext)  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|ShapeAreaValueSource
specifier|public
class|class
name|ShapeAreaValueSource
extends|extends
name|ValueSource
block|{
DECL|field|shapeValueSource
specifier|private
specifier|final
name|ValueSource
name|shapeValueSource
decl_stmt|;
DECL|field|ctx
specifier|private
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
comment|//not part of identity; should be associated with shapeValueSource indirectly
DECL|field|geoArea
specifier|private
specifier|final
name|boolean
name|geoArea
decl_stmt|;
DECL|field|multiplier
specifier|private
name|double
name|multiplier
decl_stmt|;
DECL|method|ShapeAreaValueSource
specifier|public
name|ShapeAreaValueSource
parameter_list|(
name|ValueSource
name|shapeValueSource
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|,
name|boolean
name|geoArea
parameter_list|,
name|double
name|multiplier
parameter_list|)
block|{
name|this
operator|.
name|shapeValueSource
operator|=
name|shapeValueSource
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|geoArea
operator|=
name|geoArea
expr_stmt|;
name|this
operator|.
name|multiplier
operator|=
name|multiplier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"area("
operator|+
name|shapeValueSource
operator|.
name|description
argument_list|()
operator|+
literal|",geo="
operator|+
name|geoArea
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|shapeValueSource
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|shapeValues
init|=
name|shapeValueSource
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Shape
name|shape
init|=
operator|(
name|Shape
operator|)
name|shapeValues
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|==
literal|null
operator|||
name|shape
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|0
return|;
comment|//or NaN?
comment|//This part of Spatial4j API is kinda weird. Passing null means 2D area, otherwise geo
comment|//   assuming ctx.isGeo()
return|return
name|shape
operator|.
name|getArea
argument_list|(
name|geoArea
condition|?
name|ctx
else|:
literal|null
argument_list|)
operator|*
name|multiplier
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|shapeValues
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|exp
init|=
name|super
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Explanation
argument_list|>
name|details
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|exp
operator|.
name|getDetails
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|details
operator|.
name|add
argument_list|(
name|shapeValues
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|exp
operator|.
name|getValue
argument_list|()
argument_list|,
name|exp
operator|.
name|getDescription
argument_list|()
argument_list|,
name|details
argument_list|)
return|;
block|}
block|}
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
name|ShapeAreaValueSource
name|that
init|=
operator|(
name|ShapeAreaValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|geoArea
operator|!=
name|that
operator|.
name|geoArea
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|shapeValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|shapeValueSource
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
name|shapeValueSource
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
name|geoArea
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

