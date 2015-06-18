begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|builders
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|flexible
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|MatchNoDocsQueryNode
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
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|flexible
operator|.
name|messages
operator|.
name|MessageImpl
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
name|flexible
operator|.
name|standard
operator|.
name|parser
operator|.
name|EscapeQuerySyntaxImpl
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
name|MatchNoDocsQuery
import|;
end_import

begin_comment
comment|/**  * Builds a {@link MatchNoDocsQuery} object from a  * {@link MatchNoDocsQueryNode} object.  */
end_comment

begin_class
DECL|class|MatchNoDocsQueryNodeBuilder
specifier|public
class|class
name|MatchNoDocsQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|MatchNoDocsQueryNodeBuilder
specifier|public
name|MatchNoDocsQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|MatchNoDocsQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
comment|// validates node
if|if
condition|(
operator|!
operator|(
name|queryNode
operator|instanceof
name|MatchNoDocsQueryNode
operator|)
condition|)
block|{
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|LUCENE_QUERY_CONVERSION_ERROR
argument_list|,
name|queryNode
operator|.
name|toQueryString
argument_list|(
operator|new
name|EscapeQuerySyntaxImpl
argument_list|()
argument_list|)
argument_list|,
name|queryNode
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
operator|new
name|MatchNoDocsQuery
argument_list|()
return|;
block|}
block|}
end_class

end_unit

