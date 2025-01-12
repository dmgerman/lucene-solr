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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|SolrTestCaseJ4
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
name|util
operator|.
name|PathTrie
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|api
operator|.
name|ApiBag
operator|.
name|HANDLER_NAME
import|;
end_import

begin_class
DECL|class|TestPathTrie
specifier|public
class|class
name|TestPathTrie
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testPathTrie
specifier|public
name|void
name|testPathTrie
parameter_list|()
block|{
name|PathTrie
argument_list|<
name|String
argument_list|>
name|pathTrie
init|=
operator|new
name|PathTrie
argument_list|<>
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"_introspect"
argument_list|)
argument_list|)
decl_stmt|;
name|pathTrie
operator|.
name|insert
argument_list|(
literal|"/"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|"R"
argument_list|)
expr_stmt|;
name|pathTrie
operator|.
name|insert
argument_list|(
literal|"/aa"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|pathTrie
operator|.
name|insert
argument_list|(
literal|"/aa/bb/{cc}/dd"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|pathTrie
operator|.
name|insert
argument_list|(
literal|"/$handlerName/{cc}/dd"
argument_list|,
name|singletonMap
argument_list|(
name|HANDLER_NAME
argument_list|,
literal|"test"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|pathTrie
operator|.
name|insert
argument_list|(
literal|"/aa/bb/{cc}/{xx}"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|pathTrie
operator|.
name|insert
argument_list|(
literal|"/aa/bb"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|HashMap
name|templateValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"R"
argument_list|,
name|pathTrie
operator|.
name|lookup
argument_list|(
literal|"/"
argument_list|,
name|templateValues
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d"
argument_list|,
name|pathTrie
operator|.
name|lookup
argument_list|(
literal|"/aa"
argument_list|,
name|templateValues
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|pathTrie
operator|.
name|lookup
argument_list|(
literal|"/aa/bb/hello/dd"
argument_list|,
name|templateValues
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|templateValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|pathTrie
operator|.
name|lookup
argument_list|(
literal|"/test/hello/dd"
argument_list|,
name|templateValues
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|templateValues
operator|.
name|get
argument_list|(
literal|"cc"
argument_list|)
argument_list|)
expr_stmt|;
name|templateValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|pathTrie
operator|.
name|lookup
argument_list|(
literal|"/aa/bb/hello/world"
argument_list|,
name|templateValues
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|templateValues
operator|.
name|get
argument_list|(
literal|"cc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"world"
argument_list|,
name|templateValues
operator|.
name|get
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|subPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|templateValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|pathTrie
operator|.
name|lookup
argument_list|(
literal|"/aa"
argument_list|,
name|templateValues
argument_list|,
name|subPaths
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|subPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

