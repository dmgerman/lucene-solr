begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|ArrayList
import|;
end_import

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
name|util
operator|.
name|Set
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
name|ArrayUtil
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
name|BytesRefArray
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
name|BytesRefBuilder
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
name|Counter
import|;
end_import

begin_comment
comment|/**  * This wrapper buffers incoming elements.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BufferedInputIterator
specifier|public
class|class
name|BufferedInputIterator
implements|implements
name|InputIterator
block|{
comment|// TODO keep this for now
comment|/** buffered term entries */
DECL|field|entries
specifier|protected
name|BytesRefArray
name|entries
init|=
operator|new
name|BytesRefArray
argument_list|(
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
decl_stmt|;
comment|/** buffered payload entries */
DECL|field|payloads
specifier|protected
name|BytesRefArray
name|payloads
init|=
operator|new
name|BytesRefArray
argument_list|(
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
decl_stmt|;
comment|/** buffered context set entries */
DECL|field|contextSets
specifier|protected
name|List
argument_list|<
name|Set
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|contextSets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** current buffer position */
DECL|field|curPos
specifier|protected
name|int
name|curPos
init|=
operator|-
literal|1
decl_stmt|;
comment|/** buffered weights, parallel with {@link #entries} */
DECL|field|freqs
specifier|protected
name|long
index|[]
name|freqs
init|=
operator|new
name|long
index|[
literal|1
index|]
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|payloadSpare
specifier|private
specifier|final
name|BytesRefBuilder
name|payloadSpare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|hasPayloads
specifier|private
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|field|hasContexts
specifier|private
specifier|final
name|boolean
name|hasContexts
decl_stmt|;
comment|/** Creates a new iterator, buffering entries from the specified iterator */
DECL|method|BufferedInputIterator
specifier|public
name|BufferedInputIterator
parameter_list|(
name|InputIterator
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|spare
decl_stmt|;
name|int
name|freqIndex
init|=
literal|0
decl_stmt|;
name|hasPayloads
operator|=
name|source
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|hasContexts
operator|=
name|source
operator|.
name|hasContexts
argument_list|()
expr_stmt|;
while|while
condition|(
operator|(
name|spare
operator|=
name|source
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|entries
operator|.
name|append
argument_list|(
name|spare
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|payloads
operator|.
name|append
argument_list|(
name|source
operator|.
name|payload
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasContexts
condition|)
block|{
name|contextSets
operator|.
name|add
argument_list|(
name|source
operator|.
name|contexts
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|freqIndex
operator|>=
name|freqs
operator|.
name|length
condition|)
block|{
name|freqs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|freqs
argument_list|,
name|freqs
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|freqs
index|[
name|freqIndex
operator|++
index|]
operator|=
name|source
operator|.
name|weight
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|freqs
index|[
name|curPos
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|++
name|curPos
operator|<
name|entries
operator|.
name|size
argument_list|()
condition|)
block|{
name|entries
operator|.
name|get
argument_list|(
name|spare
argument_list|,
name|curPos
argument_list|)
expr_stmt|;
return|return
name|spare
operator|.
name|get
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
if|if
condition|(
name|hasPayloads
operator|&&
name|curPos
operator|<
name|payloads
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|payloads
operator|.
name|get
argument_list|(
name|payloadSpare
argument_list|,
name|curPos
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
if|if
condition|(
name|hasContexts
operator|&&
name|curPos
operator|<
name|contextSets
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|contextSets
operator|.
name|get
argument_list|(
name|curPos
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
name|hasContexts
return|;
block|}
block|}
end_class

end_unit

