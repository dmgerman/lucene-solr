begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

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
name|index
operator|.
name|PrefixCodedTerms
operator|.
name|TermIterator
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
name|PriorityQueue
import|;
end_import

begin_comment
comment|/** Merges multiple {@link FieldTermIterator}s */
end_comment

begin_class
DECL|class|MergedPrefixCodedTermsIterator
class|class
name|MergedPrefixCodedTermsIterator
extends|extends
name|FieldTermIterator
block|{
DECL|class|TermMergeQueue
specifier|private
specifier|static
class|class
name|TermMergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermIterator
argument_list|>
block|{
DECL|method|TermMergeQueue
name|TermMergeQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|TermIterator
name|a
parameter_list|,
name|TermIterator
name|b
parameter_list|)
block|{
name|int
name|cmp
init|=
name|a
operator|.
name|bytes
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|delGen
argument_list|()
operator|>
name|b
operator|.
name|delGen
argument_list|()
return|;
block|}
block|}
block|}
DECL|class|FieldMergeQueue
specifier|private
specifier|static
class|class
name|FieldMergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermIterator
argument_list|>
block|{
DECL|method|FieldMergeQueue
name|FieldMergeQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|TermIterator
name|a
parameter_list|,
name|TermIterator
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|field
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|field
argument_list|)
operator|<
literal|0
return|;
block|}
block|}
DECL|field|termQueue
specifier|final
name|TermMergeQueue
name|termQueue
decl_stmt|;
DECL|field|fieldQueue
specifier|final
name|FieldMergeQueue
name|fieldQueue
decl_stmt|;
DECL|method|MergedPrefixCodedTermsIterator
specifier|public
name|MergedPrefixCodedTermsIterator
parameter_list|(
name|List
argument_list|<
name|PrefixCodedTerms
argument_list|>
name|termsList
parameter_list|)
block|{
name|fieldQueue
operator|=
operator|new
name|FieldMergeQueue
argument_list|(
name|termsList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PrefixCodedTerms
name|terms
range|:
name|termsList
control|)
block|{
name|TermIterator
name|iter
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|iter
operator|.
name|field
operator|!=
literal|null
condition|)
block|{
name|fieldQueue
operator|.
name|add
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
block|}
name|termQueue
operator|=
operator|new
name|TermMergeQueue
argument_list|(
name|termsList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|field
name|String
name|field
decl_stmt|;
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
name|termQueue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// No more terms in current field:
if|if
condition|(
name|fieldQueue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// No more fields:
name|field
operator|=
literal|null
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Transfer all iterators on the next field into the term queue:
name|TermIterator
name|top
init|=
name|fieldQueue
operator|.
name|pop
argument_list|()
decl_stmt|;
name|termQueue
operator|.
name|add
argument_list|(
name|top
argument_list|)
expr_stmt|;
name|field
operator|=
name|top
operator|.
name|field
expr_stmt|;
assert|assert
name|field
operator|!=
literal|null
assert|;
while|while
condition|(
name|fieldQueue
operator|.
name|size
argument_list|()
operator|!=
literal|0
operator|&&
name|fieldQueue
operator|.
name|top
argument_list|()
operator|.
name|field
operator|.
name|equals
argument_list|(
name|top
operator|.
name|field
argument_list|)
condition|)
block|{
name|TermIterator
name|iter
init|=
name|fieldQueue
operator|.
name|pop
argument_list|()
decl_stmt|;
assert|assert
name|iter
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
assert|;
comment|// TODO: a little bit evil; we do this so we can == on field down below:
name|iter
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|termQueue
operator|.
name|add
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
return|return
name|termQueue
operator|.
name|top
argument_list|()
operator|.
name|bytes
return|;
block|}
else|else
block|{
name|TermIterator
name|top
init|=
name|termQueue
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|.
name|next
argument_list|()
operator|==
literal|null
condition|)
block|{
name|termQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|top
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
comment|// Field changed
name|termQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|fieldQueue
operator|.
name|add
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|termQueue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Recurse (just once) to go to next field:
return|return
name|next
argument_list|()
return|;
block|}
else|else
block|{
comment|// Still terms left in this field
return|return
name|termQueue
operator|.
name|top
argument_list|()
operator|.
name|bytes
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|delGen
specifier|public
name|long
name|delGen
parameter_list|()
block|{
return|return
name|termQueue
operator|.
name|top
argument_list|()
operator|.
name|delGen
argument_list|()
return|;
block|}
block|}
end_class

end_unit

