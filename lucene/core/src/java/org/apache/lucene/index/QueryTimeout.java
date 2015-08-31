begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Base for query timeout implementations, which will provide a {@code shouldExit()} method,  * used with {@link ExitableDirectoryReader}.  */
end_comment

begin_interface
DECL|interface|QueryTimeout
specifier|public
interface|interface
name|QueryTimeout
block|{
comment|/**    * Called from {@link ExitableDirectoryReader.ExitableTermsEnum#next()}     * to determine whether to stop processing a query.    */
DECL|method|shouldExit
specifier|public
specifier|abstract
name|boolean
name|shouldExit
parameter_list|()
function_decl|;
comment|/** Returns true if timeouts are enabled for this query (i.e. if shouldExit would ever return true) */
DECL|method|isTimeoutEnabled
specifier|public
specifier|default
name|boolean
name|isTimeoutEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_interface

end_unit

