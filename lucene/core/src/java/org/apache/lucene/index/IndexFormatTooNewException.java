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

begin_comment
comment|/**  * This exception is thrown when Lucene detects  * an index that is newer than this Lucene version.  */
end_comment

begin_class
DECL|class|IndexFormatTooNewException
specifier|public
class|class
name|IndexFormatTooNewException
extends|extends
name|IOException
block|{
comment|/** Creates an {@code IndexFormatTooNewException}    *    *  @param resourceDesc describes the file that was too old    *  @param version the version of the file that was too old    *  @param minVersion the minimum version accepted    *  @param maxVersion the maxium version accepted    *    * @lucene.internal */
DECL|method|IndexFormatTooNewException
specifier|public
name|IndexFormatTooNewException
parameter_list|(
name|String
name|resourceDesc
parameter_list|,
name|int
name|version
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
block|{
name|super
argument_list|(
literal|"Format version is not supported (resource "
operator|+
name|resourceDesc
operator|+
literal|"): "
operator|+
name|version
operator|+
literal|" (needs to be between "
operator|+
name|minVersion
operator|+
literal|" and "
operator|+
name|maxVersion
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an {@code IndexFormatTooNewException}    *    *  @param in the open file that's too old    *  @param version the version of the file that was too old    *  @param minVersion the minimum version accepted    *  @param maxVersion the maxium version accepted    *    * @lucene.internal */
DECL|method|IndexFormatTooNewException
specifier|public
name|IndexFormatTooNewException
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|version
parameter_list|,
name|int
name|minVersion
parameter_list|,
name|int
name|maxVersion
parameter_list|)
block|{
name|this
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|in
argument_list|)
argument_list|,
name|version
argument_list|,
name|minVersion
argument_list|,
name|maxVersion
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

