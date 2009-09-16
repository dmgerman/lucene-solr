begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

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
name|Token
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
name|WhitespaceAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Test for SpellingQueryConverter  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SpellingQueryConverterTest
specifier|public
class|class
name|SpellingQueryConverterTest
block|{
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|SpellingQueryConverter
name|converter
init|=
operator|new
name|SpellingQueryConverter
argument_list|()
decl_stmt|;
name|converter
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|converter
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|converter
operator|.
name|convert
argument_list|(
literal|"field:foo"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpecialChars
specifier|public
name|void
name|testSpecialChars
parameter_list|()
block|{
name|SpellingQueryConverter
name|converter
init|=
operator|new
name|SpellingQueryConverter
argument_list|()
decl_stmt|;
name|converter
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|converter
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|converter
operator|.
name|convert
argument_list|(
literal|"field_with_underscore:value_with_underscore"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|converter
operator|.
name|convert
argument_list|(
literal|"field_with_digits123:value_with_digits123"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|converter
operator|.
name|convert
argument_list|(
literal|"field-with-hyphens:value-with-hyphens"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// mix 'em up and add some to the value
name|tokens
operator|=
name|converter
operator|.
name|convert
argument_list|(
literal|"field_with-123s:value_,.|with-hyphens"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnicode
specifier|public
name|void
name|testUnicode
parameter_list|()
block|{
name|SpellingQueryConverter
name|converter
init|=
operator|new
name|SpellingQueryConverter
argument_list|()
decl_stmt|;
name|converter
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|converter
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
comment|// chinese text value
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|converter
operator|.
name|convert
argument_list|(
literal|"text_field:æè´­ä¹°äºéå·åæè£ã"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|converter
operator|.
name|convert
argument_list|(
literal|"text_è´­field:æè´­ä¹°äºéå·åæè£ã"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|converter
operator|.
name|convert
argument_list|(
literal|"text_field:æè´­xyzä¹°äºéå·åæè£ã"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 1"
argument_list|,
literal|1
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleClauses
specifier|public
name|void
name|testMultipleClauses
parameter_list|()
block|{
name|SpellingQueryConverter
name|converter
init|=
operator|new
name|SpellingQueryConverter
argument_list|()
decl_stmt|;
name|converter
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|converter
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
comment|// two field:value pairs should give two tokens
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|converter
operator|.
name|convert
argument_list|(
literal|"ä¹°text_field:æè´­ä¹°äºéå·åæè£ã field2:bar"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 2"
argument_list|,
literal|2
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// a field:value pair and a search term should give two tokens
name|tokens
operator|=
name|converter
operator|.
name|convert
argument_list|(
literal|"text_field:æè´­ä¹°äºéå·åæè£ã bar"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tokens is null and it shouldn't be"
argument_list|,
name|tokens
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"tokens Size: "
operator|+
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|" is not 2"
argument_list|,
literal|2
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

