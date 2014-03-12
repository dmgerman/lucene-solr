begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|util
operator|.
name|LuceneTestCase
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
name|common
operator|.
name|SolrException
import|;
end_import

begin_class
DECL|class|NamedListTest
specifier|public
class|class
name|NamedListTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRemove
specifier|public
name|void
name|testRemove
parameter_list|()
block|{
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
name|value
operator|=
name|nl
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|nl
operator|.
name|remove
argument_list|(
literal|"key2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoveAll
specifier|public
name|void
name|testRemoveAll
parameter_list|()
block|{
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-2"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-2"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-3"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-3"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-4"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-4"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-5"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-5"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-6"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
literal|null
decl_stmt|;
name|values
operator|=
name|nl
operator|.
name|removeAll
argument_list|(
literal|"key1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1-1"
argument_list|,
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1-3"
argument_list|,
name|values
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|=
name|nl
operator|.
name|removeAll
argument_list|(
literal|"key2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoveArgs
specifier|public
name|void
name|testRemoveArgs
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-2"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-2"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-3"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-3"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-4"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-4"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-5"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-5"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1-6"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
literal|"value2-7"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|nl
operator|.
name|removeConfigArgs
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1-1"
argument_list|,
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value1-3"
argument_list|,
name|values
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|values
operator|=
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|nl
operator|.
name|removeConfigArgs
argument_list|(
literal|"key2"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// Expected exception.
name|assertTrue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// nl should be unmodified when removeArgs throws an exception.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRecursive
specifier|public
name|void
name|testRecursive
parameter_list|()
block|{
comment|// key1
comment|// key2
comment|// - key2a
comment|// - key2b
comment|// --- key2b1
comment|// --- key2b2
comment|// - key2c
comment|// - k2int1
comment|// key3
comment|// - key3a
comment|// --- key3a1
comment|// --- key3a2
comment|// --- key3a3
comment|// - key3b
comment|// - key3c
comment|// this is a varied NL structure.
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl2b
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl2b
operator|.
name|add
argument_list|(
literal|"key2b1"
argument_list|,
literal|"value2b1"
argument_list|)
expr_stmt|;
name|nl2b
operator|.
name|add
argument_list|(
literal|"key2b2"
argument_list|,
literal|"value2b2"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl3a
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl3a
operator|.
name|add
argument_list|(
literal|"key3a1"
argument_list|,
literal|"value3a1"
argument_list|)
expr_stmt|;
name|nl3a
operator|.
name|add
argument_list|(
literal|"key3a2"
argument_list|,
literal|"value3a2"
argument_list|)
expr_stmt|;
name|nl3a
operator|.
name|add
argument_list|(
literal|"key3a3"
argument_list|,
literal|"value3a3"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl2
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl2
operator|.
name|add
argument_list|(
literal|"key2a"
argument_list|,
literal|"value2a"
argument_list|)
expr_stmt|;
name|nl2
operator|.
name|add
argument_list|(
literal|"key2b"
argument_list|,
name|nl2b
argument_list|)
expr_stmt|;
name|nl2
operator|.
name|add
argument_list|(
literal|"k2int1"
argument_list|,
operator|(
name|int
operator|)
literal|5
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl3
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl3
operator|.
name|add
argument_list|(
literal|"key3a"
argument_list|,
name|nl3a
argument_list|)
expr_stmt|;
name|nl3
operator|.
name|add
argument_list|(
literal|"key3b"
argument_list|,
literal|"value3b"
argument_list|)
expr_stmt|;
name|nl3
operator|.
name|add
argument_list|(
literal|"key3c"
argument_list|,
literal|"value3c"
argument_list|)
expr_stmt|;
name|nl3
operator|.
name|add
argument_list|(
literal|"key3c"
argument_list|,
literal|"value3c2"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
name|nl2
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"key3"
argument_list|,
name|nl3
argument_list|)
expr_stmt|;
comment|// Simple three-level checks.
name|String
name|test1
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key2"
argument_list|,
literal|"key2b"
argument_list|,
literal|"key2b2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value2b2"
argument_list|,
name|test1
argument_list|)
expr_stmt|;
name|String
name|test2
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key3"
argument_list|,
literal|"key3a"
argument_list|,
literal|"key3a3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value3a3"
argument_list|,
name|test2
argument_list|)
expr_stmt|;
comment|// Two-level check.
name|String
name|test3
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key3"
argument_list|,
literal|"key3c"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value3c"
argument_list|,
name|test3
argument_list|)
expr_stmt|;
comment|// Checking that invalid values return null.
name|String
name|test4
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key3"
argument_list|,
literal|"key3c"
argument_list|,
literal|"invalid"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|test4
argument_list|)
expr_stmt|;
name|String
name|test5
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key3"
argument_list|,
literal|"invalid"
argument_list|,
literal|"invalid"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|test5
argument_list|)
expr_stmt|;
name|String
name|test6
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"invalid"
argument_list|,
literal|"key3c"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|test6
argument_list|)
expr_stmt|;
comment|// Verify that retrieved NamedList objects have the right type.
name|Object
name|test7
init|=
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key2"
argument_list|,
literal|"key2b"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test7
operator|instanceof
name|NamedList
argument_list|)
expr_stmt|;
comment|// Integer check.
name|int
name|test8
init|=
operator|(
name|Integer
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key2"
argument_list|,
literal|"k2int1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|test8
argument_list|)
expr_stmt|;
comment|// Check that a single argument works the same as get(String).
name|String
name|test9
init|=
operator|(
name|String
operator|)
name|nl
operator|.
name|findRecursive
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1"
argument_list|,
name|test9
argument_list|)
expr_stmt|;
comment|// enl == explicit nested list
comment|//
comment|// key1
comment|// - key1a
comment|// - key1b
comment|// key2 (null list)
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|>
name|enl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|enlkey1
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|enlkey2
init|=
literal|null
decl_stmt|;
name|enlkey1
operator|.
name|add
argument_list|(
literal|"key1a"
argument_list|,
literal|"value1a"
argument_list|)
expr_stmt|;
name|enlkey1
operator|.
name|add
argument_list|(
literal|"key1b"
argument_list|,
literal|"value1b"
argument_list|)
expr_stmt|;
name|enl
operator|.
name|add
argument_list|(
literal|"key1"
argument_list|,
name|enlkey1
argument_list|)
expr_stmt|;
name|enl
operator|.
name|add
argument_list|(
literal|"key2"
argument_list|,
name|enlkey2
argument_list|)
expr_stmt|;
comment|// Tests that are very similar to the test above, just repeated
comment|// on the explicitly nested object type.
name|String
name|enltest1
init|=
operator|(
name|String
operator|)
name|enl
operator|.
name|findRecursive
argument_list|(
literal|"key1"
argument_list|,
literal|"key1a"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1a"
argument_list|,
name|enltest1
argument_list|)
expr_stmt|;
name|String
name|enltest2
init|=
operator|(
name|String
operator|)
name|enl
operator|.
name|findRecursive
argument_list|(
literal|"key1"
argument_list|,
literal|"key1b"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value1b"
argument_list|,
name|enltest2
argument_list|)
expr_stmt|;
comment|// Verify that when a null value is stored, the standard get method
comment|// says it is null, then check the recursive method.
name|Object
name|enltest3
init|=
name|enl
operator|.
name|get
argument_list|(
literal|"key2"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|enltest3
argument_list|)
expr_stmt|;
name|Object
name|enltest4
init|=
name|enl
operator|.
name|findRecursive
argument_list|(
literal|"key2"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|enltest4
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

