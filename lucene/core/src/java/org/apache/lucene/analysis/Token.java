begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|tokenattributes
operator|.
name|CharTermAttributeImpl
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
name|FlagsAttribute
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
name|PayloadAttribute
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
name|TypeAttribute
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
name|DocsAndPositionsEnum
import|;
end_import

begin_comment
comment|// for javadoc
end_comment

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
name|Attribute
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
name|AttributeImpl
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
name|AttributeReflector
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
comment|/**    A Token is an occurrence of a term from the text of a field.  It consists of   a term's text, the start and end offset of the term in the text of the field,   and a type string.<p>   The start and end offsets permit applications to re-associate a token with   its source text, e.g., to display highlighted query terms in a document   browser, or to show matching text fragments in a<abbr title="KeyWord In Context">KWIC</abbr>   display, etc.<p>   The type is a string, assigned by a lexical analyzer   (a.k.a. tokenizer), naming the lexical or syntactic class that the token   belongs to.  For example an end of sentence marker token might be implemented   with type "eos".  The default token type is "word".<p>   A Token can optionally have metadata (a.k.a. payload) in the form of a variable   length byte array. Use {@link DocsAndPositionsEnum#getPayload()} to retrieve the    payloads from the index.<br><br><p><b>NOTE:</b> As of 2.9, Token implements all {@link Attribute} interfaces   that are part of core Lucene and can be found in the {@code tokenattributes} subpackage.   Even though it is not necessary to use Token anymore, with the new TokenStream API it can   be used as convenience class that implements all {@link Attribute}s, which is especially useful   to easily switch from the old to the new TokenStream API.<br><br><p>Tokenizers and TokenFilters should try to re-use a Token   instance when possible for best performance, by   implementing the {@link TokenStream#incrementToken()} API.   Failing that, to create a new Token you should first use   one of the constructors that starts with null text.  To load   the token from a char[] use {@link #copyBuffer(char[], int, int)}.   To load from a String use {@link #setEmpty} followed by {@link #append(CharSequence)} or {@link #append(CharSequence, int, int)}.   Alternatively you can get the Token's termBuffer by calling either {@link #buffer()},   if you know that your text is shorter than the capacity of the termBuffer   or {@link #resizeBuffer(int)}, if there is any possibility   that you may need to grow the buffer. Fill in the characters of your term into this   buffer, with {@link String#getChars(int, int, char[], int)} if loading from a string,   or with {@link System#arraycopy(Object, int, Object, int, int)}, and finally call {@link #setLength(int)} to   set the length of the term text.  See<a target="_top"   href="https://issues.apache.org/jira/browse/LUCENE-969">LUCENE-969</a>   for details.</p><p>Typical Token reuse patterns:<ul><li> Copying text from a string (type is reset to {@link #DEFAULT_TYPE} if not specified):<br/><pre>     return reusableToken.reinit(string, startOffset, endOffset[, type]);</pre></li><li> Copying some text from a string (type is reset to {@link #DEFAULT_TYPE} if not specified):<br/><pre>     return reusableToken.reinit(string, 0, string.length(), startOffset, endOffset[, type]);</pre></li></li><li> Copying text from char[] buffer (type is reset to {@link #DEFAULT_TYPE} if not specified):<br/><pre>     return reusableToken.reinit(buffer, 0, buffer.length, startOffset, endOffset[, type]);</pre></li><li> Copying some text from a char[] buffer (type is reset to {@link #DEFAULT_TYPE} if not specified):<br/><pre>     return reusableToken.reinit(buffer, start, end - start, startOffset, endOffset[, type]);</pre></li><li> Copying from one one Token to another (type is reset to {@link #DEFAULT_TYPE} if not specified):<br/><pre>     return reusableToken.reinit(source.buffer(), 0, source.length(), source.startOffset(), source.endOffset()[, source.type()]);</pre></li></ul>   A few things to note:<ul><li>clear() initializes all of the fields to default values. This was changed in contrast to Lucene 2.4, but should affect no one.</li><li>Because<code>TokenStreams</code> can be chained, one cannot assume that the<code>Token's</code> current type is correct.</li><li>The startOffset and endOffset represent the start and offset in the source text, so be careful in adjusting them.</li><li>When caching a reusable token, clone it. When injecting a cached token into a stream that can be reset, clone it again.</li></ul></p><p><b>Please note:</b> With Lucene 3.1, the<code>{@linkplain #toString toString()}</code> method had to be changed to match the   {@link CharSequence} interface introduced by the interface {@link org.apache.lucene.analysis.tokenattributes.CharTermAttribute}.   This method now only prints the term text, no additional information anymore.</p> */
end_comment

begin_class
DECL|class|Token
specifier|public
class|class
name|Token
extends|extends
name|CharTermAttributeImpl
implements|implements
name|TypeAttribute
implements|,
name|PositionIncrementAttribute
implements|,
name|FlagsAttribute
implements|,
name|OffsetAttribute
implements|,
name|PayloadAttribute
implements|,
name|PositionLengthAttribute
block|{
DECL|field|startOffset
DECL|field|endOffset
specifier|private
name|int
name|startOffset
decl_stmt|,
name|endOffset
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
init|=
name|DEFAULT_TYPE
decl_stmt|;
DECL|field|flags
specifier|private
name|int
name|flags
decl_stmt|;
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
DECL|field|positionIncrement
specifier|private
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
DECL|field|positionLength
specifier|private
name|int
name|positionLength
init|=
literal|1
decl_stmt|;
comment|/** Constructs a Token will null text. */
DECL|method|Token
specifier|public
name|Token
parameter_list|()
block|{   }
comment|/** Constructs a Token with null text and start& end    *  offsets.    *  @param start start offset in the source text    *  @param end end offset in the source text */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
block|}
comment|/** Constructs a Token with null text and start& end    *  offsets plus the Token type.    *  @param start start offset in the source text    *  @param end end offset in the source text    *  @param typ the lexical type of this Token */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|String
name|typ
parameter_list|)
block|{
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
name|type
operator|=
name|typ
expr_stmt|;
block|}
comment|/**    * Constructs a Token with null text and start& end    *  offsets plus flags. NOTE: flags is EXPERIMENTAL.    *  @param start start offset in the source text    *  @param end end offset in the source text    *  @param flags The bits to set for this token    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
comment|/** Constructs a Token with the given term text, and start    *& end offsets.  The type defaults to "word."    *<b>NOTE:</b> for better indexing speed you should    *  instead use the char[] termBuffer methods to set the    *  term text.    *  @param text term text    *  @param start start offset    *  @param end end offset    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
block|}
comment|/** Constructs a Token with the given text, start and end    *  offsets,& type.<b>NOTE:</b> for better indexing    *  speed you should instead use the char[] termBuffer    *  methods to set the term text.    *  @param text term text    *  @param start start offset    *  @param end end offset    *  @param typ token type    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|String
name|typ
parameter_list|)
block|{
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
name|type
operator|=
name|typ
expr_stmt|;
block|}
comment|/**    *  Constructs a Token with the given text, start and end    *  offsets,& type.<b>NOTE:</b> for better indexing    *  speed you should instead use the char[] termBuffer    *  methods to set the term text.    * @param text    * @param start    * @param end    * @param flags token type bits    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
comment|/**    *  Constructs a Token with the given term buffer (offset    *& length), start and end    *  offsets    * @param startTermBuffer    * @param termBufferOffset    * @param termBufferLength    * @param start    * @param end    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|char
index|[]
name|startTermBuffer
parameter_list|,
name|int
name|termBufferOffset
parameter_list|,
name|int
name|termBufferLength
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|copyBuffer
argument_list|(
name|startTermBuffer
argument_list|,
name|termBufferOffset
argument_list|,
name|termBufferLength
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
block|}
comment|/** Set the position increment.  This determines the position of this token    * relative to the previous Token in a {@link TokenStream}, used in phrase    * searching.    *    *<p>The default value is one.    *    *<p>Some common uses for this are:<ul>    *    *<li>Set it to zero to put multiple terms in the same position.  This is    * useful if, e.g., a word has multiple stems.  Searches for phrases    * including either stem will match.  In this case, all but the first stem's    * increment should be set to zero: the increment of the first instance    * should be one.  Repeating a token with an increment of zero can also be    * used to boost the scores of matches on that token.    *    *<li>Set it to values greater than one to inhibit exact phrase matches.    * If, for example, one does not want phrases to match across removed stop    * words, then one could build a stop word filter that removes stop words and    * also sets the increment to the number of stop words removed before each    * non-stop word.  Then exact phrase queries will only match when the terms    * occur with no intervening stop words.    *    *</ul>    * @param positionIncrement the distance from the prior term    * @see org.apache.lucene.index.DocsAndPositionsEnum    */
DECL|method|setPositionIncrement
specifier|public
name|void
name|setPositionIncrement
parameter_list|(
name|int
name|positionIncrement
parameter_list|)
block|{
if|if
condition|(
name|positionIncrement
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Increment must be zero or greater: "
operator|+
name|positionIncrement
argument_list|)
throw|;
name|this
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
block|}
comment|/** Returns the position increment of this Token.    * @see #setPositionIncrement    */
DECL|method|getPositionIncrement
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
name|positionIncrement
return|;
block|}
comment|/** Set the position length.    * @see PositionLengthAttribute */
annotation|@
name|Override
DECL|method|setPositionLength
specifier|public
name|void
name|setPositionLength
parameter_list|(
name|int
name|positionLength
parameter_list|)
block|{
name|this
operator|.
name|positionLength
operator|=
name|positionLength
expr_stmt|;
block|}
comment|/** Get the position length.    * @see PositionLengthAttribute */
annotation|@
name|Override
DECL|method|getPositionLength
specifier|public
name|int
name|getPositionLength
parameter_list|()
block|{
return|return
name|positionLength
return|;
block|}
comment|/** Returns this Token's starting offset, the position of the first character     corresponding to this token in the source text.      Note that the difference between endOffset() and startOffset() may not be     equal to {@link #length}, as the term text may have been altered by a     stemmer or some other filter. */
DECL|method|startOffset
specifier|public
specifier|final
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/** Set the starting offset.       @see #startOffset() */
DECL|method|setStartOffset
specifier|public
name|void
name|setStartOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|offset
expr_stmt|;
block|}
comment|/** Returns this Token's ending offset, one greater than the position of the     last character corresponding to this token in the source text. The length     of the token in the source text is (endOffset - startOffset). */
DECL|method|endOffset
specifier|public
specifier|final
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/** Set the ending offset.       @see #endOffset() */
DECL|method|setEndOffset
specifier|public
name|void
name|setEndOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|endOffset
operator|=
name|offset
expr_stmt|;
block|}
comment|/** Set the starting and ending offset.   @see #startOffset() and #endOffset()*/
DECL|method|setOffset
specifier|public
name|void
name|setOffset
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
comment|/** Returns this Token's lexical type.  Defaults to "word". */
DECL|method|type
specifier|public
specifier|final
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/** Set the lexical type.       @see #type() */
DECL|method|setType
specifier|public
specifier|final
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    *<p/>    *    * Get the bitset for any bits that have been set.  This is completely distinct from {@link #type()}, although they do share similar purposes.    * The flags can be used to encode information about the token for use by other {@link org.apache.lucene.analysis.TokenFilter}s.    *    *     * @return The bits    * @lucene.experimental While we think this is here to stay, we may want to change it to be a long.    */
DECL|method|getFlags
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
return|;
block|}
comment|/**    * @see #getFlags()    */
DECL|method|setFlags
specifier|public
name|void
name|setFlags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
comment|/**    * Returns this Token's payload.    */
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
return|return
name|this
operator|.
name|payload
return|;
block|}
comment|/**     * Sets this Token's payload.    */
DECL|method|setPayload
specifier|public
name|void
name|setPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
block|}
comment|/** Resets the term text, payload, flags, and positionIncrement,    * startOffset, endOffset and token type to default.    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
name|positionIncrement
operator|=
literal|1
expr_stmt|;
name|flags
operator|=
literal|0
expr_stmt|;
name|startOffset
operator|=
name|endOffset
operator|=
literal|0
expr_stmt|;
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Token
name|clone
parameter_list|()
block|{
name|Token
name|t
init|=
operator|(
name|Token
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Do a deep clone
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|payload
operator|=
name|payload
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
comment|/** Makes a clone, but replaces the term buffer&    * start/end offset in the process.  This is more    * efficient than doing a full clone (and then calling    * {@link #copyBuffer}) because it saves a wasted copy of the old    * termBuffer. */
DECL|method|clone
specifier|public
name|Token
name|clone
parameter_list|(
name|char
index|[]
name|newTermBuffer
parameter_list|,
name|int
name|newTermOffset
parameter_list|,
name|int
name|newTermLength
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|)
block|{
specifier|final
name|Token
name|t
init|=
operator|new
name|Token
argument_list|(
name|newTermBuffer
argument_list|,
name|newTermOffset
argument_list|,
name|newTermLength
argument_list|,
name|newStartOffset
argument_list|,
name|newEndOffset
argument_list|)
decl_stmt|;
name|t
operator|.
name|positionIncrement
operator|=
name|positionIncrement
expr_stmt|;
name|t
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|t
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
name|t
operator|.
name|payload
operator|=
name|payload
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|t
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|instanceof
name|Token
condition|)
block|{
specifier|final
name|Token
name|other
init|=
operator|(
name|Token
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|startOffset
operator|==
name|other
operator|.
name|startOffset
operator|&&
name|endOffset
operator|==
name|other
operator|.
name|endOffset
operator|&&
name|flags
operator|==
name|other
operator|.
name|flags
operator|&&
name|positionIncrement
operator|==
name|other
operator|.
name|positionIncrement
operator|&&
operator|(
name|type
operator|==
literal|null
condition|?
name|other
operator|.
name|type
operator|==
literal|null
else|:
name|type
operator|.
name|equals
argument_list|(
name|other
operator|.
name|type
argument_list|)
operator|)
operator|&&
operator|(
name|payload
operator|==
literal|null
condition|?
name|other
operator|.
name|payload
operator|==
literal|null
else|:
name|payload
operator|.
name|equals
argument_list|(
name|other
operator|.
name|payload
argument_list|)
operator|)
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|)
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|code
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|startOffset
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|endOffset
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|flags
expr_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|positionIncrement
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|type
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|payload
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|code
return|;
block|}
comment|// like clear() but doesn't clear termBuffer/text
DECL|method|clearNoTermBuffer
specifier|private
name|void
name|clearNoTermBuffer
parameter_list|()
block|{
name|payload
operator|=
literal|null
expr_stmt|;
name|positionIncrement
operator|=
literal|1
expr_stmt|;
name|flags
operator|=
literal|0
expr_stmt|;
name|startOffset
operator|=
name|endOffset
operator|=
literal|0
expr_stmt|;
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
block|}
comment|/** Shorthand for calling {@link #clear},    *  {@link #copyBuffer(char[], int, int)},    *  {@link #setStartOffset},    *  {@link #setEndOffset},    *  {@link #setType}    *  @return this Token instance */
DECL|method|reinit
specifier|public
name|Token
name|reinit
parameter_list|(
name|char
index|[]
name|newTermBuffer
parameter_list|,
name|int
name|newTermOffset
parameter_list|,
name|int
name|newTermLength
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|,
name|String
name|newType
parameter_list|)
block|{
name|clearNoTermBuffer
argument_list|()
expr_stmt|;
name|copyBuffer
argument_list|(
name|newTermBuffer
argument_list|,
name|newTermOffset
argument_list|,
name|newTermLength
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
name|positionIncrement
operator|=
literal|1
expr_stmt|;
name|startOffset
operator|=
name|newStartOffset
expr_stmt|;
name|endOffset
operator|=
name|newEndOffset
expr_stmt|;
name|type
operator|=
name|newType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Shorthand for calling {@link #clear},    *  {@link #copyBuffer(char[], int, int)},    *  {@link #setStartOffset},    *  {@link #setEndOffset}    *  {@link #setType} on Token.DEFAULT_TYPE    *  @return this Token instance */
DECL|method|reinit
specifier|public
name|Token
name|reinit
parameter_list|(
name|char
index|[]
name|newTermBuffer
parameter_list|,
name|int
name|newTermOffset
parameter_list|,
name|int
name|newTermLength
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|)
block|{
name|clearNoTermBuffer
argument_list|()
expr_stmt|;
name|copyBuffer
argument_list|(
name|newTermBuffer
argument_list|,
name|newTermOffset
argument_list|,
name|newTermLength
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|newStartOffset
expr_stmt|;
name|endOffset
operator|=
name|newEndOffset
expr_stmt|;
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Shorthand for calling {@link #clear},    *  {@link #append(CharSequence)},    *  {@link #setStartOffset},    *  {@link #setEndOffset}    *  {@link #setType}    *  @return this Token instance */
DECL|method|reinit
specifier|public
name|Token
name|reinit
parameter_list|(
name|String
name|newTerm
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|,
name|String
name|newType
parameter_list|)
block|{
name|clear
argument_list|()
expr_stmt|;
name|append
argument_list|(
name|newTerm
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|newStartOffset
expr_stmt|;
name|endOffset
operator|=
name|newEndOffset
expr_stmt|;
name|type
operator|=
name|newType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Shorthand for calling {@link #clear},    *  {@link #append(CharSequence, int, int)},    *  {@link #setStartOffset},    *  {@link #setEndOffset}    *  {@link #setType}    *  @return this Token instance */
DECL|method|reinit
specifier|public
name|Token
name|reinit
parameter_list|(
name|String
name|newTerm
parameter_list|,
name|int
name|newTermOffset
parameter_list|,
name|int
name|newTermLength
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|,
name|String
name|newType
parameter_list|)
block|{
name|clear
argument_list|()
expr_stmt|;
name|append
argument_list|(
name|newTerm
argument_list|,
name|newTermOffset
argument_list|,
name|newTermOffset
operator|+
name|newTermLength
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|newStartOffset
expr_stmt|;
name|endOffset
operator|=
name|newEndOffset
expr_stmt|;
name|type
operator|=
name|newType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Shorthand for calling {@link #clear},    *  {@link #append(CharSequence)},    *  {@link #setStartOffset},    *  {@link #setEndOffset}    *  {@link #setType} on Token.DEFAULT_TYPE    *  @return this Token instance */
DECL|method|reinit
specifier|public
name|Token
name|reinit
parameter_list|(
name|String
name|newTerm
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|)
block|{
name|clear
argument_list|()
expr_stmt|;
name|append
argument_list|(
name|newTerm
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|newStartOffset
expr_stmt|;
name|endOffset
operator|=
name|newEndOffset
expr_stmt|;
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Shorthand for calling {@link #clear},    *  {@link #append(CharSequence, int, int)},    *  {@link #setStartOffset},    *  {@link #setEndOffset}    *  {@link #setType} on Token.DEFAULT_TYPE    *  @return this Token instance */
DECL|method|reinit
specifier|public
name|Token
name|reinit
parameter_list|(
name|String
name|newTerm
parameter_list|,
name|int
name|newTermOffset
parameter_list|,
name|int
name|newTermLength
parameter_list|,
name|int
name|newStartOffset
parameter_list|,
name|int
name|newEndOffset
parameter_list|)
block|{
name|clear
argument_list|()
expr_stmt|;
name|append
argument_list|(
name|newTerm
argument_list|,
name|newTermOffset
argument_list|,
name|newTermOffset
operator|+
name|newTermLength
argument_list|)
expr_stmt|;
name|startOffset
operator|=
name|newStartOffset
expr_stmt|;
name|endOffset
operator|=
name|newEndOffset
expr_stmt|;
name|type
operator|=
name|DEFAULT_TYPE
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Copy the prototype token's fields into this one. Note: Payloads are shared.    * @param prototype    */
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|Token
name|prototype
parameter_list|)
block|{
name|copyBuffer
argument_list|(
name|prototype
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|prototype
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|positionIncrement
operator|=
name|prototype
operator|.
name|positionIncrement
expr_stmt|;
name|flags
operator|=
name|prototype
operator|.
name|flags
expr_stmt|;
name|startOffset
operator|=
name|prototype
operator|.
name|startOffset
expr_stmt|;
name|endOffset
operator|=
name|prototype
operator|.
name|endOffset
expr_stmt|;
name|type
operator|=
name|prototype
operator|.
name|type
expr_stmt|;
name|payload
operator|=
name|prototype
operator|.
name|payload
expr_stmt|;
block|}
comment|/**    * Copy the prototype token's fields into this one, with a different term. Note: Payloads are shared.    * @param prototype    * @param newTerm    */
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|Token
name|prototype
parameter_list|,
name|String
name|newTerm
parameter_list|)
block|{
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|newTerm
argument_list|)
expr_stmt|;
name|positionIncrement
operator|=
name|prototype
operator|.
name|positionIncrement
expr_stmt|;
name|flags
operator|=
name|prototype
operator|.
name|flags
expr_stmt|;
name|startOffset
operator|=
name|prototype
operator|.
name|startOffset
expr_stmt|;
name|endOffset
operator|=
name|prototype
operator|.
name|endOffset
expr_stmt|;
name|type
operator|=
name|prototype
operator|.
name|type
expr_stmt|;
name|payload
operator|=
name|prototype
operator|.
name|payload
expr_stmt|;
block|}
comment|/**    * Copy the prototype token's fields into this one, with a different term. Note: Payloads are shared.    * @param prototype    * @param newTermBuffer    * @param offset    * @param length    */
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|(
name|Token
name|prototype
parameter_list|,
name|char
index|[]
name|newTermBuffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|copyBuffer
argument_list|(
name|newTermBuffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|positionIncrement
operator|=
name|prototype
operator|.
name|positionIncrement
expr_stmt|;
name|flags
operator|=
name|prototype
operator|.
name|flags
expr_stmt|;
name|startOffset
operator|=
name|prototype
operator|.
name|startOffset
expr_stmt|;
name|endOffset
operator|=
name|prototype
operator|.
name|endOffset
expr_stmt|;
name|type
operator|=
name|prototype
operator|.
name|type
expr_stmt|;
name|payload
operator|=
name|prototype
operator|.
name|payload
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|instanceof
name|Token
condition|)
block|{
specifier|final
name|Token
name|to
init|=
operator|(
name|Token
operator|)
name|target
decl_stmt|;
name|to
operator|.
name|reinit
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// reinit shares the payload, so clone it:
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|to
operator|.
name|payload
operator|=
name|payload
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|copyTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
operator|(
operator|(
name|OffsetAttribute
operator|)
name|target
operator|)
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PositionIncrementAttribute
operator|)
name|target
operator|)
operator|.
name|setPositionIncrement
argument_list|(
name|positionIncrement
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PayloadAttribute
operator|)
name|target
operator|)
operator|.
name|setPayload
argument_list|(
operator|(
name|payload
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|payload
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|FlagsAttribute
operator|)
name|target
operator|)
operator|.
name|setFlags
argument_list|(
name|flags
argument_list|)
expr_stmt|;
operator|(
operator|(
name|TypeAttribute
operator|)
name|target
operator|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reflectWith
specifier|public
name|void
name|reflectWith
parameter_list|(
name|AttributeReflector
name|reflector
parameter_list|)
block|{
name|super
operator|.
name|reflectWith
argument_list|(
name|reflector
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|,
literal|"startOffset"
argument_list|,
name|startOffset
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|,
literal|"endOffset"
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|,
literal|"positionIncrement"
argument_list|,
name|positionIncrement
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|,
literal|"payload"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|,
literal|"flags"
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|reflector
operator|.
name|reflect
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|,
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/** Convenience factory that returns<code>Token</code> as implementation for the basic    * attributes and return the default impl (with&quot;Impl&quot; appended) for all other    * attributes.    * @since 3.0    */
DECL|field|TOKEN_ATTRIBUTE_FACTORY
specifier|public
specifier|static
specifier|final
name|AttributeSource
operator|.
name|AttributeFactory
name|TOKEN_ATTRIBUTE_FACTORY
init|=
operator|new
name|TokenAttributeFactory
argument_list|(
name|AttributeSource
operator|.
name|AttributeFactory
operator|.
name|DEFAULT_ATTRIBUTE_FACTORY
argument_list|)
decl_stmt|;
comment|/**<b>Expert:</b> Creates a TokenAttributeFactory returning {@link Token} as instance for the basic attributes    * and for all other attributes calls the given delegate factory.    * @since 3.0    */
DECL|class|TokenAttributeFactory
specifier|public
specifier|static
specifier|final
class|class
name|TokenAttributeFactory
extends|extends
name|AttributeSource
operator|.
name|AttributeFactory
block|{
DECL|field|delegate
specifier|private
specifier|final
name|AttributeSource
operator|.
name|AttributeFactory
name|delegate
decl_stmt|;
comment|/**<b>Expert</b>: Creates an AttributeFactory returning {@link Token} as instance for the basic attributes      * and for all other attributes calls the given delegate factory. */
DECL|method|TokenAttributeFactory
specifier|public
name|TokenAttributeFactory
parameter_list|(
name|AttributeSource
operator|.
name|AttributeFactory
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAttributeInstance
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
return|return
name|attClass
operator|.
name|isAssignableFrom
argument_list|(
name|Token
operator|.
name|class
argument_list|)
condition|?
operator|new
name|Token
argument_list|()
else|:
name|delegate
operator|.
name|createAttributeInstance
argument_list|(
name|attClass
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|other
operator|instanceof
name|TokenAttributeFactory
condition|)
block|{
specifier|final
name|TokenAttributeFactory
name|af
init|=
operator|(
name|TokenAttributeFactory
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|delegate
operator|.
name|equals
argument_list|(
name|af
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x0a45aa31
return|;
block|}
block|}
block|}
end_class

end_unit

