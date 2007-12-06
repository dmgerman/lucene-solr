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
name|index
operator|.
name|Payload
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
name|TermPositions
import|;
end_import

begin_comment
comment|/** A Token is an occurence of a term from the text of a field.  It consists of   a term's text, the start and end offset of the term in the text of the field,   and a type string.<p>   The start and end offsets permit applications to re-associate a token with   its source text, e.g., to display highlighted query terms in a document   browser, or to show matching text fragments in a KWIC (KeyWord In Context)   display, etc.<p>   The type is an interned string, assigned by a lexical analyzer   (a.k.a. tokenizer), naming the lexical or syntactic class that the token   belongs to.  For example an end of sentence marker token might be implemented   with type "eos".  The default token type is "word".<p>   A Token can optionally have metadata (a.k.a. Payload) in the form of a variable   length byte array. Use {@link TermPositions#getPayloadLength()} and    {@link TermPositions#getPayload(byte[], int)} to retrieve the payloads from the index.<br><br><p><font color="#FF0000">   WARNING: The status of the<b>Payloads</b> feature is experimental.    The APIs introduced here might change in the future and will not be    supported anymore in such a case.</font><br><br><p><b>NOTE:</b> As of 2.3, Token stores the term text   internally as a malleable char[] termBuffer instead of   String termText.  The indexing code and core tokenizers   have been changed re-use a single Token instance, changing   its buffer and other fields in-place as the Token is   processed.  This provides substantially better indexing   performance as it saves the GC cost of new'ing a Token and   String for every term.  The APIs that accept String   termText are still available but a warning about the   associated performance cost has been added (below).  The   {@link #termText()} method has been deprecated.</p><p>Tokenizers and filters should try to re-use a Token   instance when possible for best performance, by   implementing the {@link TokenStream#next(Token)} API.   Failing that, to create a new Token you should first use   one of the constructors that starts with null text.  Then   you should call either {@link #termBuffer()} or {@link   #resizeTermBuffer(int)} to retrieve the Token's   termBuffer.  Fill in the characters of your term into this   buffer, and finally call {@link #setTermLength(int)} to   set the length of the term text.  See<a target="_top"   href="https://issues.apache.org/jira/browse/LUCENE-969">LUCENE-969</a>   for details.</p>    @see org.apache.lucene.index.Payload */
end_comment

begin_class
DECL|class|Token
specifier|public
class|class
name|Token
implements|implements
name|Cloneable
block|{
DECL|field|DEFAULT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TYPE
init|=
literal|"word"
decl_stmt|;
DECL|field|MIN_BUFFER_SIZE
specifier|private
specifier|static
name|int
name|MIN_BUFFER_SIZE
init|=
literal|10
decl_stmt|;
comment|/** @deprecated: we will remove this when we remove the    * deprecated APIs */
DECL|field|termText
specifier|private
name|String
name|termText
decl_stmt|;
DECL|field|termBuffer
name|char
index|[]
name|termBuffer
decl_stmt|;
comment|// characters for the term text
DECL|field|termLength
name|int
name|termLength
decl_stmt|;
comment|// length of term text in buffer
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
comment|// start in source text
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
comment|// end in source text
DECL|field|type
name|String
name|type
init|=
name|DEFAULT_TYPE
decl_stmt|;
comment|// lexical type
DECL|field|payload
name|Payload
name|payload
decl_stmt|;
DECL|field|positionIncrement
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
comment|/** Constructs a Token will null text. */
DECL|method|Token
specifier|public
name|Token
parameter_list|()
block|{   }
comment|/** Constructs a Token with null text and start& end    *  offsets.    *  @param start start offset    *  @param end end offset */
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
comment|/** Constructs a Token with null text and start& end    *  offsets plus the Token type.    *  @param start start offset    *  @param end end offset */
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
comment|/** Constructs a Token with the given term text, and start    *& end offsets.  The type defaults to "word."    *<b>NOTE:</b> for better indexing speed you should    *  instead use the char[] termBuffer methods to set the    *  term text.    *  @param text term text    *  @param start start offset    *  @param end end offset */
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
name|termText
operator|=
name|text
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
comment|/** Constructs a Token with the given text, start and end    *  offsets,& type.<b>NOTE:</b> for better indexing    *  speed you should instead use the char[] termBuffer    *  methods to set the term text.    *  @param text term text    *  @param start start offset    *  @param end end offset    *  @param typ token type */
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
name|termText
operator|=
name|text
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
comment|/** Set the position increment.  This determines the position of this token    * relative to the previous Token in a {@link TokenStream}, used in phrase    * searching.    *    *<p>The default value is one.    *    *<p>Some common uses for this are:<ul>    *    *<li>Set it to zero to put multiple terms in the same position.  This is    * useful if, e.g., a word has multiple stems.  Searches for phrases    * including either stem will match.  In this case, all but the first stem's    * increment should be set to zero: the increment of the first instance    * should be one.  Repeating a token with an increment of zero can also be    * used to boost the scores of matches on that token.    *    *<li>Set it to values greater than one to inhibit exact phrase matches.    * If, for example, one does not want phrases to match across removed stop    * words, then one could build a stop word filter that removes stop words and    * also sets the increment to the number of stop words removed before each    * non-stop word.  Then exact phrase queries will only match when the terms    * occur with no intervening stop words.    *    *</ul>    * @see org.apache.lucene.index.TermPositions    */
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
comment|/** Sets the Token's term text.<b>NOTE:</b> for better    *  indexing speed you should instead use the char[]    *  termBuffer methods to set the term text. */
DECL|method|setTermText
specifier|public
name|void
name|setTermText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|termText
operator|=
name|text
expr_stmt|;
name|termBuffer
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Returns the Token's term text.    *     * @deprecated Use {@link #termBuffer()} and {@link    * #termLength()} instead. */
DECL|method|termText
specifier|public
specifier|final
name|String
name|termText
parameter_list|()
block|{
if|if
condition|(
name|termText
operator|==
literal|null
operator|&&
name|termBuffer
operator|!=
literal|null
condition|)
name|termText
operator|=
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
return|return
name|termText
return|;
block|}
comment|/** Copies the contents of buffer, starting at offset for    *  length characters, into the termBuffer    *  array.<b>NOTE:</b> for better indexing speed you    *  should instead retrieve the termBuffer, using {@link    *  #termBuffer()} or {@link #resizeTermBuffer(int)}, and    *  fill it in directly to set the term text.  This saves    *  an extra copy. */
DECL|method|setTermBuffer
specifier|public
specifier|final
name|void
name|setTermBuffer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|resizeTermBuffer
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** Returns the internal termBuffer character array which    *  you can then directly alter.  If the array is too    *  small for your token, use {@link    *  #resizeTermBuffer(int)} to increase it.  After    *  altering the buffer be sure to call {@link    *  #setTermLength} to record the number of valid    *  characters that were placed into the termBuffer. */
DECL|method|termBuffer
specifier|public
specifier|final
name|char
index|[]
name|termBuffer
parameter_list|()
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
return|return
name|termBuffer
return|;
block|}
comment|/** Grows the termBuffer to at least size newSize.    *  @param newSize minimum size of the new termBuffer    *  @return newly created termBuffer with length>= newSize    */
DECL|method|resizeTermBuffer
specifier|public
name|char
index|[]
name|resizeTermBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|newSize
operator|>
name|termBuffer
operator|.
name|length
condition|)
block|{
name|int
name|size
init|=
name|termBuffer
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|size
operator|<
name|newSize
condition|)
name|size
operator|*=
literal|2
expr_stmt|;
name|char
index|[]
name|newBuffer
init|=
operator|new
name|char
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|termBuffer
operator|=
name|newBuffer
expr_stmt|;
block|}
return|return
name|termBuffer
return|;
block|}
comment|// TODO: once we remove the deprecated termText() method
comment|// and switch entirely to char[] termBuffer we don't need
comment|// to use this method anymore
DECL|method|initTermBuffer
specifier|private
name|void
name|initTermBuffer
parameter_list|()
block|{
if|if
condition|(
name|termBuffer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|termText
operator|==
literal|null
condition|)
block|{
name|termBuffer
operator|=
operator|new
name|char
index|[
name|MIN_BUFFER_SIZE
index|]
expr_stmt|;
name|termLength
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|int
name|length
init|=
name|termText
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|MIN_BUFFER_SIZE
condition|)
name|length
operator|=
name|MIN_BUFFER_SIZE
expr_stmt|;
name|termBuffer
operator|=
operator|new
name|char
index|[
name|length
index|]
expr_stmt|;
name|termLength
operator|=
name|termText
operator|.
name|length
argument_list|()
expr_stmt|;
name|termText
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|termText
operator|.
name|length
argument_list|()
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termText
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|termText
operator|!=
literal|null
condition|)
name|termText
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Return number of valid characters (length of the term)    *  in the termBuffer array. */
DECL|method|termLength
specifier|public
specifier|final
name|int
name|termLength
parameter_list|()
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
return|return
name|termLength
return|;
block|}
comment|/** Set number of valid characters (length of the term) in    *  the termBuffer array. */
DECL|method|setTermLength
specifier|public
specifier|final
name|void
name|setTermLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** Returns this Token's starting offset, the position of the first character     corresponding to this token in the source text.      Note that the difference between endOffset() and startOffset() may not be     equal to termText.length(), as the term text may have been altered by a     stemmer or some other filter. */
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
comment|/** Returns this Token's ending offset, one greater than the position of the     last character corresponding to this token in the source text. */
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
comment|/**     * Returns this Token's payload.    */
DECL|method|getPayload
specifier|public
name|Payload
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
name|Payload
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|initTermBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|termBuffer
operator|==
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
else|else
name|sb
operator|.
name|append
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|startOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|endOffset
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
literal|"word"
argument_list|)
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",type="
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|positionIncrement
operator|!=
literal|1
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|",posIncr="
argument_list|)
operator|.
name|append
argument_list|(
name|positionIncrement
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Resets the term text, payload, and positionIncrement to default.    * Other fields such as startOffset, endOffset and the token type are    * not reset since they are normally overwritten by the tokenizer. */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|payload
operator|=
literal|null
expr_stmt|;
comment|// Leave termBuffer to allow re-use
name|termLength
operator|=
literal|0
expr_stmt|;
name|termText
operator|=
literal|null
expr_stmt|;
name|positionIncrement
operator|=
literal|1
expr_stmt|;
comment|// startOffset = endOffset = 0;
comment|// type = DEFAULT_TYPE;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
try|try
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
if|if
condition|(
name|termBuffer
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|termBuffer
operator|=
literal|null
expr_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|setPayload
argument_list|(
operator|(
name|Payload
operator|)
name|payload
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// shouldn't happen
block|}
block|}
block|}
end_class

end_unit

