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

begin_comment
comment|/**  * Tests currency field type using OpenExchangeRatesOrgProvider.  */
end_comment

begin_class
DECL|class|CurrencyFieldOpenExchangeTest
specifier|public
class|class
name|CurrencyFieldOpenExchangeTest
extends|extends
name|AbstractCurrencyFieldTest
block|{
DECL|method|field
specifier|public
name|String
name|field
parameter_list|()
block|{
return|return
literal|"oer_amount"
return|;
block|}
block|}
end_class

end_unit

