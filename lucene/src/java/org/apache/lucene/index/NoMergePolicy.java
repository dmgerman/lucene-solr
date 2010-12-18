begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A {@link MergePolicy} which never returns merges to execute (hence it's  * name). It is also a singleton and can be accessed through  * {@link NoMergePolicy#NO_COMPOUND_FILES} if you want to indicate the index  * does not use compound files, or through {@link NoMergePolicy#COMPOUND_FILES}  * otherwise. Use it if you want to prevent an {@link IndexWriter} from ever  * executing merges, without going through the hassle of tweaking a merge  * policy's settings to achieve that, such as changing its merge factor.  */
end_comment

begin_class
DECL|class|NoMergePolicy
specifier|public
specifier|final
class|class
name|NoMergePolicy
extends|extends
name|MergePolicy
block|{
comment|/**    * A singleton {@link NoMergePolicy} which indicates the index does not use    * compound files.    */
DECL|field|NO_COMPOUND_FILES
specifier|public
specifier|static
specifier|final
name|MergePolicy
name|NO_COMPOUND_FILES
init|=
operator|new
name|NoMergePolicy
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**    * A singleton {@link NoMergePolicy} which indicates the index uses compound    * files.    */
DECL|field|COMPOUND_FILES
specifier|public
specifier|static
specifier|final
name|MergePolicy
name|COMPOUND_FILES
init|=
operator|new
name|NoMergePolicy
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|useCompoundFile
specifier|private
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|method|NoMergePolicy
specifier|private
name|NoMergePolicy
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
comment|// prevent instantiation
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|findMergesForOptimize
specifier|public
name|MergeSpecification
name|findMergesForOptimize
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Set
argument_list|<
name|SegmentInfo
argument_list|>
name|segmentsToOptimize
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|findMergesToExpungeDeletes
specifier|public
name|MergeSpecification
name|findMergesToExpungeDeletes
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|useCompoundFile
specifier|public
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|segments
parameter_list|,
name|SegmentInfo
name|newSegment
parameter_list|)
block|{
return|return
name|useCompoundFile
return|;
block|}
annotation|@
name|Override
DECL|method|setIndexWriter
specifier|public
name|void
name|setIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NoMergePolicy"
return|;
block|}
block|}
end_class

end_unit

