begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|util
package|;
end_package

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|facet
operator|.
name|search
operator|.
name|CategoryListIterator
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
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Iterates over multiple {@link CategoryListIterator}s, consuming the provided  * iterators in order.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiCategoryListIterator
specifier|public
class|class
name|MultiCategoryListIterator
implements|implements
name|CategoryListIterator
block|{
DECL|field|iterators
specifier|private
specifier|final
name|CategoryListIterator
index|[]
name|iterators
decl_stmt|;
DECL|field|validIterators
specifier|private
specifier|final
name|List
argument_list|<
name|CategoryListIterator
argument_list|>
name|validIterators
decl_stmt|;
comment|/** Receives the iterators to iterate on */
DECL|method|MultiCategoryListIterator
specifier|public
name|MultiCategoryListIterator
parameter_list|(
name|CategoryListIterator
modifier|...
name|iterators
parameter_list|)
block|{
name|this
operator|.
name|iterators
operator|=
name|iterators
expr_stmt|;
name|this
operator|.
name|validIterators
operator|=
operator|new
name|ArrayList
argument_list|<
name|CategoryListIterator
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|CategoryListIterator
name|cli
range|:
name|iterators
control|)
block|{
if|if
condition|(
name|cli
operator|.
name|init
argument_list|()
condition|)
block|{
name|validIterators
operator|.
name|add
argument_list|(
name|cli
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|!
name|validIterators
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinals
specifier|public
name|void
name|getOrdinals
parameter_list|(
name|int
name|docID
parameter_list|,
name|IntsRef
name|ints
parameter_list|)
throws|throws
name|IOException
block|{
name|IntsRef
name|tmp
init|=
operator|new
name|IntsRef
argument_list|(
name|ints
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|CategoryListIterator
name|cli
range|:
name|validIterators
control|)
block|{
name|cli
operator|.
name|getOrdinals
argument_list|(
name|docID
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
if|if
condition|(
name|ints
operator|.
name|ints
operator|.
name|length
operator|<
name|ints
operator|.
name|length
operator|+
name|tmp
operator|.
name|length
condition|)
block|{
name|ints
operator|.
name|grow
argument_list|(
name|ints
operator|.
name|length
operator|+
name|tmp
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|ints
operator|.
name|length
operator|+=
name|tmp
operator|.
name|length
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

