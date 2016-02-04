begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
operator|.
name|Toffs
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

begin_comment
comment|/**  * FieldFragList has a list of "frag info" that is used by FragmentsBuilder class  * to create fragments (snippets).  */
end_comment

begin_class
DECL|class|FieldFragList
specifier|public
specifier|abstract
class|class
name|FieldFragList
block|{
DECL|field|fragInfos
specifier|private
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|fragInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * a constructor.    *     * @param fragCharSize the length (number of chars) of a fragment    */
DECL|method|FieldFragList
specifier|public
name|FieldFragList
parameter_list|(
name|int
name|fragCharSize
parameter_list|)
block|{   }
comment|/**    * convert the list of WeightedPhraseInfo to WeightedFragInfo, then add it to the fragInfos    *     * @param startOffset start offset of the fragment    * @param endOffset end offset of the fragment    * @param phraseInfoList list of WeightedPhraseInfo objects    */
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|phraseInfoList
parameter_list|)
function_decl|;
comment|/**    * return the list of WeightedFragInfos.    *     * @return fragInfos.    */
DECL|method|getFragInfos
specifier|public
name|List
argument_list|<
name|WeightedFragInfo
argument_list|>
name|getFragInfos
parameter_list|()
block|{
return|return
name|fragInfos
return|;
block|}
comment|/**    * List of term offsets + weight for a frag info    */
DECL|class|WeightedFragInfo
specifier|public
specifier|static
class|class
name|WeightedFragInfo
block|{
DECL|field|subInfos
specifier|private
name|List
argument_list|<
name|SubInfo
argument_list|>
name|subInfos
decl_stmt|;
DECL|field|totalBoost
specifier|private
name|float
name|totalBoost
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|int
name|endOffset
decl_stmt|;
DECL|method|WeightedFragInfo
specifier|public
name|WeightedFragInfo
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|List
argument_list|<
name|SubInfo
argument_list|>
name|subInfos
parameter_list|,
name|float
name|totalBoost
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|this
operator|.
name|totalBoost
operator|=
name|totalBoost
expr_stmt|;
name|this
operator|.
name|subInfos
operator|=
name|subInfos
expr_stmt|;
block|}
DECL|method|getSubInfos
specifier|public
name|List
argument_list|<
name|SubInfo
argument_list|>
name|getSubInfos
parameter_list|()
block|{
return|return
name|subInfos
return|;
block|}
DECL|method|getTotalBoost
specifier|public
name|float
name|getTotalBoost
parameter_list|()
block|{
return|return
name|totalBoost
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"subInfos=("
argument_list|)
expr_stmt|;
for|for
control|(
name|SubInfo
name|si
range|:
name|subInfos
control|)
name|sb
operator|.
name|append
argument_list|(
name|si
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")/"
argument_list|)
operator|.
name|append
argument_list|(
name|totalBoost
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|startOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|endOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Represents the list of term offsets for some text      */
DECL|class|SubInfo
specifier|public
specifier|static
class|class
name|SubInfo
block|{
DECL|field|text
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
comment|// unnecessary member, just exists for debugging purpose
DECL|field|termsOffsets
specifier|private
specifier|final
name|List
argument_list|<
name|Toffs
argument_list|>
name|termsOffsets
decl_stmt|;
comment|// usually termsOffsets.size() == 1,
comment|// but if position-gap> 1 and slop> 0 then size() could be greater than 1
DECL|field|seqnum
specifier|private
specifier|final
name|int
name|seqnum
decl_stmt|;
DECL|field|boost
specifier|private
specifier|final
name|float
name|boost
decl_stmt|;
comment|// used for scoring split WeightedPhraseInfos.
DECL|method|SubInfo
specifier|public
name|SubInfo
parameter_list|(
name|String
name|text
parameter_list|,
name|List
argument_list|<
name|Toffs
argument_list|>
name|termsOffsets
parameter_list|,
name|int
name|seqnum
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|termsOffsets
operator|=
name|termsOffsets
expr_stmt|;
name|this
operator|.
name|seqnum
operator|=
name|seqnum
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
DECL|method|getTermsOffsets
specifier|public
name|List
argument_list|<
name|Toffs
argument_list|>
name|getTermsOffsets
parameter_list|()
block|{
return|return
name|termsOffsets
return|;
block|}
DECL|method|getSeqnum
specifier|public
name|int
name|getSeqnum
parameter_list|()
block|{
return|return
name|seqnum
return|;
block|}
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|text
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|Toffs
name|to
range|:
name|termsOffsets
control|)
name|sb
operator|.
name|append
argument_list|(
name|to
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

