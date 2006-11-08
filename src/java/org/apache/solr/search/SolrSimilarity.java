begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|search
operator|.
name|DefaultSimilarity
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * @author yonik  */
end_comment

begin_comment
comment|// don't make it public for now... easier to change later.
end_comment

begin_comment
comment|// This class is currently unused.
end_comment

begin_class
DECL|class|SolrSimilarity
class|class
name|SolrSimilarity
extends|extends
name|DefaultSimilarity
block|{
DECL|field|lengthNormConfig
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|lengthNormConfig
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
comment|// Float f = lengthNormConfig.
comment|// if (lengthNormDisabled.)
return|return
name|super
operator|.
name|lengthNorm
argument_list|(
name|fieldName
argument_list|,
name|numTerms
argument_list|)
return|;
block|}
block|}
end_class

end_unit

