begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|Tokenizer
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
name|CharTermAttribute
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

begin_comment
comment|/**  * This tokenizer uses regex pattern matching to construct distinct tokens  * for the input stream.  It takes two arguments:  "pattern" and "group".  *<p/>  *<ul>  *<li>"pattern" is the regular expression.</li>  *<li>"group" says which group to extract into tokens.</li>  *</ul>  *<p>  * group=-1 (the default) is equivalent to "split".  In this case, the tokens will  * be equivalent to the output from (without empty tokens):  * {@link String#split(java.lang.String)}  *</p>  *<p>  * Using group>= 0 selects the matching group as the token.  For example, if you have:<br/>  *<pre>  *  pattern = \'([^\']+)\'  *  group = 0  *  input = aaa 'bbb' 'ccc'  *</pre>  * the output will be two tokens: 'bbb' and 'ccc' (including the ' marks).  With the same input  * but using group=1, the output would be: bbb and ccc (no ' marks)  *</p>  *<p>NOTE: This Tokenizer does not output tokens that are of zero length.</p>  *  * @see Pattern  */
end_comment

begin_class
DECL|class|PatternTokenizer
specifier|public
specifier|final
class|class
name|PatternTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|str
specifier|private
specifier|final
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|int
name|group
decl_stmt|;
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
comment|/** creates a new PatternTokenizer returning tokens from group (-1 for split functionality) */
DECL|method|PatternTokenizer
specifier|public
name|PatternTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|int
name|group
parameter_list|)
block|{
name|this
argument_list|(
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|,
name|input
argument_list|,
name|pattern
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
comment|/** creates a new PatternTokenizer returning tokens from group (-1 for split functionality) */
DECL|method|PatternTokenizer
specifier|public
name|PatternTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|int
name|group
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
comment|// Use "" instead of str so don't consume chars
comment|// (fillBuffer) from the input on throwing IAE below:
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|// confusingly group count depends ENTIRELY on the pattern but is only accessible via matcher
if|if
condition|(
name|group
operator|>=
literal|0
operator|&&
name|group
operator|>
name|matcher
operator|.
name|groupCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid group specified: pattern only has: "
operator|+
name|matcher
operator|.
name|groupCount
argument_list|()
operator|+
literal|" capturing groups"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|index
operator|>=
name|str
operator|.
name|length
argument_list|()
condition|)
return|return
literal|false
return|;
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
name|group
operator|>=
literal|0
condition|)
block|{
comment|// match a specific group
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|index
operator|=
name|matcher
operator|.
name|start
argument_list|(
name|group
argument_list|)
expr_stmt|;
specifier|final
name|int
name|endIndex
init|=
name|matcher
operator|.
name|end
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
name|endIndex
condition|)
continue|continue;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|str
argument_list|,
name|index
argument_list|,
name|endIndex
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|index
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|endIndex
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|index
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// mark exhausted
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// String.split() functionality
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
if|if
condition|(
name|matcher
operator|.
name|start
argument_list|()
operator|-
name|index
operator|>
literal|0
condition|)
block|{
comment|// found a non-zero-length token
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|str
argument_list|,
name|index
argument_list|,
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|index
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|matcher
operator|.
name|end
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|index
operator|=
name|matcher
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|-
name|index
operator|==
literal|0
condition|)
block|{
name|index
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// mark exhausted
return|return
literal|false
return|;
block|}
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|str
argument_list|,
name|index
argument_list|,
name|str
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|index
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// mark exhausted
return|return
literal|true
return|;
block|}
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
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
specifier|final
name|int
name|ofs
init|=
name|correctOffset
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|ofs
argument_list|,
name|ofs
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
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|fillBuffer
argument_list|(
name|str
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|matcher
operator|.
name|reset
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|index
operator|=
literal|0
expr_stmt|;
block|}
comment|// TODO: we should see if we can make this tokenizer work without reading
comment|// the entire document into RAM, perhaps with Matcher.hitEnd/requireEnd ?
DECL|field|buffer
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|8192
index|]
decl_stmt|;
DECL|method|fillBuffer
specifier|private
name|void
name|fillBuffer
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

