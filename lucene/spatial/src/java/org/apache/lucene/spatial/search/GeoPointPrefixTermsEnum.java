begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|BytesRefBuilder
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
name|util
operator|.
name|GeoEncodingUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
operator|.
name|mortonHash
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
operator|.
name|geoCodedToPrefixCoded
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
operator|.
name|prefixCodedToGeoCoded
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoEncodingUtils
operator|.
name|getPrefixCodedShift
import|;
end_import

begin_comment
comment|/**  * Decomposes a given {@link GeoPointMultiTermQuery} into a set of terms that represent the query criteria using  * {@link org.apache.lucene.spatial.document.GeoPointField.TermEncoding#PREFIX} method defined by  * {@link GeoPointField}. The terms are then enumerated by the  * {@link GeoPointTermQueryConstantScoreWrapper} and all docs whose GeoPoint fields match the prefix terms or pass  * the {@link GeoPointMultiTermQuery.CellComparator#postFilter} criteria are returned in the  * resulting DocIdSet.  *  *  @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPointPrefixTermsEnum
specifier|final
class|class
name|GeoPointPrefixTermsEnum
extends|extends
name|GeoPointTermsEnum
block|{
DECL|field|start
specifier|private
specifier|final
name|long
name|start
decl_stmt|;
DECL|field|shift
specifier|private
name|short
name|shift
decl_stmt|;
comment|// current range as long
DECL|field|currStart
specifier|private
name|long
name|currStart
decl_stmt|;
DECL|field|currEnd
specifier|private
name|long
name|currEnd
decl_stmt|;
DECL|field|nextRange
specifier|private
specifier|final
name|Range
name|nextRange
init|=
operator|new
name|Range
argument_list|(
operator|-
literal|1
argument_list|,
name|shift
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|hasNext
specifier|private
name|boolean
name|hasNext
init|=
literal|false
decl_stmt|;
DECL|field|withinOnly
specifier|private
name|boolean
name|withinOnly
init|=
literal|false
decl_stmt|;
DECL|field|lastWithin
specifier|private
name|long
name|lastWithin
decl_stmt|;
DECL|method|GeoPointPrefixTermsEnum
specifier|public
name|GeoPointPrefixTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|,
specifier|final
name|GeoPointMultiTermQuery
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|mortonHash
argument_list|(
name|query
operator|.
name|minLon
argument_list|,
name|query
operator|.
name|minLat
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentRange
operator|=
operator|new
name|Range
argument_list|(
literal|0
argument_list|,
name|shift
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// start shift at maxShift value (from computeMaxShift)
name|this
operator|.
name|shift
operator|=
name|maxShift
expr_stmt|;
specifier|final
name|long
name|mask
init|=
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1
decl_stmt|;
name|this
operator|.
name|currStart
operator|=
name|start
operator|&
operator|~
name|mask
expr_stmt|;
name|this
operator|.
name|currEnd
operator|=
name|currStart
operator||
name|mask
expr_stmt|;
block|}
DECL|method|within
specifier|private
name|boolean
name|within
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
name|relationImpl
operator|.
name|cellWithin
argument_list|(
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
DECL|method|boundary
specifier|private
name|boolean
name|boundary
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
name|shift
operator|==
name|maxShift
operator|&&
name|relationImpl
operator|.
name|cellIntersectsShape
argument_list|(
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
DECL|method|nextWithin
specifier|private
name|boolean
name|nextWithin
parameter_list|()
block|{
if|if
condition|(
name|withinOnly
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|currStart
operator|+=
operator|(
literal|1L
operator|<<
name|shift
operator|)
expr_stmt|;
name|setNextRange
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|currentRange
operator|.
name|set
argument_list|(
name|nextRange
argument_list|)
expr_stmt|;
name|hasNext
operator|=
literal|true
expr_stmt|;
name|withinOnly
operator|=
name|lastWithin
operator|!=
name|currStart
expr_stmt|;
if|if
condition|(
name|withinOnly
operator|==
literal|false
condition|)
name|advanceVariables
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|nextRelation
specifier|private
name|void
name|nextRelation
parameter_list|()
block|{
name|double
name|minLon
init|=
name|mortonUnhashLon
argument_list|(
name|currStart
argument_list|)
decl_stmt|;
name|double
name|minLat
init|=
name|mortonUnhashLat
argument_list|(
name|currStart
argument_list|)
decl_stmt|;
name|double
name|maxLon
decl_stmt|;
name|double
name|maxLat
decl_stmt|;
name|boolean
name|isWithin
decl_stmt|;
do|do
block|{
name|maxLon
operator|=
name|mortonUnhashLon
argument_list|(
name|currEnd
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|mortonUnhashLat
argument_list|(
name|currEnd
argument_list|)
expr_stmt|;
comment|// within or a boundary
if|if
condition|(
operator|(
name|isWithin
operator|=
name|within
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|==
literal|true
operator|)
operator|||
name|boundary
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|==
literal|true
condition|)
block|{
specifier|final
name|int
name|m
decl_stmt|;
if|if
condition|(
name|isWithin
operator|==
literal|false
operator|||
operator|(
name|m
operator|=
name|shift
operator|%
name|GeoPointField
operator|.
name|PRECISION_STEP
operator|)
operator|==
literal|0
condition|)
block|{
name|setNextRange
argument_list|(
name|isWithin
operator|==
literal|false
argument_list|)
expr_stmt|;
name|advanceVariables
argument_list|()
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|shift
operator|<
literal|54
condition|)
block|{
name|withinOnly
operator|=
literal|true
expr_stmt|;
name|shift
operator|=
call|(
name|short
call|)
argument_list|(
name|shift
operator|-
name|m
argument_list|)
expr_stmt|;
name|lastWithin
operator|=
name|currEnd
operator|&
operator|~
operator|(
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1
operator|)
expr_stmt|;
name|setNextRange
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// within cell but not at a depth factor of PRECISION_STEP
if|if
condition|(
name|isWithin
operator|==
literal|true
operator|||
operator|(
name|relationImpl
operator|.
name|cellIntersectsMBR
argument_list|(
name|minLon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLat
argument_list|)
operator|==
literal|true
operator|&&
name|shift
operator|!=
name|maxShift
operator|)
condition|)
block|{
comment|// descend: currStart need not change since shift handles end of range
name|currEnd
operator|=
name|currStart
operator||
operator|(
literal|1L
operator|<<
operator|--
name|shift
operator|)
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|advanceVariables
argument_list|()
expr_stmt|;
name|minLon
operator|=
name|mortonUnhashLon
argument_list|(
name|currStart
argument_list|)
expr_stmt|;
name|minLat
operator|=
name|mortonUnhashLat
argument_list|(
name|currStart
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|shift
operator|<
literal|63
condition|)
do|;
block|}
DECL|method|setNextRange
specifier|private
name|void
name|setNextRange
parameter_list|(
specifier|final
name|boolean
name|boundary
parameter_list|)
block|{
name|nextRange
operator|.
name|start
operator|=
name|currStart
expr_stmt|;
name|nextRange
operator|.
name|shift
operator|=
name|shift
expr_stmt|;
name|nextRange
operator|.
name|boundary
operator|=
name|boundary
expr_stmt|;
block|}
DECL|method|advanceVariables
specifier|private
name|void
name|advanceVariables
parameter_list|()
block|{
comment|/** set next variables */
name|long
name|shiftMask
init|=
literal|1L
operator|<<
name|shift
decl_stmt|;
comment|// pop-up if shift bit is set
while|while
condition|(
operator|(
name|currStart
operator|&
name|shiftMask
operator|)
operator|==
name|shiftMask
condition|)
block|{
name|shiftMask
operator|=
literal|1L
operator|<<
operator|++
name|shift
expr_stmt|;
block|}
specifier|final
name|long
name|shiftMOne
init|=
name|shiftMask
operator|-
literal|1
decl_stmt|;
name|currStart
operator|=
name|currStart
operator|&
operator|~
name|shiftMOne
operator||
name|shiftMask
expr_stmt|;
name|currEnd
operator|=
name|currStart
operator||
name|shiftMOne
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|peek
specifier|protected
specifier|final
name|BytesRef
name|peek
parameter_list|()
block|{
name|nextRange
operator|.
name|fillBytesRef
argument_list|(
name|nextSubRangeBRB
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|peek
argument_list|()
return|;
block|}
DECL|method|seek
specifier|protected
name|void
name|seek
parameter_list|(
name|long
name|term
parameter_list|,
name|short
name|res
parameter_list|)
block|{
if|if
condition|(
name|term
operator|<
name|currStart
operator|&&
name|res
operator|<
name|maxShift
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"trying to seek backwards"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|term
operator|==
name|currStart
condition|)
block|{
return|return;
block|}
name|shift
operator|=
name|res
expr_stmt|;
name|currStart
operator|=
name|term
expr_stmt|;
name|currEnd
operator|=
name|currStart
operator||
operator|(
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1
operator|)
expr_stmt|;
name|withinOnly
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextRange
specifier|protected
name|void
name|nextRange
parameter_list|()
block|{
name|hasNext
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|nextRange
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|protected
specifier|final
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|hasNext
operator|==
literal|true
operator|||
name|nextWithin
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|nextRelation
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentRange
operator|.
name|compareTo
argument_list|(
name|nextRange
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|currentRange
operator|.
name|set
argument_list|(
name|nextRange
argument_list|)
expr_stmt|;
return|return
operator|(
name|hasNext
operator|=
literal|true
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|nextSeekTerm
specifier|protected
specifier|final
name|BytesRef
name|nextSeekTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
while|while
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|nextRange
argument_list|()
expr_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
return|return
name|currentCell
return|;
block|}
specifier|final
name|int
name|comparison
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|currentCell
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparison
operator|>
literal|0
condition|)
block|{
name|seek
argument_list|(
name|GeoEncodingUtils
operator|.
name|prefixCodedToGeoCoded
argument_list|(
name|term
argument_list|)
argument_list|,
call|(
name|short
call|)
argument_list|(
literal|64
operator|-
name|GeoEncodingUtils
operator|.
name|getPrefixCodedShift
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
return|return
name|currentCell
return|;
block|}
comment|// no more sub-range enums available
return|return
literal|null
return|;
block|}
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
comment|// range< term or range is null
while|while
condition|(
name|currentCell
operator|==
literal|null
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|currentCell
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// no more ranges, be gone
if|if
condition|(
name|hasNext
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
comment|// peek next range, if the range> term then seek
specifier|final
name|int
name|peekCompare
init|=
name|term
operator|.
name|compareTo
argument_list|(
name|peek
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|peekCompare
operator|<
literal|0
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO_AND_SEEK
return|;
block|}
elseif|else
if|if
condition|(
name|peekCompare
operator|>
literal|0
condition|)
block|{
name|seek
argument_list|(
name|prefixCodedToGeoCoded
argument_list|(
name|term
argument_list|)
argument_list|,
call|(
name|short
call|)
argument_list|(
literal|64
operator|-
name|getPrefixCodedShift
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nextRange
argument_list|()
expr_stmt|;
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
DECL|class|Range
specifier|protected
specifier|final
class|class
name|Range
extends|extends
name|BaseRange
block|{
DECL|method|Range
specifier|public
name|Range
parameter_list|(
specifier|final
name|long
name|start
parameter_list|,
specifier|final
name|short
name|res
parameter_list|,
specifier|final
name|boolean
name|boundary
parameter_list|)
block|{
name|super
argument_list|(
name|start
argument_list|,
name|res
argument_list|,
name|boundary
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillBytesRef
specifier|protected
name|void
name|fillBytesRef
parameter_list|(
name|BytesRefBuilder
name|result
parameter_list|)
block|{
assert|assert
name|result
operator|!=
literal|null
assert|;
name|geoCodedToPrefixCoded
argument_list|(
name|start
argument_list|,
name|shift
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

