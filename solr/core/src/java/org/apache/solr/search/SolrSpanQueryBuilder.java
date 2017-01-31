begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|analysis
operator|.
name|Analyzer
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
name|queryparser
operator|.
name|xml
operator|.
name|builders
operator|.
name|SpanQueryBuilder
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_class
DECL|class|SolrSpanQueryBuilder
specifier|public
specifier|abstract
class|class
name|SolrSpanQueryBuilder
extends|extends
name|SolrQueryBuilder
implements|implements
name|SpanQueryBuilder
block|{
DECL|field|spanFactory
specifier|protected
specifier|final
name|SpanQueryBuilder
name|spanFactory
decl_stmt|;
DECL|method|SolrSpanQueryBuilder
specifier|public
name|SolrSpanQueryBuilder
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SpanQueryBuilder
name|spanFactory
parameter_list|)
block|{
name|super
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|,
name|req
argument_list|,
name|spanFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|spanFactory
operator|=
name|spanFactory
expr_stmt|;
block|}
block|}
end_class

end_unit

