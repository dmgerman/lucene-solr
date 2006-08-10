begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.registry
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
package|;
end_package

begin_comment
comment|/**  * To Register a class as a component in the  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry} the class  * or a super class must implements this interface.  *<p>  *<tt>ServerComponent</tt> defines a method<tt>initialize</tt> and  *<tt>destroy</tt>.<tt>initialize</tt> will be called when the component  * is registered and<tt>destroy</tt> when the registry is destroyed (usually  * at server shut down).</p>  * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry  * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|ServerComponent
specifier|public
interface|interface
name|ServerComponent
block|{
comment|/**      * will be call when the component is registered.      * if this fails the server must not startup.      */
DECL|method|initialize
specifier|public
specifier|abstract
name|void
name|initialize
parameter_list|()
function_decl|;
comment|/**      * will be called when the registry is going down e.g. when the  {@link GDataServerRegistry#destroy()} method is called.      */
DECL|method|destroy
specifier|public
specifier|abstract
name|void
name|destroy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

