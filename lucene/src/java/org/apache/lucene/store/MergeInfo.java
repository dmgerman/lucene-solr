begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *<p>A MergeInfo provides information required for a MERGE context and other optimization operations.  *  It is used as part of an {@link IOContext} in case of MERGE context.</p>  */
end_comment

begin_class
DECL|class|MergeInfo
specifier|public
class|class
name|MergeInfo
block|{
DECL|field|totalDocCount
specifier|public
name|int
name|totalDocCount
decl_stmt|;
DECL|field|estimatedMergeBytes
specifier|public
name|long
name|estimatedMergeBytes
decl_stmt|;
comment|// used by IndexWriter
DECL|field|isExternal
name|boolean
name|isExternal
decl_stmt|;
comment|// used by IndexWriter
DECL|field|optimize
name|boolean
name|optimize
decl_stmt|;
comment|// used by IndexWriter
comment|/**    *<p>Creates a new {@link MergeInfo} instance from    * the values required for a MERGE {@link IOContext} context.    *     * These values are only estimates and are not the actual values.    *     */
DECL|method|MergeInfo
specifier|public
name|MergeInfo
parameter_list|(
name|int
name|totalDocCount
parameter_list|,
name|long
name|estimatedMergeBytes
parameter_list|,
name|boolean
name|isExternal
parameter_list|,
name|boolean
name|optimize
parameter_list|)
block|{
name|this
operator|.
name|totalDocCount
operator|=
name|totalDocCount
expr_stmt|;
name|this
operator|.
name|estimatedMergeBytes
operator|=
name|estimatedMergeBytes
expr_stmt|;
name|this
operator|.
name|isExternal
operator|=
name|isExternal
expr_stmt|;
name|this
operator|.
name|optimize
operator|=
name|optimize
expr_stmt|;
block|}
block|}
end_class

end_unit

