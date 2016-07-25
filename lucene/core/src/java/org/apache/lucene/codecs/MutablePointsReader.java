begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/** {@link PointsReader} whose order of points can be changed.  *  This class is useful for codecs to optimize flush.  *  @lucene.internal */
end_comment

begin_class
DECL|class|MutablePointsReader
specifier|public
specifier|abstract
class|class
name|MutablePointsReader
extends|extends
name|PointsReader
block|{
comment|/** Sole constructor. */
DECL|method|MutablePointsReader
specifier|protected
name|MutablePointsReader
parameter_list|()
block|{}
comment|/** Fill {@code packedValue} with the packed bytes of the i-th value. */
DECL|method|getValue
specifier|public
specifier|abstract
name|void
name|getValue
parameter_list|(
name|int
name|i
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
function_decl|;
comment|/** Get the k-th byte of the i-th value. */
DECL|method|getByteAt
specifier|public
specifier|abstract
name|byte
name|getByteAt
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|k
parameter_list|)
function_decl|;
comment|/** Return the doc ID of the i-th value. */
DECL|method|getDocID
specifier|public
specifier|abstract
name|int
name|getDocID
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/** Swap the i-th and j-th values. */
DECL|method|swap
specifier|public
specifier|abstract
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
function_decl|;
block|}
end_class

end_unit

