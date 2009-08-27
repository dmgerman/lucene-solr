begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|ArrayUtilTest
specifier|public
class|class
name|ArrayUtilTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testParseInt
specifier|public
name|void
name|testParseInt
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|test
decl_stmt|;
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|""
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"foo"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"0.34"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|test
operator|==
literal|1
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"-10000"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
operator|-
literal|10000
argument_list|,
name|test
operator|==
operator|-
literal|10000
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"1923"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1923
argument_list|,
name|test
operator|==
literal|1923
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"-1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
operator|-
literal|1
argument_list|,
name|test
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"foo 1923 bar"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1923
argument_list|,
name|test
operator|==
literal|1923
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

