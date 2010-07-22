begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.geohash
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geohash
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
name|FieldCache
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
name|FieldCache
operator|.
name|DocTerms
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
name|Filter
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
name|DocIdSet
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
name|FilteredDocIdSet
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
name|DistanceUtils
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
name|spatial
operator|.
name|tier
operator|.
name|DistanceFilter
import|;
end_import

begin_comment
comment|/**<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment

begin_class
DECL|class|GeoHashDistanceFilter
specifier|public
class|class
name|GeoHashDistanceFilter
extends|extends
name|DistanceFilter
block|{
comment|/**    *     */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|lat
specifier|private
name|double
name|lat
decl_stmt|;
DECL|field|lng
specifier|private
name|double
name|lng
decl_stmt|;
DECL|field|geoHashField
specifier|private
name|String
name|geoHashField
decl_stmt|;
comment|/**    * Provide a distance filter based from a center point with a radius    * in miles    * @param startingFilter    * @param lat    * @param lng    * @param miles    */
DECL|method|GeoHashDistanceFilter
specifier|public
name|GeoHashDistanceFilter
parameter_list|(
name|Filter
name|startingFilter
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lng
parameter_list|,
name|double
name|miles
parameter_list|,
name|String
name|geoHashField
parameter_list|)
block|{
name|super
argument_list|(
name|startingFilter
argument_list|,
name|miles
argument_list|)
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lng
operator|=
name|lng
expr_stmt|;
name|this
operator|.
name|geoHashField
operator|=
name|geoHashField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocTerms
name|geoHashValues
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|geoHashField
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docBase
init|=
name|nextDocBase
decl_stmt|;
name|nextDocBase
operator|+=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
return|return
operator|new
name|FilteredDocIdSet
argument_list|(
name|startingFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|match
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// TODO: cutover to BytesRef so we don't have to
comment|// make String here
name|String
name|geoHash
init|=
name|geoHashValues
operator|.
name|getTerm
argument_list|(
name|doc
argument_list|,
name|br
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|double
index|[]
name|coords
init|=
name|GeoHashUtils
operator|.
name|decode
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|double
name|x
init|=
name|coords
index|[
literal|0
index|]
decl_stmt|;
name|double
name|y
init|=
name|coords
index|[
literal|1
index|]
decl_stmt|;
comment|// round off lat / longs if necessary
comment|//      x = DistanceHandler.getPrecision(x, precise);
comment|//      y = DistanceHandler.getPrecision(y, precise);
name|Double
name|cachedDistance
init|=
name|distanceLookupCache
operator|.
name|get
argument_list|(
name|geoHash
argument_list|)
decl_stmt|;
name|double
name|d
decl_stmt|;
if|if
condition|(
name|cachedDistance
operator|!=
literal|null
condition|)
block|{
name|d
operator|=
name|cachedDistance
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|d
operator|=
name|DistanceUtils
operator|.
name|getDistanceMi
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
expr_stmt|;
name|distanceLookupCache
operator|.
name|put
argument_list|(
name|geoHash
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|d
operator|<
name|distance
condition|)
block|{
name|distances
operator|.
name|put
argument_list|(
name|doc
operator|+
name|docBase
argument_list|,
name|d
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
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
name|GeoHashDistanceFilter
operator|)
condition|)
return|return
literal|false
return|;
name|GeoHashDistanceFilter
name|other
init|=
operator|(
name|GeoHashDistanceFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|startingFilter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|startingFilter
argument_list|)
operator|||
name|this
operator|.
name|distance
operator|!=
name|other
operator|.
name|distance
operator|||
name|this
operator|.
name|lat
operator|!=
name|other
operator|.
name|lat
operator|||
name|this
operator|.
name|lng
operator|!=
name|other
operator|.
name|lng
operator|||
operator|!
name|this
operator|.
name|geoHashField
operator|.
name|equals
argument_list|(
name|other
operator|.
name|geoHashField
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Returns a hash code value for this object.*/
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
name|Double
operator|.
name|valueOf
argument_list|(
name|distance
argument_list|)
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
name|startingFilter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
name|Double
operator|.
name|valueOf
argument_list|(
name|lat
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
name|Double
operator|.
name|valueOf
argument_list|(
name|lng
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|^=
name|geoHashField
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

