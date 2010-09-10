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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|UnicodeUtil
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
name|_TestUtil
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
name|automaton
operator|.
name|Automaton
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
name|automaton
operator|.
name|AutomatonTestUtil
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
name|automaton
operator|.
name|CharacterRunAutomaton
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
name|automaton
operator|.
name|RegExp
import|;
end_import

begin_comment
comment|/**  * Create an index with random unicode terms  * Generates random regexps, and validates against a simple impl.  */
end_comment

begin_class
DECL|class|TestRegexpRandom2
specifier|public
class|class
name|TestRegexpRandom2
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
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
operator|new
name|Field
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
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|num
init|=
literal|2000
operator|*
name|RANDOM_MULTIPLIER
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|field
operator|.
name|setValue
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|s
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|// utf16 order
name|Collections
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"UTF16 order:"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|terms
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** a stupid regexp query that just blasts thru the terms */
DECL|class|DumbRegexpQuery
specifier|private
class|class
name|DumbRegexpQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|automaton
specifier|private
specifier|final
name|Automaton
name|automaton
decl_stmt|;
DECL|method|DumbRegexpQuery
name|DumbRegexpQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|RegExp
name|re
init|=
operator|new
name|RegExp
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|automaton
operator|=
name|re
operator|.
name|toAutomaton
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleAutomatonTermsEnum
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|class|SimpleAutomatonTermsEnum
specifier|private
class|class
name|SimpleAutomatonTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|runAutomaton
name|CharacterRunAutomaton
name|runAutomaton
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|automaton
argument_list|)
decl_stmt|;
DECL|field|utf16
name|UnicodeUtil
operator|.
name|UTF16Result
name|utf16
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|()
decl_stmt|;
DECL|method|SimpleAutomatonTermsEnum
specifier|private
name|SimpleAutomatonTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|setInitialSeekTerm
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|,
name|term
operator|.
name|length
argument_list|,
name|utf16
argument_list|)
expr_stmt|;
return|return
name|runAutomaton
operator|.
name|run
argument_list|(
name|utf16
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|utf16
operator|.
name|length
argument_list|)
condition|?
name|AcceptStatus
operator|.
name|YES
else|:
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|toString
argument_list|()
operator|+
name|automaton
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** test a bunch of random regular expressions */
DECL|method|testRegexps
specifier|public
name|void
name|testRegexps
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
literal|1000
operator|*
name|RANDOM_MULTIPLIER
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|reg
init|=
name|AutomatonTestUtil
operator|.
name|randomRegexp
argument_list|(
name|random
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|reg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** check that the # of hits is the same as from a very    * simple regexpquery implementation.    */
DECL|method|assertSame
specifier|private
name|void
name|assertSame
parameter_list|(
name|String
name|regexp
parameter_list|)
throws|throws
name|IOException
block|{
name|RegexpQuery
name|smart
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|regexp
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|DumbRegexpQuery
name|dumb
init|=
operator|new
name|DumbRegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|regexp
argument_list|)
argument_list|,
name|RegExp
operator|.
name|NONE
argument_list|)
decl_stmt|;
comment|// we can't compare the two if automaton rewrites to a simpler enum.
comment|// for example: "a\uda07\udcc7?.*?" gets rewritten to a simpler query:
comment|// a\uda07* prefixquery. Prefixquery then does the "wrong" thing, which
comment|// isn't really wrong as the query was undefined to begin with... but not
comment|// automatically comparable.
if|if
condition|(
operator|!
operator|(
name|smart
operator|.
name|getTermsEnum
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
operator|instanceof
name|AutomatonTermsEnum
operator|)
condition|)
return|return;
name|TopDocs
name|smartDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|smart
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|TopDocs
name|dumbDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|dumb
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|smart
argument_list|,
name|smartDocs
operator|.
name|scoreDocs
argument_list|,
name|dumbDocs
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

