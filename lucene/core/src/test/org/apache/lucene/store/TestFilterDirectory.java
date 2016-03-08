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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|HashSet
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestFilterDirectory
specifier|public
class|class
name|TestFilterDirectory
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
name|FilterDirectory
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
block|{}
return|;
block|}
annotation|@
name|Test
DECL|method|testOverrides
specifier|public
name|void
name|testOverrides
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verify that all methods of Directory are overridden by FilterDirectory,
comment|// except those under the 'exclude' list
name|Set
argument_list|<
name|Method
argument_list|>
name|exclude
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|exclude
operator|.
name|add
argument_list|(
name|Directory
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"copyFrom"
argument_list|,
name|Directory
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|IOContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|exclude
operator|.
name|add
argument_list|(
name|Directory
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"openChecksumInput"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|IOContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|FilterDirectory
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|Directory
operator|.
name|class
condition|)
block|{
name|assertTrue
argument_list|(
literal|"method "
operator|+
name|m
operator|.
name|getName
argument_list|()
operator|+
literal|" not overridden!"
argument_list|,
name|exclude
operator|.
name|contains
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testUnwrap
specifier|public
name|void
name|testUnwrap
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FilterDirectory
name|dir2
init|=
operator|new
name|FilterDirectory
argument_list|(
name|dir
argument_list|)
block|{}
decl_stmt|;
name|assertEquals
argument_list|(
name|dir
argument_list|,
name|dir2
operator|.
name|getDelegate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
argument_list|,
name|FilterDirectory
operator|.
name|unwrap
argument_list|(
name|dir2
argument_list|)
argument_list|)
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

