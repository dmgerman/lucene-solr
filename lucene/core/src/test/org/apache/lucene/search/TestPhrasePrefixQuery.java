begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LinkedList
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|MultiFields
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
name|RandomIndexWriter
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
name|index
operator|.
name|TermsEnum
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
name|store
operator|.
name|Directory
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
name|BytesRef
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

begin_comment
comment|/**  * This class tests PhrasePrefixQuery class.  */
end_comment

begin_class
DECL|class|TestPhrasePrefixQuery
specifier|public
class|class
name|TestPhrasePrefixQuery
extends|extends
name|LuceneTestCase
block|{
comment|/**      *      */
DECL|method|testPhrasePrefix
specifier|public
name|void
name|testPhrasePrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|indexStore
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexStore
argument_list|)
decl_stmt|;
name|Document
name|doc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|doc5
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc1
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry pie"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc2
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry strudel"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc3
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry pizza"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc4
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry chewing gum"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc5
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"body"
argument_list|,
literal|"piccadilly circus"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc4
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc5
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// PhrasePrefixQuery query1 = new PhrasePrefixQuery();
name|MultiPhraseQuery
operator|.
name|Builder
name|query1builder
init|=
operator|new
name|MultiPhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// PhrasePrefixQuery query2 = new PhrasePrefixQuery();
name|MultiPhraseQuery
operator|.
name|Builder
name|query2builder
init|=
operator|new
name|MultiPhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query1builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"blueberry"
argument_list|)
argument_list|)
expr_stmt|;
name|query2builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"strawberry"
argument_list|)
argument_list|)
expr_stmt|;
name|LinkedList
argument_list|<
name|Term
argument_list|>
name|termsWithPrefix
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// this TermEnum gives "piccadilly", "pie" and "pizza".
name|String
name|prefix
init|=
literal|"pi"
decl_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|te
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
do|do
block|{
name|String
name|s
init|=
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|termsWithPrefix
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
do|;
name|query1builder
operator|.
name|add
argument_list|(
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|query2builder
operator|.
name|add
argument_list|(
name|termsWithPrefix
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|result
decl_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1builder
operator|.
name|build
argument_list|()
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2builder
operator|.
name|build
argument_list|()
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

