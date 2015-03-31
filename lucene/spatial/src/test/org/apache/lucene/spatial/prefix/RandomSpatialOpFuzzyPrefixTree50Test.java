begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Test RandomSpatialOpFuzzyPrefixTreeTest using the PrefixTree index format found in 5.0 and prior. */
end_comment

begin_class
DECL|class|RandomSpatialOpFuzzyPrefixTree50Test
specifier|public
class|class
name|RandomSpatialOpFuzzyPrefixTree50Test
extends|extends
name|RandomSpatialOpFuzzyPrefixTreeTest
block|{
DECL|method|newRPT
specifier|protected
name|RecursivePrefixTreeStrategy
name|newRPT
parameter_list|()
block|{
return|return
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|this
operator|.
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|CellToBytesRefIterator
name|newCellToBytesRefIterator
parameter_list|()
block|{
return|return
operator|new
name|CellToBytesRefIterator50
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

