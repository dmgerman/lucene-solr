begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
operator|.
name|cache
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|search
operator|.
name|CategoryListIterator
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|AtomicReaderContext
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
name|IndexReader
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
name|IntsRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Category list data maintained in RAM.  *<p>  * Speeds up facets accumulation when more RAM is available.  *<p>  * Note that this will consume more memory: one int (4 bytes) for each category  * of each document.  *<p>  * Note: at the moment this class is insensitive to updates of the index, and,  * in particular, does not make use of Lucene's ability to refresh a single  * segment.  *<p>  * See {@link CategoryListCache#register(CategoryListParams, CategoryListData)}  * and  * {@link CategoryListCache#loadAndRegister(CategoryListParams, IndexReader, TaxonomyReader, FacetIndexingParams)}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CategoryListData
specifier|public
class|class
name|CategoryListData
block|{
comment|// TODO (Facet): experiment with different orders - p-d-c vs. current d-p-c.
DECL|field|docPartitionCategories
specifier|private
specifier|transient
specifier|volatile
name|int
index|[]
index|[]
index|[]
name|docPartitionCategories
decl_stmt|;
comment|/**    * Empty constructor for extensions with modified computation of the data.    */
DECL|method|CategoryListData
specifier|protected
name|CategoryListData
parameter_list|()
block|{   }
comment|/** Compute category list data for caching for faster iteration. */
DECL|method|CategoryListData
name|CategoryListData
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TaxonomyReader
name|taxo
parameter_list|,
name|FacetIndexingParams
name|iparams
parameter_list|,
name|CategoryListParams
name|clp
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
index|[]
index|[]
name|dpf
init|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
index|[]
index|[]
decl_stmt|;
name|int
name|numPartitions
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|taxo
operator|.
name|getSize
argument_list|()
operator|/
operator|(
name|double
operator|)
name|iparams
operator|.
name|getPartitionSize
argument_list|()
argument_list|)
decl_stmt|;
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|part
init|=
literal|0
init|;
name|part
operator|<
name|numPartitions
condition|;
name|part
operator|++
control|)
block|{
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|CategoryListIterator
name|cli
init|=
name|clp
operator|.
name|createCategoryListIterator
argument_list|(
name|part
argument_list|)
decl_stmt|;
if|if
condition|(
name|cli
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
condition|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
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
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
name|cli
operator|.
name|getOrdinals
argument_list|(
name|i
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
if|if
condition|(
name|ordinals
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|doc
init|=
name|i
operator|+
name|context
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
name|dpf
index|[
name|doc
index|]
operator|==
literal|null
condition|)
block|{
name|dpf
index|[
name|doc
index|]
operator|=
operator|new
name|int
index|[
name|numPartitions
index|]
index|[]
expr_stmt|;
block|}
if|if
condition|(
name|dpf
index|[
name|doc
index|]
index|[
name|part
index|]
operator|==
literal|null
condition|)
block|{
name|dpf
index|[
name|doc
index|]
index|[
name|part
index|]
operator|=
operator|new
name|int
index|[
name|ordinals
operator|.
name|length
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ordinals
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|dpf
index|[
name|doc
index|]
index|[
name|part
index|]
index|[
name|j
index|]
operator|=
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
name|docPartitionCategories
operator|=
name|dpf
expr_stmt|;
block|}
comment|/**    * Iterate on the category list data for the specified partition.    */
DECL|method|iterator
specifier|public
name|CategoryListIterator
name|iterator
parameter_list|(
name|int
name|partition
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RAMCategoryListIterator
argument_list|(
name|partition
argument_list|,
name|docPartitionCategories
argument_list|)
return|;
block|}
comment|/** Internal: category list iterator over uncompressed category info in RAM */
DECL|class|RAMCategoryListIterator
specifier|private
specifier|static
class|class
name|RAMCategoryListIterator
implements|implements
name|CategoryListIterator
block|{
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|part
specifier|private
specifier|final
name|int
name|part
decl_stmt|;
DECL|field|dpc
specifier|private
specifier|final
name|int
index|[]
index|[]
index|[]
name|dpc
decl_stmt|;
DECL|method|RAMCategoryListIterator
name|RAMCategoryListIterator
parameter_list|(
name|int
name|part
parameter_list|,
name|int
index|[]
index|[]
index|[]
name|docPartitionCategories
parameter_list|)
block|{
name|this
operator|.
name|part
operator|=
name|part
expr_stmt|;
name|dpc
operator|=
name|docPartitionCategories
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|boolean
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docBase
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
return|return
name|dpc
operator|!=
literal|null
operator|&&
name|dpc
operator|.
name|length
operator|>
name|part
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinals
specifier|public
name|void
name|getOrdinals
parameter_list|(
name|int
name|docID
parameter_list|,
name|IntsRef
name|ints
parameter_list|)
throws|throws
name|IOException
block|{
name|ints
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|docID
operator|+=
name|docBase
expr_stmt|;
if|if
condition|(
name|dpc
operator|.
name|length
operator|>
name|docID
operator|&&
name|dpc
index|[
name|docID
index|]
operator|!=
literal|null
operator|&&
name|dpc
index|[
name|docID
index|]
index|[
name|part
index|]
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ints
operator|.
name|ints
operator|.
name|length
operator|<
name|dpc
index|[
name|docID
index|]
index|[
name|part
index|]
operator|.
name|length
condition|)
block|{
name|ints
operator|.
name|grow
argument_list|(
name|dpc
index|[
name|docID
index|]
index|[
name|part
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|ints
operator|.
name|length
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dpc
index|[
name|docID
index|]
index|[
name|part
index|]
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ints
operator|.
name|ints
index|[
name|ints
operator|.
name|length
operator|++
index|]
operator|=
name|dpc
index|[
name|docID
index|]
index|[
name|part
index|]
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

