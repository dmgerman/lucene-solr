begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
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
name|ArrayList
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
name|Locale
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|SolrClientCache
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
name|AddEvaluator
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
name|GreaterThanEvaluator
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
name|IfThenElseEvaluator
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
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
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
name|request
operator|.
name|UpdateRequest
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
name|cloud
operator|.
name|AbstractDistribZkTestBase
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
name|cloud
operator|.
name|SolrCloudTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
comment|/**  *  All base tests will be done with CloudSolrStream. Under the covers CloudSolrStream uses SolrStream so  *  SolrStream will get fully exercised through these tests.  *  **/
end_comment

begin_class
annotation|@
name|Slow
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|}
argument_list|)
DECL|class|SelectWithEvaluatorsTest
specifier|public
class|class
name|SelectWithEvaluatorsTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|COLLECTIONORALIAS
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTIONORALIAS
init|=
literal|"collection1"
decl_stmt|;
DECL|field|TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
name|DEFAULT_TIMEOUT
decl_stmt|;
DECL|field|id
specifier|private
specifier|static
specifier|final
name|String
name|id
init|=
literal|"id"
decl_stmt|;
DECL|field|useAlias
specifier|private
specifier|static
name|boolean
name|useAlias
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|4
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
argument_list|,
name|getFile
argument_list|(
literal|"solrj"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"streaming"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"ml"
argument_list|,
name|getFile
argument_list|(
literal|"solrj"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"ml"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|String
name|collection
decl_stmt|;
name|useAlias
operator|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
if|if
condition|(
name|useAlias
condition|)
block|{
name|collection
operator|=
name|COLLECTIONORALIAS
operator|+
literal|"_collection"
expr_stmt|;
block|}
else|else
block|{
name|collection
operator|=
name|COLLECTIONORALIAS
expr_stmt|;
block|}
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collection
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|useAlias
condition|)
block|{
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
name|COLLECTIONORALIAS
argument_list|,
name|collection
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|cleanIndex
specifier|public
name|void
name|cleanIndex
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|COLLECTIONORALIAS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectWithEvaluatorsStream
specifier|public
name|void
name|testSelectWithEvaluatorsStream
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"a_s"
argument_list|,
literal|"foo"
argument_list|,
literal|"b_i"
argument_list|,
literal|"1"
argument_list|,
literal|"c_d"
argument_list|,
literal|"3.3"
argument_list|,
literal|"d_b"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|COLLECTIONORALIAS
argument_list|)
expr_stmt|;
name|String
name|clause
decl_stmt|;
name|TupleStream
name|stream
decl_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
decl_stmt|;
name|StreamContext
name|streamContext
init|=
operator|new
name|StreamContext
argument_list|()
decl_stmt|;
name|SolrClientCache
name|solrClientCache
init|=
operator|new
name|SolrClientCache
argument_list|()
decl_stmt|;
name|streamContext
operator|.
name|setSolrClientCache
argument_list|(
name|solrClientCache
argument_list|)
expr_stmt|;
name|StreamFactory
name|factory
init|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withCollectionZkHost
argument_list|(
literal|"collection1"
argument_list|,
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"search"
argument_list|,
name|CloudSolrStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"select"
argument_list|,
name|SelectStream
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"add"
argument_list|,
name|AddEvaluator
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"if"
argument_list|,
name|IfThenElseEvaluator
operator|.
name|class
argument_list|)
operator|.
name|withFunctionName
argument_list|(
literal|"gt"
argument_list|,
name|GreaterThanEvaluator
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Basic test
name|clause
operator|=
literal|"select("
operator|+
literal|"id,"
operator|+
literal|"add(b_i,c_d) as result,"
operator|+
literal|"search(collection1, q=*:*, fl=\"id,a_s,b_i,c_d,d_b\", sort=\"id asc\")"
operator|+
literal|")"
expr_stmt|;
name|stream
operator|=
name|factory
operator|.
name|constructStream
argument_list|(
name|clause
argument_list|)
expr_stmt|;
name|stream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
name|tuples
operator|=
name|getTuples
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertFields
argument_list|(
name|tuples
argument_list|,
literal|"id"
argument_list|,
literal|"result"
argument_list|)
expr_stmt|;
name|assertNotFields
argument_list|(
name|tuples
argument_list|,
literal|"a_s"
argument_list|,
literal|"b_i"
argument_list|,
literal|"c_d"
argument_list|,
literal|"d_b"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tuples
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertDouble
argument_list|(
name|tuples
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"result"
argument_list|,
literal|4.3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.3
argument_list|,
name|tuples
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"result"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|solrClientCache
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getTuples
specifier|protected
name|List
argument_list|<
name|Tuple
argument_list|>
name|getTuples
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|)
throws|throws
name|IOException
block|{
name|tupleStream
operator|.
name|open
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
init|=
operator|new
name|ArrayList
argument_list|<
name|Tuple
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Tuple
name|t
init|=
name|tupleStream
operator|.
name|read
argument_list|()
init|;
operator|!
name|t
operator|.
name|EOF
condition|;
name|t
operator|=
name|tupleStream
operator|.
name|read
argument_list|()
control|)
block|{
name|tuples
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|tuples
return|;
block|}
DECL|method|assertOrder
specifier|protected
name|boolean
name|assertOrder
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|assertOrderOf
argument_list|(
name|tuples
argument_list|,
literal|"id"
argument_list|,
name|ids
argument_list|)
return|;
block|}
DECL|method|assertOrderOf
specifier|protected
name|boolean
name|assertOrderOf
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Tuple
name|t
init|=
name|tuples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|tip
init|=
name|t
operator|.
name|getString
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tip
operator|.
name|equals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|tip
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertMapOrder
specifier|protected
name|boolean
name|assertMapOrder
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Tuple
name|t
init|=
name|tuples
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|>
name|tip
init|=
name|t
operator|.
name|getMaps
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
name|int
name|id
init|=
operator|(
name|int
operator|)
name|tip
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|val
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|id
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertFields
specifier|protected
name|boolean
name|assertFields
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
operator|!
name|tuple
operator|.
name|fields
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected field '%s' not found"
argument_list|,
name|field
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertNotFields
specifier|protected
name|boolean
name|assertNotFields
parameter_list|(
name|List
argument_list|<
name|Tuple
argument_list|>
name|tuples
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Tuple
name|tuple
range|:
name|tuples
control|)
block|{
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|tuple
operator|.
name|fields
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Unexpected field '%s' found"
argument_list|,
name|field
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertGroupOrder
specifier|protected
name|boolean
name|assertGroupOrder
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|?
argument_list|>
name|group
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|tuple
operator|.
name|get
argument_list|(
literal|"tuples"
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|t
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|group
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Long
name|tip
init|=
operator|(
name|Long
operator|)
name|t
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tip
operator|.
name|intValue
argument_list|()
operator|!=
name|val
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|tip
operator|.
name|intValue
argument_list|()
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertLong
specifier|public
name|boolean
name|assertLong
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|long
name|l
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|lv
init|=
operator|(
name|long
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lv
operator|!=
name|l
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Longs not equal:"
operator|+
name|l
operator|+
literal|" : "
operator|+
name|lv
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertDouble
specifier|public
name|boolean
name|assertDouble
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|double
name|expectedValue
parameter_list|)
throws|throws
name|Exception
block|{
name|double
name|value
init|=
operator|(
name|double
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedValue
operator|!=
name|value
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Doubles not equal:"
operator|+
name|value
operator|+
literal|" : "
operator|+
name|expectedValue
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertString
specifier|public
name|boolean
name|assertString
parameter_list|(
name|Tuple
name|tuple
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|actual
init|=
operator|(
name|String
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
literal|null
operator|==
name|expected
operator|&&
literal|null
operator|!=
name|actual
operator|)
operator|||
operator|(
literal|null
operator|!=
name|expected
operator|&&
literal|null
operator|==
name|actual
operator|)
operator|||
operator|(
literal|null
operator|!=
name|expected
operator|&&
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Longs not equal:"
operator|+
name|expected
operator|+
literal|" : "
operator|+
name|actual
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertMaps
specifier|protected
name|boolean
name|assertMaps
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|>
name|maps
parameter_list|,
name|int
modifier|...
name|ids
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|maps
operator|.
name|size
argument_list|()
operator|!=
name|ids
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Expected id count != actual map count:"
operator|+
name|ids
operator|.
name|length
operator|+
literal|":"
operator|+
name|maps
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|val
range|:
name|ids
control|)
block|{
name|Map
name|t
init|=
name|maps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|tip
init|=
operator|(
name|String
operator|)
name|t
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tip
operator|.
name|equals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Found value:"
operator|+
name|tip
operator|+
literal|" expecting:"
operator|+
name|val
argument_list|)
throw|;
block|}
operator|++
name|i
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|assertList
specifier|private
name|boolean
name|assertList
parameter_list|(
name|List
name|list
parameter_list|,
name|Object
modifier|...
name|vals
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|!=
name|vals
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Lists are not the same size:"
operator|+
name|list
operator|.
name|size
argument_list|()
operator|+
literal|" : "
operator|+
name|vals
operator|.
name|length
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|a
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|b
init|=
name|vals
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"List items not equals:"
operator|+
name|a
operator|+
literal|" : "
operator|+
name|b
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

