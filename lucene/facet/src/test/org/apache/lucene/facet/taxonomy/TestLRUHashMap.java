begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
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
name|facet
operator|.
name|FacetTestCase
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
name|facet
operator|.
name|taxonomy
operator|.
name|LRUHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestLRUHashMap
specifier|public
class|class
name|TestLRUHashMap
extends|extends
name|FacetTestCase
block|{
comment|// testLRU() tests that the specified size limit is indeed honored, and
comment|// the remaining objects in the map are indeed those that have been most
comment|// recently used
annotation|@
name|Test
DECL|method|testLRU
specifier|public
name|void
name|testLRU
parameter_list|()
throws|throws
name|Exception
block|{
name|LRUHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|lru
init|=
operator|new
name|LRUHashMap
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|"Hello world"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|"Hi man"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"three"
argument_list|,
literal|"Bonjour"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"four"
argument_list|,
literal|"Shalom"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"five"
argument_list|,
literal|"Yo!"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
comment|// three was last used, so it got removed
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|lru
operator|.
name|get
argument_list|(
literal|"four"
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"six"
argument_list|,
literal|"hi"
argument_list|)
expr_stmt|;
name|lru
operator|.
name|put
argument_list|(
literal|"seven"
argument_list|,
literal|"hey dude"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|lru
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"five"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"six"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|lru
operator|.
name|get
argument_list|(
literal|"seven"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

