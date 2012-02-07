begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Filename filter that attempts to accept only filenames  * created by Lucene.  Note that this is a "best effort"  * process.  If a file is used in a Lucene index, it will  * always match the file; but if a file is not used in a  * Lucene index but is named in a similar way to Lucene's  * files then this filter may accept the file.  *  *<p>This does not accept<code>*-write.lock</code> files.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|IndexFileNameFilter
specifier|public
class|class
name|IndexFileNameFilter
implements|implements
name|FilenameFilter
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|FilenameFilter
name|INSTANCE
init|=
operator|new
name|IndexFileNameFilter
argument_list|()
decl_stmt|;
DECL|method|IndexFileNameFilter
specifier|private
name|IndexFileNameFilter
parameter_list|()
block|{   }
comment|// Approximate match for files that seem to be Lucene
comment|// index files.  This can easily over-match, ie if some
comment|// app names a file _foo_bar.go:
DECL|field|luceneFilePattern
specifier|private
specifier|final
name|Pattern
name|luceneFilePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^_[a-z0-9]+(_[a-z0-9]+)?\\.[a-z0-9]+$"
argument_list|)
decl_stmt|;
comment|/* (non-Javadoc)    * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)    */
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Has an extension
return|return
name|luceneFilePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
else|else
block|{
comment|// No extension -- only segments_N file;
return|return
name|name
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

