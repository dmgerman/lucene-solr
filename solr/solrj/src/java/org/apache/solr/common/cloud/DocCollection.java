begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
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
name|noggit
operator|.
name|JSONUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/**  * Models a Collection in zookeeper (but that Java name is obviously taken, hence "DocCollection")  */
end_comment

begin_class
DECL|class|DocCollection
specifier|public
class|class
name|DocCollection
extends|extends
name|ZkNodeProps
block|{
DECL|field|DOC_ROUTER
specifier|public
specifier|static
specifier|final
name|String
name|DOC_ROUTER
init|=
literal|"router"
decl_stmt|;
DECL|field|SHARDS
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS
init|=
literal|"shards"
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|slices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
decl_stmt|;
DECL|field|allSlices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|allSlices
decl_stmt|;
DECL|field|router
specifier|private
specifier|final
name|DocRouter
name|router
decl_stmt|;
comment|/**    * @param name  The name of the collection    * @param slices The logical shards of the collection.  This is used directly and a copy is not made.    * @param props  The properties of the slice.  This is used directly and a copy is not made.    */
DECL|method|DocCollection
specifier|public
name|DocCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|DocRouter
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|props
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
operator|:
name|props
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|allSlices
operator|=
name|slices
expr_stmt|;
name|this
operator|.
name|slices
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|iter
init|=
name|slices
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slice
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|slice
operator|.
name|getValue
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|Slice
operator|.
name|ACTIVE
argument_list|)
condition|)
name|this
operator|.
name|slices
operator|.
name|put
argument_list|(
name|slice
operator|.
name|getKey
argument_list|()
argument_list|,
name|slice
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
assert|assert
name|name
operator|!=
literal|null
operator|&&
name|slices
operator|!=
literal|null
assert|;
block|}
comment|/**    * Return collection name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getSlice
specifier|public
name|Slice
name|getSlice
parameter_list|(
name|String
name|sliceName
parameter_list|)
block|{
return|return
name|allSlices
operator|.
name|get
argument_list|(
name|sliceName
argument_list|)
return|;
block|}
comment|/**    * Gets the list of active slices for this collection.    */
DECL|method|getSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSlices
parameter_list|()
block|{
return|return
name|slices
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Return the list of all slices for this collection.    */
DECL|method|getAllSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getAllSlices
parameter_list|()
block|{
return|return
name|allSlices
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Get the map of active slices (sliceName->Slice) for this collection.    */
DECL|method|getSlicesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getSlicesMap
parameter_list|()
block|{
return|return
name|slices
return|;
block|}
comment|/**    * Get the map of all slices (sliceName->Slice) for this collection.    */
DECL|method|getAllSlicesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getAllSlicesMap
parameter_list|()
block|{
return|return
name|allSlices
return|;
block|}
DECL|method|getRouter
specifier|public
name|DocRouter
name|getRouter
parameter_list|()
block|{
return|return
name|router
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
literal|"DocCollection("
operator|+
name|name
operator|+
literal|")="
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|jsonWriter
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|all
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|allSlices
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|all
operator|.
name|putAll
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
name|all
operator|.
name|put
argument_list|(
name|SHARDS
argument_list|,
name|allSlices
argument_list|)
expr_stmt|;
name|jsonWriter
operator|.
name|write
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

