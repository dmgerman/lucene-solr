begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * @deprecated use JavaBinCodec instead  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|NamedListCodec
specifier|public
class|class
name|NamedListCodec
extends|extends
name|JavaBinCodec
block|{
DECL|method|NamedListCodec
specifier|public
name|NamedListCodec
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|NamedListCodec
specifier|public
name|NamedListCodec
parameter_list|(
name|ObjectResolver
name|resolver
parameter_list|)
block|{
name|super
argument_list|(
name|resolver
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

