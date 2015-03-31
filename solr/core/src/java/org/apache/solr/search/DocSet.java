begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_comment
comment|/**  *<code>DocSet</code> represents an unordered set of Lucene Document Ids.  *  *<p>  * WARNING: Any DocSet returned from SolrIndexSearcher should<b>not</b> be modified as it may have been retrieved from  * a cache and could be shared.  *</p>  *  * @since solr 0.9  */
end_comment

begin_interface
DECL|interface|DocSet
specifier|public
interface|interface
name|DocSet
extends|extends
name|Closeable
comment|/* extends Collection<Integer> */
block|{
comment|/**    * Adds the specified document if it is not currently in the DocSet    * (optional operation).    *    * @see #addUnique    * @throws SolrException if the implementation does not allow modifications    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**    * Adds a document the caller knows is not currently in the DocSet    * (optional operation).    *    *<p>    * This method may be faster then<code>add(doc)</code> in some    * implementaions provided the caller is certain of the precondition.    *</p>    *    * @see #add    * @throws SolrException if the implementation does not allow modifications    */
DECL|method|addUnique
specifier|public
name|void
name|addUnique
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**    * Returns the number of documents in the set.    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Returns true if a document is in the DocSet.    */
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|docid
parameter_list|)
function_decl|;
comment|/**    * Returns an iterator that may be used to iterate over all of the documents in the set.    *    *<p>    * The order of the documents returned by this iterator is    * non-deterministic, and any scoring information is meaningless    *</p>    */
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Returns the approximate amount of memory taken by this DocSet.    * This is only an approximation and doesn't take into account java object overhead.    *    * @return    * the approximate memory consumption in bytes    */
DECL|method|memSize
specifier|public
name|long
name|memSize
parameter_list|()
function_decl|;
comment|/**    * Returns the intersection of this set with another set.  Neither set is modified - a new DocSet is    * created and returned.    * @return a DocSet representing the intersection    */
DECL|method|intersection
specifier|public
name|DocSet
name|intersection
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the number of documents of the intersection of this set with another set.    * May be more efficient than actually creating the intersection and then getting its size.    */
DECL|method|intersectionSize
specifier|public
name|int
name|intersectionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/** Returns true if these sets have any elements in common */
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the union of this set with another set.  Neither set is modified - a new DocSet is    * created and returned.    * @return a DocSet representing the union    */
DECL|method|union
specifier|public
name|DocSet
name|union
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the number of documents of the union of this set with another set.    * May be more efficient than actually creating the union and then getting its size.    */
DECL|method|unionSize
specifier|public
name|int
name|unionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the documents in this set that are not in the other set. Neither set is modified - a new DocSet is    * created and returned.    * @return a DocSet representing this AND NOT other    */
DECL|method|andNot
specifier|public
name|DocSet
name|andNot
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the number of documents in this set that are not in the other set.    */
DECL|method|andNotSize
specifier|public
name|int
name|andNotSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns a Filter for use in Lucene search methods, assuming this DocSet    * was generated from the top-level MultiReader that the Lucene search    * methods will be invoked with.    */
DECL|method|getTopFilter
specifier|public
name|Filter
name|getTopFilter
parameter_list|()
function_decl|;
comment|/**    * Adds all the docs from this set to the target set. The target should be    * sized large enough to accommodate all of the documents before calling this    * method.    */
DECL|method|addAllTo
specifier|public
name|void
name|addAllTo
parameter_list|(
name|DocSet
name|target
parameter_list|)
function_decl|;
DECL|field|EMPTY
specifier|public
specifier|static
name|DocSet
name|EMPTY
init|=
operator|new
name|SortedIntDocSet
argument_list|(
operator|new
name|int
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

