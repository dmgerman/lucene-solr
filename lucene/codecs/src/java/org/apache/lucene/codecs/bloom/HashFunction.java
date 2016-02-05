begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|bloom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Base class for hashing functions that can be referred to by name.  * Subclasses are expected to provide threadsafe implementations of the hash function  * on the range of bytes referenced in the provided {@link BytesRef}  * @lucene.experimental  */
end_comment

begin_class
DECL|class|HashFunction
specifier|public
specifier|abstract
class|class
name|HashFunction
block|{
comment|/**    * Hashes the contents of the referenced bytes    * @param bytes the data to be hashed    * @return the hash of the bytes referenced by bytes.offset and length bytes.length    */
DECL|method|hash
specifier|public
specifier|abstract
name|int
name|hash
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
function_decl|;
block|}
end_class

end_unit

