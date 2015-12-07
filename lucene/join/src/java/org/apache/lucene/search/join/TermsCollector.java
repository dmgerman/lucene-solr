begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BinaryDocValues
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
name|SortedSetDocValues
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRefHash
import|;
end_import

begin_comment
comment|/**  * A collector that collects all terms from a specified field matching the query.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermsCollector
specifier|abstract
class|class
name|TermsCollector
parameter_list|<
name|DV
parameter_list|>
extends|extends
name|DocValuesTermsCollector
argument_list|<
name|DV
argument_list|>
block|{
DECL|method|TermsCollector
name|TermsCollector
parameter_list|(
name|Function
argument_list|<
name|DV
argument_list|>
name|docValuesCall
parameter_list|)
block|{
name|super
argument_list|(
name|docValuesCall
argument_list|)
expr_stmt|;
block|}
DECL|field|collectorTerms
specifier|final
name|BytesRefHash
name|collectorTerms
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
DECL|method|getCollectorTerms
specifier|public
name|BytesRefHash
name|getCollectorTerms
parameter_list|()
block|{
return|return
name|collectorTerms
return|;
block|}
comment|/**    * Chooses the right {@link TermsCollector} implementation.    *    * @param field                     The field to collect terms for    * @param multipleValuesPerDocument Whether the field to collect terms for has multiple values per document.    * @return a {@link TermsCollector} instance    */
DECL|method|create
specifier|static
name|TermsCollector
argument_list|<
name|?
argument_list|>
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|multipleValuesPerDocument
parameter_list|)
block|{
return|return
name|multipleValuesPerDocument
condition|?
operator|new
name|MV
argument_list|(
name|sortedSetDocValues
argument_list|(
name|field
argument_list|)
argument_list|)
else|:
operator|new
name|SV
argument_list|(
name|binaryDocValues
argument_list|(
name|field
argument_list|)
argument_list|)
return|;
block|}
comment|// impl that works with multiple values per document
DECL|class|MV
specifier|static
class|class
name|MV
extends|extends
name|TermsCollector
argument_list|<
name|SortedSetDocValues
argument_list|>
block|{
DECL|method|MV
name|MV
parameter_list|(
name|Function
argument_list|<
name|SortedSetDocValues
argument_list|>
name|docValuesCall
parameter_list|)
block|{
name|super
argument_list|(
name|docValuesCall
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|ord
decl_stmt|;
name|docValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|docValues
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|docValues
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|collectorTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// impl that works with single value per document
DECL|class|SV
specifier|static
class|class
name|SV
extends|extends
name|TermsCollector
argument_list|<
name|BinaryDocValues
argument_list|>
block|{
DECL|method|SV
name|SV
parameter_list|(
name|Function
argument_list|<
name|BinaryDocValues
argument_list|>
name|docValuesCall
parameter_list|)
block|{
name|super
argument_list|(
name|docValuesCall
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|term
init|=
name|docValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|collectorTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

