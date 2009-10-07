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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|IndexWriter
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
name|store
operator|.
name|Directory
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
name|RAMDirectory
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
name|OpenBitSet
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
name|OpenBitSetDISI
import|;
end_import

begin_class
DECL|class|TestCachingWrapperFilter
specifier|public
class|class
name|TestCachingWrapperFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCachingWorks
specifier|public
name|void
name|testCachingWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MockFilter
name|filter
init|=
operator|new
name|MockFilter
argument_list|()
decl_stmt|;
name|CachingWrapperFilter
name|cacher
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// first time, nested filter is called
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"first time"
argument_list|,
name|filter
operator|.
name|wasCalled
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure no exception if cache is holding the wrong docIdSet
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// second time, nested filter should not be called
name|filter
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"second time"
argument_list|,
name|filter
operator|.
name|wasCalled
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertDocIdSetCacheable
specifier|private
specifier|static
name|void
name|assertDocIdSetCacheable
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|boolean
name|shouldCacheable
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|CachingWrapperFilter
name|cacher
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|originalSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|cachedSet
init|=
name|cacher
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cachedSet
operator|.
name|isCacheable
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shouldCacheable
argument_list|,
name|originalSet
operator|.
name|isCacheable
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("Original: "+originalSet.getClass().getName()+" -- cached: "+cachedSet.getClass().getName());
if|if
condition|(
name|originalSet
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Cached DocIdSet must be of same class like uncached, if cacheable"
argument_list|,
name|originalSet
operator|.
name|getClass
argument_list|()
argument_list|,
name|cachedSet
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"Cached DocIdSet must be an OpenBitSet if the original one was not cacheable"
argument_list|,
name|cachedSet
operator|instanceof
name|OpenBitSetDISI
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIsCacheAble
specifier|public
name|void
name|testIsCacheAble
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// not cacheable:
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// returns default empty docidset, always cacheable:
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
name|NumericRangeFilter
operator|.
name|newIntRange
argument_list|(
literal|"test"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|10000
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
operator|-
literal|10000
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// is cacheable:
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
name|FieldCacheRangeFilter
operator|.
name|newIntRange
argument_list|(
literal|"test"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|10
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|20
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// a openbitset filter is always cacheable
name|assertDocIdSetCacheable
argument_list|(
name|reader
argument_list|,
operator|new
name|Filter
argument_list|()
block|{
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|OpenBitSet
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

