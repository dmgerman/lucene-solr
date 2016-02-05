begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
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
name|facet
operator|.
name|taxonomy
operator|.
name|FacetLabel
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
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|TaxonomyWriterCache
import|;
end_import

begin_comment
comment|/**  * LRU {@link TaxonomyWriterCache} - good choice for huge taxonomies.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|LruTaxonomyWriterCache
specifier|public
class|class
name|LruTaxonomyWriterCache
implements|implements
name|TaxonomyWriterCache
block|{
comment|/**    * Determines cache type.    * For guaranteed correctness - not relying on no-collisions in the hash    * function, LRU_STRING should be used.    */
DECL|enum|LRUType
specifier|public
enum|enum
name|LRUType
block|{
comment|/** Use the label's hash as the key; this can lead to      *  silent conflicts! */
DECL|enum constant|LRU_HASHED
name|LRU_HASHED
block|,
comment|/** Use the label as the hash key; this is always      *  correct but will usually use more RAM. */
DECL|enum constant|LRU_STRING
name|LRU_STRING
block|}
DECL|field|cache
specifier|private
name|NameIntCacheLRU
name|cache
decl_stmt|;
comment|/** Creates this with {@link LRUType#LRU_HASHED} method. */
DECL|method|LruTaxonomyWriterCache
specifier|public
name|LruTaxonomyWriterCache
parameter_list|(
name|int
name|cacheSize
parameter_list|)
block|{
comment|// TODO (Facet): choose between NameHashIntCacheLRU and NameIntCacheLRU.
comment|// For guaranteed correctness - not relying on no-collisions in the hash
comment|// function, NameIntCacheLRU should be used:
comment|// On the other hand, NameHashIntCacheLRU takes less RAM but if there
comment|// are collisions (which we never found) two different paths would be
comment|// mapped to the same ordinal...
name|this
argument_list|(
name|cacheSize
argument_list|,
name|LRUType
operator|.
name|LRU_HASHED
argument_list|)
expr_stmt|;
block|}
comment|/** Creates this with the specified method. */
DECL|method|LruTaxonomyWriterCache
specifier|public
name|LruTaxonomyWriterCache
parameter_list|(
name|int
name|cacheSize
parameter_list|,
name|LRUType
name|lruType
parameter_list|)
block|{
comment|// TODO (Facet): choose between NameHashIntCacheLRU and NameIntCacheLRU.
comment|// For guaranteed correctness - not relying on no-collisions in the hash
comment|// function, NameIntCacheLRU should be used:
comment|// On the other hand, NameHashIntCacheLRU takes less RAM but if there
comment|// are collisions (which we never found) two different paths would be
comment|// mapped to the same ordinal...
if|if
condition|(
name|lruType
operator|==
name|LRUType
operator|.
name|LRU_HASHED
condition|)
block|{
name|this
operator|.
name|cache
operator|=
operator|new
name|NameHashIntCacheLRU
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|cache
operator|=
operator|new
name|NameIntCacheLRU
argument_list|(
name|cacheSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isFull
specifier|public
specifier|synchronized
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|cache
operator|.
name|getSize
argument_list|()
operator|==
name|cache
operator|.
name|getMaxSize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cache
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|synchronized
name|int
name|get
parameter_list|(
name|FacetLabel
name|categoryPath
parameter_list|)
block|{
name|Integer
name|res
init|=
name|cache
operator|.
name|get
argument_list|(
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|res
operator|.
name|intValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
specifier|synchronized
name|boolean
name|put
parameter_list|(
name|FacetLabel
name|categoryPath
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|boolean
name|ret
init|=
name|cache
operator|.
name|put
argument_list|(
name|categoryPath
argument_list|,
operator|new
name|Integer
argument_list|(
name|ordinal
argument_list|)
argument_list|)
decl_stmt|;
comment|// If the cache is full, we need to clear one or more old entries
comment|// from the cache. However, if we delete from the cache a recent
comment|// addition that isn't yet in our reader, for this entry to be
comment|// visible to us we need to make sure that the changes have been
comment|// committed and we reopen the reader. Because this is a slow
comment|// operation, we don't delete entries one-by-one but rather in bulk
comment|// (put() removes the 2/3rd oldest entries).
if|if
condition|(
name|ret
condition|)
block|{
name|cache
operator|.
name|makeRoomLRU
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

