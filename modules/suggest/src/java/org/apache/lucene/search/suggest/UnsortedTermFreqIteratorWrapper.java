begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|TermFreqIterator
import|;
end_import

begin_comment
comment|/**  * This wrapper buffers the incoming elements and makes sure they are in  * random order.  */
end_comment

begin_class
DECL|class|UnsortedTermFreqIteratorWrapper
specifier|public
class|class
name|UnsortedTermFreqIteratorWrapper
extends|extends
name|BufferingTermFreqIteratorWrapper
block|{
DECL|method|UnsortedTermFreqIteratorWrapper
specifier|public
name|UnsortedTermFreqIteratorWrapper
parameter_list|(
name|TermFreqIterator
name|source
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

