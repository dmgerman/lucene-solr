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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|SegmentTermVector
class|class
name|SegmentTermVector
implements|implements
name|TermFreqVector
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|terms
specifier|private
name|String
name|terms
index|[]
decl_stmt|;
DECL|field|termFreqs
specifier|private
name|int
name|termFreqs
index|[]
decl_stmt|;
DECL|method|SegmentTermVector
name|SegmentTermVector
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|terms
index|[]
parameter_list|,
name|int
name|termFreqs
index|[]
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
name|this
operator|.
name|termFreqs
operator|=
name|termFreqs
expr_stmt|;
block|}
comment|/**    *     * @return The number of the field this vector is associated with    */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|termFreqs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|terms
operator|==
literal|null
condition|?
literal|0
else|:
name|terms
operator|.
name|length
return|;
block|}
DECL|method|getTerms
specifier|public
name|String
index|[]
name|getTerms
parameter_list|()
block|{
return|return
name|terms
return|;
block|}
DECL|method|getTermFrequencies
specifier|public
name|int
index|[]
name|getTermFrequencies
parameter_list|()
block|{
return|return
name|termFreqs
return|;
block|}
DECL|method|indexOf
specifier|public
name|int
name|indexOf
parameter_list|(
name|String
name|termText
parameter_list|)
block|{
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
name|int
name|res
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|terms
argument_list|,
name|termText
argument_list|)
decl_stmt|;
return|return
name|res
operator|>=
literal|0
condition|?
name|res
else|:
operator|-
literal|1
return|;
block|}
DECL|method|indexesOf
specifier|public
name|int
index|[]
name|indexesOf
parameter_list|(
name|String
index|[]
name|termNumbers
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// TODO: there must be a more efficient way of doing this.
comment|//       At least, we could advance the lower bound of the terms array
comment|//       as we find valid indexes. Also, it might be possible to leverage
comment|//       this even more by starting in the middle of the termNumbers array
comment|//       and thus dividing the terms array maybe in half with each found index.
name|int
name|res
index|[]
init|=
operator|new
name|int
index|[
name|len
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|indexOf
argument_list|(
name|termNumbers
index|[
name|start
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

