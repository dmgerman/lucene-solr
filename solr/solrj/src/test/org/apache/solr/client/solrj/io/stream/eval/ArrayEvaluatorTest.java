begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.eval
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|eval
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|ArrayEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|StreamEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamContext
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|ArrayEvaluatorTest
specifier|public
class|class
name|ArrayEvaluatorTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|factory
name|StreamFactory
name|factory
decl_stmt|;
DECL|field|values
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
decl_stmt|;
DECL|method|ArrayEvaluatorTest
specifier|public
name|ArrayEvaluatorTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withFunctionName
argument_list|(
literal|"array"
argument_list|,
name|ArrayEvaluator
operator|.
name|class
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|arrayLongSortAscTest
specifier|public
name|void
name|arrayLongSortAscTest
parameter_list|()
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"array(a,b,c, sort=asc)"
argument_list|)
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Object
name|result
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1L
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2L
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3L
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|arrayLongSortDescTest
specifier|public
name|void
name|arrayLongSortDescTest
parameter_list|()
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"array(a,b,c, sort=desc)"
argument_list|)
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Object
name|result
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3L
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2L
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1L
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|arrayStringSortAscTest
specifier|public
name|void
name|arrayStringSortAscTest
parameter_list|()
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"array(a,b,c, sort=asc)"
argument_list|)
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Object
name|result
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|arrayStringSortDescTest
specifier|public
name|void
name|arrayStringSortDescTest
parameter_list|()
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"array(a,b,c, sort=desc)"
argument_list|)
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Object
name|result
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|arrayStringUnsortedTest
specifier|public
name|void
name|arrayStringUnsortedTest
parameter_list|()
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"array(a,b,c)"
argument_list|)
decl_stmt|;
name|StreamContext
name|context
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|evaluator
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Object
name|result
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|result
operator|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

