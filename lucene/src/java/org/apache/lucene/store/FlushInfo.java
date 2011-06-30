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
comment|/**  *<p>A FlushInfo provides information required for a FLUSH context and other optimization operations.  *  It is used as part of an {@link IOContext} in case of FLUSH context.</p>  */
end_comment

begin_class
DECL|class|FlushInfo
specifier|public
class|class
name|FlushInfo
block|{
DECL|field|numDocs
specifier|public
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|estimatedSegmentSize
specifier|public
specifier|final
name|long
name|estimatedSegmentSize
decl_stmt|;
comment|/**    *<p>Creates a new {@link FlushInfo} instance from    * the values required for a FLUSH {@link IOContext} context.    *     * These values are only estimates and are not the actual values.    *     */
DECL|method|FlushInfo
specifier|public
name|FlushInfo
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|long
name|estimatedSegmentSize
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|estimatedSegmentSize
operator|=
name|estimatedSegmentSize
expr_stmt|;
block|}
block|}
end_class

end_unit

