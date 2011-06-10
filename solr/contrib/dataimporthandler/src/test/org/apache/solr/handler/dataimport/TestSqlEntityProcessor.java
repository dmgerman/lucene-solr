begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<p>  * Test for SqlEntityProcessor  *</p>  *  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestSqlEntityProcessor
specifier|public
class|class
name|TestSqlEntityProcessor
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|local
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
name|local
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testSingleBatch
specifier|public
name|void
name|testSingleBatch
parameter_list|()
block|{
name|SqlEntityProcessor
name|sep
init|=
operator|new
name|SqlEntityProcessor
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
name|getRows
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ea
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
name|ea
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
literal|"SELECT * FROM A"
argument_list|)
expr_stmt|;
name|Context
name|c
init|=
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|getDs
argument_list|(
name|rows
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
literal|null
argument_list|,
name|ea
argument_list|)
decl_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
name|sep
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTranformer
specifier|public
name|void
name|testTranformer
parameter_list|()
block|{
name|EntityProcessor
name|sep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|SqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
name|getRows
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ea
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
name|ea
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
literal|"SELECT * FROM A"
argument_list|)
expr_stmt|;
name|ea
operator|.
name|put
argument_list|(
literal|"transformer"
argument_list|,
name|T
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|getDs
argument_list|(
name|rows
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
literal|null
argument_list|,
name|ea
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rs
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|r
operator|=
name|sep
operator|.
name|nextRow
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rs
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"T"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTranformerWithReflection
specifier|public
name|void
name|testTranformerWithReflection
parameter_list|()
block|{
name|EntityProcessor
name|sep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|SqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
name|getRows
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ea
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
name|ea
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
literal|"SELECT * FROM A"
argument_list|)
expr_stmt|;
name|ea
operator|.
name|put
argument_list|(
literal|"transformer"
argument_list|,
name|T3
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|getDs
argument_list|(
name|rows
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
literal|null
argument_list|,
name|ea
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rs
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|r
operator|=
name|sep
operator|.
name|nextRow
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|rs
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"T3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTranformerList
specifier|public
name|void
name|testTranformerList
parameter_list|()
block|{
name|EntityProcessor
name|sep
init|=
operator|new
name|EntityProcessorWrapper
argument_list|(
operator|new
name|SqlEntityProcessor
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
name|getRows
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|VariableResolverImpl
name|vr
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ea
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
name|ea
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
literal|"SELECT * FROM A"
argument_list|)
expr_stmt|;
name|ea
operator|.
name|put
argument_list|(
literal|"transformer"
argument_list|,
name|T2
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sep
operator|.
name|init
argument_list|(
name|getContext
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
name|getDs
argument_list|(
name|rows
argument_list|)
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
literal|null
argument_list|,
name|ea
argument_list|)
argument_list|)
expr_stmt|;
name|local
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|r
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|r
operator|=
name|sep
operator|.
name|nextRow
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
break|break;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
name|int
operator|)
name|local
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|getRows
specifier|private
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getRows
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"value"
argument_list|,
literal|"The value is "
operator|+
name|i
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
return|return
name|rows
return|;
block|}
DECL|method|getDs
specifier|private
specifier|static
name|DataSource
argument_list|<
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|getDs
parameter_list|(
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
parameter_list|)
block|{
return|return
operator|new
name|DataSource
argument_list|<
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getData
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
name|rows
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|,
name|Properties
name|initProps
parameter_list|)
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
block|}
return|;
block|}
DECL|class|T
specifier|public
specifier|static
class|class
name|T
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|aRow
operator|.
name|put
argument_list|(
literal|"T"
argument_list|,
literal|"Class T"
argument_list|)
expr_stmt|;
return|return
name|aRow
return|;
block|}
block|}
DECL|class|T3
specifier|public
specifier|static
class|class
name|T3
block|{
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|)
block|{
name|aRow
operator|.
name|put
argument_list|(
literal|"T3"
argument_list|,
literal|"T3 class"
argument_list|)
expr_stmt|;
return|return
name|aRow
return|;
block|}
block|}
DECL|class|T2
specifier|public
specifier|static
class|class
name|T2
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|Integer
name|count
init|=
name|local
operator|.
name|get
argument_list|()
decl_stmt|;
name|local
operator|.
name|set
argument_list|(
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|aRow
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|aRow
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
block|}
block|}
end_class

end_unit

