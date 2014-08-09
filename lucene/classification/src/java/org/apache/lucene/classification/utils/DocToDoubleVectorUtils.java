begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * utility class for converting Lucene {@link org.apache.lucene.document.Document}s to<code>Double</code> vectors.  */
end_comment

begin_class
DECL|class|DocToDoubleVectorUtils
specifier|public
class|class
name|DocToDoubleVectorUtils
block|{
DECL|method|DocToDoubleVectorUtils
specifier|private
name|DocToDoubleVectorUtils
parameter_list|()
block|{
comment|// no public constructors
block|}
comment|/**    * create a sparse<code>Double</code> vector given doc and field term vectors using local frequency of the terms in the doc    * @param docTerms term vectors for a given document    * @param fieldTerms field term vectors    * @return a sparse vector of<code>Double</code>s as an array    * @throws IOException in case accessing the underlying index fails    */
DECL|method|toSparseLocalFreqDoubleArray
specifier|public
specifier|static
name|Double
index|[]
name|toSparseLocalFreqDoubleArray
parameter_list|(
name|Terms
name|docTerms
parameter_list|,
name|Terms
name|fieldTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|fieldTermsEnum
init|=
name|fieldTerms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Double
index|[]
name|freqVector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|docTerms
operator|!=
literal|null
operator|&&
name|fieldTerms
operator|.
name|size
argument_list|()
operator|>
operator|-
literal|1
condition|)
block|{
name|freqVector
operator|=
operator|new
name|Double
index|[
operator|(
name|int
operator|)
name|fieldTerms
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|TermsEnum
name|docTermsEnum
init|=
name|docTerms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|fieldTermsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
operator|.
name|SeekStatus
name|seekStatus
init|=
name|docTermsEnum
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|seekStatus
operator|.
name|equals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
argument_list|)
condition|)
block|{
name|docTermsEnum
operator|=
name|docTerms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seekStatus
operator|.
name|equals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|)
condition|)
block|{
name|long
name|termFreqLocal
init|=
name|docTermsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
comment|// the total number of occurrences of this term in the given document
name|freqVector
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|termFreqLocal
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|freqVector
index|[
name|i
index|]
operator|=
literal|0d
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|freqVector
return|;
block|}
comment|/**    * create a dense<code>Double</code> vector given doc and field term vectors using local frequency of the terms in the doc    * @param docTerms term vectors for a given document    * @return a dense vector of<code>Double</code>s as an array    * @throws IOException in case accessing the underlying index fails    */
DECL|method|toDenseLocalFreqDoubleArray
specifier|public
specifier|static
name|Double
index|[]
name|toDenseLocalFreqDoubleArray
parameter_list|(
name|Terms
name|docTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|Double
index|[]
name|freqVector
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|docTerms
operator|!=
literal|null
condition|)
block|{
name|freqVector
operator|=
operator|new
name|Double
index|[
operator|(
name|int
operator|)
name|docTerms
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|TermsEnum
name|docTermsEnum
init|=
name|docTerms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|docTermsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|long
name|termFreqLocal
init|=
name|docTermsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
comment|// the total number of occurrences of this term in the given document
name|freqVector
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|termFreqLocal
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|freqVector
return|;
block|}
block|}
end_class

end_unit

