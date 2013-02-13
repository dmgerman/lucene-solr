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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A per-document set of presorted byte[] values.  *<p>  * Per-Document values in a SortedDocValues are deduplicated, dereferenced,  * and sorted into a dictionary of unique values. A pointer to the  * dictionary value (ordinal) can be retrieved for each document. Ordinals  * are dense and in increasing sorted order.  */
end_comment

begin_class
DECL|class|SortedSetDocValues
specifier|public
specifier|abstract
class|class
name|SortedSetDocValues
block|{
comment|/** Sole constructor. (For invocation by subclass     * constructors, typically implicit.) */
DECL|method|SortedSetDocValues
specifier|protected
name|SortedSetDocValues
parameter_list|()
block|{}
DECL|field|NO_MORE_ORDS
specifier|public
specifier|static
specifier|final
name|long
name|NO_MORE_ORDS
init|=
operator|-
literal|1
decl_stmt|;
comment|/**     * Returns the next ordinal for the current document (previously    * set by {@link #setDocument(int)}.    * @return next ordinal for the document, or {@link #NO_MORE_ORDS}.     *         ordinals are dense, start at 0, then increment by 1 for     *         the next value in sorted order.     */
DECL|method|nextOrd
specifier|public
specifier|abstract
name|long
name|nextOrd
parameter_list|()
function_decl|;
comment|/**     * Sets iteration to the specified docID     * @param docID document ID     */
DECL|method|setDocument
specifier|public
specifier|abstract
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Retrieves the value for the specified ordinal.    * @param ord ordinal to lookup    * @param result will be populated with the ordinal's value    * @see #nextOrd    */
DECL|method|lookupOrd
specifier|public
specifier|abstract
name|void
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
function_decl|;
comment|/**    * Returns the number of unique values.    * @return number of unique values in this SortedDocValues. This is    *         also equivalent to one plus the maximum ordinal.    */
DECL|method|getValueCount
specifier|public
specifier|abstract
name|long
name|getValueCount
parameter_list|()
function_decl|;
comment|/** An empty SortedDocValues which returns {@link #NO_MORE_ORDS} for every document */
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|SortedSetDocValues
name|EMPTY
init|=
operator|new
name|SortedSetDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
return|return
name|NO_MORE_ORDS
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|/** If {@code key} exists, returns its ordinal, else    *  returns {@code -insertionPoint-1}, like {@code    *  Arrays.binarySearch}.    *    *  @param key Key to look up    **/
DECL|method|lookupTerm
specifier|public
name|long
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|long
name|low
init|=
literal|0
decl_stmt|;
name|long
name|high
init|=
name|getValueCount
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|long
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|lookupOrd
argument_list|(
name|mid
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|int
name|cmp
init|=
name|spare
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
return|return
name|mid
return|;
comment|// key found
block|}
block|}
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
comment|// key not found.
block|}
block|}
end_class

end_unit

