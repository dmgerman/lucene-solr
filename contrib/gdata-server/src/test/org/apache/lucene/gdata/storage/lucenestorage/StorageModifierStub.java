begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
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
operator|.
name|lucenestorage
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|StorageBuffer
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
name|StorageCoreController
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
name|StorageModifier
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
name|utils
operator|.
name|StorageControllerStub
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
name|index
operator|.
name|IndexModifier
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
name|RAMDirectory
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|StorageModifierStub
specifier|public
class|class
name|StorageModifierStub
extends|extends
name|StorageModifier
block|{
comment|/**      * @param controller      * @param modifier      * @param buffer      * @param persitsFactor      * @param optimizeInterval      * @throws IOException       * @throws StorageException       */
DECL|method|StorageModifierStub
specifier|public
name|StorageModifierStub
parameter_list|(
name|StorageCoreController
name|controller
parameter_list|,
name|IndexModifier
name|modifier
parameter_list|,
name|StorageBuffer
name|buffer
parameter_list|,
name|int
name|persitsFactor
parameter_list|,
name|int
name|optimizeInterval
parameter_list|)
throws|throws
name|IOException
throws|,
name|StorageException
block|{
name|super
argument_list|(
operator|new
name|StorageCoreController
argument_list|()
argument_list|,
operator|new
name|IndexModifier
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|StorageBuffer
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#close()      */
annotation|@
name|Override
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#createAccount(org.apache.lucene.gdata.storage.lucenestorage.StorageAccountWrapper)      */
annotation|@
name|Override
DECL|method|createAccount
specifier|public
name|void
name|createAccount
parameter_list|(
name|StorageAccountWrapper
name|account
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#createFeed(org.apache.lucene.gdata.storage.lucenestorage.StorageFeedWrapper)      */
annotation|@
name|Override
DECL|method|createFeed
specifier|public
name|void
name|createFeed
parameter_list|(
name|StorageFeedWrapper
name|wrapper
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#deleteAccount(java.lang.String)      */
annotation|@
name|Override
DECL|method|deleteAccount
specifier|public
name|void
name|deleteAccount
parameter_list|(
name|String
name|accountName
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#deleteEntry(org.apache.lucene.gdata.storage.lucenestorage.StorageEntryWrapper)      */
annotation|@
name|Override
DECL|method|deleteEntry
specifier|public
name|void
name|deleteEntry
parameter_list|(
name|StorageEntryWrapper
name|wrapper
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#deleteFeed(java.lang.String)      */
annotation|@
name|Override
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
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#forceWrite()      */
annotation|@
name|Override
DECL|method|forceWrite
specifier|protected
name|void
name|forceWrite
parameter_list|()
throws|throws
name|IOException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#insertEntry(org.apache.lucene.gdata.storage.lucenestorage.StorageEntryWrapper)      */
annotation|@
name|Override
DECL|method|insertEntry
specifier|public
name|void
name|insertEntry
parameter_list|(
name|StorageEntryWrapper
name|wrapper
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#updateAccount(org.apache.lucene.gdata.storage.lucenestorage.StorageAccountWrapper)      */
annotation|@
name|Override
DECL|method|updateAccount
specifier|public
name|void
name|updateAccount
parameter_list|(
name|StorageAccountWrapper
name|user
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#updateEntry(org.apache.lucene.gdata.storage.lucenestorage.StorageEntryWrapper)      */
annotation|@
name|Override
DECL|method|updateEntry
specifier|public
name|void
name|updateEntry
parameter_list|(
name|StorageEntryWrapper
name|wrapper
parameter_list|)
throws|throws
name|StorageException
block|{                       }
comment|/**      * @see org.apache.lucene.gdata.storage.lucenestorage.StorageModifier#updateFeed(org.apache.lucene.gdata.storage.lucenestorage.StorageFeedWrapper)      */
annotation|@
name|Override
DECL|method|updateFeed
specifier|public
name|void
name|updateFeed
parameter_list|(
name|StorageFeedWrapper
name|wrapper
parameter_list|)
throws|throws
name|StorageException
block|{                       }
block|}
end_class

end_unit

