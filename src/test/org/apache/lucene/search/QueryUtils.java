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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Document
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
name|MultiReader
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
operator|.
name|MaxFieldLength
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
name|ReaderUtil
import|;
end_import

begin_comment
comment|/**  * Copyright 2005 Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|QueryUtils
specifier|public
class|class
name|QueryUtils
block|{
comment|/** Check the types of things query objects should be able to do. */
DECL|method|check
specifier|public
specifier|static
name|void
name|check
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|checkHashEquals
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
comment|/** check very basic hashCode and equals */
DECL|method|checkHashEquals
specifier|public
specifier|static
name|void
name|checkHashEquals
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|Query
name|q2
init|=
operator|(
name|Query
operator|)
name|q
operator|.
name|clone
argument_list|()
decl_stmt|;
name|checkEqual
argument_list|(
name|q
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|Query
name|q3
init|=
operator|(
name|Query
operator|)
name|q
operator|.
name|clone
argument_list|()
decl_stmt|;
name|q3
operator|.
name|setBoost
argument_list|(
literal|7.21792348f
argument_list|)
expr_stmt|;
name|checkUnequal
argument_list|(
name|q
argument_list|,
name|q3
argument_list|)
expr_stmt|;
comment|// test that a class check is done so that no exception is thrown
comment|// in the implementation of equals()
name|Query
name|whacky
init|=
operator|new
name|Query
argument_list|()
block|{
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"My Whacky Query"
return|;
block|}
block|}
decl_stmt|;
name|whacky
operator|.
name|setBoost
argument_list|(
name|q
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|checkUnequal
argument_list|(
name|q
argument_list|,
name|whacky
argument_list|)
expr_stmt|;
block|}
DECL|method|checkEqual
specifier|public
specifier|static
name|void
name|checkEqual
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|q1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|q2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUnequal
specifier|public
specifier|static
name|void
name|checkUnequal
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|q1
operator|.
name|equals
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|q2
operator|.
name|equals
argument_list|(
name|q1
argument_list|)
argument_list|)
expr_stmt|;
comment|// possible this test can fail on a hash collision... if that
comment|// happens, please change test to use a different example.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|q1
operator|.
name|hashCode
argument_list|()
operator|!=
name|q2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** deep check that explanations of a query 'score' correctly */
DECL|method|checkExplanations
specifier|public
specifier|static
name|void
name|checkExplanations
parameter_list|(
specifier|final
name|Query
name|q
parameter_list|,
specifier|final
name|Searcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|s
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**     * Various query sanity checks on a searcher, some checks are only done for    * instanceof IndexSearcher.    *    * @see #check(Query)    * @see #checkFirstSkipTo    * @see #checkSkipTo    * @see #checkExplanations    * @see #checkSerialization    * @see #checkEqual    */
DECL|method|check
specifier|public
specifier|static
name|void
name|check
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Searcher
name|s
parameter_list|)
block|{
name|check
argument_list|(
name|q1
argument_list|,
name|s
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
specifier|static
name|void
name|check
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Searcher
name|s
parameter_list|,
name|boolean
name|wrap
parameter_list|)
block|{
try|try
block|{
name|check
argument_list|(
name|q1
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|s
operator|instanceof
name|IndexSearcher
condition|)
block|{
name|IndexSearcher
name|is
init|=
operator|(
name|IndexSearcher
operator|)
name|s
decl_stmt|;
name|checkFirstSkipTo
argument_list|(
name|q1
argument_list|,
name|is
argument_list|)
expr_stmt|;
name|checkSkipTo
argument_list|(
name|q1
argument_list|,
name|is
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrap
condition|)
block|{
name|check
argument_list|(
name|q1
argument_list|,
name|wrapUnderlyingReader
argument_list|(
name|is
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q1
argument_list|,
name|wrapUnderlyingReader
argument_list|(
name|is
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q1
argument_list|,
name|wrapUnderlyingReader
argument_list|(
name|is
argument_list|,
operator|+
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|wrap
condition|)
block|{
name|check
argument_list|(
name|q1
argument_list|,
name|wrapSearcher
argument_list|(
name|s
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q1
argument_list|,
name|wrapSearcher
argument_list|(
name|s
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|q1
argument_list|,
name|wrapSearcher
argument_list|(
name|s
argument_list|,
operator|+
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|checkExplanations
argument_list|(
name|q1
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|checkSerialization
argument_list|(
name|q1
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
operator|(
name|Query
operator|)
name|q1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|checkEqual
argument_list|(
name|s
operator|.
name|rewrite
argument_list|(
name|q1
argument_list|)
argument_list|,
name|s
operator|.
name|rewrite
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Given an IndexSearcher, returns a new IndexSearcher whose IndexReader     * is a MultiReader containing the Reader of the original IndexSearcher,     * as well as several "empty" IndexReaders -- some of which will have     * deleted documents in them.  This new IndexSearcher should     * behave exactly the same as the original IndexSearcher.    * @param s the searcher to wrap    * @param edge if negative, s will be the first sub; if 0, s will be in the middle, if positive s will be the last sub    */
DECL|method|wrapUnderlyingReader
specifier|public
specifier|static
name|IndexSearcher
name|wrapUnderlyingReader
parameter_list|(
specifier|final
name|IndexSearcher
name|s
parameter_list|,
specifier|final
name|int
name|edge
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|r
init|=
name|s
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
comment|// we can't put deleted docs before the nested reader, because
comment|// it will throw off the docIds
name|IndexReader
index|[]
name|readers
init|=
operator|new
name|IndexReader
index|[]
block|{
name|edge
operator|<
literal|0
condition|?
name|r
else|:
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|MultiReader
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
name|edge
operator|<
literal|0
condition|?
literal|4
else|:
literal|0
argument_list|)
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
literal|0
operator|==
name|edge
condition|?
name|r
else|:
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|}
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
operator|<
name|edge
condition|?
literal|0
else|:
literal|7
argument_list|)
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|MultiReader
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
operator|<
name|edge
condition|?
literal|0
else|:
literal|5
argument_list|)
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
literal|0
operator|<
name|edge
condition|?
name|r
else|:
name|IndexReader
operator|.
name|open
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
name|IndexSearcher
name|out
init|=
operator|new
name|IndexSearcher
argument_list|(
operator|new
name|MultiReader
argument_list|(
name|readers
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|setSimilarity
argument_list|(
name|s
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
comment|/**    * Given a Searcher, returns a new MultiSearcher wrapping the      * the original Searcher,     * as well as several "empty" IndexSearchers -- some of which will have    * deleted documents in them.  This new MultiSearcher     * should behave exactly the same as the original Searcher.    * @param s the Searcher to wrap    * @param edge if negative, s will be the first sub; if 0, s will be in hte middle, if positive s will be the last sub    */
DECL|method|wrapSearcher
specifier|public
specifier|static
name|MultiSearcher
name|wrapSearcher
parameter_list|(
specifier|final
name|Searcher
name|s
parameter_list|,
specifier|final
name|int
name|edge
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we can't put deleted docs before the nested reader, because
comment|// it will through off the docIds
name|Searcher
index|[]
name|searchers
init|=
operator|new
name|Searcher
index|[]
block|{
name|edge
operator|<
literal|0
condition|?
name|s
else|:
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|MultiSearcher
argument_list|(
operator|new
name|Searcher
index|[]
block|{
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
name|edge
operator|<
literal|0
condition|?
literal|65
else|:
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
literal|0
operator|==
name|edge
condition|?
name|s
else|:
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|}
argument_list|)
block|,
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
operator|<
name|edge
condition|?
literal|0
else|:
literal|3
argument_list|)
argument_list|)
block|,
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
operator|new
name|MultiSearcher
argument_list|(
operator|new
name|Searcher
index|[]
block|{
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
operator|<
name|edge
condition|?
literal|0
else|:
literal|5
argument_list|)
argument_list|)
block|,
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|,
literal|0
operator|<
name|edge
condition|?
name|s
else|:
operator|new
name|IndexSearcher
argument_list|(
name|makeEmptyIndex
argument_list|(
literal|0
argument_list|)
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
name|MultiSearcher
name|out
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchers
argument_list|)
decl_stmt|;
name|out
operator|.
name|setSimilarity
argument_list|(
name|s
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|out
return|;
block|}
DECL|method|makeEmptyIndex
specifier|private
specifier|static
name|RAMDirectory
name|makeEmptyIndex
parameter_list|(
specifier|final
name|int
name|numDeletedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMDirectory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|LIMITED
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
name|numDeletedDocs
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|<
name|numDeletedDocs
condition|)
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"writer has no deletions"
argument_list|,
name|w
operator|.
name|hasDeletions
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"writer is missing some deleted docs"
argument_list|,
name|numDeletedDocs
argument_list|,
name|w
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"writer has non-deleted docs"
argument_list|,
literal|0
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"reader has wrong number of deleted docs"
argument_list|,
name|numDeletedDocs
argument_list|,
name|r
operator|.
name|numDeletedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|d
return|;
block|}
comment|/** check that the query weight is serializable.     * @throws IOException if serialization check fail.     */
DECL|method|checkSerialization
specifier|private
specifier|static
name|void
name|checkSerialization
parameter_list|(
name|Query
name|q
parameter_list|,
name|Searcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|Weight
name|w
init|=
name|q
operator|.
name|weight
argument_list|(
name|s
argument_list|)
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|oos
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bos
argument_list|)
decl_stmt|;
name|oos
operator|.
name|writeObject
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|oos
operator|.
name|close
argument_list|()
expr_stmt|;
name|ObjectInputStream
name|ois
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ois
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|ois
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//skip equals() test for now - most weights don't override equals() and we won't add this just for the tests.
comment|//TestCase.assertEquals("writeObject(w) != w.  ("+w+")",w2,w);
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|IOException
name|e2
init|=
operator|new
name|IOException
argument_list|(
literal|"Serialization failed for "
operator|+
name|w
argument_list|)
decl_stmt|;
name|e2
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
block|}
comment|/** alternate scorer skipTo(),skipTo(),next(),next(),skipTo(),skipTo(), etc    * and ensure a hitcollector receives same docs and scores    */
DECL|method|checkSkipTo
specifier|public
specifier|static
name|void
name|checkSkipTo
parameter_list|(
specifier|final
name|Query
name|q
parameter_list|,
specifier|final
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("Checking "+q);
if|if
condition|(
name|BooleanQuery
operator|.
name|getAllowDocsOutOfOrder
argument_list|()
condition|)
return|return;
comment|// in this case order of skipTo() might differ from that of next().
specifier|final
name|int
name|skip_op
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|next_op
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|orders
index|[]
index|[]
init|=
block|{
block|{
name|next_op
block|}
block|,
block|{
name|skip_op
block|}
block|,
block|{
name|skip_op
block|,
name|next_op
block|}
block|,
block|{
name|next_op
block|,
name|skip_op
block|}
block|,
block|{
name|skip_op
block|,
name|skip_op
block|,
name|next_op
block|,
name|next_op
block|}
block|,
block|{
name|next_op
block|,
name|next_op
block|,
name|skip_op
block|,
name|skip_op
block|}
block|,
block|{
name|skip_op
block|,
name|skip_op
block|,
name|skip_op
block|,
name|next_op
block|,
name|next_op
block|}
block|,     }
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|orders
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
specifier|final
name|int
name|order
index|[]
init|=
name|orders
index|[
name|k
index|]
decl_stmt|;
comment|// System.out.print("Order:");for (int i = 0; i< order.length; i++)
comment|// System.out.print(order[i]==skip_op ? " skip()":" next()");
comment|// System.out.println();
specifier|final
name|int
name|opidx
index|[]
init|=
block|{
literal|0
block|}
decl_stmt|;
specifier|final
name|Weight
name|w
init|=
name|q
operator|.
name|weight
argument_list|(
name|s
argument_list|)
decl_stmt|;
specifier|final
name|Scorer
name|scorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|s
operator|.
name|getIndexReader
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// FUTURE: ensure scorer.doc()==-1
specifier|final
name|int
index|[]
name|sdoc
init|=
operator|new
name|int
index|[]
block|{
operator|-
literal|1
block|}
decl_stmt|;
specifier|final
name|float
name|maxDiff
init|=
literal|1e-5f
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
specifier|private
name|Scorer
name|sc
decl_stmt|;
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|sc
operator|=
name|scorer
expr_stmt|;
block|}
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
operator|=
name|doc
operator|+
name|base
expr_stmt|;
name|float
name|score
init|=
name|sc
operator|.
name|score
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|op
init|=
name|order
index|[
operator|(
name|opidx
index|[
literal|0
index|]
operator|++
operator|)
operator|%
name|order
operator|.
name|length
index|]
decl_stmt|;
comment|// System.out.println(op==skip_op ?
comment|// "skip("+(sdoc[0]+1)+")":"next()");
name|boolean
name|more
init|=
name|op
operator|==
name|skip_op
condition|?
name|scorer
operator|.
name|advance
argument_list|(
name|sdoc
index|[
literal|0
index|]
operator|+
literal|1
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
else|:
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
decl_stmt|;
name|sdoc
index|[
literal|0
index|]
operator|=
name|scorer
operator|.
name|docID
argument_list|()
expr_stmt|;
name|float
name|scorerScore
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|float
name|scorerScore2
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|float
name|scoreDiff
init|=
name|Math
operator|.
name|abs
argument_list|(
name|score
operator|-
name|scorerScore
argument_list|)
decl_stmt|;
name|float
name|scorerDiff
init|=
name|Math
operator|.
name|abs
argument_list|(
name|scorerScore2
operator|-
name|scorerScore
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|more
operator|||
name|doc
operator|!=
name|sdoc
index|[
literal|0
index|]
operator|||
name|scoreDiff
operator|>
name|maxDiff
operator|||
name|scorerDiff
operator|>
name|maxDiff
condition|)
block|{
name|StringBuffer
name|sbord
init|=
operator|new
name|StringBuffer
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
name|order
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|sbord
operator|.
name|append
argument_list|(
name|order
index|[
name|i
index|]
operator|==
name|skip_op
condition|?
literal|" skip()"
else|:
literal|" next()"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"ERROR matching docs:"
operator|+
literal|"\n\t"
operator|+
operator|(
name|doc
operator|!=
name|sdoc
index|[
literal|0
index|]
condition|?
literal|"--> "
else|:
literal|""
operator|)
operator|+
literal|"doc="
operator|+
name|sdoc
index|[
literal|0
index|]
operator|+
literal|"\n\t"
operator|+
operator|(
operator|!
name|more
condition|?
literal|"--> "
else|:
literal|""
operator|)
operator|+
literal|"tscorer.more="
operator|+
name|more
operator|+
literal|"\n\t"
operator|+
operator|(
name|scoreDiff
operator|>
name|maxDiff
condition|?
literal|"--> "
else|:
literal|""
operator|)
operator|+
literal|"scorerScore="
operator|+
name|scorerScore
operator|+
literal|" scoreDiff="
operator|+
name|scoreDiff
operator|+
literal|" maxDiff="
operator|+
name|maxDiff
operator|+
literal|"\n\t"
operator|+
operator|(
name|scorerDiff
operator|>
name|maxDiff
condition|?
literal|"--> "
else|:
literal|""
operator|)
operator|+
literal|"scorerScore2="
operator|+
name|scorerScore2
operator|+
literal|" scorerDiff="
operator|+
name|scorerDiff
operator|+
literal|"\n\thitCollector.doc="
operator|+
name|doc
operator|+
literal|" score="
operator|+
name|score
operator|+
literal|"\n\t Scorer="
operator|+
name|scorer
operator|+
literal|"\n\t Query="
operator|+
name|q
operator|+
literal|"  "
operator|+
name|q
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"\n\t Searcher="
operator|+
name|s
operator|+
literal|"\n\t Order="
operator|+
name|sbord
operator|+
literal|"\n\t Op="
operator|+
operator|(
name|op
operator|==
name|skip_op
condition|?
literal|" skip()"
else|:
literal|" next()"
operator|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|base
operator|=
name|docBase
expr_stmt|;
block|}
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// make sure next call to scorer is false.
name|int
name|op
init|=
name|order
index|[
operator|(
name|opidx
index|[
literal|0
index|]
operator|++
operator|)
operator|%
name|order
operator|.
name|length
index|]
decl_stmt|;
comment|// System.out.println(op==skip_op ? "last: skip()":"last: next()");
name|boolean
name|more
init|=
operator|(
name|op
operator|==
name|skip_op
condition|?
name|scorer
operator|.
name|advance
argument_list|(
name|sdoc
index|[
literal|0
index|]
operator|+
literal|1
argument_list|)
else|:
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|more
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check that first skip on just created scorers always goes to the right doc
DECL|method|checkFirstSkipTo
specifier|private
specifier|static
name|void
name|checkFirstSkipTo
parameter_list|(
specifier|final
name|Query
name|q
parameter_list|,
specifier|final
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("checkFirstSkipTo: "+q);
specifier|final
name|float
name|maxDiff
init|=
literal|1e-5f
decl_stmt|;
specifier|final
name|int
name|lastDoc
index|[]
init|=
block|{
operator|-
literal|1
block|}
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|Collector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
specifier|private
name|IndexReader
name|reader
decl_stmt|;
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
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
comment|//System.out.println("doc="+doc);
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
name|lastDoc
index|[
literal|0
index|]
operator|+
literal|1
init|;
name|i
operator|<=
name|doc
condition|;
name|i
operator|++
control|)
block|{
name|Weight
name|w
init|=
name|q
operator|.
name|weight
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Scorer
name|scorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"query collected "
operator|+
name|doc
operator|+
literal|" but skipTo("
operator|+
name|i
operator|+
literal|") says no more docs!"
argument_list|,
name|scorer
operator|.
name|advance
argument_list|(
name|i
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"query collected "
operator|+
name|doc
operator|+
literal|" but skipTo("
operator|+
name|i
operator|+
literal|") got to "
operator|+
name|scorer
operator|.
name|docID
argument_list|()
argument_list|,
name|doc
argument_list|,
name|scorer
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|skipToScore
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"unstable skipTo("
operator|+
name|i
operator|+
literal|") score!"
argument_list|,
name|skipToScore
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|,
name|maxDiff
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"query assigned doc "
operator|+
name|doc
operator|+
literal|" a score of<"
operator|+
name|score
operator|+
literal|"> but skipTo("
operator|+
name|i
operator|+
literal|") has<"
operator|+
name|skipToScore
operator|+
literal|">!"
argument_list|,
name|score
argument_list|,
name|skipToScore
argument_list|,
name|maxDiff
argument_list|)
expr_stmt|;
block|}
name|lastDoc
index|[
literal|0
index|]
operator|=
name|doc
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|lastDoc
index|[
literal|0
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
name|readerList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ReaderUtil
operator|.
name|gatherSubReaders
argument_list|(
name|readerList
argument_list|,
name|s
operator|.
name|getIndexReader
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers
init|=
operator|(
name|IndexReader
index|[]
operator|)
name|readerList
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
literal|0
index|]
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|readers
index|[
name|i
index|]
decl_stmt|;
name|Weight
name|w
init|=
name|q
operator|.
name|weight
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Scorer
name|scorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|boolean
name|more
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|lastDoc
index|[
literal|0
index|]
operator|+
literal|1
argument_list|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
decl_stmt|;
if|if
condition|(
name|more
operator|&&
name|lastDoc
index|[
literal|0
index|]
operator|!=
operator|-
literal|1
condition|)
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"query's last doc was "
operator|+
name|lastDoc
index|[
literal|0
index|]
operator|+
literal|" but skipTo("
operator|+
operator|(
name|lastDoc
index|[
literal|0
index|]
operator|+
literal|1
operator|)
operator|+
literal|") got to "
operator|+
name|scorer
operator|.
name|docID
argument_list|()
argument_list|,
name|more
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

