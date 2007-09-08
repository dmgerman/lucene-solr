begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|snowball
operator|.
name|SnowballFilter
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|SolrConfig
import|;
end_import

begin_comment
comment|/**  * Factory for SnowballFilters, with configurable language  *   * Browsing the code, SnowballFilter uses reflection to adapt to Lucene... don't  * use this if you are concerned about speed. Use EnglishPorterFilterFactory.  *   * @version $Id$  */
end_comment

begin_class
DECL|class|SnowballPorterFilterFactory
specifier|public
class|class
name|SnowballPorterFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|language
specifier|private
name|String
name|language
init|=
literal|"English"
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrConfig
name|solrConfig
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|solrConfig
argument_list|,
name|args
argument_list|)
expr_stmt|;
specifier|final
name|String
name|cfgLanguage
init|=
name|args
operator|.
name|get
argument_list|(
literal|"language"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfgLanguage
operator|!=
literal|null
condition|)
name|language
operator|=
name|cfgLanguage
expr_stmt|;
name|SolrCore
operator|.
name|log
operator|.
name|fine
argument_list|(
literal|"SnowballPorterFilterFactory: language="
operator|+
name|language
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|SnowballFilter
argument_list|(
name|input
argument_list|,
name|language
argument_list|)
return|;
block|}
block|}
end_class

end_unit

