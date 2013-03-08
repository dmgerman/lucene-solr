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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|KeywordAttribute
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This TokenFilterÂ emits each incoming token twice once as keyword and once non-keyword, in other words once with  * {@link KeywordAttribute#setKeyword(boolean)} set to<code>true</code> and once set to<code>false</code>.  * This is useful if used with a stem filter that respects the {@link KeywordAttribute} to index the stemmed and the  * un-stemmed version of a term into the same field.  */
end_comment

begin_class
DECL|class|KeywordRepeatFilter
specifier|public
specifier|final
class|class
name|KeywordRepeatFilter
extends|extends
name|TokenFilter
block|{
DECL|field|keywordAttribute
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAttribute
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAttr
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAttr
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
comment|/**    * Construct a token stream filtering the given input.    */
DECL|method|KeywordRepeatFilter
specifier|public
name|KeywordRepeatFilter
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
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|posIncAttr
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|keywordAttribute
operator|.
name|setKeyword
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|state
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|state
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|keywordAttribute
operator|.
name|setKeyword
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
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
name|state
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

