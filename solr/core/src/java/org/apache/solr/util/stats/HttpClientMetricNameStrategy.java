begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_comment
comment|/**  * Strategy for creating metric names for HttpClient  * Copied from metrics-httpclient library  */
end_comment

begin_interface
DECL|interface|HttpClientMetricNameStrategy
specifier|public
interface|interface
name|HttpClientMetricNameStrategy
block|{
DECL|method|getNameFor
name|String
name|getNameFor
parameter_list|(
name|String
name|scope
parameter_list|,
name|HttpRequest
name|request
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

