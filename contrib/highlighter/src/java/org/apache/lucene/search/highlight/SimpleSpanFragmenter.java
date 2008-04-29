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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * {@link Fragmenter} implementation which breaks text up into same-size  * fragments but does not split up Spans. This is a simple sample class.  */
end_comment

begin_class
DECL|class|SimpleSpanFragmenter
specifier|public
class|class
name|SimpleSpanFragmenter
implements|implements
name|Fragmenter
block|{
DECL|field|DEFAULT_FRAGMENT_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_FRAGMENT_SIZE
init|=
literal|100
decl_stmt|;
DECL|field|fragmentSize
specifier|private
name|int
name|fragmentSize
decl_stmt|;
DECL|field|currentNumFrags
specifier|private
name|int
name|currentNumFrags
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|spanScorer
specifier|private
name|SpanScorer
name|spanScorer
decl_stmt|;
DECL|field|waitForPos
specifier|private
name|int
name|waitForPos
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * @param spanscorer SpanScorer that was used to score hits    */
DECL|method|SimpleSpanFragmenter
specifier|public
name|SimpleSpanFragmenter
parameter_list|(
name|SpanScorer
name|spanscorer
parameter_list|)
block|{
name|this
argument_list|(
name|spanscorer
argument_list|,
name|DEFAULT_FRAGMENT_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param spanscorer SpanScorer that was used to score hits    * @param fragmentSize size in bytes of each fragment    */
DECL|method|SimpleSpanFragmenter
specifier|public
name|SimpleSpanFragmenter
parameter_list|(
name|SpanScorer
name|spanscorer
parameter_list|,
name|int
name|fragmentSize
parameter_list|)
block|{
name|this
operator|.
name|fragmentSize
operator|=
name|fragmentSize
expr_stmt|;
name|this
operator|.
name|spanScorer
operator|=
name|spanscorer
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.Fragmenter#isNewFragment(org.apache.lucene.analysis.Token)    */
DECL|method|isNewFragment
specifier|public
name|boolean
name|isNewFragment
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|position
operator|+=
name|token
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
if|if
condition|(
name|waitForPos
operator|==
name|position
condition|)
block|{
name|waitForPos
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|waitForPos
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
literal|false
return|;
block|}
name|WeightedSpanTerm
name|wSpanTerm
init|=
name|spanScorer
operator|.
name|getWeightedSpanTerm
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|wSpanTerm
operator|!=
literal|null
condition|)
block|{
name|List
name|positionSpans
init|=
name|wSpanTerm
operator|.
name|getPositionSpans
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|positionSpans
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|PositionSpan
operator|)
name|positionSpans
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|start
operator|==
name|position
condition|)
block|{
name|waitForPos
operator|=
operator|(
operator|(
name|PositionSpan
operator|)
name|positionSpans
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|end
operator|+
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
name|boolean
name|isNewFrag
init|=
name|token
operator|.
name|endOffset
argument_list|()
operator|>=
operator|(
name|fragmentSize
operator|*
name|currentNumFrags
operator|)
decl_stmt|;
if|if
condition|(
name|isNewFrag
condition|)
block|{
name|currentNumFrags
operator|++
expr_stmt|;
block|}
return|return
name|isNewFrag
return|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.Fragmenter#start(java.lang.String)    */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|String
name|originalText
parameter_list|)
block|{
name|position
operator|=
literal|0
expr_stmt|;
name|currentNumFrags
operator|=
literal|1
expr_stmt|;
block|}
block|}
end_class

end_unit

