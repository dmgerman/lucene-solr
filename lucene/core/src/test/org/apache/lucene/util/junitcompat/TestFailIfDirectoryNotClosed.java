begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
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
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
import|;
end_import

begin_class
DECL|class|TestFailIfDirectoryNotClosed
specifier|public
class|class
name|TestFailIfDirectoryNotClosed
extends|extends
name|WithNestedTests
block|{
DECL|method|TestFailIfDirectoryNotClosed
specifier|public
name|TestFailIfDirectoryNotClosed
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|Nested1
specifier|public
specifier|static
class|class
name|Nested1
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailIfDirectoryNotClosed
specifier|public
name|void
name|testFailIfDirectoryNotClosed
parameter_list|()
block|{
name|Result
name|r
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested1
operator|.
name|class
argument_list|)
decl_stmt|;
name|RandomizedTest
operator|.
name|assumeTrue
argument_list|(
literal|"Ignoring nested test, very likely zombie threads present."
argument_list|,
name|r
operator|.
name|getIgnoreCount
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertFailureCount
argument_list|(
literal|1
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|r
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Resource in scope SUITE failed to close"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

