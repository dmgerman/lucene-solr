begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_class
DECL|class|TestTrackingDirectoryWrapper
specifier|public
class|class
name|TestTrackingDirectoryWrapper
extends|extends
name|BaseDirectoryTestCase
block|{
annotation|@
name|Override
DECL|method|getDirectory
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
return|;
block|}
DECL|method|testTrackEmpty
specifier|public
name|void
name|testTrackEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|TrackingDirectoryWrapper
name|dir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|dir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrackCreate
specifier|public
name|void
name|testTrackCreate
parameter_list|()
throws|throws
name|IOException
block|{
name|TrackingDirectoryWrapper
name|dir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|dir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrackDelete
specifier|public
name|void
name|testTrackDelete
parameter_list|()
throws|throws
name|IOException
block|{
name|TrackingDirectoryWrapper
name|dir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|dir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|dir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrackRename
specifier|public
name|void
name|testTrackRename
parameter_list|()
throws|throws
name|IOException
block|{
name|TrackingDirectoryWrapper
name|dir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|dir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|rename
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
name|dir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrackCopyFrom
specifier|public
name|void
name|testTrackCopyFrom
parameter_list|()
throws|throws
name|IOException
block|{
name|TrackingDirectoryWrapper
name|source
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|TrackingDirectoryWrapper
name|dest
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|source
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|source
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|dest
operator|.
name|copyFrom
argument_list|(
name|source
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
name|dest
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asSet
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|source
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

