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

begin_class
DECL|class|TestIntsRef
specifier|public
class|class
name|TestIntsRef
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|IntsRef
name|i
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|IntsRef
operator|.
name|EMPTY_INTS
argument_list|,
name|i
operator|.
name|ints
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|i
operator|.
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|i
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromInts
specifier|public
name|void
name|testFromInts
parameter_list|()
block|{
name|int
name|ints
index|[]
init|=
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
decl_stmt|;
name|IntsRef
name|i
init|=
operator|new
name|IntsRef
argument_list|(
name|ints
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ints
argument_list|,
name|i
operator|.
name|ints
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|i
operator|.
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|i
operator|.
name|length
argument_list|)
expr_stmt|;
name|IntsRef
name|i2
init|=
operator|new
name|IntsRef
argument_list|(
name|ints
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|IntsRef
argument_list|(
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
name|i2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i
operator|.
name|equals
argument_list|(
name|i2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

