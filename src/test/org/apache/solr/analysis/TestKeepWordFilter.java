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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Token
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|TestKeepWordFilter
specifier|public
class|class
name|TestKeepWordFilter
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|testStopAndGo
specifier|public
name|void
name|testStopAndGo
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|words
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|words
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|words
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|input
init|=
name|tokens
argument_list|(
literal|"aaa BBB ccc ddd EEE"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Test Stopwords
name|KeepWordFilterFactory
name|factory
init|=
operator|new
name|KeepWordFilterFactory
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|solrConfig
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setWords
argument_list|(
name|words
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|expect
init|=
name|tokens
argument_list|(
literal|"aaa BBB"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|real
init|=
name|getTokens
argument_list|(
name|factory
operator|.
name|create
argument_list|(
operator|new
name|IterTokenStream
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokEqual
argument_list|(
name|expect
argument_list|,
name|real
argument_list|)
expr_stmt|;
comment|// Now force case
name|args
operator|.
name|put
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|solrConfig
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|expect
operator|=
name|tokens
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|real
operator|=
name|getTokens
argument_list|(
name|factory
operator|.
name|create
argument_list|(
operator|new
name|IterTokenStream
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|expect
argument_list|,
name|real
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

