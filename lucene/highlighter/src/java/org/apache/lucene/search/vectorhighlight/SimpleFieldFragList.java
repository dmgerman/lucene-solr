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
name|FieldFragList
operator|.
name|WeightedFragInfo
operator|.
name|SubInfo
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
comment|/**  * A simple implementation of {@link FieldFragList}.  */
end_comment

begin_class
DECL|class|SimpleFieldFragList
specifier|public
class|class
name|SimpleFieldFragList
extends|extends
name|FieldFragList
block|{
comment|/**    * a constructor.    *     * @param fragCharSize the length (number of chars) of a fragment    */
DECL|method|SimpleFieldFragList
specifier|public
name|SimpleFieldFragList
parameter_list|(
name|int
name|fragCharSize
parameter_list|)
block|{
name|super
argument_list|(
name|fragCharSize
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.vectorhighlight.FieldFragList#add( int startOffset, int endOffset, List<WeightedPhraseInfo> phraseInfoList )    */
annotation|@
name|Override
DECL|method|add
specifier|public
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
block|{
name|float
name|totalBoost
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|SubInfo
argument_list|>
name|subInfos
init|=
operator|new
name|ArrayList
argument_list|<
name|SubInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|WeightedPhraseInfo
name|phraseInfo
range|:
name|phraseInfoList
control|)
block|{
name|subInfos
operator|.
name|add
argument_list|(
operator|new
name|SubInfo
argument_list|(
name|phraseInfo
operator|.
name|getText
argument_list|()
argument_list|,
name|phraseInfo
operator|.
name|getTermsOffsets
argument_list|()
argument_list|,
name|phraseInfo
operator|.
name|getSeqnum
argument_list|()
argument_list|,
name|phraseInfo
operator|.
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|totalBoost
operator|+=
name|phraseInfo
operator|.
name|getBoost
argument_list|()
expr_stmt|;
block|}
name|getFragInfos
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|WeightedFragInfo
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|,
name|subInfos
argument_list|,
name|totalBoost
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

