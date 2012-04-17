begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Exception indicating that a certain operation could not be performed   * on a taxonomy related object because of an inconsistency.  *<p>  * For example, trying to refresh a taxonomy reader might fail in case   * the underlying taxonomy was meanwhile modified in a manner which   * does not allow to perform such a refresh. (See {@link TaxonomyReader#refresh()}.)  *     * @lucene.experimental  */
end_comment

begin_class
DECL|class|InconsistentTaxonomyException
specifier|public
class|class
name|InconsistentTaxonomyException
extends|extends
name|Exception
block|{
DECL|method|InconsistentTaxonomyException
specifier|public
name|InconsistentTaxonomyException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|InconsistentTaxonomyException
specifier|public
name|InconsistentTaxonomyException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

