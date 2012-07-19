begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * Subclasses of CharFilter can be chained to filter a Reader  * They can be used as {@link java.io.Reader} with additional offset  * correction. {@link Tokenizer}s will automatically use {@link #correctOffset}  * if a CharFilter subclass is used.  */
end_comment

begin_class
DECL|class|CharFilter
specifier|public
specifier|abstract
class|class
name|CharFilter
extends|extends
name|FilterReader
block|{
comment|/**    * Create a new CharFilter wrapping the provided reader.    * @param in a Reader, can also be a CharFilter for chaining.    */
DECL|method|CharFilter
specifier|public
name|CharFilter
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**    * Subclasses override to correct the current offset.    *    * @param currentOff current offset    * @return corrected offset    */
DECL|method|correct
specifier|protected
specifier|abstract
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
function_decl|;
comment|/**    * Chains the corrected offset through the input    * CharFilter(s).    */
DECL|method|correctOffset
specifier|public
specifier|final
name|int
name|correctOffset
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
specifier|final
name|int
name|corrected
init|=
name|correct
argument_list|(
name|currentOff
argument_list|)
decl_stmt|;
return|return
operator|(
name|in
operator|instanceof
name|CharFilter
operator|)
condition|?
operator|(
operator|(
name|CharFilter
operator|)
name|in
operator|)
operator|.
name|correctOffset
argument_list|(
name|corrected
argument_list|)
else|:
name|corrected
return|;
block|}
block|}
end_class

end_unit

