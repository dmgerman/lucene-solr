begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Searcher
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
name|search
operator|.
name|function
operator|.
name|DocValues
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
name|search
operator|.
name|function
operator|.
name|ValueSource
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
comment|/**  * Calculate the Haversine formula (distance) between any two points on a sphere  * Takes in four value sources: (latA, lonA); (latB, lonB).  *<p/>  * Assumes the value sources are in radians  *<p/>  * See http://en.wikipedia.org/wiki/Great-circle_distance and  * http://en.wikipedia.org/wiki/Haversine_formula for the actual formula and  * also http://www.movable-type.co.uk/scripts/latlong.html  *  * @see org.apache.solr.search.function.RadianFunction  */
end_comment

begin_class
DECL|class|HaversineFunction
specifier|public
class|class
name|HaversineFunction
extends|extends
name|ValueSource
block|{
DECL|field|x1
specifier|private
name|ValueSource
name|x1
decl_stmt|;
DECL|field|y1
specifier|private
name|ValueSource
name|y1
decl_stmt|;
DECL|field|x2
specifier|private
name|ValueSource
name|x2
decl_stmt|;
DECL|field|y2
specifier|private
name|ValueSource
name|y2
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
name|ValueSource
name|x1
parameter_list|,
name|ValueSource
name|y1
parameter_list|,
name|ValueSource
name|x2
parameter_list|,
name|ValueSource
name|y2
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
name|this
operator|.
name|x1
operator|=
name|x1
expr_stmt|;
name|this
operator|.
name|y1
operator|=
name|y1
expr_stmt|;
name|this
operator|.
name|x2
operator|=
name|x2
expr_stmt|;
name|this
operator|.
name|y2
operator|=
name|y2
expr_stmt|;
name|this
operator|.
name|radius
operator|=
name|radius
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
comment|/**    * @param doc  The doc to score    * @param x1DV    * @param y1DV    * @param x2DV    * @param y2DV    * @return The haversine distance formula    */
DECL|method|distance
specifier|protected
name|double
name|distance
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|x1DV
parameter_list|,
name|DocValues
name|y1DV
parameter_list|,
name|DocValues
name|x2DV
parameter_list|,
name|DocValues
name|y2DV
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
name|double
name|x1
init|=
name|x1DV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|//in radians
name|double
name|y1
init|=
name|y1DV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|double
name|x2
init|=
name|x2DV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|double
name|y2
init|=
name|y2DV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|//make sure they aren't all the same, as then we can just return 0
if|if
condition|(
operator|(
name|x1
operator|!=
name|x2
operator|)
operator|||
operator|(
name|y1
operator|!=
name|y2
operator|)
condition|)
block|{
name|double
name|diffX
init|=
name|x1
operator|-
name|x2
decl_stmt|;
name|double
name|diffY
init|=
name|y1
operator|-
name|y2
decl_stmt|;
name|double
name|hsinX
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffX
operator|/
literal|2
argument_list|)
decl_stmt|;
name|double
name|hsinY
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffY
operator|/
literal|2
argument_list|)
decl_stmt|;
name|double
name|h
init|=
name|hsinX
operator|*
name|hsinX
operator|+
operator|(
name|Math
operator|.
name|cos
argument_list|(
name|x1
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|x2
argument_list|)
operator|*
name|hsinY
operator|*
name|hsinY
operator|)
decl_stmt|;
name|result
operator|=
operator|(
name|radius
operator|*
literal|2
operator|*
name|Math
operator|.
name|atan2
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
name|h
argument_list|)
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|1
operator|-
name|h
argument_list|)
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocValues
name|x1DV
init|=
name|x1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
name|y1DV
init|=
name|y1
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
name|x2DV
init|=
name|x2
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|DocValues
name|y2DV
init|=
name|y2
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|doubleVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|distance
argument_list|(
name|doc
argument_list|,
name|x1DV
argument_list|,
name|y1DV
argument_list|,
name|x2DV
argument_list|,
name|y2DV
argument_list|)
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|doubleVal
argument_list|(
name|doc
argument_list|)
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
name|x1DV
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
name|y1DV
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
name|x2DV
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
name|y2DV
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
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|x1
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|x2
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|y1
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|y2
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
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
name|x1
operator|.
name|equals
argument_list|(
name|other
operator|.
name|x1
argument_list|)
operator|&&
name|y1
operator|.
name|equals
argument_list|(
name|other
operator|.
name|y1
argument_list|)
operator|&&
name|x2
operator|.
name|equals
argument_list|(
name|other
operator|.
name|x2
argument_list|)
operator|&&
name|y2
operator|.
name|equals
argument_list|(
name|other
operator|.
name|y2
argument_list|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|x1
operator|.
name|hashCode
argument_list|()
operator|+
name|x2
operator|.
name|hashCode
argument_list|()
operator|+
name|y1
operator|.
name|hashCode
argument_list|()
operator|+
name|y2
operator|.
name|hashCode
argument_list|()
operator|+
name|name
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
operator|+
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|x1
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|y1
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|x2
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|y2
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

