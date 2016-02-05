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
name|java
operator|.
name|util
operator|.
name|List
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
name|builders
operator|.
name|QueryTreeBuilder
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
name|FieldQueryNode
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
name|core
operator|.
name|nodes
operator|.
name|TokenizedPhraseQueryNode
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
name|PhraseQuery
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
name|TermQuery
import|;
end_import

begin_comment
comment|/**  * Builds a {@link PhraseQuery} object from a {@link TokenizedPhraseQueryNode}  * object.  */
end_comment

begin_class
DECL|class|PhraseQueryNodeBuilder
specifier|public
class|class
name|PhraseQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|PhraseQueryNodeBuilder
specifier|public
name|PhraseQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Query
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|TokenizedPhraseQueryNode
name|phraseNode
init|=
operator|(
name|TokenizedPhraseQueryNode
operator|)
name|queryNode
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|phraseNode
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|TermQuery
name|termQuery
init|=
operator|(
name|TermQuery
operator|)
name|child
operator|.
name|getTag
argument_list|(
name|QueryTreeBuilder
operator|.
name|QUERY_TREE_BUILDER_TAGID
argument_list|)
decl_stmt|;
name|FieldQueryNode
name|termNode
init|=
operator|(
name|FieldQueryNode
operator|)
name|child
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|termQuery
operator|.
name|getTerm
argument_list|()
argument_list|,
name|termNode
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

