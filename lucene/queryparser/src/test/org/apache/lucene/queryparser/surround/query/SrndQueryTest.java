begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
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
name|queryparser
operator|.
name|surround
operator|.
name|parser
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
name|search
operator|.
name|QueryUtils
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|SrndQueryTest
specifier|public
class|class
name|SrndQueryTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|checkEqualParsings
name|void
name|checkEqualParsings
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"foo"
decl_stmt|;
name|BasicQueryFactory
name|qf
init|=
operator|new
name|BasicQueryFactory
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|Query
name|lq1
decl_stmt|,
name|lq2
decl_stmt|;
name|lq1
operator|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|s1
argument_list|)
operator|.
name|makeLuceneQueryField
argument_list|(
name|fieldName
argument_list|,
name|qf
argument_list|)
expr_stmt|;
name|lq2
operator|=
name|QueryParser
operator|.
name|parse
argument_list|(
name|s2
argument_list|)
operator|.
name|makeLuceneQueryField
argument_list|(
name|fieldName
argument_list|,
name|qf
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|lq1
argument_list|,
name|lq2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHashEquals
specifier|public
name|void
name|testHashEquals
parameter_list|()
throws|throws
name|Exception
block|{
comment|//grab some sample queries from Test02Boolean and Test03Distance and
comment|//check there hashes and equals
name|checkEqualParsings
argument_list|(
literal|"word1 w word2"
argument_list|,
literal|" word1  w  word2 "
argument_list|)
expr_stmt|;
name|checkEqualParsings
argument_list|(
literal|"2N(w1,w2,w3)"
argument_list|,
literal|" 2N(w1, w2 , w3)"
argument_list|)
expr_stmt|;
name|checkEqualParsings
argument_list|(
literal|"abc?"
argument_list|,
literal|" abc? "
argument_list|)
expr_stmt|;
name|checkEqualParsings
argument_list|(
literal|"w*rd?"
argument_list|,
literal|" w*rd?"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

