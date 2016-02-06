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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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

begin_class
DECL|class|Test01Exceptions
specifier|public
class|class
name|Test01Exceptions
extends|extends
name|LuceneTestCase
block|{
comment|/** Main for running test case by itself. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|Test01Exceptions
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|verbose
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
comment|/* to show actual parsing error messages */
DECL|field|fieldName
specifier|final
name|String
name|fieldName
init|=
literal|"bi"
decl_stmt|;
DECL|field|exceptionQueries
name|String
index|[]
name|exceptionQueries
init|=
block|{
literal|"*"
block|,
literal|"a*"
block|,
literal|"ab*"
block|,
literal|"?"
block|,
literal|"a?"
block|,
literal|"ab?"
block|,
literal|"a???b"
block|,
literal|"a?"
block|,
literal|"a*b?"
block|,
literal|"word1 word2"
block|,
literal|"word2 AND"
block|,
literal|"word1 OR"
block|,
literal|"AND(word2)"
block|,
literal|"AND(word2,)"
block|,
literal|"AND(word2,word1,)"
block|,
literal|"OR(word2)"
block|,
literal|"OR(word2 ,"
block|,
literal|"OR(word2 , word1 ,)"
block|,
literal|"xx NOT"
block|,
literal|"xx (a AND b)"
block|,
literal|"(a AND b"
block|,
literal|"a OR b)"
block|,
literal|"or(word2+ not ord+, and xyz,def)"
block|,
literal|""
block|}
decl_stmt|;
DECL|method|test01Exceptions
specifier|public
name|void
name|test01Exceptions
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|m
init|=
name|ExceptionQueryTst
operator|.
name|getFailQueries
argument_list|(
name|exceptionQueries
argument_list|,
name|verbose
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"No ParseException for:\n"
operator|+
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

