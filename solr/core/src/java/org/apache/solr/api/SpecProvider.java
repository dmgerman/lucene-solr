begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.api
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|api
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
name|util
operator|.
name|ValidatingJsonMap
import|;
end_import

begin_comment
comment|/**A generic interface for any class that is capable of providing its specification as a json schema  */
end_comment

begin_interface
DECL|interface|SpecProvider
specifier|public
interface|interface
name|SpecProvider
block|{
DECL|method|getSpec
name|ValidatingJsonMap
name|getSpec
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

