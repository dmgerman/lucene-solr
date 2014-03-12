begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|LocaleUtils
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
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|SolrQueryResponse
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
name|schema
operator|.
name|DateValueFieldType
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Collection
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
name|LinkedHashMap
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>  * Attempts to mutate selected fields that have only CharSequence-typed values  * into Date values.  Solr will continue to index date/times in the UTC time  * zone, but the input date/times may be expressed using other time zones,  * and will be converted to UTC when they are mutated.  *</p>  *<p>  * The default selection behavior is to mutate both those fields that don't match  * a schema field, as well as those fields that match a schema field with a field   * type that uses class solr.DateField or a sub-class, including solr.TrieDateField.  *</p>  *<p>  * If all values are parseable as dates (or are already Date), then the field will  * be mutated, replacing each value with its parsed Date equivalent; otherwise, no  * mutation will occur.  *</p>  *<p>  * One or more date "format" specifiers must be specified.  See   *<a href="http://joda-time.sourceforge.net/apidocs/org/joda/time/format/DateTimeFormat.html"  *>Joda-time's DateTimeFormat javadocs</a> for a description of format strings.  *</p>  *<p>  * A default time zone name or offset may optionally be specified for those dates  * that don't include an explicit zone/offset.  NOTE: three-letter zone  * designations like "EST" are not parseable (with the single exception of "UTC"),  * because they are ambiguous.  If no default time zone is specified, UTC will be  * used. See<a href="http://en.wikipedia.org/wiki/List_of_tz_database_time_zones"  *>Wikipedia's list of TZ database time zone names</a>.  *</p>  *<p>  * The locale to use when parsing field values using the specified formats may  * optionally be specified.  If no locale is configured, then {@link Locale#ROOT}  * will be used. The following configuration specifies the French/France locale and  * two date formats that will parse the strings "le mardi 8 janvier 2013" and   * "le 28 dÃ©c. 2010 Ã  15 h 30", respectively.  Note that either individual&lt;str&gt;  * elements or&lt;arr&gt;-s of&lt;str&gt; elements may be used to specify the  * date format(s):  *</p>  *  *<pre class="prettyprint">  *&lt;processor class="solr.ParseDateFieldUpdateProcessorFactory"&gt;  *&lt;str name="defaultTimeZone"&gt;Europe/Paris&lt;/str&gt;  *&lt;str name="locale"&gt;fr_FR&lt;/str&gt;  *&lt;arr name="format"&gt;  *&lt;str&gt;'le' EEEE dd MMMM yyyy&lt;/str&gt;  *&lt;str&gt;'le' dd MMM. yyyy 'Ã ' HH 'h' mm&lt;/str&gt;  *&lt;/arr&gt;  *&lt;/processor&gt;</pre>  *  *<p>  * See {@link Locale} for a description of acceptable language, country (optional)  * and variant (optional) values, joined with underscore(s).  *</p>  */
end_comment

begin_class
DECL|class|ParseDateFieldUpdateProcessorFactory
specifier|public
class|class
name|ParseDateFieldUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ParseDateFieldUpdateProcessorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FORMATS_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|FORMATS_PARAM
init|=
literal|"format"
decl_stmt|;
DECL|field|DEFAULT_TIME_ZONE_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_TIME_ZONE_PARAM
init|=
literal|"defaultTimeZone"
decl_stmt|;
DECL|field|LOCALE_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|LOCALE_PARAM
init|=
literal|"locale"
decl_stmt|;
DECL|field|formats
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DateTimeFormatter
argument_list|>
name|formats
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|AllValuesOrNoneFieldMutatingUpdateProcessor
argument_list|(
name|getSelector
argument_list|()
argument_list|,
name|next
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Object
name|mutateValue
parameter_list|(
name|Object
name|srcVal
parameter_list|)
block|{
if|if
condition|(
name|srcVal
operator|instanceof
name|CharSequence
condition|)
block|{
name|String
name|srcStringVal
init|=
name|srcVal
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DateTimeFormatter
argument_list|>
name|format
range|:
name|formats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DateTimeFormatter
name|parser
init|=
name|format
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
name|DateTime
name|dateTime
init|=
name|parser
operator|.
name|parseDateTime
argument_list|(
name|srcStringVal
argument_list|)
decl_stmt|;
return|return
name|dateTime
operator|.
name|withZone
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
operator|.
name|toDate
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"value '{}' is not parseable with format '{}'"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|srcStringVal
block|,
name|format
operator|.
name|getKey
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"value '{}' was not parsed by any configured format, thus was not mutated"
argument_list|,
name|srcStringVal
argument_list|)
expr_stmt|;
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
if|if
condition|(
name|srcVal
operator|instanceof
name|Date
condition|)
block|{
return|return
name|srcVal
return|;
block|}
return|return
name|SKIP_FIELD_VALUE_LIST_SINGLETON
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Locale
name|locale
init|=
name|Locale
operator|.
name|ROOT
decl_stmt|;
name|String
name|localeParam
init|=
operator|(
name|String
operator|)
name|args
operator|.
name|remove
argument_list|(
name|LOCALE_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|localeParam
condition|)
block|{
name|locale
operator|=
name|LocaleUtils
operator|.
name|toLocale
argument_list|(
name|localeParam
argument_list|)
expr_stmt|;
block|}
name|Object
name|defaultTimeZoneParam
init|=
name|args
operator|.
name|remove
argument_list|(
name|DEFAULT_TIME_ZONE_PARAM
argument_list|)
decl_stmt|;
name|DateTimeZone
name|defaultTimeZone
init|=
name|DateTimeZone
operator|.
name|UTC
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|defaultTimeZoneParam
condition|)
block|{
name|defaultTimeZone
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|defaultTimeZoneParam
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|formatsParam
init|=
name|args
operator|.
name|removeConfigArgs
argument_list|(
name|FORMATS_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|formatsParam
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|formatsParam
control|)
block|{
name|formats
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
name|value
argument_list|)
operator|.
name|withZone
argument_list|(
name|defaultTimeZone
argument_list|)
operator|.
name|withLocale
argument_list|(
name|locale
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true if the field doesn't match any schema field or dynamic field,    *           or if the matched field's type is BoolField    */
annotation|@
name|Override
specifier|public
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
DECL|method|getDefaultSelector
name|getDefaultSelector
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
return|return
operator|new
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldMutate
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|FieldType
name|type
init|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
operator|(
literal|null
operator|==
name|type
operator|)
operator|||
name|type
operator|instanceof
name|DateValueFieldType
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

