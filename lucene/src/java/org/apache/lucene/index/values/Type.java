begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** Controls whether per-field values are stored into  *  index.  This storage is non-sparse, so it's best to  *  use this when all docs have the field, and loads all  *  values into RAM, exposing a random access API, when  *  loaded.  *  * @lucene.experimental   */
end_comment

begin_enum
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
comment|/** Integral value is stored as packed ints.  The bit    *  precision is fixed across the segment, and    *  determined by the min/max values in the field. */
DECL|enum constant|PACKED_INTS
name|PACKED_INTS
block|,
DECL|enum constant|SIMPLE_FLOAT_4BYTE
name|SIMPLE_FLOAT_4BYTE
block|,
DECL|enum constant|SIMPLE_FLOAT_8BYTE
name|SIMPLE_FLOAT_8BYTE
block|,
comment|// TODO(simonw): -- shouldn't lucene decide/detect straight vs
comment|// deref, as well fixed vs var?
DECL|enum constant|BYTES_FIXED_STRAIGHT
name|BYTES_FIXED_STRAIGHT
block|,
DECL|enum constant|BYTES_FIXED_DEREF
name|BYTES_FIXED_DEREF
block|,
DECL|enum constant|BYTES_FIXED_SORTED
name|BYTES_FIXED_SORTED
block|,
DECL|enum constant|BYTES_VAR_STRAIGHT
name|BYTES_VAR_STRAIGHT
block|,
DECL|enum constant|BYTES_VAR_DEREF
name|BYTES_VAR_DEREF
block|,
DECL|enum constant|BYTES_VAR_SORTED
name|BYTES_VAR_SORTED
comment|// TODO(simonw): -- need STRING variants as well
block|}
end_enum

end_unit

