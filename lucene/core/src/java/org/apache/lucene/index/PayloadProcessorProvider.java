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
name|IOException
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
name|Directory
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
comment|/**  * Provides a {@link ReaderPayloadProcessor} to be used for a {@link Directory}.  * This allows using different {@link ReaderPayloadProcessor}s for different  * source {@link AtomicReader}, for e.g. to perform different processing of payloads of  * different directories.  *<p>  *<b>NOTE:</b> to avoid processing payloads of certain directories, you can  * return<code>null</code> in {@link #getReaderProcessor}.  *<p>  *<b>NOTE:</b> it is possible that the same {@link ReaderPayloadProcessor} will be  * requested for the same {@link Directory} concurrently. Therefore, to avoid  * concurrency issues you should return different instances for different  * threads. Usually, if your {@link ReaderPayloadProcessor} does not maintain state  * this is not a problem. The merge code ensures that the  * {@link ReaderPayloadProcessor} instance you return will be accessed by one  * thread to obtain the {@link PayloadProcessor}s for different terms.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|PayloadProcessorProvider
specifier|public
specifier|abstract
class|class
name|PayloadProcessorProvider
block|{
comment|/**    * Returns a {@link PayloadProcessor} for a given {@link Term} which allows    * processing the payloads of different terms differently. If you intent to    * process all your payloads the same way, then you can ignore the given term.    *<p>    *<b>NOTE:</b> if you protect your {@link ReaderPayloadProcessor} from    * concurrency issues, then you shouldn't worry about any such issues when    * {@link PayloadProcessor}s are requested for different terms.    */
DECL|class|ReaderPayloadProcessor
specifier|public
specifier|static
specifier|abstract
class|class
name|ReaderPayloadProcessor
block|{
comment|/** Returns a {@link PayloadProcessor} for the given term. */
DECL|method|getProcessor
specifier|public
specifier|abstract
name|PayloadProcessor
name|getProcessor
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Processes the given payload.    *     * @lucene.experimental    */
DECL|class|PayloadProcessor
specifier|public
specifier|static
specifier|abstract
class|class
name|PayloadProcessor
block|{
comment|/** Process the incoming payload and stores the result in the given {@link BytesRef}. */
DECL|method|processPayload
specifier|public
specifier|abstract
name|void
name|processPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Returns a {@link ReaderPayloadProcessor} for the given {@link Directory},    * through which {@link PayloadProcessor}s can be obtained for each    * {@link Term}, or<code>null</code> if none should be used.    */
DECL|method|getReaderProcessor
specifier|public
specifier|abstract
name|ReaderPayloadProcessor
name|getReaderProcessor
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

