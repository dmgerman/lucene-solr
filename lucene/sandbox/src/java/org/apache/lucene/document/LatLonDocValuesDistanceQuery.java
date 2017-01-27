begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
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
name|geo
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
name|index
operator|.
name|SortedNumericDocValues
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
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|search
operator|.
name|Scorer
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
name|TwoPhaseIterator
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
name|Weight
import|;
end_import

begin_comment
comment|/** Distance query for {@link LatLonDocValuesField}. */
end_comment

begin_class
DECL|class|LatLonDocValuesDistanceQuery
specifier|final
class|class
name|LatLonDocValuesDistanceQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|latitude
DECL|field|longitude
specifier|private
specifier|final
name|double
name|latitude
decl_stmt|,
name|longitude
decl_stmt|;
DECL|field|radiusMeters
specifier|private
specifier|final
name|double
name|radiusMeters
decl_stmt|;
DECL|method|LatLonDocValuesDistanceQuery
name|LatLonDocValuesDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
if|if
condition|(
name|Double
operator|.
name|isFinite
argument_list|(
name|radiusMeters
argument_list|)
operator|==
literal|false
operator|||
name|radiusMeters
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"radiusMeters: '"
operator|+
name|radiusMeters
operator|+
literal|"' is invalid"
argument_list|)
throw|;
block|}
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|latitude
operator|=
name|latitude
expr_stmt|;
name|this
operator|.
name|longitude
operator|=
name|longitude
expr_stmt|;
name|this
operator|.
name|radiusMeters
operator|=
name|radiusMeters
expr_stmt|;
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
name|sb
operator|.
name|append
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" +/- "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" meters"
argument_list|)
expr_stmt|;
return|return
name|sb
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|sameClassAs
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|LatLonDocValuesDistanceQuery
name|other
init|=
operator|(
name|LatLonDocValuesDistanceQuery
operator|)
name|obj
decl_stmt|;
return|return
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
operator|&&
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
operator|==
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|latitude
argument_list|)
operator|&&
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
operator|==
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|longitude
argument_list|)
operator|&&
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|radiusMeters
argument_list|)
operator|==
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|radiusMeters
argument_list|)
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
name|h
init|=
name|classHash
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Double
operator|.
name|hashCode
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Double
operator|.
name|hashCode
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Double
operator|.
name|hashCode
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|,
name|boost
argument_list|)
block|{
specifier|private
specifier|final
name|GeoEncodingUtils
operator|.
name|DistancePredicate
name|distancePredicate
init|=
name|GeoEncodingUtils
operator|.
name|createDistancePredicate
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getSortedNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|TwoPhaseIterator
name|iterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|values
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|count
init|=
name|values
operator|.
name|docValueCount
argument_list|()
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|value
init|=
name|values
operator|.
name|nextValue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|lat
init|=
call|(
name|int
call|)
argument_list|(
name|value
operator|>>>
literal|32
argument_list|)
decl_stmt|;
specifier|final
name|int
name|lon
init|=
call|(
name|int
call|)
argument_list|(
name|value
operator|&
literal|0xFFFFFFFF
argument_list|)
decl_stmt|;
if|if
condition|(
name|distancePredicate
operator|.
name|apply
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|100f
return|;
comment|// TODO: what should it be?
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|boost
argument_list|,
name|iterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

