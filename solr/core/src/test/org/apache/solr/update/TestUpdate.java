begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|*
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
name|DocIdSetIterator
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
name|TermQuery
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
name|noggit
operator|.
name|ObjectBuilder
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
name|common
operator|.
name|SolrException
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
name|SolrInputDocument
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
name|request
operator|.
name|SolrQueryRequest
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
name|TestHarness
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
name|*
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
name|*
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
name|atomic
operator|.
name|AtomicInteger
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
name|atomic
operator|.
name|AtomicLong
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|core
operator|.
name|SolrCore
operator|.
name|verbose
import|;
end_import

begin_class
DECL|class|TestUpdate
specifier|public
class|class
name|TestUpdate
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateableDocs
specifier|public
name|void
name|testUpdateableDocs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The document may be retrieved from the index or from the transaction log.
comment|// Test both by running the same test with and without commits
comment|// do without commits
name|doUpdateTest
argument_list|(
operator|new
name|Callable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// do with commits
name|doUpdateTest
argument_list|(
operator|new
name|Callable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|doUpdateTest
specifier|public
name|void
name|doUpdateTest
parameter_list|(
name|Callable
name|afterUpdate
parameter_list|)
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|long
name|version
decl_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_i"
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*_i,*_is"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':5, 'val_is':[10,5]}}"
argument_list|)
expr_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|"val_i"
argument_list|,
name|map
argument_list|(
literal|"set"
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*_i,*_is"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':100, 'val_is':[10,5,-1]}}"
argument_list|)
expr_stmt|;
name|long
name|version2
decl_stmt|;
try|try
block|{
comment|// try bad version added as a field in the doc
name|version2
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|100
argument_list|)
argument_list|,
literal|"_version_"
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|409
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// try bad version added as a request param
name|version2
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|100
argument_list|)
argument_list|)
argument_list|,
name|params
argument_list|(
literal|"_version_"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|409
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// try good version added as a field in the doc
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|100
argument_list|)
argument_list|,
literal|"_version_"
argument_list|,
name|version
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
comment|// try good version added as a request parameter
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|200
argument_list|)
argument_list|)
argument_list|,
name|params
argument_list|(
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*_i,*_is"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':100, 'val_is':[10,5,-1,-100,-200]}}"
argument_list|)
expr_stmt|;
comment|// extra field should just be treated as a "set"
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|300
argument_list|)
argument_list|,
literal|"val_i"
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*_i,*_is"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':2, 'val_is':[10,5,-1,-100,-200,-300]}}"
argument_list|)
expr_stmt|;
comment|// a null value should be treated as "remove"
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|400
argument_list|)
argument_list|,
literal|"val_i"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,*_i,*_is"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_is':[10,5,-1,-100,-200,-300,-400]}}"
argument_list|)
expr_stmt|;
name|version
operator|=
name|deleteAndGetVersion
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Currently, there is an implicit _version_=1 for updates (doc must exist).  This is subject to change!
name|version2
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"add"
argument_list|,
operator|-
literal|100
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|409
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_i"
argument_list|,
literal|5
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"val2_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"val2_f"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"val2_d"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"1.0"
argument_list|)
argument_list|,
literal|"val2_l"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,val*"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':5, 'val_is':[1], 'val2_i':1, 'val2_f':1.0, 'val2_d':1.0, 'val2_l':1}}"
argument_list|)
expr_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"-5"
argument_list|)
argument_list|,
literal|"val2_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
operator|-
literal|5
argument_list|)
argument_list|,
literal|"val2_f"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"-5.0"
argument_list|)
argument_list|,
literal|"val2_d"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
operator|-
literal|5
argument_list|)
argument_list|,
literal|"val2_l"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"-5"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,val*"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':5, 'val_is':[-4], 'val2_i':-4, 'val2_f':-4.0, 'val2_d':-4.0, 'val2_l':-4}}"
argument_list|)
expr_stmt|;
name|version
operator|=
name|addAndGetVersion
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"val_is"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"2000000000"
argument_list|)
argument_list|,
literal|"val2_i"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
operator|-
literal|2000000000
argument_list|)
argument_list|,
literal|"val2_f"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"1e+20"
argument_list|)
argument_list|,
literal|"val2_d"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
operator|-
literal|1.2345678901e+100
argument_list|)
argument_list|,
literal|"val2_l"
argument_list|,
name|map
argument_list|(
literal|"inc"
argument_list|,
literal|"5000000000"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|afterUpdate
operator|.
name|call
argument_list|()
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,val*"
argument_list|)
argument_list|,
literal|"=={'doc':{'id':'1', 'val_i':5, 'val_is':[1999999996], 'val2_i':-2000000004, 'val2_f':1.0E20, 'val2_d':-1.2345678901e+100, 'val2_l':4999999996}}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

