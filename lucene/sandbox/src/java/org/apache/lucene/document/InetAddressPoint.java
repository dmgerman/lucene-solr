begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|search
operator|.
name|PointRangeQuery
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
name|search
operator|.
name|Query
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
comment|/**   * A field indexing {@link InetAddress} dimensionally such that finding  * all documents within a range at search time is  * efficient.  Multiple values for the same field in one document  * is allowed.   *<p>  * This field defines static factory methods for creating common queries:  *<ul>  *<li>{@link #newExactQuery newExactQuery()} for matching an exact network address.  *<li>{@link #newPrefixQuery newPrefixQuery()} for matching a network based on CIDR prefix.  *<li>{@link #newRangeQuery newRangeQuery()} for matching arbitrary network address ranges.  *<li>{@link #newSetQuery newSetQuery()} for matching a set of addresses.  *</ul>  *<p>  * This field supports both IPv4 and IPv6 addresses: IPv4 addresses are converted  * to<a href="https://tools.ietf.org/html/rfc4291#section-2.5.5">IPv4-Mapped IPv6 Addresses</a>:  * indexing {@code 1.2.3.4} is the same as indexing {@code ::FFFF:1.2.3.4}.  */
end_comment

begin_class
DECL|class|InetAddressPoint
specifier|public
class|class
name|InetAddressPoint
extends|extends
name|Field
block|{
comment|// implementation note: we convert all addresses to IPv6: we expect prefix compression of values,
comment|// so its not wasteful, but allows one field to handle both IPv4 and IPv6.
comment|/** The number of bytes per dimension: 128 bits */
DECL|field|BYTES
specifier|public
specifier|static
specifier|final
name|int
name|BYTES
init|=
literal|16
decl_stmt|;
comment|// rfc4291 prefix
DECL|field|IPV4_PREFIX
specifier|static
specifier|final
name|byte
index|[]
name|IPV4_PREFIX
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
operator|-
literal|1
block|,
operator|-
literal|1
block|}
decl_stmt|;
DECL|field|TYPE
specifier|private
specifier|static
specifier|final
name|FieldType
name|TYPE
decl_stmt|;
static|static
block|{
name|TYPE
operator|=
operator|new
name|FieldType
argument_list|()
expr_stmt|;
name|TYPE
operator|.
name|setDimensions
argument_list|(
literal|1
argument_list|,
name|BYTES
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Change the values of this field */
DECL|method|setInetAddressValue
specifier|public
name|void
name|setInetAddressValue
parameter_list|(
name|InetAddress
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be null"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
operator|new
name|BytesRef
argument_list|(
name|encode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBytesValue
specifier|public
name|void
name|setBytesValue
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from InetAddress to BytesRef"
argument_list|)
throw|;
block|}
comment|/** Creates a new InetAddressPoint, indexing the    *  provided address.    *    *  @param name field name    *  @param point InetAddress value    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|InetAddressPoint
specifier|public
name|InetAddressPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|InetAddress
name|point
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|setInetAddressValue
argument_list|(
name|point
argument_list|)
expr_stmt|;
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
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
comment|// IPv6 addresses are bracketed, to not cause confusion with historic field:value representation
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
name|InetAddress
name|address
init|=
name|decode
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
operator|.
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|length
operator|==
literal|16
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|address
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
name|address
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// public helper methods (e.g. for queries)
comment|/** Encode InetAddress value into binary encoding */
DECL|method|encode
specifier|public
specifier|static
name|byte
index|[]
name|encode
parameter_list|(
name|InetAddress
name|value
parameter_list|)
block|{
name|byte
index|[]
name|address
init|=
name|value
operator|.
name|getAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|byte
index|[]
name|mapped
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|IPV4_PREFIX
argument_list|,
literal|0
argument_list|,
name|mapped
argument_list|,
literal|0
argument_list|,
name|IPV4_PREFIX
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|address
argument_list|,
literal|0
argument_list|,
name|mapped
argument_list|,
name|IPV4_PREFIX
operator|.
name|length
argument_list|,
name|address
operator|.
name|length
argument_list|)
expr_stmt|;
name|address
operator|=
name|mapped
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|address
operator|.
name|length
operator|!=
literal|16
condition|)
block|{
comment|// more of an assertion, how did you create such an InetAddress :)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Only IPv4 and IPv6 addresses are supported"
argument_list|)
throw|;
block|}
return|return
name|address
return|;
block|}
comment|/** Decodes InetAddress value from binary encoding */
DECL|method|decode
specifier|public
specifier|static
name|InetAddress
name|decode
parameter_list|(
name|byte
name|value
index|[]
parameter_list|)
block|{
try|try
block|{
return|return
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
comment|// this only happens if value.length != 4 or 16, strange exception class
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"encoded bytes are of incorrect length"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// static methods for generating queries
comment|/**     * Create a query for matching a network address.    *    * @param field field name. must not be {@code null}.    * @param value exact value    * @throws IllegalArgumentException if {@code field} is null.    * @return a query matching documents with this exact value    */
DECL|method|newExactQuery
specifier|public
specifier|static
name|Query
name|newExactQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|InetAddress
name|value
parameter_list|)
block|{
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
name|value
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**     * Create a prefix query for matching a CIDR network range.    *    * @param field field name. must not be {@code null}.    * @param value any host address    * @param prefixLength the network prefix length for this address. This is also known as the subnet mask in the context of IPv4 addresses.    * @throws IllegalArgumentException if {@code field} is null, or prefixLength is invalid.    * @return a query matching documents with addresses contained within this network    */
DECL|method|newPrefixQuery
specifier|public
specifier|static
name|Query
name|newPrefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|InetAddress
name|value
parameter_list|,
name|int
name|prefixLength
parameter_list|)
block|{
if|if
condition|(
name|prefixLength
argument_list|<
literal|0
operator|||
name|prefixLength
argument_list|>
literal|8
operator|*
name|value
operator|.
name|getAddress
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal prefixLength '"
operator|+
name|prefixLength
operator|+
literal|"'. Must be 0-32 for IPv4 ranges, 0-128 for IPv6 ranges"
argument_list|)
throw|;
block|}
comment|// create the lower value by zeroing out the host portion, upper value by filling it with all ones.
name|byte
name|lower
index|[]
init|=
name|value
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|byte
name|upper
index|[]
init|=
name|value
operator|.
name|getAddress
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|prefixLength
init|;
name|i
operator|<
literal|8
operator|*
name|lower
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|lower
index|[
name|i
operator|>>
literal|3
index|]
operator|&=
operator|~
operator|(
literal|1
operator|<<
operator|(
name|i
operator|&
literal|7
operator|)
operator|)
expr_stmt|;
name|upper
index|[
name|i
operator|>>
literal|3
index|]
operator||=
literal|1
operator|<<
operator|(
name|i
operator|&
literal|7
operator|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|newRangeQuery
argument_list|(
name|field
argument_list|,
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|lower
argument_list|)
argument_list|,
literal|true
argument_list|,
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
comment|// values are coming from InetAddress
block|}
block|}
comment|/**     * Create a range query for network addresses.    *<p>    * You can have half-open ranges (which are in fact&lt;/&le; or&gt;/&ge; queries)    * by setting the {@code lowerValue} or {@code upperValue} to {@code null}.     *<p>    * By setting inclusive ({@code lowerInclusive} or {@code upperInclusive}) to false, it will    * match all documents excluding the bounds, with inclusive on, the boundaries are hits, too.    *    * @param field field name. must not be {@code null}.    * @param lowerValue lower portion of the range. {@code null} means "open".    * @param lowerInclusive {@code true} if the lower portion of the range is inclusive, {@code false} if it should be excluded.    * @param upperValue upper portion of the range. {@code null} means "open".    * @param upperInclusive {@code true} if the upper portion of the range is inclusive, {@code false} if it should be excluded.    * @throws IllegalArgumentException if {@code field} is null.    * @return a query matching documents within this range.    */
DECL|method|newRangeQuery
specifier|public
specifier|static
name|Query
name|newRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|InetAddress
name|lowerValue
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|InetAddress
name|upperValue
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|lowerBytes
init|=
operator|new
name|byte
index|[
literal|1
index|]
index|[]
decl_stmt|;
if|if
condition|(
name|lowerValue
operator|!=
literal|null
condition|)
block|{
name|lowerBytes
index|[
literal|0
index|]
operator|=
name|encode
argument_list|(
name|lowerValue
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
index|[]
name|upperBytes
init|=
operator|new
name|byte
index|[
literal|1
index|]
index|[]
decl_stmt|;
if|if
condition|(
name|upperValue
operator|!=
literal|null
condition|)
block|{
name|upperBytes
index|[
literal|0
index|]
operator|=
name|encode
argument_list|(
name|upperValue
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PointRangeQuery
argument_list|(
name|field
argument_list|,
name|lowerBytes
argument_list|,
operator|new
name|boolean
index|[]
block|{
name|lowerInclusive
block|}
argument_list|,
name|upperBytes
argument_list|,
operator|new
name|boolean
index|[]
block|{
name|upperInclusive
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|int
name|dimension
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
name|decode
argument_list|(
name|value
argument_list|)
operator|.
name|getHostAddress
argument_list|()
return|;
comment|// for ranges, the range itself is already bracketed
block|}
block|}
return|;
block|}
comment|/**    * Create a query matching any of the specified 1D values.  This is the points equivalent of {@code TermsQuery}.    *     * @param field field name. must not be {@code null}.    * @param valuesIn all int values to match    */
DECL|method|newSetQuery
specifier|public
specifier|static
name|Query
name|newSetQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|InetAddress
modifier|...
name|valuesIn
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Don't unexpectedly change the user's incoming values array:
name|InetAddress
index|[]
name|values
init|=
name|valuesIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|values
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|value
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
name|BYTES
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|PointInSetQuery
argument_list|(
name|field
argument_list|,
literal|1
argument_list|,
name|BYTES
argument_list|,
operator|new
name|BytesRefIterator
argument_list|()
block|{
name|int
name|upto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|==
name|values
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|encode
argument_list|(
name|values
index|[
name|upto
index|]
argument_list|,
name|value
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toString
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
assert|assert
name|value
operator|.
name|length
operator|==
name|BYTES
assert|;
return|return
name|decode
argument_list|(
name|value
argument_list|)
operator|.
name|getHostAddress
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

