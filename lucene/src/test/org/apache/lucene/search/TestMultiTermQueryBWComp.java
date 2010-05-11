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
name|MockAnalyzer
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
name|LuceneTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test MultiTermQuery api backwards compat  * @deprecated Remove test when old API is no longer supported  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|TestMultiTermQueryBWComp
specifier|public
class|class
name|TestMultiTermQueryBWComp
extends|extends
name|LuceneTestCaseJ4
block|{
DECL|field|dir
specifier|private
specifier|static
name|RAMDirectory
name|dir
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|Searcher
name|searcher
decl_stmt|;
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"test"
decl_stmt|;
comment|/**    * Test that the correct method (getTermsEnum/getEnum) is called.    */
annotation|@
name|Test
DECL|method|testEnumMethod
specifier|public
name|void
name|testEnumMethod
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAPI
argument_list|(
literal|"old"
argument_list|,
operator|new
name|OldAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new"
argument_list|,
operator|new
name|NewAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new"
argument_list|,
operator|new
name|BothAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"old2"
argument_list|,
operator|new
name|OldExtendsOldAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"old2"
argument_list|,
operator|new
name|OldExtendsNewAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"old2"
argument_list|,
operator|new
name|OldExtendsBothAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|NewExtendsOldAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|NewExtendsNewAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|NewExtendsBothAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|BothExtendsOldAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|BothExtendsNewAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertAPI
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|BothExtendsBothAPI
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAPI
specifier|private
specifier|static
name|void
name|assertAPI
parameter_list|(
name|String
name|expected
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|FIELD
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|OldAPI
specifier|private
class|class
name|OldAPI
extends|extends
name|MultiTermQuery
block|{
DECL|method|OldAPI
name|OldAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|NewAPI
specifier|private
class|class
name|NewAPI
extends|extends
name|MultiTermQuery
block|{
DECL|method|NewAPI
name|NewAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|BothAPI
specifier|private
class|class
name|BothAPI
extends|extends
name|MultiTermQuery
block|{
DECL|method|BothAPI
name|BothAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|OldExtendsOldAPI
specifier|private
class|class
name|OldExtendsOldAPI
extends|extends
name|OldAPI
block|{
DECL|method|OldExtendsOldAPI
name|OldExtendsOldAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|OldExtendsNewAPI
specifier|private
class|class
name|OldExtendsNewAPI
extends|extends
name|NewAPI
block|{
DECL|method|OldExtendsNewAPI
name|OldExtendsNewAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|OldExtendsBothAPI
specifier|private
class|class
name|OldExtendsBothAPI
extends|extends
name|BothAPI
block|{
DECL|method|OldExtendsBothAPI
name|OldExtendsBothAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|NewExtendsOldAPI
specifier|private
class|class
name|NewExtendsOldAPI
extends|extends
name|OldAPI
block|{
DECL|method|NewExtendsOldAPI
name|NewExtendsOldAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|NewExtendsNewAPI
specifier|private
class|class
name|NewExtendsNewAPI
extends|extends
name|NewAPI
block|{
DECL|method|NewExtendsNewAPI
name|NewExtendsNewAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|NewExtendsBothAPI
specifier|private
class|class
name|NewExtendsBothAPI
extends|extends
name|BothAPI
block|{
DECL|method|NewExtendsBothAPI
name|NewExtendsBothAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|BothExtendsOldAPI
specifier|private
class|class
name|BothExtendsOldAPI
extends|extends
name|OldAPI
block|{
DECL|method|BothExtendsOldAPI
name|BothExtendsOldAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|BothExtendsNewAPI
specifier|private
class|class
name|BothExtendsNewAPI
extends|extends
name|NewAPI
block|{
DECL|method|BothExtendsNewAPI
name|BothExtendsNewAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old2"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|BothExtendsBothAPI
specifier|private
class|class
name|BothExtendsBothAPI
extends|extends
name|BothAPI
block|{
DECL|method|BothExtendsBothAPI
name|BothExtendsBothAPI
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"old2"
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|MockAnalyzer
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
name|String
name|values
index|[]
init|=
block|{
literal|"old"
block|,
literal|"old2"
block|,
literal|"new"
block|,
literal|"new2"
block|}
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
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
name|FIELD
argument_list|,
name|value
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
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

