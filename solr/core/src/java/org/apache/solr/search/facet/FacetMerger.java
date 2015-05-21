begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The FacetMerger code is in the prototype stage, and this is the reason that
end_comment

begin_comment
comment|// many implementations are all in this file.  They can be moved to separate
end_comment

begin_comment
comment|// files after the interfaces are locked down more.
end_comment

begin_comment
comment|//
end_comment

begin_class
DECL|class|FacetMerger
specifier|public
specifier|abstract
class|class
name|FacetMerger
block|{
DECL|method|merge
specifier|public
specifier|abstract
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|,
name|Context
name|mcontext
parameter_list|)
function_decl|;
DECL|method|getMergedResult
specifier|public
specifier|abstract
name|Object
name|getMergedResult
parameter_list|()
function_decl|;
DECL|class|Context
specifier|public
specifier|static
class|class
name|Context
block|{
comment|// FacetComponentState state;  // todo: is this needed?
DECL|field|root
name|Object
name|root
decl_stmt|;
block|}
block|}
end_class

end_unit

