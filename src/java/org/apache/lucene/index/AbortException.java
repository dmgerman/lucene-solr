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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|// Used only internally to DW to call abort "up the stack"
end_comment

begin_class
DECL|class|AbortException
class|class
name|AbortException
extends|extends
name|IOException
block|{
DECL|method|AbortException
specifier|public
name|AbortException
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|DocumentsWriter
name|docWriter
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|initCause
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|setAborting
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

