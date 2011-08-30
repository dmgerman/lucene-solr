begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** A field with byte[] value that is only stored. */
end_comment

begin_class
DECL|class|BinaryField
specifier|public
specifier|final
class|class
name|BinaryField
extends|extends
name|Field
block|{
DECL|field|TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a new BinaryField */
DECL|method|BinaryField
specifier|public
name|BinaryField
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|BinaryField
operator|.
name|TYPE_STORED
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new BinaryField */
DECL|method|BinaryField
specifier|public
name|BinaryField
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|BinaryField
operator|.
name|TYPE_STORED
argument_list|,
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new BinaryField */
DECL|method|BinaryField
specifier|public
name|BinaryField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|BinaryField
operator|.
name|TYPE_STORED
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

