begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package

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
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocument
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @author ryan  */
end_comment

begin_class
DECL|class|SolrDocumentTest
specifier|public
class|class
name|SolrDocumentTest
extends|extends
name|TestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|Float
name|fval
init|=
operator|new
name|Float
argument_list|(
literal|10.01f
argument_list|)
decl_stmt|;
name|Boolean
name|bval
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
name|String
name|sval
init|=
literal|"12qwaszx"
decl_stmt|;
comment|// Set up a simple document
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"b"
argument_list|,
name|bval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"s"
argument_list|,
name|sval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// again, but something else
comment|// make sure we can pull values out of it
name|assertEquals
argument_list|(
name|fval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"xxxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"xxxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|keys
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|keys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[b, f, s]"
argument_list|,
name|keys
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// set field replaced existing values:
name|doc
operator|.
name|setField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"n"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"n"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now remove some fields
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|doc
operator|.
name|removeFields
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|doc
operator|.
name|removeFields
argument_list|(
literal|"asdgsadgas"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocumentBoosts
specifier|public
name|void
name|testDocumentBoosts
parameter_list|()
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setBoost
argument_list|(
literal|"aaa"
argument_list|,
literal|10.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.0f
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setBoost
argument_list|(
literal|"aaa"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getBoost
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnsupportedStuff
specifier|public
name|void
name|testUnsupportedStuff
parameter_list|()
block|{
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|containsValue
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|entrySet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|putAll
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|values
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"aaa"
argument_list|,
literal|"bbb"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|remove
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddCollections
specifier|public
name|void
name|testAddCollections
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|c0
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"ccc"
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|c0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c0
operator|.
name|size
argument_list|()
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Same thing with an array
name|Object
index|[]
name|arr
init|=
operator|new
name|Object
index|[]
block|{
literal|"aaa"
block|,
literal|"aaa"
block|,
literal|"aaa"
block|,
literal|10
block|,
literal|'b'
block|}
decl_stmt|;
name|doc
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|c0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|arr
operator|.
name|length
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrderedDistinctFields
specifier|public
name|void
name|testOrderedDistinctFields
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|c0
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"ccc"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setKeepDuplicateFieldValues
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|c0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[aaa, bbb, ccc]"
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicate
specifier|public
name|void
name|testDuplicate
parameter_list|()
block|{
name|Float
name|fval0
init|=
operator|new
name|Float
argument_list|(
literal|10.01f
argument_list|)
decl_stmt|;
name|Float
name|fval1
init|=
operator|new
name|Float
argument_list|(
literal|11.01f
argument_list|)
decl_stmt|;
name|Float
name|fval2
init|=
operator|new
name|Float
argument_list|(
literal|12.01f
argument_list|)
decl_stmt|;
comment|// Set up a simple document
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval0
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval2
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|(
literal|3
operator|*
literal|5
operator|)
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|doc
operator|.
name|setKeepDuplicateFieldValues
argument_list|(
literal|"f"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"can't change distinct for an existing field"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|doc
operator|.
name|removeFields
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setKeepDuplicateFieldValues
argument_list|(
literal|"f"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval0
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval2
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|(
literal|3
operator|)
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

