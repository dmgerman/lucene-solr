begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Directory provider for implementations that do not persist over reboots.  *   */
end_comment

begin_class
DECL|class|EphemeralDirectoryFactory
specifier|public
specifier|abstract
class|class
name|EphemeralDirectoryFactory
extends|extends
name|CachingDirectoryFactory
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fullPath
init|=
name|normalize
argument_list|(
name|path
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|CacheValue
name|cacheValue
init|=
name|byPathCache
operator|.
name|get
argument_list|(
name|fullPath
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cacheValue
operator|!=
literal|null
condition|)
block|{
name|directory
operator|=
name|cacheValue
operator|.
name|directory
expr_stmt|;
block|}
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
DECL|method|isPersistent
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isAbsolute
specifier|public
name|boolean
name|isAbsolute
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ram dir does not persist its dir anywhere
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ram dir does not persist its dir anywhere
block|}
DECL|method|cleanupOldIndexDirectories
specifier|public
name|void
name|cleanupOldIndexDirectories
parameter_list|(
specifier|final
name|String
name|dataDirPath
parameter_list|,
specifier|final
name|String
name|currentIndexDirPath
parameter_list|,
name|boolean
name|reload
parameter_list|)
block|{
comment|// currently a no-op
block|}
block|}
end_class

end_unit

