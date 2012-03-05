begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|List
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
name|CompositeReader
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
name|AtomicReader
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
name|IndexReaderContext
import|;
end_import

begin_comment
comment|/**  * Common util methods for dealing with {@link IndexReader}s.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|ReaderUtil
specifier|public
specifier|final
class|class
name|ReaderUtil
block|{
DECL|method|ReaderUtil
specifier|private
name|ReaderUtil
parameter_list|()
block|{}
comment|// no instance
DECL|class|Slice
specifier|public
specifier|static
class|class
name|Slice
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|Slice
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Slice
index|[
literal|0
index|]
decl_stmt|;
DECL|field|start
specifier|public
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|length
specifier|public
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|readerIndex
specifier|public
specifier|final
name|int
name|readerIndex
decl_stmt|;
DECL|method|Slice
specifier|public
name|Slice
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|readerIndex
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|readerIndex
operator|=
name|readerIndex
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
return|return
literal|"slice start="
operator|+
name|start
operator|+
literal|" length="
operator|+
name|length
operator|+
literal|" readerIndex="
operator|+
name|readerIndex
return|;
block|}
block|}
comment|/**    * Gathers sub-readers from reader into a List.  See    * {@link Gather} for are more general way to gather    * whatever you need to, per reader.    *    * @lucene.experimental    *     * @param allSubReaders    * @param reader    */
DECL|method|gatherSubReaders
specifier|public
specifier|static
name|void
name|gatherSubReaders
parameter_list|(
specifier|final
name|List
argument_list|<
name|AtomicReader
argument_list|>
name|allSubReaders
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
try|try
block|{
operator|new
name|Gather
argument_list|(
name|reader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicReader
name|r
parameter_list|)
block|{
name|allSubReaders
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// won't happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/** Recursively visits all sub-readers of a reader.  You    *  should subclass this and override the add method to    *  gather what you need.    *    * @lucene.experimental */
DECL|class|Gather
specifier|public
specifier|static
specifier|abstract
class|class
name|Gather
block|{
DECL|field|topReader
specifier|private
specifier|final
name|IndexReader
name|topReader
decl_stmt|;
DECL|method|Gather
specifier|public
name|Gather
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|topReader
operator|=
name|r
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|int
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|run
argument_list|(
literal|0
argument_list|,
name|topReader
argument_list|)
return|;
block|}
DECL|method|run
specifier|public
name|int
name|run
parameter_list|(
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|run
argument_list|(
name|docBase
argument_list|,
name|topReader
argument_list|)
return|;
block|}
DECL|method|run
specifier|private
name|int
name|run
parameter_list|(
name|int
name|base
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|instanceof
name|AtomicReader
condition|)
block|{
comment|// atomic reader
name|add
argument_list|(
name|base
argument_list|,
operator|(
name|AtomicReader
operator|)
name|reader
argument_list|)
expr_stmt|;
name|base
operator|+=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|reader
operator|instanceof
name|CompositeReader
operator|:
literal|"must be a composite reader"
assert|;
name|IndexReader
index|[]
name|subReaders
init|=
operator|(
operator|(
name|CompositeReader
operator|)
name|reader
operator|)
operator|.
name|getSequentialSubReaders
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|base
operator|=
name|run
argument_list|(
name|base
argument_list|,
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|base
return|;
block|}
DECL|method|add
specifier|protected
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicReader
name|r
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Walks up the reader tree and return the given context's top level reader    * context, or in other words the reader tree's root context.    */
DECL|method|getTopLevelContext
specifier|public
specifier|static
name|IndexReaderContext
name|getTopLevelContext
parameter_list|(
name|IndexReaderContext
name|context
parameter_list|)
block|{
while|while
condition|(
name|context
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|context
operator|=
name|context
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|context
return|;
block|}
comment|/**    * Returns index of the searcher/reader for document<code>n</code> in the    * array used to construct this searcher/reader.    */
DECL|method|subIndex
specifier|public
specifier|static
name|int
name|subIndex
parameter_list|(
name|int
name|n
parameter_list|,
name|int
index|[]
name|docStarts
parameter_list|)
block|{
comment|// find
comment|// searcher/reader for doc n:
name|int
name|size
init|=
name|docStarts
operator|.
name|length
decl_stmt|;
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|size
operator|-
literal|1
decl_stmt|;
comment|// for first element less than n, return its index
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|docStarts
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|size
operator|&&
name|docStarts
index|[
name|mid
operator|+
literal|1
index|]
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
comment|/**    * Returns index of the searcher/reader for document<code>n</code> in the    * array used to construct this searcher/reader.    */
DECL|method|subIndex
specifier|public
specifier|static
name|int
name|subIndex
parameter_list|(
name|int
name|n
parameter_list|,
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
block|{
comment|// find
comment|// searcher/reader for doc n:
name|int
name|size
init|=
name|leaves
operator|.
name|length
decl_stmt|;
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|size
operator|-
literal|1
decl_stmt|;
comment|// for first element less than n, return its index
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|leaves
index|[
name|mid
index|]
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|size
operator|&&
name|leaves
index|[
name|mid
operator|+
literal|1
index|]
operator|.
name|docBase
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
block|}
end_class

end_unit

