begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/** Represents byte[], as a slice (offset + length) into an  *  existing byte[].  The {@link #bytes} member should never be null;  *  use {@link #EMPTY_BYTES} if necessary.  *  *<p><b>Important note:</b> Unless otherwise noted, Lucene uses this class to  * represent terms that are encoded as<b>UTF8</b> bytes in the index. To  * convert them to a Java {@link String} (which is UTF16), use {@link #utf8ToString}.  * Using code like {@code new String(bytes, offset, length)} to do this  * is<b>wrong</b>, as it does not respect the correct character set  * and may return wrong results (depending on the platform's defaults)!  *   *<p>{@code BytesRef} implements {@link Comparable}. The underlying byte arrays  * are sorted lexicographically, numerically treating elements as unsigned.  * This is identical to Unicode codepoint order.  */
end_comment

begin_class
DECL|class|BytesRef
specifier|public
specifier|final
class|class
name|BytesRef
implements|implements
name|Comparable
argument_list|<
name|BytesRef
argument_list|>
implements|,
name|Cloneable
block|{
comment|/** An empty byte array for convenience */
DECL|field|EMPTY_BYTES
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/** The contents of the BytesRef. Should never be {@code null}. */
DECL|field|bytes
specifier|public
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/** Offset of first valid byte. */
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
comment|/** Length of used bytes. */
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
comment|/** Create a BytesRef with {@link #EMPTY_BYTES} */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_BYTES
argument_list|)
expr_stmt|;
block|}
comment|/** This instance will directly reference bytes w/o making a copy.    * bytes should not be null.    */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
assert|assert
name|isValid
argument_list|()
assert|;
block|}
comment|/** This instance will directly reference bytes w/o making a copy.    * bytes should not be null */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**     * Create a BytesRef pointing to a new array of size<code>capacity</code>.    * Offset and length will both be zero.    */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/**    * Initialize the byte[] from the UTF8 bytes    * for the provided String.      *     * @param text This must be well-formed    * unicode text, with no unpaired surrogates.    */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|byte
index|[
name|UnicodeUtil
operator|.
name|maxUTF8Length
argument_list|(
name|text
operator|.
name|length
argument_list|()
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|length
operator|=
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: compares the bytes against another BytesRef,    * returning true if the bytes are equal.    *     * @param other Another BytesRef, should not be null.    * @lucene.internal    */
DECL|method|bytesEquals
specifier|public
name|boolean
name|bytesEquals
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
assert|assert
name|other
operator|!=
literal|null
assert|;
if|if
condition|(
name|length
operator|==
name|other
operator|.
name|length
condition|)
block|{
name|int
name|otherUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|otherBytes
init|=
name|other
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|upto
init|=
name|offset
init|;
name|upto
operator|<
name|end
condition|;
name|upto
operator|++
operator|,
name|otherUpto
operator|++
control|)
block|{
if|if
condition|(
name|bytes
index|[
name|upto
index|]
operator|!=
name|otherBytes
index|[
name|otherUpto
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Returns a shallow clone of this instance (the underlying bytes are    *<b>not</b> copied and will be shared by both the returned object and this    * object.    *     * @see #deepCopyOf    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|BytesRef
name|clone
parameter_list|()
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/** Calculates the hash code as required by TermsHash during indexing.    *<p> This is currently implemented as MurmurHash3 (32    *  bit), using the seed from {@link    *  StringHelper#GOOD_FAST_HASH_SEED}, but is subject to    *  change from release to release. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|StringHelper
operator|.
name|murmurhash3_x86_32
argument_list|(
name|this
argument_list|,
name|StringHelper
operator|.
name|GOOD_FAST_HASH_SEED
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
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
name|this
operator|.
name|bytesEquals
argument_list|(
operator|(
name|BytesRef
operator|)
name|other
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Interprets stored bytes as UTF8 bytes, returning the    *  resulting string */
DECL|method|utf8ToString
specifier|public
name|String
name|utf8ToString
parameter_list|()
block|{
specifier|final
name|char
index|[]
name|ref
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|ref
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|ref
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/** Returns hex encoded bytes, eg [0x6c 0x75 0x63 0x65 0x6e 0x65] */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
name|offset
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|bytes
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Unsigned byte order comparison */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
comment|// TODO: Once we are on Java 9 replace this by java.util.Arrays#compareUnsigned()
comment|// which is implemented by a Hotspot intrinsic! Also consider building a
comment|// Multi-Release-JAR!
specifier|final
name|byte
index|[]
name|aBytes
init|=
name|this
operator|.
name|bytes
decl_stmt|;
name|int
name|aUpto
init|=
name|this
operator|.
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|bBytes
init|=
name|other
operator|.
name|bytes
decl_stmt|;
name|int
name|bUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|aStop
init|=
name|aUpto
operator|+
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|length
argument_list|,
name|other
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|int
name|aByte
init|=
name|aBytes
index|[
name|aUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|bByte
init|=
name|bBytes
index|[
name|bUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|diff
init|=
name|aByte
operator|-
name|bByte
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|this
operator|.
name|length
operator|-
name|other
operator|.
name|length
return|;
block|}
comment|/**    * Creates a new BytesRef that points to a copy of the bytes from     *<code>other</code>    *<p>    * The returned BytesRef will have a length of other.length    * and an offset of zero.    */
DECL|method|deepCopyOf
specifier|public
specifier|static
name|BytesRef
name|deepCopyOf
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
name|BytesRef
name|copy
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|copy
operator|.
name|bytes
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|other
operator|.
name|bytes
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|copy
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|copy
operator|.
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|/**     * Performs internal consistency checks.    * Always returns true (or throws IllegalStateException)     */
DECL|method|isValid
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"bytes is null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"length is negative: "
operator|+
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"length is out of bounds: "
operator|+
name|length
operator|+
literal|",bytes.length="
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset is negative: "
operator|+
name|offset
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset out of bounds: "
operator|+
name|offset
operator|+
literal|",bytes.length="
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|+
name|length
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset+length is negative: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|+
name|length
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"offset+length out of bounds: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
operator|+
literal|",bytes.length="
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

