begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_comment
comment|/**  * Token for a replication session, for guaranteeing that source replicated  * files will be kept safe until the replication completes.  *   * @see Replicator#checkForUpdate(String)  * @see Replicator#release(String)  * @see LocalReplicator#DEFAULT_SESSION_EXPIRATION_THRESHOLD  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SessionToken
specifier|public
specifier|final
class|class
name|SessionToken
block|{
comment|/**    * ID of this session.    * Should be passed when releasing the session, thereby acknowledging the     * {@link Replicator Replicator} that this session is no longer in use.    * @see Replicator#release(String)    */
DECL|field|id
specifier|public
specifier|final
name|String
name|id
decl_stmt|;
comment|/**    * @see Revision#getVersion()    */
DECL|field|version
specifier|public
specifier|final
name|String
name|version
decl_stmt|;
comment|/**    * @see Revision#getSourceFiles()    */
DECL|field|sourceFiles
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|sourceFiles
decl_stmt|;
comment|/** Constructor which deserializes from the given {@link DataInput}. */
DECL|method|SessionToken
specifier|public
name|SessionToken
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|id
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourceFiles
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|int
name|numSources
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
while|while
condition|(
name|numSources
operator|>
literal|0
condition|)
block|{
name|String
name|source
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|int
name|numFiles
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RevisionFile
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numFiles
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFiles
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|RevisionFile
name|file
init|=
operator|new
name|RevisionFile
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|file
operator|.
name|size
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|sourceFiles
operator|.
name|put
argument_list|(
name|source
argument_list|,
name|files
argument_list|)
expr_stmt|;
operator|--
name|numSources
expr_stmt|;
block|}
block|}
comment|/** Constructor with the given id and revision. */
DECL|method|SessionToken
specifier|public
name|SessionToken
parameter_list|(
name|String
name|id
parameter_list|,
name|Revision
name|revision
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|revision
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourceFiles
operator|=
name|revision
operator|.
name|getSourceFiles
argument_list|()
expr_stmt|;
block|}
comment|/** Serialize the token data for communication between server and client. */
DECL|method|serialize
specifier|public
name|void
name|serialize
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|sourceFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|e
range|:
name|sourceFiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RevisionFile
argument_list|>
name|files
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RevisionFile
name|file
range|:
name|files
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|file
operator|.
name|fileName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|file
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"id="
operator|+
name|id
operator|+
literal|" version="
operator|+
name|version
operator|+
literal|" files="
operator|+
name|sourceFiles
return|;
block|}
block|}
end_class

end_unit

