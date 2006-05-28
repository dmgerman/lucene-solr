begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * MBean interface for getting various ui friendly strings and URLs  * for use by objects which are 'plugable' to make server administration  * easier.  *  * @author ronp  * @version $Id$  */
end_comment

begin_interface
DECL|interface|SolrInfoMBean
specifier|public
interface|interface
name|SolrInfoMBean
block|{
DECL|enum|Category
DECL|enum constant|CORE
DECL|enum constant|QUERYHANDLER
DECL|enum constant|UPDATEHANDLER
DECL|enum constant|CACHE
DECL|enum constant|OTHER
specifier|public
enum|enum
name|Category
block|{
name|CORE
block|,
name|QUERYHANDLER
block|,
name|UPDATEHANDLER
block|,
name|CACHE
block|,
name|OTHER
block|}
empty_stmt|;
comment|/**    * Simple common usage name, e.g. BasicQueryHandler,    * or fully qualified clas name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/** Simple common usage version, e.g. 2.0 */
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/** Simple one or two line description */
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/** Purpose of this Class */
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
function_decl|;
comment|/** CVS Id, SVN Id, etc */
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
function_decl|;
comment|/** CVS Source, SVN Source, etc */
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
function_decl|;
comment|/**    * Documentation URL list.    *    *<p>    * Suggested documentaion URLs: Homepage for sponsoring project,    * FAQ on class usage, Design doc for class, Wiki, bug reporting URL, etc...    *</p>    */
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
function_decl|;
comment|/**    * Any statistics this instance would like to be publicly available via    * the Solr Administration interface.    *    *<p>    * Any Object type may be stored in the list, but only the    *<code>toString()</code> representation will be used.    *</p>    */
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

