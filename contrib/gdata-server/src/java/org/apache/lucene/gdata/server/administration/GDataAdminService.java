begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.administration
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|administration
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|data
operator|.
name|GDataAccount
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
name|data
operator|.
name|ServerBaseFeed
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
name|server
operator|.
name|GDataService
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
name|server
operator|.
name|ServiceException
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
name|StorageException
import|;
end_import

begin_comment
comment|/**  * default implementation of the {@link org.apache.lucene.gdata.server.administration.AdminService} interface.  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|GDataAdminService
specifier|public
class|class
name|GDataAdminService
extends|extends
name|GDataService
implements|implements
name|AdminService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|GDataAdminService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * @throws ServiceException      */
DECL|method|GDataAdminService
specifier|public
name|GDataAdminService
parameter_list|()
throws|throws
name|ServiceException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#createFeed(org.apache.lucene.gdata.data.ServerBaseFeed, org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|createFeed
specifier|public
name|void
name|createFeed
parameter_list|(
specifier|final
name|ServerBaseFeed
name|feed
parameter_list|,
specifier|final
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|feed
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not create feed -- feed is null"
argument_list|)
throw|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not create feed -- account is null"
argument_list|)
throw|;
if|if
condition|(
name|feed
operator|.
name|getId
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Feed ID is null can not create feed"
argument_list|)
throw|;
if|if
condition|(
name|account
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Account name is null -- can't create feed"
argument_list|)
throw|;
try|try
block|{
name|feed
operator|.
name|setAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
name|this
operator|.
name|storage
operator|.
name|storeFeed
argument_list|(
name|feed
argument_list|,
name|account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not save feed -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not save feed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#updateFeed(org.apache.lucene.gdata.data.ServerBaseFeed, org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|updateFeed
specifier|public
name|void
name|updateFeed
parameter_list|(
name|ServerBaseFeed
name|feed
parameter_list|,
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|feed
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not update null feed"
argument_list|)
throw|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not update feed -- account is null"
argument_list|)
throw|;
if|if
condition|(
name|feed
operator|.
name|getId
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Feed ID is null can not update feed"
argument_list|)
throw|;
if|if
condition|(
name|account
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Account name is null -- can't update feed"
argument_list|)
throw|;
try|try
block|{
name|feed
operator|.
name|setAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
name|this
operator|.
name|storage
operator|.
name|updateFeed
argument_list|(
name|feed
argument_list|,
name|account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not update feed -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not update feed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#deleteFeed(org.apache.lucene.gdata.data.ServerBaseFeed)      */
DECL|method|deleteFeed
specifier|public
name|void
name|deleteFeed
parameter_list|(
name|ServerBaseFeed
name|feed
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|feed
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not delete null feed"
argument_list|)
throw|;
if|if
condition|(
name|feed
operator|.
name|getId
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Feed ID is null can not delete feed"
argument_list|)
throw|;
try|try
block|{
name|this
operator|.
name|storage
operator|.
name|deleteFeed
argument_list|(
name|feed
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not delete feed -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not delete feed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#createAccount(org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|createAccount
specifier|public
name|void
name|createAccount
parameter_list|(
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not save null account"
argument_list|)
throw|;
try|try
block|{
name|this
operator|.
name|storage
operator|.
name|storeAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not save account -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not save account"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#deleteAccount(org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|deleteAccount
specifier|public
name|void
name|deleteAccount
parameter_list|(
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not delete null account"
argument_list|)
throw|;
try|try
block|{
name|this
operator|.
name|storage
operator|.
name|deleteAccount
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not save account -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not save account"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#updateAccount(org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|updateAccount
specifier|public
name|void
name|updateAccount
parameter_list|(
name|GDataAccount
name|account
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|account
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not update null account"
argument_list|)
throw|;
try|try
block|{
name|this
operator|.
name|storage
operator|.
name|updateAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not save account -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not save account"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#getAccount(java.lang.String)      */
DECL|method|getAccount
specifier|public
name|GDataAccount
name|getAccount
parameter_list|(
name|String
name|accountName
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|accountName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not get null account"
argument_list|)
throw|;
try|try
block|{
return|return
name|this
operator|.
name|storage
operator|.
name|getAccount
argument_list|(
name|accountName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not get account -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not get account"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.apache.lucene.gdata.server.administration.AdminService#getFeedOwningAccount(java.lang.String)      */
DECL|method|getFeedOwningAccount
specifier|public
name|GDataAccount
name|getFeedOwningAccount
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|ServiceException
block|{
if|if
condition|(
name|feedId
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not get account - feed id must not be null"
argument_list|)
throw|;
try|try
block|{
name|String
name|accountName
init|=
name|this
operator|.
name|storage
operator|.
name|getAccountNameForFeedId
argument_list|(
name|feedId
argument_list|)
decl_stmt|;
return|return
name|this
operator|.
name|storage
operator|.
name|getAccount
argument_list|(
name|accountName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not get account for feed Id -- "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Can not get account for the given feed id"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

