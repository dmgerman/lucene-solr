begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|Closeable
import|;
end_import

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
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import

begin_comment
comment|/**  * Provides access to a Directory implementation. You must release every  * Directory that you get.  */
end_comment

begin_class
DECL|class|DirectoryFactory
specifier|public
specifier|abstract
class|class
name|DirectoryFactory
implements|implements
name|NamedListInitializedPlugin
implements|,
name|Closeable
block|{
comment|/**    * Close the this and all of the Directories it contains.    *     * @throws IOException    */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a new Directory for a given path.    *     * @throws IOException    */
DECL|method|create
specifier|protected
specifier|abstract
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns true if a Directory exists for a given path.    *     */
DECL|method|exists
specifier|public
specifier|abstract
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
comment|/**    * Returns the Directory for a given path, using the specified rawLockType.    * Will return the same Directory instance for the same path.    *     * @throws IOException    */
DECL|method|get
specifier|public
specifier|abstract
name|Directory
name|get
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|rawLockType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the Directory for a given path, using the specified rawLockType.    * Will return the same Directory instance for the same path unless forceNew,    * in which case a new Directory is returned.    *     * @throws IOException    */
DECL|method|get
specifier|public
specifier|abstract
name|Directory
name|get
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|rawLockType
parameter_list|,
name|boolean
name|forceNew
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Increment the number of references to the given Directory. You must call    * release for every call to this method.    *     */
DECL|method|incRef
specifier|public
specifier|abstract
name|void
name|incRef
parameter_list|(
name|Directory
name|directory
parameter_list|)
function_decl|;
comment|/**    * Releases the Directory so that it may be closed when it is no longer    * referenced.    *     * @throws IOException    */
DECL|method|release
specifier|public
specifier|abstract
name|void
name|release
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

