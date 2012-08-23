begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
end_comment

begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment

begin_class
DECL|class|BulkOperationPacked56
specifier|final
class|class
name|BulkOperationPacked56
extends|extends
name|BulkOperation
block|{
DECL|method|blockCount
specifier|public
name|int
name|blockCount
parameter_list|()
block|{
return|return
literal|7
return|;
block|}
DECL|method|valueCount
specifier|public
name|int
name|valueCount
parameter_list|()
block|{
return|return
literal|8
return|;
block|}
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block0
operator|>>>
literal|8
expr_stmt|;
specifier|final
name|long
name|block1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block0
operator|&
literal|255L
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
name|block1
operator|>>>
literal|16
operator|)
expr_stmt|;
specifier|final
name|long
name|block2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block1
operator|&
literal|65535L
operator|)
operator|<<
literal|40
operator|)
operator||
operator|(
name|block2
operator|>>>
literal|24
operator|)
expr_stmt|;
specifier|final
name|long
name|block3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block2
operator|&
literal|16777215L
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|block3
operator|>>>
literal|32
operator|)
expr_stmt|;
specifier|final
name|long
name|block4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block3
operator|&
literal|4294967295L
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
name|block4
operator|>>>
literal|40
operator|)
expr_stmt|;
specifier|final
name|long
name|block5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block4
operator|&
literal|1099511627775L
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
name|block5
operator|>>>
literal|48
operator|)
expr_stmt|;
specifier|final
name|long
name|block6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|block5
operator|&
literal|281474976710655L
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|block6
operator|>>>
literal|56
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block6
operator|&
literal|72057594037927935L
expr_stmt|;
block|}
block|}
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte0
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte1
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte2
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte3
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte4
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte5
operator|<<
literal|8
operator|)
operator||
name|byte6
expr_stmt|;
specifier|final
name|long
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte8
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte9
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte10
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte11
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte12
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte13
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte7
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte8
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte9
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte10
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte11
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte12
operator|<<
literal|8
operator|)
operator||
name|byte13
expr_stmt|;
specifier|final
name|long
name|byte14
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte15
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte16
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte17
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte18
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte19
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte20
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte14
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte15
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte16
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte17
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte18
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte19
operator|<<
literal|8
operator|)
operator||
name|byte20
expr_stmt|;
specifier|final
name|long
name|byte21
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte22
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte23
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte24
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte25
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte26
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte27
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte21
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte22
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte23
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte24
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte25
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte26
operator|<<
literal|8
operator|)
operator||
name|byte27
expr_stmt|;
specifier|final
name|long
name|byte28
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte29
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte30
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte31
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte32
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte33
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte34
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte28
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte29
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte30
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte31
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte32
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte33
operator|<<
literal|8
operator|)
operator||
name|byte34
expr_stmt|;
specifier|final
name|long
name|byte35
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte36
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte37
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte38
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte39
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte40
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte41
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte35
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte36
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte37
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte38
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte39
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte40
operator|<<
literal|8
operator|)
operator||
name|byte41
expr_stmt|;
specifier|final
name|long
name|byte42
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte43
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte44
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte45
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte46
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte47
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte48
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte42
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte43
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte44
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte45
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte46
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte47
operator|<<
literal|8
operator|)
operator||
name|byte48
expr_stmt|;
specifier|final
name|long
name|byte49
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte50
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte51
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte52
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte53
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte54
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|long
name|byte55
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte49
operator|<<
literal|48
operator|)
operator||
operator|(
name|byte50
operator|<<
literal|40
operator|)
operator||
operator|(
name|byte51
operator|<<
literal|32
operator|)
operator||
operator|(
name|byte52
operator|<<
literal|24
operator|)
operator||
operator|(
name|byte53
operator|<<
literal|16
operator|)
operator||
operator|(
name|byte54
operator|<<
literal|8
operator|)
operator||
name|byte55
expr_stmt|;
block|}
block|}
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xffffffffL
operator|)
operator|>>>
literal|48
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xffffffffL
operator|)
operator|>>>
literal|40
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xffffffffL
operator|)
operator|>>>
literal|32
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xffffffffL
operator|)
operator|>>>
literal|24
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|40
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xffffffffL
operator|)
operator|>>>
literal|16
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|&
literal|0xffffffffL
operator|)
operator|>>>
literal|8
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|56
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
expr_stmt|;
block|}
block|}
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|8
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|>>>
literal|48
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|16
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|>>>
literal|40
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|24
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|>>>
literal|32
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|32
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|>>>
literal|24
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|40
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|>>>
literal|16
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|48
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
index|]
operator|>>>
literal|8
operator|)
expr_stmt|;
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|56
operator|)
operator||
name|values
index|[
name|valuesOffset
operator|++
index|]
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

