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
comment|/**  * A numeric field that can contain 64-bit signed two's complement integer values.  *  *<ul>  *<li>Min Value Allowed: -9223372036854775808</li>  *<li>Max Value Allowed: 9223372036854775807</li>  *</ul>  *   * @see Long  */
end_comment

begin_class
DECL|class|TrieLongField
specifier|public
class|class
name|TrieLongField
extends|extends
name|TrieField
block|{
block|{
name|type
operator|=
name|TrieTypes
operator|.
name|LONG
expr_stmt|;
block|}
block|}
end_class

end_unit

