begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|builders
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
name|queryParser
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
name|queryParser
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
name|search
operator|.
name|TermQuery
import|;
end_import

begin_comment
comment|/**  * Builds a {@link TermQuery} object from a {@link FieldQueryNode} object.  */
end_comment

begin_class
DECL|class|DummyQueryNodeBuilder
specifier|public
class|class
name|DummyQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|DummyQueryNodeBuilder
specifier|public
name|DummyQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|TermQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

