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
name|search
operator|.
name|DocIdSet
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

begin_class
DECL|class|TestBitDocIdSetBuilder
specifier|public
class|class
name|TestBitDocIdSetBuilder
extends|extends
name|LuceneTestCase
block|{
DECL|method|randomSet
specifier|private
specifier|static
name|DocIdSet
name|randomSet
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|==
literal|false
condition|)
block|{
name|set
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
return|;
block|}
DECL|method|assertEquals
specifier|private
name|void
name|assertEquals
parameter_list|(
name|DocIdSet
name|set1
parameter_list|,
name|DocIdSet
name|set2
parameter_list|)
throws|throws
name|IOException
block|{
name|DocIdSetIterator
name|it1
init|=
name|set1
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|it2
init|=
name|set2
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|it1
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|it1
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|it2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|it2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrDense
specifier|public
name|void
name|testOrDense
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
literal|10000
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSet
name|other
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|dense
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrSparse
specifier|public
name|void
name|testOrSparse
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
literal|10000
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSet
name|other
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|5000
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|dense
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|5000
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndDense
specifier|public
name|void
name|testAndDense
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
literal|10000
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSet
name|other
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|dense
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|and
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|and
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndSparse
specifier|public
name|void
name|testAndSparse
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
literal|10000
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSet
name|other
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2000
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|dense
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|and
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|and
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndNotDense
specifier|public
name|void
name|testAndNotDense
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
literal|10000
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSet
name|other
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|dense
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|andNot
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|andNot
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndNotSparse
specifier|public
name|void
name|testAndNotSparse
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
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
literal|10000
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|DocIdSet
name|other
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2000
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|dense
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|,
name|maxDoc
operator|/
literal|2
argument_list|)
expr_stmt|;
name|builder
operator|.
name|andNot
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|set
operator|.
name|andNot
argument_list|(
name|other
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

