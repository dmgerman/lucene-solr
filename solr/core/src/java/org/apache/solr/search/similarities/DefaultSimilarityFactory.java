begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|similarities
operator|.
name|Similarity
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
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * @deprecated This class has been renamed to<code>ClassicSimilarityFactory</code> to reflect the renaming of the underlying Similarity returned.  *  * @see ClassicSimilarityFactory  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|DefaultSimilarityFactory
specifier|public
class|class
name|DefaultSimilarityFactory
extends|extends
name|ClassicSimilarityFactory
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DefaultSimilarityFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"DefaultSimilarityFactory has been renamed and deprecated.  "
operator|+
literal|"Please update your configuration file to refer to ClassicSimilarityFactory instead"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

