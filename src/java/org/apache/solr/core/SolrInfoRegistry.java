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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrInfoMBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author ronp  * @version $Id$  */
end_comment

begin_comment
comment|// A Registry to hold a collection of SolrInfo objects
end_comment

begin_class
DECL|class|SolrInfoRegistry
specifier|public
class|class
name|SolrInfoRegistry
block|{
DECL|field|cvsId
specifier|public
specifier|static
specifier|final
name|String
name|cvsId
init|=
literal|"$Id$"
decl_stmt|;
DECL|field|cvsSource
specifier|public
specifier|static
specifier|final
name|String
name|cvsSource
init|=
literal|"$Source: /cvs/main/searching/solr/solarcore/src/solr/SolrInfoRegistry.java,v $"
decl_stmt|;
DECL|field|cvsName
specifier|public
specifier|static
specifier|final
name|String
name|cvsName
init|=
literal|"$Name:  $"
decl_stmt|;
DECL|field|inst
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|inst
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getRegistry
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInfoMBean
argument_list|>
name|getRegistry
parameter_list|()
block|{
return|return
name|inst
return|;
block|}
block|}
end_class

end_unit

