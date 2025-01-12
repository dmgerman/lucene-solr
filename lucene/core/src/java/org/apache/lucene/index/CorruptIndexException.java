begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
comment|/**  * This exception is thrown when Lucene detects  * an inconsistency in the index.  */
end_comment

begin_class
DECL|class|CorruptIndexException
specifier|public
class|class
name|CorruptIndexException
extends|extends
name|IOException
block|{
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
DECL|field|resourceDescription
specifier|private
specifier|final
name|String
name|resourceDescription
decl_stmt|;
comment|/** Create exception with a message only */
DECL|method|CorruptIndexException
specifier|public
name|CorruptIndexException
parameter_list|(
name|String
name|message
parameter_list|,
name|DataInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|input
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create exception with a message only */
DECL|method|CorruptIndexException
specifier|public
name|CorruptIndexException
parameter_list|(
name|String
name|message
parameter_list|,
name|DataOutput
name|output
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|output
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create exception with message and root cause. */
DECL|method|CorruptIndexException
specifier|public
name|CorruptIndexException
parameter_list|(
name|String
name|message
parameter_list|,
name|DataInput
name|input
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|Objects
operator|.
name|toString
argument_list|(
name|input
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/** Create exception with message and root cause. */
DECL|method|CorruptIndexException
specifier|public
name|CorruptIndexException
parameter_list|(
name|String
name|message
parameter_list|,
name|DataOutput
name|output
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|Objects
operator|.
name|toString
argument_list|(
name|output
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/** Create exception with a message only */
DECL|method|CorruptIndexException
specifier|public
name|CorruptIndexException
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|resourceDescription
parameter_list|)
block|{
name|this
argument_list|(
name|message
argument_list|,
name|resourceDescription
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create exception with message and root cause. */
DECL|method|CorruptIndexException
specifier|public
name|CorruptIndexException
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|resourceDescription
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|message
argument_list|)
operator|+
literal|" (resource="
operator|+
name|resourceDescription
operator|+
literal|")"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|resourceDescription
operator|=
name|resourceDescription
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
comment|/**    * Returns a description of the file that was corrupted    */
DECL|method|getResourceDescription
specifier|public
name|String
name|getResourceDescription
parameter_list|()
block|{
return|return
name|resourceDescription
return|;
block|}
comment|/**    * Returns the original exception message without the corrupted file description.    */
DECL|method|getOriginalMessage
specifier|public
name|String
name|getOriginalMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

