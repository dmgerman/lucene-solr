begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.params
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
name|params
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
name|FacetException
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Thrown when the facets params are missing a property. *  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|FacetParamsMissingPropertyException
specifier|public
class|class
name|FacetParamsMissingPropertyException
extends|extends
name|FacetException
block|{
DECL|method|FacetParamsMissingPropertyException
specifier|public
name|FacetParamsMissingPropertyException
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|super
argument_list|(
literal|"Property with key \""
operator|+
name|key
operator|+
literal|"\" not found"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

