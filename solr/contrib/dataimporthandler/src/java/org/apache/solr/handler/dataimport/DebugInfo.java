begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|common
operator|.
name|SolrInputDocument
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|common
operator|.
name|util
operator|.
name|StrUtils
import|;
end_import

begin_class
DECL|class|DebugInfo
specifier|public
class|class
name|DebugInfo
block|{
DECL|class|ChildRollupDocs
specifier|private
specifier|static
specifier|final
class|class
name|ChildRollupDocs
extends|extends
name|AbstractList
argument_list|<
name|SolrInputDocument
argument_list|>
block|{
DECL|field|delegate
specifier|private
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|delegate
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|SolrInputDocument
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|SolrInputDocument
name|e
parameter_list|)
block|{
name|SolrInputDocument
name|transformed
init|=
name|e
operator|.
name|deepCopy
argument_list|()
decl_stmt|;
if|if
condition|(
name|transformed
operator|.
name|hasChildDocuments
argument_list|()
condition|)
block|{
name|ChildRollupDocs
name|childList
init|=
operator|new
name|ChildRollupDocs
argument_list|()
decl_stmt|;
name|childList
operator|.
name|addAll
argument_list|(
name|transformed
operator|.
name|getChildDocuments
argument_list|()
argument_list|)
expr_stmt|;
name|transformed
operator|.
name|addField
argument_list|(
literal|"_childDocuments_"
argument_list|,
name|childList
argument_list|)
expr_stmt|;
name|transformed
operator|.
name|getChildDocuments
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|delegate
operator|.
name|add
argument_list|(
name|transformed
argument_list|)
return|;
block|}
block|}
DECL|field|debugDocuments
specifier|public
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|debugDocuments
init|=
operator|new
name|ChildRollupDocs
argument_list|()
decl_stmt|;
DECL|field|debugVerboseOutput
specifier|public
name|NamedList
argument_list|<
name|String
argument_list|>
name|debugVerboseOutput
init|=
literal|null
decl_stmt|;
DECL|field|verbose
specifier|public
name|boolean
name|verbose
decl_stmt|;
DECL|method|DebugInfo
specifier|public
name|DebugInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|requestParams
parameter_list|)
block|{
name|verbose
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"verbose"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|debugVerboseOutput
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

