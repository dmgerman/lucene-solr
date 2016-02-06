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
name|Closeable
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * An interface for replicating files. Allows a producer to  * {@link #publish(Revision) publish} {@link Revision}s and consumers to  * {@link #checkForUpdate(String) check for updates}. When a client needs to be  * updated, it is given a {@link SessionToken} through which it can  * {@link #obtainFile(String, String, String) obtain} the files of that  * revision. After the client has finished obtaining all the files, it should  * {@link #release(String) release} the given session, so that the files can be  * reclaimed if they are not needed anymore.  *<p>  * A client is always updated to the newest revision available. That is, if a  * client is on revision<em>r1</em> and revisions<em>r2</em> and<em>r3</em>  * were published, then when the cllient will next check for update, it will  * receive<em>r3</em>.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Replicator
specifier|public
interface|interface
name|Replicator
extends|extends
name|Closeable
block|{
comment|/**    * Publish a new {@link Revision} for consumption by clients. It is the    * caller's responsibility to verify that the revision files exist and can be    * read by clients. When the revision is no longer needed, it will be    * {@link Revision#release() released} by the replicator.    */
DECL|method|publish
specifier|public
name|void
name|publish
parameter_list|(
name|Revision
name|revision
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check whether the given version is up-to-date and returns a    * {@link SessionToken} which can be used for fetching the revision files,    * otherwise returns {@code null}.    *<p>    *<b>NOTE:</b> when the returned session token is no longer needed, you    * should call {@link #release(String)} so that the session resources can be    * reclaimed, including the revision files.    */
DECL|method|checkForUpdate
specifier|public
name|SessionToken
name|checkForUpdate
parameter_list|(
name|String
name|currVersion
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Notify that the specified {@link SessionToken} is no longer needed by the    * caller.    */
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|String
name|sessionID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an {@link InputStream} for the requested file and source in the    * context of the given {@link SessionToken#id session}.    *<p>    *<b>NOTE:</b> it is the caller's responsibility to close the returned    * stream.    *     * @throws SessionExpiredException if the specified session has already    *         expired    */
DECL|method|obtainFile
specifier|public
name|InputStream
name|obtainFile
parameter_list|(
name|String
name|sessionID
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

