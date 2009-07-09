begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_class
DECL|class|SimpleFragmentsBuilderTest
specifier|public
class|class
name|SimpleFragmentsBuilderTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|test1TermIndex
specifier|public
name|void
name|test1TermIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b>"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
comment|// change tags
name|sfb
operator|=
operator|new
name|SimpleFragmentsBuilder
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"["
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"]"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[a]"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Frags
specifier|public
name|void
name|test2Frags
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
literal|"a"
argument_list|,
literal|"a b b b b b b b b b b b a b a b"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|f
init|=
name|sfb
operator|.
name|createFragments
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// 3 snippets requested, but should be 2
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b> b b b b b b b b b "
argument_list|,
name|f
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b b<b>a</b> b<b>a</b> b"
argument_list|,
name|f
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|test3Frags
specifier|public
name|void
name|test3Frags
parameter_list|()
throws|throws
name|Exception
block|{
name|FieldFragList
name|ffl
init|=
name|ffl
argument_list|(
literal|"a c"
argument_list|,
literal|"a b b b b b b b b b b b a b a b b b b b c a a b b"
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|String
index|[]
name|f
init|=
name|sfb
operator|.
name|createFragments
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|f
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>a</b> b b b b b b b b b "
argument_list|,
name|f
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b b<b>a</b> b<b>a</b> b b b b b "
argument_list|,
name|f
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<b>c</b><b>a</b><b>a</b> b b"
argument_list|,
name|f
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|ffl
specifier|private
name|FieldFragList
name|ffl
parameter_list|(
name|String
name|queryValue
parameter_list|,
name|String
name|indexValue
parameter_list|)
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
name|indexValue
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|paW
operator|.
name|parse
argument_list|(
name|queryValue
argument_list|)
decl_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimpleFragListBuilder
argument_list|()
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|20
argument_list|)
return|;
block|}
DECL|method|test1PhraseShortMV
specifier|public
name|void
name|test1PhraseShortMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexShortMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a b c<b>d</b> e"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseLongMV
specifier|public
name|void
name|test1PhraseLongMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"search"
argument_list|,
literal|"engines"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|SimpleFragmentsBuilder
name|sfb
init|=
operator|new
name|SimpleFragmentsBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|" most<b>search engines</b> use only one of these methods. Even the<b>search engines</b> that says they can use t"
argument_list|,
name|sfb
operator|.
name|createFragment
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|ffl
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*  * ----------------------------------  *  THIS TEST DEPENDS ON LUCENE-1448  *  UNCOMMENT WHEN IT IS COMMITTED.  * ----------------------------------   public void test1PhraseLongMVB() throws Exception {     makeIndexLongMVB();      FieldQuery fq = new FieldQuery( pqF( "sp", "pe", "ee", "ed" ), true, true ); // "speed" -(2gram)-> "sp","pe","ee","ed"     FieldTermStack stack = new FieldTermStack( reader, 0, F, fq );     FieldPhraseList fpl = new FieldPhraseList( stack, fq );     SimpleFragListBuilder sflb = new SimpleFragListBuilder();     FieldFragList ffl = sflb.createFieldFragList( fpl, 100 );     SimpleFragmentsBuilder sfb = new SimpleFragmentsBuilder();     assertEquals( "ssing<b>speed</b>, the", sfb.createFragment( reader, 0, F, ffl ) );   } */
block|}
end_class

end_unit

