begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * MergeTrigger is passed to  * {@link org.apache.lucene.index.MergePolicy#findMerges(MergeTrigger, org.apache.lucene.index.SegmentInfos, IndexWriter)} to indicate the  * event that triggered the merge.  */
end_comment

begin_enum
DECL|enum|MergeTrigger
specifier|public
enum|enum
name|MergeTrigger
block|{
comment|/**    * Merge was triggered by a segment flush.    */
DECL|enum constant|SEGMENT_FLUSH
name|SEGMENT_FLUSH
block|,
comment|/**    * Merge was triggered by a full flush. Full flushes    * can be caused by a commit, NRT reader reopen or a close call on the index writer.    */
DECL|enum constant|FULL_FLUSH
name|FULL_FLUSH
block|,
comment|/**    * Merge has been triggered explicitly by the user.    */
DECL|enum constant|EXPLICIT
name|EXPLICIT
block|,
comment|/**    * Merge was triggered by a successfully finished merge.    */
DECL|enum constant|MERGE_FINISHED
name|MERGE_FINISHED
block|,
comment|/**    * Merge was triggered by a closing IndexWriter.    */
DECL|enum constant|CLOSING
name|CLOSING
block|}
end_enum

end_unit

