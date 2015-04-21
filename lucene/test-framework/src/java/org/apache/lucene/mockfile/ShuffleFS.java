begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystem
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Random
import|;
end_import

begin_comment
comment|/**  * Gives an unpredictable, but deterministic order to directory listings.  *<p>  * This can be useful if for instance, you have build servers on  * linux but developers are using macs.  */
end_comment

begin_class
DECL|class|ShuffleFS
specifier|public
class|class
name|ShuffleFS
extends|extends
name|FilterFileSystemProvider
block|{
DECL|field|seed
specifier|final
name|long
name|seed
decl_stmt|;
comment|/**     * Create a new instance, wrapping {@code delegate}.    */
DECL|method|ShuffleFS
specifier|public
name|ShuffleFS
parameter_list|(
name|FileSystem
name|delegate
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|(
literal|"shuffle://"
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newDirectoryStream
specifier|public
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|newDirectoryStream
parameter_list|(
name|Path
name|dir
parameter_list|,
name|Filter
argument_list|<
name|?
super|super
name|Path
argument_list|>
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
name|super
operator|.
name|newDirectoryStream
argument_list|(
name|dir
argument_list|,
name|filter
argument_list|)
init|)
block|{
comment|// read complete directory listing
name|List
argument_list|<
name|Path
argument_list|>
name|contents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|stream
control|)
block|{
name|contents
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|// sort first based only on filename
name|Collections
operator|.
name|sort
argument_list|(
name|contents
argument_list|,
parameter_list|(
name|path1
parameter_list|,
name|path2
parameter_list|)
lambda|->
name|path1
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|path2
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// sort based on current class seed
name|Collections
operator|.
name|shuffle
argument_list|(
name|contents
argument_list|,
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Path
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|contents
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

