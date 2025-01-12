begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
DECL|class|CloneFieldUpdateProcessorFactoryTest
specifier|public
class|class
name|CloneFieldUpdateProcessorFactoryTest
extends|extends
name|UpdateProcessorTestBase
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
literal|"solrconfig-update-processor-chains.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleClone
specifier|public
name|void
name|testSimpleClone
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|processAdd
argument_list|(
literal|"clone-single"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"source1_s should have stringValue"
argument_list|,
literal|"foo"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dest_s should have stringValue"
argument_list|,
literal|"foo"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiClone
specifier|public
name|void
name|testMultiClone
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|processAdd
argument_list|(
literal|"clone-multi"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source2_s"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"source1_s should have stringValue"
argument_list|,
literal|"foo"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"source2_s should have stringValue"
argument_list|,
literal|"bar"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|dest_s
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArrayClone
specifier|public
name|void
name|testArrayClone
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|processAdd
argument_list|(
literal|"clone-array"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source2_s"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"source1_s should have stringValue"
argument_list|,
literal|"foo"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"source2_s should have stringValue"
argument_list|,
literal|"bar"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|dest_s
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectorClone
specifier|public
name|void
name|testSelectorClone
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|processAdd
argument_list|(
literal|"clone-selector"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source0_s"
argument_list|,
literal|"nope, not me"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source2_s"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"source0_s should have stringValue"
argument_list|,
literal|"nope, not me"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source0_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"source1_s should have stringValue"
argument_list|,
literal|"foo"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"source2_s should have stringValue"
argument_list|,
literal|"bar"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"source2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|dest_s
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dest_s
operator|.
name|contains
argument_list|(
literal|"nope, not me"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleClones
specifier|public
name|void
name|testMultipleClones
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|processAdd
argument_list|(
literal|"multiple-clones"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"category"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"authors"
argument_list|,
literal|"author1"
argument_list|,
literal|"author2"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"editors"
argument_list|,
literal|"ed1"
argument_list|,
literal|"ed2"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"bfriday_price"
argument_list|,
literal|4.00
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"sale_price"
argument_list|,
literal|5.00
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"list_price"
argument_list|,
literal|6.00
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"features"
argument_list|,
literal|"hill"
argument_list|,
literal|"valley"
argument_list|,
literal|"dune"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// the original values should remain
name|assertEquals
argument_list|(
literal|"category should have a value"
argument_list|,
literal|"test"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"category"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|auths
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"authors"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|auths
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|auths
operator|.
name|contains
argument_list|(
literal|"author1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|auths
operator|.
name|contains
argument_list|(
literal|"author2"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|eds
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"editors"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|eds
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|eds
operator|.
name|contains
argument_list|(
literal|"ed1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|eds
operator|.
name|contains
argument_list|(
literal|"ed2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bfriday_price should have a value"
argument_list|,
literal|4.0
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"bfriday_price"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sale_price should have a value"
argument_list|,
literal|5.0
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"sale_price"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"list_price should have a value"
argument_list|,
literal|6.0
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"list_price"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|features
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"features"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|features
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|features
operator|.
name|contains
argument_list|(
literal|"hill"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|features
operator|.
name|contains
argument_list|(
literal|"valley"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|features
operator|.
name|contains
argument_list|(
literal|"dune"
argument_list|)
argument_list|)
expr_stmt|;
comment|// and the copied values shoul be added
name|assertEquals
argument_list|(
literal|"category_s should have a value"
argument_list|,
literal|"test"
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"category_s"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|contribs
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"contributors"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|contribs
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|contribs
operator|.
name|contains
argument_list|(
literal|"author1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|contribs
operator|.
name|contains
argument_list|(
literal|"author2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|contribs
operator|.
name|contains
argument_list|(
literal|"ed1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|contribs
operator|.
name|contains
argument_list|(
literal|"ed2"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|prices
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"all_prices"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|prices
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prices
operator|.
name|contains
argument_list|(
literal|5.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|prices
operator|.
name|contains
argument_list|(
literal|4.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prices
operator|.
name|contains
argument_list|(
literal|6.0
argument_list|)
argument_list|)
expr_stmt|;
comment|// n.b. the field names below imply singularity but that would be achieved with a subsequent
comment|// FirstFieldValueUpdateProcessorFactory (or similar custom class), and not in clone field itself
name|Collection
argument_list|<
name|Object
argument_list|>
name|keyf
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"key_feature"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|keyf
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyf
operator|.
name|contains
argument_list|(
literal|"hill"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyf
operator|.
name|contains
argument_list|(
literal|"valley"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|keyf
operator|.
name|contains
argument_list|(
literal|"dune"
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Object
argument_list|>
name|bestf
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"best_feature"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bestf
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bestf
operator|.
name|contains
argument_list|(
literal|"hill"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bestf
operator|.
name|contains
argument_list|(
literal|"valley"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bestf
operator|.
name|contains
argument_list|(
literal|"dune"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCloneField
specifier|public
name|void
name|testCloneField
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|d
decl_stmt|;
comment|// regardless of chain, all of these checks should be equivalent
for|for
control|(
name|String
name|chain
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"clone-single"
argument_list|,
literal|"clone-single-regex"
argument_list|,
literal|"clone-multi"
argument_list|,
literal|"clone-multi-regex"
argument_list|,
literal|"clone-array"
argument_list|,
literal|"clone-array-regex"
argument_list|,
literal|"clone-selector"
argument_list|,
literal|"clone-selector-regex"
argument_list|)
control|)
block|{
comment|// simple clone
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source0_s"
argument_list|,
literal|"NOT COPIED"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
comment|// append to existing values, preserve boost
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"dest_s"
argument_list|,
literal|"orig1"
argument_list|,
literal|"orig2"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source0_s"
argument_list|,
literal|"NOT COPIED"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"orig1"
argument_list|,
literal|"orig2"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// should be equivalent for any chain matching source1_s and source2_s (but not source0_s)
for|for
control|(
name|String
name|chain
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"clone-multi"
argument_list|,
literal|"clone-multi-regex"
argument_list|,
literal|"clone-array"
argument_list|,
literal|"clone-array-regex"
argument_list|,
literal|"clone-selector"
argument_list|,
literal|"clone-selector-regex"
argument_list|)
control|)
block|{
comment|// simple clone
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source0_s"
argument_list|,
literal|"NOT COPIED"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source2_s"
argument_list|,
literal|"xxx"
argument_list|,
literal|999
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"xxx"
argument_list|,
literal|999
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|,
literal|"xxx"
argument_list|,
literal|999
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
comment|// append to existing values
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"dest_s"
argument_list|,
literal|"orig1"
argument_list|,
literal|"orig2"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source0_s"
argument_list|,
literal|"NOT COPIED"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source2_s"
argument_list|,
literal|"xxx"
argument_list|,
literal|999
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"xxx"
argument_list|,
literal|999
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"orig1"
argument_list|,
literal|"orig2"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|,
literal|"xxx"
argument_list|,
literal|999
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// any chain that copies source1_s to dest_s should be equivalent for these assertions
for|for
control|(
name|String
name|chain
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"clone-simple-regex-syntax"
argument_list|,
literal|"clone-single"
argument_list|,
literal|"clone-single-regex"
argument_list|,
literal|"clone-multi"
argument_list|,
literal|"clone-multi-regex"
argument_list|,
literal|"clone-array"
argument_list|,
literal|"clone-array-regex"
argument_list|,
literal|"clone-selector"
argument_list|,
literal|"clone-selector-regex"
argument_list|)
control|)
block|{
comment|// simple clone
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
comment|// append to existing values, preserve boost
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|field
argument_list|(
literal|"dest_s"
argument_list|,
literal|"orig1"
argument_list|,
literal|"orig2"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"source1_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chain
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"source1_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"orig1"
argument_list|,
literal|"orig2"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"dest_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCloneFieldRegexReplaceAll
specifier|public
name|void
name|testCloneFieldRegexReplaceAll
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"clone-regex-replaceall"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"foo_x2_s"
argument_list|,
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"foo_x3_x7_s"
argument_list|,
literal|"xyz"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"123456789"
argument_list|,
literal|""
argument_list|,
literal|42
argument_list|,
literal|"abcd"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"foo_y2_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"xyz"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"foo_y3_y7_s"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCloneFieldExample
specifier|public
name|void
name|testCloneFieldExample
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|d
decl_stmt|;
comment|// test example from the javadocs
name|d
operator|=
name|processAdd
argument_list|(
literal|"multiple-clones"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"category"
argument_list|,
literal|"misc"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"authors"
argument_list|,
literal|"Isaac Asimov"
argument_list|,
literal|"John Brunner"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"editors"
argument_list|,
literal|"John W. Campbell"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"store1_price"
argument_list|,
literal|87
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"store2_price"
argument_list|,
literal|78
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"store3_price"
argument_list|,
operator|(
name|Object
operator|)
literal|null
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"list_price"
argument_list|,
literal|1000
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"features"
argument_list|,
literal|"Pages!"
argument_list|,
literal|"Binding!"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"feat_of_strengths"
argument_list|,
literal|"Pullups"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"misc"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"category"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"misc"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"category_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"Isaac Asimov"
argument_list|,
literal|"John Brunner"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"authors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"John W. Campbell"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"editors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"Isaac Asimov"
argument_list|,
literal|"John Brunner"
argument_list|,
literal|"John W. Campbell"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"contributors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|87
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"store1_price"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|78
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"store2_price"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"list_price"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|87
argument_list|,
literal|78
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"all_prices"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"Pages!"
argument_list|,
literal|"Binding!"
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValues
argument_list|(
literal|"key_feature"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pullups"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"key_feat_of_strength"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCloneCombinations
specifier|public
name|void
name|testCloneCombinations
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|d
decl_stmt|;
comment|// maxChars
name|d
operator|=
name|processAdd
argument_list|(
literal|"clone-max-chars"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field1"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tex"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"toField"
argument_list|)
argument_list|)
expr_stmt|;
comment|// move
name|d
operator|=
name|processAdd
argument_list|(
literal|"clone-move"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field1"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"toField"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|d
operator|.
name|containsKey
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// replace
name|d
operator|=
name|processAdd
argument_list|(
literal|"clone-replace"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"toField"
argument_list|,
literal|"IGNORED"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field1"
argument_list|,
literal|"text"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"toField"
argument_list|)
argument_list|)
expr_stmt|;
comment|// append
name|d
operator|=
name|processAdd
argument_list|(
literal|"clone-append"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"toField"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field1"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field2"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ccc"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"field2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaa; bbb; ccc"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"toField"
argument_list|)
argument_list|)
expr_stmt|;
comment|// first value
name|d
operator|=
name|processAdd
argument_list|(
literal|"clone-first"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1111"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field0"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field1"
argument_list|,
literal|"bbb"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"field2"
argument_list|,
literal|"ccc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"toField"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

