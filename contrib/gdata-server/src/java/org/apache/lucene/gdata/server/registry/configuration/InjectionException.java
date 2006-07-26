begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.registry.configuration
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
name|registry
operator|.
name|configuration
package|;
end_package

begin_comment
comment|/**  * Will be throw if an exception occures while injecting properties, a type or  * cast exception occures or a  * {@link org.apache.lucene.gdata.server.registry.configuration.Requiered}  * property is not available.  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|InjectionException
specifier|public
class|class
name|InjectionException
extends|extends
name|RuntimeException
block|{
comment|/**      *       */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3559845601111510210L
decl_stmt|;
comment|/**      * Constructs a new InjectionException      */
DECL|method|InjectionException
specifier|public
name|InjectionException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new InjectionException      *       * @param message -      *            the exception message      */
DECL|method|InjectionException
specifier|public
name|InjectionException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new InjectionException      *       * @param message -      *            the exception message      * @param cause -      *            the root cause of this exception      */
DECL|method|InjectionException
specifier|public
name|InjectionException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new InjectionException      *       * @param cause -      *            the root cause of this exception      */
DECL|method|InjectionException
specifier|public
name|InjectionException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

