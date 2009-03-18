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
name|File
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

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|FileUtils
specifier|public
class|class
name|FileUtils
block|{
comment|/**    * Resolves a path relative a base directory.    *    *<p>    * This method does what "new File(base,path)"<b>Should</b> do, it wasn't     * completely lame: If path is absolute, then a File for that path is returned;    * if it's not absoluve, then a File is returnd using "path" as a child     * of "base")     *</p>    */
DECL|method|resolvePath
specifier|public
specifier|static
name|File
name|resolvePath
parameter_list|(
name|File
name|base
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|r
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|isAbsolute
argument_list|()
condition|?
name|r
else|:
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

