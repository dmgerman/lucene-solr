begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TwoPhaseCommitTool
operator|.
name|TwoPhaseCommitWrapper
import|;
end_import

begin_class
DECL|class|TestTwoPhaseCommitTool
specifier|public
class|class
name|TestTwoPhaseCommitTool
extends|extends
name|LuceneTestCase
block|{
DECL|class|TwoPhaseCommitImpl
specifier|private
specifier|static
class|class
name|TwoPhaseCommitImpl
implements|implements
name|TwoPhaseCommit
block|{
DECL|field|commitCalled
specifier|static
name|boolean
name|commitCalled
init|=
literal|false
decl_stmt|;
DECL|field|failOnPrepare
specifier|final
name|boolean
name|failOnPrepare
decl_stmt|;
DECL|field|failOnCommit
specifier|final
name|boolean
name|failOnCommit
decl_stmt|;
DECL|field|failOnRollback
specifier|final
name|boolean
name|failOnRollback
decl_stmt|;
DECL|field|rollbackCalled
name|boolean
name|rollbackCalled
init|=
literal|false
decl_stmt|;
DECL|field|prepareCommitData
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|prepareCommitData
init|=
literal|null
decl_stmt|;
DECL|field|commitData
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
init|=
literal|null
decl_stmt|;
DECL|method|TwoPhaseCommitImpl
specifier|public
name|TwoPhaseCommitImpl
parameter_list|(
name|boolean
name|failOnPrepare
parameter_list|,
name|boolean
name|failOnCommit
parameter_list|,
name|boolean
name|failOnRollback
parameter_list|)
block|{
name|this
operator|.
name|failOnPrepare
operator|=
name|failOnPrepare
expr_stmt|;
name|this
operator|.
name|failOnCommit
operator|=
name|failOnCommit
expr_stmt|;
name|this
operator|.
name|failOnRollback
operator|=
name|failOnRollback
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|prepareCommit
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareCommit
specifier|public
name|void
name|prepareCommit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|prepareCommitData
operator|=
name|commitData
expr_stmt|;
name|assertFalse
argument_list|(
literal|"commit should not have been called before all prepareCommit were"
argument_list|,
name|commitCalled
argument_list|)
expr_stmt|;
if|if
condition|(
name|failOnPrepare
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"failOnPrepare"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|commit
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|commitData
operator|=
name|commitData
expr_stmt|;
name|commitCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|failOnCommit
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failOnCommit"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IOException
block|{
name|rollbackCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|failOnRollback
condition|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
literal|"failOnRollback"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|TwoPhaseCommitImpl
operator|.
name|commitCalled
operator|=
literal|false
expr_stmt|;
comment|// reset count before every test
block|}
DECL|method|testPrepareThenCommit
specifier|public
name|void
name|testPrepareThenCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that prepareCommit() is called on all objects before commit()
name|TwoPhaseCommitImpl
index|[]
name|objects
init|=
operator|new
name|TwoPhaseCommitImpl
index|[
literal|2
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
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|objects
index|[
name|i
index|]
operator|=
operator|new
name|TwoPhaseCommitImpl
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// following call will fail if commit() is called before all prepare() were
name|TwoPhaseCommitTool
operator|.
name|execute
argument_list|(
name|objects
argument_list|)
expr_stmt|;
block|}
DECL|method|testRollback
specifier|public
name|void
name|testRollback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that rollback is called if failure occurs at any stage
name|int
name|numObjects
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|+
literal|3
decl_stmt|;
comment|// between [3, 10]
name|TwoPhaseCommitImpl
index|[]
name|objects
init|=
operator|new
name|TwoPhaseCommitImpl
index|[
name|numObjects
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
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|failOnPrepare
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// we should not hit failures on commit usually
name|boolean
name|failOnCommit
init|=
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.05
decl_stmt|;
name|boolean
name|railOnRollback
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|objects
index|[
name|i
index|]
operator|=
operator|new
name|TwoPhaseCommitImpl
argument_list|(
name|failOnPrepare
argument_list|,
name|failOnCommit
argument_list|,
name|railOnRollback
argument_list|)
expr_stmt|;
block|}
name|boolean
name|anyFailure
init|=
literal|false
decl_stmt|;
try|try
block|{
name|TwoPhaseCommitTool
operator|.
name|execute
argument_list|(
name|objects
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|anyFailure
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|anyFailure
condition|)
block|{
comment|// if any failure happened, ensure that rollback was called on all.
for|for
control|(
name|TwoPhaseCommitImpl
name|tpc
range|:
name|objects
control|)
block|{
name|assertTrue
argument_list|(
literal|"rollback was not called while a failure occurred during the 2-phase commit"
argument_list|,
name|tpc
operator|.
name|rollbackCalled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testWrapper
specifier|public
name|void
name|testWrapper
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that TwoPhaseCommitWrapper delegates prepare/commit w/ commitData
name|TwoPhaseCommitImpl
name|impl
init|=
operator|new
name|TwoPhaseCommitImpl
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|commitData
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
name|TwoPhaseCommitWrapper
name|wrapper
init|=
operator|new
name|TwoPhaseCommitWrapper
argument_list|(
name|impl
argument_list|,
name|commitData
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|commitData
argument_list|,
name|impl
operator|.
name|prepareCommitData
argument_list|)
expr_stmt|;
comment|// wrapper should ignore passed commitData
name|wrapper
operator|.
name|prepareCommit
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
expr_stmt|;
name|assertSame
argument_list|(
name|commitData
argument_list|,
name|impl
operator|.
name|prepareCommitData
argument_list|)
expr_stmt|;
name|wrapper
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|commitData
argument_list|,
name|impl
operator|.
name|commitData
argument_list|)
expr_stmt|;
comment|// wrapper should ignore passed commitData
name|wrapper
operator|.
name|commit
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
expr_stmt|;
name|assertSame
argument_list|(
name|commitData
argument_list|,
name|impl
operator|.
name|commitData
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullTPCs
specifier|public
name|void
name|testNullTPCs
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numObjects
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|3
decl_stmt|;
comment|// between [3, 6]
name|TwoPhaseCommit
index|[]
name|tpcs
init|=
operator|new
name|TwoPhaseCommit
index|[
name|numObjects
index|]
decl_stmt|;
name|boolean
name|setNull
init|=
literal|false
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
name|tpcs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|isNull
init|=
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
literal|0.3
decl_stmt|;
if|if
condition|(
name|isNull
condition|)
block|{
name|setNull
operator|=
literal|true
expr_stmt|;
name|tpcs
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|tpcs
index|[
name|i
index|]
operator|=
operator|new
name|TwoPhaseCommitImpl
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|setNull
condition|)
block|{
comment|// none of the TPCs were picked to be null, pick one at random
name|int
name|idx
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|numObjects
argument_list|)
decl_stmt|;
name|tpcs
index|[
name|idx
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|// following call would fail if TPCTool won't handle null TPCs properly
name|TwoPhaseCommitTool
operator|.
name|execute
argument_list|(
name|tpcs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

