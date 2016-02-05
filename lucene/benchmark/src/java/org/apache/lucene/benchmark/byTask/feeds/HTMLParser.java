begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * HTML Parsing Interface for test purposes  */
end_comment

begin_interface
DECL|interface|HTMLParser
specifier|public
interface|interface
name|HTMLParser
block|{
comment|/**    * Parse the input Reader and return DocData.     * The provided name,title,date are used for the result, unless when they're null,     * in which case an attempt is made to set them from the parsed data.    * @param docData result reused    * @param name name of the result doc data.    * @param date date of the result doc data. If null, attempt to set by parsed data.    * @param reader reader of html text to parse.    * @param trecSrc the {@link TrecContentSource} used to parse dates.       * @return Parsed doc data.    * @throws IOException If there is a low-level I/O error.    */
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|TrecContentSource
name|trecSrc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

