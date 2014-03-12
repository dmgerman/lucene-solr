begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
import|;
end_import

begin_comment
comment|/**  * A abstract implementation of {@link FragListBuilder}.  */
end_comment

begin_class
DECL|class|BaseFragListBuilder
specifier|public
specifier|abstract
class|class
name|BaseFragListBuilder
implements|implements
name|FragListBuilder
block|{
DECL|field|MARGIN_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MARGIN_DEFAULT
init|=
literal|6
decl_stmt|;
DECL|field|MIN_FRAG_CHAR_SIZE_FACTOR
specifier|public
specifier|static
specifier|final
name|int
name|MIN_FRAG_CHAR_SIZE_FACTOR
init|=
literal|3
decl_stmt|;
DECL|field|margin
specifier|final
name|int
name|margin
decl_stmt|;
DECL|field|minFragCharSize
specifier|final
name|int
name|minFragCharSize
decl_stmt|;
DECL|method|BaseFragListBuilder
specifier|public
name|BaseFragListBuilder
parameter_list|(
name|int
name|margin
parameter_list|)
block|{
if|if
condition|(
name|margin
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"margin("
operator|+
name|margin
operator|+
literal|") is too small. It must be 0 or higher."
argument_list|)
throw|;
name|this
operator|.
name|margin
operator|=
name|margin
expr_stmt|;
name|this
operator|.
name|minFragCharSize
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|margin
operator|*
name|MIN_FRAG_CHAR_SIZE_FACTOR
argument_list|)
expr_stmt|;
block|}
DECL|method|BaseFragListBuilder
specifier|public
name|BaseFragListBuilder
parameter_list|()
block|{
name|this
argument_list|(
name|MARGIN_DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|createFieldFragList
specifier|protected
name|FieldFragList
name|createFieldFragList
parameter_list|(
name|FieldPhraseList
name|fieldPhraseList
parameter_list|,
name|FieldFragList
name|fieldFragList
parameter_list|,
name|int
name|fragCharSize
parameter_list|)
block|{
if|if
condition|(
name|fragCharSize
operator|<
name|minFragCharSize
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fragCharSize("
operator|+
name|fragCharSize
operator|+
literal|") is too small. It must be "
operator|+
name|minFragCharSize
operator|+
literal|" or higher."
argument_list|)
throw|;
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|wpil
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|IteratorQueue
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|queue
init|=
operator|new
name|IteratorQueue
argument_list|<>
argument_list|(
name|fieldPhraseList
operator|.
name|getPhraseList
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
name|WeightedPhraseInfo
name|phraseInfo
init|=
literal|null
decl_stmt|;
name|int
name|startOffset
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|phraseInfo
operator|=
name|queue
operator|.
name|top
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// if the phrase violates the border of previous fragment, discard it and try next phrase
if|if
condition|(
name|phraseInfo
operator|.
name|getStartOffset
argument_list|()
operator|<
name|startOffset
condition|)
block|{
name|queue
operator|.
name|removeTop
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|wpil
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|int
name|currentPhraseStartOffset
init|=
name|phraseInfo
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
name|int
name|currentPhraseEndOffset
init|=
name|phraseInfo
operator|.
name|getEndOffset
argument_list|()
decl_stmt|;
name|int
name|spanStart
init|=
name|Math
operator|.
name|max
argument_list|(
name|currentPhraseStartOffset
operator|-
name|margin
argument_list|,
name|startOffset
argument_list|)
decl_stmt|;
name|int
name|spanEnd
init|=
name|Math
operator|.
name|max
argument_list|(
name|currentPhraseEndOffset
argument_list|,
name|spanStart
operator|+
name|fragCharSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|acceptPhrase
argument_list|(
name|queue
operator|.
name|removeTop
argument_list|()
argument_list|,
name|currentPhraseEndOffset
operator|-
name|currentPhraseStartOffset
argument_list|,
name|fragCharSize
argument_list|)
condition|)
block|{
name|wpil
operator|.
name|add
argument_list|(
name|phraseInfo
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|phraseInfo
operator|=
name|queue
operator|.
name|top
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// pull until we crossed the current spanEnd
if|if
condition|(
name|phraseInfo
operator|.
name|getEndOffset
argument_list|()
operator|<=
name|spanEnd
condition|)
block|{
name|currentPhraseEndOffset
operator|=
name|phraseInfo
operator|.
name|getEndOffset
argument_list|()
expr_stmt|;
if|if
condition|(
name|acceptPhrase
argument_list|(
name|queue
operator|.
name|removeTop
argument_list|()
argument_list|,
name|currentPhraseEndOffset
operator|-
name|currentPhraseStartOffset
argument_list|,
name|fragCharSize
argument_list|)
condition|)
block|{
name|wpil
operator|.
name|add
argument_list|(
name|phraseInfo
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|wpil
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
specifier|final
name|int
name|matchLen
init|=
name|currentPhraseEndOffset
operator|-
name|currentPhraseStartOffset
decl_stmt|;
comment|// now recalculate the start and end position to "center" the result
specifier|final
name|int
name|newMargin
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
operator|(
name|fragCharSize
operator|-
name|matchLen
operator|)
operator|/
literal|2
argument_list|)
decl_stmt|;
comment|// matchLen can be> fragCharSize prevent IAOOB here
name|spanStart
operator|=
name|currentPhraseStartOffset
operator|-
name|newMargin
expr_stmt|;
if|if
condition|(
name|spanStart
operator|<
name|startOffset
condition|)
block|{
name|spanStart
operator|=
name|startOffset
expr_stmt|;
block|}
comment|// whatever is bigger here we grow this out
name|spanEnd
operator|=
name|spanStart
operator|+
name|Math
operator|.
name|max
argument_list|(
name|matchLen
argument_list|,
name|fragCharSize
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|spanEnd
expr_stmt|;
name|fieldFragList
operator|.
name|add
argument_list|(
name|spanStart
argument_list|,
name|spanEnd
argument_list|,
name|wpil
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldFragList
return|;
block|}
comment|/**    * A predicate to decide if the given {@link WeightedPhraseInfo} should be    * accepted as a highlighted phrase or if it should be discarded.    *<p>    * The default implementation discards phrases that are composed of more than one term    * and where the matchLength exceeds the fragment character size.    *     * @param info the phrase info to accept    * @param matchLength the match length of the current phrase    * @param fragCharSize the configured fragment character size    * @return<code>true</code> if this phrase info should be accepted as a highligh phrase    */
DECL|method|acceptPhrase
specifier|protected
name|boolean
name|acceptPhrase
parameter_list|(
name|WeightedPhraseInfo
name|info
parameter_list|,
name|int
name|matchLength
parameter_list|,
name|int
name|fragCharSize
parameter_list|)
block|{
return|return
name|info
operator|.
name|getTermsOffsets
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
literal|1
operator|||
name|matchLength
operator|<=
name|fragCharSize
return|;
block|}
DECL|class|IteratorQueue
specifier|private
specifier|static
specifier|final
class|class
name|IteratorQueue
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|iter
specifier|private
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|iter
decl_stmt|;
DECL|field|top
specifier|private
name|T
name|top
decl_stmt|;
DECL|method|IteratorQueue
specifier|public
name|IteratorQueue
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iter
parameter_list|)
block|{
name|this
operator|.
name|iter
operator|=
name|iter
expr_stmt|;
name|T
name|removeTop
init|=
name|removeTop
argument_list|()
decl_stmt|;
assert|assert
name|removeTop
operator|==
literal|null
assert|;
block|}
DECL|method|top
specifier|public
name|T
name|top
parameter_list|()
block|{
return|return
name|top
return|;
block|}
DECL|method|removeTop
specifier|public
name|T
name|removeTop
parameter_list|()
block|{
name|T
name|currentTop
init|=
name|top
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|top
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|top
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|currentTop
return|;
block|}
block|}
block|}
end_class

end_unit

