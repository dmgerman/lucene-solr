begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
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
name|common
operator|.
name|SolrDocument
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  * Simple Augmenter that adds the score  *  *  * @since solr 4.0  */
end_comment

begin_class
DECL|class|ScoreAugmenter
specifier|public
class|class
name|ScoreAugmenter
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|ScoreAugmenter
specifier|public
name|ScoreAugmenter
parameter_list|(
name|String
name|display
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|display
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
if|if
condition|(
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|wantsScores
argument_list|()
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getDocIterator
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|context
operator|.
name|getDocIterator
argument_list|()
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

