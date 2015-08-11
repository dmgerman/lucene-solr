begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermContext
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
name|CollectionStatistics
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
name|lucene
operator|.
name|search
operator|.
name|TermStatistics
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
name|Weight
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_comment
comment|/**  * The purpose of this class is only to provide two pieces of information  * necessary to create {@link Weight} from a {@link Query}, that is  * {@link TermStatistics} for a term and {@link CollectionStatistics} for the  * whole collection.  */
end_comment

begin_class
DECL|class|StatsSource
specifier|public
specifier|abstract
class|class
name|StatsSource
block|{
DECL|method|termStatistics
specifier|public
specifier|abstract
name|TermStatistics
name|termStatistics
parameter_list|(
name|SolrIndexSearcher
name|localSearcher
parameter_list|,
name|Term
name|term
parameter_list|,
name|TermContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|collectionStatistics
specifier|public
specifier|abstract
name|CollectionStatistics
name|collectionStatistics
parameter_list|(
name|SolrIndexSearcher
name|localSearcher
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

