begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * Writes 'words' of fixed width, in sequence, to a byte array.  */
end_comment

begin_interface
DECL|interface|IWordSerializer
interface|interface
name|IWordSerializer
block|{
comment|/**      * Writes the word to the backing array.      *      * @param  word the word to write.      */
DECL|method|writeWord
name|void
name|writeWord
parameter_list|(
specifier|final
name|long
name|word
parameter_list|)
function_decl|;
comment|/**      * Returns the backing array of<code>byte</code>s that contain the serialized      * words.      * @return the serialized words as a<code>byte[]</code>.      */
DECL|method|getBytes
name|byte
index|[]
name|getBytes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

