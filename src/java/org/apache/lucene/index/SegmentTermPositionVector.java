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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|SegmentTermPositionVector
specifier|public
class|class
name|SegmentTermPositionVector
extends|extends
name|SegmentTermVector
implements|implements
name|TermPositionVector
block|{
DECL|field|positions
specifier|protected
name|int
index|[]
index|[]
name|positions
decl_stmt|;
DECL|field|offsets
specifier|protected
name|TermVectorOffsetInfo
index|[]
index|[]
name|offsets
decl_stmt|;
DECL|field|EMPTY_TERM_POS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|EMPTY_TERM_POS
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|method|SegmentTermPositionVector
specifier|public
name|SegmentTermPositionVector
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
parameter_list|,
name|int
index|[]
index|[]
name|positions
parameter_list|,
name|TermVectorOffsetInfo
index|[]
index|[]
name|offsets
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|terms
argument_list|,
name|termFreqs
argument_list|)
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
block|}
comment|/**    * Returns an array of TermVectorOffsetInfo in which the term is found.    *    * @param index The position in the array to get the offsets from    * @return An array of TermVectorOffsetInfo objects or the empty list    * @see org.apache.lucene.analysis.Token    */
DECL|method|getOffsets
specifier|public
name|TermVectorOffsetInfo
index|[]
name|getOffsets
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|TermVectorOffsetInfo
index|[]
name|result
init|=
name|TermVectorOffsetInfo
operator|.
name|EMPTY_OFFSET_INFO
decl_stmt|;
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|offsets
operator|.
name|length
condition|)
block|{
name|result
operator|=
name|offsets
index|[
name|index
index|]
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Returns an array of positions in which the term is found.    * Terms are identified by the index at which its number appears in the    * term String array obtained from the<code>indexOf</code> method.    */
DECL|method|getTermPositions
specifier|public
name|int
index|[]
name|getTermPositions
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|int
index|[]
name|result
init|=
name|EMPTY_TERM_POS
decl_stmt|;
if|if
condition|(
name|positions
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|positions
operator|.
name|length
condition|)
block|{
name|result
operator|=
name|positions
index|[
name|index
index|]
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

