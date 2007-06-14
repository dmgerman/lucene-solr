begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * @author ryan  * @version $Id$  * @since solr 1.2  */
end_comment

begin_interface
DECL|interface|ContentStream
specifier|public
interface|interface
name|ContentStream
block|{
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getSourceInfo
name|String
name|getSourceInfo
parameter_list|()
function_decl|;
DECL|method|getContentType
name|String
name|getContentType
parameter_list|()
function_decl|;
comment|/**    * @return the stream size or<code>null</code> if not known    */
DECL|method|getSize
name|Long
name|getSize
parameter_list|()
function_decl|;
comment|// size if we know it, otherwise null
comment|/**    * Get an open stream.  You are responsible for closing it.  Consider using     * something like:    *<pre>    *   InputStream stream = stream.getStream();    *   try {    *     // use the stream...    *   }    *   finally {    *     IOUtils.closeQuietly(reader);    *   }    *</pre>    *      * Only the first call to<code>getStream()</code> or<code>getReader()</code>    * is gaurenteed to work.  The runtime behavior for aditional calls is undefined.    */
DECL|method|getStream
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get an open stream.  You are responsible for closing it.  Consider using     * something like:    *<pre>    *   Reader reader = stream.getReader();    *   try {    *     // use the reader...    *   }    *   finally {    *     IOUtils.closeQuietly(reader);    *   }    *</pre>    *      * Only the first call to<code>getStream()</code> or<code>getReader()</code>    * is gaurenteed to work.  The runtime behavior for aditional calls is undefined.    */
DECL|method|getReader
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

