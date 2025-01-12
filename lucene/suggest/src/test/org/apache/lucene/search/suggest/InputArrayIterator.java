begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
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
name|BytesRefBuilder
import|;
end_import

begin_comment
comment|/**  * A {@link InputIterator} over a sequence of {@link Input}s.  */
end_comment

begin_class
DECL|class|InputArrayIterator
specifier|public
specifier|final
class|class
name|InputArrayIterator
implements|implements
name|InputIterator
block|{
DECL|field|i
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Input
argument_list|>
name|i
decl_stmt|;
DECL|field|hasPayloads
specifier|private
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|field|hasContexts
specifier|private
specifier|final
name|boolean
name|hasContexts
decl_stmt|;
DECL|field|first
specifier|private
name|boolean
name|first
decl_stmt|;
DECL|field|current
specifier|private
name|Input
name|current
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|method|InputArrayIterator
specifier|public
name|InputArrayIterator
parameter_list|(
name|Iterator
argument_list|<
name|Input
argument_list|>
name|i
parameter_list|)
block|{
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|first
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|hasPayloads
operator|=
name|current
operator|.
name|hasPayloads
expr_stmt|;
name|this
operator|.
name|hasContexts
operator|=
name|current
operator|.
name|hasContexts
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|hasPayloads
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|hasContexts
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|InputArrayIterator
specifier|public
name|InputArrayIterator
parameter_list|(
name|Input
index|[]
name|i
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|InputArrayIterator
specifier|public
name|InputArrayIterator
parameter_list|(
name|Iterable
argument_list|<
name|Input
argument_list|>
name|i
parameter_list|)
block|{
name|this
argument_list|(
name|i
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|current
operator|.
name|v
return|;
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
name|i
operator|.
name|hasNext
argument_list|()
operator|||
operator|(
name|first
operator|&&
name|current
operator|!=
literal|null
operator|)
condition|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|spare
operator|.
name|copyBytes
argument_list|(
name|current
operator|.
name|term
argument_list|)
expr_stmt|;
return|return
name|spare
operator|.
name|get
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
return|return
name|current
operator|.
name|payload
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
return|return
name|current
operator|.
name|contexts
return|;
block|}
annotation|@
name|Override
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
name|hasContexts
return|;
block|}
block|}
end_class

end_unit

