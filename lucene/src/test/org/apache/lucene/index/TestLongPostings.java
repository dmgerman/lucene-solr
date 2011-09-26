begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|document
operator|.
name|TextField
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
name|codecs
operator|.
name|CodecProvider
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
name|FixedBitSet
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

begin_class
DECL|class|TestLongPostings
specifier|public
class|class
name|TestLongPostings
extends|extends
name|LuceneTestCase
block|{
comment|// Produces a realistic unicode random string that
comment|// survives MockAnalyzer unchanged:
DECL|method|getRandomTerm
specifier|private
name|String
name|getRandomTerm
parameter_list|(
name|String
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
operator|&&
name|s
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|TokenStream
name|ts
init|=
name|a
operator|.
name|reusableTokenStream
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|termBytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
operator|&&
operator|!
name|termBytes
operator|.
name|utf8ToString
argument_list|()
operator|.
name|equals
argument_list|(
name|s
argument_list|)
condition|)
block|{
comment|// The value was changed during analysis.  Keep iterating so the
comment|// tokenStream is exhausted.
name|changed
operator|=
literal|true
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Did we iterate just once and the value was unchanged?
if|if
condition|(
operator|!
name|changed
operator|&&
name|count
operator|==
literal|1
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
block|}
DECL|method|testLongPostings
specifier|public
name|void
name|testLongPostings
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"Too slow with SimpleText codec at night"
argument_list|,
name|TEST_NIGHTLY
operator|&&
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|getFieldCodec
argument_list|(
literal|"field"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"SimpleText"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"Too slow with Memory codec at night"
argument_list|,
name|TEST_NIGHTLY
operator|&&
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|getFieldCodec
argument_list|(
literal|"field"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"Memory"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Don't use _TestUtil.getTempDir so that we own the
comment|// randomness (ie same seed will point to same dir):
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"longpostings"
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: NUM_DOCS="
operator|+
name|NUM_DOCS
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|s1
init|=
name|getRandomTerm
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s2
init|=
name|getRandomTerm
argument_list|(
name|s1
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: s1="
operator|+
name|s1
operator|+
literal|" s2="
operator|+
name|s2
argument_list|)
expr_stmt|;
comment|/*       for(int idx=0;idx<s1.length();idx++) {         System.out.println("  s1 ch=0x" + Integer.toHexString(s1.charAt(idx)));       }       for(int idx=0;idx<s2.length();idx++) {         System.out.println("  s2 ch=0x" + Integer.toHexString(s2.charAt(idx)));       }       */
block|}
specifier|final
name|FixedBitSet
name|isS1
init|=
operator|new
name|FixedBitSet
argument_list|(
name|NUM_DOCS
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|NUM_DOCS
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|isS1
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|IndexReader
name|r
decl_stmt|;
if|if
condition|(
literal|true
condition|)
block|{
specifier|final
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|16.0
operator|+
literal|16.0
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|NUM_DOCS
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|isS1
operator|.
name|get
argument_list|(
name|idx
argument_list|)
condition|?
name|s1
else|:
name|s2
decl_stmt|;
specifier|final
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
name|s
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ct
init|=
literal|0
init|;
name|ct
operator|<
name|count
condition|;
name|ct
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|riw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|riw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/*     if (VERBOSE) {       System.out.println("TEST: terms");       TermEnum termEnum = r.terms();       while(termEnum.next()) {         System.out.println("  term=" + termEnum.term() + " len=" + termEnum.term().text().length());         assertTrue(termEnum.docFreq()> 0);         System.out.println("    s1?=" + (termEnum.term().text().equals(s1)) + " s1len=" + s1.length());         System.out.println("    s2?=" + (termEnum.term().text().equals(s2)) + " s2len=" + s2.length());         final String s = termEnum.term().text();         for(int idx=0;idx<s.length();idx++) {           System.out.println("      ch=0x" + Integer.toHexString(s.charAt(idx)));         }       }     }     */
name|assertEquals
argument_list|(
name|NUM_DOCS
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|s1
argument_list|)
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|s2
argument_list|)
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|String
name|term
decl_stmt|;
specifier|final
name|boolean
name|doS1
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|term
operator|=
name|s1
expr_stmt|;
name|doS1
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|s2
expr_stmt|;
name|doS1
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" doS1="
operator|+
name|doS1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocsAndPositionsEnum
name|postings
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|docID
operator|<
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
specifier|final
name|int
name|what
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|what
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: docID="
operator|+
name|docID
operator|+
literal|"; do next()"
argument_list|)
expr_stmt|;
block|}
comment|// nextDoc
name|int
name|expected
init|=
name|docID
operator|+
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|expected
operator|==
name|NUM_DOCS
condition|)
block|{
name|expected
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|isS1
operator|.
name|get
argument_list|(
name|expected
argument_list|)
operator|==
name|doS1
condition|)
block|{
break|break;
block|}
else|else
block|{
name|expected
operator|++
expr_stmt|;
block|}
block|}
name|docID
operator|=
name|postings
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  got docID="
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|docID
argument_list|)
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
operator|==
literal|3
condition|)
block|{
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|>=
literal|1
operator|&&
name|freq
operator|<=
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|freq
condition|;
name|pos
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|postings
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|postings
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
name|postings
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// advance
specifier|final
name|int
name|targetDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|==
operator|-
literal|1
condition|)
block|{
name|targetDocID
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|NUM_DOCS
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|targetDocID
operator|=
name|docID
operator|+
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|NUM_DOCS
operator|-
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: docID="
operator|+
name|docID
operator|+
literal|"; do advance("
operator|+
name|targetDocID
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|int
name|expected
init|=
name|targetDocID
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|expected
operator|==
name|NUM_DOCS
condition|)
block|{
name|expected
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|isS1
operator|.
name|get
argument_list|(
name|expected
argument_list|)
operator|==
name|doS1
condition|)
block|{
break|break;
block|}
else|else
block|{
name|expected
operator|++
expr_stmt|;
block|}
block|}
name|docID
operator|=
name|postings
operator|.
name|advance
argument_list|(
name|targetDocID
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  got docID="
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|docID
argument_list|)
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
operator|==
literal|3
condition|)
block|{
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|>=
literal|1
operator|&&
name|freq
operator|<=
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|freq
condition|;
name|pos
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|postings
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|postings
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
name|postings
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
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
comment|// a weaker form of testLongPostings, that doesnt check positions
DECL|method|testLongPostingsNoPositions
specifier|public
name|void
name|testLongPostingsNoPositions
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestLongPostingsNoPositions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|doTestLongPostingsNoPositions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestLongPostingsNoPositions
specifier|public
name|void
name|doTestLongPostingsNoPositions
parameter_list|(
name|IndexOptions
name|options
parameter_list|)
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"Too slow with SimpleText codec at night"
argument_list|,
name|TEST_NIGHTLY
operator|&&
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|getFieldCodec
argument_list|(
literal|"field"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"SimpleText"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"Too slow with Memory codec at night"
argument_list|,
name|TEST_NIGHTLY
operator|&&
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|getFieldCodec
argument_list|(
literal|"field"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"Memory"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Don't use _TestUtil.getTempDir so that we own the
comment|// randomness (ie same seed will point to same dir):
name|Directory
name|dir
init|=
name|newFSDirectory
argument_list|(
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"longpostings"
operator|+
literal|"."
operator|+
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_DOCS
init|=
name|atLeast
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: NUM_DOCS="
operator|+
name|NUM_DOCS
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|s1
init|=
name|getRandomTerm
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s2
init|=
name|getRandomTerm
argument_list|(
name|s1
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: s1="
operator|+
name|s1
operator|+
literal|" s2="
operator|+
name|s2
argument_list|)
expr_stmt|;
comment|/*       for(int idx=0;idx<s1.length();idx++) {         System.out.println("  s1 ch=0x" + Integer.toHexString(s1.charAt(idx)));       }       for(int idx=0;idx<s2.length();idx++) {         System.out.println("  s2 ch=0x" + Integer.toHexString(s2.charAt(idx)));       }       */
block|}
specifier|final
name|FixedBitSet
name|isS1
init|=
operator|new
name|FixedBitSet
argument_list|(
name|NUM_DOCS
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|NUM_DOCS
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|isS1
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|IndexReader
name|r
decl_stmt|;
if|if
condition|(
literal|true
condition|)
block|{
specifier|final
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|16.0
operator|+
literal|16.0
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|riw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|NUM_DOCS
condition|;
name|idx
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|isS1
operator|.
name|get
argument_list|(
name|idx
argument_list|)
condition|?
name|s1
else|:
name|s2
decl_stmt|;
specifier|final
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
name|s
argument_list|,
name|ft
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ct
init|=
literal|0
init|;
name|ct
operator|<
name|count
condition|;
name|ct
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|riw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|riw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|riw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/*     if (VERBOSE) {       System.out.println("TEST: terms");       TermEnum termEnum = r.terms();       while(termEnum.next()) {         System.out.println("  term=" + termEnum.term() + " len=" + termEnum.term().text().length());         assertTrue(termEnum.docFreq()> 0);         System.out.println("    s1?=" + (termEnum.term().text().equals(s1)) + " s1len=" + s1.length());         System.out.println("    s2?=" + (termEnum.term().text().equals(s2)) + " s2len=" + s2.length());         final String s = termEnum.term().text();         for(int idx=0;idx<s.length();idx++) {           System.out.println("      ch=0x" + Integer.toHexString(s.charAt(idx)));         }       }     }     */
name|assertEquals
argument_list|(
name|NUM_DOCS
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|s1
argument_list|)
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|s2
argument_list|)
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|String
name|term
decl_stmt|;
specifier|final
name|boolean
name|doS1
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|term
operator|=
name|s1
expr_stmt|;
name|doS1
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|s2
expr_stmt|;
name|doS1
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: iter="
operator|+
name|iter
operator|+
literal|" doS1="
operator|+
name|doS1
operator|+
literal|" term="
operator|+
name|term
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DocsEnum
name|postings
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|docID
operator|<
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
specifier|final
name|int
name|what
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|what
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: docID="
operator|+
name|docID
operator|+
literal|"; do next()"
argument_list|)
expr_stmt|;
block|}
comment|// nextDoc
name|int
name|expected
init|=
name|docID
operator|+
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|expected
operator|==
name|NUM_DOCS
condition|)
block|{
name|expected
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|isS1
operator|.
name|get
argument_list|(
name|expected
argument_list|)
operator|==
name|doS1
condition|)
block|{
break|break;
block|}
else|else
block|{
name|expected
operator|++
expr_stmt|;
block|}
block|}
name|docID
operator|=
name|postings
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  got docID="
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|docID
argument_list|)
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
operator|==
literal|3
condition|)
block|{
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freq
operator|>=
literal|1
operator|&&
name|freq
operator|<=
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// advance
specifier|final
name|int
name|targetDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|==
operator|-
literal|1
condition|)
block|{
name|targetDocID
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|NUM_DOCS
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|targetDocID
operator|=
name|docID
operator|+
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|NUM_DOCS
operator|-
name|docID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: docID="
operator|+
name|docID
operator|+
literal|"; do advance("
operator|+
name|targetDocID
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|int
name|expected
init|=
name|targetDocID
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|expected
operator|==
name|NUM_DOCS
condition|)
block|{
name|expected
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|isS1
operator|.
name|get
argument_list|(
name|expected
argument_list|)
operator|==
name|doS1
condition|)
block|{
break|break;
block|}
else|else
block|{
name|expected
operator|++
expr_stmt|;
block|}
block|}
name|docID
operator|=
name|postings
operator|.
name|advance
argument_list|(
name|targetDocID
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  got docID="
operator|+
name|docID
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|docID
argument_list|)
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|6
argument_list|)
operator|==
literal|3
condition|)
block|{
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freq
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"got invalid freq="
operator|+
name|freq
argument_list|,
name|freq
operator|>=
literal|1
operator|&&
name|freq
operator|<=
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
block|}
end_class

end_unit

