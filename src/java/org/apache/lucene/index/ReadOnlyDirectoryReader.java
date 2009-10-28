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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

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
name|Map
import|;
end_import

begin_class
DECL|class|ReadOnlyDirectoryReader
class|class
name|ReadOnlyDirectoryReader
extends|extends
name|DirectoryReader
block|{
DECL|method|ReadOnlyDirectoryReader
name|ReadOnlyDirectoryReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|sis
parameter_list|,
name|IndexDeletionPolicy
name|deletionPolicy
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|sis
argument_list|,
name|deletionPolicy
argument_list|,
literal|true
argument_list|,
name|termInfosIndexDivisor
argument_list|)
expr_stmt|;
block|}
DECL|method|ReadOnlyDirectoryReader
name|ReadOnlyDirectoryReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|SegmentReader
index|[]
name|oldReaders
parameter_list|,
name|int
index|[]
name|oldStarts
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|oldNormsCache
parameter_list|,
name|boolean
name|doClone
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|infos
argument_list|,
name|oldReaders
argument_list|,
name|oldStarts
argument_list|,
name|oldNormsCache
argument_list|,
literal|true
argument_list|,
name|doClone
argument_list|,
name|termInfosIndexDivisor
argument_list|)
expr_stmt|;
block|}
DECL|method|ReadOnlyDirectoryReader
name|ReadOnlyDirectoryReader
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|SegmentInfos
name|infos
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|infos
argument_list|,
name|termInfosIndexDivisor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acquireWriteLock
specifier|protected
name|void
name|acquireWriteLock
parameter_list|()
block|{
name|ReadOnlySegmentReader
operator|.
name|noWrite
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

