begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|analyzing
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|analysis
operator|.
name|MockBytesAnalyzer
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
name|MockTokenFilter
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
name|analysis
operator|.
name|TokenFilter
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
name|Tokenizer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|document
operator|.
name|FieldType
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
comment|/**  */
end_comment

begin_class
DECL|class|TestAnalyzingQueryParser
specifier|public
class|class
name|TestAnalyzingQueryParser
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD
specifier|private
specifier|final
specifier|static
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
DECL|field|a
specifier|private
name|Analyzer
name|a
decl_stmt|;
DECL|field|wildcardInput
specifier|private
name|String
index|[]
name|wildcardInput
decl_stmt|;
DECL|field|wildcardExpected
specifier|private
name|String
index|[]
name|wildcardExpected
decl_stmt|;
DECL|field|prefixInput
specifier|private
name|String
index|[]
name|prefixInput
decl_stmt|;
DECL|field|prefixExpected
specifier|private
name|String
index|[]
name|prefixExpected
decl_stmt|;
DECL|field|rangeInput
specifier|private
name|String
index|[]
name|rangeInput
decl_stmt|;
DECL|field|rangeExpected
specifier|private
name|String
index|[]
name|rangeExpected
decl_stmt|;
DECL|field|fuzzyInput
specifier|private
name|String
index|[]
name|fuzzyInput
decl_stmt|;
DECL|field|fuzzyExpected
specifier|private
name|String
index|[]
name|fuzzyExpected
decl_stmt|;
DECL|field|wildcardEscapeHits
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|wildcardEscapeHits
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|wildcardEscapeMisses
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|wildcardEscapeMisses
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|wildcardInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"*bersetzung Ã¼ber*ung"
block|,
literal|"MÃ¶tley Cr\u00fce MÃ¶tl?* CrÃ¼?"
block|,
literal|"RenÃ©e Zellweger Ren?? Zellw?ger"
block|}
expr_stmt|;
name|wildcardExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"*bersetzung uber*ung"
block|,
literal|"motley crue motl?* cru?"
block|,
literal|"renee zellweger ren?? zellw?ger"
block|}
expr_stmt|;
name|prefixInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"Ã¼bersetzung Ã¼bersetz*"
block|,
literal|"MÃ¶tley CrÃ¼e MÃ¶tl* crÃ¼*"
block|,
literal|"RenÃ©? Zellw*"
block|}
expr_stmt|;
name|prefixExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"ubersetzung ubersetz*"
block|,
literal|"motley crue motl* cru*"
block|,
literal|"rene? zellw*"
block|}
expr_stmt|;
name|rangeInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"[aa TO bb]"
block|,
literal|"{AnaÃ¯s TO ZoÃ©}"
block|}
expr_stmt|;
name|rangeExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"[aa TO bb]"
block|,
literal|"{anais TO zoe}"
block|}
expr_stmt|;
name|fuzzyInput
operator|=
operator|new
name|String
index|[]
block|{
literal|"Ãbersetzung Ãbersetzung~0.9"
block|,
literal|"MÃ¶tley CrÃ¼e MÃ¶tley~0.75 CrÃ¼e~0.5"
block|,
literal|"RenÃ©e Zellweger RenÃ©e~0.9 Zellweger~"
block|}
expr_stmt|;
name|fuzzyExpected
operator|=
operator|new
name|String
index|[]
block|{
literal|"ubersetzung ubersetzung~1"
block|,
literal|"motley crue motley~1 crue~2"
block|,
literal|"renee zellweger renee~0 zellweger~2"
block|}
expr_stmt|;
name|wildcardEscapeHits
operator|.
name|put
argument_list|(
literal|"mÃ¶*tley"
argument_list|,
literal|"moatley"
argument_list|)
expr_stmt|;
comment|// need to have at least one genuine wildcard to trigger the wildcard analysis
comment|// hence the * before the y
name|wildcardEscapeHits
operator|.
name|put
argument_list|(
literal|"mÃ¶\\*tl*y"
argument_list|,
literal|"mo*tley"
argument_list|)
expr_stmt|;
comment|// escaped backslash then true wildcard
name|wildcardEscapeHits
operator|.
name|put
argument_list|(
literal|"mÃ¶\\\\*tley"
argument_list|,
literal|"mo\\atley"
argument_list|)
expr_stmt|;
comment|// escaped wildcard then true wildcard
name|wildcardEscapeHits
operator|.
name|put
argument_list|(
literal|"mÃ¶\\??ley"
argument_list|,
literal|"mo?tley"
argument_list|)
expr_stmt|;
comment|// the first is an escaped * which should yield a miss
name|wildcardEscapeMisses
operator|.
name|put
argument_list|(
literal|"mÃ¶\\*tl*y"
argument_list|,
literal|"moatley"
argument_list|)
expr_stmt|;
name|a
operator|=
operator|new
name|ASCIIAnalyzer
argument_list|()
expr_stmt|;
block|}
DECL|method|testSingleChunkExceptions
specifier|public
name|void
name|testSingleChunkExceptions
parameter_list|()
block|{
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
name|String
name|termStr
init|=
literal|"the*tre"
decl_stmt|;
name|Analyzer
name|stopsAnalyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|q
init|=
name|parseWithAnalyzingQueryParser
argument_list|(
name|termStr
argument_list|,
name|stopsAnalyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"returned nothing"
argument_list|)
condition|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Should have returned nothing"
argument_list|,
literal|true
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|ex
operator|=
literal|false
expr_stmt|;
name|AnalyzingQueryParser
name|qp
init|=
operator|new
name|AnalyzingQueryParser
argument_list|(
name|FIELD
argument_list|,
name|a
argument_list|)
decl_stmt|;
try|try
block|{
name|qp
operator|.
name|analyzeSingleChunk
argument_list|(
name|FIELD
argument_list|,
literal|""
argument_list|,
literal|"not a single chunk"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"multiple terms"
argument_list|)
condition|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Should have produced multiple terms"
argument_list|,
literal|true
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
DECL|method|testWildcardAlone
specifier|public
name|void
name|testWildcardAlone
parameter_list|()
throws|throws
name|ParseException
block|{
comment|//seems like crazy edge case, but can be useful in concordance
name|boolean
name|pex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Query
name|q
init|=
name|getAnalyzedQuery
argument_list|(
literal|"*"
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|pex
operator|=
literal|true
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wildcard alone with allowWildcard=false"
argument_list|,
literal|true
argument_list|,
name|pex
argument_list|)
expr_stmt|;
name|pex
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|String
name|qString
init|=
name|parseWithAnalyzingQueryParser
argument_list|(
literal|"*"
argument_list|,
name|a
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Every word"
argument_list|,
literal|"*"
argument_list|,
name|qString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|pex
operator|=
literal|true
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wildcard alone with allowWildcard=true"
argument_list|,
literal|false
argument_list|,
name|pex
argument_list|)
expr_stmt|;
block|}
DECL|method|testWildCardEscapes
specifier|public
name|void
name|testWildCardEscapes
parameter_list|()
throws|throws
name|ParseException
throws|,
name|IOException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|wildcardEscapeHits
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Query
name|q
init|=
name|getAnalyzedQuery
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"WildcardEscapeHits: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|,
name|isAHit
argument_list|(
name|q
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|wildcardEscapeMisses
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Query
name|q
init|=
name|getAnalyzedQuery
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"WildcardEscapeMisses: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|,
name|isAHit
argument_list|(
name|q
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWildCardQueryNoLeadingAllowed
specifier|public
name|void
name|testWildCardQueryNoLeadingAllowed
parameter_list|()
block|{
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|String
name|q
init|=
name|parseWithAnalyzingQueryParser
argument_list|(
name|wildcardInput
index|[
literal|0
index|]
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Testing initial wildcard not allowed"
argument_list|,
literal|true
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
DECL|method|testWildCardQuery
specifier|public
name|void
name|testWildCardQuery
parameter_list|()
throws|throws
name|ParseException
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
name|wildcardInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing wildcards with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|wildcardInput
index|[
name|i
index|]
argument_list|,
name|wildcardExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|wildcardInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPrefixQuery
specifier|public
name|void
name|testPrefixQuery
parameter_list|()
throws|throws
name|ParseException
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
name|prefixInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing prefixes with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|prefixInput
index|[
name|i
index|]
argument_list|,
name|prefixExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|prefixInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRangeQuery
specifier|public
name|void
name|testRangeQuery
parameter_list|()
throws|throws
name|ParseException
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
name|rangeInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing ranges with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|rangeInput
index|[
name|i
index|]
argument_list|,
name|rangeExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|rangeInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFuzzyQuery
specifier|public
name|void
name|testFuzzyQuery
parameter_list|()
throws|throws
name|ParseException
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
name|fuzzyInput
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Testing fuzzys with analyzer "
operator|+
name|a
operator|.
name|getClass
argument_list|()
operator|+
literal|", input string: "
operator|+
name|fuzzyInput
index|[
name|i
index|]
argument_list|,
name|fuzzyExpected
index|[
name|i
index|]
argument_list|,
name|parseWithAnalyzingQueryParser
argument_list|(
name|fuzzyInput
index|[
name|i
index|]
argument_list|,
name|a
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseWithAnalyzingQueryParser
specifier|private
name|String
name|parseWithAnalyzingQueryParser
parameter_list|(
name|String
name|s
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|allowLeadingWildcard
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
name|getAnalyzedQuery
argument_list|(
name|s
argument_list|,
name|a
argument_list|,
name|allowLeadingWildcard
argument_list|)
decl_stmt|;
return|return
name|q
operator|.
name|toString
argument_list|(
name|FIELD
argument_list|)
return|;
block|}
DECL|method|getAnalyzedQuery
specifier|private
name|Query
name|getAnalyzedQuery
parameter_list|(
name|String
name|s
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|allowLeadingWildcard
parameter_list|)
throws|throws
name|ParseException
block|{
name|AnalyzingQueryParser
name|qp
init|=
operator|new
name|AnalyzingQueryParser
argument_list|(
name|FIELD
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setAllowLeadingWildcard
argument_list|(
name|allowLeadingWildcard
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
name|q
return|;
block|}
DECL|class|FoldingFilter
specifier|final
specifier|static
class|class
name|FoldingFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|FoldingFilter
specifier|public
name|FoldingFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|char
name|term
index|[]
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
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
name|term
operator|.
name|length
condition|;
name|i
operator|++
control|)
switch|switch
condition|(
name|term
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'Ã¼'
case|:
name|term
index|[
name|i
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'Ã¶'
case|:
name|term
index|[
name|i
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'Ã©'
case|:
name|term
index|[
name|i
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'Ã¯'
case|:
name|term
index|[
name|i
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
DECL|class|ASCIIAnalyzer
specifier|final
specifier|static
class|class
name|ASCIIAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|result
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|result
argument_list|,
operator|new
name|FoldingFilter
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// LUCENE-4176
DECL|method|testByteTerms
specifier|public
name|void
name|testByteTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|s
init|=
literal|"à¹à¸"
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|MockBytesAnalyzer
argument_list|()
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|AnalyzingQueryParser
argument_list|(
name|FIELD
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
literal|"[à¹à¸ TO à¹à¸]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|isAHit
argument_list|(
name|q
argument_list|,
name|s
argument_list|,
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isAHit
specifier|private
name|boolean
name|isAHit
parameter_list|(
name|Query
name|q
parameter_list|,
name|String
name|content
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|ramDir
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
name|ramDir
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
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|fieldType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
name|FIELD
argument_list|,
name|content
argument_list|,
name|fieldType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|ramDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|is
init|=
operator|new
name|IndexSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|int
name|hits
init|=
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|hits
operator|==
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

