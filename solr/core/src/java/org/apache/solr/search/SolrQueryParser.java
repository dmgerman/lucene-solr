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
name|solr
operator|.
name|parser
operator|.
name|QueryParser
import|;
end_import

begin_comment
comment|/**  * Solr's default query parser, a schema-driven superset of the classic lucene query parser.  */
end_comment

begin_class
DECL|class|SolrQueryParser
specifier|public
class|class
name|SolrQueryParser
extends|extends
name|QueryParser
block|{
DECL|method|SolrQueryParser
specifier|public
name|SolrQueryParser
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|parser
operator|.
name|getReq
argument_list|()
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|luceneMatchVersion
argument_list|,
name|defaultField
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

