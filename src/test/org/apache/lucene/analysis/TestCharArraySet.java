begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|StopAnalyzer
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
name|CharArraySet
import|;
end_import

begin_class
DECL|class|TestCharArraySet
specifier|public
class|class
name|TestCharArraySet
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRehash
specifier|public
name|void
name|testRehash
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|cas
init|=
operator|new
name|CharArraySet
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|cas
operator|.
name|add
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
operator|.
name|length
argument_list|,
name|cas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|assertTrue
argument_list|(
name|cas
operator|.
name|contains
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

