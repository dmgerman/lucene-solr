begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
import|;
end_import

begin_class
DECL|class|TestDocIDMerger
specifier|public
class|class
name|TestDocIDMerger
extends|extends
name|LuceneTestCase
block|{
DECL|class|TestSubUnsorted
specifier|private
specifier|static
class|class
name|TestSubUnsorted
extends|extends
name|DocIDMerger
operator|.
name|Sub
block|{
DECL|field|docID
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|valueStart
specifier|final
name|int
name|valueStart
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|TestSubUnsorted
specifier|public
name|TestSubUnsorted
parameter_list|(
name|MergeState
operator|.
name|DocMap
name|docMap
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|int
name|valueStart
parameter_list|)
block|{
name|super
argument_list|(
name|docMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|valueStart
operator|=
name|valueStart
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|docID
operator|++
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|maxDoc
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
return|return
name|docID
return|;
block|}
block|}
DECL|method|getValue
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|valueStart
operator|+
name|docID
return|;
block|}
block|}
DECL|method|testNoSort
specifier|public
name|void
name|testNoSort
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|subCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TestSubUnsorted
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|valueStart
init|=
literal|0
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
name|subCount
condition|;
name|i
operator|++
control|)
block|{
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|docBase
init|=
name|valueStart
decl_stmt|;
name|subs
operator|.
name|add
argument_list|(
operator|new
name|TestSubUnsorted
argument_list|(
operator|new
name|MergeState
operator|.
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|docBase
operator|+
name|docID
return|;
block|}
block|}
argument_list|,
name|maxDoc
argument_list|,
name|valueStart
argument_list|)
argument_list|)
expr_stmt|;
name|valueStart
operator|+=
name|maxDoc
expr_stmt|;
block|}
name|DocIDMerger
argument_list|<
name|TestSubUnsorted
argument_list|>
name|merger
init|=
name|DocIDMerger
operator|.
name|of
argument_list|(
name|subs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|TestSubUnsorted
name|sub
init|=
name|merger
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|sub
operator|.
name|mappedDocID
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|sub
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|valueStart
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
DECL|class|TestSubSorted
specifier|private
specifier|static
class|class
name|TestSubSorted
extends|extends
name|DocIDMerger
operator|.
name|Sub
block|{
DECL|field|docID
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|index
specifier|final
name|int
name|index
decl_stmt|;
DECL|method|TestSubSorted
specifier|public
name|TestSubSorted
parameter_list|(
name|MergeState
operator|.
name|DocMap
name|docMap
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|super
argument_list|(
name|docMap
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|docID
operator|++
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|maxDoc
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
return|return
name|docID
return|;
block|}
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
literal|"TestSubSorted(index="
operator|+
name|index
operator|+
literal|", mappedDocID="
operator|+
name|mappedDocID
operator|+
literal|")"
return|;
block|}
block|}
DECL|method|testWithSort
specifier|public
name|void
name|testWithSort
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|subCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|oldToNew
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// how many docs we've written to each sub:
name|List
argument_list|<
name|Integer
argument_list|>
name|uptos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|totDocCount
init|=
literal|0
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
name|subCount
condition|;
name|i
operator|++
control|)
block|{
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|uptos
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|oldToNew
operator|.
name|add
argument_list|(
operator|new
name|int
index|[
name|maxDoc
index|]
argument_list|)
expr_stmt|;
name|totDocCount
operator|+=
name|maxDoc
expr_stmt|;
block|}
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|completedSubs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// randomly distribute target docIDs into the segments:
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|totDocCount
condition|;
name|docID
operator|++
control|)
block|{
name|int
name|sub
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|oldToNew
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|upto
init|=
name|uptos
operator|.
name|get
argument_list|(
name|sub
argument_list|)
decl_stmt|;
name|int
index|[]
name|subDocs
init|=
name|oldToNew
operator|.
name|get
argument_list|(
name|sub
argument_list|)
decl_stmt|;
name|subDocs
index|[
name|upto
index|]
operator|=
name|docID
expr_stmt|;
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|subDocs
operator|.
name|length
condition|)
block|{
name|completedSubs
operator|.
name|add
argument_list|(
name|subDocs
argument_list|)
expr_stmt|;
name|oldToNew
operator|.
name|remove
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|uptos
operator|.
name|remove
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|uptos
operator|.
name|set
argument_list|(
name|sub
argument_list|,
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|oldToNew
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// sometimes do some deletions:
specifier|final
name|FixedBitSet
name|liveDocs
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|liveDocs
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|totDocCount
argument_list|)
expr_stmt|;
name|liveDocs
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|totDocCount
argument_list|)
expr_stmt|;
name|int
name|deleteAttemptCount
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|totDocCount
argument_list|)
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
name|deleteAttemptCount
condition|;
name|i
operator|++
control|)
block|{
name|liveDocs
operator|.
name|clear
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|totDocCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|liveDocs
operator|=
literal|null
expr_stmt|;
block|}
name|List
argument_list|<
name|TestSubSorted
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|subCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
index|[]
name|docMap
init|=
name|completedSubs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|subs
operator|.
name|add
argument_list|(
operator|new
name|TestSubSorted
argument_list|(
operator|new
name|MergeState
operator|.
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|mapped
init|=
name|docMap
index|[
name|docID
index|]
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|mapped
argument_list|)
condition|)
block|{
return|return
name|mapped
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
argument_list|,
name|docMap
operator|.
name|length
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DocIDMerger
argument_list|<
name|TestSubSorted
argument_list|>
name|merger
init|=
name|DocIDMerger
operator|.
name|of
argument_list|(
name|subs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|TestSubSorted
name|sub
init|=
name|merger
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
name|count
operator|=
name|liveDocs
operator|.
name|nextSetBit
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|sub
operator|.
name|mappedDocID
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|<
name|totDocCount
condition|)
block|{
name|assertEquals
argument_list|(
name|NO_MORE_DOCS
argument_list|,
name|liveDocs
operator|.
name|nextSetBit
argument_list|(
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|totDocCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|totDocCount
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

