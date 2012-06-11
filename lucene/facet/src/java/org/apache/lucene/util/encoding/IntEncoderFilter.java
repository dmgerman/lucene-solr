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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * An abstract implementation of {@link IntEncoder} which is served as a filter  * on the values to encode. An encoder filter wraps another {@link IntEncoder}  * which does the actual encoding. This allows for chaining filters and  * encoders, such as:<code><pre>  * new UniqueValuesIntEncoder(new DGapIntEncoder(new VInt8IntEnoder()));  * {@link UniqueValuesIntEncoder} followed by {@link DGapIntEncoder}</pre></code>  *<p>  * The default implementation implements {@link #close()} by closing the wrapped  * encoder and {@link #reInit(OutputStream)} by re-initializing the wrapped  * encoder.  *   * @lucene.experimental  */
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
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// There is no need to call super.close(), since we don't pass the output
comment|// stream to super.
name|encoder
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reInit
specifier|public
name|void
name|reInit
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|encoder
operator|.
name|reInit
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

