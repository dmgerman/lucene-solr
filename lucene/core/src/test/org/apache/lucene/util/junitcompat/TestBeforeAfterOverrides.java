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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestBeforeAfterOverrides
specifier|public
class|class
name|TestBeforeAfterOverrides
extends|extends
name|WithNestedTests
block|{
DECL|method|TestBeforeAfterOverrides
specifier|public
name|TestBeforeAfterOverrides
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|Before1
specifier|public
specifier|static
class|class
name|Before1
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{}
block|}
DECL|class|Before2
specifier|public
specifier|static
class|class
name|Before2
extends|extends
name|Before1
block|{}
DECL|class|Before3
specifier|public
specifier|static
class|class
name|Before3
extends|extends
name|Before2
block|{
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{}
block|}
DECL|class|After1
specifier|public
specifier|static
class|class
name|After1
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{}
block|}
DECL|class|After2
specifier|public
specifier|static
class|class
name|After2
extends|extends
name|Before1
block|{}
DECL|class|After3
specifier|public
specifier|static
class|class
name|After3
extends|extends
name|Before2
block|{
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{}
block|}
annotation|@
name|Test
DECL|method|testBefore
specifier|public
name|void
name|testBefore
parameter_list|()
block|{
name|Result
name|result
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Before3
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTrace
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There are overridden methods"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAfter
specifier|public
name|void
name|testAfter
parameter_list|()
block|{
name|Result
name|result
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Before3
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTrace
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There are overridden methods"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

