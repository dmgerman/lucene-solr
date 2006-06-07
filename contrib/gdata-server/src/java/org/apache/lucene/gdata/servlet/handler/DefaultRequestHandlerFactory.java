begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
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

begin_comment
comment|/**   * Default implementation for RequestHandlerFactory Builds the   * {@link org.apache.lucene.gdata.servlet.handler.GDataRequestHandler}   * instances.   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|DefaultRequestHandlerFactory
specifier|public
class|class
name|DefaultRequestHandlerFactory
extends|extends
name|RequestHandlerFactory
block|{
DECL|method|DefaultRequestHandlerFactory
name|DefaultRequestHandlerFactory
parameter_list|()
block|{
comment|//
block|}
comment|/**       * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getUpdateHandler()       */
annotation|@
name|Override
DECL|method|getUpdateHandler
specifier|public
name|GDataRequestHandler
name|getUpdateHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultUpdateHandler
argument_list|()
return|;
block|}
comment|/**       * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getDeleteHandler()       */
annotation|@
name|Override
DECL|method|getDeleteHandler
specifier|public
name|GDataRequestHandler
name|getDeleteHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultDeleteHandler
argument_list|()
return|;
block|}
comment|/**       * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getQueryHandler()       */
annotation|@
name|Override
DECL|method|getQueryHandler
specifier|public
name|GDataRequestHandler
name|getQueryHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultGetHandler
argument_list|()
return|;
block|}
comment|/**       * @see org.apache.lucene.gdata.servlet.handler.RequestHandlerFactory#getInsertHandler()       */
annotation|@
name|Override
DECL|method|getInsertHandler
specifier|public
name|GDataRequestHandler
name|getInsertHandler
parameter_list|()
block|{
return|return
operator|new
name|DefaultInsertHandler
argument_list|()
return|;
block|}
block|}
end_class

end_unit

