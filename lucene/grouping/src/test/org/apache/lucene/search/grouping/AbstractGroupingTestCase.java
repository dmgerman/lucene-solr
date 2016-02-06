begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
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
name|util
operator|.
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  * Base class for grouping related tests.  */
end_comment

begin_comment
comment|// TODO (MvG) : The grouping tests contain a lot of code duplication. Try to move the common code to this class..
end_comment

begin_class
DECL|class|AbstractGroupingTestCase
specifier|public
specifier|abstract
class|class
name|AbstractGroupingTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|method|generateRandomNonEmptyString
specifier|protected
name|String
name|generateRandomNonEmptyString
parameter_list|()
block|{
name|String
name|randomValue
decl_stmt|;
do|do
block|{
comment|// B/c of DV based impl we can't see the difference between an empty string and a null value.
comment|// For that reason we don't generate empty string
comment|// groups.
name|randomValue
operator|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
comment|//randomValue = _TestUtil.randomSimpleString(random());
block|}
do|while
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|randomValue
argument_list|)
condition|)
do|;
return|return
name|randomValue
return|;
block|}
block|}
end_class

end_unit

