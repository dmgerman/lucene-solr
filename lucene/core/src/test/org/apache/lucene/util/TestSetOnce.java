begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|SetOnce
operator|.
name|AlreadySetException
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
DECL|class|TestSetOnce
specifier|public
class|class
name|TestSetOnce
extends|extends
name|LuceneTestCase
block|{
DECL|class|SetOnceThread
specifier|private
specifier|static
specifier|final
class|class
name|SetOnceThread
extends|extends
name|Thread
block|{
DECL|field|set
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|set
decl_stmt|;
DECL|field|success
name|boolean
name|success
init|=
literal|false
decl_stmt|;
DECL|field|RAND
specifier|final
name|Random
name|RAND
decl_stmt|;
DECL|method|SetOnceThread
specifier|public
name|SetOnceThread
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|RAND
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|sleep
argument_list|(
name|RAND
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
comment|// sleep for a short time
name|set
operator|.
name|set
argument_list|(
operator|new
name|Integer
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO: change exception type
comment|// expected.
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testEmptyCtor
specifier|public
name|void
name|testEmptyCtor
parameter_list|()
throws|throws
name|Exception
block|{
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|set
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AlreadySetException
operator|.
name|class
argument_list|)
DECL|method|testSettingCtor
specifier|public
name|void
name|testSettingCtor
parameter_list|()
throws|throws
name|Exception
block|{
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|(
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|set
operator|.
name|get
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|set
argument_list|(
operator|new
name|Integer
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AlreadySetException
operator|.
name|class
argument_list|)
DECL|method|testSetOnce
specifier|public
name|void
name|testSetOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
name|set
operator|.
name|set
argument_list|(
operator|new
name|Integer
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|set
operator|.
name|get
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|set
argument_list|(
operator|new
name|Integer
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetMultiThreaded
specifier|public
name|void
name|testSetMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SetOnce
argument_list|<
name|Integer
argument_list|>
name|set
init|=
operator|new
name|SetOnce
argument_list|<>
argument_list|()
decl_stmt|;
name|SetOnceThread
index|[]
name|threads
init|=
operator|new
name|SetOnceThread
index|[
literal|10
index|]
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|SetOnceThread
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"t-"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|set
operator|=
name|set
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|SetOnceThread
name|t
range|:
name|threads
control|)
block|{
if|if
condition|(
name|t
operator|.
name|success
condition|)
block|{
name|int
name|expectedVal
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"thread "
operator|+
name|t
operator|.
name|getName
argument_list|()
argument_list|,
name|expectedVal
argument_list|,
name|t
operator|.
name|set
operator|.
name|get
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

