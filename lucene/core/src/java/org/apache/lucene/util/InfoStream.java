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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SegmentInfos
import|;
end_import

begin_comment
comment|// javadocs
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

begin_comment
comment|/**   * Debugging API for Lucene classes such as {@link IndexWriter}   * and {@link SegmentInfos}.  *<p>  * NOTE: Enabling infostreams may cause performance degradation  * in some components.  *   * @lucene.internal   */
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
comment|/** Instance of InfoStream that does no logging at all. */
DECL|field|NO_OUTPUT
specifier|public
specifier|static
specifier|final
name|InfoStream
name|NO_OUTPUT
init|=
operator|new
name|NoOutput
argument_list|()
decl_stmt|;
DECL|class|NoOutput
specifier|private
specifier|static
specifier|final
class|class
name|NoOutput
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
literal|false
operator|:
literal|"message() should not be called when isEnabled returns false"
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
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
block|}
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
comment|/** returns true if messages are enabled and should be posted to {@link #message}. */
DECL|method|isEnabled
specifier|public
specifier|abstract
name|boolean
name|isEnabled
parameter_list|(
name|String
name|component
parameter_list|)
function_decl|;
DECL|field|defaultInfoStream
specifier|private
specifier|static
name|InfoStream
name|defaultInfoStream
init|=
name|NO_OUTPUT
decl_stmt|;
comment|/** The default {@code InfoStream} used by a newly instantiated classes.    * @see #setDefault */
DECL|method|getDefault
specifier|public
specifier|static
specifier|synchronized
name|InfoStream
name|getDefault
parameter_list|()
block|{
return|return
name|defaultInfoStream
return|;
block|}
comment|/** Sets the default {@code InfoStream} used    * by a newly instantiated classes. It cannot be {@code null},    * to disable logging use {@link #NO_OUTPUT}.    * @see #getDefault */
DECL|method|setDefault
specifier|public
specifier|static
specifier|synchronized
name|void
name|setDefault
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot set InfoStream default implementation to null. "
operator|+
literal|"To disable logging use InfoStream.NO_OUTPUT"
argument_list|)
throw|;
block|}
name|defaultInfoStream
operator|=
name|infoStream
expr_stmt|;
block|}
block|}
end_class

end_unit

