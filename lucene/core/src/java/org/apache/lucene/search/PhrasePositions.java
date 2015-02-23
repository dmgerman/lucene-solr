begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Position of a term in a document that takes into account the term offset within the phrase.   */
end_comment

begin_class
DECL|class|PhrasePositions
specifier|final
class|class
name|PhrasePositions
block|{
DECL|field|position
name|int
name|position
decl_stmt|;
comment|// position in doc
DECL|field|count
name|int
name|count
decl_stmt|;
comment|// remaining pos in this doc
DECL|field|offset
name|int
name|offset
decl_stmt|;
comment|// position in phrase
DECL|field|ord
specifier|final
name|int
name|ord
decl_stmt|;
comment|// unique across all PhrasePositions instances
DECL|field|postings
specifier|final
name|PostingsEnum
name|postings
decl_stmt|;
comment|// stream of docs& positions
DECL|field|next
name|PhrasePositions
name|next
decl_stmt|;
comment|// used to make lists
DECL|field|rptGroup
name|int
name|rptGroup
init|=
operator|-
literal|1
decl_stmt|;
comment|//>=0 indicates that this is a repeating PP
DECL|field|rptInd
name|int
name|rptInd
decl_stmt|;
comment|// index in the rptGroup
DECL|field|terms
specifier|final
name|Term
index|[]
name|terms
decl_stmt|;
comment|// for repetitions initialization
DECL|method|PhrasePositions
name|PhrasePositions
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|int
name|o
parameter_list|,
name|int
name|ord
parameter_list|,
name|Term
index|[]
name|terms
parameter_list|)
block|{
name|this
operator|.
name|postings
operator|=
name|postings
expr_stmt|;
name|offset
operator|=
name|o
expr_stmt|;
name|this
operator|.
name|ord
operator|=
name|ord
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
block|}
DECL|method|firstPosition
specifier|final
name|void
name|firstPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|count
operator|=
name|postings
operator|.
name|freq
argument_list|()
expr_stmt|;
comment|// read first pos
name|nextPosition
argument_list|()
expr_stmt|;
block|}
comment|/**    * Go to next location of this term current document, and set     *<code>position</code> as<code>location - offset</code>, so that a     * matching exact phrase is easily identified when all PhrasePositions     * have exactly the same<code>position</code>.    */
DECL|method|nextPosition
specifier|final
name|boolean
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|--
operator|>
literal|0
condition|)
block|{
comment|// read subsequent pos's
name|position
operator|=
name|postings
operator|.
name|nextPosition
argument_list|()
operator|-
name|offset
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/** for debug purposes */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
literal|"o:"
operator|+
name|offset
operator|+
literal|" p:"
operator|+
name|position
operator|+
literal|" c:"
operator|+
name|count
decl_stmt|;
if|if
condition|(
name|rptGroup
operator|>=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" rpt:"
operator|+
name|rptGroup
operator|+
literal|",i"
operator|+
name|rptInd
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

