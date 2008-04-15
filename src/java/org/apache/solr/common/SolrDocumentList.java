begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Represent a list of SolrDocuments returned from a search.  This includes  * position and offset information.  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrDocumentList
specifier|public
class|class
name|SolrDocumentList
extends|extends
name|ArrayList
argument_list|<
name|SolrDocument
argument_list|>
block|{
DECL|field|numFound
specifier|private
name|long
name|numFound
init|=
literal|0
decl_stmt|;
DECL|field|start
specifier|private
name|long
name|start
init|=
literal|0
decl_stmt|;
DECL|field|maxScore
specifier|private
name|Float
name|maxScore
init|=
literal|null
decl_stmt|;
DECL|method|getMaxScore
specifier|public
name|Float
name|getMaxScore
parameter_list|()
block|{
return|return
name|maxScore
return|;
block|}
DECL|method|setMaxScore
specifier|public
name|void
name|setMaxScore
parameter_list|(
name|Float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
DECL|method|getNumFound
specifier|public
name|long
name|getNumFound
parameter_list|()
block|{
return|return
name|numFound
return|;
block|}
DECL|method|setNumFound
specifier|public
name|void
name|setNumFound
parameter_list|(
name|long
name|numFound
parameter_list|)
block|{
name|this
operator|.
name|numFound
operator|=
name|numFound
expr_stmt|;
block|}
DECL|method|getStart
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
DECL|method|setStart
specifier|public
name|void
name|setStart
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{numFound="
operator|+
name|numFound
operator|+
literal|",start="
operator|+
name|start
operator|+
operator|(
name|maxScore
operator|!=
literal|null
condition|?
literal|""
operator|+
name|maxScore
else|:
literal|""
operator|)
operator|+
literal|",docs="
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

