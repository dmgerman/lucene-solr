begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|IndexWriter
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
name|LogByteSizeMergePolicy
import|;
end_import

begin_comment
comment|/**  * Dummy implementation of {@link org.apache.lucene.index.MergePolicy} which doesn't have an empty constructor and  * is expected to fail if used within Solr  */
end_comment

begin_class
DECL|class|DummyMergePolicy
class|class
name|DummyMergePolicy
extends|extends
name|LogByteSizeMergePolicy
block|{
DECL|method|DummyMergePolicy
specifier|private
name|DummyMergePolicy
parameter_list|()
block|{}
DECL|method|DummyMergePolicy
specifier|public
name|DummyMergePolicy
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

