begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
operator|.
name|StorageImplementation
import|;
end_import

begin_comment
comment|/**   *TODO document me   * @author Simon Willnauer   *   */
end_comment

begin_class
DECL|class|StorageFactory
specifier|public
class|class
name|StorageFactory
block|{
comment|/**       * Creates a {@link Storage} instance       * @return - a storage instance       * @throws StorageException  - if the storage can not be created       */
DECL|method|getStorage
specifier|public
specifier|static
name|Storage
name|getStorage
parameter_list|()
throws|throws
name|StorageException
block|{
try|try
block|{
return|return
operator|new
name|StorageImplementation
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|StorageException
name|ex
init|=
operator|new
name|StorageException
argument_list|(
literal|"Can't create Storage instance -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|ex
operator|.
name|setStackTrace
argument_list|(
name|e
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
end_class

end_unit

