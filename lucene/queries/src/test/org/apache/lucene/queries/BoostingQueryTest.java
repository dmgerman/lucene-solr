begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|MultiReader
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
name|Term
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|search
operator|.
name|MatchNoDocsQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|QueryUtils
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
name|search
operator|.
name|TermQuery
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

begin_class
DECL|class|BoostingQueryTest
specifier|public
class|class
name|BoostingQueryTest
extends|extends
name|LuceneTestCase
block|{
comment|// TODO: this suite desperately needs more tests!
comment|// ... like ones that actually run the query
DECL|method|testBoostingQueryEquals
specifier|public
name|void
name|testBoostingQueryEquals
parameter_list|()
block|{
name|TermQuery
name|q1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"subject:"
argument_list|,
literal|"java"
argument_list|)
argument_list|)
decl_stmt|;
name|TermQuery
name|q2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"subject:"
argument_list|,
literal|"java"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Two TermQueries with same attributes should be equal"
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|BoostingQuery
name|bq1
init|=
operator|new
name|BoostingQuery
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
literal|0.1f
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|bq1
argument_list|)
expr_stmt|;
name|BoostingQuery
name|bq2
init|=
operator|new
name|BoostingQuery
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
literal|0.1f
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BoostingQuery with same attributes is not equal"
argument_list|,
name|bq1
argument_list|,
name|bq2
argument_list|)
expr_stmt|;
block|}
DECL|method|testRewrite
specifier|public
name|void
name|testRewrite
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
operator|new
name|MultiReader
argument_list|()
decl_stmt|;
name|BoostingQuery
name|q
init|=
operator|new
name|BoostingQuery
argument_list|(
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Query
name|rewritten
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|Query
name|expectedRewritten
init|=
operator|new
name|BoostingQuery
argument_list|(
operator|new
name|MatchNoDocsQuery
argument_list|()
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedRewritten
argument_list|,
name|rewritten
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|rewritten
argument_list|,
name|rewritten
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

