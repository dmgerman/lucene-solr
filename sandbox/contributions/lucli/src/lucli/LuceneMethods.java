begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|lucli
package|package
name|lucli
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Token
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
name|TokenStream
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|MultiFieldQueryParser
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
name|queryParser
operator|.
name|ParseException
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
name|Explanation
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
name|Hits
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
name|IndexSearcher
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
name|Query
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
name|Searcher
import|;
end_import

begin_comment
comment|/*  * Parts addapted from Lucene demo. Various methods that interact with  * Lucene and provide info about the index, search, etc.  */
end_comment

begin_class
DECL|class|LuceneMethods
class|class
name|LuceneMethods
block|{
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|indexName
specifier|private
name|String
name|indexName
decl_stmt|;
comment|//directory of this index
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
comment|//version number of this index
DECL|field|fieldIterator
name|java
operator|.
name|util
operator|.
name|Iterator
name|fieldIterator
decl_stmt|;
DECL|field|fields
name|Vector
name|fields
decl_stmt|;
comment|//Fields as a vector
DECL|field|indexedFields
name|Vector
name|indexedFields
decl_stmt|;
comment|//Fields as a vector
DECL|field|fieldsArray
name|String
name|fieldsArray
index|[]
decl_stmt|;
comment|//Fields as an array
DECL|field|searcher
name|Searcher
name|searcher
decl_stmt|;
DECL|field|query
name|Query
name|query
decl_stmt|;
comment|//current query string
DECL|method|LuceneMethods
specifier|public
name|LuceneMethods
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|indexName
operator|=
name|index
expr_stmt|;
name|message
argument_list|(
literal|"Lucene CLI. Using directory:"
operator|+
name|indexName
argument_list|)
expr_stmt|;
block|}
DECL|method|info
specifier|public
name|void
name|info
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|getFieldInfo
argument_list|()
expr_stmt|;
name|numDocs
operator|=
name|indexReader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|message
argument_list|(
literal|"Index has "
operator|+
name|numDocs
operator|+
literal|" documents "
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"All Fields:"
operator|+
name|fields
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"Indexed Fields:"
operator|+
name|indexedFields
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|IndexReader
operator|.
name|isLocked
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
name|message
argument_list|(
literal|"Index is locked"
argument_list|)
expr_stmt|;
block|}
comment|//IndexReader.getCurrentVersion(indexName);
comment|//System.out.println("Version:" + version);
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|String
name|queryString
parameter_list|,
name|boolean
name|explain
parameter_list|,
name|boolean
name|showTokens
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
throws|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
block|{
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|initSearch
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total matching documents"
argument_list|)
expr_stmt|;
if|if
condition|(
name|explain
condition|)
block|{
name|query
operator|=
name|explainQuery
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|HITS_PER_PAGE
init|=
literal|10
decl_stmt|;
name|message
argument_list|(
literal|"--------------------------------------"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|start
init|=
literal|0
init|;
name|start
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|start
operator|+=
name|HITS_PER_PAGE
control|)
block|{
name|int
name|end
init|=
name|Math
operator|.
name|min
argument_list|(
name|hits
operator|.
name|length
argument_list|()
argument_list|,
name|start
operator|+
name|HITS_PER_PAGE
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
name|start
init|;
name|ii
operator|<
name|end
condition|;
name|ii
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|message
argument_list|(
literal|"---------------- "
operator|+
operator|(
name|ii
operator|+
literal|1
operator|)
operator|+
literal|" score:"
operator|+
name|hits
operator|.
name|score
argument_list|(
name|ii
argument_list|)
operator|+
literal|"---------------------"
argument_list|)
expr_stmt|;
name|printHit
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|showTokens
condition|)
block|{
name|invertDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|explain
condition|)
block|{
name|Explanation
name|exp
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
operator|.
name|id
argument_list|(
name|ii
argument_list|)
argument_list|)
decl_stmt|;
name|message
argument_list|(
literal|"Explanation:"
operator|+
name|exp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|message
argument_list|(
literal|"#################################################"
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
argument_list|()
operator|>
name|end
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"more (y/n) ? "
argument_list|)
expr_stmt|;
name|queryString
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|queryString
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|queryString
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'n'
condition|)
break|break;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * @todo Allow user to specify what field(s) to display    */
DECL|method|printHit
specifier|private
name|void
name|printHit
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|fieldsArray
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
name|String
name|currField
init|=
name|fieldsArray
index|[
name|ii
index|]
decl_stmt|;
name|String
index|[]
name|result
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|currField
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
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|message
argument_list|(
name|currField
operator|+
literal|":"
operator|+
name|result
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|//another option is to just do message(doc);
block|}
DECL|method|optimize
specifier|public
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
comment|//open the index writer. False: don't create a new one
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexName
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|message
argument_list|(
literal|"Starting to optimize index."
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|indexWriter
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|message
argument_list|(
literal|"Done optimizing index. Took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|" msecs"
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|explainQuery
specifier|private
name|Query
name|explainQuery
parameter_list|(
name|String
name|queryString
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|getFieldInfo
argument_list|()
expr_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|MultiFieldQueryParser
name|parser
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|queryString
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|int
name|arraySize
init|=
name|indexedFields
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
name|indexedArray
index|[]
init|=
operator|new
name|String
index|[
name|arraySize
index|]
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|arraySize
condition|;
name|ii
operator|++
control|)
block|{
name|indexedArray
index|[
name|ii
index|]
operator|=
operator|(
name|String
operator|)
name|indexedFields
operator|.
name|get
argument_list|(
name|ii
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|queryString
argument_list|,
name|indexedArray
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for: "
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|query
operator|)
return|;
block|}
comment|/**    * @todo Allow user to specify analyzer    */
DECL|method|initSearch
specifier|private
name|Hits
name|initSearch
parameter_list|(
name|String
name|queryString
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|getFieldInfo
argument_list|()
expr_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|MultiFieldQueryParser
name|parser
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|queryString
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|int
name|arraySize
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
name|fieldsArray
operator|=
operator|new
name|String
index|[
name|arraySize
index|]
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|arraySize
condition|;
name|ii
operator|++
control|)
block|{
name|fieldsArray
index|[
name|ii
index|]
operator|=
operator|(
name|String
operator|)
name|fields
operator|.
name|get
argument_list|(
name|ii
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|queryString
argument_list|,
name|fieldsArray
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for: "
operator|+
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
operator|(
name|hits
operator|)
return|;
block|}
DECL|method|count
specifier|public
name|void
name|count
parameter_list|(
name|String
name|queryString
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
throws|,
name|ParseException
block|{
name|Hits
name|hits
init|=
name|initSearch
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total documents"
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|message
specifier|static
specifier|public
name|void
name|message
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldInfo
specifier|private
name|void
name|getFieldInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|fields
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
name|indexedFields
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
comment|//get the list of all field names
name|fieldIterator
operator|=
name|indexReader
operator|.
name|getFieldNames
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|field
init|=
name|fieldIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|!
name|field
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|fields
operator|.
name|add
argument_list|(
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//
comment|//get the list of indexed field names
name|fieldIterator
operator|=
name|indexReader
operator|.
name|getFieldNames
argument_list|(
literal|true
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|field
init|=
name|fieldIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
operator|!
name|field
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|indexedFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Copied from DocumentWriter
comment|// Tokenizes the fields of a document into Postings.
DECL|method|invertDocument
specifier|private
name|void
name|invertDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Hashtable
name|tokenHash
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxFieldLength
init|=
literal|10000
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|()
decl_stmt|;
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|field
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
block|{
comment|// un-tokenized field
name|Reader
name|reader
decl_stmt|;
comment|// find or make Reader
if|if
condition|(
name|field
operator|.
name|readerValue
argument_list|()
operator|!=
literal|null
condition|)
name|reader
operator|=
name|field
operator|.
name|readerValue
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
name|reader
operator|=
operator|new
name|StringReader
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must have either String or Reader value"
argument_list|)
throw|;
name|int
name|position
init|=
literal|0
decl_stmt|;
comment|// Tokenize field and add to postingTable
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|Token
name|t
init|=
name|stream
operator|.
name|next
argument_list|()
init|;
name|t
operator|!=
literal|null
condition|;
name|t
operator|=
name|stream
operator|.
name|next
argument_list|()
control|)
block|{
name|position
operator|+=
operator|(
name|t
operator|.
name|getPositionIncrement
argument_list|()
operator|-
literal|1
operator|)
expr_stmt|;
name|position
operator|++
expr_stmt|;
name|String
name|name
init|=
name|t
operator|.
name|termText
argument_list|()
decl_stmt|;
name|Integer
name|Count
init|=
operator|(
name|Integer
operator|)
name|tokenHash
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|Count
operator|==
literal|null
condition|)
block|{
comment|// not in there yet
name|tokenHash
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//first one
block|}
else|else
block|{
name|int
name|count
init|=
name|Count
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|tokenHash
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Integer
argument_list|(
name|count
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|>
name|maxFieldLength
condition|)
break|break;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|Entry
index|[]
name|sortedHash
init|=
name|getSortedHashtableEntries
argument_list|(
name|tokenHash
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|sortedHash
operator|.
name|length
operator|&&
name|ii
operator|<
literal|10
condition|;
name|ii
operator|++
control|)
block|{
name|Entry
name|currentEntry
init|=
name|sortedHash
index|[
name|ii
index|]
decl_stmt|;
name|message
argument_list|(
operator|(
name|ii
operator|+
literal|1
operator|)
operator|+
literal|":"
operator|+
name|currentEntry
operator|.
name|getKey
argument_list|()
operator|+
literal|" "
operator|+
name|currentEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Provides a list of the top terms of the index.    *    * @param field  - the name of the command or null for all of them.    */
DECL|method|terms
specifier|public
name|void
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeMap
name|termMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|IndexReader
name|indexReader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|TermEnum
name|terms
init|=
name|indexReader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|terms
operator|.
name|next
argument_list|()
condition|)
block|{
name|Term
name|term
init|=
name|terms
operator|.
name|term
argument_list|()
decl_stmt|;
comment|//message(term.field() + ":" + term.text() + " freq:" + terms.docFreq());
comment|//if we're either not looking by field or we're matching the specific field
if|if
condition|(
operator|(
name|field
operator|==
literal|null
operator|)
operator|||
name|field
operator|.
name|equals
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
condition|)
name|termMap
operator|.
name|put
argument_list|(
name|term
operator|.
name|field
argument_list|()
operator|+
literal|":"
operator|+
name|term
operator|.
name|text
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
operator|(
name|terms
operator|.
name|docFreq
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Iterator
name|termIterator
init|=
name|termMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|termIterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|ii
operator|<
literal|100
condition|;
name|ii
operator|++
control|)
block|{
name|String
name|termDetails
init|=
operator|(
name|String
operator|)
name|termIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Integer
name|termFreq
init|=
operator|(
name|Integer
operator|)
name|termMap
operator|.
name|get
argument_list|(
name|termDetails
argument_list|)
decl_stmt|;
name|message
argument_list|(
name|termDetails
operator|+
literal|": "
operator|+
name|termFreq
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Sort Hashtable values    * @param h the hashtable we're sorting    * from http://developer.java.sun.com/developer/qow/archive/170/index.jsp    */
specifier|public
specifier|static
name|Entry
index|[]
DECL|method|getSortedHashtableEntries
name|getSortedHashtableEntries
parameter_list|(
name|Hashtable
name|h
parameter_list|)
block|{
name|Set
name|set
init|=
name|h
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|Entry
index|[]
name|entries
init|=
operator|(
name|Entry
index|[]
operator|)
name|set
operator|.
name|toArray
argument_list|(
operator|new
name|Entry
index|[
name|set
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|Object
name|v1
init|=
operator|(
operator|(
name|Entry
operator|)
name|o1
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|v2
init|=
operator|(
operator|(
name|Entry
operator|)
name|o2
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|Comparable
operator|)
name|v2
operator|)
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
comment|//descending order
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|entries
return|;
block|}
block|}
end_class

end_unit

