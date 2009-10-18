begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Compares {@link org.apache.lucene.index.TermVectorEntry}s first by frequency and then by  * the term (case-sensitive)  *  **/
end_comment

begin_class
DECL|class|TermVectorEntryFreqSortedComparator
specifier|public
class|class
name|TermVectorEntryFreqSortedComparator
implements|implements
name|Comparator
argument_list|<
name|TermVectorEntry
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|TermVectorEntry
name|entry
parameter_list|,
name|TermVectorEntry
name|entry1
parameter_list|)
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
name|result
operator|=
name|entry1
operator|.
name|getFrequency
argument_list|()
operator|-
name|entry
operator|.
name|getFrequency
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|entry
operator|.
name|getTerm
argument_list|()
operator|.
name|compareTo
argument_list|(
name|entry1
operator|.
name|getTerm
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|entry
operator|.
name|getField
argument_list|()
operator|.
name|compareTo
argument_list|(
name|entry1
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

