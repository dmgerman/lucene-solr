begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|LeafReaderContext
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
name|Scorer
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
name|SimpleCollector
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
name|FixedBitSet
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DocSetCollector
specifier|public
class|class
name|DocSetCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|pos
name|int
name|pos
init|=
literal|0
decl_stmt|;
DECL|field|bits
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|smallSetSize
specifier|final
name|int
name|smallSetSize
decl_stmt|;
DECL|field|base
name|int
name|base
decl_stmt|;
comment|// in case there aren't that many hits, we may not want a very sparse
comment|// bit array.  Optimistically collect the first few docs in an array
comment|// in case there are only a few.
DECL|field|scratch
specifier|final
name|int
index|[]
name|scratch
decl_stmt|;
DECL|method|DocSetCollector
specifier|public
name|DocSetCollector
parameter_list|(
name|int
name|smallSetSize
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|smallSetSize
operator|=
name|smallSetSize
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|scratch
operator|=
operator|new
name|int
index|[
name|smallSetSize
index|]
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
name|doc
operator|+=
name|base
expr_stmt|;
comment|// optimistically collect the first docs in an array
comment|// in case the total number will be small enough to represent
comment|// as a small set like SortedIntDocSet instead...
comment|// Storing in this array will be quicker to convert
comment|// than scanning through a potentially huge bit vector.
comment|// FUTURE: when search methods all start returning docs in order, maybe
comment|// we could have a ListDocSet() and use the collected array directly.
if|if
condition|(
name|pos
operator|<
name|scratch
operator|.
name|length
condition|)
block|{
name|scratch
index|[
name|pos
index|]
operator|=
name|doc
expr_stmt|;
block|}
else|else
block|{
comment|// this conditional could be removed if BitSet was preallocated, but that
comment|// would take up more memory, and add more GC time...
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
DECL|method|getDocSet
specifier|public
name|DocSet
name|getDocSet
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<=
name|scratch
operator|.
name|length
condition|)
block|{
comment|// assumes docs were collected in sorted order!
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|scratch
argument_list|,
name|pos
argument_list|)
return|;
block|}
else|else
block|{
comment|// set the bits for ids that were collected in the array
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scratch
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|bits
operator|.
name|set
argument_list|(
name|scratch
index|[
name|i
index|]
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocSet
argument_list|(
name|bits
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|base
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
block|}
end_class

end_unit

