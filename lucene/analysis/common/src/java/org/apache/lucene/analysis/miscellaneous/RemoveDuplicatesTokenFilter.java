begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TokenFilter
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|util
operator|.
name|CharArraySet
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
name|Version
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
comment|/**  * A TokenFilter which filters out Tokens at the same position and Term text as the previous token in the stream.  */
end_comment

begin_class
DECL|class|RemoveDuplicatesTokenFilter
specifier|public
specifier|final
class|class
name|RemoveDuplicatesTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAttribute
specifier|private
specifier|final
name|CharTermAttribute
name|termAttribute
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAttribute
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// use a fixed version, as we don't care about case sensitivity.
DECL|field|previous
specifier|private
specifier|final
name|CharArraySet
name|previous
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_50
argument_list|,
literal|8
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Creates a new RemoveDuplicatesTokenFilter    *    * @param in TokenStream that will be filtered    */
DECL|method|RemoveDuplicatesTokenFilter
specifier|public
name|RemoveDuplicatesTokenFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
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
specifier|final
name|char
name|term
index|[]
init|=
name|termAttribute
operator|.
name|buffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|termAttribute
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|posIncrement
init|=
name|posIncAttribute
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|posIncrement
operator|>
literal|0
condition|)
block|{
name|previous
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|boolean
name|duplicate
init|=
operator|(
name|posIncrement
operator|==
literal|0
operator|&&
name|previous
operator|.
name|contains
argument_list|(
name|term
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
operator|)
decl_stmt|;
comment|// clone the term, and add to the set of seen terms.
name|char
name|saved
index|[]
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|term
argument_list|,
literal|0
argument_list|,
name|saved
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|previous
operator|.
name|add
argument_list|(
name|saved
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|duplicate
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|previous
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

