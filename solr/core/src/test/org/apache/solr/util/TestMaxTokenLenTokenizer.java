begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Tests for:  * {@link org.apache.lucene.analysis.core.LowerCaseTokenizerFactory}  * {@link org.apache.lucene.analysis.core.LetterTokenizerFactory}  * {@link org.apache.lucene.analysis.core.KeywordTokenizerFactory}  * {@link org.apache.lucene.analysis.core.WhitespaceTokenizerFactory}  */
end_comment

begin_class
DECL|class|TestMaxTokenLenTokenizer
specifier|public
class|class
name|TestMaxTokenLenTokenizer
extends|extends
name|SolrTestCaseJ4
block|{
comment|/* field names are used in accordance with the solrconfig and schema supplied */
DECL|field|ID
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-update-processor-chains.xml"
argument_list|,
literal|"schema-tokenizer-test.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleFieldDiffAnalyzers
specifier|public
name|void
name|testSingleFieldDiffAnalyzers
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
comment|// using fields with definitions, different tokenizer factories respectively at index time and standard tokenizer at query time.
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":1,\"letter\":\"letter\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":2,\"lowerCase\":\"lowerCase\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":3,\"whiteSpace\":\"whiteSpace in\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":4,\"unicodeWhiteSpace\":\"unicode in\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":5,\"keyword\":\"keyword\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound=5]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "letter": "let" "ter" "letter" , maxTokenLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"letter:let"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"letter:lett"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "lowerCase": "low" "erC" "ase" "lowerCase" , maxTokenLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase:low"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase:l"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase:lo"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase:lower"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "whiteSpace in": "whi" "teS" "pac" "e" "in" "whiteSpace" , maxTokenLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace:whi"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace:teS"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace:in"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace:white"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "unicode in": "uni" "cod" "e" "in" "unicode" , maxTokenLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace:uni"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace:cod"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace:e"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace:unico"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "keyword": "keyword" , maxTokenLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"keyword:keyword"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"keyword:key"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleFieldSameAnalyzers
specifier|public
name|void
name|testSingleFieldSameAnalyzers
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
comment|// using fields with definitions, same tokenizers both at index and query time.
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":1,\"letter0\":\"letter\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":2,\"lowerCase0\":\"lowerCase\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":3,\"whiteSpace0\":\"whiteSpace in\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":4,\"unicodeWhiteSpace0\":\"unicode in\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|"{\"add\":{\"doc\": {\"id\":5,\"keyword0\":\"keyword\"}},\"commit\":{}}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound=5]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "letter": "let" "ter" "letter" , maxTokenLen=3
comment|// Anything that matches the first three letters should be found when maxLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"letter0:l"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"letter0:let"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"letter0:lett"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"letter0:letXYZ"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "lowerCase": "low" "erC" "ase" "lowerCase" , maxTokenLen=3
comment|// Anything that matches the first three letters should be found when maxLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase0:low"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase0:l"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase0:lo"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerCase0:lowerXYZ"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "whiteSpace in": "whi" "teS" "pac" "e" "in" "whiteSpace" , maxTokenLen=3
comment|// Anything that matches the first three letters should be found when maxLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace0:h"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace0:whi"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace0:teS"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace0:in"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"whiteSpace0:whiteZKY"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "unicode in": "uni" "cod" "e" "in" "unicode" , maxTokenLen=3
comment|// Anything that matches the first three letters should be found when maxLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace0:u"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace0:uni"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace0:cod"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace0:e"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"unicodeWhiteSpace0:unicoVBRT"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|//Tokens generated for "keyword": "keyword" , maxTokenLen=3
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"keyword0:keyword"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Check the total number of docs"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"keyword0:key"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

