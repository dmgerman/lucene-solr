begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|standard
operator|.
name|nodes
operator|.
name|RegexpQueryNode
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
name|processors
operator|.
name|MultiTermRewriteMethodProcessor
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
name|MultiTermQuery
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
name|RegexpQuery
import|;
end_import

begin_comment
comment|/**  * Builds a {@link RegexpQuery} object from a {@link RegexpQueryNode} object.  */
end_comment

begin_class
DECL|class|RegexpQueryNodeBuilder
specifier|public
class|class
name|RegexpQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|RegexpQueryNodeBuilder
specifier|public
name|RegexpQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|RegexpQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|RegexpQueryNode
name|regexpNode
init|=
operator|(
name|RegexpQueryNode
operator|)
name|queryNode
decl_stmt|;
comment|// TODO: make the maxStates configurable w/ a reasonable default (QueryParserBase uses 10000)
name|RegexpQuery
name|q
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|regexpNode
operator|.
name|getFieldAsString
argument_list|()
argument_list|,
name|regexpNode
operator|.
name|textToBytesRef
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
init|=
operator|(
name|MultiTermQuery
operator|.
name|RewriteMethod
operator|)
name|queryNode
operator|.
name|getTag
argument_list|(
name|MultiTermRewriteMethodProcessor
operator|.
name|TAG_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|q
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
block|}
end_class

end_unit

