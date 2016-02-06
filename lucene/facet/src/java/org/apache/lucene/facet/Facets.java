begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Common base class for all facets implementations.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|Facets
specifier|public
specifier|abstract
class|class
name|Facets
block|{
comment|/** Default constructor. */
DECL|method|Facets
specifier|public
name|Facets
parameter_list|()
block|{   }
comment|/** Returns the topN child labels under the specified    *  path.  Returns null if the specified path doesn't    *  exist or if this dimension was never seen. */
DECL|method|getTopChildren
specifier|public
specifier|abstract
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Return the count or value    *  for a specific path.  Returns -1 if    *  this path doesn't exist, else the count. */
DECL|method|getSpecificValue
specifier|public
specifier|abstract
name|Number
name|getSpecificValue
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns topN labels for any dimension that had hits,    *  sorted by the number of hits that dimension matched;    *  this is used for "sparse" faceting, where many    *  different dimensions were indexed, for example    *  depending on the type of document. */
DECL|method|getAllDims
specifier|public
specifier|abstract
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getAllDims
parameter_list|(
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

