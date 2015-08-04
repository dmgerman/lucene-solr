begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SuppressCodecs
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
name|AbstractSolrTestCase
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

begin_comment
comment|/**  * Split out from SortByFunctionTest due to codec support limitations for SortedSetSelector  *  * @see SortByFunctionTest  **/
end_comment

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Memory"
block|,
literal|"SimpleText"
block|}
argument_list|)
comment|// see TestSortedSetSelector
DECL|class|TestSortByMinMaxFunction
specifier|public
class|class
name|TestSortByMinMaxFunction
extends|extends
name|SortByFunctionTest
block|{
annotation|@
name|Override
DECL|method|getFieldFunctionClausesToTest
specifier|public
name|String
index|[]
name|getFieldFunctionClausesToTest
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"primary_tl1"
block|,
literal|"field(primary_tl1)"
block|,
literal|"field(multi_l_dv,max)"
block|,
literal|"field(multi_l_dv,min)"
block|}
return|;
block|}
block|}
end_class

end_unit

