begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|Iterator
import|;
end_import

begin_interface
DECL|interface|TermFreqIterator
specifier|public
interface|interface
name|TermFreqIterator
extends|extends
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
function_decl|;
DECL|class|TermFreqIteratorWrapper
specifier|public
specifier|static
class|class
name|TermFreqIteratorWrapper
implements|implements
name|TermFreqIterator
block|{
DECL|field|wrapped
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|wrapped
decl_stmt|;
DECL|method|TermFreqIteratorWrapper
specifier|public
name|TermFreqIteratorWrapper
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|next
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_interface

end_unit

