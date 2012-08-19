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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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

begin_interface
DECL|interface|StorableField
specifier|public
interface|interface
name|StorableField
extends|extends
name|GeneralField
block|{
comment|/** Non-null if this field has a binary value */
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a string value */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a Reader value */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a numeric value */
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

