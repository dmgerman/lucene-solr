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
name|TaxonomyReader
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This class filters our the ROOT category path. For more information see  * {@link PathPolicy}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|DefaultPathPolicy
specifier|public
class|class
name|DefaultPathPolicy
implements|implements
name|PathPolicy
block|{
comment|/**    * Filters out (returns false) CategoryPaths equal or less than    * {@link TaxonomyReader#ROOT_ORDINAL}. true otherwise.    */
DECL|method|shouldAdd
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
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

