begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.categorypolicy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|categorypolicy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|index
operator|.
name|streaming
operator|.
name|CategoryParentsStream
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
name|TaxonomyWriter
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Filtering category ordinals in {@link CategoryParentsStream}, where a given  * category ordinal is added to the stream, and than its parents are being added  * one after the other using {@link TaxonomyWriter#getParent(int)}.<br>  * That loop should have a stop point - the default approach (excluding the  * ROOT) is implemented in {@link DefaultOrdinalPolicy}.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|OrdinalPolicy
specifier|public
interface|interface
name|OrdinalPolicy
extends|extends
name|Serializable
block|{
comment|/**    * Check whether a given category ordinal should be added to the stream.    *     * @param ordinal    *            A given category ordinal which is to be tested for stream    *            addition.    * @return<code>true</code> if the category should be added.    *<code>false</code> otherwise.    */
DECL|method|shouldAdd
specifier|public
specifier|abstract
name|boolean
name|shouldAdd
parameter_list|(
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/**    * Initialize the policy with a TaxonomyWriter. This method can be    * implemented as noop if the ordinal policy is not taxonomy dependent    *     * @param taxonomyWriter    *            A relevant taxonomyWriter object, with which ordinals sent to    *            {@link #shouldAdd(int)} are examined.    */
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

