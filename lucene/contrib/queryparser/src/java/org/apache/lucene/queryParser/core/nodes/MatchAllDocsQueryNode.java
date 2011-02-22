begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|nodes
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link MatchAllDocsQueryNode} indicates that a query node tree or subtree  * will match all documents if executed in the index.  */
end_comment

begin_class
DECL|class|MatchAllDocsQueryNode
specifier|public
class|class
name|MatchAllDocsQueryNode
extends|extends
name|QueryNodeImpl
block|{
DECL|method|MatchAllDocsQueryNode
specifier|public
name|MatchAllDocsQueryNode
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<matchAllDocs field='*' term='*'/>"
return|;
block|}
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
block|{
return|return
literal|"*:*"
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|MatchAllDocsQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|MatchAllDocsQueryNode
name|clone
init|=
operator|(
name|MatchAllDocsQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
comment|// nothing to clone
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit

