begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * ExtendedBufferedReader  *  * A special reader decorater which supports more  * sophisticated access to the underlying reader object.  *   * In particular the reader supports a look-ahead option,  * which allows you to see the next char returned by  * next().  * Furthermore the skip-method supports skipping until  * (but excluding) a given char. Similar functionality  * is supported by the reader as well.  *   */
end_comment

begin_class
DECL|class|ExtendedBufferedReader
class|class
name|ExtendedBufferedReader
extends|extends
name|BufferedReader
block|{
comment|/** the end of stream symbol */
DECL|field|END_OF_STREAM
specifier|public
specifier|static
specifier|final
name|int
name|END_OF_STREAM
init|=
operator|-
literal|1
decl_stmt|;
comment|/** undefined state for the lookahead char */
DECL|field|UNDEFINED
specifier|public
specifier|static
specifier|final
name|int
name|UNDEFINED
init|=
operator|-
literal|2
decl_stmt|;
comment|/** the lookahead chars */
DECL|field|lookaheadChar
specifier|private
name|int
name|lookaheadChar
init|=
name|UNDEFINED
decl_stmt|;
comment|/** the last char returned */
DECL|field|lastChar
specifier|private
name|int
name|lastChar
init|=
name|UNDEFINED
decl_stmt|;
comment|/** the line counter */
DECL|field|lineCounter
specifier|private
name|int
name|lineCounter
init|=
literal|0
decl_stmt|;
DECL|field|line
specifier|private
name|CharBuffer
name|line
init|=
operator|new
name|CharBuffer
argument_list|()
decl_stmt|;
comment|/**    * Created extended buffered reader using default buffer-size    *    */
DECL|method|ExtendedBufferedReader
specifier|public
name|ExtendedBufferedReader
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
comment|/* note uh: do not fetch the first char here,      *          because this might block the method!      */
block|}
comment|/**    * Create extended buffered reader using the given buffer-size    */
DECL|method|ExtendedBufferedReader
specifier|public
name|ExtendedBufferedReader
parameter_list|(
name|Reader
name|r
parameter_list|,
name|int
name|bufSize
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|,
name|bufSize
argument_list|)
expr_stmt|;
comment|/* note uh: do not fetch the first char here,      *          because this might block the method!      */
block|}
comment|/**    * Reads the next char from the input stream.    * @return the next char or END_OF_STREAM if end of stream has been reached.    */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
comment|// initalize the lookahead
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|lastChar
operator|=
name|lookaheadChar
expr_stmt|;
if|if
condition|(
name|super
operator|.
name|ready
argument_list|()
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|lookaheadChar
operator|=
name|UNDEFINED
expr_stmt|;
block|}
if|if
condition|(
name|lastChar
operator|==
literal|'\n'
condition|)
block|{
name|lineCounter
operator|++
expr_stmt|;
block|}
return|return
name|lastChar
return|;
block|}
comment|/**    * Returns the last read character again.    *     * @return the last read char or UNDEFINED    */
DECL|method|readAgain
specifier|public
name|int
name|readAgain
parameter_list|()
block|{
return|return
name|lastChar
return|;
block|}
comment|/**    * Non-blocking reading of len chars into buffer buf starting    * at bufferposition off.    *     * performs an iteratative read on the underlying stream    * as long as the following conditions hold:    *   - less than len chars have been read    *   - end of stream has not been reached    *   - next read is not blocking    *     * @return nof chars actually read or END_OF_STREAM    */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do not claim if len == 0
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// init lookahead, but do not block !!
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
if|if
condition|(
name|ready
argument_list|()
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|// 'first read of underlying stream'
if|if
condition|(
name|lookaheadChar
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|// continue until the lookaheadChar would block
name|int
name|cOff
init|=
name|off
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
operator|&&
name|ready
argument_list|()
condition|)
block|{
if|if
condition|(
name|lookaheadChar
operator|==
operator|-
literal|1
condition|)
block|{
comment|// eof stream reached, do not continue
return|return
name|cOff
operator|-
name|off
return|;
block|}
else|else
block|{
name|buf
index|[
name|cOff
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|lookaheadChar
expr_stmt|;
if|if
condition|(
name|lookaheadChar
operator|==
literal|'\n'
condition|)
block|{
name|lineCounter
operator|++
expr_stmt|;
block|}
name|lastChar
operator|=
name|lookaheadChar
expr_stmt|;
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
block|}
return|return
name|cOff
operator|-
name|off
return|;
block|}
comment|/**   * Reads all characters up to (but not including) the given character.   *    * @param c the character to read up to   * @return the string up to the character<code>c</code>   * @throws IOException   */
DECL|method|readUntil
specifier|public
name|String
name|readUntil
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|line
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// reuse
while|while
condition|(
name|lookaheadChar
operator|!=
name|c
operator|&&
name|lookaheadChar
operator|!=
name|END_OF_STREAM
condition|)
block|{
name|line
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|lookaheadChar
argument_list|)
expr_stmt|;
if|if
condition|(
name|lookaheadChar
operator|==
literal|'\n'
condition|)
block|{
name|lineCounter
operator|++
expr_stmt|;
block|}
name|lastChar
operator|=
name|lookaheadChar
expr_stmt|;
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
return|return
name|line
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**   * @return A String containing the contents of the line, not    *         including any line-termination characters, or null    *         if the end of the stream has been reached   */
DECL|method|readLine
specifier|public
name|String
name|readLine
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|line
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//reuse
comment|// return null if end of stream has been reached
if|if
condition|(
name|lookaheadChar
operator|==
name|END_OF_STREAM
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// do we have a line termination already
name|char
name|laChar
init|=
operator|(
name|char
operator|)
name|lookaheadChar
decl_stmt|;
if|if
condition|(
name|laChar
operator|==
literal|'\n'
operator|||
name|laChar
operator|==
literal|'\r'
condition|)
block|{
name|lastChar
operator|=
name|lookaheadChar
expr_stmt|;
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
comment|// ignore '\r\n' as well
if|if
condition|(
operator|(
name|char
operator|)
name|lookaheadChar
operator|==
literal|'\n'
condition|)
block|{
name|lastChar
operator|=
name|lookaheadChar
expr_stmt|;
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|lineCounter
operator|++
expr_stmt|;
return|return
name|line
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// create the rest-of-line return and update the lookahead
name|line
operator|.
name|append
argument_list|(
name|laChar
argument_list|)
expr_stmt|;
name|String
name|restOfLine
init|=
name|super
operator|.
name|readLine
argument_list|()
decl_stmt|;
comment|// TODO involves copying
name|lastChar
operator|=
name|lookaheadChar
expr_stmt|;
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|restOfLine
operator|!=
literal|null
condition|)
block|{
name|line
operator|.
name|append
argument_list|(
name|restOfLine
argument_list|)
expr_stmt|;
block|}
name|lineCounter
operator|++
expr_stmt|;
return|return
name|line
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Skips char in the stream    *     * ATTENTION: invalidates the line-counter !!!!!    *     * @return nof skiped chars    */
DECL|method|skip
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
comment|// illegal argument
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"negative argument not supported"
argument_list|)
throw|;
block|}
comment|// no skipping
if|if
condition|(
name|n
operator|==
literal|0
operator|||
name|lookaheadChar
operator|==
name|END_OF_STREAM
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// skip and reread the lookahead-char
name|long
name|skiped
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|1
condition|)
block|{
name|skiped
operator|=
name|super
operator|.
name|skip
argument_list|(
name|n
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
comment|// fixme uh: we should check the skiped sequence for line-terminations...
name|lineCounter
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
return|return
name|skiped
operator|+
literal|1
return|;
block|}
comment|/**    * Skips all chars in the input until (but excluding) the given char    *     * @param c    * @return counter    * @throws IllegalArgumentException    * @throws IOException    */
DECL|method|skipUntil
specifier|public
name|long
name|skipUntil
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|long
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|lookaheadChar
operator|!=
name|c
operator|&&
name|lookaheadChar
operator|!=
name|END_OF_STREAM
condition|)
block|{
if|if
condition|(
name|lookaheadChar
operator|==
literal|'\n'
condition|)
block|{
name|lineCounter
operator|++
expr_stmt|;
block|}
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
name|counter
operator|++
expr_stmt|;
block|}
return|return
name|counter
return|;
block|}
comment|/**    * Returns the next char in the stream without consuming it.    *     * Remember the next char read by read(..) will always be    * identical to lookAhead().    *     * @return the next char (without consuming it) or END_OF_STREAM    */
DECL|method|lookAhead
specifier|public
name|int
name|lookAhead
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lookaheadChar
operator|==
name|UNDEFINED
condition|)
block|{
name|lookaheadChar
operator|=
name|super
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
return|return
name|lookaheadChar
return|;
block|}
comment|/**    * Returns the nof line read    * ATTENTION: the skip-method does invalidate the line-number counter    *     * @return the current-line-number (or -1)    */
DECL|method|getLineNumber
specifier|public
name|int
name|getLineNumber
parameter_list|()
block|{
if|if
condition|(
name|lineCounter
operator|>
operator|-
literal|1
condition|)
block|{
return|return
name|lineCounter
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|method|markSupported
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
comment|/* note uh: marking is not supported, cause we cannot      *          see into the future...      */
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

