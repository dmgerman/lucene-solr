begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TermEnum
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/** Indexer for HTML files. */
end_comment

begin_class
DECL|class|IndexHTML
specifier|public
class|class
name|IndexHTML
block|{
DECL|method|IndexHTML
specifier|private
name|IndexHTML
parameter_list|()
block|{}
DECL|field|deleting
specifier|private
specifier|static
name|boolean
name|deleting
init|=
literal|false
decl_stmt|;
comment|// true during deletion pass
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
comment|// existing index
DECL|field|writer
specifier|private
specifier|static
name|IndexWriter
name|writer
decl_stmt|;
comment|// new index being built
DECL|field|uidIter
specifier|private
specifier|static
name|TermEnum
name|uidIter
decl_stmt|;
comment|// document id iterator
comment|/** Indexer for HTML files.*/
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
try|try
block|{
name|String
name|index
init|=
literal|"index"
decl_stmt|;
name|boolean
name|create
init|=
literal|false
decl_stmt|;
name|File
name|root
init|=
literal|null
decl_stmt|;
name|String
name|usage
init|=
literal|"IndexHTML [-create] [-index<index>]<root_directory>"
decl_stmt|;
if|if
condition|(
name|argv
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|usage
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argv
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|argv
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-index"
argument_list|)
condition|)
block|{
comment|// parse -index option
name|index
operator|=
name|argv
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|argv
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-create"
argument_list|)
condition|)
block|{
comment|// parse -create option
name|create
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|!=
name|argv
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|usage
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
name|root
operator|=
operator|new
name|File
argument_list|(
name|argv
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|create
condition|)
block|{
comment|// delete stale docs
name|deleting
operator|=
literal|true
expr_stmt|;
name|indexDocs
argument_list|(
name|root
argument_list|,
name|index
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|index
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxFieldLength
argument_list|(
literal|1000000
argument_list|)
expr_stmt|;
name|indexDocs
argument_list|(
name|root
argument_list|,
name|index
argument_list|,
name|create
argument_list|)
expr_stmt|;
comment|// add new docs
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Optimizing index..."
argument_list|)
expr_stmt|;
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
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" total milliseconds"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Walk directory hierarchy in uid order, while keeping uid iterator from   /* existing index in sync.  Mismatches indicate one of: (a) old documents to   /* be deleted; (b) unchanged documents, to be left alone; or (c) new   /* documents, to be indexed.    */
DECL|method|indexDocs
specifier|private
specifier|static
name|void
name|indexDocs
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|index
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|create
condition|)
block|{
comment|// incrementally update
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|index
argument_list|)
expr_stmt|;
comment|// open existing index
name|uidIter
operator|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"uid"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// init uid iterator
name|indexDocs
argument_list|(
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
name|deleting
condition|)
block|{
comment|// delete rest of stale docs
while|while
condition|(
name|uidIter
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
operator|==
literal|"uid"
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"deleting "
operator|+
name|HTMLDocument
operator|.
name|uid2url
argument_list|(
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|delete
argument_list|(
name|uidIter
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|uidIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|deleting
operator|=
literal|false
expr_stmt|;
block|}
name|uidIter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close uid iterator
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close existing index
block|}
else|else
comment|// don't have exisiting
name|indexDocs
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
DECL|method|indexDocs
specifier|private
specifier|static
name|void
name|indexDocs
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// if a directory
name|String
index|[]
name|files
init|=
name|file
operator|.
name|list
argument_list|()
decl_stmt|;
comment|// list its files
name|Arrays
operator|.
name|sort
argument_list|(
name|files
argument_list|)
expr_stmt|;
comment|// sort the files
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
comment|// recursively index them
name|indexDocs
argument_list|(
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".html"
argument_list|)
operator|||
comment|// index .html files
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".htm"
argument_list|)
operator|||
comment|// index .htm files
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".txt"
argument_list|)
condition|)
block|{
comment|// index .txt files
if|if
condition|(
name|uidIter
operator|!=
literal|null
condition|)
block|{
name|String
name|uid
init|=
name|HTMLDocument
operator|.
name|uid
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// construct uid for doc
while|while
condition|(
name|uidIter
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
operator|==
literal|"uid"
operator|&&
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
operator|.
name|compareTo
argument_list|(
name|uid
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|deleting
condition|)
block|{
comment|// delete stale docs
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"deleting "
operator|+
name|HTMLDocument
operator|.
name|uid2url
argument_list|(
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|delete
argument_list|(
name|uidIter
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|uidIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|uidIter
operator|.
name|term
argument_list|()
operator|!=
literal|null
operator|&&
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
operator|==
literal|"uid"
operator|&&
name|uidIter
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
operator|.
name|compareTo
argument_list|(
name|uid
argument_list|)
operator|==
literal|0
condition|)
block|{
name|uidIter
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// keep matching docs
block|}
elseif|else
if|if
condition|(
operator|!
name|deleting
condition|)
block|{
comment|// add new docs
name|Document
name|doc
init|=
name|HTMLDocument
operator|.
name|Document
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding "
operator|+
name|doc
operator|.
name|get
argument_list|(
literal|"path"
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
block|}
else|else
block|{
comment|// creating a new index
name|Document
name|doc
init|=
name|HTMLDocument
operator|.
name|Document
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"adding "
operator|+
name|doc
operator|.
name|get
argument_list|(
literal|"path"
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
comment|// add docs unconditionally
block|}
block|}
block|}
block|}
end_class

end_unit

