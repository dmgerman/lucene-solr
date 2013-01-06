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
name|DrillDownStream
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
name|CategoryPath
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Determines which {@link CategoryPath categories} should be added as terms to  * the {@link DrillDownStream}. The default approach is implemented by  * {@link #ALL_CATEGORIES}.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|PathPolicy
specifier|public
interface|interface
name|PathPolicy
extends|extends
name|Serializable
block|{
comment|/**    * A {@link PathPolicy} which adds all {@link CategoryPath} that have at least    * one component (i.e. {@link CategoryPath#length}&gt; 0) to the categories    * stream.    */
DECL|field|ALL_CATEGORIES
specifier|public
specifier|static
specifier|final
name|PathPolicy
name|ALL_CATEGORIES
init|=
operator|new
name|PathPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldAdd
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|)
block|{
return|return
name|categoryPath
operator|.
name|length
operator|>
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Check whether a given category path should be added to the stream.    *     * @param categoryPath    *            A given category path which is to be tested for stream    *            addition.    * @return<code>true</code> if the category path should be added.    *<code>false</code> otherwise.    */
DECL|method|shouldAdd
specifier|public
specifier|abstract
name|boolean
name|shouldAdd
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

