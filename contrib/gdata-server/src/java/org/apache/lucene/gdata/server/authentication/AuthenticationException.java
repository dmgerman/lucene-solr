begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.authentication
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
name|authentication
package|;
end_package

begin_comment
comment|/**  *   * @author Simon Willnauer  *  */
end_comment

begin_class
DECL|class|AuthenticationException
specifier|public
class|class
name|AuthenticationException
extends|extends
name|Exception
block|{
comment|/**      *       */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5467768078178612671L
decl_stmt|;
comment|/**      * Constructs a new Authentication Exception      */
DECL|method|AuthenticationException
specifier|public
name|AuthenticationException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new Authentication Exception with the specified detail message       * @param arg0 - detail message      */
DECL|method|AuthenticationException
specifier|public
name|AuthenticationException
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new Authentication Exception with the specified detail message and      * nested exception caused this exception.       * @param arg0 -      *            detail message      * @param arg1 -      *            nested exception      */
DECL|method|AuthenticationException
specifier|public
name|AuthenticationException
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Throwable
name|arg1
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new Authentication Exception with a nested exception caused this exception      * @param arg0 - nested exception      */
DECL|method|AuthenticationException
specifier|public
name|AuthenticationException
parameter_list|(
name|Throwable
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

