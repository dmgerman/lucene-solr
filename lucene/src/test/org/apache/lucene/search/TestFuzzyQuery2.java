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
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|analysis
operator|.
name|MockTokenizer
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
comment|/**   * Tests the results of fuzzy against pre-recorded output   * The format of the file is the following:  *   * Header Row: # of bits: generate 2^n sequential documents   * with a value of Integer.toBinaryString  *   * Entries: an entry is a param spec line, a resultCount line, and  * then 'resultCount' results lines. The results lines are in the  * expected order.  *   * param spec line: a comma-separated list of params to FuzzyQuery  *   (query, prefixLen, pqSize, minScore)  * query = query text as a number (expand with Integer.toBinaryString)  * prefixLen = prefix length  * pqSize = priority queue maximum size for TopTermsBoostOnlyBooleanQueryRewrite  * minScore = minimum similarity  *   * resultCount line: total number of expected hits.  *   * results line: comma-separated docID, score pair  **/
end_comment

begin_class
DECL|class|TestFuzzyQuery2
specifier|public
class|class
name|TestFuzzyQuery2
extends|extends
name|LuceneTestCase
block|{
comment|/** epsilon for score comparisons */
DECL|field|epsilon
specifier|static
specifier|final
name|float
name|epsilon
init|=
literal|0.00001f
decl_stmt|;
DECL|field|mappings
specifier|static
name|int
index|[]
index|[]
name|mappings
init|=
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|0x40
block|,
literal|0x41
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x40
block|,
literal|0x0195
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x40
block|,
literal|0x0906
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x40
block|,
literal|0x1040F
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x0194
block|,
literal|0x0195
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x0194
block|,
literal|0x0906
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x0194
block|,
literal|0x1040F
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x0905
block|,
literal|0x0906
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x0905
block|,
literal|0x1040F
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0x1040E
block|,
literal|0x1040F
block|}
block|}
decl_stmt|;
DECL|method|testFromTestData
specifier|public
name|void
name|testFromTestData
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: randomize!
name|assertFromTestData
argument_list|(
name|mappings
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|mappings
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFromTestData
specifier|public
name|void
name|assertFromTestData
parameter_list|(
name|int
name|codePointTable
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"fuzzyTestData.txt"
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bits
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|terms
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|bits
argument_list|)
decl_stmt|;
name|Directory
name|dir
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
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
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
name|terms
condition|;
name|i
operator|++
control|)
block|{
name|field
operator|.
name|setValue
argument_list|(
name|mapInt
argument_list|(
name|codePointTable
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|params
index|[]
init|=
name|line
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
name|query
init|=
name|mapInt
argument_list|(
name|codePointTable
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|prefix
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|int
name|pqSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|float
name|minScore
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|params
index|[
literal|3
index|]
argument_list|)
decl_stmt|;
name|FuzzyQuery
name|q
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|query
argument_list|)
argument_list|,
name|minScore
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
name|q
operator|.
name|setRewriteMethod
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsBoostOnlyBooleanQueryRewrite
argument_list|(
name|pqSize
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|expectedResults
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|expectedResults
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedResults
argument_list|,
name|docs
operator|.
name|totalHits
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
name|expectedResults
condition|;
name|i
operator|++
control|)
block|{
name|String
name|scoreDoc
index|[]
init|=
name|reader
operator|.
name|readLine
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|scoreDoc
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|docs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|scoreDoc
index|[
literal|1
index|]
argument_list|)
argument_list|,
name|docs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/* map bits to unicode codepoints */
DECL|method|mapInt
specifier|private
specifier|static
name|String
name|mapInt
parameter_list|(
name|int
name|codePointTable
index|[]
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|binary
init|=
name|Integer
operator|.
name|toBinaryString
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|binary
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
name|sb
operator|.
name|appendCodePoint
argument_list|(
name|codePointTable
index|[
name|binary
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
operator|-
literal|'0'
index|]
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* Code to generate test data   public static void main(String args[]) throws Exception {     int bits = 3;     System.out.println(bits);     int terms = (int) Math.pow(2, bits);          RAMDirectory dir = new RAMDirectory();     IndexWriter writer = new IndexWriter(dir, new KeywordAnalyzer(),         IndexWriter.MaxFieldLength.UNLIMITED);          Document doc = new Document();     Field field = newField("field", "", Field.Store.NO, Field.Index.ANALYZED);     doc.add(field);      for (int i = 0; i< terms; i++) {       field.setValue(Integer.toBinaryString(i));       writer.addDocument(doc);     }          writer.optimize();     writer.close();      IndexSearcher searcher = new IndexSearcher(dir);     for (int prefix = 0; prefix< bits; prefix++)       for (int pqsize = 1; pqsize<= terms; pqsize++)         for (float minscore = 0.1F; minscore< 1F; minscore += 0.2F)           for (int query = 0; query< terms; query++) {             FuzzyQuery q = new FuzzyQuery(                 new Term("field", Integer.toBinaryString(query)), minscore, prefix);             q.setRewriteMethod(new MultiTermQuery.TopTermsBoostOnlyBooleanQueryRewrite(pqsize));             System.out.println(query + "," + prefix + "," + pqsize + "," + minscore);             TopDocs docs = searcher.search(q, terms);             System.out.println(docs.totalHits);             for (int i = 0; i< docs.totalHits; i++)               System.out.println(docs.scoreDocs[i].doc + "," + docs.scoreDocs[i].score);           }   }   */
block|}
end_class

end_unit

