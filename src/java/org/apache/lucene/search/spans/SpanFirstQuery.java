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
name|Collection
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
name|ToStringUtils
import|;
end_import

begin_comment
comment|/** Matches spans near the beginning of a field. */
end_comment

begin_class
DECL|class|SpanFirstQuery
specifier|public
class|class
name|SpanFirstQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|match
specifier|private
name|SpanQuery
name|match
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
comment|/** Construct a SpanFirstQuery matching spans in<code>match</code> whose end    * position is less than or equal to<code>end</code>. */
DECL|method|SpanFirstQuery
specifier|public
name|SpanFirstQuery
parameter_list|(
name|SpanQuery
name|match
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
comment|/** Return the SpanQuery whose matches are filtered. */
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
comment|/** Return the maximum end position permitted in a match. */
DECL|method|getEnd
specifier|public
name|int
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
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
comment|/** Returns a collection of all terms matched by this query.    * @deprecated use extractTerms instead    * @see #extractTerms(Set)    */
DECL|method|getTerms
specifier|public
name|Collection
name|getTerms
parameter_list|()
block|{
return|return
name|match
operator|.
name|getTerms
argument_list|()
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanFirst("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|match
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|SpanFirstQuery
name|spanFirstQuery
init|=
operator|new
name|SpanFirstQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|match
operator|.
name|clone
argument_list|()
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|spanFirstQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spanFirstQuery
return|;
block|}
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
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
DECL|method|getPayloadSpans
specifier|public
name|PayloadSpans
name|getPayloadSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|PayloadSpans
operator|)
name|getSpans
argument_list|(
name|reader
argument_list|)
return|;
block|}
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PayloadSpans
argument_list|()
block|{
specifier|private
name|PayloadSpans
name|spans
init|=
name|match
operator|.
name|getPayloadSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|spans
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// scan to next match
if|if
condition|(
name|end
argument_list|()
operator|<=
name|end
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
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
name|spans
operator|.
name|end
argument_list|()
operator|<=
name|end
operator|||
name|next
argument_list|()
return|;
block|}
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
specifier|public
name|Collection
comment|/*<byte[]>*/
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
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
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
name|spans
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|SpanFirstQuery
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
return|;
block|}
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
name|SpanFirstQuery
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
name|SpanFirstQuery
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
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SpanFirstQuery
operator|)
condition|)
return|return
literal|false
return|;
name|SpanFirstQuery
name|other
init|=
operator|(
name|SpanFirstQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|end
operator|==
name|other
operator|.
name|end
operator|&&
name|this
operator|.
name|match
operator|.
name|equals
argument_list|(
name|other
operator|.
name|match
argument_list|)
operator|&&
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|match
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|8
operator|)
operator||
operator|(
name|h
operator|>>>
literal|25
operator|)
expr_stmt|;
comment|// reversible
name|h
operator|^=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|^
name|end
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

