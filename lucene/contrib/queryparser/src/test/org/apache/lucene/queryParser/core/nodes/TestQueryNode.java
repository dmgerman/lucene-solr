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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestQueryNode
specifier|public
class|class
name|TestQueryNode
extends|extends
name|LuceneTestCase
block|{
comment|/* LUCENE-2227 bug in QueryNodeImpl.add() */
DECL|method|testAddChildren
specifier|public
name|void
name|testAddChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryNode
name|nodeA
init|=
operator|new
name|FieldQueryNode
argument_list|(
literal|"foo"
argument_list|,
literal|"A"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|QueryNode
name|nodeB
init|=
operator|new
name|FieldQueryNode
argument_list|(
literal|"foo"
argument_list|,
literal|"B"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|BooleanQueryNode
name|bq
init|=
operator|new
name|BooleanQueryNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|nodeA
argument_list|)
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|nodeB
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bq
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* LUCENE-3045 bug in QueryNodeImpl.containsTag(String key)*/
DECL|method|testTags
specifier|public
name|void
name|testTags
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryNode
name|node
init|=
operator|new
name|FieldQueryNode
argument_list|(
literal|"foo"
argument_list|,
literal|"A"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|node
operator|.
name|setTag
argument_list|(
literal|"TaG"
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|getTagMap
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|containsTag
argument_list|(
literal|"tAg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|getTag
argument_list|(
literal|"tAg"
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

