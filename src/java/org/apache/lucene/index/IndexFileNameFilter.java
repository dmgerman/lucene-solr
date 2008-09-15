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

begin_comment
comment|/**  * Filename filter that accept filenames and extensions only created by Lucene.  *  * @version $rcs = ' $Id: Exp $ ' ;  */
end_comment

begin_class
DECL|class|IndexFileNameFilter
specifier|public
class|class
name|IndexFileNameFilter
implements|implements
name|FilenameFilter
block|{
DECL|field|singleton
specifier|static
name|IndexFileNameFilter
name|singleton
init|=
operator|new
name|IndexFileNameFilter
argument_list|()
decl_stmt|;
DECL|field|extensions
specifier|private
name|HashSet
name|extensions
decl_stmt|;
DECL|field|extensionsInCFS
specifier|private
name|HashSet
name|extensionsInCFS
decl_stmt|;
DECL|method|IndexFileNameFilter
specifier|public
name|IndexFileNameFilter
parameter_list|()
block|{
name|extensions
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|extensionsInCFS
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS_IN_COMPOUND_FILE
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|extensionsInCFS
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|INDEX_EXTENSIONS_IN_COMPOUND_FILE
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
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
name|int
name|i
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|extension
init|=
name|name
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|extensions
operator|.
name|contains
argument_list|(
name|extension
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|extension
operator|.
name|startsWith
argument_list|(
literal|"f"
argument_list|)
operator|&&
name|extension
operator|.
name|matches
argument_list|(
literal|"f\\d+"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|extension
operator|.
name|startsWith
argument_list|(
literal|"s"
argument_list|)
operator|&&
name|extension
operator|.
name|matches
argument_list|(
literal|"s\\d+"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|DELETABLE
argument_list|)
condition|)
return|return
literal|true
return|;
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns true if this is a file that would be contained    * in a CFS file.  This function should only be called on    * files that pass the above "accept" (ie, are already    * known to be a Lucene index file).    */
DECL|method|isCFSFile
specifier|public
name|boolean
name|isCFSFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|i
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|extension
init|=
name|name
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|extensionsInCFS
operator|.
name|contains
argument_list|(
name|extension
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|extension
operator|.
name|startsWith
argument_list|(
literal|"f"
argument_list|)
operator|&&
name|extension
operator|.
name|matches
argument_list|(
literal|"f\\d+"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|getFilter
specifier|public
specifier|static
name|IndexFileNameFilter
name|getFilter
parameter_list|()
block|{
return|return
name|singleton
return|;
block|}
block|}
end_class

end_unit

