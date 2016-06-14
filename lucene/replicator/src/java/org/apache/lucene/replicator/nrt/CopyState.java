begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SegmentInfos
import|;
end_import

begin_comment
comment|/** Holds incRef'd file level details for one point-in-time segment infos on the primary node.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|CopyState
specifier|public
class|class
name|CopyState
block|{
DECL|field|files
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
decl_stmt|;
DECL|field|version
specifier|public
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|gen
specifier|public
specifier|final
name|long
name|gen
decl_stmt|;
DECL|field|infosBytes
specifier|public
specifier|final
name|byte
index|[]
name|infosBytes
decl_stmt|;
DECL|field|completedMergeFiles
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|completedMergeFiles
decl_stmt|;
DECL|field|primaryGen
specifier|public
specifier|final
name|long
name|primaryGen
decl_stmt|;
comment|// only non-null on the primary node
DECL|field|infos
specifier|public
specifier|final
name|SegmentInfos
name|infos
decl_stmt|;
DECL|method|CopyState
specifier|public
name|CopyState
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
parameter_list|,
name|long
name|version
parameter_list|,
name|long
name|gen
parameter_list|,
name|byte
index|[]
name|infosBytes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|completedMergeFiles
parameter_list|,
name|long
name|primaryGen
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|)
block|{
assert|assert
name|completedMergeFiles
operator|!=
literal|null
assert|;
name|this
operator|.
name|files
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|gen
operator|=
name|gen
expr_stmt|;
name|this
operator|.
name|infosBytes
operator|=
name|infosBytes
expr_stmt|;
name|this
operator|.
name|completedMergeFiles
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|completedMergeFiles
argument_list|)
expr_stmt|;
name|this
operator|.
name|primaryGen
operator|=
name|primaryGen
expr_stmt|;
name|this
operator|.
name|infos
operator|=
name|infos
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(version="
operator|+
name|version
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

