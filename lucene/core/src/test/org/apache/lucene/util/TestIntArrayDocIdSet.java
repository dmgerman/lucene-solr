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
name|DocIdSetIterator
import|;
end_import

begin_class
DECL|class|TestIntArrayDocIdSet
specifier|public
class|class
name|TestIntArrayDocIdSet
extends|extends
name|BaseDocIdSetTestCase
argument_list|<
name|IntArrayDocIdSet
argument_list|>
block|{
annotation|@
name|Override
DECL|method|copyOf
specifier|public
name|IntArrayDocIdSet
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
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
name|int
name|l
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bs
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|!=
operator|-
literal|1
condition|;
name|i
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|docs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docs
argument_list|,
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|docs
index|[
name|l
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
name|docs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docs
argument_list|,
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|docs
index|[
name|l
index|]
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
return|return
operator|new
name|IntArrayDocIdSet
argument_list|(
name|docs
argument_list|,
name|l
argument_list|)
return|;
block|}
block|}
end_class

end_unit

