begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|OffsetAttribute
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
name|index
operator|.
name|DocsAndPositionsEnum
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
name|Terms
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
name|TermsEnum
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
name|CollectionUtil
import|;
end_import

begin_class
DECL|class|TokenStreamFromTermPositionVector
specifier|public
specifier|final
class|class
name|TokenStreamFromTermPositionVector
extends|extends
name|TokenStream
block|{
DECL|field|positionedTokens
specifier|private
specifier|final
name|List
argument_list|<
name|Token
argument_list|>
name|positionedTokens
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|tokensAtCurrentPosition
specifier|private
name|Iterator
argument_list|<
name|Token
argument_list|>
name|tokensAtCurrentPosition
decl_stmt|;
DECL|field|termAttribute
specifier|private
name|CharTermAttribute
name|termAttribute
decl_stmt|;
DECL|field|positionIncrementAttribute
specifier|private
name|PositionIncrementAttribute
name|positionIncrementAttribute
decl_stmt|;
DECL|field|offsetAttribute
specifier|private
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
comment|/**    * Constructor.    *     * @param vector Terms that contains the data for    *        creating the TokenStream. Must have positions and offsets.    */
DECL|method|TokenStreamFromTermPositionVector
specifier|public
name|TokenStreamFromTermPositionVector
parameter_list|(
specifier|final
name|Terms
name|vector
parameter_list|)
throws|throws
name|IOException
block|{
name|termAttribute
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|vector
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|text
decl_stmt|;
name|DocsAndPositionsEnum
name|dpEnum
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|dpEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|dpEnum
argument_list|)
expr_stmt|;
name|dpEnum
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
specifier|final
name|int
name|freq
init|=
name|dpEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
specifier|final
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
if|if
condition|(
name|dpEnum
operator|.
name|attributes
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|offsetAtt
operator|=
name|dpEnum
operator|.
name|attributes
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offsetAtt
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
name|int
name|pos
init|=
name|dpEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|offsetAtt
operator|!=
literal|null
condition|)
block|{
name|token
operator|=
operator|new
name|Token
argument_list|(
name|text
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|token
operator|=
operator|new
name|Token
argument_list|()
expr_stmt|;
name|token
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|text
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Yes - this is the position, not the increment! This is for
comment|// sorting. This value
comment|// will be corrected before use.
name|token
operator|.
name|setPositionIncrement
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|positionedTokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
name|CollectionUtil
operator|.
name|mergeSort
argument_list|(
name|this
operator|.
name|positionedTokens
argument_list|,
name|tokenComparator
argument_list|)
expr_stmt|;
name|int
name|lastPosition
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|Token
name|token
range|:
name|this
operator|.
name|positionedTokens
control|)
block|{
name|int
name|thisPosition
init|=
name|token
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
name|thisPosition
operator|-
name|lastPosition
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|thisPosition
expr_stmt|;
block|}
name|this
operator|.
name|tokensAtCurrentPosition
operator|=
name|this
operator|.
name|positionedTokens
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|field|tokenComparator
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Token
argument_list|>
name|tokenComparator
init|=
operator|new
name|Comparator
argument_list|<
name|Token
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Token
name|o1
parameter_list|,
specifier|final
name|Token
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getPositionIncrement
argument_list|()
operator|-
name|o2
operator|.
name|getPositionIncrement
argument_list|()
return|;
block|}
block|}
decl_stmt|;
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
name|this
operator|.
name|tokensAtCurrentPosition
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Token
name|next
init|=
name|this
operator|.
name|tokensAtCurrentPosition
operator|.
name|next
argument_list|()
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAttribute
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|positionIncrementAttribute
operator|.
name|setPositionIncrement
argument_list|(
name|next
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAttribute
operator|.
name|setOffset
argument_list|(
name|next
operator|.
name|startOffset
argument_list|()
argument_list|,
name|next
operator|.
name|endOffset
argument_list|()
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
name|this
operator|.
name|tokensAtCurrentPosition
operator|=
name|this
operator|.
name|positionedTokens
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

