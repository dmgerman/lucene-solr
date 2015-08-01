begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Utilities for converting to/from the GeoHash standard  *  * The geohash long format is represented as lon/lat (x/y) interleaved with the 4 least significant bits  * representing the level (1-12) [xyxy...xyxyllll]  *  * This differs from a morton encoded value which interleaves lat/lon (y/x).  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoHashUtils
specifier|public
class|class
name|GeoHashUtils
block|{
DECL|field|BASE_32
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|BASE_32
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|,
literal|'g'
block|,
literal|'h'
block|,
literal|'j'
block|,
literal|'k'
block|,
literal|'m'
block|,
literal|'n'
block|,
literal|'p'
block|,
literal|'q'
block|,
literal|'r'
block|,
literal|'s'
block|,
literal|'t'
block|,
literal|'u'
block|,
literal|'v'
block|,
literal|'w'
block|,
literal|'x'
block|,
literal|'y'
block|,
literal|'z'
block|}
decl_stmt|;
DECL|field|BASE_32_STRING
specifier|public
specifier|static
specifier|final
name|String
name|BASE_32_STRING
init|=
operator|new
name|String
argument_list|(
name|BASE_32
argument_list|)
decl_stmt|;
DECL|field|PRECISION
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION
init|=
literal|12
decl_stmt|;
DECL|field|MORTON_OFFSET
specifier|private
specifier|static
specifier|final
name|short
name|MORTON_OFFSET
init|=
operator|(
name|GeoUtils
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
operator|(
name|PRECISION
operator|*
literal|5
operator|)
decl_stmt|;
comment|/**    * Encode lon/lat to the geohash based long format (lon/lat interleaved, 4 least significant bits = level)    */
DECL|method|longEncode
specifier|public
specifier|static
specifier|final
name|long
name|longEncode
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
comment|// shift to appropriate level
specifier|final
name|short
name|msf
init|=
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
literal|12
operator|-
name|level
operator|)
operator|*
literal|5
operator|)
operator|+
name|MORTON_OFFSET
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|GeoUtils
operator|.
name|mortonHash
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
argument_list|)
operator|>>>
name|msf
operator|)
operator|<<
literal|4
operator|)
operator||
name|level
return|;
block|}
comment|/**    * Encode from geohash string to the geohash based long format (lon/lat interleaved, 4 least significant bits = level)    */
DECL|method|longEncode
specifier|public
specifier|static
specifier|final
name|long
name|longEncode
parameter_list|(
specifier|final
name|String
name|hash
parameter_list|)
block|{
name|int
name|level
init|=
name|hash
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
name|long
name|b
decl_stmt|;
name|long
name|l
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|hash
operator|.
name|toCharArray
argument_list|()
control|)
block|{
name|b
operator|=
call|(
name|long
call|)
argument_list|(
name|BASE_32_STRING
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator||=
operator|(
name|b
operator|<<
operator|(
name|level
operator|--
operator|*
literal|5
operator|)
operator|)
expr_stmt|;
block|}
return|return
operator|(
name|l
operator|<<
literal|4
operator|)
operator||
name|hash
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Encode to a geohash string from the geohash based long format    */
DECL|method|stringEncode
specifier|public
specifier|static
specifier|final
name|String
name|stringEncode
parameter_list|(
name|long
name|geoHashLong
parameter_list|)
block|{
name|int
name|level
init|=
operator|(
name|int
operator|)
name|geoHashLong
operator|&
literal|15
decl_stmt|;
name|geoHashLong
operator|>>>=
literal|4
expr_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|level
index|]
decl_stmt|;
do|do
block|{
name|chars
index|[
operator|--
name|level
index|]
operator|=
name|BASE_32
index|[
call|(
name|int
call|)
argument_list|(
name|geoHashLong
operator|&
literal|31L
argument_list|)
index|]
expr_stmt|;
name|geoHashLong
operator|>>>=
literal|5
expr_stmt|;
block|}
do|while
condition|(
name|level
operator|>
literal|0
condition|)
do|;
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|)
return|;
block|}
comment|/**    * Encode to a geohash string from full resolution longitude, latitude)    */
DECL|method|stringEncode
specifier|public
specifier|static
specifier|final
name|String
name|stringEncode
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|)
block|{
return|return
name|stringEncode
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
literal|12
argument_list|)
return|;
block|}
comment|/**    * Encode to a level specific geohash string from full resolution longitude, latitude    */
DECL|method|stringEncode
specifier|public
specifier|static
specifier|final
name|String
name|stringEncode
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
comment|// bit twiddle to geohash (since geohash is a swapped (lon/lat) encoding)
specifier|final
name|long
name|hashedVal
init|=
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|GeoUtils
operator|.
name|mortonHash
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|geoHash
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|short
name|precision
init|=
literal|0
decl_stmt|;
specifier|final
name|short
name|msf
init|=
operator|(
name|GeoUtils
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
literal|5
decl_stmt|;
name|long
name|mask
init|=
literal|31L
operator|<<
name|msf
decl_stmt|;
do|do
block|{
name|geoHash
operator|.
name|append
argument_list|(
name|BASE_32
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|mask
operator|&
name|hashedVal
operator|)
operator|>>>
operator|(
name|msf
operator|-
operator|(
name|precision
operator|*
literal|5
operator|)
operator|)
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// next 5 bits
name|mask
operator|>>>=
literal|5
expr_stmt|;
block|}
do|while
condition|(
operator|++
name|precision
operator|<
name|level
condition|)
do|;
return|return
name|geoHash
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Encode to a full precision geohash string from a given morton encoded long value    */
DECL|method|stringEncodeFromMortonLong
specifier|public
specifier|static
specifier|final
name|String
name|stringEncodeFromMortonLong
parameter_list|(
specifier|final
name|long
name|hashedVal
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|stringEncode
argument_list|(
name|hashedVal
argument_list|,
name|PRECISION
argument_list|)
return|;
block|}
comment|/**    * Encode to a geohash string at a given level from a morton long    */
DECL|method|stringEncodeFromMortonLong
specifier|public
specifier|static
specifier|final
name|String
name|stringEncodeFromMortonLong
parameter_list|(
name|long
name|hashedVal
parameter_list|,
specifier|final
name|int
name|level
parameter_list|)
block|{
comment|// bit twiddle to geohash (since geohash is a swapped (lon/lat) encoding)
name|hashedVal
operator|=
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|hashedVal
argument_list|)
expr_stmt|;
name|StringBuilder
name|geoHash
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|short
name|precision
init|=
literal|0
decl_stmt|;
specifier|final
name|short
name|msf
init|=
operator|(
name|GeoUtils
operator|.
name|BITS
operator|<<
literal|1
operator|)
operator|-
literal|5
decl_stmt|;
name|long
name|mask
init|=
literal|31L
operator|<<
name|msf
decl_stmt|;
do|do
block|{
name|geoHash
operator|.
name|append
argument_list|(
name|BASE_32
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|mask
operator|&
name|hashedVal
operator|)
operator|>>>
operator|(
name|msf
operator|-
operator|(
name|precision
operator|*
literal|5
operator|)
operator|)
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// next 5 bits
name|mask
operator|>>>=
literal|5
expr_stmt|;
block|}
do|while
condition|(
operator|++
name|precision
operator|<
name|level
condition|)
do|;
return|return
name|geoHash
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Encode to a morton long value from a given geohash string    */
DECL|method|mortonEncode
specifier|public
specifier|static
specifier|final
name|long
name|mortonEncode
parameter_list|(
specifier|final
name|String
name|hash
parameter_list|)
block|{
name|int
name|level
init|=
literal|11
decl_stmt|;
name|long
name|b
decl_stmt|;
name|long
name|l
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|hash
operator|.
name|toCharArray
argument_list|()
control|)
block|{
name|b
operator|=
call|(
name|long
call|)
argument_list|(
name|BASE_32_STRING
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator||=
operator|(
name|b
operator|<<
operator|(
operator|(
name|level
operator|--
operator|*
literal|5
operator|)
operator|+
name|MORTON_OFFSET
operator|)
operator|)
expr_stmt|;
block|}
return|return
name|BitUtil
operator|.
name|flipFlop
argument_list|(
name|l
argument_list|)
return|;
block|}
comment|/**    * Encode to a morton long value from a given geohash long value    */
DECL|method|mortonEncode
specifier|public
specifier|static
specifier|final
name|long
name|mortonEncode
parameter_list|(
specifier|final
name|long
name|geoHashLong
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
call|(
name|int
call|)
argument_list|(
name|geoHashLong
operator|&
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|short
name|odd
init|=
call|(
name|short
call|)
argument_list|(
name|level
operator|&
literal|1
argument_list|)
decl_stmt|;
return|return
name|BitUtil
operator|.
name|flipFlop
argument_list|(
operator|(
name|geoHashLong
operator|>>>
literal|4
operator|)
operator|<<
name|odd
argument_list|)
operator|<<
operator|(
operator|(
operator|(
literal|12
operator|-
name|level
operator|)
operator|*
literal|5
operator|)
operator|+
operator|(
name|MORTON_OFFSET
operator|-
name|odd
operator|)
operator|)
return|;
block|}
block|}
end_class

end_unit

