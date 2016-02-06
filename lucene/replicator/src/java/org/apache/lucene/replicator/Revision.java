begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|IndexInput
import|;
end_import

begin_comment
comment|/**  * A revision comprises lists of files that come from different sources and need  * to be replicated together to e.g. guarantee that all resources are in sync.  * In most cases an application will replicate a single index, and so the  * revision will contain files from a single source. However, some applications  * may require to treat a collection of indexes as a single entity so that the  * files from all sources are replicated together, to guarantee consistency  * beween them. For example, an application which indexes facets will need to  * replicate both the search and taxonomy indexes together, to guarantee that  * they match at the client side.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Revision
specifier|public
interface|interface
name|Revision
extends|extends
name|Comparable
argument_list|<
name|Revision
argument_list|>
block|{
comment|/**    * Compares the revision to the given version string. Behaves like    * {@link Comparable#compareTo(Object)}.    */
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|String
name|version
parameter_list|)
function_decl|;
comment|/**    * Returns a string representation of the version of this revision. The    * version is used by {@link #compareTo(String)} as well as to    * serialize/deserialize revision information. Therefore it must be self    * descriptive as well as be able to identify one revision from another.    */
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Returns the files that comprise this revision, as a mapping from a source    * to a list of files.    */
DECL|method|getSourceFiles
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|getSourceFiles
parameter_list|()
function_decl|;
comment|/**    * Returns an {@link IndexInput} for the given fileName and source. It is the    * caller's respnsibility to close the {@link IndexInput} when it has been    * consumed.    */
DECL|method|open
specifier|public
name|InputStream
name|open
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called when this revision can be safely released, i.e. where there are no    * more references to it.    */
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

