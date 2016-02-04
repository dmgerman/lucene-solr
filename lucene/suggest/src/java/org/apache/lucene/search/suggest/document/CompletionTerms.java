begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest.document
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
operator|.
name|document
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FilterLeafReader
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
name|index
operator|.
name|Terms
import|;
end_import

begin_comment
comment|/**  * Wrapped {@link org.apache.lucene.index.Terms}  * used by {@link SuggestField} and {@link ContextSuggestField}  * to access corresponding suggester and their attributes  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompletionTerms
specifier|public
specifier|final
class|class
name|CompletionTerms
extends|extends
name|FilterLeafReader
operator|.
name|FilterTerms
block|{
DECL|field|reader
specifier|private
specifier|final
name|CompletionsTermsReader
name|reader
decl_stmt|;
comment|/**    * Creates a completionTerms based on {@link CompletionsTermsReader}    */
DECL|method|CompletionTerms
name|CompletionTerms
parameter_list|(
name|Terms
name|in
parameter_list|,
name|CompletionsTermsReader
name|reader
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
comment|/**    * Returns the type of FST, either {@link SuggestField#TYPE} or    * {@link ContextSuggestField#TYPE}    */
DECL|method|getType
specifier|public
name|byte
name|getType
parameter_list|()
block|{
return|return
operator|(
name|reader
operator|!=
literal|null
operator|)
condition|?
name|reader
operator|.
name|type
else|:
name|SuggestField
operator|.
name|TYPE
return|;
block|}
comment|/**    * Returns the minimum weight of all entries in the weighted FST    */
DECL|method|getMinWeight
specifier|public
name|long
name|getMinWeight
parameter_list|()
block|{
return|return
operator|(
name|reader
operator|!=
literal|null
operator|)
condition|?
name|reader
operator|.
name|minWeight
else|:
literal|0
return|;
block|}
comment|/**    * Returns the maximum weight of all entries in the weighted FST    */
DECL|method|getMaxWeight
specifier|public
name|long
name|getMaxWeight
parameter_list|()
block|{
return|return
operator|(
name|reader
operator|!=
literal|null
operator|)
condition|?
name|reader
operator|.
name|maxWeight
else|:
literal|0
return|;
block|}
comment|/**    * Returns a {@link NRTSuggester} for the field    * or<code>null</code> if no FST    * was indexed for this field    */
DECL|method|suggester
specifier|public
name|NRTSuggester
name|suggester
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|reader
operator|!=
literal|null
operator|)
condition|?
name|reader
operator|.
name|suggester
argument_list|()
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

