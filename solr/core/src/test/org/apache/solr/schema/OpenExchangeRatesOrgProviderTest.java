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
name|SolrTestCaseJ4
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
name|SuppressForbidden
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
name|SolrResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests currency field type.  */
end_comment

begin_class
DECL|class|OpenExchangeRatesOrgProviderTest
specifier|public
class|class
name|OpenExchangeRatesOrgProviderTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|HARDCODED_TEST_TIMESTAMP
specifier|private
specifier|final
specifier|static
name|long
name|HARDCODED_TEST_TIMESTAMP
init|=
literal|1332070464L
decl_stmt|;
DECL|field|oerp
name|OpenExchangeRatesOrgProvider
name|oerp
decl_stmt|;
DECL|field|loader
name|ResourceLoader
name|loader
decl_stmt|;
DECL|field|mockParams
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mockParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractCurrencyFieldTest
operator|.
name|assumeCurrencySupport
argument_list|(
literal|"USD"
argument_list|,
literal|"EUR"
argument_list|,
literal|"MXN"
argument_list|,
literal|"GBP"
argument_list|,
literal|"JPY"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|mockParams
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_RATES_FILE_LOCATION
argument_list|,
literal|"open-exchange-rates.json"
argument_list|)
expr_stmt|;
name|oerp
operator|=
operator|new
name|OpenExchangeRatesOrgProvider
argument_list|()
expr_stmt|;
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInit
specifier|public
name|void
name|testInit
parameter_list|()
throws|throws
name|Exception
block|{
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
comment|// don't inform, we don't want to hit any of these URLs
name|assertEquals
argument_list|(
literal|"Wrong url"
argument_list|,
literal|"open-exchange-rates.json"
argument_list|,
name|oerp
operator|.
name|ratesFileLocation
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong default interval"
argument_list|,
operator|(
literal|1440
operator|*
literal|60
operator|)
argument_list|,
name|oerp
operator|.
name|refreshIntervalSeconds
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_RATES_FILE_LOCATION
argument_list|,
literal|"http://foo.bar/baz"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_REFRESH_INTERVAL
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong param set url"
argument_list|,
literal|"http://foo.bar/baz"
argument_list|,
name|oerp
operator|.
name|ratesFileLocation
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong param interval"
argument_list|,
operator|(
literal|100
operator|*
literal|60
operator|)
argument_list|,
name|oerp
operator|.
name|refreshIntervalSeconds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testList
specifier|public
name|void
name|testList
parameter_list|()
block|{
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|oerp
operator|.
name|listAvailableCurrencies
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetExchangeRate
specifier|public
name|void
name|testGetExchangeRate
parameter_list|()
block|{
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|81.29D
argument_list|,
name|oerp
operator|.
name|getExchangeRate
argument_list|(
literal|"USD"
argument_list|,
literal|"JPY"
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"USD"
argument_list|,
name|oerp
operator|.
name|rates
operator|.
name|getBaseCurrency
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Needs currentTimeMillis to construct rates file contents"
argument_list|)
annotation|@
name|Test
DECL|method|testReload
specifier|public
name|void
name|testReload
parameter_list|()
block|{
comment|// reminder: interval is in minutes
name|mockParams
operator|.
name|put
argument_list|(
name|OpenExchangeRatesOrgProvider
operator|.
name|PARAM_REFRESH_INTERVAL
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|init
argument_list|(
name|mockParams
argument_list|)
expr_stmt|;
name|oerp
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
comment|// reminder: timestamp is in seconds
name|assertEquals
argument_list|(
name|HARDCODED_TEST_TIMESTAMP
argument_list|,
name|oerp
operator|.
name|rates
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
comment|// modify the timestamp to be "current" then fetch a rate and ensure no reload
specifier|final
name|long
name|currentTimestamp
init|=
call|(
name|long
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|/
literal|1000
argument_list|)
decl_stmt|;
name|oerp
operator|.
name|rates
operator|.
name|setTimestamp
argument_list|(
name|currentTimestamp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|81.29D
argument_list|,
name|oerp
operator|.
name|getExchangeRate
argument_list|(
literal|"USD"
argument_list|,
literal|"JPY"
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|currentTimestamp
argument_list|,
name|oerp
operator|.
name|rates
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
comment|// roll back clock on timestamp and ensure rate fetch does reload
name|oerp
operator|.
name|rates
operator|.
name|setTimestamp
argument_list|(
name|currentTimestamp
operator|-
operator|(
literal|101
operator|*
literal|60
operator|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|81.29D
argument_list|,
name|oerp
operator|.
name|getExchangeRate
argument_list|(
literal|"USD"
argument_list|,
literal|"JPY"
argument_list|)
argument_list|,
literal|0.0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"timestamp wasn't reset to hardcoded value, indicating no reload"
argument_list|,
name|HARDCODED_TEST_TIMESTAMP
argument_list|,
name|oerp
operator|.
name|rates
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testNoInit
specifier|public
name|void
name|testNoInit
parameter_list|()
block|{
name|oerp
operator|.
name|getExchangeRate
argument_list|(
literal|"ABC"
argument_list|,
literal|"DEF"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have thrown exception if not initialized"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

