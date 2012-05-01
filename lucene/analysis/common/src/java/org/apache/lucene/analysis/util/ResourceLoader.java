begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Abstraction for loading resources (streams, files, and classes).  */
end_comment

begin_interface
DECL|interface|ResourceLoader
specifier|public
interface|interface
name|ResourceLoader
block|{
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Accesses a resource by name and returns the (non comment) lines    * containing data.    *    *<p>    * A comment line is any line that starts with the character "#"    *</p>    *    * @param resource    * @return a list of non-blank non-comment lines with whitespace trimmed    * from front and back.    * @throws IOException    */
DECL|method|getLines
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLines
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|newInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|newInstance
parameter_list|(
name|String
name|cname
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|expectedType
parameter_list|,
name|String
modifier|...
name|subpackages
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

