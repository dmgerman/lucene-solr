begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
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
name|TermToBytesRefAttribute
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

begin_comment
comment|/** Consumes a TokenStream and creates an {@link TermAutomatonQuery}  *  where the transition labels are tokens from the {@link  *  TermToBytesRefAttribute}.  *  *<p>This code is very new and likely has exciting bugs!  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|TokenStreamToTermAutomatonQuery
specifier|public
class|class
name|TokenStreamToTermAutomatonQuery
block|{
DECL|field|preservePositionIncrements
specifier|private
name|boolean
name|preservePositionIncrements
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|TokenStreamToTermAutomatonQuery
specifier|public
name|TokenStreamToTermAutomatonQuery
parameter_list|()
block|{
name|this
operator|.
name|preservePositionIncrements
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Whether to generate holes in the automaton for missing positions,<code>true</code> by default. */
DECL|method|setPreservePositionIncrements
specifier|public
name|void
name|setPreservePositionIncrements
parameter_list|(
name|boolean
name|enablePositionIncrements
parameter_list|)
block|{
name|this
operator|.
name|preservePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
block|}
comment|/** Pulls the graph (including {@link    *  PositionLengthAttribute}) from the provided {@link    *  TokenStream}, and creates the corresponding    *  automaton where arcs are bytes (or Unicode code points     *  if unicodeArcs = true) from each term. */
DECL|method|toQuery
specifier|public
name|TermAutomatonQuery
name|toQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|TokenStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermToBytesRefAttribute
name|termBytesAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|PositionLengthAttribute
name|posLengthAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|TermAutomatonQuery
name|query
init|=
operator|new
name|TermAutomatonQuery
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
name|int
name|maxOffset
init|=
literal|0
decl_stmt|;
name|int
name|maxPos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|state
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|posInc
init|=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|preservePositionIncrements
operator|==
literal|false
operator|&&
name|posInc
operator|>
literal|1
condition|)
block|{
name|posInc
operator|=
literal|1
expr_stmt|;
block|}
assert|assert
name|pos
operator|>
operator|-
literal|1
operator|||
name|posInc
operator|>
literal|0
assert|;
if|if
condition|(
name|posInc
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot handle holes; to accept any term, use '*' term"
argument_list|)
throw|;
block|}
if|if
condition|(
name|posInc
operator|>
literal|0
condition|)
block|{
comment|// New node:
name|pos
operator|+=
name|posInc
expr_stmt|;
block|}
name|int
name|endPos
init|=
name|pos
operator|+
name|posLengthAtt
operator|.
name|getPositionLength
argument_list|()
decl_stmt|;
while|while
condition|(
name|state
operator|<
name|endPos
condition|)
block|{
name|state
operator|=
name|query
operator|.
name|createState
argument_list|()
expr_stmt|;
block|}
name|BytesRef
name|term
init|=
name|termBytesAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
comment|//System.out.println(pos + "-" + endPos + ": " + term.utf8ToString() + ": posInc=" + posInc);
if|if
condition|(
name|term
operator|.
name|length
operator|==
literal|1
operator|&&
name|term
operator|.
name|bytes
index|[
name|term
operator|.
name|offset
index|]
operator|==
operator|(
name|byte
operator|)
literal|'*'
condition|)
block|{
name|query
operator|.
name|addAnyTransition
argument_list|(
name|pos
argument_list|,
name|endPos
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|.
name|addTransition
argument_list|(
name|pos
argument_list|,
name|endPos
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|maxOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxOffset
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|maxPos
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxPos
argument_list|,
name|endPos
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// TODO: look at endOffset?  ts2a did...
comment|// TODO: this (setting "last" state as the only accept state) may be too simplistic?
name|query
operator|.
name|setAccept
argument_list|(
name|state
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

