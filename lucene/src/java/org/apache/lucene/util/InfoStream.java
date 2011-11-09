begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/** @lucene.internal */
end_comment

begin_class
DECL|class|InfoStream
specifier|public
specifier|abstract
class|class
name|InfoStream
implements|implements
name|Closeable
block|{
comment|// Used for printing messages
DECL|field|MESSAGE_ID
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|MESSAGE_ID
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|messageID
specifier|protected
specifier|final
name|int
name|messageID
init|=
name|MESSAGE_ID
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
comment|/** prints a message */
DECL|method|message
specifier|public
specifier|abstract
name|void
name|message
parameter_list|(
name|String
name|component
parameter_list|,
name|String
name|message
parameter_list|)
function_decl|;
DECL|field|defaultInfoStream
specifier|private
specifier|static
name|InfoStream
name|defaultInfoStream
decl_stmt|;
comment|/** The default infoStream (possibly null) used    * by a newly instantiated classes.    * @see #setDefault */
DECL|method|getDefault
specifier|public
specifier|static
name|InfoStream
name|getDefault
parameter_list|()
block|{
return|return
name|defaultInfoStream
return|;
block|}
comment|/** Sets the default infoStream (possibly null) used    * by a newly instantiated classes.    * @see #setDefault */
DECL|method|setDefault
specifier|public
specifier|static
name|void
name|setDefault
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|)
block|{
name|defaultInfoStream
operator|=
name|infoStream
expr_stmt|;
block|}
block|}
end_class

end_unit

