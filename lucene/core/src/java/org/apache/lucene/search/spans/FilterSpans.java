begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Objects
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
name|TwoPhaseIterator
import|;
end_import

begin_comment
comment|/**  * A {@link Spans} implementation wrapping another spans instance,  * allowing to filter spans matches easily by implementing {@link #accept}  */
end_comment

begin_class
DECL|class|FilterSpans
specifier|public
specifier|abstract
class|class
name|FilterSpans
extends|extends
name|Spans
block|{
comment|/** The wrapped spans instance. */
DECL|field|in
specifier|protected
specifier|final
name|Spans
name|in
decl_stmt|;
DECL|field|atFirstInCurrentDoc
specifier|private
name|boolean
name|atFirstInCurrentDoc
init|=
literal|false
decl_stmt|;
DECL|field|startPos
specifier|private
name|int
name|startPos
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Wrap the given {@link Spans}. */
DECL|method|FilterSpans
specifier|protected
name|FilterSpans
parameter_list|(
name|Spans
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**     * Returns YES if the candidate should be an accepted match,    * NO if it should not, and NO_MORE_IN_CURRENT_DOC if iteration    * should move on to the next document.    */
DECL|method|accept
specifier|protected
specifier|abstract
name|AcceptStatus
name|accept
parameter_list|(
name|Spans
name|candidate
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|doc
init|=
name|in
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
elseif|else
if|if
condition|(
name|twoPhaseCurrentDocMatches
argument_list|()
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
while|while
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|twoPhaseCurrentDocMatches
argument_list|()
condition|)
block|{
break|break;
block|}
name|doc
operator|=
name|in
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextStartPosition
specifier|public
specifier|final
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|atFirstInCurrentDoc
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|false
expr_stmt|;
return|return
name|startPos
return|;
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|startPos
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
return|return
name|NO_MORE_POSITIONS
return|;
block|}
switch|switch
condition|(
name|accept
argument_list|(
name|in
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
return|return
name|startPos
return|;
case|case
name|NO
case|:
break|break;
case|case
name|NO_MORE_IN_CURRENT_DOC
case|:
return|return
name|startPos
operator|=
name|NO_MORE_POSITIONS
return|;
comment|// startPos ahead for the current doc.
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
specifier|final
name|int
name|startPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|startPos
return|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
specifier|final
name|int
name|endPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
operator|(
name|startPos
operator|!=
name|NO_MORE_POSITIONS
operator|)
condition|?
name|in
operator|.
name|endPosition
argument_list|()
else|:
name|NO_MORE_POSITIONS
return|;
block|}
annotation|@
name|Override
DECL|method|width
specifier|public
name|int
name|width
parameter_list|()
block|{
return|return
name|in
operator|.
name|width
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|collect
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
specifier|final
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
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
literal|"Filter("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
specifier|final
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
name|TwoPhaseIterator
name|inner
init|=
name|in
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|inner
operator|!=
literal|null
condition|)
block|{
comment|// wrapped instance has an approximation
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|inner
operator|.
name|approximation
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|inner
operator|.
name|matches
argument_list|()
operator|&&
name|twoPhaseCurrentDocMatches
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|inner
operator|.
name|matchCost
argument_list|()
return|;
comment|// underestimate
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FilterSpans@asTwoPhaseIterator(inner="
operator|+
name|inner
operator|+
literal|", in="
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
else|else
block|{
comment|// wrapped instance has no approximation, but
comment|// we can still defer matching until absolutely needed.
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|in
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|twoPhaseCurrentDocMatches
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|in
operator|.
name|positionsCost
argument_list|()
return|;
comment|// overestimate
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FilterSpans@asTwoPhaseIterator(in="
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|positionsCost
specifier|public
name|float
name|positionsCost
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// asTwoPhaseIterator never returns null
block|}
comment|/**    * Returns true if the current document matches.    *<p>    * This is called during two-phase processing.    */
comment|// return true if the current document matches
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|twoPhaseCurrentDocMatches
specifier|private
specifier|final
name|boolean
name|twoPhaseCurrentDocMatches
parameter_list|()
throws|throws
name|IOException
block|{
name|atFirstInCurrentDoc
operator|=
literal|false
expr_stmt|;
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
assert|assert
name|startPos
operator|!=
name|NO_MORE_POSITIONS
assert|;
for|for
control|(
init|;
condition|;
control|)
block|{
switch|switch
condition|(
name|accept
argument_list|(
name|in
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
case|case
name|NO
case|:
name|startPos
operator|=
name|in
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|startPos
operator|!=
name|NO_MORE_POSITIONS
condition|)
block|{
break|break;
block|}
comment|// else fallthrough
case|case
name|NO_MORE_IN_CURRENT_DOC
case|:
name|startPos
operator|=
operator|-
literal|1
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
comment|/**    * Status returned from {@link FilterSpans#accept(Spans)} that indicates    * whether a candidate match should be accepted, rejected, or rejected    * and move on to the next document.    */
DECL|enum|AcceptStatus
specifier|public
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
comment|/**      * Indicates the match should be rejected, and the enumeration may continue      * with the next document.      */
DECL|enum constant|NO_MORE_IN_CURRENT_DOC
name|NO_MORE_IN_CURRENT_DOC
block|}
empty_stmt|;
block|}
end_class

end_unit

