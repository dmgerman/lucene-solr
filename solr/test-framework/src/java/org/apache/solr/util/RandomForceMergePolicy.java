begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|index
operator|.
name|ForceMergePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MergePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * A {@link MergePolicy} with a no-arg constructor that proxies to a  * {@link ForceMergePolicy} wrapped instance retrieved from  * {@link LuceneTestCase#newMergePolicy}.  * Solr tests utilizing the Lucene randomized test framework can refer   * to this class in solrconfig.xml to get a fully randomized merge policy  * that only returns forced merges.  */
end_comment

begin_class
DECL|class|RandomForceMergePolicy
specifier|public
specifier|final
class|class
name|RandomForceMergePolicy
extends|extends
name|RandomMergePolicy
block|{
DECL|method|RandomForceMergePolicy
specifier|public
name|RandomForceMergePolicy
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|ForceMergePolicy
argument_list|(
name|LuceneTestCase
operator|.
name|newMergePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

