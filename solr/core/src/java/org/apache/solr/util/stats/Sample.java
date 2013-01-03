begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/*  * Forked from https://github.com/codahale/metrics  */
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

begin_comment
comment|/**  * A statistically representative sample of a data stream.  */
end_comment

begin_interface
DECL|interface|Sample
specifier|public
interface|interface
name|Sample
block|{
comment|/**    * Clears all recorded values.    */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Returns the number of values recorded.    *    * @return the number of values recorded    */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Adds a new recorded value to the sample.    *    * @param value a new recorded value    */
DECL|method|update
name|void
name|update
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Returns a snapshot of the sample's values.    *    * @return a snapshot of the sample's values    */
DECL|method|getSnapshot
name|Snapshot
name|getSnapshot
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

