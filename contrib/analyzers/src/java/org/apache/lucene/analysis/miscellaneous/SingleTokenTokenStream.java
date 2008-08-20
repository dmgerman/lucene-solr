begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A token stream containing a single token.  */
end_comment

begin_class
DECL|class|SingleTokenTokenStream
specifier|public
class|class
name|SingleTokenTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
init|=
literal|false
decl_stmt|;
comment|// The token needs to be immutable, so work with clones!
DECL|field|token
specifier|private
name|Token
name|token
decl_stmt|;
DECL|method|SingleTokenTokenStream
specifier|public
name|SingleTokenTokenStream
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
assert|assert
name|token
operator|!=
literal|null
assert|;
name|this
operator|.
name|token
operator|=
operator|(
name|Token
operator|)
name|token
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
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
name|exhausted
condition|)
block|{
return|return
literal|null
return|;
block|}
name|exhausted
operator|=
literal|true
expr_stmt|;
return|return
operator|(
name|Token
operator|)
name|token
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
name|exhausted
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|getToken
specifier|public
name|Token
name|getToken
parameter_list|()
block|{
return|return
operator|(
name|Token
operator|)
name|token
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|setToken
specifier|public
name|void
name|setToken
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
operator|(
name|Token
operator|)
name|token
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

