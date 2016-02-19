begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_class
DECL|class|TestVirtualMethod
specifier|public
class|class
name|TestVirtualMethod
extends|extends
name|LuceneTestCase
block|{
DECL|field|publicTestMethod
specifier|private
specifier|static
specifier|final
name|VirtualMethod
argument_list|<
name|TestVirtualMethod
argument_list|>
name|publicTestMethod
init|=
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|TestVirtualMethod
operator|.
name|class
argument_list|,
literal|"publicTest"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|protectedTestMethod
specifier|private
specifier|static
specifier|final
name|VirtualMethod
argument_list|<
name|TestVirtualMethod
argument_list|>
name|protectedTestMethod
init|=
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|TestVirtualMethod
operator|.
name|class
argument_list|,
literal|"protectedTest"
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|publicTest
specifier|public
name|void
name|publicTest
parameter_list|(
name|String
name|test
parameter_list|)
block|{}
DECL|method|protectedTest
specifier|protected
name|void
name|protectedTest
parameter_list|(
name|int
name|test
parameter_list|)
block|{}
DECL|class|TestClass1
specifier|static
class|class
name|TestClass1
extends|extends
name|TestVirtualMethod
block|{
annotation|@
name|Override
DECL|method|publicTest
specifier|public
name|void
name|publicTest
parameter_list|(
name|String
name|test
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|protectedTest
specifier|protected
name|void
name|protectedTest
parameter_list|(
name|int
name|test
parameter_list|)
block|{}
block|}
DECL|class|TestClass2
specifier|static
class|class
name|TestClass2
extends|extends
name|TestClass1
block|{
annotation|@
name|Override
comment|// make it public here
DECL|method|protectedTest
specifier|public
name|void
name|protectedTest
parameter_list|(
name|int
name|test
parameter_list|)
block|{}
block|}
DECL|class|TestClass3
specifier|static
class|class
name|TestClass3
extends|extends
name|TestClass2
block|{
annotation|@
name|Override
DECL|method|publicTest
specifier|public
name|void
name|publicTest
parameter_list|(
name|String
name|test
parameter_list|)
block|{}
block|}
DECL|class|TestClass4
specifier|static
class|class
name|TestClass4
extends|extends
name|TestVirtualMethod
block|{   }
DECL|class|TestClass5
specifier|static
class|class
name|TestClass5
extends|extends
name|TestClass4
block|{   }
DECL|method|testGeneral
specifier|public
name|void
name|testGeneral
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|publicTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|publicTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|TestClass1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|publicTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|TestClass2
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|publicTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|TestClass3
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|publicTestMethod
operator|.
name|isOverriddenAsOf
argument_list|(
name|TestClass4
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|publicTestMethod
operator|.
name|isOverriddenAsOf
argument_list|(
name|TestClass5
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|protectedTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|protectedTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|TestClass1
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|protectedTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|TestClass2
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|protectedTestMethod
operator|.
name|getImplementationDistance
argument_list|(
name|TestClass3
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|protectedTestMethod
operator|.
name|isOverriddenAsOf
argument_list|(
name|TestClass4
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|protectedTestMethod
operator|.
name|isOverriddenAsOf
argument_list|(
name|TestClass5
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|VirtualMethod
operator|.
name|compareImplementationDistance
argument_list|(
name|TestClass3
operator|.
name|class
argument_list|,
name|publicTestMethod
argument_list|,
name|protectedTestMethod
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|VirtualMethod
operator|.
name|compareImplementationDistance
argument_list|(
name|TestClass5
operator|.
name|class
argument_list|,
name|publicTestMethod
argument_list|,
name|protectedTestMethod
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|testExceptions
specifier|public
name|void
name|testExceptions
parameter_list|()
block|{
comment|// LuceneTestCase is not a subclass and can never override publicTest(String)
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
comment|// cast to Class to remove generics:
name|publicTestMethod
operator|.
name|getImplementationDistance
argument_list|(
operator|(
name|Class
operator|)
name|LuceneTestCase
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// Method bogus() does not exist, so IAE should be thrown
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|TestVirtualMethod
operator|.
name|class
argument_list|,
literal|"bogus"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// Method publicTest(String) is not declared in TestClass2, so IAE should be thrown
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|TestClass2
operator|.
name|class
argument_list|,
literal|"publicTest"
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
comment|// try to create a second instance of the same baseClass / method combination
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|TestVirtualMethod
operator|.
name|class
argument_list|,
literal|"publicTest"
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

