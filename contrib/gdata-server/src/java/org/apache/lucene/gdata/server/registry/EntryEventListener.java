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

begin_comment
comment|/**  * The EntryEventListener interface should be implemented by any class needs to be informed about any changes on entries.  * To register a class as a EntryEventListener use:  *<p>  *<tt>  * GdataServerRegistry.registerEntryEventListener(EntryEventListener);  *<tt>  *</p>  * @author Simon Willnauer  *  */
end_comment

begin_interface
DECL|interface|EntryEventListener
specifier|public
interface|interface
name|EntryEventListener
block|{
comment|/**      * will be invoked on every successful update on every entry      * @param entry the updated entry      */
DECL|method|fireUpdateEvent
specifier|public
specifier|abstract
name|void
name|fireUpdateEvent
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * will be invoked on every successful entry insert      * @param entry      */
DECL|method|fireInsertEvent
specifier|public
specifier|abstract
name|void
name|fireInsertEvent
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
function_decl|;
comment|/**      * will be invoked on every successful entry delete      * @param entry      */
DECL|method|fireDeleteEvent
specifier|public
specifier|abstract
name|void
name|fireDeleteEvent
parameter_list|(
name|ServerBaseEntry
name|entry
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

