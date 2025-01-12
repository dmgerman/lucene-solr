begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|Cell
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
name|BytesRefIterator
import|;
end_import

begin_comment
comment|/**  * A reset'able {@link org.apache.lucene.util.BytesRefIterator} wrapper around  * an {@link java.util.Iterator} of {@link org.apache.lucene.spatial.prefix.tree.Cell}s.  *  * @see PrefixTreeStrategy#newCellToBytesRefIterator()  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|CellToBytesRefIterator
specifier|public
class|class
name|CellToBytesRefIterator
implements|implements
name|BytesRefIterator
block|{
DECL|field|cellIter
specifier|protected
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|cellIter
decl_stmt|;
DECL|field|bytesRef
specifier|protected
name|BytesRef
name|bytesRef
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Iterator
argument_list|<
name|Cell
argument_list|>
name|cellIter
parameter_list|)
block|{
name|this
operator|.
name|cellIter
operator|=
name|cellIter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|cellIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|cellIter
operator|.
name|next
argument_list|()
operator|.
name|getTokenBytesWithLeaf
argument_list|(
name|bytesRef
argument_list|)
return|;
block|}
block|}
end_class

end_unit

