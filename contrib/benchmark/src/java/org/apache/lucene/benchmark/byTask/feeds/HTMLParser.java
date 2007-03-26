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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * HTML Parsing Interfacew for test purposes  */
end_comment

begin_interface
DECL|interface|HTMLParser
specifier|public
interface|interface
name|HTMLParser
block|{
comment|/**    * Parse the input Reader and return DocData.     * A provided name or date is used for the result, otherwise an attempt is     * made to set them from the parsed data.    * @param dateFormat date formatter to use for extracting the date.       * @param name name of the result doc data. If null, attempt to set by parsed data.    * @param date date of the result doc data. If null, attempt to set by parsed data.    * @param reader of html text to parse.    * @return Parsed doc data.    * @throws IOException    * @throws InterruptedException    */
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|DateFormat
name|dateFormat
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Parse the inputText and return DocData.     * @param inputText the html text to parse.    * @see #parse(String, Date, Reader, DateFormat)    */
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|StringBuffer
name|inputText
parameter_list|,
name|DateFormat
name|dateFormat
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

