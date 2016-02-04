begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy
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
package|;
end_package

begin_comment
comment|/**  * Returns 3 arrays for traversing the taxonomy:  *<ul>  *<li>{@code parents}: {@code parents[i]} denotes the parent of category  * ordinal {@code i}.</li>  *<li>{@code children}: {@code children[i]} denotes a child of category ordinal  * {@code i}.</li>  *<li>{@code siblings}: {@code siblings[i]} denotes the sibling of category  * ordinal {@code i}.</li>  *</ul>  *   * To traverse the taxonomy tree, you typically start with {@code children[0]}  * (ordinal 0 is reserved for ROOT), and then depends if you want to do DFS or  * BFS, you call {@code children[children[0]]} or {@code siblings[children[0]]}  * and so forth, respectively.  *   *<p>  *<b>NOTE:</b> you are not expected to modify the values of the arrays, since  * the arrays are shared with other threads.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|ParallelTaxonomyArrays
specifier|public
specifier|abstract
class|class
name|ParallelTaxonomyArrays
block|{
comment|/** Sole constructor. */
DECL|method|ParallelTaxonomyArrays
specifier|public
name|ParallelTaxonomyArrays
parameter_list|()
block|{   }
comment|/**    * Returns the parents array, where {@code parents[i]} denotes the parent of    * category ordinal {@code i}.    */
DECL|method|parents
specifier|public
specifier|abstract
name|int
index|[]
name|parents
parameter_list|()
function_decl|;
comment|/**    * Returns the children array, where {@code children[i]} denotes a child of    * category ordinal {@code i}.    */
DECL|method|children
specifier|public
specifier|abstract
name|int
index|[]
name|children
parameter_list|()
function_decl|;
comment|/**    * Returns the siblings array, where {@code siblings[i]} denotes the sibling    * of category ordinal {@code i}.    */
DECL|method|siblings
specifier|public
specifier|abstract
name|int
index|[]
name|siblings
parameter_list|()
function_decl|;
block|}
end_class

end_unit

