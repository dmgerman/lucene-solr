begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|// TODO: make inner?
end_comment

begin_comment
comment|// TODO: store the highest possible in the index on a commit (but how to not block adds?)
end_comment

begin_comment
comment|// TODO: could also store highest possible in the transaction log after a commit.
end_comment

begin_comment
comment|// Or on a new index, just scan "version" for the max?
end_comment

begin_comment
comment|/** @lucene.internal */
end_comment

begin_class
DECL|class|VersionBucket
specifier|public
class|class
name|VersionBucket
block|{
DECL|field|highest
specifier|public
name|long
name|highest
decl_stmt|;
DECL|method|updateHighest
specifier|public
name|void
name|updateHighest
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|highest
operator|!=
literal|0
condition|)
block|{
name|highest
operator|=
name|Math
operator|.
name|max
argument_list|(
name|highest
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

