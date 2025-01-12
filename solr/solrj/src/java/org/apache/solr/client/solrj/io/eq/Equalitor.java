begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.eq
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eq
package|;
end_package

begin_comment
comment|/**  * Interface defining a way to determine if two items are equal  *   * This borrows from Java 8's BiPredicate interface but to keep Java 7 compatible   * we will not use that interface directory. We will use the test method, however,  * so that future refactoring for Java 8 is simplified.  */
end_comment

begin_interface
DECL|interface|Equalitor
specifier|public
interface|interface
name|Equalitor
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|test
specifier|public
name|boolean
name|test
parameter_list|(
name|T
name|left
parameter_list|,
name|T
name|right
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

