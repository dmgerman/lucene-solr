begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
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
name|util
operator|.
name|RamUsageEstimator
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
name|ArrayUtil
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
name|InPlaceMergeSorter
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
name|Sorter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
import|;
end_import

begin_comment
comment|// TODO
end_comment

begin_comment
comment|//   - do we really need the .bits...?  if not we can make util in UnicodeUtil to convert 1 char into a BytesRef
end_comment

begin_comment
comment|/**   * Converts UTF-32 automata to the equivalent UTF-8 representation.  * @lucene.internal   */
end_comment

begin_class
DECL|class|UTF32ToUTF8Light
specifier|public
specifier|final
class|class
name|UTF32ToUTF8Light
block|{
comment|// Unicode boundaries for UTF8 bytes 1,2,3,4
DECL|field|startCodes
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|startCodes
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|128
block|,
literal|2048
block|,
literal|65536
block|}
decl_stmt|;
DECL|field|endCodes
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|endCodes
init|=
operator|new
name|int
index|[]
block|{
literal|127
block|,
literal|2047
block|,
literal|65535
block|,
literal|1114111
block|}
decl_stmt|;
DECL|field|MASKS
specifier|static
name|int
index|[]
name|MASKS
init|=
operator|new
name|int
index|[
literal|32
index|]
decl_stmt|;
static|static
block|{
name|int
name|v
init|=
literal|2
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
literal|32
condition|;
name|i
operator|++
control|)
block|{
name|MASKS
index|[
name|i
index|]
operator|=
name|v
operator|-
literal|1
expr_stmt|;
name|v
operator|*=
literal|2
expr_stmt|;
block|}
block|}
comment|// Represents one of the N utf8 bytes that (in sequence)
comment|// define a code point.  value is the byte value; bits is
comment|// how many bits are "used" by utf8 at that byte
DECL|class|UTF8Byte
specifier|private
specifier|static
class|class
name|UTF8Byte
block|{
DECL|field|value
name|int
name|value
decl_stmt|;
comment|// TODO: change to byte
DECL|field|bits
name|byte
name|bits
decl_stmt|;
block|}
comment|// Holds a single code point, as a sequence of 1-4 utf8 bytes:
comment|// TODO: maybe move to UnicodeUtil?
DECL|class|UTF8Sequence
specifier|private
specifier|static
class|class
name|UTF8Sequence
block|{
DECL|field|bytes
specifier|private
specifier|final
name|UTF8Byte
index|[]
name|bytes
decl_stmt|;
DECL|field|len
specifier|private
name|int
name|len
decl_stmt|;
DECL|method|UTF8Sequence
specifier|public
name|UTF8Sequence
parameter_list|()
block|{
name|bytes
operator|=
operator|new
name|UTF8Byte
index|[
literal|4
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
operator|new
name|UTF8Byte
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|byteAt
specifier|public
name|int
name|byteAt
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
name|bytes
index|[
name|idx
index|]
operator|.
name|value
return|;
block|}
DECL|method|numBits
specifier|public
name|int
name|numBits
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
name|bytes
index|[
name|idx
index|]
operator|.
name|bits
return|;
block|}
DECL|method|set
specifier|private
name|void
name|set
parameter_list|(
name|int
name|code
parameter_list|)
block|{
if|if
condition|(
name|code
operator|<
literal|128
condition|)
block|{
comment|// 0xxxxxxx
name|bytes
index|[
literal|0
index|]
operator|.
name|value
operator|=
name|code
expr_stmt|;
name|bytes
index|[
literal|0
index|]
operator|.
name|bits
operator|=
literal|7
expr_stmt|;
name|len
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
operator|<
literal|2048
condition|)
block|{
comment|// 110yyyxx 10xxxxxx
name|bytes
index|[
literal|0
index|]
operator|.
name|value
operator|=
operator|(
literal|6
operator|<<
literal|5
operator|)
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
expr_stmt|;
name|bytes
index|[
literal|0
index|]
operator|.
name|bits
operator|=
literal|5
expr_stmt|;
name|setRest
argument_list|(
name|code
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|len
operator|=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
operator|<
literal|65536
condition|)
block|{
comment|// 1110yyyy 10yyyyxx 10xxxxxx
name|bytes
index|[
literal|0
index|]
operator|.
name|value
operator|=
operator|(
literal|14
operator|<<
literal|4
operator|)
operator||
operator|(
name|code
operator|>>
literal|12
operator|)
expr_stmt|;
name|bytes
index|[
literal|0
index|]
operator|.
name|bits
operator|=
literal|4
expr_stmt|;
name|setRest
argument_list|(
name|code
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|len
operator|=
literal|3
expr_stmt|;
block|}
else|else
block|{
comment|// 11110zzz 10zzyyyy 10yyyyxx 10xxxxxx
name|bytes
index|[
literal|0
index|]
operator|.
name|value
operator|=
operator|(
literal|30
operator|<<
literal|3
operator|)
operator||
operator|(
name|code
operator|>>
literal|18
operator|)
expr_stmt|;
name|bytes
index|[
literal|0
index|]
operator|.
name|bits
operator|=
literal|3
expr_stmt|;
name|setRest
argument_list|(
name|code
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|len
operator|=
literal|4
expr_stmt|;
block|}
block|}
DECL|method|setRest
specifier|private
name|void
name|setRest
parameter_list|(
name|int
name|code
parameter_list|,
name|int
name|numBytes
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBytes
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|numBytes
operator|-
name|i
index|]
operator|.
name|value
operator|=
literal|128
operator||
operator|(
name|code
operator|&
name|MASKS
index|[
literal|5
index|]
operator|)
expr_stmt|;
name|bytes
index|[
name|numBytes
operator|-
name|i
index|]
operator|.
name|bits
operator|=
literal|6
expr_stmt|;
name|code
operator|=
name|code
operator|>>
literal|6
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toBinaryString
argument_list|(
name|bytes
index|[
name|i
index|]
operator|.
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|field|startUTF8
specifier|private
specifier|final
name|UTF8Sequence
name|startUTF8
init|=
operator|new
name|UTF8Sequence
argument_list|()
decl_stmt|;
DECL|field|endUTF8
specifier|private
specifier|final
name|UTF8Sequence
name|endUTF8
init|=
operator|new
name|UTF8Sequence
argument_list|()
decl_stmt|;
DECL|field|tmpUTF8a
specifier|private
specifier|final
name|UTF8Sequence
name|tmpUTF8a
init|=
operator|new
name|UTF8Sequence
argument_list|()
decl_stmt|;
DECL|field|tmpUTF8b
specifier|private
specifier|final
name|UTF8Sequence
name|tmpUTF8b
init|=
operator|new
name|UTF8Sequence
argument_list|()
decl_stmt|;
comment|// Builds necessary utf8 edges between start& end
DECL|method|convertOneEdge
name|void
name|convertOneEdge
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|startCodePoint
parameter_list|,
name|int
name|endCodePoint
parameter_list|)
block|{
name|startUTF8
operator|.
name|set
argument_list|(
name|startCodePoint
argument_list|)
expr_stmt|;
name|endUTF8
operator|.
name|set
argument_list|(
name|endCodePoint
argument_list|)
expr_stmt|;
comment|//System.out.println("start = " + startUTF8);
comment|//System.out.println("  end = " + endUTF8);
name|build
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
argument_list|,
name|endUTF8
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|build
specifier|private
name|void
name|build
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|UTF8Sequence
name|startUTF8
parameter_list|,
name|UTF8Sequence
name|endUTF8
parameter_list|,
name|int
name|upto
parameter_list|)
block|{
comment|// Break into start, middle, end:
if|if
condition|(
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|==
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
condition|)
block|{
comment|// Degen case: lead with the same byte:
if|if
condition|(
name|upto
operator|==
name|startUTF8
operator|.
name|len
operator|-
literal|1
operator|&&
name|upto
operator|==
name|endUTF8
operator|.
name|len
operator|-
literal|1
condition|)
block|{
comment|// Super degen: just single edge, one UTF8 byte:
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
assert|assert
name|startUTF8
operator|.
name|len
operator|>
name|upto
operator|+
literal|1
assert|;
assert|assert
name|endUTF8
operator|.
name|len
operator|>
name|upto
operator|+
literal|1
assert|;
name|int
name|n
init|=
name|utf8
operator|.
name|createState
argument_list|()
decl_stmt|;
comment|// Single value leading edge
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|n
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
comment|//start.addTransition(new Transition(startUTF8.byteAt(upto), n));  // type=single
comment|// Recurse for the rest
name|build
argument_list|(
name|n
argument_list|,
name|end
argument_list|,
name|startUTF8
argument_list|,
name|endUTF8
argument_list|,
literal|1
operator|+
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|startUTF8
operator|.
name|len
operator|==
name|endUTF8
operator|.
name|len
condition|)
block|{
if|if
condition|(
name|upto
operator|==
name|startUTF8
operator|.
name|len
operator|-
literal|1
condition|)
block|{
comment|//start.addTransition(new Transition(startUTF8.byteAt(upto), endUTF8.byteAt(upto), end));        // type=startend
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|start
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
argument_list|,
name|upto
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|-
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|>
literal|1
condition|)
block|{
comment|// There is a middle
name|all
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|+
literal|1
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|-
literal|1
argument_list|,
name|startUTF8
operator|.
name|len
operator|-
name|upto
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|end
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|endUTF8
argument_list|,
name|upto
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// start
name|start
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
argument_list|,
name|upto
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// possibly middle, spanning multiple num bytes
name|int
name|byteCount
init|=
literal|1
operator|+
name|startUTF8
operator|.
name|len
operator|-
name|upto
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|endUTF8
operator|.
name|len
operator|-
name|upto
decl_stmt|;
while|while
condition|(
name|byteCount
operator|<
name|limit
condition|)
block|{
comment|// wasteful: we only need first byte, and, we should
comment|// statically encode this first byte:
name|tmpUTF8a
operator|.
name|set
argument_list|(
name|startCodes
index|[
name|byteCount
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|tmpUTF8b
operator|.
name|set
argument_list|(
name|endCodes
index|[
name|byteCount
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|all
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|tmpUTF8a
operator|.
name|byteAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|tmpUTF8b
operator|.
name|byteAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|tmpUTF8a
operator|.
name|len
operator|-
literal|1
argument_list|)
expr_stmt|;
name|byteCount
operator|++
expr_stmt|;
block|}
comment|// end
name|end
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|endUTF8
argument_list|,
name|upto
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start
specifier|private
name|void
name|start
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|UTF8Sequence
name|startUTF8
parameter_list|,
name|int
name|upto
parameter_list|,
name|boolean
name|doAll
parameter_list|)
block|{
if|if
condition|(
name|upto
operator|==
name|startUTF8
operator|.
name|len
operator|-
literal|1
condition|)
block|{
comment|// Done recursing
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator||
name|MASKS
index|[
name|startUTF8
operator|.
name|numBits
argument_list|(
name|upto
argument_list|)
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// type=start
comment|//start.addTransition(new Transition(startUTF8.byteAt(upto), startUTF8.byteAt(upto) | MASKS[startUTF8.numBits(upto)-1], end));  // type=start
block|}
else|else
block|{
name|int
name|n
init|=
name|utf8
operator|.
name|createState
argument_list|()
decl_stmt|;
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|n
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
comment|//start.addTransition(new Transition(startUTF8.byteAt(upto), n));  // type=start
name|start
argument_list|(
name|n
argument_list|,
name|end
argument_list|,
name|startUTF8
argument_list|,
literal|1
operator|+
name|upto
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|int
name|endCode
init|=
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator||
name|MASKS
index|[
name|startUTF8
operator|.
name|numBits
argument_list|(
name|upto
argument_list|)
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|doAll
operator|&&
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|!=
name|endCode
condition|)
block|{
name|all
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|+
literal|1
argument_list|,
name|endCode
argument_list|,
name|startUTF8
operator|.
name|len
operator|-
name|upto
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|end
specifier|private
name|void
name|end
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|UTF8Sequence
name|endUTF8
parameter_list|,
name|int
name|upto
parameter_list|,
name|boolean
name|doAll
parameter_list|)
block|{
if|if
condition|(
name|upto
operator|==
name|endUTF8
operator|.
name|len
operator|-
literal|1
condition|)
block|{
comment|// Done recursing
comment|//start.addTransition(new Transition(endUTF8.byteAt(upto)& (~MASKS[endUTF8.numBits(upto)-1]), endUTF8.byteAt(upto), end));   // type=end
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|&
operator|(
operator|~
name|MASKS
index|[
name|endUTF8
operator|.
name|numBits
argument_list|(
name|upto
argument_list|)
operator|-
literal|1
index|]
operator|)
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|startCode
decl_stmt|;
if|if
condition|(
name|endUTF8
operator|.
name|numBits
argument_list|(
name|upto
argument_list|)
operator|==
literal|5
condition|)
block|{
comment|// special case -- avoid created unused edges (endUTF8
comment|// doesn't accept certain byte sequences) -- there
comment|// are other cases we could optimize too:
name|startCode
operator|=
literal|194
expr_stmt|;
block|}
else|else
block|{
name|startCode
operator|=
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|&
operator|(
operator|~
name|MASKS
index|[
name|endUTF8
operator|.
name|numBits
argument_list|(
name|upto
argument_list|)
operator|-
literal|1
index|]
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|doAll
operator|&&
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|!=
name|startCode
condition|)
block|{
name|all
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startCode
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
operator|-
literal|1
argument_list|,
name|endUTF8
operator|.
name|len
operator|-
name|upto
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|n
init|=
name|utf8
operator|.
name|createState
argument_list|()
decl_stmt|;
comment|//start.addTransition(new Transition(endUTF8.byteAt(upto), n));  // type=end
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|n
argument_list|,
name|endUTF8
operator|.
name|byteAt
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
name|end
argument_list|(
name|n
argument_list|,
name|end
argument_list|,
name|endUTF8
argument_list|,
literal|1
operator|+
name|upto
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|all
specifier|private
name|void
name|all
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|startCode
parameter_list|,
name|int
name|endCode
parameter_list|,
name|int
name|left
parameter_list|)
block|{
if|if
condition|(
name|left
operator|==
literal|0
condition|)
block|{
comment|//start.addTransition(new Transition(startCode, endCode, end));  // type=all
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|startCode
argument_list|,
name|endCode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|lastN
init|=
name|utf8
operator|.
name|createState
argument_list|()
decl_stmt|;
comment|//start.addTransition(new Transition(startCode, endCode, lastN));  // type=all
name|utf8
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|lastN
argument_list|,
name|startCode
argument_list|,
name|endCode
argument_list|)
expr_stmt|;
while|while
condition|(
name|left
operator|>
literal|1
condition|)
block|{
name|int
name|n
init|=
name|utf8
operator|.
name|createState
argument_list|()
decl_stmt|;
comment|//lastN.addTransition(new Transition(128, 191, n));  // type=all*
name|utf8
operator|.
name|addTransition
argument_list|(
name|lastN
argument_list|,
name|n
argument_list|,
literal|128
argument_list|,
literal|191
argument_list|)
expr_stmt|;
comment|// type=all*
name|left
operator|--
expr_stmt|;
name|lastN
operator|=
name|n
expr_stmt|;
block|}
comment|//lastN.addTransition(new Transition(128, 191, end)); // type = all*
name|utf8
operator|.
name|addTransition
argument_list|(
name|lastN
argument_list|,
name|end
argument_list|,
literal|128
argument_list|,
literal|191
argument_list|)
expr_stmt|;
comment|// type = all*
block|}
block|}
DECL|field|utf8
name|LightAutomaton
operator|.
name|Builder
name|utf8
decl_stmt|;
comment|/** Converts an incoming utf32 automaton to an equivalent    *  utf8 one.  The incoming automaton need not be    *  deterministic.  Note that the returned automaton will    *  not in general be deterministic, so you must    *  determinize it if that's needed. */
DECL|method|convert
specifier|public
name|LightAutomaton
name|convert
parameter_list|(
name|LightAutomaton
name|utf32
parameter_list|)
block|{
comment|//System.out.println("\nCONVERT");
comment|// nocommit make sure singleton cases work:
comment|//if (utf32.isSingleton()) {
comment|//utf32 = utf32.cloneExpanded();
comment|//}
name|int
index|[]
name|map
init|=
operator|new
name|int
index|[
name|utf32
operator|.
name|getNumStates
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|map
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|pending
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|utf32State
init|=
literal|0
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|utf32State
argument_list|)
expr_stmt|;
name|utf8
operator|=
operator|new
name|LightAutomaton
operator|.
name|Builder
argument_list|()
expr_stmt|;
comment|// nocommit we don't track this
comment|// utf8.setDeterministic(false);
name|int
name|utf8State
init|=
name|utf8
operator|.
name|createState
argument_list|()
decl_stmt|;
name|utf8
operator|.
name|setAccept
argument_list|(
name|utf8State
argument_list|,
name|utf32
operator|.
name|isAccept
argument_list|(
name|utf32State
argument_list|)
argument_list|)
expr_stmt|;
name|map
index|[
name|utf32State
index|]
operator|=
name|utf8State
expr_stmt|;
name|LightAutomaton
operator|.
name|Transition
name|scratch
init|=
operator|new
name|LightAutomaton
operator|.
name|Transition
argument_list|()
decl_stmt|;
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|utf32State
operator|=
name|pending
operator|.
name|remove
argument_list|(
name|pending
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|utf8State
operator|=
name|map
index|[
name|utf32State
index|]
expr_stmt|;
assert|assert
name|utf8State
operator|!=
operator|-
literal|1
assert|;
name|int
name|numTransitions
init|=
name|utf32
operator|.
name|getNumTransitions
argument_list|(
name|utf32State
argument_list|)
decl_stmt|;
name|utf32
operator|.
name|initTransition
argument_list|(
name|utf32State
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
comment|//System.out.println("  convert state=" + utf32State + " numTransitions=" + numTransitions);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTransitions
condition|;
name|i
operator|++
control|)
block|{
name|utf32
operator|.
name|getNextTransition
argument_list|(
name|scratch
argument_list|)
expr_stmt|;
name|int
name|destUTF32
init|=
name|scratch
operator|.
name|dest
decl_stmt|;
name|int
name|destUTF8
init|=
name|map
index|[
name|destUTF32
index|]
decl_stmt|;
comment|//System.out.println("    transition min=" + scratch.min + " max=" + scratch.max);
if|if
condition|(
name|destUTF8
operator|==
operator|-
literal|1
condition|)
block|{
name|destUTF8
operator|=
name|utf8
operator|.
name|createState
argument_list|()
expr_stmt|;
comment|//System.out.println("      create dest=" + destUTF8 +" accept=" + utf32.isAccept(destUTF32));
name|utf8
operator|.
name|setAccept
argument_list|(
name|destUTF8
argument_list|,
name|utf32
operator|.
name|isAccept
argument_list|(
name|destUTF32
argument_list|)
argument_list|)
expr_stmt|;
name|map
index|[
name|destUTF32
index|]
operator|=
name|destUTF8
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|destUTF32
argument_list|)
expr_stmt|;
block|}
comment|// Writes new transitions into pendingTransitions:
name|convertOneEdge
argument_list|(
name|utf8State
argument_list|,
name|destUTF8
argument_list|,
name|scratch
operator|.
name|min
argument_list|,
name|scratch
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|utf8
operator|.
name|finish
argument_list|()
return|;
block|}
comment|/*   private State newUTF8State() {     State s = new State();     if (utf8StateCount == utf8States.length) {       final State[] newArray = new State[ArrayUtil.oversize(1+utf8StateCount, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];       System.arraycopy(utf8States, 0, newArray, 0, utf8StateCount);       utf8States = newArray;     }     utf8States[utf8StateCount] = s;     s.number = utf8StateCount;     utf8StateCount++;     return s;   }   */
block|}
end_class

end_unit

