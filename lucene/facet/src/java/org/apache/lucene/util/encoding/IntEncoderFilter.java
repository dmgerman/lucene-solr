begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An abstract implementation of {@link IntEncoder} which wraps another encoder.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IntEncoderFilter
specifier|public
specifier|abstract
class|class
name|IntEncoderFilter
extends|extends
name|IntEncoder
block|{
DECL|field|encoder
specifier|protected
specifier|final
name|IntEncoder
name|encoder
decl_stmt|;
DECL|method|IntEncoderFilter
specifier|protected
name|IntEncoderFilter
parameter_list|(
name|IntEncoder
name|encoder
parameter_list|)
block|{
name|this
operator|.
name|encoder
operator|=
name|encoder
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|encoder
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

