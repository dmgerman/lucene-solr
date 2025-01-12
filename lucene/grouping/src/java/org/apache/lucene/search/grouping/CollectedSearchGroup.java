begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
name|FieldComparator
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**   * Expert: representation of a group in {@link FirstPassGroupingCollector},  * tracking the top doc and {@link FieldComparator} slot.  * @lucene.internal */
end_comment

begin_class
DECL|class|CollectedSearchGroup
specifier|public
class|class
name|CollectedSearchGroup
parameter_list|<
name|T
parameter_list|>
extends|extends
name|SearchGroup
argument_list|<
name|T
argument_list|>
block|{
DECL|field|topDoc
name|int
name|topDoc
decl_stmt|;
DECL|field|comparatorSlot
name|int
name|comparatorSlot
decl_stmt|;
block|}
end_class

end_unit

