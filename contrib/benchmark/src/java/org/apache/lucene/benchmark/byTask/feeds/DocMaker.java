begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * Create documents for the test.  *<br>Each call to makeDocument would create the next document.  * When input is exhausted, the DocMaker iterates over the input again,  * providing a source for unlimited number of documents,  * though not all of them are unique.   */
end_comment

begin_interface
DECL|interface|DocMaker
specifier|public
interface|interface
name|DocMaker
block|{
comment|/**     * Create the next document, of the given size by input bytes.    * If the implementation does not support control over size, an exception is thrown.    * @param size size of document, or 0 if there is no size requirement.    * @exception if cannot make the document, or if size>0 was specified but this feature is not supported.    */
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/** Create the next document. */
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/** Set the properties */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
function_decl|;
comment|/** Reset inputs so that the test run would behave, input wise, as if it just started. */
DECL|method|resetInputs
specifier|public
name|void
name|resetInputs
parameter_list|()
function_decl|;
comment|/** Return how many real unique texts are available, 0 if not applicable. */
DECL|method|numUniqueTexts
specifier|public
name|int
name|numUniqueTexts
parameter_list|()
function_decl|;
comment|/** Return total bytes of all available unique texts, 0 if not applicable */
DECL|method|numUniqueBytes
specifier|public
name|long
name|numUniqueBytes
parameter_list|()
function_decl|;
comment|/** Return number of docs made since last reset. */
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
function_decl|;
comment|/** Return total byte size of docs made since last reset. */
DECL|method|getByteCount
specifier|public
name|long
name|getByteCount
parameter_list|()
function_decl|;
comment|/** Print some statistics on docs available/added/etc. */
DECL|method|printDocStatistics
specifier|public
name|void
name|printDocStatistics
parameter_list|()
function_decl|;
comment|/** Set the html parser to use, when appropriate */
DECL|method|setHTMLParser
specifier|public
name|void
name|setHTMLParser
parameter_list|(
name|HTMLParser
name|htmlParser
parameter_list|)
function_decl|;
comment|/** Returns the htmlParser. */
DECL|method|getHtmlParser
specifier|public
name|HTMLParser
name|getHtmlParser
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

