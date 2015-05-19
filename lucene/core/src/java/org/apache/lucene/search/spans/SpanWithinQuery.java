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
name|LeafReaderContext
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
name|Map
import|;
end_import

begin_comment
comment|/** Keep matches that are contained within another Spans. */
end_comment

begin_class
DECL|class|SpanWithinQuery
specifier|public
class|class
name|SpanWithinQuery
extends|extends
name|SpanContainQuery
block|{
comment|/** Construct a SpanWithinQuery matching spans from<code>little</code>    * that are inside of<code>big</code>.    * This query has the boost of<code>little</code>.    *<code>big</code> and<code>little</code> must be in the same field.    */
DECL|method|SpanWithinQuery
specifier|public
name|SpanWithinQuery
parameter_list|(
name|SpanQuery
name|big
parameter_list|,
name|SpanQuery
name|little
parameter_list|)
block|{
name|super
argument_list|(
name|big
argument_list|,
name|little
argument_list|,
name|little
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|toString
argument_list|(
name|field
argument_list|,
literal|"SpanWithin"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanWithinQuery
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SpanWithinQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|big
operator|.
name|clone
argument_list|()
argument_list|,
operator|(
name|SpanQuery
operator|)
name|little
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
comment|/**     * Return spans from<code>little</code> that are contained in a spans from<code>big</code>.    * The payload is from the spans of<code>little</code>.    */
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
parameter_list|,
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|Spans
argument_list|>
name|containerContained
init|=
name|prepareConjunction
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|,
name|collector
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerContained
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Spans
name|big
init|=
name|containerContained
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Spans
name|little
init|=
name|containerContained
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|ContainSpans
argument_list|(
name|big
argument_list|,
name|little
argument_list|,
name|little
argument_list|)
block|{
annotation|@
name|Override
name|boolean
name|twoPhaseCurrentDocMatches
parameter_list|()
throws|throws
name|IOException
block|{
name|oneExhaustedInCurrentDoc
operator|=
literal|false
expr_stmt|;
assert|assert
name|littleSpans
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
assert|;
while|while
condition|(
name|littleSpans
operator|.
name|nextStartPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
condition|)
block|{
while|while
condition|(
name|bigSpans
operator|.
name|endPosition
argument_list|()
operator|<
name|littleSpans
operator|.
name|endPosition
argument_list|()
condition|)
block|{
if|if
condition|(
name|bigSpans
operator|.
name|nextStartPosition
argument_list|()
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
name|oneExhaustedInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|bigSpans
operator|.
name|startPosition
argument_list|()
operator|<=
name|littleSpans
operator|.
name|startPosition
argument_list|()
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|oneExhaustedInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|littleSpans
operator|.
name|startPosition
argument_list|()
return|;
block|}
while|while
condition|(
name|littleSpans
operator|.
name|nextStartPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
condition|)
block|{
while|while
condition|(
name|bigSpans
operator|.
name|endPosition
argument_list|()
operator|<
name|littleSpans
operator|.
name|endPosition
argument_list|()
condition|)
block|{
if|if
condition|(
name|bigSpans
operator|.
name|nextStartPosition
argument_list|()
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
name|oneExhaustedInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
name|NO_MORE_POSITIONS
return|;
block|}
block|}
if|if
condition|(
name|bigSpans
operator|.
name|startPosition
argument_list|()
operator|<=
name|littleSpans
operator|.
name|startPosition
argument_list|()
condition|)
block|{
return|return
name|littleSpans
operator|.
name|startPosition
argument_list|()
return|;
block|}
block|}
name|oneExhaustedInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
name|NO_MORE_POSITIONS
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

