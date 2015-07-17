begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A serialization schema for HLLs. Reads and writes HLL metadata to  * and from<code>byte[]</code> representations.  */
end_comment

begin_interface
DECL|interface|ISchemaVersion
interface|interface
name|ISchemaVersion
block|{
comment|/**      * The number of metadata bytes required for a serialized HLL of the      * specified type.      *      * @param  type the type of the serialized HLL      * @return the number of padding bytes needed in order to fully accommodate      *         the needed metadata.      */
DECL|method|paddingBytes
name|int
name|paddingBytes
parameter_list|(
name|HLLType
name|type
parameter_list|)
function_decl|;
comment|/**      * Writes metadata bytes to serialized HLL.      *      * @param bytes the padded data bytes of the HLL      * @param metadata the metadata to write to the padding bytes      */
DECL|method|writeMetadata
name|void
name|writeMetadata
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|IHLLMetadata
name|metadata
parameter_list|)
function_decl|;
comment|/**      * Reads the metadata bytes of the serialized HLL.      *      * @param  bytes the serialized HLL      * @return the HLL metadata      */
DECL|method|readMetadata
name|IHLLMetadata
name|readMetadata
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
function_decl|;
comment|/**      * Builds an HLL serializer that matches this schema version.      *      * @param  type the HLL type that will be serialized. This cannot be      *<code>null</code>.      * @param  wordLength the length of the 'words' that comprise the data of the      *         HLL. Words must be at least 5 bits and at most 64 bits long.      * @param  wordCount the number of 'words' in the HLL's data.      * @return a byte array serializer used to serialize a HLL according      *         to this schema version's specification.      * @see #paddingBytes(HLLType)      * @see IWordSerializer      */
DECL|method|getSerializer
name|IWordSerializer
name|getSerializer
parameter_list|(
name|HLLType
name|type
parameter_list|,
name|int
name|wordLength
parameter_list|,
name|int
name|wordCount
parameter_list|)
function_decl|;
comment|/**      * Builds an HLL deserializer that matches this schema version.      *      * @param  type the HLL type that will be deserialized. This cannot be      *<code>null</code>.      * @param  wordLength the length of the 'words' that comprise the data of the      *         serialized HLL. Words must be at least 5 bits and at most 64      *         bits long.      * @param  bytes the serialized HLL to deserialize. This cannot be      *<code>null</code>.      * @return a byte array deserializer used to deserialize a HLL serialized      *         according to this schema version's specification.      */
DECL|method|getDeserializer
name|IWordDeserializer
name|getDeserializer
parameter_list|(
name|HLLType
name|type
parameter_list|,
name|int
name|wordLength
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
function_decl|;
comment|/**      * @return the schema version number.      */
DECL|method|schemaVersionNumber
name|int
name|schemaVersionNumber
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

