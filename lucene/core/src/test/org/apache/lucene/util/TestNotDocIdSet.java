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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|search
operator|.
name|DocIdSet
import|;
end_import

begin_class
DECL|class|TestNotDocIdSet
specifier|public
class|class
name|TestNotDocIdSet
extends|extends
name|BaseDocIdSetTestCase
argument_list|<
name|NotDocIdSet
argument_list|>
block|{
annotation|@
name|Override
DECL|method|copyOf
specifier|public
name|NotDocIdSet
name|copyOf
parameter_list|(
name|BitSet
name|bs
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|bs
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
init|;
name|doc
operator|<
name|length
condition|;
name|doc
operator|=
name|bs
operator|.
name|nextClearBit
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NotDocIdSet
argument_list|(
name|length
argument_list|,
operator|new
name|FixedBitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|assertEquals
specifier|public
name|void
name|assertEquals
parameter_list|(
name|int
name|numBits
parameter_list|,
name|BitSet
name|ds1
parameter_list|,
name|NotDocIdSet
name|ds2
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|ds1
argument_list|,
name|ds2
argument_list|)
expr_stmt|;
specifier|final
name|Bits
name|bits2
init|=
name|ds2
operator|.
name|bits
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|ds2
operator|.
name|isCacheable
argument_list|()
argument_list|)
expr_stmt|;
comment|// since we wrapped a FixedBitSet
name|assertNotNull
argument_list|(
name|bits2
argument_list|)
expr_stmt|;
comment|// since we wrapped a FixedBitSet
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|bits2
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBits
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|ds1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|bits2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBits
specifier|public
name|void
name|testBits
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNull
argument_list|(
operator|new
name|NotDocIdSet
argument_list|(
literal|3
argument_list|,
name|DocIdSet
operator|.
name|EMPTY
argument_list|)
operator|.
name|bits
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|new
name|NotDocIdSet
argument_list|(
literal|3
argument_list|,
operator|new
name|FixedBitDocIdSet
argument_list|(
operator|new
name|FixedBitSet
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
operator|.
name|bits
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

