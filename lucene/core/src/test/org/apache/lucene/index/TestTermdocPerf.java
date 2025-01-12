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
name|Random
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
name|analysis
operator|.
name|Tokenizer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|IndexWriterConfig
operator|.
name|OpenMode
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

begin_class
DECL|class|RepeatingTokenizer
class|class
name|RepeatingTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|percentDocs
specifier|private
specifier|final
name|float
name|percentDocs
decl_stmt|;
DECL|field|maxTF
specifier|private
specifier|final
name|int
name|maxTF
decl_stmt|;
DECL|field|num
specifier|private
name|int
name|num
decl_stmt|;
DECL|field|termAtt
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|value
name|String
name|value
decl_stmt|;
DECL|method|RepeatingTokenizer
specifier|public
name|RepeatingTokenizer
parameter_list|(
name|String
name|val
parameter_list|,
name|Random
name|random
parameter_list|,
name|float
name|percentDocs
parameter_list|,
name|int
name|maxTF
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|percentDocs
operator|=
name|percentDocs
expr_stmt|;
name|this
operator|.
name|maxTF
operator|=
name|maxTF
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|num
operator|--
expr_stmt|;
if|if
condition|(
name|num
operator|>=
literal|0
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextFloat
argument_list|()
operator|<
name|percentDocs
condition|)
block|{
name|num
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|maxTF
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|num
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|TestTermdocPerf
specifier|public
class|class
name|TestTermdocPerf
extends|extends
name|LuceneTestCase
block|{
DECL|method|addDocs
name|void
name|addDocs
parameter_list|(
specifier|final
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
specifier|final
name|int
name|ndocs
parameter_list|,
name|String
name|field
parameter_list|,
specifier|final
name|String
name|val
parameter_list|,
specifier|final
name|int
name|maxTF
parameter_list|,
specifier|final
name|float
name|percentDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|RepeatingTokenizer
argument_list|(
name|val
argument_list|,
name|random
argument_list|,
name|percentDocs
argument_list|,
name|maxTF
argument_list|)
argument_list|)
return|;
block|}
block|}
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
name|newStringField
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|100
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|100
argument_list|)
argument_list|)
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
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
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doTest
specifier|public
name|int
name|doTest
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|ndocs
parameter_list|,
name|int
name|maxTF
parameter_list|,
name|float
name|percentDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|addDocs
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|ndocs
argument_list|,
literal|"foo"
argument_list|,
literal|"val"
argument_list|,
name|maxTF
argument_list|,
name|percentDocs
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"milliseconds for creation of "
operator|+
name|ndocs
operator|+
literal|" docs = "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|TermsEnum
name|tenum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|PostingsEnum
name|tdocs
init|=
literal|null
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|tenum
operator|.
name|seekCeil
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
name|tdocs
operator|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|,
name|tenum
argument_list|,
name|tdocs
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
expr_stmt|;
while|while
condition|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|ret
operator|+=
name|tdocs
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
block|}
name|end
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"milliseconds for "
operator|+
name|iter
operator|+
literal|" TermDocs iteration: "
operator|+
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|testTermDocPerf
specifier|public
name|void
name|testTermDocPerf
parameter_list|()
throws|throws
name|IOException
block|{
comment|// performance test for 10% of documents containing a term
comment|// doTest(100000, 10000,3,.1f);
block|}
block|}
end_class

end_unit

