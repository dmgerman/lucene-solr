begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Interface defining a factory for creating new {@link SpanCollector}s  */
end_comment

begin_interface
DECL|interface|SpanCollectorFactory
specifier|public
interface|interface
name|SpanCollectorFactory
block|{
comment|/**    * @return a new SpanCollector    */
DECL|method|newCollector
name|SpanCollector
name|newCollector
parameter_list|()
function_decl|;
comment|/**    * Factory for creating NO_OP collectors    */
DECL|field|NO_OP_FACTORY
specifier|public
specifier|static
specifier|final
name|SpanCollectorFactory
name|NO_OP_FACTORY
init|=
operator|new
name|SpanCollectorFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SpanCollector
name|newCollector
parameter_list|()
block|{
return|return
name|SpanCollector
operator|.
name|NO_OP
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

