begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.mutable
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|mutable
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
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Simple test of the basic contract of the various {@link MutableValue} implementaitons.  */
end_comment

begin_class
DECL|class|TestMutableValues
specifier|public
class|class
name|TestMutableValues
extends|extends
name|LuceneTestCase
block|{
DECL|method|testStr
specifier|public
name|void
name|testStr
parameter_list|()
block|{
name|MutableValueStr
name|xxx
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
assert|assert
name|xxx
operator|.
name|value
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
assert|assert
name|xxx
operator|.
name|exists
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|MutableValueStr
name|yyy
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|value
operator|.
name|clear
argument_list|()
expr_stmt|;
name|xxx
operator|.
name|value
operator|.
name|copyChars
argument_list|(
literal|"zzz"
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|value
operator|.
name|clear
argument_list|()
expr_stmt|;
name|yyy
operator|.
name|value
operator|.
name|copyChars
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|xxx
operator|.
name|compareTo
argument_list|(
name|yyy
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|yyy
operator|.
name|compareTo
argument_list|(
name|xxx
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|copy
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
comment|// special BytesRef considerations...
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|xxx
operator|.
name|value
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// but leave bytes alone
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|yyy
operator|.
name|value
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// but leave bytes alone
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
block|}
DECL|method|testDouble
specifier|public
name|void
name|testDouble
parameter_list|()
block|{
name|MutableValueDouble
name|xxx
init|=
operator|new
name|MutableValueDouble
argument_list|()
decl_stmt|;
assert|assert
name|xxx
operator|.
name|value
operator|==
literal|0.0D
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
assert|assert
name|xxx
operator|.
name|exists
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|MutableValueDouble
name|yyy
init|=
operator|new
name|MutableValueDouble
argument_list|()
decl_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|value
operator|=
literal|42.0D
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|value
operator|=
operator|-
literal|99.0D
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|xxx
operator|.
name|compareTo
argument_list|(
name|yyy
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|yyy
operator|.
name|compareTo
argument_list|(
name|xxx
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|copy
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
block|}
DECL|method|testInt
specifier|public
name|void
name|testInt
parameter_list|()
block|{
name|MutableValueInt
name|xxx
init|=
operator|new
name|MutableValueInt
argument_list|()
decl_stmt|;
assert|assert
name|xxx
operator|.
name|value
operator|==
literal|0
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
assert|assert
name|xxx
operator|.
name|exists
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|MutableValueInt
name|yyy
init|=
operator|new
name|MutableValueInt
argument_list|()
decl_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|value
operator|=
literal|42
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|value
operator|=
operator|-
literal|99
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|xxx
operator|.
name|compareTo
argument_list|(
name|yyy
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|yyy
operator|.
name|compareTo
argument_list|(
name|xxx
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|copy
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
block|}
DECL|method|testFloat
specifier|public
name|void
name|testFloat
parameter_list|()
block|{
name|MutableValueFloat
name|xxx
init|=
operator|new
name|MutableValueFloat
argument_list|()
decl_stmt|;
assert|assert
name|xxx
operator|.
name|value
operator|==
literal|0.0F
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
assert|assert
name|xxx
operator|.
name|exists
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|MutableValueFloat
name|yyy
init|=
operator|new
name|MutableValueFloat
argument_list|()
decl_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|value
operator|=
literal|42.0F
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|value
operator|=
operator|-
literal|99.0F
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|xxx
operator|.
name|compareTo
argument_list|(
name|yyy
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|yyy
operator|.
name|compareTo
argument_list|(
name|xxx
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|copy
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
block|}
DECL|method|testLong
specifier|public
name|void
name|testLong
parameter_list|()
block|{
name|MutableValueLong
name|xxx
init|=
operator|new
name|MutableValueLong
argument_list|()
decl_stmt|;
assert|assert
name|xxx
operator|.
name|value
operator|==
literal|0L
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
assert|assert
name|xxx
operator|.
name|exists
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|MutableValueLong
name|yyy
init|=
operator|new
name|MutableValueLong
argument_list|()
decl_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|value
operator|=
literal|42L
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|value
operator|=
operator|-
literal|99L
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|xxx
operator|.
name|compareTo
argument_list|(
name|yyy
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|yyy
operator|.
name|compareTo
argument_list|(
name|xxx
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|copy
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
block|}
DECL|method|testBool
specifier|public
name|void
name|testBool
parameter_list|()
block|{
name|MutableValueBool
name|xxx
init|=
operator|new
name|MutableValueBool
argument_list|()
decl_stmt|;
assert|assert
name|xxx
operator|.
name|value
operator|==
literal|false
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
assert|assert
name|xxx
operator|.
name|exists
operator|:
literal|"defaults have changed, test utility may not longer be as high"
assert|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|MutableValueBool
name|yyy
init|=
operator|new
name|MutableValueBool
argument_list|()
decl_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|value
operator|=
literal|true
expr_stmt|;
name|xxx
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|yyy
operator|.
name|value
operator|=
literal|false
expr_stmt|;
name|yyy
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
name|assertSanity
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertInEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|0
operator|<
name|xxx
operator|.
name|compareTo
argument_list|(
name|yyy
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|yyy
operator|.
name|compareTo
argument_list|(
name|xxx
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|xxx
operator|.
name|copy
argument_list|(
name|yyy
argument_list|)
expr_stmt|;
name|assertSanity
argument_list|(
name|xxx
argument_list|)
expr_stmt|;
name|assertEquality
argument_list|(
name|xxx
argument_list|,
name|yyy
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSanity
specifier|private
name|void
name|assertSanity
parameter_list|(
name|MutableValue
name|x
parameter_list|)
block|{
name|assertEquality
argument_list|(
name|x
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|MutableValue
name|y
init|=
name|x
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|assertEquality
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEquality
specifier|private
name|void
name|assertEquality
parameter_list|(
name|MutableValue
name|x
parameter_list|,
name|MutableValue
name|y
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|x
operator|.
name|hashCode
argument_list|()
argument_list|,
name|y
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|y
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x
operator|.
name|equalsSameType
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|equalsSameType
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|x
operator|.
name|compareTo
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|y
operator|.
name|compareTo
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|x
operator|.
name|compareSameType
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|y
operator|.
name|compareSameType
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertInEquality
specifier|private
name|void
name|assertInEquality
parameter_list|(
name|MutableValue
name|x
parameter_list|,
name|MutableValue
name|y
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|x
operator|.
name|equals
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|y
operator|.
name|equals
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|x
operator|.
name|equalsSameType
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|y
operator|.
name|equalsSameType
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|0
operator|==
name|x
operator|.
name|compareTo
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|0
operator|==
name|y
operator|.
name|compareTo
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

