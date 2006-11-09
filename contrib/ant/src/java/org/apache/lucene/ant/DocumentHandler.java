begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.ant
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|ant
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  *  Allows a class to act as a Lucene document handler  *  *@author     Erik Hatcher  *@created    October 27, 2001  */
end_comment

begin_interface
DECL|interface|DocumentHandler
specifier|public
interface|interface
name|DocumentHandler
block|{
comment|/**      *  Gets the document attribute of the DocumentHandler object      *      *@param  file  Description of Parameter      *@return       The document value      */
DECL|method|getDocument
name|Document
name|getDocument
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|DocumentHandlerException
function_decl|;
block|}
end_interface

end_unit

