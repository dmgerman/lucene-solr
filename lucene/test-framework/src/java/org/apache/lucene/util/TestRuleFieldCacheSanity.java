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
name|FieldCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestRuleFieldCacheSanity
specifier|public
class|class
name|TestRuleFieldCacheSanity
implements|implements
name|TestRule
block|{
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|s
parameter_list|,
specifier|final
name|Description
name|d
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
name|Throwable
name|problem
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// calling assertSaneFieldCaches here isn't as useful as having test
comment|// classes call it directly from the scope where the index readers
comment|// are used, because they could be gc'ed just before this tearDown
comment|// method is called.
comment|//
comment|// But it's better then nothing.
comment|//
comment|// If you are testing functionality that you know for a fact
comment|// "violates" FieldCache sanity, then you should either explicitly
comment|// call purgeFieldCache at the end of your test method, or refactor
comment|// your Test class so that the inconsistent FieldCache usages are
comment|// isolated in distinct test methods
name|LuceneTestCase
operator|.
name|assertSaneFieldCaches
argument_list|(
name|d
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|problem
operator|=
name|t
expr_stmt|;
block|}
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|purgeAllCaches
argument_list|()
expr_stmt|;
if|if
condition|(
name|problem
operator|!=
literal|null
condition|)
block|{
name|Rethrow
operator|.
name|rethrow
argument_list|(
name|problem
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

