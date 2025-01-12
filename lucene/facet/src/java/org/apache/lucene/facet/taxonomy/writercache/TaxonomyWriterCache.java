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
name|directory
operator|.
name|DirectoryTaxonomyWriter
import|;
end_import

begin_comment
comment|/**  * TaxonomyWriterCache is a relatively simple interface for a cache of  * category-&gt;ordinal mappings, used in TaxonomyWriter implementations (such as  * {@link DirectoryTaxonomyWriter}).  *<p>  * It basically has put() methods for adding a mapping, and get() for looking a  * mapping up the cache. The cache does<B>not</B> guarantee to hold everything  * that has been put into it, and might in fact selectively delete some of the  * mappings (e.g., the ones least recently used). This means that if get()  * returns a negative response, it does not necessarily mean that the category  * doesn't exist - just that it is not in the cache. The caller can only infer  * that the category doesn't exist if it knows the cache to be complete (because  * all the categories were loaded into the cache, and since then no put()  * returned true).  *<p>  * However, if it does so, it should clear out large parts of the cache at once,  * because the user will typically need to work hard to recover from every cache  * cleanup (see {@link #put(FacetLabel, int)}'s return value).  *<p>  *<b>NOTE:</b> the cache may be accessed concurrently by multiple threads,  * therefore cache implementations should take this into consideration.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|TaxonomyWriterCache
specifier|public
interface|interface
name|TaxonomyWriterCache
block|{
comment|/**    * Let go of whatever resources the cache is holding. After a close(),    * this object can no longer be used.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Lookup a category in the cache, returning its ordinal, or a negative    * number if the category is not in the cache.    *<P>    * It is up to the caller to remember what a negative response means:    * If the caller knows the cache is<I>complete</I> (it was initially    * fed with all the categories, and since then put() never returned true)    * it means the category does not exist. Otherwise, the category might    * still exist, but just be missing from the cache.    */
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|FacetLabel
name|categoryPath
parameter_list|)
function_decl|;
comment|/**    * Add a category to the cache, with the given ordinal as the value.    *<P>    * If the implementation keeps only a partial cache (e.g., an LRU cache)    * and finds that its cache is full, it should clear up part of the cache    * and return<code>true</code>. Otherwise, it should return    *<code>false</code>.    *<P>    * The reason why the caller needs to know if part of the cache was    * cleared is that in that case it will have to commit its on-disk index    * (so that all the latest category additions can be searched on disk, if    * we can't rely on the cache to contain them).    *<P>    * Ordinals should be non-negative. Currently there is no defined way to    * specify that a cache should remember a category does NOT exist.    * It doesn't really matter, because normally the next thing we do after    * finding that a category does not exist is to add it.    */
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|FacetLabel
name|categoryPath
parameter_list|,
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/**    * Returns true if the cache is full, such that the next {@link #put} will    * evict entries from it, false otherwise.    */
DECL|method|isFull
specifier|public
name|boolean
name|isFull
parameter_list|()
function_decl|;
comment|/**    * Clears the content of the cache. Unlike {@link #close()}, the caller can    * assume that the cache is still operable after this method returns.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

