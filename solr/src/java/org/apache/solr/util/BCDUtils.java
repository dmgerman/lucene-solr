begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BCDUtils
specifier|public
class|class
name|BCDUtils
block|{
comment|// idiv is expensive...
comment|// use fixed point math to multiply by 1/10
comment|// http://www.cs.uiowa.edu/~jones/bcd/divide.html
DECL|method|div10
specifier|private
specifier|static
name|int
name|div10
parameter_list|(
name|int
name|a
parameter_list|)
block|{
return|return
operator|(
name|a
operator|*
literal|0xcccd
operator|)
operator|>>>
literal|19
return|;
block|}
DECL|method|mul10
specifier|private
specifier|static
name|int
name|mul10
parameter_list|(
name|int
name|a
parameter_list|)
block|{
return|return
operator|(
name|a
operator|*
literal|10
operator|)
return|;
block|}
comment|// private static int mul10(int a) { return ((a<<3)+(a<<1)); }
comment|// private static int mul10(int a) { return (a+(a<<2))<<1; } // attempt to use LEA instr
comment|// (imul32 on AMD64 only has a 3 cycle latency in any case)
comment|// something that won't clash with other base100int
comment|// chars (something>= 100)
DECL|field|NEG_CHAR
specifier|private
specifier|static
specifier|final
name|char
name|NEG_CHAR
init|=
operator|(
name|char
operator|)
literal|126
decl_stmt|;
comment|// The zero exponent.
comment|// NOTE: for smaller integer representations, this current implementation
comment|// combines sign and exponent into the first char.  sign is negative if
comment|// exponent is less than the zero point (no negative exponents themselves)
DECL|field|ZERO_EXPONENT
specifier|private
specifier|static
specifier|final
name|int
name|ZERO_EXPONENT
init|=
literal|'a'
decl_stmt|;
comment|// 97
comment|// WARNING: assumption is that this is a legal int...
comment|// no validation is done.  [+-]?digit*
comment|//
comment|// Normalization of zeros *is* done...
comment|//  0004, 004, 04, 4 will all end up being equal
comment|//  0,-0 are normalized to '' (zero length)
comment|//
comment|// The value is written to the output buffer
comment|// from the end to the start.  The return value
comment|// is the start of the Base100 int in the output buffer.
comment|//
comment|// As the output will be smaller than the input, arr and
comment|// out may refer to the same array if desired.
comment|//
DECL|method|base10toBase100
specifier|public
specifier|static
name|int
name|base10toBase100
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|outend
parameter_list|)
block|{
name|int
name|wpos
init|=
name|outend
decl_stmt|;
comment|// write position
name|boolean
name|neg
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|--
name|end
operator|>=
name|start
condition|)
block|{
name|int
name|val
init|=
name|arr
index|[
name|end
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|'+'
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|val
operator|==
literal|'-'
condition|)
block|{
name|neg
operator|=
operator|!
name|neg
expr_stmt|;
break|break;
block|}
else|else
block|{
name|val
operator|=
name|val
operator|-
literal|'0'
expr_stmt|;
if|if
condition|(
name|end
operator|>
name|start
condition|)
block|{
name|int
name|val2
init|=
name|arr
index|[
name|end
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|val2
operator|==
literal|'+'
condition|)
block|{
name|out
index|[
operator|--
name|wpos
index|]
operator|=
operator|(
name|char
operator|)
name|val
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|val2
operator|==
literal|'-'
condition|)
block|{
name|out
index|[
operator|--
name|wpos
index|]
operator|=
operator|(
name|char
operator|)
name|val
expr_stmt|;
name|neg
operator|=
operator|!
name|neg
expr_stmt|;
break|break;
block|}
name|end
operator|--
expr_stmt|;
name|val
operator|=
name|val
operator|+
operator|(
name|val2
operator|-
literal|'0'
operator|)
operator|*
literal|10
expr_stmt|;
block|}
name|out
index|[
operator|--
name|wpos
index|]
operator|=
operator|(
name|char
operator|)
name|val
expr_stmt|;
block|}
block|}
comment|// remove leading base100 zeros
while|while
condition|(
name|wpos
operator|<
name|outend
operator|&&
name|out
index|[
name|wpos
index|]
operator|==
literal|0
condition|)
name|wpos
operator|++
expr_stmt|;
comment|// check for a zero value
if|if
condition|(
name|wpos
operator|==
name|outend
condition|)
block|{
comment|// if zero, don't add negative sign
block|}
elseif|else
if|if
condition|(
name|neg
condition|)
block|{
name|out
index|[
operator|--
name|wpos
index|]
operator|=
name|NEG_CHAR
expr_stmt|;
block|}
return|return
name|wpos
return|;
comment|// the start of the base100 int
block|}
comment|// Converts a base100 number to base10 character form
comment|// returns number of chars written.
comment|// At least 1 char is always written.
DECL|method|base100toBase10
specifier|public
specifier|static
name|int
name|base100toBase10
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|wpos
init|=
name|offset
decl_stmt|;
comment|// write position
name|boolean
name|firstDigit
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|int
name|val
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|==
name|NEG_CHAR
condition|)
block|{
name|out
index|[
name|wpos
operator|++
index|]
operator|=
literal|'-'
expr_stmt|;
continue|continue;
block|}
name|char
name|tens
init|=
call|(
name|char
call|)
argument_list|(
name|val
operator|/
literal|10
operator|+
literal|'0'
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|firstDigit
operator|||
name|tens
operator|!=
literal|'0'
condition|)
block|{
comment|// skip leading 0
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|/
literal|10
operator|+
literal|'0'
argument_list|)
expr_stmt|;
comment|// tens position
block|}
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|%
literal|10
operator|+
literal|'0'
argument_list|)
expr_stmt|;
comment|// ones position
name|firstDigit
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|firstDigit
condition|)
name|out
index|[
name|wpos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
return|return
name|wpos
operator|-
name|offset
return|;
block|}
DECL|method|base10toBase100SortableInt
specifier|public
specifier|static
name|String
name|base10toBase100SortableInt
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|val
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|val
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|base10toBase100SortableInt
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|,
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
operator|-
name|len
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|base100SortableIntToBase10
specifier|public
specifier|static
name|String
name|base100SortableIntToBase10
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|int
name|slen
init|=
name|val
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|slen
operator|<<
literal|2
index|]
decl_stmt|;
name|val
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|slen
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|base100SortableIntToBase10
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|,
name|arr
argument_list|,
name|slen
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|,
name|slen
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|base10toBase10kSortableInt
specifier|public
specifier|static
name|String
name|base10toBase10kSortableInt
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|val
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|val
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|base10toBase10kSortableInt
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|,
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
operator|-
name|len
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|base10kSortableIntToBase10
specifier|public
specifier|static
name|String
name|base10kSortableIntToBase10
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|int
name|slen
init|=
name|val
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|slen
operator|*
literal|5
index|]
decl_stmt|;
comment|// +1 time for orig, +4 for new
name|val
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|slen
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|base10kSortableIntToBase10
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|,
name|arr
argument_list|,
name|slen
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|arr
argument_list|,
name|slen
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/********* FUTURE     // the zero exponent... exponents above this point are positive     // and below are negative.     // It is desirable to make ordinary numbers have a single byte     // exponent when converted to UTF-8     // For integers, the exponent will always be>=0, but this format     // is meant to be valid for floating point numbers as well...     private static final int ZERO_EXPONENT='a';  // 97      // if exponent is larger than what can be represented     // in a single byte (char), then this is the multibyte     // escape char.     // UCS-2 surrogates start at 0xD800     private static final int POSITIVE_EXPONENT_ESCAPE=0x3fff;      // if exponent is smaller than what can be represented in     // a single byte, then this is the multibyte escape     private static final int NEGATIVE_EXPONENT_ESCAPE=1;      // if number is negative, it starts with this optional value     // this should not overlap with any exponent values     private static final int NEGATIVE_SIGN=0;   **********/
comment|// WARNING: assumption is that this is a legal int...
comment|// no validation is done.  [+-]?digit*
comment|//
comment|// Normalization of zeros *is* done...
comment|//  0004, 004, 04, 4 will all end up being equal
comment|//  0,-0 are normalized to '' (zero length)
comment|//
comment|// The value is written to the output buffer
comment|// from the end to the start.  The return value
comment|// is the start of the Base100 int in the output buffer.
comment|//
comment|// As the output will be smaller than the input, arr and
comment|// out may refer to the same array if desired.
comment|//
DECL|method|base10toBase100SortableInt
specifier|public
specifier|static
name|int
name|base10toBase100SortableInt
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|outend
parameter_list|)
block|{
name|int
name|wpos
init|=
name|outend
decl_stmt|;
comment|// write position
name|boolean
name|neg
init|=
literal|false
decl_stmt|;
operator|--
name|end
expr_stmt|;
comment|// position end pointer *on* the last char
comment|// read signs and leading zeros
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|char
name|val
init|=
name|arr
index|[
name|start
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|'-'
condition|)
name|neg
operator|=
operator|!
name|neg
expr_stmt|;
elseif|else
if|if
condition|(
name|val
operator|>=
literal|'1'
operator|&&
name|val
operator|<=
literal|'9'
condition|)
break|break;
name|start
operator|++
expr_stmt|;
block|}
comment|// eat whitespace on RHS?
name|outer
label|:
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
switch|switch
condition|(
name|arr
index|[
name|end
index|]
condition|)
block|{
case|case
literal|' '
case|:
case|case
literal|'\t'
case|:
case|case
literal|'\n'
case|:
case|case
literal|'\r'
case|:
name|end
operator|--
expr_stmt|;
break|break;
default|default:
break|break
name|outer
break|;
block|}
block|}
name|int
name|hundreds
init|=
literal|0
decl_stmt|;
comment|/******************************************************        * remove RHS zero normalization since it only helps 1 in 100        * numbers and complicates both encoding and decoding.        // remove pairs of zeros on the RHS and keep track of       // the count.       while (start<= end) {         char val = arr[end];          if (val=='0'&& start<= end) {           val=arr[end-1];           if (val=='0') {             hundreds++;             end-=2;             continue;           }         }          break;       }       *************************************************************/
comment|// now start at the end and work our way forward
comment|// encoding two base 10 digits into 1 base 100 digit
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|int
name|val
init|=
name|arr
index|[
name|end
operator|--
index|]
decl_stmt|;
name|val
operator|=
name|val
operator|-
literal|'0'
expr_stmt|;
if|if
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|int
name|val2
init|=
name|arr
index|[
name|end
operator|--
index|]
decl_stmt|;
name|val
operator|=
name|val
operator|+
operator|(
name|val2
operator|-
literal|'0'
operator|)
operator|*
literal|10
expr_stmt|;
block|}
name|out
index|[
operator|--
name|wpos
index|]
operator|=
name|neg
condition|?
call|(
name|char
call|)
argument_list|(
literal|99
operator|-
name|val
argument_list|)
else|:
operator|(
name|char
operator|)
name|val
expr_stmt|;
block|}
comment|/****** FUTURE: not needed for this implementation of exponent combined with sign       // normalize all zeros to positive values       if (wpos==outend) neg=false;       ******/
comment|// adjust exponent by the number of base 100 chars written
name|hundreds
operator|+=
name|outend
operator|-
name|wpos
expr_stmt|;
comment|// write the exponent and sign combined
name|out
index|[
operator|--
name|wpos
index|]
operator|=
name|neg
condition|?
call|(
name|char
call|)
argument_list|(
name|ZERO_EXPONENT
operator|-
name|hundreds
argument_list|)
else|:
call|(
name|char
call|)
argument_list|(
name|ZERO_EXPONENT
operator|+
name|hundreds
argument_list|)
expr_stmt|;
return|return
name|outend
operator|-
name|wpos
return|;
comment|// the length of the base100 int
block|}
comment|// Converts a base100 sortable number to base10 character form
comment|// returns number of chars written.
comment|// At least 1 char is always written.
DECL|method|base100SortableIntToBase10
specifier|public
specifier|static
name|int
name|base100SortableIntToBase10
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
comment|// Take care of "0" case first.  It's the only number that is represented
comment|// in one char.
if|if
condition|(
name|end
operator|-
name|start
operator|==
literal|1
condition|)
block|{
name|out
index|[
name|offset
index|]
operator|=
literal|'0'
expr_stmt|;
return|return
literal|1
return|;
block|}
name|int
name|wpos
init|=
name|offset
decl_stmt|;
comment|// write position
name|boolean
name|neg
init|=
literal|false
decl_stmt|;
name|int
name|exp
init|=
name|arr
index|[
name|start
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|exp
operator|<
name|ZERO_EXPONENT
condition|)
block|{
name|neg
operator|=
literal|true
expr_stmt|;
name|exp
operator|=
name|ZERO_EXPONENT
operator|-
name|exp
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
literal|'-'
expr_stmt|;
block|}
name|boolean
name|firstDigit
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|end
condition|)
block|{
name|int
name|val
init|=
name|arr
index|[
name|start
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|neg
condition|)
name|val
operator|=
literal|99
operator|-
name|val
expr_stmt|;
comment|// opt - if we ever want a faster version we can avoid one integer
comment|// divide by using fixed point math to multiply by 1/10
comment|// http://www.cs.uiowa.edu/~jones/bcd/divide.html
comment|// TIP: write a small function in gcc or cl and see what
comment|// the optimized assemply output looks like (and which is fastest).
comment|// In C you can specify "unsigned" which gives the compiler more
comment|// info than the Java compiler has.
name|char
name|tens
init|=
call|(
name|char
call|)
argument_list|(
name|val
operator|/
literal|10
operator|+
literal|'0'
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|firstDigit
operator|||
name|tens
operator|!=
literal|'0'
condition|)
block|{
comment|// skip leading 0
name|out
index|[
name|wpos
operator|++
index|]
operator|=
name|tens
expr_stmt|;
comment|// write tens position
block|}
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|%
literal|10
operator|+
literal|'0'
argument_list|)
expr_stmt|;
comment|// write ones position
name|firstDigit
operator|=
literal|false
expr_stmt|;
block|}
comment|// OPTIONAL: if trailing zeros were truncated, then this is where
comment|// we would restore them (compare number of chars read vs exponent)
return|return
name|wpos
operator|-
name|offset
return|;
block|}
DECL|method|base10toBase10kSortableInt
specifier|public
specifier|static
name|int
name|base10toBase10kSortableInt
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|outend
parameter_list|)
block|{
name|int
name|wpos
init|=
name|outend
decl_stmt|;
comment|// write position
name|boolean
name|neg
init|=
literal|false
decl_stmt|;
operator|--
name|end
expr_stmt|;
comment|// position end pointer *on* the last char
comment|// read signs and leading zeros
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|char
name|val
init|=
name|arr
index|[
name|start
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|'-'
condition|)
name|neg
operator|=
operator|!
name|neg
expr_stmt|;
elseif|else
if|if
condition|(
name|val
operator|>=
literal|'1'
operator|&&
name|val
operator|<=
literal|'9'
condition|)
break|break;
name|start
operator|++
expr_stmt|;
block|}
comment|// eat whitespace on RHS?
name|outer
label|:
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
switch|switch
condition|(
name|arr
index|[
name|end
index|]
condition|)
block|{
case|case
literal|' '
case|:
comment|// fallthrough
case|case
literal|'\t'
case|:
comment|// fallthrough
case|case
literal|'\n'
case|:
comment|// fallthrough
case|case
literal|'\r'
case|:
name|end
operator|--
expr_stmt|;
break|break;
default|default:
break|break
name|outer
break|;
block|}
block|}
name|int
name|exp
init|=
literal|0
decl_stmt|;
comment|/******************************************************      * remove RHS zero normalization since it only helps 1 in 100      * numbers and complicates both encoding and decoding.      // remove pairs of zeros on the RHS and keep track of     // the count.     while (start<= end) {       char val = arr[end];        if (val=='0'&& start<= end) {         val=arr[end-1];         if (val=='0') {           hundreds++;           end-=2;           continue;         }       }        break;     }     *************************************************************/
comment|// now start at the end and work our way forward
comment|// encoding two base 10 digits into 1 base 100 digit
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|int
name|val
init|=
name|arr
index|[
name|end
operator|--
index|]
operator|-
literal|'0'
decl_stmt|;
comment|// ones
if|if
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|val
operator|+=
operator|(
name|arr
index|[
name|end
operator|--
index|]
operator|-
literal|'0'
operator|)
operator|*
literal|10
expr_stmt|;
comment|// tens
if|if
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|val
operator|+=
operator|(
name|arr
index|[
name|end
operator|--
index|]
operator|-
literal|'0'
operator|)
operator|*
literal|100
expr_stmt|;
comment|// hundreds
if|if
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|val
operator|+=
operator|(
name|arr
index|[
name|end
operator|--
index|]
operator|-
literal|'0'
operator|)
operator|*
literal|1000
expr_stmt|;
comment|// thousands
block|}
block|}
block|}
name|out
index|[
operator|--
name|wpos
index|]
operator|=
name|neg
condition|?
call|(
name|char
call|)
argument_list|(
literal|9999
operator|-
name|val
argument_list|)
else|:
operator|(
name|char
operator|)
name|val
expr_stmt|;
block|}
comment|/****** FUTURE: not needed for this implementation of exponent combined with sign     // normalize all zeros to positive values     if (wpos==outend) neg=false;     ******/
comment|// adjust exponent by the number of base 100 chars written
name|exp
operator|+=
name|outend
operator|-
name|wpos
expr_stmt|;
comment|// write the exponent and sign combined
name|out
index|[
operator|--
name|wpos
index|]
operator|=
name|neg
condition|?
call|(
name|char
call|)
argument_list|(
name|ZERO_EXPONENT
operator|-
name|exp
argument_list|)
else|:
call|(
name|char
call|)
argument_list|(
name|ZERO_EXPONENT
operator|+
name|exp
argument_list|)
expr_stmt|;
return|return
name|outend
operator|-
name|wpos
return|;
comment|// the length of the base100 int
block|}
comment|// Converts a base100 sortable number to base10 character form
comment|// returns number of chars written.
comment|// At least 1 char is always written.
DECL|method|base10kSortableIntToBase10
specifier|public
specifier|static
name|int
name|base10kSortableIntToBase10
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
comment|// Take care of "0" case first.  It's the only number that is represented
comment|// in one char since we don't chop trailing zeros.
if|if
condition|(
name|end
operator|-
name|start
operator|==
literal|1
condition|)
block|{
name|out
index|[
name|offset
index|]
operator|=
literal|'0'
expr_stmt|;
return|return
literal|1
return|;
block|}
name|int
name|wpos
init|=
name|offset
decl_stmt|;
comment|// write position
name|boolean
name|neg
decl_stmt|;
name|int
name|exp
init|=
name|arr
index|[
name|start
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|exp
operator|<
name|ZERO_EXPONENT
condition|)
block|{
name|neg
operator|=
literal|true
expr_stmt|;
comment|// We don't currently use exp on decoding...
comment|// exp = ZERO_EXPONENT - exp;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
literal|'-'
expr_stmt|;
block|}
else|else
block|{
name|neg
operator|=
literal|false
expr_stmt|;
block|}
comment|// since so many values will fall in one char, pull it
comment|// out of the loop (esp since the first value must
comment|// be special-cased to not print leading zeros.
comment|// integer division is still expensive, so it's best to check
comment|// if you actually need to do it.
comment|//
comment|// TIP: write a small function in gcc or cl and see what
comment|// the optimized assemply output looks like (and which is fastest).
comment|// In C you can specify "unsigned" which gives the compiler more
comment|// info than the Java compiler has.
name|int
name|val
init|=
name|arr
index|[
name|start
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|neg
condition|)
name|val
operator|=
literal|9999
operator|-
name|val
expr_stmt|;
comment|/***     if (val< 10) {       out[wpos++] = (char)(val + '0');     } else if (val< 100) {       out[wpos++] = (char)(val/10 + '0');       out[wpos++] = (char)(val%10 + '0');     } else if (val< 1000) {       out[wpos++] = (char)(val/100 + '0');       out[wpos++] = (char)((val/10)%10 + '0');       out[wpos++] = (char)(val%10 + '0');     } else {       out[wpos++] = (char)(val/1000 + '0');       out[wpos++] = (char)((val/100)%10 + '0');       out[wpos++] = (char)((val/10)%10 + '0');       out[wpos++] = (char)(val % 10 + '0');     }     ***/
if|if
condition|(
name|val
operator|<
literal|10
condition|)
block|{
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|val
operator|+
literal|'0'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|<
literal|100
condition|)
block|{
name|int
name|div
init|=
name|div10
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|int
name|ones
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|div
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|ones
operator|+
literal|'0'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|<
literal|1000
condition|)
block|{
name|int
name|div
init|=
name|div10
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|int
name|ones
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|val
operator|=
name|div
expr_stmt|;
name|div
operator|=
name|div10
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|int
name|tens
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|div
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|tens
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|ones
operator|+
literal|'0'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|div
init|=
name|div10
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|int
name|ones
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|val
operator|=
name|div
expr_stmt|;
name|div
operator|=
name|div10
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|int
name|tens
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|val
operator|=
name|div
expr_stmt|;
name|div
operator|=
name|div10
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|int
name|hundreds
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|div
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|hundreds
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|tens
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|ones
operator|+
literal|'0'
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|start
operator|<
name|end
condition|)
block|{
name|val
operator|=
name|arr
index|[
name|start
operator|++
index|]
expr_stmt|;
if|if
condition|(
name|neg
condition|)
name|val
operator|=
literal|9999
operator|-
name|val
expr_stmt|;
name|int
name|div
init|=
name|div10
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|int
name|ones
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|val
operator|=
name|div
expr_stmt|;
name|div
operator|=
name|div10
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|int
name|tens
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
name|val
operator|=
name|div
expr_stmt|;
name|div
operator|=
name|div10
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|int
name|hundreds
init|=
name|val
operator|-
name|mul10
argument_list|(
name|div
argument_list|)
decl_stmt|;
comment|// mod 10
comment|/***       int ones = val % 10;       val /= 10;       int tens = val!=0 ? val % 10 : 0;       val /= 10;       int hundreds = val!=0 ? val % 10 : 0;       val /= 10;       int thousands = val!=0 ? val % 10 : 0;       ***/
comment|/***       int thousands = val>=1000 ? val/1000 : 0;       int hundreds  = val>=100 ? (val/100)%10 : 0;       int tens      = val>=10 ? (val/10)%10 : 0;       int ones      = val % 10;       ***/
comment|/***       int thousands =  val/1000;       int hundreds  = (val/100)%10;       int tens      = (val/10)%10;       int ones      = val % 10;       ***/
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|div
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|hundreds
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|tens
operator|+
literal|'0'
argument_list|)
expr_stmt|;
name|out
index|[
name|wpos
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
name|ones
operator|+
literal|'0'
argument_list|)
expr_stmt|;
block|}
comment|// OPTIONAL: if trailing zeros were truncated, then this is where
comment|// we would restore them (compare number of chars read vs exponent)
return|return
name|wpos
operator|-
name|offset
return|;
block|}
block|}
end_class

end_unit

