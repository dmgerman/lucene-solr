begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|MockAnalyzer
import|;
end_import

begin_class
DECL|class|TestQueryTermVector
specifier|public
class|class
name|TestQueryTermVector
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestQueryTermVector
specifier|public
name|TestQueryTermVector
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
DECL|method|testConstructor
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
name|String
index|[]
name|queryTerm
init|=
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"again"
block|,
literal|"foo"
block|,
literal|"bar"
block|,
literal|"go"
block|,
literal|"go"
block|,
literal|"go"
block|}
decl_stmt|;
comment|//Items are sorted lexicographically
name|String
index|[]
name|gold
init|=
block|{
literal|"again"
block|,
literal|"bar"
block|,
literal|"foo"
block|,
literal|"go"
block|}
decl_stmt|;
name|int
index|[]
name|goldFreqs
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|3
block|}
decl_stmt|;
name|QueryTermVector
name|result
init|=
operator|new
name|QueryTermVector
argument_list|(
name|queryTerm
argument_list|)
decl_stmt|;
name|String
index|[]
name|terms
init|=
name|result
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
literal|4
argument_list|)
expr_stmt|;
name|int
index|[]
name|freq
init|=
name|result
operator|.
name|getTermFrequencies
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|.
name|length
operator|==
literal|4
argument_list|)
expr_stmt|;
name|checkGold
argument_list|(
name|terms
argument_list|,
name|gold
argument_list|,
name|freq
argument_list|,
name|goldFreqs
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|QueryTermVector
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getTerms
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|QueryTermVector
argument_list|(
literal|"foo bar foo again foo bar go go go"
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|terms
operator|=
name|result
operator|.
name|getTerms
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
literal|4
argument_list|)
expr_stmt|;
name|freq
operator|=
name|result
operator|.
name|getTermFrequencies
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|.
name|length
operator|==
literal|4
argument_list|)
expr_stmt|;
name|checkGold
argument_list|(
name|terms
argument_list|,
name|gold
argument_list|,
name|freq
argument_list|,
name|goldFreqs
argument_list|)
expr_stmt|;
block|}
DECL|method|checkGold
specifier|private
name|void
name|checkGold
parameter_list|(
name|String
index|[]
name|terms
parameter_list|,
name|String
index|[]
name|gold
parameter_list|,
name|int
index|[]
name|freq
parameter_list|,
name|int
index|[]
name|goldFreqs
parameter_list|)
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|terms
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|gold
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|freq
index|[
name|i
index|]
operator|==
name|goldFreqs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

