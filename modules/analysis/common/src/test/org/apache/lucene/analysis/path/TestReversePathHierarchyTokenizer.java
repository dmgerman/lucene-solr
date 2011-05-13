begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.path
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|path
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
name|StringReader
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
name|analysis
operator|.
name|BaseTokenStreamTestCase
import|;
end_import

begin_class
DECL|class|TestReversePathHierarchyTokenizer
specifier|public
class|class
name|TestReversePathHierarchyTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testBasicReverse
specifier|public
name|void
name|testBasicReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b/c"
block|,
literal|"a/b/c"
block|,
literal|"b/c"
block|,
literal|"c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEndOfDelimiterReverse
specifier|public
name|void
name|testEndOfDelimiterReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b/c/"
block|,
literal|"a/b/c/"
block|,
literal|"b/c/"
block|,
literal|"c/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|7
block|,
literal|7
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharReverse
specifier|public
name|void
name|testStartOfCharReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a/b/c"
block|,
literal|"b/c"
block|,
literal|"c"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharEndOfDelimiterReverse
specifier|public
name|void
name|testStartOfCharEndOfDelimiterReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a/b/c/"
block|,
literal|"b/c/"
block|,
literal|"c/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|6
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimiterReverse
specifier|public
name|void
name|testOnlyDelimiterReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimitersReverse
specifier|public
name|void
name|testOnlyDelimitersReverse
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"//"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"//"
block|,
literal|"/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEndOfDelimiterReverseSkip
specifier|public
name|void
name|testEndOfDelimiterReverseSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b/"
block|,
literal|"a/b/"
block|,
literal|"b/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharReverseSkip
specifier|public
name|void
name|testStartOfCharReverseSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a/b/"
block|,
literal|"b/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStartOfCharEndOfDelimiterReverseSkip
specifier|public
name|void
name|testStartOfCharEndOfDelimiterReverseSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"a/b/c/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a/b/"
block|,
literal|"b/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimiterReverseSkip
specifier|public
name|void
name|testOnlyDelimiterReverseSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyDelimitersReverseSkip
specifier|public
name|void
name|testOnlyDelimitersReverseSkip
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"//"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseSkip2
specifier|public
name|void
name|testReverseSkip2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"/a/b/c/"
decl_stmt|;
name|ReversePathHierarchyTokenizer
name|t
init|=
operator|new
name|ReversePathHierarchyTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|path
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/"
block|,
literal|"a/"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|}
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

