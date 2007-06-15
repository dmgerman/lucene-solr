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
name|request
operator|.
name|XMLWriter
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
name|TextResponseWriter
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
name|Fieldable
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
name|solr
operator|.
name|search
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
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|OrdFieldSource
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
name|util
operator|.
name|Map
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
comment|/**  * FieldType that can represent any Date/Time with millisecond precisison.  *<p>  * Date Format for the XML, incoming and outgoing:  *</p>  *<blockquote>  * A date field shall be of the form 1995-12-31T23:59:59Z  * The trailing "Z" designates UTC time and is mandatory.  * Optional fractional seconds are allowed: 1995-12-31T23:59:59.999Z  * All other parts are mandatory.  *</blockquote>  *<p>  * This format was derived to be standards compliant (ISO 8601) and is a more  * restricted form of the canonical representation of dateTime from XML  * schema part 2.  * http://www.w3.org/TR/xmlschema-2/#dateTime  *</p>  *<blockquote>  * "In 1970 the Coordinated Universal Time system was devised by an  * international advisory group of technical experts within the International  * Telecommunication Union (ITU).  The ITU felt it was best to designate a  * single abbreviation for use in all languages in order to minimize  * confusion.  Since unanimous agreement could not be achieved on using  * either the English word order, CUT, or the French word order, TUC, the  * acronym UTC was chosen as a compromise."  *</blockquote>  *  *<p>  * This FieldType also supports incoming "Date Math" strings for computing  * values by adding/rounding internals of time relative "NOW",  * ie: "NOW+1YEAR", "NOW/DAY", etc.. -- see {@link DateMathParser}  * for more examples.  *</p>  *  * @author yonik  * @version $Id$  * @see<a href="http://www.w3.org/TR/xmlschema-2/#dateTime">XML schema part 2</a>  *  */
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
comment|// The XML (external) date format will sort correctly, except if
comment|// fractions of seconds are present (because '.' is lower than 'Z').
comment|// The easiest fix is to simply remove the 'Z' for the internal
comment|// format.
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
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|val
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|charAt
argument_list|(
name|len
operator|-
literal|1
argument_list|)
operator|==
literal|'Z'
condition|)
block|{
return|return
name|val
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
operator|-
literal|1
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|val
operator|.
name|startsWith
argument_list|(
literal|"NOW"
argument_list|)
condition|)
block|{
comment|/* :TODO: let Locale/TimeZone come from init args for rounding only */
name|DateMathParser
name|p
init|=
operator|new
name|DateMathParser
argument_list|(
name|UTC
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|toInternal
argument_list|(
name|p
operator|.
name|parseMath
argument_list|(
name|val
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
argument_list|)
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
name|getThreadLocalDateFormat
argument_list|()
operator|.
name|format
argument_list|(
name|val
argument_list|)
return|;
block|}
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
literal|'Z'
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|Fieldable
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
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Date
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
try|try
block|{
return|return
name|getThreadLocalDateFormat
argument_list|()
operator|.
name|parse
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
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
return|return
operator|new
name|OrdFieldSource
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
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
name|Fieldable
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
comment|/**    * Returns a formatter that can be use by the current thread if needed to    * convert Date objects to the Internal representation.    */
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
DECL|field|fmtThreadLocal
specifier|private
specifier|static
name|ThreadLocalDateFormat
name|fmtThreadLocal
init|=
operator|new
name|ThreadLocalDateFormat
argument_list|()
decl_stmt|;
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
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|SimpleDateFormat
name|tmp
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss.SSS"
argument_list|,
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|setTimeZone
argument_list|(
name|UTC
argument_list|)
expr_stmt|;
name|proto
operator|=
name|tmp
expr_stmt|;
block|}
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
block|}
end_class

end_unit

