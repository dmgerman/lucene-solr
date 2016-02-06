begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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

begin_comment
comment|/**  * Prints nothing. Just to make sure tests pass w/ and without enabled InfoStream  * without actually making noise.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|NullInfoStream
specifier|public
class|class
name|NullInfoStream
extends|extends
name|InfoStream
block|{
annotation|@
name|Override
DECL|method|message
specifier|public
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
block|{
assert|assert
name|component
operator|!=
literal|null
assert|;
assert|assert
name|message
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|(
name|String
name|component
parameter_list|)
block|{
assert|assert
name|component
operator|!=
literal|null
assert|;
return|return
literal|true
return|;
comment|// to actually enable logging, we just ignore on message()
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{      }
block|}
end_class

end_unit

