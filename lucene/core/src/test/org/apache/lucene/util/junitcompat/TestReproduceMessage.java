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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|rules
operator|.
name|TestRule
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
name|Description
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
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_comment
comment|/**  * Test reproduce message is right.  */
end_comment

begin_class
DECL|class|TestReproduceMessage
specifier|public
class|class
name|TestReproduceMessage
extends|extends
name|WithNestedTests
block|{
DECL|field|where
specifier|public
specifier|static
name|SorePoint
name|where
decl_stmt|;
DECL|field|type
specifier|public
specifier|static
name|SoreType
name|type
decl_stmt|;
DECL|class|Nested
specifier|public
specifier|static
class|class
name|Nested
extends|extends
name|AbstractNestedTest
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
if|if
condition|(
name|isRunningNested
argument_list|()
condition|)
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|BEFORE_CLASS
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Rule
DECL|field|rule
specifier|public
name|TestRule
name|rule
init|=
operator|new
name|TestRule
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|base
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|RULE
argument_list|)
expr_stmt|;
name|base
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
comment|/** Class initializer block/ default constructor. */
DECL|method|Nested
specifier|public
name|Nested
parameter_list|()
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|INITIALIZER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|BEFORE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|TEST
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|AFTER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
if|if
condition|(
name|isRunningNested
argument_list|()
condition|)
block|{
name|triggerOn
argument_list|(
name|SorePoint
operator|.
name|AFTER_CLASS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** */
DECL|method|triggerOn
specifier|private
specifier|static
name|void
name|triggerOn
parameter_list|(
name|SorePoint
name|pt
parameter_list|)
block|{
if|if
condition|(
name|pt
operator|==
name|where
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|ASSUMPTION
case|:
name|LuceneTestCase
operator|.
name|assumeTrue
argument_list|(
name|pt
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unreachable"
argument_list|)
throw|;
case|case
name|ERROR
case|:
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|pt
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
case|case
name|FAILURE
case|:
name|Assert
operator|.
name|assertTrue
argument_list|(
name|pt
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unreachable"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/*    * ASSUMPTIONS.    */
DECL|method|TestReproduceMessage
specifier|public
name|TestReproduceMessage
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeBeforeClass
specifier|public
name|void
name|testAssumeBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|BEFORE_CLASS
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeInitializer
specifier|public
name|void
name|testAssumeInitializer
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|INITIALIZER
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeRule
specifier|public
name|void
name|testAssumeRule
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|RULE
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|runAndReturnSyserr
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeBefore
specifier|public
name|void
name|testAssumeBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|BEFORE
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeTest
specifier|public
name|void
name|testAssumeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|TEST
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeAfter
specifier|public
name|void
name|testAssumeAfter
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|AFTER
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAssumeAfterClass
specifier|public
name|void
name|testAssumeAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ASSUMPTION
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|AFTER_CLASS
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * FAILURES    */
annotation|@
name|Test
DECL|method|testFailureBeforeClass
specifier|public
name|void
name|testFailureBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|BEFORE_CLASS
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureInitializer
specifier|public
name|void
name|testFailureInitializer
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|INITIALIZER
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureRule
specifier|public
name|void
name|testFailureRule
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|RULE
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureBefore
specifier|public
name|void
name|testFailureBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|BEFORE
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureTest
specifier|public
name|void
name|testFailureTest
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|TEST
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureAfter
specifier|public
name|void
name|testFailureAfter
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|AFTER
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailureAfterClass
specifier|public
name|void
name|testFailureAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|FAILURE
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|AFTER_CLASS
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * ERRORS    */
annotation|@
name|Test
DECL|method|testErrorBeforeClass
specifier|public
name|void
name|testErrorBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|BEFORE_CLASS
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorInitializer
specifier|public
name|void
name|testErrorInitializer
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|INITIALIZER
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorRule
specifier|public
name|void
name|testErrorRule
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|RULE
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorBefore
specifier|public
name|void
name|testErrorBefore
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|BEFORE
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorTest
specifier|public
name|void
name|testErrorTest
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|TEST
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorAfter
specifier|public
name|void
name|testErrorAfter
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|AFTER
expr_stmt|;
specifier|final
name|String
name|syserr
init|=
name|runAndReturnSyserr
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|syserr
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtests.method=test"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|syserr
operator|.
name|split
argument_list|(
literal|"\\s"
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
literal|"-Dtestcase="
operator|+
name|Nested
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorAfterClass
specifier|public
name|void
name|testErrorAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|type
operator|=
name|SoreType
operator|.
name|ERROR
expr_stmt|;
name|where
operator|=
name|SorePoint
operator|.
name|AFTER_CLASS
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|runAndReturnSyserr
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOTE: reproduce with:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runAndReturnSyserr
specifier|private
name|String
name|runAndReturnSyserr
parameter_list|()
block|{
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|err
init|=
name|getSysErr
argument_list|()
decl_stmt|;
comment|// super.prevSysErr.println("Type: " + type + ", point: " + where + " resulted in:\n" + err);
comment|// super.prevSysErr.println("---");
return|return
name|err
return|;
block|}
block|}
end_class

end_unit

