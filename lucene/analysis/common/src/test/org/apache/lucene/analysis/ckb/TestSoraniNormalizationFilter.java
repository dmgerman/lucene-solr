begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ckb
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ckb
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
name|BaseTokenStreamTestCase
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
name|core
operator|.
name|KeywordTokenizer
import|;
end_import

begin_comment
comment|/**  * Tests normalization for Sorani (this is more critical than stemming...)  */
end_comment

begin_class
DECL|class|TestSoraniNormalizationFilter
specifier|public
class|class
name|TestSoraniNormalizationFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
name|Analyzer
name|a
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
name|a
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|SoraniNormalizationFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|a
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
DECL|method|testY
specifier|public
name|void
name|testY
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u064A"
argument_list|,
literal|"\u06CC"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0649"
argument_list|,
literal|"\u06CC"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u06CC"
argument_list|,
literal|"\u06CC"
argument_list|)
expr_stmt|;
block|}
DECL|method|testK
specifier|public
name|void
name|testK
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0643"
argument_list|,
literal|"\u06A9"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u06A9"
argument_list|,
literal|"\u06A9"
argument_list|)
expr_stmt|;
block|}
DECL|method|testH
specifier|public
name|void
name|testH
parameter_list|()
throws|throws
name|Exception
block|{
comment|// initial
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0647\u200C"
argument_list|,
literal|"\u06D5"
argument_list|)
expr_stmt|;
comment|// medial
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0647\u200C\u06A9"
argument_list|,
literal|"\u06D5\u06A9"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u06BE"
argument_list|,
literal|"\u0647"
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0629"
argument_list|,
literal|"\u06D5"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFinalH
specifier|public
name|void
name|testFinalH
parameter_list|()
throws|throws
name|Exception
block|{
comment|// always (and in final form by def), so frequently omitted
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0647\u0647\u0647"
argument_list|,
literal|"\u0647\u0647\u06D5"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRR
specifier|public
name|void
name|testRR
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0692"
argument_list|,
literal|"\u0695"
argument_list|)
expr_stmt|;
block|}
DECL|method|testInitialRR
specifier|public
name|void
name|testInitialRR
parameter_list|()
throws|throws
name|Exception
block|{
comment|// always, so frequently omitted
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0631\u0631\u0631"
argument_list|,
literal|"\u0695\u0631\u0631"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemove
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0640"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u064B"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u064C"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u064D"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u064E"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u064F"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0650"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0651"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u0652"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// we peek backwards in this case to look for h+200C, ensure this works
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"\u200C"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|SoraniNormalizationFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

