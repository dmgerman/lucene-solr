begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|IndexReader
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
name|NumericUtils
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
name|document
operator|.
name|NumericField
import|;
end_import

begin_comment
comment|// for javadocs
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
name|NumericTokenStream
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

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
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_comment
comment|/**  * Expert: Maintains caches of term values.  *  *<p>Created: May 19, 2004 11:13:14 AM  *  * @since   lucene 1.4  * @see org.apache.lucene.util.FieldCacheSanityChecker  */
end_comment

begin_interface
DECL|interface|FieldCache
specifier|public
interface|interface
name|FieldCache
block|{
DECL|class|CreationPlaceholder
specifier|public
specifier|static
specifier|final
class|class
name|CreationPlaceholder
block|{
DECL|field|value
name|Object
name|value
decl_stmt|;
block|}
comment|/** Indicator for StringIndex values in the cache. */
comment|// NOTE: the value assigned to this constant must not be
comment|// the same as any of those in SortField!!
DECL|field|STRING_INDEX
specifier|public
specifier|static
specifier|final
name|int
name|STRING_INDEX
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Expert: Stores term text values and document ordering data. */
DECL|class|StringIndex
specifier|public
specifier|static
class|class
name|StringIndex
block|{
DECL|method|binarySearchLookup
specifier|public
name|int
name|binarySearchLookup
parameter_list|(
name|String
name|key
parameter_list|)
block|{
comment|// this special case is the reason that Arrays.binarySearch() isn't useful.
if|if
condition|(
name|key
operator|==
literal|null
condition|)
return|return
literal|0
return|;
name|int
name|low
init|=
literal|1
decl_stmt|;
name|int
name|high
init|=
name|lookup
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|cmp
init|=
name|lookup
index|[
name|mid
index|]
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
return|return
name|mid
return|;
comment|// key found
block|}
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
comment|// key not found.
block|}
comment|/** All the term values, in natural order. */
DECL|field|lookup
specifier|public
specifier|final
name|String
index|[]
name|lookup
decl_stmt|;
comment|/** For each document, an index into the lookup array. */
DECL|field|order
specifier|public
specifier|final
name|int
index|[]
name|order
decl_stmt|;
comment|/** Creates one of these objects */
DECL|method|StringIndex
specifier|public
name|StringIndex
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|String
index|[]
name|lookup
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
block|}
block|}
comment|/**    * Marker interface as super-interface to all parsers. It    * is used to specify a custom parser to {@link    * SortField#SortField(String, FieldCache.Parser)}.    */
DECL|interface|Parser
specifier|public
interface|interface
name|Parser
extends|extends
name|Serializable
block|{   }
comment|/** Interface to parse bytes from document fields.    * @see FieldCache#getBytes(IndexReader, String, FieldCache.ByteParser)    */
DECL|interface|ByteParser
specifier|public
interface|interface
name|ByteParser
extends|extends
name|Parser
block|{
comment|/** Return a single Byte representation of this field's value. */
DECL|method|parseByte
specifier|public
name|byte
name|parseByte
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
comment|/** Interface to parse shorts from document fields.    * @see FieldCache#getShorts(IndexReader, String, FieldCache.ShortParser)    */
DECL|interface|ShortParser
specifier|public
interface|interface
name|ShortParser
extends|extends
name|Parser
block|{
comment|/** Return a short representation of this field's value. */
DECL|method|parseShort
specifier|public
name|short
name|parseShort
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
comment|/** Interface to parse ints from document fields.    * @see FieldCache#getInts(IndexReader, String, FieldCache.IntParser)    */
DECL|interface|IntParser
specifier|public
interface|interface
name|IntParser
extends|extends
name|Parser
block|{
comment|/** Return an integer representation of this field's value. */
DECL|method|parseInt
specifier|public
name|int
name|parseInt
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
comment|/** Interface to parse floats from document fields.    * @see FieldCache#getFloats(IndexReader, String, FieldCache.FloatParser)    */
DECL|interface|FloatParser
specifier|public
interface|interface
name|FloatParser
extends|extends
name|Parser
block|{
comment|/** Return an float representation of this field's value. */
DECL|method|parseFloat
specifier|public
name|float
name|parseFloat
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
comment|/** Interface to parse long from document fields.    * @see FieldCache#getLongs(IndexReader, String, FieldCache.LongParser)    */
DECL|interface|LongParser
specifier|public
interface|interface
name|LongParser
extends|extends
name|Parser
block|{
comment|/** Return an long representation of this field's value. */
DECL|method|parseLong
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
comment|/** Interface to parse doubles from document fields.    * @see FieldCache#getDoubles(IndexReader, String, FieldCache.DoubleParser)    */
DECL|interface|DoubleParser
specifier|public
interface|interface
name|DoubleParser
extends|extends
name|Parser
block|{
comment|/** Return an long representation of this field's value. */
DECL|method|parseDouble
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
block|}
comment|/** Expert: The cache used internally by sorting and range query classes. */
DECL|field|DEFAULT
specifier|public
specifier|static
name|FieldCache
name|DEFAULT
init|=
operator|new
name|FieldCacheImpl
argument_list|()
decl_stmt|;
comment|/** The default parser for byte values, which are encoded by {@link Byte#toString(byte)} */
DECL|field|DEFAULT_BYTE_PARSER
specifier|public
specifier|static
specifier|final
name|ByteParser
name|DEFAULT_BYTE_PARSER
init|=
operator|new
name|ByteParser
argument_list|()
block|{
specifier|public
name|byte
name|parseByte
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Byte
operator|.
name|parseByte
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|DEFAULT_BYTE_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".DEFAULT_BYTE_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/** The default parser for short values, which are encoded by {@link Short#toString(short)} */
DECL|field|DEFAULT_SHORT_PARSER
specifier|public
specifier|static
specifier|final
name|ShortParser
name|DEFAULT_SHORT_PARSER
init|=
operator|new
name|ShortParser
argument_list|()
block|{
specifier|public
name|short
name|parseShort
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Short
operator|.
name|parseShort
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|DEFAULT_SHORT_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".DEFAULT_SHORT_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/** The default parser for int values, which are encoded by {@link Integer#toString(int)} */
DECL|field|DEFAULT_INT_PARSER
specifier|public
specifier|static
specifier|final
name|IntParser
name|DEFAULT_INT_PARSER
init|=
operator|new
name|IntParser
argument_list|()
block|{
specifier|public
name|int
name|parseInt
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|DEFAULT_INT_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".DEFAULT_INT_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/** The default parser for float values, which are encoded by {@link Float#toString(float)} */
DECL|field|DEFAULT_FLOAT_PARSER
specifier|public
specifier|static
specifier|final
name|FloatParser
name|DEFAULT_FLOAT_PARSER
init|=
operator|new
name|FloatParser
argument_list|()
block|{
specifier|public
name|float
name|parseFloat
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|DEFAULT_FLOAT_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".DEFAULT_FLOAT_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/** The default parser for long values, which are encoded by {@link Long#toString(long)} */
DECL|field|DEFAULT_LONG_PARSER
specifier|public
specifier|static
specifier|final
name|LongParser
name|DEFAULT_LONG_PARSER
init|=
operator|new
name|LongParser
argument_list|()
block|{
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|DEFAULT_LONG_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".DEFAULT_LONG_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/** The default parser for double values, which are encoded by {@link Double#toString(double)} */
DECL|field|DEFAULT_DOUBLE_PARSER
specifier|public
specifier|static
specifier|final
name|DoubleParser
name|DEFAULT_DOUBLE_PARSER
init|=
operator|new
name|DoubleParser
argument_list|()
block|{
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|DEFAULT_DOUBLE_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".DEFAULT_DOUBLE_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for int values encoded by {@link NumericUtils#intToPrefixCoded(int)}, e.g. when indexed    * via {@link NumericField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_INT_PARSER
specifier|public
specifier|static
specifier|final
name|IntParser
name|NUMERIC_UTILS_INT_PARSER
init|=
operator|new
name|IntParser
argument_list|()
block|{
specifier|public
name|int
name|parseInt
parameter_list|(
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|NumericUtils
operator|.
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|31
condition|)
throw|throw
operator|new
name|FieldCacheImpl
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|val
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|NUMERIC_UTILS_INT_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_INT_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for float values encoded with {@link NumericUtils}, e.g. when indexed    * via {@link NumericField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_FLOAT_PARSER
specifier|public
specifier|static
specifier|final
name|FloatParser
name|NUMERIC_UTILS_FLOAT_PARSER
init|=
operator|new
name|FloatParser
argument_list|()
block|{
specifier|public
name|float
name|parseFloat
parameter_list|(
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|NumericUtils
operator|.
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|31
condition|)
throw|throw
operator|new
name|FieldCacheImpl
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|NUMERIC_UTILS_FLOAT_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_FLOAT_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for long values encoded by {@link NumericUtils#longToPrefixCoded(long)}, e.g. when indexed    * via {@link NumericField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_LONG_PARSER
specifier|public
specifier|static
specifier|final
name|LongParser
name|NUMERIC_UTILS_LONG_PARSER
init|=
operator|new
name|LongParser
argument_list|()
block|{
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|NumericUtils
operator|.
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|63
condition|)
throw|throw
operator|new
name|FieldCacheImpl
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|val
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|NUMERIC_UTILS_LONG_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_LONG_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/**    * A parser instance for double values encoded with {@link NumericUtils}, e.g. when indexed    * via {@link NumericField}/{@link NumericTokenStream}.    */
DECL|field|NUMERIC_UTILS_DOUBLE_PARSER
specifier|public
specifier|static
specifier|final
name|DoubleParser
name|NUMERIC_UTILS_DOUBLE_PARSER
init|=
operator|new
name|DoubleParser
argument_list|()
block|{
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|-
name|NumericUtils
operator|.
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|0
operator|&&
name|shift
operator|<=
literal|63
condition|)
throw|throw
operator|new
name|FieldCacheImpl
operator|.
name|StopFillCacheException
argument_list|()
throw|;
return|return
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|Object
name|readResolve
parameter_list|()
block|{
return|return
name|NUMERIC_UTILS_DOUBLE_PARSER
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|FieldCache
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".NUMERIC_UTILS_DOUBLE_PARSER"
return|;
block|}
block|}
decl_stmt|;
comment|/** Checks the internal cache for an appropriate entry, and if none is    * found, reads the terms in<code>field</code> as a single byte and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the single byte values.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none is found,    * reads the terms in<code>field</code> as bytes and returns an array of    * size<code>reader.maxDoc()</code> of the value each document has in the    * given field.    * @param reader  Used to get field values.    * @param field   Which field contains the bytes.    * @param parser  Computes byte for string values.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ByteParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none is    * found, reads the terms in<code>field</code> as shorts and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the shorts.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getShorts
specifier|public
name|short
index|[]
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none is found,    * reads the terms in<code>field</code> as shorts and returns an array of    * size<code>reader.maxDoc()</code> of the value each document has in the    * given field.    * @param reader  Used to get field values.    * @param field   Which field contains the shorts.    * @param parser  Computes short for string values.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getShorts
specifier|public
name|short
index|[]
name|getShorts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ShortParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none is    * found, reads the terms in<code>field</code> as integers and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the integers.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none is found,    * reads the terms in<code>field</code> as integers and returns an array of    * size<code>reader.maxDoc()</code> of the value each document has in the    * given field.    * @param reader  Used to get field values.    * @param field   Which field contains the integers.    * @param parser  Computes integer for string values.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getInts
specifier|public
name|int
index|[]
name|getInts
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|IntParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if    * none is found, reads the terms in<code>field</code> as floats and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the floats.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if    * none is found, reads the terms in<code>field</code> as floats and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the floats.    * @param parser  Computes float for string values.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getFloats
specifier|public
name|float
index|[]
name|getFloats
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|FloatParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks the internal cache for an appropriate entry, and if none is    * found, reads the terms in<code>field</code> as longs and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    *    * @param reader Used to get field values.    * @param field  Which field contains the longs.    * @return The values in the given field for each document.    * @throws java.io.IOException If any error occurs.    */
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks the internal cache for an appropriate entry, and if none is found,    * reads the terms in<code>field</code> as longs and returns an array of    * size<code>reader.maxDoc()</code> of the value each document has in the    * given field.    *    * @param reader Used to get field values.    * @param field  Which field contains the longs.    * @param parser Computes integer for string values.    * @return The values in the given field for each document.    * @throws IOException If any error occurs.    */
DECL|method|getLongs
specifier|public
name|long
index|[]
name|getLongs
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|LongParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks the internal cache for an appropriate entry, and if none is    * found, reads the terms in<code>field</code> as integers and returns an array    * of size<code>reader.maxDoc()</code> of the value each document    * has in the given field.    *    * @param reader Used to get field values.    * @param field  Which field contains the doubles.    * @return The values in the given field for each document.    * @throws IOException If any error occurs.    */
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks the internal cache for an appropriate entry, and if none is found,    * reads the terms in<code>field</code> as doubles and returns an array of    * size<code>reader.maxDoc()</code> of the value each document has in the    * given field.    *    * @param reader Used to get field values.    * @param field  Which field contains the doubles.    * @param parser Computes integer for string values.    * @return The values in the given field for each document.    * @throws IOException If any error occurs.    */
DECL|method|getDoubles
specifier|public
name|double
index|[]
name|getDoubles
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|DoubleParser
name|parser
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found, reads the term values in<code>field</code> and returns an array    * of size<code>reader.maxDoc()</code> containing the value each document    * has in the given field.    * @param reader  Used to get field values.    * @param field   Which field contains the strings.    * @return The values in the given field for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getStrings
specifier|public
name|String
index|[]
name|getStrings
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Checks the internal cache for an appropriate entry, and if none    * is found reads the term values in<code>field</code> and returns    * an array of them in natural order, along with an array telling    * which element in the term array each document uses.    * @param reader  Used to get field values.    * @param field   Which field contains the strings.    * @return Array of terms and index into the array for each document.    * @throws IOException  If any error occurs.    */
DECL|method|getStringIndex
specifier|public
name|StringIndex
name|getStringIndex
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * EXPERT: A unique Identifier/Description for each item in the FieldCache.     * Can be useful for logging/debugging.    *<p>    *<b>EXPERIMENTAL API:</b> This API is considered extremely advanced     * and experimental.  It may be removed or altered w/o warning in future     * releases     * of Lucene.    *</p>    */
DECL|class|CacheEntry
specifier|public
specifier|static
specifier|abstract
class|class
name|CacheEntry
block|{
DECL|method|getReaderKey
specifier|public
specifier|abstract
name|Object
name|getReaderKey
parameter_list|()
function_decl|;
DECL|method|getFieldName
specifier|public
specifier|abstract
name|String
name|getFieldName
parameter_list|()
function_decl|;
DECL|method|getCacheType
specifier|public
specifier|abstract
name|Class
name|getCacheType
parameter_list|()
function_decl|;
DECL|method|getCustom
specifier|public
specifier|abstract
name|Object
name|getCustom
parameter_list|()
function_decl|;
DECL|method|getValue
specifier|public
specifier|abstract
name|Object
name|getValue
parameter_list|()
function_decl|;
DECL|field|size
specifier|private
name|String
name|size
init|=
literal|null
decl_stmt|;
DECL|method|setEstimatedSize
specifier|protected
specifier|final
name|void
name|setEstimatedSize
parameter_list|(
name|String
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**       * @see #estimateSize(RamUsageEstimator)      */
DECL|method|estimateSize
specifier|public
name|void
name|estimateSize
parameter_list|()
block|{
name|estimateSize
argument_list|(
operator|new
name|RamUsageEstimator
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// doesn't check for interned
block|}
comment|/**       * Computes (and stores) the estimated size of the cache Value       * @see #getEstimatedSize      */
DECL|method|estimateSize
specifier|public
name|void
name|estimateSize
parameter_list|(
name|RamUsageEstimator
name|ramCalc
parameter_list|)
block|{
name|long
name|size
init|=
name|ramCalc
operator|.
name|estimateRamUsage
argument_list|(
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|setEstimatedSize
argument_list|(
name|RamUsageEstimator
operator|.
name|humanReadableUnits
argument_list|(
name|size
argument_list|,
operator|new
name|DecimalFormat
argument_list|(
literal|"0.#"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * The most recently estimated size of the value, null unless       * estimateSize has been called.      */
DECL|method|getEstimatedSize
specifier|public
specifier|final
name|String
name|getEstimatedSize
parameter_list|()
block|{
return|return
name|size
return|;
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
name|b
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
operator|.
name|append
argument_list|(
name|getReaderKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"'=>"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
operator|.
name|append
argument_list|(
name|getFieldName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"',"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|getCacheType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|getCustom
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"=>"
argument_list|)
operator|.
name|append
argument_list|(
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|System
operator|.
name|identityHashCode
argument_list|(
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|getEstimatedSize
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|s
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" (size =~ "
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
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
comment|/**    * EXPERT: Generates an array of CacheEntry objects representing all items     * currently in the FieldCache.    *<p>    * NOTE: These CacheEntry objects maintain a strong reference to the     * Cached Values.  Maintaining references to a CacheEntry the IndexReader     * associated with it has garbage collected will prevent the Value itself    * from being garbage collected when the Cache drops the WeakRefrence.    *</p>    *<p>    *<b>EXPERIMENTAL API:</b> This API is considered extremely advanced     * and experimental.  It may be removed or altered w/o warning in future     * releases     * of Lucene.    *</p>    */
DECL|method|getCacheEntries
specifier|public
specifier|abstract
name|CacheEntry
index|[]
name|getCacheEntries
parameter_list|()
function_decl|;
comment|/**    *<p>    * EXPERT: Instructs the FieldCache to forcibly expunge all entries     * from the underlying caches.  This is intended only to be used for     * test methods as a way to ensure a known base state of the Cache     * (with out needing to rely on GC to free WeakReferences).      * It should not be relied on for "Cache maintenance" in general     * application code.    *</p>    *<p>    *<b>EXPERIMENTAL API:</b> This API is considered extremely advanced     * and experimental.  It may be removed or altered w/o warning in future     * releases     * of Lucene.    *</p>    */
DECL|method|purgeAllCaches
specifier|public
specifier|abstract
name|void
name|purgeAllCaches
parameter_list|()
function_decl|;
comment|/**    * If non-null, FieldCacheImpl will warn whenever    * entries are created that are not sane according to    * {@link org.apache.lucene.util.FieldCacheSanityChecker}.    */
DECL|method|setInfoStream
specifier|public
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|stream
parameter_list|)
function_decl|;
comment|/** counterpart of {@link #setInfoStream(PrintStream)} */
DECL|method|getInfoStream
specifier|public
name|PrintStream
name|getInfoStream
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

