begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|util
operator|.
name|Set
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
name|SuppressForbidden
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|SolrException
operator|.
name|ErrorCode
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

begin_comment
comment|/**  *<p>  * Exchange Rates Provider for {@link CurrencyField} capable of fetching&amp;   * parsing the freely available exchange rates from openexchangerates.org  *</p>  *<p>  * Configuration Options:  *</p>  *<ul>  *<li><code>ratesFileLocation</code> - A file path or absolute URL specifying the JSON data to load (mandatory)</li>  *<li><code>refreshInterval</code> - How frequently (in minutes) to reload the exchange rate data (default: 1440)</li>  *</ul>  *<p>  *<b>Disclaimer:</b> This data is collected from various providers and provided free of charge  * for informational purposes only, with no guarantee whatsoever of accuracy, validity,  * availability or fitness for any purpose; use at your own risk. Other than that - have  * fun, and please share/watch/fork if you think data like this should be free!  *</p>  * @see<a href="https://openexchangerates.org/documentation">openexchangerates.org JSON Data Format</a>  */
end_comment

begin_class
DECL|class|OpenExchangeRatesOrgProvider
specifier|public
class|class
name|OpenExchangeRatesOrgProvider
implements|implements
name|ExchangeRateProvider
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|PARAM_RATES_FILE_LOCATION
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_RATES_FILE_LOCATION
init|=
literal|"ratesFileLocation"
decl_stmt|;
DECL|field|PARAM_REFRESH_INTERVAL
specifier|protected
specifier|static
specifier|final
name|String
name|PARAM_REFRESH_INTERVAL
init|=
literal|"refreshInterval"
decl_stmt|;
DECL|field|DEFAULT_REFRESH_INTERVAL
specifier|protected
specifier|static
specifier|final
name|String
name|DEFAULT_REFRESH_INTERVAL
init|=
literal|"1440"
decl_stmt|;
DECL|field|ratesFileLocation
specifier|protected
name|String
name|ratesFileLocation
decl_stmt|;
comment|// configured in minutes, but stored in seconds for quicker math
DECL|field|refreshIntervalSeconds
specifier|protected
name|int
name|refreshIntervalSeconds
decl_stmt|;
DECL|field|resourceLoader
specifier|protected
name|ResourceLoader
name|resourceLoader
decl_stmt|;
DECL|field|rates
specifier|protected
name|OpenExchangeRates
name|rates
decl_stmt|;
comment|/**    * Returns the currently known exchange rate between two currencies. The rates are fetched from    * the freely available OpenExchangeRates.org JSON, hourly updated. All rates are symmetrical with    * base currency being USD by default.    *    * @param sourceCurrencyCode The source currency being converted from.    * @param targetCurrencyCode The target currency being converted to.    * @return The exchange rate.    * @throws SolrException if the requested currency pair cannot be found    */
annotation|@
name|Override
DECL|method|getExchangeRate
specifier|public
name|double
name|getExchangeRate
parameter_list|(
name|String
name|sourceCurrencyCode
parameter_list|,
name|String
name|targetCurrencyCode
parameter_list|)
block|{
if|if
condition|(
name|rates
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"Rates not initialized."
argument_list|)
throw|;
block|}
if|if
condition|(
name|sourceCurrencyCode
operator|==
literal|null
operator|||
name|targetCurrencyCode
operator|==
literal|null
condition|)
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
literal|"Cannot get exchange rate; currency was null."
argument_list|)
throw|;
block|}
name|reloadIfExpired
argument_list|()
expr_stmt|;
name|Double
name|source
init|=
operator|(
name|Double
operator|)
name|rates
operator|.
name|getRates
argument_list|()
operator|.
name|get
argument_list|(
name|sourceCurrencyCode
argument_list|)
decl_stmt|;
name|Double
name|target
init|=
operator|(
name|Double
operator|)
name|rates
operator|.
name|getRates
argument_list|()
operator|.
name|get
argument_list|(
name|targetCurrencyCode
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
operator|||
name|target
operator|==
literal|null
condition|)
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
literal|"No available conversion rate from "
operator|+
name|sourceCurrencyCode
operator|+
literal|" to "
operator|+
name|targetCurrencyCode
operator|+
literal|". "
operator|+
literal|"Available rates are "
operator|+
name|listAvailableCurrencies
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|target
operator|/
name|source
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis, for comparison with stamp in an external file"
argument_list|)
DECL|method|reloadIfExpired
specifier|private
name|void
name|reloadIfExpired
parameter_list|()
block|{
if|if
condition|(
operator|(
name|rates
operator|.
name|getTimestamp
argument_list|()
operator|+
name|refreshIntervalSeconds
operator|)
operator|*
literal|1000
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Refresh interval has expired. Refreshing exchange rates."
argument_list|)
expr_stmt|;
name|reload
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|OpenExchangeRatesOrgProvider
name|that
init|=
operator|(
name|OpenExchangeRatesOrgProvider
operator|)
name|o
decl_stmt|;
return|return
operator|!
operator|(
name|rates
operator|!=
literal|null
condition|?
operator|!
name|rates
operator|.
name|equals
argument_list|(
name|that
operator|.
name|rates
argument_list|)
else|:
name|that
operator|.
name|rates
operator|!=
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|rates
operator|!=
literal|null
condition|?
name|rates
operator|.
name|hashCode
argument_list|()
else|:
literal|0
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
return|return
literal|"["
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|rates
operator|.
name|getRates
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" rates.]"
return|;
block|}
annotation|@
name|Override
DECL|method|listAvailableCurrencies
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|listAvailableCurrencies
parameter_list|()
block|{
if|if
condition|(
name|rates
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Rates not initialized"
argument_list|)
throw|;
return|return
name|rates
operator|.
name|getRates
argument_list|()
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|boolean
name|reload
parameter_list|()
throws|throws
name|SolrException
block|{
name|InputStream
name|ratesJsonStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Reloading exchange rates from "
operator|+
name|ratesFileLocation
argument_list|)
expr_stmt|;
try|try
block|{
name|ratesJsonStream
operator|=
operator|(
operator|new
name|URL
argument_list|(
name|ratesFileLocation
argument_list|)
operator|)
operator|.
name|openStream
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ratesJsonStream
operator|=
name|resourceLoader
operator|.
name|openResource
argument_list|(
name|ratesFileLocation
argument_list|)
expr_stmt|;
block|}
name|rates
operator|=
operator|new
name|OpenExchangeRates
argument_list|(
name|ratesJsonStream
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error reloading exchange rates"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|ratesJsonStream
operator|!=
literal|null
condition|)
try|try
block|{
name|ratesJsonStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error closing stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
throws|throws
name|SolrException
block|{
try|try
block|{
name|ratesFileLocation
operator|=
name|params
operator|.
name|get
argument_list|(
name|PARAM_RATES_FILE_LOCATION
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|ratesFileLocation
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Init param must be specified: "
operator|+
name|PARAM_RATES_FILE_LOCATION
argument_list|)
throw|;
block|}
name|int
name|refreshInterval
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getParam
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|PARAM_REFRESH_INTERVAL
argument_list|)
argument_list|,
name|DEFAULT_REFRESH_INTERVAL
argument_list|)
argument_list|)
decl_stmt|;
comment|// Force a refresh interval of minimum one hour, since the API does not offer better resolution
if|if
condition|(
name|refreshInterval
operator|<
literal|60
condition|)
block|{
name|refreshInterval
operator|=
literal|60
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Specified refreshInterval was too small. Setting to 60 minutes which is the update rate of openexchangerates.org"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Initialized with rates="
operator|+
name|ratesFileLocation
operator|+
literal|", refreshInterval="
operator|+
name|refreshInterval
operator|+
literal|"."
argument_list|)
expr_stmt|;
name|refreshIntervalSeconds
operator|=
name|refreshInterval
operator|*
literal|60
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e1
parameter_list|)
block|{
throw|throw
name|e1
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error initializing: "
operator|+
name|e2
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e2
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// Removing config params custom to us
name|params
operator|.
name|remove
argument_list|(
name|PARAM_RATES_FILE_LOCATION
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|PARAM_REFRESH_INTERVAL
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|SolrException
block|{
name|resourceLoader
operator|=
name|loader
expr_stmt|;
name|reload
argument_list|()
expr_stmt|;
block|}
DECL|method|getParam
specifier|private
name|String
name|getParam
parameter_list|(
name|String
name|param
parameter_list|,
name|String
name|defaultParam
parameter_list|)
block|{
return|return
name|param
operator|==
literal|null
condition|?
name|defaultParam
else|:
name|param
return|;
block|}
comment|/**    * A simple class encapsulating the JSON data from openexchangerates.org    */
DECL|class|OpenExchangeRates
specifier|static
class|class
name|OpenExchangeRates
block|{
DECL|field|rates
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|rates
decl_stmt|;
DECL|field|baseCurrency
specifier|private
name|String
name|baseCurrency
decl_stmt|;
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
DECL|field|disclaimer
specifier|private
name|String
name|disclaimer
decl_stmt|;
DECL|field|license
specifier|private
name|String
name|license
decl_stmt|;
DECL|field|parser
specifier|private
name|JSONParser
name|parser
decl_stmt|;
DECL|method|OpenExchangeRates
specifier|public
name|OpenExchangeRates
parameter_list|(
name|InputStream
name|ratesStream
parameter_list|)
throws|throws
name|IOException
block|{
name|parser
operator|=
operator|new
name|JSONParser
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|ratesStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|rates
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|int
name|ev
decl_stmt|;
do|do
block|{
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|ev
condition|)
block|{
case|case
name|JSONParser
operator|.
name|STRING
case|:
if|if
condition|(
name|parser
operator|.
name|wasKey
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|parser
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"disclaimer"
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|disclaimer
operator|=
name|parser
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"license"
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|license
operator|=
name|parser
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"timestamp"
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|timestamp
operator|=
name|parser
operator|.
name|getLong
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"base"
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|baseCurrency
operator|=
name|parser
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"rates"
argument_list|)
condition|)
block|{
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_START
operator|)
assert|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
while|while
condition|(
name|ev
operator|!=
name|JSONParser
operator|.
name|OBJECT_END
condition|)
block|{
name|String
name|curr
init|=
name|parser
operator|.
name|getString
argument_list|()
decl_stmt|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|Double
name|rate
init|=
name|parser
operator|.
name|getDouble
argument_list|()
decl_stmt|;
name|rates
operator|.
name|put
argument_list|(
name|curr
argument_list|,
name|rate
argument_list|)
expr_stmt|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown key "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Expected key, got "
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|JSONParser
operator|.
name|OBJECT_END
case|:
case|case
name|JSONParser
operator|.
name|OBJECT_START
case|:
case|case
name|JSONParser
operator|.
name|EOF
case|:
break|break;
default|default:
name|log
operator|.
name|info
argument_list|(
literal|"Noggit UNKNOWN_EVENT_ID:"
operator|+
name|JSONParser
operator|.
name|getEventString
argument_list|(
name|ev
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
do|while
condition|(
name|ev
operator|!=
name|JSONParser
operator|.
name|EOF
condition|)
do|;
block|}
DECL|method|getRates
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|getRates
parameter_list|()
block|{
return|return
name|rates
return|;
block|}
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
comment|/** Package protected method for test purposes      * @lucene.internal      */
DECL|method|setTimestamp
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
DECL|method|getDisclaimer
specifier|public
name|String
name|getDisclaimer
parameter_list|()
block|{
return|return
name|disclaimer
return|;
block|}
DECL|method|getBaseCurrency
specifier|public
name|String
name|getBaseCurrency
parameter_list|()
block|{
return|return
name|baseCurrency
return|;
block|}
DECL|method|getLicense
specifier|public
name|String
name|getLicense
parameter_list|()
block|{
return|return
name|license
return|;
block|}
block|}
block|}
end_class

end_unit

