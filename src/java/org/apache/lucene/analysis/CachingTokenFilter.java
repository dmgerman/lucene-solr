begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|util
operator|.
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * This class can be used if the Tokens of a TokenStream  * are intended to be consumed more than once. It caches  * all Tokens locally in a List.  *   * CachingTokenFilter implements the optional method  * {@link TokenStream#reset()}, which repositions the  * stream to the first Token.   *  */
end_comment

begin_class
DECL|class|CachingTokenFilter
specifier|public
class|class
name|CachingTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|cache
specifier|private
name|List
name|cache
decl_stmt|;
DECL|field|iterator
specifier|private
name|Iterator
name|iterator
decl_stmt|;
DECL|method|CachingTokenFilter
specifier|public
name|CachingTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
comment|// fill cache lazily
name|cache
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|fillCache
argument_list|()
expr_stmt|;
name|iterator
operator|=
name|cache
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// the cache is exhausted, return null
return|return
literal|false
return|;
block|}
comment|// Since the TokenFilter can be reset, the tokens need to be preserved as immutable.
name|AttributeSource
name|state
init|=
operator|(
name|AttributeSource
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|state
operator|.
name|restoreState
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** @deprecated */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
comment|// fill cache lazily
name|cache
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|fillCache
argument_list|(
name|reusableToken
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|cache
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// the cache is exhausted, return null
return|return
literal|null
return|;
block|}
comment|// Since the TokenFilter can be reset, the tokens need to be preserved as immutable.
name|Token
name|nextToken
init|=
operator|(
name|Token
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|(
name|Token
operator|)
name|nextToken
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|iterator
operator|=
name|cache
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|fillCache
specifier|private
name|void
name|fillCache
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|cache
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** @deprecated */
DECL|method|fillCache
specifier|private
name|void
name|fillCache
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
init|;
name|nextToken
operator|!=
literal|null
condition|;
name|nextToken
operator|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
control|)
block|{
name|cache
operator|.
name|add
argument_list|(
name|nextToken
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

