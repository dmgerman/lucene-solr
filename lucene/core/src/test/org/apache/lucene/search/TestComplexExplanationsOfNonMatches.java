begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * subclass of TestSimpleExplanations that verifies non matches.  */
end_comment

begin_class
DECL|class|TestComplexExplanationsOfNonMatches
specifier|public
class|class
name|TestComplexExplanationsOfNonMatches
extends|extends
name|TestComplexExplanations
block|{
comment|/**    * Overrides superclass to ignore matches and focus on non-matches    *    * @see CheckHits#checkNoMatchExplanations    */
annotation|@
name|Override
DECL|method|qtest
specifier|public
name|void
name|qtest
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
name|CheckHits
operator|.
name|checkNoMatchExplanations
argument_list|(
name|q
argument_list|,
name|FIELD
argument_list|,
name|searcher
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

