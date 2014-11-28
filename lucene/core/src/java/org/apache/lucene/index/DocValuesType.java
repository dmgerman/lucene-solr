begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * DocValues types. Note that DocValues is strongly typed, so a  * field cannot have different types across different documents.  */
end_comment

begin_enum
DECL|enum|DocValuesType
specifier|public
enum|enum
name|DocValuesType
block|{
comment|/**    * No doc values for this field.    */
DECL|enum constant|NONE
name|NONE
block|,
comment|/**     * A per-document Number    */
DECL|enum constant|NUMERIC
name|NUMERIC
block|,
comment|/**    * A per-document byte[].  Values may be larger than    * 32766 bytes, but different codecs may enforce their own limits.    */
DECL|enum constant|BINARY
name|BINARY
block|,
comment|/**     * A pre-sorted byte[]. Fields with this type only store distinct byte values     * and store an additional offset pointer per document to dereference the shared     * byte[]. The stored byte[] is presorted and allows access via document id,     * ordinal and by-value.  Values must be {@code<= 32766} bytes.    */
DECL|enum constant|SORTED
name|SORTED
block|,
comment|/**     * A pre-sorted Number[]. Fields with this type store numeric values in sorted    * order according to {@link Long#compare(long, long)}.    */
DECL|enum constant|SORTED_NUMERIC
name|SORTED_NUMERIC
block|,
comment|/**     * A pre-sorted Set&lt;byte[]&gt;. Fields with this type only store distinct byte values     * and store additional offset pointers per document to dereference the shared     * byte[]s. The stored byte[] is presorted and allows access via document id,     * ordinal and by-value.  Values must be {@code<= 32766} bytes.    */
DECL|enum constant|SORTED_SET
name|SORTED_SET
block|, }
end_enum

end_unit

