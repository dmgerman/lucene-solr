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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|analysis
operator|.
name|Analyzer
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
name|ControlledRealTimeReopenThread
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|Query
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/** Class that tracks changes to a delegated  *  IndexWriter, used by {@link  *  ControlledRealTimeReopenThread} to ensure specific  *  changes are visible.   Create this class (passing your  *  IndexWriter), and then pass this class to {@link  *  ControlledRealTimeReopenThread}.  *  Be sure to make all changes via the  *  TrackingIndexWriter, otherwise {@link  *  ControlledRealTimeReopenThread} won't know about the changes.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|TrackingIndexWriter
specifier|public
class|class
name|TrackingIndexWriter
block|{
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|indexingGen
specifier|private
specifier|final
name|AtomicLong
name|indexingGen
init|=
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/** Create a {@code TrackingIndexWriter} wrapping the    *  provided {@link IndexWriter}. */
DECL|method|TrackingIndexWriter
specifier|public
name|TrackingIndexWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
comment|/** Calls {@link    *  IndexWriter#updateDocument(Term,Iterable)} and    *  returns the generation that reflects this change. */
DECL|method|updateDocument
specifier|public
name|long
name|updateDocument
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|t
argument_list|,
name|d
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link    *  IndexWriter#updateDocuments(Term,Iterable)} and returns    *  the generation that reflects this change. */
DECL|method|updateDocuments
specifier|public
name|long
name|updateDocuments
parameter_list|(
name|Term
name|t
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocuments
argument_list|(
name|t
argument_list|,
name|docs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#deleteDocuments(Term...)} and    *  returns the generation that reflects this change. */
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#deleteDocuments(Term...)} and    *  returns the generation that reflects this change. */
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Term
modifier|...
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|terms
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#deleteDocuments(Query...)} and    *  returns the generation that reflects this change. */
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|q
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#deleteDocuments(Query...)}    *  and returns the generation that reflects this change. */
DECL|method|deleteDocuments
specifier|public
name|long
name|deleteDocuments
parameter_list|(
name|Query
modifier|...
name|queries
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|queries
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#deleteAll} and returns the    *  generation that reflects this change. */
DECL|method|deleteAll
specifier|public
name|long
name|deleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#addDocument(Iterable)}    *  and returns the generation that reflects this change. */
DECL|method|addDocument
specifier|public
name|long
name|addDocument
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#addDocuments(Iterable)} and    *  returns the generation that reflects this change. */
DECL|method|addDocuments
specifier|public
name|long
name|addDocuments
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Iterable
argument_list|<
name|?
extends|extends
name|IndexableField
argument_list|>
argument_list|>
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#addIndexes(Directory...)} and    *  returns the generation that reflects this change. */
DECL|method|addIndexes
specifier|public
name|long
name|addIndexes
parameter_list|(
name|Directory
modifier|...
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Calls {@link IndexWriter#addIndexes(CodecReader...)}    *  and returns the generation that reflects this change. */
DECL|method|addIndexes
specifier|public
name|long
name|addIndexes
parameter_list|(
name|CodecReader
modifier|...
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|readers
argument_list|)
expr_stmt|;
comment|// Return gen as of when indexing finished:
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Return the current generation being indexed. */
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Return the wrapped {@link IndexWriter}. */
DECL|method|getIndexWriter
specifier|public
name|IndexWriter
name|getIndexWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
comment|/** Return and increment current gen.    *    * @lucene.internal */
DECL|method|getAndIncrementGeneration
specifier|public
name|long
name|getAndIncrementGeneration
parameter_list|()
block|{
return|return
name|indexingGen
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
comment|/** Cals {@link    *  IndexWriter#tryDeleteDocument(IndexReader,int)} and    *  returns the generation that reflects this change. */
DECL|method|tryDeleteDocument
specifier|public
name|long
name|tryDeleteDocument
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|.
name|tryDeleteDocument
argument_list|(
name|reader
argument_list|,
name|docID
argument_list|)
condition|)
block|{
return|return
name|indexingGen
operator|.
name|get
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

