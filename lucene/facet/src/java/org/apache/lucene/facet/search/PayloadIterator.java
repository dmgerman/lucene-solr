begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|DocsAndPositionsEnum
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
name|Fields
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
name|Term
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A utility class for iterating through a posting list of a given term and  * retrieving the payload of the first position in every document. For  * efficiency, this class does not check if documents passed to  * {@link #getPayload(int)} are deleted, since it is usually used to iterate on  * payloads of documents that matched a query. If you need to skip over deleted  * documents, you should do so before calling {@link #getPayload(int)}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|PayloadIterator
specifier|public
class|class
name|PayloadIterator
block|{
DECL|field|data
specifier|protected
name|BytesRef
name|data
decl_stmt|;
DECL|field|reuseTE
specifier|private
name|TermsEnum
name|reuseTE
decl_stmt|;
DECL|field|currentDPE
specifier|private
name|DocsAndPositionsEnum
name|currentDPE
decl_stmt|;
DECL|field|hasMore
specifier|private
name|boolean
name|hasMore
decl_stmt|;
DECL|field|curDocID
DECL|field|curDocBase
specifier|private
name|int
name|curDocID
decl_stmt|,
name|curDocBase
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|Iterator
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|method|PayloadIterator
specifier|public
name|PayloadIterator
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|leaves
operator|=
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
DECL|method|nextSegment
specifier|private
name|void
name|nextSegment
parameter_list|()
throws|throws
name|IOException
block|{
name|hasMore
operator|=
literal|false
expr_stmt|;
while|while
condition|(
name|leaves
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AtomicReaderContext
name|ctx
init|=
name|leaves
operator|.
name|next
argument_list|()
decl_stmt|;
name|curDocBase
operator|=
name|ctx
operator|.
name|docBase
expr_stmt|;
name|Fields
name|fields
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|reuseTE
operator|=
name|terms
operator|.
name|iterator
argument_list|(
name|reuseTE
argument_list|)
expr_stmt|;
if|if
condition|(
name|reuseTE
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// this class is usually used to iterate on whatever a Query matched
comment|// if it didn't match deleted documents, we won't receive them. if it
comment|// did, we should iterate on them too, therefore we pass liveDocs=null
name|currentDPE
operator|=
name|reuseTE
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|currentDPE
argument_list|,
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentDPE
operator|!=
literal|null
operator|&&
operator|(
name|curDocID
operator|=
name|currentDPE
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
name|hasMore
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**    * Initialize the iterator. Should be done before the first call to    * {@link #getPayload(int)}. Returns {@code false} if no category list is    * found, or the category list has no documents.    */
DECL|method|init
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|nextSegment
argument_list|()
expr_stmt|;
return|return
name|hasMore
return|;
block|}
comment|/**    * Returns the {@link BytesRef payload} of the given document, or {@code null}    * if the document does not exist, there are no more documents in the posting    * list, or the document exists but has not payload. You should call    * {@link #init()} before the first call to this method.    */
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasMore
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// re-basing docId->localDocID is done fewer times than currentDoc->globalDoc
name|int
name|localDocID
init|=
name|docID
operator|-
name|curDocBase
decl_stmt|;
if|if
condition|(
name|curDocID
operator|>
name|localDocID
condition|)
block|{
comment|// document does not exist
return|return
literal|null
return|;
block|}
if|if
condition|(
name|curDocID
operator|<
name|localDocID
condition|)
block|{
comment|// look for the document either in that segment, or others
while|while
condition|(
name|hasMore
operator|&&
operator|(
name|curDocID
operator|=
name|currentDPE
operator|.
name|advance
argument_list|(
name|localDocID
argument_list|)
operator|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|nextSegment
argument_list|()
expr_stmt|;
comment|// also updates curDocID
name|localDocID
operator|=
name|docID
operator|-
name|curDocBase
expr_stmt|;
comment|// nextSegment advances to nextDoc, so check if we still need to advance
if|if
condition|(
name|curDocID
operator|>=
name|localDocID
condition|)
block|{
break|break;
block|}
block|}
comment|// we break from the above loop when:
comment|// 1. we iterated over all segments (hasMore=false)
comment|// 2. current segment advanced to a doc, either requested or higher
if|if
condition|(
operator|!
name|hasMore
operator|||
name|curDocID
operator|!=
name|localDocID
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|// we're on the document
assert|assert
name|currentDPE
operator|.
name|freq
argument_list|()
operator|==
literal|1
operator|:
literal|"expecting freq=1 (got "
operator|+
name|currentDPE
operator|.
name|freq
argument_list|()
operator|+
literal|") term="
operator|+
name|term
operator|+
literal|" doc="
operator|+
operator|(
name|curDocID
operator|+
name|curDocBase
operator|)
assert|;
name|int
name|pos
init|=
name|currentDPE
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
assert|assert
name|pos
operator|!=
operator|-
literal|1
operator|:
literal|"no positions for term="
operator|+
name|term
operator|+
literal|" doc="
operator|+
operator|(
name|curDocID
operator|+
name|curDocBase
operator|)
assert|;
return|return
name|currentDPE
operator|.
name|getPayload
argument_list|()
return|;
block|}
block|}
end_class

end_unit

