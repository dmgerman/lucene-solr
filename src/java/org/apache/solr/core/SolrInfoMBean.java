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
comment|/**  * @author ronp  * @version $Id: SolrInfoMBean.java,v 1.3 2005/05/04 19:15:23 ronp Exp $  */
end_comment

begin_comment
comment|// MBean interface for getting various ui friendly strings and URLs
end_comment

begin_comment
comment|// for use by objects which are 'plugable' to make administering
end_comment

begin_comment
comment|// production use easier
end_comment

begin_comment
comment|// name        - simple common usage name, e.g. BasicQueryHandler
end_comment

begin_comment
comment|// version     - simple common usage version, e.g. 2.0
end_comment

begin_comment
comment|// description - simple one or two line description
end_comment

begin_comment
comment|// cvsId       - yes, really the CVS Id      (type 'man co')
end_comment

begin_comment
comment|// cvsName     - yes, really the CVS Name    (type 'man co')
end_comment

begin_comment
comment|// cvsSource   - yes, really the CVS Source  (type 'man co')
end_comment

begin_comment
comment|// docs        - URL list: TWIKI, Faq, Design doc, something! :)
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
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
function_decl|;
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
function_decl|;
DECL|method|getCvsId
specifier|public
name|String
name|getCvsId
parameter_list|()
function_decl|;
DECL|method|getCvsName
specifier|public
name|String
name|getCvsName
parameter_list|()
function_decl|;
DECL|method|getCvsSource
specifier|public
name|String
name|getCvsSource
parameter_list|()
function_decl|;
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
function_decl|;
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

