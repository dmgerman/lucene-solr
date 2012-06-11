begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Document IDs with scores for each, driving facets accumulation. Document  * scores are optionally used in the process of facets scoring.  *   * @see FacetsAccumulator#accumulate(ScoredDocIDs)  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|ScoredDocIDs
specifier|public
interface|interface
name|ScoredDocIDs
block|{
comment|/** Returns an iterator over the document IDs and their scores. */
DECL|method|iterator
specifier|public
name|ScoredDocIDsIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the set of doc IDs. */
DECL|method|getDocIDs
specifier|public
name|DocIdSet
name|getDocIDs
parameter_list|()
function_decl|;
comment|/** Returns the number of scored documents. */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

