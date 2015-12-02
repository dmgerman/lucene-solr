begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
operator|.
name|TestBulkSchemaAPI
operator|.
name|getSourceCopyFields
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
operator|.
name|TestBulkSchemaAPI
operator|.
name|getObj
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
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|SolrTestCaseJ4
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
name|SolrClient
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
name|impl
operator|.
name|HttpSolrClient
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
name|AbstractFullDistribZkTestBase
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|common
operator|.
name|util
operator|.
name|Utils
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
name|util
operator|.
name|RESTfulServerProvider
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
name|util
operator|.
name|RestTestHarness
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

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestBulkSchemaConcurrent
specifier|public
class|class
name|TestBulkSchemaConcurrent
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|restTestHarnesses
specifier|private
name|List
argument_list|<
name|RestTestHarness
argument_list|>
name|restTestHarnesses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|initSysProperties
specifier|public
specifier|static
name|void
name|initSysProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-managed-schema.xml"
return|;
block|}
DECL|method|setupHarnesses
specifier|private
name|void
name|setupHarnesses
parameter_list|()
block|{
for|for
control|(
specifier|final
name|SolrClient
name|client
range|:
name|clients
control|)
block|{
name|RestTestHarness
name|harness
init|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|RESTfulServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
operator|(
operator|(
name|HttpSolrClient
operator|)
name|client
operator|)
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|restTestHarnesses
operator|.
name|add
argument_list|(
name|harness
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
for|for
control|(
name|RestTestHarness
name|r
range|:
name|restTestHarnesses
control|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|threadCount
init|=
literal|5
decl_stmt|;
name|setupHarnesses
argument_list|()
expr_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|threadCount
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|List
argument_list|>
name|collectErrors
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|finalI
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ArrayList
name|errs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|collectErrors
operator|.
name|add
argument_list|(
name|errs
argument_list|)
expr_stmt|;
try|try
block|{
name|invokeBulkAddCall
argument_list|(
name|finalI
argument_list|,
name|errs
argument_list|)
expr_stmt|;
name|invokeBulkReplaceCall
argument_list|(
name|finalI
argument_list|,
name|errs
argument_list|)
expr_stmt|;
name|invokeBulkDeleteCall
argument_list|(
name|finalI
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|true
decl_stmt|;
for|for
control|(
name|List
name|e
range|:
name|collectErrors
control|)
block|{
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
operator|!
name|e
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|collectErrors
operator|.
name|toString
argument_list|()
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|invokeBulkAddCall
specifier|private
name|void
name|invokeBulkAddCall
parameter_list|(
name|int
name|seed
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"          'add-field' : {\n"
operator|+
literal|"                       'name':'replaceFieldA',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':false\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-dynamic-field' : {\n"
operator|+
literal|"                       'name' :'replaceDynamicField',\n"
operator|+
literal|"                       'type':'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-copy-field' : {\n"
operator|+
literal|"                       'source' :'replaceFieldA',\n"
operator|+
literal|"                       'dest':['replaceDynamicCopyFieldDest']\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-field-type' : {\n"
operator|+
literal|"                       'name' :'myNewFieldTypeName',\n"
operator|+
literal|"                       'class' : 'solr.StrField',\n"
operator|+
literal|"                       'sortMissingLast':'true'\n"
operator|+
literal|"                       }\n"
operator|+
literal|" }"
decl_stmt|;
name|String
name|aField
init|=
literal|"a"
operator|+
name|seed
decl_stmt|;
name|String
name|dynamicFldName
init|=
literal|"*_lol"
operator|+
name|seed
decl_stmt|;
name|String
name|dynamicCopyFldDest
init|=
literal|"hello_lol"
operator|+
name|seed
decl_stmt|;
name|String
name|newFieldTypeName
init|=
literal|"mystr"
operator|+
name|seed
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceFieldA"
argument_list|,
name|aField
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceDynamicField"
argument_list|,
name|dynamicFldName
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceDynamicCopyFieldDest"
argument_list|,
name|dynamicCopyFldDest
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"myNewFieldTypeName"
argument_list|,
name|newFieldTypeName
argument_list|)
expr_stmt|;
name|RestTestHarness
name|publisher
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|errors
init|=
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|errors
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//get another node
name|Set
argument_list|<
name|String
argument_list|>
name|errmessages
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|RestTestHarness
name|harness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|maxTimeoutMillis
init|=
literal|100000
decl_stmt|;
while|while
condition|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutMillis
condition|)
block|{
name|errmessages
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Map
name|m
init|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|aField
argument_list|,
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"field {0} not created"
argument_list|,
name|aField
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|dynamicFldName
argument_list|,
literal|"dynamicFields"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"dynamic field {0} not created"
argument_list|,
name|dynamicFldName
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|l
init|=
name|getSourceCopyFields
argument_list|(
name|harness
argument_list|,
name|aField
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checkCopyField
argument_list|(
name|l
argument_list|,
name|aField
argument_list|,
name|dynamicCopyFldDest
argument_list|)
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"CopyField source={0},dest={1} not created"
argument_list|,
name|aField
argument_list|,
name|dynamicCopyFldDest
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|newFieldTypeName
argument_list|,
literal|"fieldTypes"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"new type {0}  not created"
argument_list|,
name|newFieldTypeName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|harness
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|errs
operator|.
name|addAll
argument_list|(
name|errmessages
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|invokeBulkReplaceCall
specifier|private
name|void
name|invokeBulkReplaceCall
parameter_list|(
name|int
name|seed
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"          'replace-field' : {\n"
operator|+
literal|"                       'name':'replaceFieldA',\n"
operator|+
literal|"                       'type': 'text',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'replace-dynamic-field' : {\n"
operator|+
literal|"                       'name' :'replaceDynamicField',\n"
operator|+
literal|"                        'type':'text',\n"
operator|+
literal|"                        'stored':true,\n"
operator|+
literal|"                        'indexed':true\n"
operator|+
literal|"                        },\n"
operator|+
literal|"          'replace-field-type' : {\n"
operator|+
literal|"                       'name' :'myNewFieldTypeName',\n"
operator|+
literal|"                       'class' : 'solr.TextField'\n"
operator|+
literal|"                        }\n"
operator|+
literal|" }"
decl_stmt|;
name|String
name|aField
init|=
literal|"a"
operator|+
name|seed
decl_stmt|;
name|String
name|dynamicFldName
init|=
literal|"*_lol"
operator|+
name|seed
decl_stmt|;
name|String
name|dynamicCopyFldDest
init|=
literal|"hello_lol"
operator|+
name|seed
decl_stmt|;
name|String
name|newFieldTypeName
init|=
literal|"mystr"
operator|+
name|seed
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceFieldA"
argument_list|,
name|aField
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceDynamicField"
argument_list|,
name|dynamicFldName
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"myNewFieldTypeName"
argument_list|,
name|newFieldTypeName
argument_list|)
expr_stmt|;
name|RestTestHarness
name|publisher
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|errors
init|=
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|errors
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//get another node
name|Set
argument_list|<
name|String
argument_list|>
name|errmessages
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|RestTestHarness
name|harness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|maxTimeoutMillis
init|=
literal|100000
decl_stmt|;
while|while
condition|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutMillis
condition|)
block|{
name|errmessages
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Map
name|m
init|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|aField
argument_list|,
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"field {0} no longer present"
argument_list|,
name|aField
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|dynamicFldName
argument_list|,
literal|"dynamicFields"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"dynamic field {0} no longer present"
argument_list|,
name|dynamicFldName
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|l
init|=
name|getSourceCopyFields
argument_list|(
name|harness
argument_list|,
name|aField
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checkCopyField
argument_list|(
name|l
argument_list|,
name|aField
argument_list|,
name|dynamicCopyFldDest
argument_list|)
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"CopyField source={0},dest={1} no longer present"
argument_list|,
name|aField
argument_list|,
name|dynamicCopyFldDest
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|newFieldTypeName
argument_list|,
literal|"fieldTypes"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"new type {0} no longer present"
argument_list|,
name|newFieldTypeName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|harness
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|errs
operator|.
name|addAll
argument_list|(
name|errmessages
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|invokeBulkDeleteCall
specifier|private
name|void
name|invokeBulkDeleteCall
parameter_list|(
name|int
name|seed
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"          'delete-copy-field' : {\n"
operator|+
literal|"                       'source' :'replaceFieldA',\n"
operator|+
literal|"                       'dest':['replaceDynamicCopyFieldDest']\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'delete-field' : {'name':'replaceFieldA'},\n"
operator|+
literal|"          'delete-dynamic-field' : {'name' :'replaceDynamicField'},\n"
operator|+
literal|"          'delete-field-type' : {'name' :'myNewFieldTypeName'}\n"
operator|+
literal|" }"
decl_stmt|;
name|String
name|aField
init|=
literal|"a"
operator|+
name|seed
decl_stmt|;
name|String
name|dynamicFldName
init|=
literal|"*_lol"
operator|+
name|seed
decl_stmt|;
name|String
name|dynamicCopyFldDest
init|=
literal|"hello_lol"
operator|+
name|seed
decl_stmt|;
name|String
name|newFieldTypeName
init|=
literal|"mystr"
operator|+
name|seed
decl_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceFieldA"
argument_list|,
name|aField
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceDynamicField"
argument_list|,
name|dynamicFldName
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"replaceDynamicCopyFieldDest"
argument_list|,
name|dynamicCopyFldDest
argument_list|)
expr_stmt|;
name|payload
operator|=
name|payload
operator|.
name|replace
argument_list|(
literal|"myNewFieldTypeName"
argument_list|,
name|newFieldTypeName
argument_list|)
expr_stmt|;
name|RestTestHarness
name|publisher
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|publisher
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|SolrTestCaseJ4
operator|.
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|errors
init|=
name|map
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
operator|new
name|String
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|errors
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//get another node
name|Set
argument_list|<
name|String
argument_list|>
name|errmessages
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|RestTestHarness
name|harness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|maxTimeoutMillis
init|=
literal|100000
decl_stmt|;
while|while
condition|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutMillis
condition|)
block|{
name|errmessages
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Map
name|m
init|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|aField
argument_list|,
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"field {0} still exists"
argument_list|,
name|aField
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|dynamicFldName
argument_list|,
literal|"dynamicFields"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"dynamic field {0} still exists"
argument_list|,
name|dynamicFldName
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|l
init|=
name|getSourceCopyFields
argument_list|(
name|harness
argument_list|,
name|aField
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkCopyField
argument_list|(
name|l
argument_list|,
name|aField
argument_list|,
name|dynamicCopyFldDest
argument_list|)
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"CopyField source={0},dest={1} still exists"
argument_list|,
name|aField
argument_list|,
name|dynamicCopyFldDest
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|getObj
argument_list|(
name|harness
argument_list|,
name|newFieldTypeName
argument_list|,
literal|"fieldTypes"
argument_list|)
expr_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
name|errmessages
operator|.
name|add
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"new type {0} still exists"
argument_list|,
name|newFieldTypeName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|harness
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|errmessages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|errs
operator|.
name|addAll
argument_list|(
name|errmessages
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkCopyField
specifier|private
name|boolean
name|checkCopyField
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|>
name|l
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dest
parameter_list|)
block|{
if|if
condition|(
name|l
operator|==
literal|null
condition|)
return|return
literal|false
return|;
for|for
control|(
name|Map
name|map
range|:
name|l
control|)
block|{
if|if
condition|(
name|src
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"source"
argument_list|)
argument_list|)
operator|&&
name|dest
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"dest"
argument_list|)
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

