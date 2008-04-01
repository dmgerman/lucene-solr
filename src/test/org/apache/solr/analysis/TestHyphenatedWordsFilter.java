begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

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
name|TokenStream
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
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/**  * HyphenatedWordsFilter test  */
end_comment

begin_class
DECL|class|TestHyphenatedWordsFilter
specifier|public
class|class
name|TestHyphenatedWordsFilter
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testHyphenatedWords
specifier|public
name|void
name|testHyphenatedWords
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|input
init|=
literal|"ecologi-\r\ncal devel-\r\n\r\nop compre-\u0009hensive-hands-on and ecologi-\ncal"
decl_stmt|;
name|String
name|outputAfterHyphenatedWordsFilter
init|=
literal|"ecological develop comprehensive-hands-on and ecological"
decl_stmt|;
comment|// first test
name|TokenStream
name|ts
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|ts
operator|=
operator|new
name|HyphenatedWordsFilter
argument_list|(
name|ts
argument_list|)
expr_stmt|;
name|String
name|actual
init|=
name|tsToString
argument_list|(
name|ts
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Testing HyphenatedWordsFilter"
argument_list|,
name|outputAfterHyphenatedWordsFilter
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

