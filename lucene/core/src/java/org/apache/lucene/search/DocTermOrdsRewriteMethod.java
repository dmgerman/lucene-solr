begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Bits
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
name|LongBitSet
import|;
end_import

begin_comment
comment|/**  * Rewrites MultiTermQueries into a filter, using DocTermOrds for term enumeration.  *<p>  * This can be used to perform these queries against an unindexed docvalues field.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocTermOrdsRewriteMethod
specifier|public
specifier|final
class|class
name|DocTermOrdsRewriteMethod
extends|extends
name|MultiTermQuery
operator|.
name|RewriteMethod
block|{
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
block|{
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|MultiTermQueryDocTermOrdsWrapperFilter
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|MultiTermQueryDocTermOrdsWrapperFilter
specifier|static
class|class
name|MultiTermQueryDocTermOrdsWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|query
specifier|protected
specifier|final
name|MultiTermQuery
name|query
decl_stmt|;
comment|/**      * Wrap a {@link MultiTermQuery} as a Filter.      */
DECL|method|MultiTermQueryDocTermOrdsWrapperFilter
specifier|protected
name|MultiTermQueryDocTermOrdsWrapperFilter
parameter_list|(
name|MultiTermQuery
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// query.toString should be ok for the filter, too, if the query boost is 1.0f
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MultiTermQueryDocTermOrdsWrapperFilter
operator|)
name|o
operator|)
operator|.
name|query
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns the field name for this query */
DECL|method|getField
specifier|public
specifier|final
name|String
name|getField
parameter_list|()
block|{
return|return
name|query
operator|.
name|getField
argument_list|()
return|;
block|}
comment|/**      * Returns a DocIdSet with documents that should be permitted in search      * results.      */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedSetDocValues
name|docTermOrds
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocTermOrds
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|query
operator|.
name|field
argument_list|)
decl_stmt|;
comment|// Cannot use FixedBitSet because we require long index (ord):
specifier|final
name|LongBitSet
name|termSet
init|=
operator|new
name|LongBitSet
argument_list|(
name|docTermOrds
operator|.
name|getValueCount
argument_list|()
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|query
operator|.
name|getTermsEnum
argument_list|(
operator|new
name|Terms
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
block|{
return|return
name|docTermOrds
operator|.
name|termsEnum
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocCount
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
if|if
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// fill into a bitset
do|do
block|{
name|termSet
operator|.
name|set
argument_list|(
name|termsEnum
operator|.
name|ord
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
do|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|FieldCacheDocIdSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
specifier|final
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|ArrayIndexOutOfBoundsException
block|{
name|docTermOrds
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
comment|// TODO: we could track max bit set and early terminate (since they come in sorted order)
while|while
condition|(
operator|(
name|ord
operator|=
name|docTermOrds
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
if|if
condition|(
name|termSet
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|877
return|;
block|}
block|}
end_class

end_unit

