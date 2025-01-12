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
DECL|class|TestCloseableThreadLocal
specifier|public
class|class
name|TestCloseableThreadLocal
extends|extends
name|LuceneTestCase
block|{
DECL|field|TEST_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|TEST_VALUE
init|=
literal|"initvaluetest"
decl_stmt|;
DECL|method|testInitValue
specifier|public
name|void
name|testInitValue
parameter_list|()
block|{
name|InitValueThreadLocal
name|tl
init|=
operator|new
name|InitValueThreadLocal
argument_list|()
decl_stmt|;
name|String
name|str
init|=
operator|(
name|String
operator|)
name|tl
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_VALUE
argument_list|,
name|str
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullValue
specifier|public
name|void
name|testNullValue
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Tests that null can be set as a valid value (LUCENE-1805). This
comment|// previously failed in get().
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
name|ctl
init|=
operator|new
name|CloseableThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
name|ctl
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ctl
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultValueWithoutSetting
specifier|public
name|void
name|testDefaultValueWithoutSetting
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-1805: make sure default get returns null,
comment|// twice in a row
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
name|ctl
init|=
operator|new
name|CloseableThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|ctl
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|InitValueThreadLocal
specifier|public
specifier|static
class|class
name|InitValueThreadLocal
extends|extends
name|CloseableThreadLocal
argument_list|<
name|Object
argument_list|>
block|{
annotation|@
name|Override
DECL|method|initialValue
specifier|protected
name|Object
name|initialValue
parameter_list|()
block|{
return|return
name|TEST_VALUE
return|;
block|}
block|}
block|}
end_class

end_unit

