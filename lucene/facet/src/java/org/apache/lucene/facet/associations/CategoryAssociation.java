begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|store
operator|.
name|ByteArrayDataInput
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
name|store
operator|.
name|ByteArrayDataOutput
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
name|store
operator|.
name|DataInput
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
name|store
operator|.
name|DataOutput
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Allows associating an arbitrary value with a {@link CategoryPath}.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|CategoryAssociation
specifier|public
interface|interface
name|CategoryAssociation
block|{
comment|/** Serializes the associated value into the given {@link DataOutput}. */
DECL|method|serialize
specifier|public
name|void
name|serialize
parameter_list|(
name|ByteArrayDataOutput
name|output
parameter_list|)
function_decl|;
comment|/** Deserializes the association value from the given {@link DataInput}. */
DECL|method|deserialize
specifier|public
name|void
name|deserialize
parameter_list|(
name|ByteArrayDataInput
name|input
parameter_list|)
function_decl|;
comment|/** Returns the maximum bytes needed to encode the association value. */
DECL|method|maxBytesNeeded
specifier|public
name|int
name|maxBytesNeeded
parameter_list|()
function_decl|;
comment|/**    * Returns the ID of the category association. The ID is used as e.g. the    * term's text under which to encode the association values.    */
DECL|method|getCategoryListID
specifier|public
name|String
name|getCategoryListID
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

