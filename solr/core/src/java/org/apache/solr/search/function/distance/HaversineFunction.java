begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|MultiValueSource
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

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|distance
operator|.
name|DistanceUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Calculate the Haversine formula (distance) between any two points on a sphere  * Takes in four value sources: (latA, lonA); (latB, lonB).  *<p>  * Assumes the value sources are in radians unless  *<p>  * See http://en.wikipedia.org/wiki/Great-circle_distance and  * http://en.wikipedia.org/wiki/Haversine_formula for the actual formula and  * also http://www.movable-type.co.uk/scripts/latlong.html  */
end_comment

begin_class
DECL|class|HaversineFunction
specifier|public
class|class
name|HaversineFunction
extends|extends
name|ValueSource
block|{
DECL|field|p1
specifier|private
name|MultiValueSource
name|p1
decl_stmt|;
DECL|field|p2
specifier|private
name|MultiValueSource
name|p2
decl_stmt|;
DECL|field|convertToRadians
specifier|private
name|boolean
name|convertToRadians
init|=
literal|false
decl_stmt|;
DECL|field|radius
specifier|private
name|double
name|radius
decl_stmt|;
DECL|method|HaversineFunction
specifier|public
name|HaversineFunction
parameter_list|(
name|MultiValueSource
name|p1
parameter_list|,
name|MultiValueSource
name|p2
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
name|this
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|,
name|radius
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|HaversineFunction
specifier|public
name|HaversineFunction
parameter_list|(
name|MultiValueSource
name|p1
parameter_list|,
name|MultiValueSource
name|p2
parameter_list|,
name|double
name|radius
parameter_list|,
name|boolean
name|convertToRads
parameter_list|)
block|{
name|this
operator|.
name|p1
operator|=
name|p1
expr_stmt|;
name|this
operator|.
name|p2
operator|=
name|p2
expr_stmt|;
if|if
condition|(
name|p1
operator|.
name|dimension
argument_list|()
operator|!=
literal|2
operator|||
name|p2
operator|.
name|dimension
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Illegal dimension for value sources"
argument_list|)
throw|;
block|}
name|this
operator|.
name|radius
operator|=
name|radius
expr_stmt|;
name|this
operator|.
name|convertToRadians
operator|=
name|convertToRads
expr_stmt|;
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"hsin"
return|;
block|}
comment|/**    * @param doc  The doc to score    * @return The haversine distance formula    */
DECL|method|distance
specifier|protected
name|double
name|distance
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
name|p1DV
parameter_list|,
name|FunctionValues
name|p2DV
parameter_list|)
throws|throws
name|IOException
block|{
name|double
index|[]
name|p1D
init|=
operator|new
name|double
index|[
literal|2
index|]
decl_stmt|;
name|double
index|[]
name|p2D
init|=
operator|new
name|double
index|[
literal|2
index|]
decl_stmt|;
name|p1DV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|,
name|p1D
argument_list|)
expr_stmt|;
name|p2DV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|,
name|p2D
argument_list|)
expr_stmt|;
name|double
name|y1
decl_stmt|;
name|double
name|x1
decl_stmt|;
name|double
name|y2
decl_stmt|;
name|double
name|x2
decl_stmt|;
if|if
condition|(
name|convertToRadians
condition|)
block|{
name|y1
operator|=
name|p1D
index|[
literal|0
index|]
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
expr_stmt|;
name|x1
operator|=
name|p1D
index|[
literal|1
index|]
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
expr_stmt|;
name|y2
operator|=
name|p2D
index|[
literal|0
index|]
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
expr_stmt|;
name|x2
operator|=
name|p2D
index|[
literal|1
index|]
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
expr_stmt|;
block|}
else|else
block|{
name|y1
operator|=
name|p1D
index|[
literal|0
index|]
expr_stmt|;
name|x1
operator|=
name|p1D
index|[
literal|1
index|]
expr_stmt|;
name|y2
operator|=
name|p2D
index|[
literal|0
index|]
expr_stmt|;
name|x2
operator|=
name|p2D
index|[
literal|1
index|]
expr_stmt|;
block|}
return|return
name|DistanceUtils
operator|.
name|distHaversineRAD
argument_list|(
name|y1
argument_list|,
name|x1
argument_list|,
name|y2
argument_list|,
name|x2
argument_list|)
operator|*
name|radius
return|;
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
name|vals1
init|=
name|p1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|vals2
init|=
name|p2
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
return|return
name|distance
argument_list|(
name|doc
argument_list|,
name|vals1
argument_list|,
name|vals2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
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
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|vals1
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|vals2
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
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
name|p1
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|p2
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
operator|.
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
name|HaversineFunction
name|other
init|=
operator|(
name|HaversineFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|p1
operator|.
name|equals
argument_list|(
name|other
operator|.
name|p1
argument_list|)
operator|&&
name|p2
operator|.
name|equals
argument_list|(
name|other
operator|.
name|p2
argument_list|)
operator|&&
name|radius
operator|==
name|other
operator|.
name|radius
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
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|result
operator|=
name|p1
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|p2
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|radius
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
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
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
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|p1
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|p2
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

