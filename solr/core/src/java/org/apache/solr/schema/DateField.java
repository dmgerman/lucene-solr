begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

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
operator|.
name|AtomicReaderContext
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
name|IndexableField
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
name|search
operator|.
name|SortField
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
name|TermRangeQuery
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
name|queries
operator|.
name|function
operator|.
name|DocValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|StringIndexDocValues
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|FieldCacheSource
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
name|CharsRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|DateUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|TextResponseWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DateMathParser
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
name|text
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|// TODO: make a FlexibleDateField that can accept dates in multiple
end_comment

begin_comment
comment|// formats, better for human entered dates.
end_comment

begin_comment
comment|// TODO: make a DayField that only stores the day?
end_comment

begin_comment
comment|/**  * FieldType that can represent any Date/Time with millisecond precision.  *<p>  * Date Format for the XML, incoming and outgoing:  *</p>  *<blockquote>  * A date field shall be of the form 1995-12-31T23:59:59Z  * The trailing "Z" designates UTC time and is mandatory  * (See below for an explanation of UTC).  * Optional fractional seconds are allowed, as long as they do not end  * in a trailing 0 (but any precision beyond milliseconds will be ignored).  * All other parts are mandatory.  *</blockquote>  *<p>  * This format was derived to be standards compliant (ISO 8601) and is a more  * restricted form of the  *<a href="http://www.w3.org/TR/xmlschema-2/#dateTime-canonical-representation">canonical  * representation of dateTime</a> from XML schema part 2.  Examples...  *</p>  *<ul>  *<li>1995-12-31T23:59:59Z</li>  *<li>1995-12-31T23:59:59.9Z</li>  *<li>1995-12-31T23:59:59.99Z</li>  *<li>1995-12-31T23:59:59.999Z</li>  *</ul>  *<p>  * Note that DateField is lenient with regards to parsing fractional  * seconds that end in trailing zeros and will ensure that those values  * are indexed in the correct canonical format.  *</p>  *<p>  * This FieldType also supports incoming "Date Math" strings for computing  * values by adding/rounding internals of time relative either an explicit  * datetime (in the format specified above) or the literal string "NOW",  * ie: "NOW+1YEAR", "NOW/DAY", "1995-12-31T23:59:59.999Z+5MINUTES", etc...  * -- see {@link DateMathParser} for more examples.  *</p>  *  *<p>  * Explanation of "UTC"...  *</p>  *<blockquote>  * "In 1970 the Coordinated Universal Time system was devised by an  * international advisory group of technical experts within the International  * Telecommunication Union (ITU).  The ITU felt it was best to designate a  * single abbreviation for use in all languages in order to minimize  * confusion.  Since unanimous agreement could not be achieved on using  * either the English word order, CUT, or the French word order, TUC, the  * acronym UTC was chosen as a compromise."  *</blockquote>  *  *  * @see<a href="http://www.w3.org/TR/xmlschema-2/#dateTime">XML schema part 2</a>  *  */
end_comment

begin_class
DECL|class|DateField
specifier|public
class|class
name|DateField
extends|extends
name|FieldType
block|{
DECL|field|UTC
specifier|public
specifier|static
name|TimeZone
name|UTC
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
decl_stmt|;
comment|/* :TODO: let Locale/TimeZone come from init args for rounding only */
comment|/** TimeZone for DateMath (UTC) */
DECL|field|MATH_TZ
specifier|protected
specifier|static
specifier|final
name|TimeZone
name|MATH_TZ
init|=
name|UTC
decl_stmt|;
comment|/** Locale for DateMath (Locale.US) */
DECL|field|MATH_LOCALE
specifier|protected
specifier|static
specifier|final
name|Locale
name|MATH_LOCALE
init|=
name|Locale
operator|.
name|US
decl_stmt|;
comment|/**     * Fixed TimeZone (UTC) needed for parsing/formating Dates in the     * canonical representation.    */
DECL|field|CANONICAL_TZ
specifier|protected
specifier|static
specifier|final
name|TimeZone
name|CANONICAL_TZ
init|=
name|UTC
decl_stmt|;
comment|/**     * Fixed Locale needed for parsing/formating Milliseconds in the     * canonical representation.    */
DECL|field|CANONICAL_LOCALE
specifier|protected
specifier|static
specifier|final
name|Locale
name|CANONICAL_LOCALE
init|=
name|Locale
operator|.
name|US
decl_stmt|;
comment|// The XML (external) date format will sort correctly, except if
comment|// fractions of seconds are present (because '.' is lower than 'Z').
comment|// The easiest fix is to simply remove the 'Z' for the internal
comment|// format.
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{   }
DECL|field|NOW
specifier|protected
specifier|static
name|String
name|NOW
init|=
literal|"NOW"
decl_stmt|;
DECL|field|Z
specifier|protected
specifier|static
name|char
name|Z
init|=
literal|'Z'
decl_stmt|;
DECL|field|Z_ARRAY
specifier|private
specifier|static
name|char
index|[]
name|Z_ARRAY
init|=
operator|new
name|char
index|[]
block|{
name|Z
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|toInternal
argument_list|(
name|parseMath
argument_list|(
literal|null
argument_list|,
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parses a String which may be a date (in the standard format)    * followed by an optional math expression.    * @param now an optional fixed date to use as "NOW" in the DateMathParser    * @param val the string to parse    */
DECL|method|parseMath
specifier|public
name|Date
name|parseMath
parameter_list|(
name|Date
name|now
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|String
name|math
init|=
literal|null
decl_stmt|;
specifier|final
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|MATH_TZ
argument_list|,
name|MATH_LOCALE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|now
condition|)
name|p
operator|.
name|setNow
argument_list|(
name|now
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|.
name|startsWith
argument_list|(
name|NOW
argument_list|)
condition|)
block|{
name|math
operator|=
name|val
operator|.
name|substring
argument_list|(
name|NOW
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|zz
init|=
name|val
operator|.
name|indexOf
argument_list|(
name|Z
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|zz
condition|)
block|{
name|math
operator|=
name|val
operator|.
name|substring
argument_list|(
name|zz
operator|+
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
comment|// p.setNow(toObject(val.substring(0,zz)));
name|p
operator|.
name|setNow
argument_list|(
name|parseDate
argument_list|(
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|zz
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid Date in Date Math String:'"
operator|+
name|val
operator|+
literal|'\''
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid Date String:'"
operator|+
name|val
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|math
operator|||
name|math
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return
name|p
operator|.
name|getNow
argument_list|()
return|;
block|}
try|try
block|{
return|return
name|p
operator|.
name|parseMath
argument_list|(
name|math
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid Date Math String:'"
operator|+
name|val
operator|+
literal|'\''
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createField
specifier|public
name|IndexableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
comment|// Convert to a string before indexing
if|if
condition|(
name|value
operator|instanceof
name|Date
condition|)
block|{
name|value
operator|=
name|toInternal
argument_list|(
operator|(
name|Date
operator|)
name|value
argument_list|)
operator|+
name|Z
expr_stmt|;
block|}
return|return
name|super
operator|.
name|createField
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|boost
argument_list|)
return|;
block|}
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|Date
name|val
parameter_list|)
block|{
return|return
name|formatDate
argument_list|(
name|val
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|indexedForm
parameter_list|)
block|{
return|return
name|indexedForm
operator|+
name|Z
return|;
block|}
annotation|@
name|Override
DECL|method|indexedToReadable
specifier|public
name|CharsRef
name|indexedToReadable
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|CharsRef
name|charsRef
parameter_list|)
block|{
name|input
operator|.
name|utf8ToChars
argument_list|(
name|charsRef
argument_list|)
expr_stmt|;
name|charsRef
operator|.
name|append
argument_list|(
name|Z_ARRAY
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|charsRef
return|;
block|}
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
return|return
name|indexedToReadable
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toObject
specifier|public
name|Date
name|toObject
parameter_list|(
name|String
name|indexedForm
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
return|return
name|parseDate
argument_list|(
name|indexedToReadable
argument_list|(
name|indexedForm
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Date
name|toObject
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
try|try
block|{
return|return
name|parseDate
argument_list|(
name|toExternal
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|IndexableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeDate
argument_list|(
name|name
argument_list|,
name|toExternal
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a formatter that can be use by the current thread if needed to    * convert Date objects to the Internal representation.    *    * Only the<tt>format(Date)</tt> can be used safely.    *     * @deprecated - use formatDate(Date) instead    */
annotation|@
name|Deprecated
DECL|method|getThreadLocalDateFormat
specifier|protected
name|DateFormat
name|getThreadLocalDateFormat
parameter_list|()
block|{
return|return
name|fmtThreadLocal
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Thread safe method that can be used by subclasses to format a Date    * using the Internal representation.    */
DECL|method|formatDate
specifier|protected
name|String
name|formatDate
parameter_list|(
name|Date
name|d
parameter_list|)
block|{
return|return
name|fmtThreadLocal
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|d
argument_list|)
return|;
block|}
comment|/**    * Return the standard human readable form of the date    */
DECL|method|formatExternal
specifier|public
specifier|static
name|String
name|formatExternal
parameter_list|(
name|Date
name|d
parameter_list|)
block|{
return|return
name|fmtThreadLocal
operator|.
name|get
argument_list|()
operator|.
name|format
argument_list|(
name|d
argument_list|)
operator|+
literal|'Z'
return|;
block|}
comment|/**    * @see {#formatExternal}    */
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|Date
name|d
parameter_list|)
block|{
return|return
name|formatExternal
argument_list|(
name|d
argument_list|)
return|;
block|}
comment|/**    * Thread safe method that can be used by subclasses to parse a Date    * that is already in the internal representation    */
DECL|method|parseDate
specifier|public
specifier|static
name|Date
name|parseDate
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|fmtThreadLocal
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/** Parse a date string in the standard format, or any supported by DateUtil.parseDate */
DECL|method|parseDateLenient
specifier|public
name|Date
name|parseDateLenient
parameter_list|(
name|String
name|s
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// request could define timezone in the future
try|try
block|{
return|return
name|fmtThreadLocal
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|s
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|DateUtil
operator|.
name|parseDate
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
comment|/**    * Parses a String which may be a date    * followed by an optional math expression.    * @param now an optional fixed date to use as "NOW" in the DateMathParser    * @param val the string to parse    */
DECL|method|parseMathLenient
specifier|public
name|Date
name|parseMathLenient
parameter_list|(
name|Date
name|now
parameter_list|,
name|String
name|val
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
name|math
init|=
literal|null
decl_stmt|;
specifier|final
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|MATH_TZ
argument_list|,
name|MATH_LOCALE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|now
condition|)
name|p
operator|.
name|setNow
argument_list|(
name|now
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|.
name|startsWith
argument_list|(
name|NOW
argument_list|)
condition|)
block|{
name|math
operator|=
name|val
operator|.
name|substring
argument_list|(
name|NOW
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|zz
init|=
name|val
operator|.
name|indexOf
argument_list|(
name|Z
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|zz
condition|)
block|{
name|math
operator|=
name|val
operator|.
name|substring
argument_list|(
name|zz
operator|+
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
comment|// p.setNow(toObject(val.substring(0,zz)));
name|p
operator|.
name|setNow
argument_list|(
name|parseDateLenient
argument_list|(
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|zz
operator|+
literal|1
argument_list|)
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid Date in Date Math String:'"
operator|+
name|val
operator|+
literal|'\''
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid Date String:'"
operator|+
name|val
operator|+
literal|'\''
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|math
operator|||
name|math
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return
name|p
operator|.
name|getNow
argument_list|()
return|;
block|}
try|try
block|{
return|return
name|p
operator|.
name|parseMath
argument_list|(
name|math
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid Date Math String:'"
operator|+
name|val
operator|+
literal|'\''
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Thread safe DateFormat that can<b>format</b> in the canonical    * ISO8601 date format, not including the trailing "Z" (since it is    * left off in the internal indexed values)    */
DECL|field|fmtThreadLocal
specifier|private
specifier|final
specifier|static
name|ThreadLocalDateFormat
name|fmtThreadLocal
init|=
operator|new
name|ThreadLocalDateFormat
argument_list|(
operator|new
name|ISO8601CanonicalDateFormat
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|ISO8601CanonicalDateFormat
specifier|private
specifier|static
class|class
name|ISO8601CanonicalDateFormat
extends|extends
name|SimpleDateFormat
block|{
DECL|field|millisParser
specifier|protected
name|NumberFormat
name|millisParser
init|=
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|(
name|CANONICAL_LOCALE
argument_list|)
decl_stmt|;
DECL|field|millisFormat
specifier|protected
name|NumberFormat
name|millisFormat
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|".###"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|CANONICAL_LOCALE
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|ISO8601CanonicalDateFormat
specifier|public
name|ISO8601CanonicalDateFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss"
argument_list|,
name|CANONICAL_LOCALE
argument_list|)
expr_stmt|;
name|this
operator|.
name|setTimeZone
argument_list|(
name|CANONICAL_TZ
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Date
name|parse
parameter_list|(
name|String
name|i
parameter_list|,
name|ParsePosition
name|p
parameter_list|)
block|{
comment|/* delegate to SimpleDateFormat for easy stuff */
name|Date
name|d
init|=
name|super
operator|.
name|parse
argument_list|(
name|i
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|int
name|milliIndex
init|=
name|p
operator|.
name|getIndex
argument_list|()
decl_stmt|;
comment|/* worry aboutthe milliseconds ourselves */
if|if
condition|(
literal|null
operator|!=
name|d
operator|&&
operator|-
literal|1
operator|==
name|p
operator|.
name|getErrorIndex
argument_list|()
operator|&&
name|milliIndex
operator|+
literal|1
operator|<
name|i
operator|.
name|length
argument_list|()
operator|&&
literal|'.'
operator|==
name|i
operator|.
name|charAt
argument_list|(
name|milliIndex
argument_list|)
condition|)
block|{
name|p
operator|.
name|setIndex
argument_list|(
operator|++
name|milliIndex
argument_list|)
expr_stmt|;
comment|// NOTE: ++ to chomp '.'
name|Number
name|millis
init|=
name|millisParser
operator|.
name|parse
argument_list|(
name|i
argument_list|,
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|p
operator|.
name|getErrorIndex
argument_list|()
condition|)
block|{
name|int
name|endIndex
init|=
name|p
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|d
operator|=
operator|new
name|Date
argument_list|(
name|d
operator|.
name|getTime
argument_list|()
operator|+
call|(
name|long
call|)
argument_list|(
name|millis
operator|.
name|doubleValue
argument_list|()
operator|*
name|Math
operator|.
name|pow
argument_list|(
literal|10
argument_list|,
operator|(
literal|3
operator|-
name|endIndex
operator|+
name|milliIndex
operator|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|d
return|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|StringBuffer
name|format
parameter_list|(
name|Date
name|d
parameter_list|,
name|StringBuffer
name|toAppendTo
parameter_list|,
name|FieldPosition
name|pos
parameter_list|)
block|{
comment|/* delegate to SimpleDateFormat for easy stuff */
name|super
operator|.
name|format
argument_list|(
name|d
argument_list|,
name|toAppendTo
argument_list|,
name|pos
argument_list|)
expr_stmt|;
comment|/* worry aboutthe milliseconds ourselves */
name|long
name|millis
init|=
name|d
operator|.
name|getTime
argument_list|()
operator|%
literal|1000l
decl_stmt|;
if|if
condition|(
literal|0L
operator|==
name|millis
condition|)
block|{
return|return
name|toAppendTo
return|;
block|}
if|if
condition|(
name|millis
operator|<
literal|0L
condition|)
block|{
comment|// original date was prior to epoch
name|millis
operator|+=
literal|1000L
expr_stmt|;
block|}
name|int
name|posBegin
init|=
name|toAppendTo
operator|.
name|length
argument_list|()
decl_stmt|;
name|toAppendTo
operator|.
name|append
argument_list|(
name|millisFormat
operator|.
name|format
argument_list|(
name|millis
operator|/
literal|1000d
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|DateFormat
operator|.
name|MILLISECOND_FIELD
operator|==
name|pos
operator|.
name|getField
argument_list|()
condition|)
block|{
name|pos
operator|.
name|setBeginIndex
argument_list|(
name|posBegin
argument_list|)
expr_stmt|;
name|pos
operator|.
name|setEndIndex
argument_list|(
name|toAppendTo
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|toAppendTo
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|ISO8601CanonicalDateFormat
name|c
init|=
operator|(
name|ISO8601CanonicalDateFormat
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|c
operator|.
name|millisParser
operator|=
name|NumberFormat
operator|.
name|getIntegerInstance
argument_list|(
name|CANONICAL_LOCALE
argument_list|)
expr_stmt|;
name|c
operator|.
name|millisFormat
operator|=
operator|new
name|DecimalFormat
argument_list|(
literal|".###"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|CANONICAL_LOCALE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
DECL|class|ThreadLocalDateFormat
specifier|private
specifier|static
class|class
name|ThreadLocalDateFormat
extends|extends
name|ThreadLocal
argument_list|<
name|DateFormat
argument_list|>
block|{
DECL|field|proto
name|DateFormat
name|proto
decl_stmt|;
DECL|method|ThreadLocalDateFormat
specifier|public
name|ThreadLocalDateFormat
parameter_list|(
name|DateFormat
name|d
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|proto
operator|=
name|d
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialValue
specifier|protected
name|DateFormat
name|initialValue
parameter_list|()
block|{
return|return
operator|(
name|DateFormat
operator|)
name|proto
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
name|field
operator|.
name|checkFieldCacheSource
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
operator|new
name|DateFieldSource
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
comment|/** DateField specific range query */
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|Date
name|part1
parameter_list|,
name|Date
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
return|return
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|,
name|part1
operator|==
literal|null
condition|?
literal|null
else|:
name|toInternal
argument_list|(
name|part1
argument_list|)
argument_list|,
name|part2
operator|==
literal|null
condition|?
literal|null
else|:
name|toInternal
argument_list|(
name|part2
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
block|}
end_class

begin_class
DECL|class|DateFieldSource
class|class
name|DateFieldSource
extends|extends
name|FieldCacheSource
block|{
comment|// NOTE: this is bad for serialization... but we currently need the fieldType for toInternal()
DECL|field|ft
name|FieldType
name|ft
decl_stmt|;
DECL|method|DateFieldSource
specifier|public
name|DateFieldSource
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|ft
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|ft
operator|=
name|ft
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"date("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StringIndexDocValues
argument_list|(
name|this
argument_list|,
name|readerContext
argument_list|,
name|field
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toTerm
parameter_list|(
name|String
name|readableValue
parameter_list|)
block|{
comment|// needed for frange queries to work properly
return|return
name|ft
operator|.
name|toInternal
argument_list|(
name|readableValue
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|ord
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|BytesRef
name|br
init|=
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
name|spare
argument_list|)
decl_stmt|;
return|return
name|ft
operator|.
name|indexedToReadable
argument_list|(
name|br
argument_list|,
name|spareChars
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|termsIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|BytesRef
name|br
init|=
name|termsIndex
operator|.
name|lookup
argument_list|(
name|ord
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ft
operator|.
name|toObject
argument_list|(
literal|null
argument_list|,
name|br
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
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
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|DateFieldSource
operator|&&
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
DECL|field|hcode
specifier|private
specifier|static
name|int
name|hcode
init|=
name|DateFieldSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hcode
operator|+
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
block|}
end_class

end_unit

