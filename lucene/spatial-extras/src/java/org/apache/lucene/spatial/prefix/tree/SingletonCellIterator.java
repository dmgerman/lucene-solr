begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.prefix.tree
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
operator|.
name|tree
package|;
end_package

begin_comment
comment|/**  * A singleton (one Cell) instance of CellIterator.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|SingletonCellIterator
class|class
name|SingletonCellIterator
extends|extends
name|CellIterator
block|{
DECL|method|SingletonCellIterator
name|SingletonCellIterator
parameter_list|(
name|Cell
name|cell
parameter_list|)
block|{
name|this
operator|.
name|nextCell
operator|=
name|cell
expr_stmt|;
comment|//preload nextCell
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
name|thisCell
operator|=
literal|null
expr_stmt|;
return|return
name|nextCell
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

