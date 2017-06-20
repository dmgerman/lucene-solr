begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
name|DocValues
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
name|NumericDocValues
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
name|SortedDocValues
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
name|search
operator|.
name|DocIdSetIterator
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
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uninverting
operator|.
name|FieldCacheImpl
import|;
end_import

begin_comment
comment|/** @lucene.internal  * Porting helper... may be removed if it offers no value in the future.  */
end_comment

begin_class
DECL|class|FieldUtil
specifier|public
class|class
name|FieldUtil
block|{
comment|/** Simpler method that creates a request context and looks up the field for you */
DECL|method|getSortedDocValues
specifier|public
specifier|static
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|QueryContext
name|qContext
init|=
name|QueryContext
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
name|getSortedDocValues
argument_list|(
name|qContext
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getSortedDocValues
specifier|public
specifier|static
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|QueryContext
name|context
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|si
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getSlowAtomicReader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// if (!field.hasDocValues()&& (field.getType() instanceof StrField || field.getType() instanceof TextField)) {
comment|// }
return|return
name|si
operator|==
literal|null
condition|?
name|DocValues
operator|.
name|emptySorted
argument_list|()
else|:
name|si
return|;
block|}
DECL|method|getSortedSetDocValues
specifier|public
specifier|static
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|QueryContext
name|context
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetDocValues
name|si
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getSlowAtomicReader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|si
operator|==
literal|null
condition|?
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
else|:
name|si
return|;
block|}
DECL|method|getNumericDocValues
specifier|public
specifier|static
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|QueryContext
name|context
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrIndexSearcher
name|searcher
init|=
name|context
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|NumericDocValues
name|si
init|=
name|searcher
operator|.
name|getSlowAtomicReader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|si
operator|==
literal|null
condition|?
name|DocValues
operator|.
name|emptyNumeric
argument_list|()
else|:
name|si
return|;
block|}
comment|/** The following ord visitors and wrappers are a work in progress and experimental    *  @lucene.experimental */
annotation|@
name|FunctionalInterface
DECL|interface|OrdFunc
specifier|public
interface|interface
name|OrdFunc
block|{
DECL|method|handleOrd
name|void
name|handleOrd
parameter_list|(
name|int
name|docid
parameter_list|,
name|int
name|ord
parameter_list|)
function_decl|;
comment|// TODO: throw exception?
block|}
DECL|method|isFieldCache
specifier|public
specifier|static
name|boolean
name|isFieldCache
parameter_list|(
name|SortedDocValues
name|singleDv
parameter_list|)
block|{
return|return
name|singleDv
operator|instanceof
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
return|;
block|}
DECL|method|visitOrds
specifier|public
specifier|static
name|void
name|visitOrds
parameter_list|(
name|SortedDocValues
name|singleDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|,
name|OrdFunc
name|ordFunc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
if|if
condition|(
name|singleDv
operator|instanceof
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
condition|)
block|{
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
name|fc
init|=
operator|(
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
operator|)
name|singleDv
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|ordFunc
operator|.
name|handleOrd
argument_list|(
name|doc
argument_list|,
name|fc
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
while|while
condition|(
operator|(
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|singleDv
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|ordFunc
operator|.
name|handleOrd
argument_list|(
name|doc
argument_list|,
name|singleDv
operator|.
name|ordValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: optionally pass in missingOrd?
block|}
block|}
block|}
block|}
DECL|method|getOrdValues
specifier|public
specifier|static
name|OrdValues
name|getOrdValues
parameter_list|(
name|SortedDocValues
name|singleDv
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|)
block|{
if|if
condition|(
name|singleDv
operator|instanceof
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
condition|)
block|{
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
name|fc
init|=
operator|(
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
operator|)
name|singleDv
decl_stmt|;
return|return
operator|new
name|FCOrdValues
argument_list|(
name|fc
argument_list|,
name|disi
argument_list|)
return|;
block|}
return|return
operator|new
name|DVOrdValues
argument_list|(
name|singleDv
argument_list|,
name|disi
argument_list|)
return|;
block|}
DECL|class|OrdValues
specifier|public
specifier|static
specifier|abstract
class|class
name|OrdValues
extends|extends
name|SortedDocValues
block|{
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|ord
name|int
name|ord
decl_stmt|;
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|()
block|{
return|return
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|abstract
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
comment|// TODO
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|FCOrdValues
specifier|public
specifier|static
class|class
name|FCOrdValues
extends|extends
name|OrdValues
block|{
DECL|field|vals
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
name|vals
decl_stmt|;
DECL|field|disi
name|DocIdSetIterator
name|disi
decl_stmt|;
DECL|method|FCOrdValues
specifier|public
name|FCOrdValues
parameter_list|(
name|FieldCacheImpl
operator|.
name|SortedDocValuesImpl
operator|.
name|Iter
name|iter
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|)
block|{
name|this
operator|.
name|vals
operator|=
name|iter
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|disi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|NO_MORE_DOCS
return|;
name|ord
operator|=
name|vals
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// todo: loop until a hit?
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|ordValue
specifier|public
name|int
name|ordValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|DVOrdValues
specifier|public
specifier|static
class|class
name|DVOrdValues
extends|extends
name|OrdValues
block|{
DECL|field|vals
name|SortedDocValues
name|vals
decl_stmt|;
DECL|field|disi
name|DocIdSetIterator
name|disi
decl_stmt|;
DECL|field|valDoc
name|int
name|valDoc
decl_stmt|;
DECL|method|DVOrdValues
specifier|public
name|DVOrdValues
parameter_list|(
name|SortedDocValues
name|vals
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|)
block|{
name|this
operator|.
name|vals
operator|=
name|vals
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|disi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// todo - use skipping when appropriate
name|doc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|NO_MORE_DOCS
return|;
name|boolean
name|match
init|=
name|vals
operator|.
name|advanceExact
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
condition|)
block|{
name|ord
operator|=
name|vals
operator|.
name|ordValue
argument_list|()
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|ordValue
specifier|public
name|int
name|ordValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

