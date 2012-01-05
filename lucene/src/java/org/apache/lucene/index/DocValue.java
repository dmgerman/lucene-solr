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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|DocValuesConsumer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DocValuesField
import|;
end_import

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
comment|/**  * Per document and field values consumed by {@link DocValuesConsumer}.   * @see DocValuesField  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|DocValue
specifier|public
interface|interface
name|DocValue
block|{
comment|/**    * Returns the set {@link BytesRef} or<code>null</code> if not set.    */
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|()
function_decl|;
comment|/**    * Returns the set {@link BytesRef} comparator or<code>null</code> if not set    */
DECL|method|bytesComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
parameter_list|()
function_decl|;
comment|/**    * Returns the set floating point value or<code>0.0d</code> if not set.    */
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|()
function_decl|;
comment|/**    * Returns the set<code>long</code> value of<code>0</code> if not set.    */
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

