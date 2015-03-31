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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Query
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
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_class
DECL|class|FacetQuery
specifier|public
class|class
name|FacetQuery
extends|extends
name|FacetRequest
block|{
comment|// query string or query?
DECL|field|q
name|Query
name|q
decl_stmt|;
annotation|@
name|Override
DECL|method|createFacetProcessor
specifier|public
name|FacetProcessor
name|createFacetProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|)
block|{
return|return
operator|new
name|FacetQueryProcessor
argument_list|(
name|fcontext
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createFacetMerger
specifier|public
name|FacetMerger
name|createFacetMerger
parameter_list|(
name|Object
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|FacetQueryMerger
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|FacetQueryProcessor
class|class
name|FacetQueryProcessor
extends|extends
name|FacetProcessor
argument_list|<
name|FacetQuery
argument_list|>
block|{
DECL|method|FacetQueryProcessor
name|FacetQueryProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetQuery
name|freq
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResponse
specifier|public
name|Object
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|()
throws|throws
name|IOException
block|{
name|response
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|fillBucket
argument_list|(
name|response
argument_list|,
name|freq
operator|.
name|q
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

