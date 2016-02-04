begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.nodes
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
name|nodes
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
name|nodes
operator|.
name|BooleanQueryNode
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
name|search
operator|.
name|BooleanQuery
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
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_comment
comment|/**  * A {@link StandardBooleanQueryNode} has the same behavior as  * {@link BooleanQueryNode}. It only indicates if the coord should be enabled or  * not for this boolean query.  *   * @see Similarity#coord(int, int)  * @see BooleanQuery  */
end_comment

begin_class
DECL|class|StandardBooleanQueryNode
specifier|public
class|class
name|StandardBooleanQueryNode
extends|extends
name|BooleanQueryNode
block|{
DECL|field|disableCoord
specifier|private
name|boolean
name|disableCoord
decl_stmt|;
DECL|method|StandardBooleanQueryNode
specifier|public
name|StandardBooleanQueryNode
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|clauses
parameter_list|,
name|boolean
name|disableCoord
parameter_list|)
block|{
name|super
argument_list|(
name|clauses
argument_list|)
expr_stmt|;
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
block|}
DECL|method|isDisableCoord
specifier|public
name|boolean
name|isDisableCoord
parameter_list|()
block|{
return|return
name|this
operator|.
name|disableCoord
return|;
block|}
block|}
end_class

end_unit

