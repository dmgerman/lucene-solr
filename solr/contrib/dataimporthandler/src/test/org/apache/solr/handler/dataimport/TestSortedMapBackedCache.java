begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|junit
operator|.
name|Assert
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
DECL|class|TestSortedMapBackedCache
specifier|public
class|class
name|TestSortedMapBackedCache
extends|extends
name|AbstractDIHCacheTestCase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSortedMapBackedCache
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testCacheWithKeyLookup
specifier|public
name|void
name|testCacheWithKeyLookup
parameter_list|()
block|{
name|DIHCache
name|cache
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cache
operator|=
operator|new
name|SortedMapBackedCache
argument_list|()
expr_stmt|;
name|cache
operator|.
name|open
argument_list|(
name|getContext
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|loadData
argument_list|(
name|cache
argument_list|,
name|data
argument_list|,
name|fieldNames
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ControlData
argument_list|>
name|testData
init|=
name|extractDataByKeyLookup
argument_list|(
name|cache
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|compareData
argument_list|(
name|data
argument_list|,
name|testData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|cache
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{       }
block|}
block|}
annotation|@
name|Test
DECL|method|testCacheWithOrderedLookup
specifier|public
name|void
name|testCacheWithOrderedLookup
parameter_list|()
block|{
name|DIHCache
name|cache
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cache
operator|=
operator|new
name|SortedMapBackedCache
argument_list|()
expr_stmt|;
name|cache
operator|.
name|open
argument_list|(
name|getContext
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|loadData
argument_list|(
name|cache
argument_list|,
name|data
argument_list|,
name|fieldNames
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ControlData
argument_list|>
name|testData
init|=
name|extractDataInKeyOrder
argument_list|(
name|cache
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|compareData
argument_list|(
name|data
argument_list|,
name|testData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|cache
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{       }
block|}
block|}
annotation|@
name|Test
DECL|method|testNullKeys
specifier|public
name|void
name|testNullKeys
parameter_list|()
throws|throws
name|Exception
block|{
comment|//A null key should just be ignored, but not throw an exception
name|DIHCache
name|cache
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cache
operator|=
operator|new
name|SortedMapBackedCache
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cacheProps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|cacheProps
operator|.
name|put
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_PRIMARY_KEY
argument_list|,
literal|"a_id"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|open
argument_list|(
name|getContext
argument_list|(
name|cacheProps
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"a_id"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
literal|"bogus"
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|cache
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|cacheIter
init|=
name|cache
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|cacheIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"cache should be empty."
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNull
argument_list|(
name|cache
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|delete
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|cache
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{       }
block|}
block|}
annotation|@
name|Test
DECL|method|testCacheReopensWithUpdate
specifier|public
name|void
name|testCacheReopensWithUpdate
parameter_list|()
block|{
name|DIHCache
name|cache
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|cacheProps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|cacheProps
operator|.
name|put
argument_list|(
name|DIHCacheSupport
operator|.
name|CACHE_PRIMARY_KEY
argument_list|,
literal|"a_id"
argument_list|)
expr_stmt|;
name|cache
operator|=
operator|new
name|SortedMapBackedCache
argument_list|()
expr_stmt|;
name|cache
operator|.
name|open
argument_list|(
name|getContext
argument_list|(
name|cacheProps
argument_list|)
argument_list|)
expr_stmt|;
comment|// We can let the data hit the cache with the fields out of order because
comment|// we've identified the pk up-front.
name|loadData
argument_list|(
name|cache
argument_list|,
name|data
argument_list|,
name|fieldNames
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Close the cache.
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ControlData
argument_list|>
name|newControlData
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Object
index|[]
name|newIdEqualsThree
init|=
literal|null
decl_stmt|;
name|int
name|j
init|=
literal|0
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
name|data
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// We'll be deleting a_id=1 so remove it from the control data.
if|if
condition|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|data
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// We'll be changing "Cookie" to "Carrot" in a_id=3 so change it in the control data.
if|if
condition|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|data
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
argument_list|)
condition|)
block|{
name|newIdEqualsThree
operator|=
operator|new
name|Object
index|[
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|data
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|data
argument_list|,
literal|0
argument_list|,
name|newIdEqualsThree
argument_list|,
literal|0
argument_list|,
name|newIdEqualsThree
operator|.
name|length
argument_list|)
expr_stmt|;
name|newIdEqualsThree
index|[
literal|3
index|]
operator|=
literal|"Carrot"
expr_stmt|;
name|newControlData
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
name|newIdEqualsThree
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Everything else can just be copied over.
else|else
block|{
name|newControlData
operator|.
name|add
argument_list|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|j
operator|++
expr_stmt|;
block|}
comment|// These new rows of data will get added to the cache, so add them to the control data too.
name|Object
index|[]
name|newDataRow1
init|=
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|99
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"Z"
block|,
literal|"Zebra"
block|,
operator|new
name|Float
argument_list|(
literal|99.99
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
decl_stmt|;
name|Object
index|[]
name|newDataRow2
init|=
operator|new
name|Object
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|2
argument_list|)
block|,
operator|new
name|BigDecimal
argument_list|(
name|Math
operator|.
name|PI
argument_list|)
block|,
literal|"B"
block|,
literal|"Ballerina"
block|,
operator|new
name|Float
argument_list|(
literal|2.22
argument_list|)
block|,
name|Feb21_2011
block|,
literal|null
block|}
decl_stmt|;
name|newControlData
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
name|newDataRow1
argument_list|)
argument_list|)
expr_stmt|;
name|newControlData
operator|.
name|add
argument_list|(
operator|new
name|ControlData
argument_list|(
name|newDataRow2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Re-open the cache
name|cache
operator|.
name|open
argument_list|(
name|getContext
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Delete a_id=1 from the cache.
name|cache
operator|.
name|delete
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Because the cache allows duplicates, the only way to update is to
comment|// delete first then add.
name|cache
operator|.
name|delete
argument_list|(
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|add
argument_list|(
name|controlDataToMap
argument_list|(
operator|new
name|ControlData
argument_list|(
name|newIdEqualsThree
argument_list|)
argument_list|,
name|fieldNames
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add this row with a new Primary key.
name|cache
operator|.
name|add
argument_list|(
name|controlDataToMap
argument_list|(
operator|new
name|ControlData
argument_list|(
name|newDataRow1
argument_list|)
argument_list|,
name|fieldNames
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add this row, creating two records in the cache with a_id=2.
name|cache
operator|.
name|add
argument_list|(
name|controlDataToMap
argument_list|(
operator|new
name|ControlData
argument_list|(
name|newDataRow2
argument_list|)
argument_list|,
name|fieldNames
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read the cache back and compare to the newControlData
name|List
argument_list|<
name|ControlData
argument_list|>
name|testData
init|=
name|extractDataInKeyOrder
argument_list|(
name|cache
argument_list|,
name|fieldNames
argument_list|)
decl_stmt|;
name|compareData
argument_list|(
name|newControlData
argument_list|,
name|testData
argument_list|)
expr_stmt|;
comment|// Now try reading the cache read-only.
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|cache
operator|.
name|open
argument_list|(
name|getContext
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testData
operator|=
name|extractDataInKeyOrder
argument_list|(
name|cache
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
name|compareData
argument_list|(
name|newControlData
argument_list|,
name|testData
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exception thrown: "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|cache
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{       }
block|}
block|}
block|}
end_class

end_unit

