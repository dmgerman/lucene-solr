begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PriorityQueue
import|;
end_import

begin_class
DECL|class|PhraseQueue
specifier|final
class|class
name|PhraseQueue
extends|extends
name|PriorityQueue
argument_list|<
name|PhrasePositions
argument_list|>
block|{
DECL|method|PhraseQueue
name|PhraseQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|PhrasePositions
name|pp1
parameter_list|,
name|PhrasePositions
name|pp2
parameter_list|)
block|{
if|if
condition|(
name|pp1
operator|.
name|doc
operator|==
name|pp2
operator|.
name|doc
condition|)
if|if
condition|(
name|pp1
operator|.
name|position
operator|==
name|pp2
operator|.
name|position
condition|)
comment|// same doc and pp.position, so decide by actual term positions.
comment|// rely on: pp.position == tp.position - offset.
return|return
name|pp1
operator|.
name|offset
operator|<
name|pp2
operator|.
name|offset
return|;
else|else
return|return
name|pp1
operator|.
name|position
operator|<
name|pp2
operator|.
name|position
return|;
else|else
return|return
name|pp1
operator|.
name|doc
operator|<
name|pp2
operator|.
name|doc
return|;
block|}
block|}
end_class

end_unit

