begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|ServerBaseEntry
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
name|registry
operator|.
name|Component
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
name|registry
operator|.
name|ComponentType
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
name|Storage
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
name|StorageController
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|componentType
operator|=
name|ComponentType
operator|.
name|STORAGECONTROLLER
argument_list|)
DECL|class|StorageStub
specifier|public
class|class
name|StorageStub
implements|implements
name|Storage
implements|,
name|StorageController
block|{
DECL|field|SERVICE_TYPE_RETURN
specifier|public
specifier|static
name|String
name|SERVICE_TYPE_RETURN
init|=
literal|"service"
decl_stmt|;
comment|/**      *       */
DECL|method|StorageStub
specifier|public
name|StorageStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#storeEntry(org.apache.lucene.gdata.data.ServerBaseEntry)      */
DECL|method|storeEntry
specifier|public
name|BaseEntry
name|storeEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#deleteEntry(org.apache.lucene.gdata.data.ServerBaseEntry)      */
DECL|method|deleteEntry
specifier|public
name|void
name|deleteEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#updateEntry(org.apache.lucene.gdata.data.ServerBaseEntry)      */
DECL|method|updateEntry
specifier|public
name|BaseEntry
name|updateEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#getFeed(org.apache.lucene.gdata.data.ServerBaseFeed)      */
DECL|method|getFeed
specifier|public
name|BaseFeed
name|getFeed
parameter_list|(
name|ServerBaseFeed
name|feed
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#getEntry(org.apache.lucene.gdata.data.ServerBaseEntry)      */
DECL|method|getEntry
specifier|public
name|BaseEntry
name|getEntry
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#storeAccount(org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|storeAccount
specifier|public
name|void
name|storeAccount
parameter_list|(
name|GDataAccount
name|Account
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#updateAccount(org.apache.lucene.gdata.data.GDataAccount)      */
DECL|method|updateAccount
specifier|public
name|void
name|updateAccount
parameter_list|(
name|GDataAccount
name|Account
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#deleteAccount(java.lang.String)      */
DECL|method|deleteAccount
specifier|public
name|void
name|deleteAccount
parameter_list|(
name|String
name|Accountname
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#storeFeed(org.apache.lucene.gdata.data.ServerBaseFeed, java.lang.String)      */
DECL|method|storeFeed
specifier|public
name|void
name|storeFeed
parameter_list|(
name|ServerBaseFeed
name|feed
parameter_list|,
name|String
name|accountname
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#deleteFeed(java.lang.String)      */
DECL|method|deleteFeed
specifier|public
name|void
name|deleteFeed
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#updateFeed(org.apache.lucene.gdata.data.ServerBaseFeed, java.lang.String)      */
DECL|method|updateFeed
specifier|public
name|void
name|updateFeed
parameter_list|(
name|ServerBaseFeed
name|feed
parameter_list|,
name|String
name|accountname
parameter_list|)
throws|throws
name|StorageException
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#close()      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.Storage#getServiceForFeed(java.lang.String)      */
DECL|method|getServiceForFeed
specifier|public
name|String
name|getServiceForFeed
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
name|SERVICE_TYPE_RETURN
return|;
block|}
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
DECL|method|getStorage
specifier|public
name|Storage
name|getStorage
parameter_list|()
throws|throws
name|StorageException
block|{
return|return
operator|new
name|StorageStub
argument_list|()
return|;
block|}
DECL|method|getAccount
specifier|public
name|GDataAccount
name|getAccount
parameter_list|(
name|String
name|accountName
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
DECL|method|getAccountNameForFeedId
specifier|public
name|String
name|getAccountNameForFeedId
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{     }
DECL|method|getFeedLastModified
specifier|public
name|Long
name|getFeedLastModified
parameter_list|(
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
DECL|method|getEntryLastModified
specifier|public
name|Long
name|getEntryLastModified
parameter_list|(
name|String
name|entryId
parameter_list|,
name|String
name|feedId
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

