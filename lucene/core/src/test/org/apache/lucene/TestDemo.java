begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|analysis
operator|.
name|Analyzer
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
name|DirectoryReader
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
name|search
operator|.
name|*
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
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * A very simple demo used in the API documentation (src/java/overview.html).  *  * Please try to keep src/java/overview.html up-to-date when making changes  * to this class.  */
end_comment

begin_class
DECL|class|TestDemo
specifier|public
class|class
name|TestDemo
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDemo
specifier|public
name|void
name|testDemo
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
comment|// Store the index in memory:
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// To store an index on disk, use this instead:
comment|// Directory directory = FSDirectory.open(new File("/tmp/testindex"));
name|RandomIndexWriter
name|iwriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|longTerm
init|=
literal|"longtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongtermlongterm"
decl_stmt|;
name|String
name|text
init|=
literal|"This is the text to be indexed. "
operator|+
name|longTerm
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"fieldname"
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iwriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Now search the index:
name|IndexReader
name|ireader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// read-only=true
name|IndexSearcher
name|isearcher
init|=
name|newSearcher
argument_list|(
name|ireader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|isearcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"fieldname"
argument_list|,
name|longTerm
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"fieldname"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|isearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Iterate through the results:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|hitDoc
init|=
name|isearcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|text
argument_list|,
name|hitDoc
operator|.
name|get
argument_list|(
literal|"fieldname"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Test simple phrase query
name|PhraseQuery
name|phraseQuery
init|=
operator|new
name|PhraseQuery
argument_list|(
literal|"fieldname"
argument_list|,
literal|"to"
argument_list|,
literal|"be"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|isearcher
operator|.
name|search
argument_list|(
name|phraseQuery
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ireader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

