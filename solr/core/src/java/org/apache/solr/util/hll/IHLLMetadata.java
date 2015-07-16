begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * The metadata and parameters associated with a HLL.  */
end_comment

begin_interface
DECL|interface|IHLLMetadata
interface|interface
name|IHLLMetadata
block|{
comment|/**      * @return the schema version of the HLL. This will never be<code>null</code>.      */
DECL|method|schemaVersion
name|int
name|schemaVersion
parameter_list|()
function_decl|;
comment|/**      * @return the type of the HLL. This will never be<code>null</code>.      */
DECL|method|HLLType
name|HLLType
name|HLLType
parameter_list|()
function_decl|;
comment|/**      * @return the log-base-2 of the register count parameter of the HLL. This      *         will always be greater than or equal to 4 and less than or equal      *         to 31.      */
DECL|method|registerCountLog2
name|int
name|registerCountLog2
parameter_list|()
function_decl|;
comment|/**      * @return the register width parameter of the HLL. This will always be      *         greater than or equal to 1 and less than or equal to 8.      */
DECL|method|registerWidth
name|int
name|registerWidth
parameter_list|()
function_decl|;
comment|/**      * @return the log-base-2 of the explicit cutoff cardinality. This will always      *         be greater than or equal to zero and less than 31, per the specification.      */
DECL|method|log2ExplicitCutoff
name|int
name|log2ExplicitCutoff
parameter_list|()
function_decl|;
comment|/**      * @return<code>true</code> if the {@link HLLType#EXPLICIT} representation      *         has been disabled.<code>false</code> otherwise.      */
DECL|method|explicitOff
name|boolean
name|explicitOff
parameter_list|()
function_decl|;
comment|/**      * @return<code>true</code> if the {@link HLLType#EXPLICIT} representation      *         cutoff cardinality is set to be automatically chosen,      *<code>false</code> otherwise.      */
DECL|method|explicitAuto
name|boolean
name|explicitAuto
parameter_list|()
function_decl|;
comment|/**      * @return<code>true</code> if the {@link HLLType#SPARSE} representation      *         is enabled.      */
DECL|method|sparseEnabled
name|boolean
name|sparseEnabled
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

