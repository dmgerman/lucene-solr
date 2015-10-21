begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
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
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** One pass iterator through all points previously written with a  *  {@link PointWriter}, abstracting away whether points a read  *  from (offline) disk or simple arrays in heap. */
end_comment

begin_interface
DECL|interface|PointReader
interface|interface
name|PointReader
extends|extends
name|Closeable
block|{
comment|/** Returns false once iteration is done, else true. */
DECL|method|next
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the packed byte[] value */
DECL|method|packedValue
name|byte
index|[]
name|packedValue
parameter_list|()
function_decl|;
comment|/** Point ordinal */
DECL|method|ord
name|long
name|ord
parameter_list|()
function_decl|;
comment|/** DocID for this point */
DECL|method|docID
name|int
name|docID
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

