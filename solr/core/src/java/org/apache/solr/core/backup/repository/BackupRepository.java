begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core.backup.repository
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|backup
operator|.
name|repository
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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|store
operator|.
name|IOContext
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
comment|/**  * This interface defines the functionality required to backup/restore Solr indexes to an arbitrary storage system.  */
end_comment

begin_interface
DECL|interface|BackupRepository
specifier|public
interface|interface
name|BackupRepository
extends|extends
name|NamedListInitializedPlugin
extends|,
name|Closeable
block|{
comment|/**    * A parameter to specify the name of the backup repository to be used.    */
DECL|field|REPOSITORY_PROPERTY_NAME
name|String
name|REPOSITORY_PROPERTY_NAME
init|=
literal|"repository"
decl_stmt|;
comment|/**    * This enumeration defines the type of a given path.    */
DECL|enum|PathType
enum|enum
name|PathType
block|{
DECL|enum constant|DIRECTORY
DECL|enum constant|FILE
name|DIRECTORY
block|,
name|FILE
block|}
comment|/**    * This method returns the value of the specified configuration property.    */
DECL|method|getConfigProperty
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfigProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * This method creates a URI using the specified path components (as method arguments).    *    * @param pathComponents    *          The directory (or file-name) to be included in the URI.    * @return A URI containing absolute path    */
DECL|method|createURI
name|URI
name|createURI
parameter_list|(
name|String
modifier|...
name|pathComponents
parameter_list|)
function_decl|;
comment|/**    * This method checks if the specified path exists in this repository.    *    * @param path    *          The path whose existence needs to be checked.    * @return if the specified path exists in this repository.    * @throws IOException    *           in case of errors    */
DECL|method|exists
name|boolean
name|exists
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns the type of a specified path    *    * @param path    *          The path whose type needs to be checked.    * @return the {@linkplain PathType} for the specified path    * @throws IOException    *           in case of errors    */
DECL|method|getPathType
name|PathType
name|getPathType
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns all the entries (files and directories) in the specified directory.    *    * @param path    *          The directory path    * @return an array of strings, one for each entry in the directory    * @throws IOException    *           in case of errors    */
DECL|method|listAll
name|String
index|[]
name|listAll
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns a Lucene input stream reading an existing file.    *    * @param dirPath    *          The parent directory of the file to be read    * @param fileName    *          The name of the file to be read    * @param ctx    *          the Lucene IO context    * @return Lucene {@linkplain IndexInput} reference    * @throws IOException    *           in case of errors    */
DECL|method|openInput
name|IndexInput
name|openInput
parameter_list|(
name|URI
name|dirPath
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|ctx
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method returns a {@linkplain OutputStream} instance for the specified<code>path</code>    *    * @param path    *          The path for which {@linkplain OutputStream} needs to be created    * @return {@linkplain OutputStream} instance for the specified<code>path</code>    * @throws IOException    *           in case of errors    */
DECL|method|createOutput
name|OutputStream
name|createOutput
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method creates a directory at the specified path.    *    * @param path    *          The path where the directory needs to be created.    * @throws IOException    *           in case of errors    */
DECL|method|createDirectory
name|void
name|createDirectory
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method deletes a directory at the specified path.    *    * @param path    *          The path referring to the directory to be deleted.    * @throws IOException    *           in case of errors    */
DECL|method|deleteDirectory
name|void
name|deleteDirectory
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Copy a file from specified<code>sourceDir</code> to the destination repository (i.e. backup).    *    * @param sourceDir    *          The source directory hosting the file to be copied.    * @param fileName    *          The name of the file to by copied    * @param dest    *          The destination backup location.    * @throws IOException    *           in case of errors    */
DECL|method|copyFileFrom
name|void
name|copyFileFrom
parameter_list|(
name|Directory
name|sourceDir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|URI
name|dest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Copy a file from specified<code>sourceRepo</code> to the destination directory (i.e. restore).    *    * @param sourceRepo    *          The source URI hosting the file to be copied.    * @param fileName    *          The name of the file to by copied    * @param dest    *          The destination where the file should be copied.    * @throws IOException    *           in case of errors.    */
DECL|method|copyFileTo
name|void
name|copyFileTo
parameter_list|(
name|URI
name|sourceRepo
parameter_list|,
name|String
name|fileName
parameter_list|,
name|Directory
name|dest
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

