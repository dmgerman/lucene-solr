begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Optional
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
name|cloud
operator|.
name|ZkController
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|params
operator|.
name|CoreAdminParams
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|backup
operator|.
name|repository
operator|.
name|BackupRepository
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
name|handler
operator|.
name|RestoreCore
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import

begin_class
DECL|class|RestoreCoreOp
class|class
name|RestoreCoreOp
implements|implements
name|CoreAdminHandler
operator|.
name|CoreAdminOp
block|{
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|CoreAdminHandler
operator|.
name|CallInfo
name|it
parameter_list|)
throws|throws
name|Exception
block|{
name|ZkController
name|zkController
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getZkController
argument_list|()
decl_stmt|;
if|if
condition|(
name|zkController
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Only valid for SolrCloud"
argument_list|)
throw|;
block|}
specifier|final
name|SolrParams
name|params
init|=
name|it
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
operator|+
literal|" is required"
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
operator|+
literal|" is required"
argument_list|)
throw|;
block|}
name|String
name|repoName
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|BACKUP_REPOSITORY
argument_list|)
decl_stmt|;
name|BackupRepository
name|repository
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|newBackupRepository
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|repoName
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|location
init|=
name|repository
operator|.
name|getBackupLocation
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|BACKUP_LOCATION
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"'location' is not specified as a query"
operator|+
literal|" parameter or as a default repository property"
argument_list|)
throw|;
block|}
name|URI
name|locationUri
init|=
name|repository
operator|.
name|createURI
argument_list|(
name|location
argument_list|)
decl_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|it
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
init|)
block|{
name|RestoreCore
name|restoreCore
init|=
operator|new
name|RestoreCore
argument_list|(
name|repository
argument_list|,
name|core
argument_list|,
name|locationUri
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
name|restoreCore
operator|.
name|doRestore
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Failed to restore core="
operator|+
name|core
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

