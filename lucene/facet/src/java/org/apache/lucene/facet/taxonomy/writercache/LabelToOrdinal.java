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

begin_comment
comment|/**  * Abstract class for storing Label-&gt;Ordinal mappings in a taxonomy.   *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|LabelToOrdinal
specifier|public
specifier|abstract
class|class
name|LabelToOrdinal
block|{
comment|/** How many ordinals we've seen. */
DECL|field|counter
specifier|protected
name|int
name|counter
decl_stmt|;
comment|/** Returned by {@link #getOrdinal} when the label isn't    *  recognized. */
DECL|field|INVALID_ORDINAL
specifier|public
specifier|static
specifier|final
name|int
name|INVALID_ORDINAL
init|=
operator|-
literal|2
decl_stmt|;
comment|/** Default constructor. */
DECL|method|LabelToOrdinal
specifier|public
name|LabelToOrdinal
parameter_list|()
block|{   }
comment|/**    * return the maximal Ordinal assigned so far    */
DECL|method|getMaxOrdinal
specifier|public
name|int
name|getMaxOrdinal
parameter_list|()
block|{
return|return
name|this
operator|.
name|counter
return|;
block|}
comment|/**    * Returns the next unassigned ordinal. The default behavior of this method    * is to simply increment a counter.    */
DECL|method|getNextOrdinal
specifier|public
name|int
name|getNextOrdinal
parameter_list|()
block|{
return|return
name|this
operator|.
name|counter
operator|++
return|;
block|}
comment|/**    * Adds a new label if its not yet in the table.    * Throws an {@link IllegalArgumentException} if the same label with    * a different ordinal was previoulsy added to this table.    */
DECL|method|addLabel
specifier|public
specifier|abstract
name|void
name|addLabel
parameter_list|(
name|FacetLabel
name|label
parameter_list|,
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/**    * Returns the ordinal assigned to the given label,     * or {@link #INVALID_ORDINAL} if the label cannot be found in this table.    */
DECL|method|getOrdinal
specifier|public
specifier|abstract
name|int
name|getOrdinal
parameter_list|(
name|FacetLabel
name|label
parameter_list|)
function_decl|;
block|}
end_class

end_unit

