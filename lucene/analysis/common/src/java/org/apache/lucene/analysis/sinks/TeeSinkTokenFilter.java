begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
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
name|analysis
operator|.
name|TokenFilter
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
name|util
operator|.
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * This TokenFilter provides the ability to set aside attribute states that have already been analyzed. This is useful  * in situations where multiple fields share many common analysis steps and then go their separate ways.  *   *<p>  * It is also useful for doing things like entity extraction or proper noun analysis as part of the analysis workflow  * and saving off those tokens for use in another field.  *</p>  *  *<pre class="prettyprint">  * TeeSinkTokenFilter source1 = new TeeSinkTokenFilter(new WhitespaceTokenizer());  * TeeSinkTokenFilter.SinkTokenStream sink1 = source1.newSinkTokenStream();  * TeeSinkTokenFilter.SinkTokenStream sink2 = source1.newSinkTokenStream();  *  * TokenStream final1 = new LowerCaseFilter(source1);  * TokenStream final2 = new EntityDetect(sink1);  * TokenStream final3 = new URLDetect(sink2);  *  * d.add(new TextField("f1", final1));  * d.add(new TextField("f2", final2));  * d.add(new TextField("f3", final3));  *</pre>  *   *<p>  * In this example, {@code sink1} and {@code sink2} will both get tokens from {@code source1} after whitespace  * tokenization, and will further do additional token filtering, e.g. detect entities and URLs.  *</p>  *   *<p>  *<b>NOTE</b>: it is important, that tees are consumed before sinks, therefore you should add them to the document  * before the sinks. In the above example,<i>f1</i> is added before the other fields, and so by the time they are  * processed, it has already been consumed, which is the correct way to index the three streams. If for some reason you  * cannot ensure that, you should call {@link #consumeAllTokens()} before adding the sinks to document fields.  */
end_comment

begin_class
DECL|class|TeeSinkTokenFilter
specifier|public
specifier|final
class|class
name|TeeSinkTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|cachedStates
specifier|private
specifier|final
name|States
name|cachedStates
init|=
operator|new
name|States
argument_list|()
decl_stmt|;
DECL|method|TeeSinkTokenFilter
specifier|public
name|TeeSinkTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a new {@link SinkTokenStream} that receives all tokens consumed by this stream. */
DECL|method|newSinkTokenStream
specifier|public
name|TokenStream
name|newSinkTokenStream
parameter_list|()
block|{
return|return
operator|new
name|SinkTokenStream
argument_list|(
name|this
operator|.
name|cloneAttributes
argument_list|()
argument_list|,
name|cachedStates
argument_list|)
return|;
block|}
comment|/**    *<code>TeeSinkTokenFilter</code> passes all tokens to the added sinks when itself is consumed. To be sure that all    * tokens from the input stream are passed to the sinks, you can call this methods. This instance is exhausted after    * this method returns, but all sinks are instant available.    */
DECL|method|consumeAllTokens
specifier|public
name|void
name|consumeAllTokens
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|incrementToken
argument_list|()
condition|)
block|{}
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|cachedStates
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
name|cachedStates
operator|.
name|setFinalState
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|cachedStates
operator|.
name|reset
argument_list|()
expr_stmt|;
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/** TokenStream output from a tee. */
DECL|class|SinkTokenStream
specifier|public
specifier|static
specifier|final
class|class
name|SinkTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|cachedStates
specifier|private
specifier|final
name|States
name|cachedStates
decl_stmt|;
DECL|field|it
specifier|private
name|Iterator
argument_list|<
name|AttributeSource
operator|.
name|State
argument_list|>
name|it
init|=
literal|null
decl_stmt|;
DECL|method|SinkTokenStream
specifier|private
name|SinkTokenStream
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|States
name|cachedStates
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|cachedStates
operator|=
name|cachedStates
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AttributeSource
operator|.
name|State
name|state
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|State
name|finalState
init|=
name|cachedStates
operator|.
name|getFinalState
argument_list|()
decl_stmt|;
if|if
condition|(
name|finalState
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|finalState
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{
name|it
operator|=
name|cachedStates
operator|.
name|getStates
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** A convenience wrapper for storing the cached states as well the final state of the stream. */
DECL|class|States
specifier|private
specifier|static
specifier|final
class|class
name|States
block|{
DECL|field|states
specifier|private
specifier|final
name|List
argument_list|<
name|State
argument_list|>
name|states
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|finalState
specifier|private
name|State
name|finalState
decl_stmt|;
DECL|method|States
specifier|public
name|States
parameter_list|()
block|{}
DECL|method|setFinalState
name|void
name|setFinalState
parameter_list|(
name|State
name|finalState
parameter_list|)
block|{
name|this
operator|.
name|finalState
operator|=
name|finalState
expr_stmt|;
block|}
DECL|method|getFinalState
name|State
name|getFinalState
parameter_list|()
block|{
return|return
name|finalState
return|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|State
name|state
parameter_list|)
block|{
name|states
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|getStates
name|Iterator
argument_list|<
name|State
argument_list|>
name|getStates
parameter_list|()
block|{
return|return
name|states
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|finalState
operator|=
literal|null
expr_stmt|;
name|states
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

