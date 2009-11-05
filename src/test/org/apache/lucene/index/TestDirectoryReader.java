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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|document
operator|.
name|Field
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestDirectoryReader
specifier|public
class|class
name|TestDirectoryReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|protected
name|Directory
name|dir
decl_stmt|;
DECL|field|doc1
specifier|private
name|Document
name|doc1
decl_stmt|;
DECL|field|doc2
specifier|private
name|Document
name|doc2
decl_stmt|;
DECL|field|readers
specifier|protected
name|SegmentReader
index|[]
name|readers
init|=
operator|new
name|SegmentReader
index|[
literal|2
index|]
decl_stmt|;
DECL|field|sis
specifier|protected
name|SegmentInfos
name|sis
decl_stmt|;
DECL|method|TestDirectoryReader
specifier|public
name|TestDirectoryReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|doc1
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc2
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|sis
operator|=
operator|new
name|SegmentInfos
argument_list|()
expr_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|openReader
specifier|protected
name|IndexReader
name|openReader
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
decl_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|instanceof
name|DirectoryReader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sis
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|reader
return|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|setUp
argument_list|()
expr_stmt|;
name|doTestDocument
argument_list|()
expr_stmt|;
name|doTestUndeleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestDocument
specifier|public
name|void
name|doTestDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|openReader
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Document
name|newDoc1
init|=
name|reader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc1
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc1
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc1
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|newDoc2
init|=
name|reader
operator|.
name|document
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc2
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc2
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TermFreqVector
name|vector
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|TestSegmentReader
operator|.
name|checkNorms
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestUndeleteAll
specifier|public
name|void
name|doTestUndeleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|openReader
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ensure undeleteAll survives commit/close/reopen:
name|reader
operator|.
name|commit
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|instanceof
name|MultiReader
condition|)
comment|// MultiReader does not "own" the directory so it does
comment|// not write the changes to sis on commit:
name|sis
operator|.
name|commit
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader
operator|=
name|openReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|commit
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|instanceof
name|MultiReader
condition|)
comment|// MultiReader does not "own" the directory so it does
comment|// not write the changes to sis on commit:
name|sis
operator|.
name|commit
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader
operator|=
name|openReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|_testTermVectors
specifier|public
name|void
name|_testTermVectors
parameter_list|()
block|{
name|MultiReader
name|reader
init|=
operator|new
name|MultiReader
argument_list|(
name|readers
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsCurrent
specifier|public
name|void
name|testIsCurrent
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|ramDir1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|ramDir1
argument_list|,
literal|"test foo"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RAMDirectory
name|ramDir2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|ramDir2
argument_list|,
literal|"test blah"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers
init|=
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir1
argument_list|,
literal|false
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir2
argument_list|,
literal|false
argument_list|)
block|}
decl_stmt|;
name|MultiReader
name|mr
init|=
operator|new
name|MultiReader
argument_list|(
name|readers
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mr
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// just opened, must be current
name|addDoc
argument_list|(
name|ramDir1
argument_list|,
literal|"more text"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mr
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// has been modified, not current anymore
name|addDoc
argument_list|(
name|ramDir2
argument_list|,
literal|"even more text"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mr
operator|.
name|isCurrent
argument_list|()
argument_list|)
expr_stmt|;
comment|// has been modified even more, not current anymore
try|try
block|{
name|mr
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
name|mr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testMultiTermDocs
specifier|public
name|void
name|testMultiTermDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|ramDir1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|ramDir1
argument_list|,
literal|"test foo"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RAMDirectory
name|ramDir2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|ramDir2
argument_list|,
literal|"test blah"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RAMDirectory
name|ramDir3
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|addDoc
argument_list|(
name|ramDir3
argument_list|,
literal|"test wow"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|readers1
init|=
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir1
argument_list|,
literal|false
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir3
argument_list|,
literal|false
argument_list|)
block|}
decl_stmt|;
name|IndexReader
index|[]
name|readers2
init|=
operator|new
name|IndexReader
index|[]
block|{
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir1
argument_list|,
literal|false
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir2
argument_list|,
literal|false
argument_list|)
block|,
name|IndexReader
operator|.
name|open
argument_list|(
name|ramDir3
argument_list|,
literal|false
argument_list|)
block|}
decl_stmt|;
name|MultiReader
name|mr2
init|=
operator|new
name|MultiReader
argument_list|(
name|readers1
argument_list|)
decl_stmt|;
name|MultiReader
name|mr3
init|=
operator|new
name|MultiReader
argument_list|(
name|readers2
argument_list|)
decl_stmt|;
comment|// test mixing up TermDocs and TermEnums from different readers.
name|TermDocs
name|td2
init|=
name|mr2
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|te3
init|=
name|mr3
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"wow"
argument_list|)
argument_list|)
decl_stmt|;
name|td2
operator|.
name|seek
argument_list|(
name|te3
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
comment|// This should blow up if we forget to check that the TermEnum is from the same
comment|// reader as the TermDocs.
while|while
condition|(
name|td2
operator|.
name|next
argument_list|()
condition|)
name|ret
operator|+=
name|td2
operator|.
name|doc
argument_list|()
expr_stmt|;
name|td2
operator|.
name|close
argument_list|()
expr_stmt|;
name|te3
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// really a dummy assert to ensure that we got some docs and to ensure that
comment|// nothing is optimized out.
name|assertTrue
argument_list|(
name|ret
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllTermDocs
specifier|public
name|void
name|testAllTermDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
name|openReader
argument_list|()
decl_stmt|;
name|int
name|NUM_DOCS
init|=
literal|2
decl_stmt|;
name|TermDocs
name|td
init|=
name|reader
operator|.
name|termDocs
argument_list|(
literal|null
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|td
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|RAMDirectory
name|ramDir1
parameter_list|,
name|String
name|s
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramDir1
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|create
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
name|s
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

