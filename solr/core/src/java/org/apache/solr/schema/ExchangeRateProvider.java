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

begin_comment
comment|/**  * Interface for providing pluggable exchange rate providers to @CurrencyField  */
end_comment

begin_interface
DECL|interface|ExchangeRateProvider
specifier|public
interface|interface
name|ExchangeRateProvider
block|{
comment|/**    * Get the exchange rate betwen the two given currencies    * @return the exchange rate as a double    * @throws SolrException if the rate is not defined in the provider    */
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
throws|throws
name|SolrException
function_decl|;
comment|/**    * List all configured currency codes which are valid as source/target for this Provider    * @return a Set of<a href="http://en.wikipedia.org/wiki/ISO_4217">ISO 4217</a> currency code strings    */
DECL|method|listAvailableCurrencies
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|listAvailableCurrencies
parameter_list|()
function_decl|;
comment|/**    * Ask the currency provider to explicitly reload/refresh its configuration.    * If this does not make sense for a particular provider, simply do nothing    * @throws SolrException if there is a problem reloading    * @return true if reload of rates succeeded, else false    */
DECL|method|reload
specifier|public
name|boolean
name|reload
parameter_list|()
throws|throws
name|SolrException
function_decl|;
comment|/**    * Initializes the provider by passing in a set of key/value configs as a map.    * Note that the map also contains other fieldType parameters, so make sure to    * avoid name clashes.    *<p>    * Important: Custom config params must be removed from the map before returning    * @param args a @Map of key/value config params to initialize the provider    */
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
name|args
parameter_list|)
function_decl|;
comment|/**    * Passes a ResourceLoader, used to read config files from e.g. ZooKeeper.    * Implementations not needing resource loader can implement this as NOOP.    *<p>Typically called after init    * @param loader a @ResourceLoader instance    */
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
function_decl|;
block|}
end_interface

end_unit

