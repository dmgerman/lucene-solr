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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_class
DECL|class|TestStopAnalyzer
specifier|public
class|class
name|TestStopAnalyzer
extends|extends
name|TestCase
block|{
DECL|field|stop
specifier|private
name|StopAnalyzer
name|stop
init|=
operator|new
name|StopAnalyzer
argument_list|()
decl_stmt|;
DECL|field|inValidTokens
specifier|private
name|Set
name|inValidTokens
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
DECL|method|TestStopAnalyzer
specifier|public
name|TestStopAnalyzer
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
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
block|{
name|inValidTokens
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
block|}
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|stop
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"This is a test of the english stop analyzer"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|stop
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Token
name|token
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertFalse
argument_list|(
name|inValidTokens
operator|.
name|contains
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testStopList
specifier|public
name|void
name|testStopList
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
name|stopWordsSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|stopWordsSet
operator|.
name|add
argument_list|(
literal|"good"
argument_list|)
expr_stmt|;
name|stopWordsSet
operator|.
name|add
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|stopWordsSet
operator|.
name|add
argument_list|(
literal|"analyzer"
argument_list|)
expr_stmt|;
name|StopAnalyzer
name|newStop
init|=
operator|new
name|StopAnalyzer
argument_list|(
operator|(
name|String
index|[]
operator|)
name|stopWordsSet
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|3
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"This is a good test of the english stop analyzer"
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|newStop
operator|.
name|tokenStream
argument_list|(
literal|"test"
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|Token
name|token
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|text
init|=
name|token
operator|.
name|termText
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|stopWordsSet
operator|.
name|contains
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

