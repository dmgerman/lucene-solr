begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|index
operator|.
name|AtomicReaderContext
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
name|IndexReader
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
name|Term
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
name|TermContext
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
name|Query
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
name|Bits
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Base class for filtering a SpanQuery based on the position of a match.  **/
end_comment

begin_class
DECL|class|SpanPositionCheckQuery
specifier|public
specifier|abstract
class|class
name|SpanPositionCheckQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|match
specifier|protected
name|SpanQuery
name|match
decl_stmt|;
DECL|method|SpanPositionCheckQuery
specifier|public
name|SpanPositionCheckQuery
parameter_list|(
name|SpanQuery
name|match
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
block|}
comment|/**    * @return the SpanQuery whose matches are filtered.    *    * */
DECL|method|getMatch
specifier|public
name|SpanQuery
name|getMatch
parameter_list|()
block|{
return|return
name|match
return|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|match
operator|.
name|getField
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|match
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
comment|/**     * Return value for {@link SpanPositionCheckQuery#acceptPosition(Spans)}.    */
DECL|enum|AcceptStatus
specifier|protected
specifier|static
enum|enum
name|AcceptStatus
block|{
comment|/** Indicates the match should be accepted */
DECL|enum constant|YES
name|YES
block|,
comment|/** Indicates the match should be rejected */
DECL|enum constant|NO
name|NO
block|,
comment|/**       * Indicates the match should be rejected, and the enumeration should advance      * to the next document.      */
DECL|enum constant|NO_AND_ADVANCE
name|NO_AND_ADVANCE
block|}
empty_stmt|;
comment|/**    * Implementing classes are required to return whether the current position is a match for the passed in    * "match" {@link org.apache.lucene.search.spans.SpanQuery}.    *    * This is only called if the underlying {@link org.apache.lucene.search.spans.Spans#next()} for the    * match is successful    *    *    * @param spans The {@link org.apache.lucene.search.spans.Spans} instance, positioned at the spot to check    * @return whether the match is accepted, rejected, or rejected and should move to the next doc.    *    * @see org.apache.lucene.search.spans.Spans#next()    *    */
DECL|method|acceptPosition
specifier|protected
specifier|abstract
name|AcceptStatus
name|acceptPosition
parameter_list|(
name|Spans
name|spans
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PositionCheckSpan
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanPositionCheckQuery
name|clone
init|=
literal|null
decl_stmt|;
name|SpanQuery
name|rewritten
init|=
operator|(
name|SpanQuery
operator|)
name|match
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|match
condition|)
block|{
name|clone
operator|=
operator|(
name|SpanPositionCheckQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|match
operator|=
name|rewritten
expr_stmt|;
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
DECL|class|PositionCheckSpan
specifier|protected
class|class
name|PositionCheckSpan
extends|extends
name|Spans
block|{
DECL|field|spans
specifier|private
name|Spans
name|spans
decl_stmt|;
DECL|method|PositionCheckSpan
specifier|public
name|PositionCheckSpan
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|)
throws|throws
name|IOException
block|{
name|spans
operator|=
name|match
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|spans
operator|.
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
name|doNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|spans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|doNext
argument_list|()
return|;
block|}
DECL|method|doNext
specifier|protected
name|boolean
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
switch|switch
condition|(
name|acceptPosition
argument_list|(
name|this
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
return|return
literal|true
return|;
case|case
name|NO
case|:
if|if
condition|(
operator|!
name|spans
operator|.
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
break|break;
case|case
name|NO_AND_ADVANCE
case|:
if|if
condition|(
operator|!
name|spans
operator|.
name|skipTo
argument_list|(
name|spans
operator|.
name|doc
argument_list|()
operator|+
literal|1
argument_list|)
condition|)
return|return
literal|false
return|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|spans
operator|.
name|doc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|spans
operator|.
name|start
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|spans
operator|.
name|end
argument_list|()
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|spans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|spans
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
comment|//TODO: any way to avoid the new construction?
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|spans
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|spans
operator|.
name|cost
argument_list|()
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
return|return
literal|"spans("
operator|+
name|SpanPositionCheckQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

