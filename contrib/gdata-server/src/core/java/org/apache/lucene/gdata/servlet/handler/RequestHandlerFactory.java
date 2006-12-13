begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.servlet.handler
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
operator|.
name|handler
package|;
end_package

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
name|ServerComponent
import|;
end_import

begin_comment
comment|/**  * Abstract Superclass for RequestHandlerFactories  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|RequestHandlerFactory
specifier|public
specifier|abstract
class|class
name|RequestHandlerFactory
implements|implements
name|ServerComponent
block|{
comment|/**      * public constructor to enable loading via the registry      * @see org.apache.lucene.gdata.server.registry.Component      * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry      */
DECL|method|RequestHandlerFactory
specifier|public
name|RequestHandlerFactory
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a EntryUpdateHandler which processes a GDATA UPDATE request.      * @return - a RequestHandlerInstance      */
DECL|method|getEntryUpdateHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getEntryUpdateHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a EntryDeleteHandler which processes a GDATA DELETE request.      * @return - a RequestHandlerInstance      */
DECL|method|getEntryDeleteHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getEntryDeleteHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a FeedQueryHandler which processes a GDATA Query / Get request.      * @return - a RequestHandlerInstance      */
DECL|method|getFeedQueryHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getFeedQueryHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a EntryInsertHandler which processes a GDATA Insert request.      * @return - a RequestHandlerInstance      */
DECL|method|getEntryInsertHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getEntryInsertHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a InsertAccountHandler which processes a Account Insert request.      * @return - a RequestHandlerInstance      */
DECL|method|getInsertAccountHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getInsertAccountHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a DeleteAccountHandler which processes a Account Delete request.      * @return - a RequestHandlerInstance      */
DECL|method|getDeleteAccountHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getDeleteAccountHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a UpdateAccountHandler which processes a Account Update request.      * @return - a RequestHandlerInstance      */
DECL|method|getUpdateAccountHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getUpdateAccountHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a InsertFeedHandler which processes a Feed Insert request.      * @return - a RequestHandlerInstance      */
DECL|method|getInsertFeedHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getInsertFeedHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a UpdateFeedHandler which processes a Feed Insert request.      * @return - a RequestHandlerInstance      */
DECL|method|getUpdateFeedHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getUpdateFeedHandler
parameter_list|()
function_decl|;
comment|/**      * Creates a DeleteFeedHandler which processes a Feed Insert request.      * @return - a RequestHandlerInstance      */
DECL|method|getDeleteFeedHandler
specifier|public
specifier|abstract
name|GDataRequestHandler
name|getDeleteFeedHandler
parameter_list|()
function_decl|;
block|}
end_class

end_unit

