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
name|FSDirectory
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
name|PrintStream
import|;
end_import

begin_comment
comment|/**  * A class to modify an index, i.e. to delete and add documents. This  * class hides {@link IndexReader} and {@link IndexWriter} so that you  * do not need to care about implementation details such as that adding  * documents is done via IndexWriter and deletion is done via IndexReader.  *   *<p>Note that you cannot create more than one<code>IndexModifier</code> object  * on the same directory at the same time.  *   *<p>Example usage:  *<!-- ======================================================== --><!-- = Java Sourcecode to HTML automatically converted code = --><!-- =   Java2Html Converter V4.1 2004 by Markus Gebhard  markus@jave.de   = --><!-- =     Further information: http://www.java2html.de     = --><div align="left" class="java"><table border="0" cellpadding="3" cellspacing="0" bgcolor="#ffffff"><tr><!-- start source code --><td nowrap="nowrap" valign="top" align="left"><code><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">Analyzer&nbsp;analyzer&nbsp;=&nbsp;</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">StandardAnalyzer</font><font color="#000000">()</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#3f7f5f">//&nbsp;create&nbsp;an&nbsp;index&nbsp;in&nbsp;/tmp/index,&nbsp;overwriting&nbsp;an&nbsp;existing&nbsp;one:</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">IndexModifier&nbsp;indexModifier&nbsp;=&nbsp;</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">IndexModifier</font><font color="#000000">(</font><font color="#2a00ff">&#34;/tmp/index&#34;</font><font color="#000000">,&nbsp;analyzer,&nbsp;</font><font color="#7f0055"><b>true</b></font><font color="#000000">)</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">Document&nbsp;doc&nbsp;=&nbsp;</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">Document</font><font color="#000000">()</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">doc.add</font><font color="#000000">(</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">Field</font><font color="#000000">(</font><font color="#2a00ff">&#34;id&#34;</font><font color="#000000">,&nbsp;</font><font color="#2a00ff">&#34;1&#34;</font><font color="#000000">,&nbsp;Field.Store.YES,&nbsp;Field.Index.UN_TOKENIZED</font><font color="#000000">))</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">doc.add</font><font color="#000000">(</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">Field</font><font color="#000000">(</font><font color="#2a00ff">&#34;body&#34;</font><font color="#000000">,&nbsp;</font><font color="#2a00ff">&#34;a&nbsp;simple&nbsp;test&#34;</font><font color="#000000">,&nbsp;Field.Store.YES,&nbsp;Field.Index.TOKENIZED</font><font color="#000000">))</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">indexModifier.addDocument</font><font color="#000000">(</font><font color="#000000">doc</font><font color="#000000">)</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#7f0055"><b>int&nbsp;</b></font><font color="#000000">deleted&nbsp;=&nbsp;indexModifier.delete</font><font color="#000000">(</font><font color="#7f0055"><b>new&nbsp;</b></font><font color="#000000">Term</font><font color="#000000">(</font><font color="#2a00ff">&#34;id&#34;</font><font color="#000000">,&nbsp;</font><font color="#2a00ff">&#34;1&#34;</font><font color="#000000">))</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">System.out.println</font><font color="#000000">(</font><font color="#2a00ff">&#34;Deleted&nbsp;&#34;&nbsp;</font><font color="#000000">+&nbsp;deleted&nbsp;+&nbsp;</font><font color="#2a00ff">&#34;&nbsp;document&#34;</font><font color="#000000">)</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">indexModifier.flush</font><font color="#000000">()</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">System.out.println</font><font color="#000000">(</font><font color="#000000">indexModifier.docCount</font><font color="#000000">()&nbsp;</font><font color="#000000">+&nbsp;</font><font color="#2a00ff">&#34;&nbsp;docs&nbsp;in&nbsp;index&#34;</font><font color="#000000">)</font><font color="#000000">;</font><br/><font color="#ffffff">&nbsp;&nbsp;&nbsp;&nbsp;</font><font color="#000000">indexModifier.close</font><font color="#000000">()</font><font color="#000000">;</font></code></td><!-- end source code --></tr></table></div><!-- =       END of automatically generated HTML code       = --><!-- ======================================================== -->  *  *<p>Not all methods of IndexReader and IndexWriter are offered by this  * class. If you need access to additional methods, either use those classes  * directly or implement your own class that extends<code>IndexModifier</code>.  *  *<p>Although an instance of this class can be used from more than one  * thread, you will not get the best performance. You might want to use  * IndexReader and IndexWriter directly for that (but you will need to  * care about synchronization yourself then).  *  *<p>While you can freely mix calls to add() and delete() using this class,  * you should batch you calls for best performance. For example, if you  * want to update 20 documents, you should first delete all those documents,  * then add all the new documents.  *  * @author Daniel Naber  */
end_comment

begin_class
DECL|class|IndexModifier
specifier|public
class|class
name|IndexModifier
block|{
DECL|field|indexWriter
specifier|protected
name|IndexWriter
name|indexWriter
init|=
literal|null
decl_stmt|;
DECL|field|indexReader
specifier|protected
name|IndexReader
name|indexReader
init|=
literal|null
decl_stmt|;
DECL|field|directory
specifier|protected
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
DECL|field|analyzer
specifier|protected
name|Analyzer
name|analyzer
init|=
literal|null
decl_stmt|;
DECL|field|open
specifier|protected
name|boolean
name|open
init|=
literal|false
decl_stmt|;
comment|// Lucene defaults:
DECL|field|infoStream
specifier|protected
name|PrintStream
name|infoStream
init|=
literal|null
decl_stmt|;
DECL|field|useCompoundFile
specifier|protected
name|boolean
name|useCompoundFile
init|=
literal|true
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|protected
name|int
name|maxBufferedDocs
init|=
name|IndexWriter
operator|.
name|DEFAULT_MAX_BUFFERED_DOCS
decl_stmt|;
DECL|field|maxFieldLength
specifier|protected
name|int
name|maxFieldLength
init|=
name|IndexWriter
operator|.
name|DEFAULT_MAX_FIELD_LENGTH
decl_stmt|;
DECL|field|mergeFactor
specifier|protected
name|int
name|mergeFactor
init|=
name|IndexWriter
operator|.
name|DEFAULT_MERGE_FACTOR
decl_stmt|;
comment|/**    * Open an index with write access.    *    * @param directory the index directory    * @param analyzer the analyzer to use for adding new documents    * @param create<code>true</code> to create the index or overwrite the existing one;    *<code>false</code> to append to the existing index    */
DECL|method|IndexModifier
specifier|public
name|IndexModifier
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|init
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/**    * Open an index with write access.    *    * @param dirName the index directory    * @param analyzer the analyzer to use for adding new documents    * @param create<code>true</code> to create the index or overwrite the existing one;    *<code>false</code> to append to the existing index    */
DECL|method|IndexModifier
specifier|public
name|IndexModifier
parameter_list|(
name|String
name|dirName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/**    * Open an index with write access.    *    * @param file the index directory    * @param analyzer the analyzer to use for adding new documents    * @param create<code>true</code> to create the index or overwrite the existing one;    *<code>false</code> to append to the existing index    */
DECL|method|IndexModifier
specifier|public
name|IndexModifier
parameter_list|(
name|File
name|file
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|init
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize an IndexWriter.    * @throws IOException    */
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|directory
init|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
expr_stmt|;
name|open
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Throw an IllegalStateException if the index is closed.    * @throws IllegalStateException    */
DECL|method|assureOpen
specifier|protected
name|void
name|assureOpen
parameter_list|()
block|{
if|if
condition|(
operator|!
name|open
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Index is closed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Close the IndexReader and open an IndexWriter.    * @throws IOException    */
DECL|method|createIndexWriter
specifier|protected
name|void
name|createIndexWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexWriter
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|indexReader
operator|!=
literal|null
condition|)
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
literal|null
expr_stmt|;
block|}
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|setInfoStream
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|setMaxFieldLength
argument_list|(
name|maxFieldLength
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Close the IndexWriter and open an IndexReader.    * @throws IOException    */
DECL|method|createIndexReader
specifier|protected
name|void
name|createIndexReader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexReader
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
literal|null
expr_stmt|;
block|}
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Make sure all changes are written to disk.    * @throws IOException    */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
literal|null
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
literal|null
expr_stmt|;
name|createIndexReader
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Adds a document to this index, using the provided analyzer instead of the    * one specific in the constructor.  If the document contains more than    * {@link #setMaxFieldLength(int)} terms for a given field, the remainder are    * discarded.    * @see IndexWriter#addDocument(Document, Analyzer)    * @throws IllegalStateException if the index is closed    */
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Analyzer
name|docAnalyzer
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
if|if
condition|(
name|docAnalyzer
operator|!=
literal|null
condition|)
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|docAnalyzer
argument_list|)
expr_stmt|;
else|else
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Adds a document to this index.  If the document contains more than    * {@link #setMaxFieldLength(int)} terms for a given field, the remainder are    * discarded.    * @see IndexWriter#addDocument(Document)    * @throws IllegalStateException if the index is closed    */
DECL|method|addDocument
specifier|public
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|addDocument
argument_list|(
name|doc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes all documents containing<code>term</code>.    * This is useful if one uses a document field to hold a unique ID string for    * the document.  Then to delete such a document, one merely constructs a    * term with the appropriate field and the unique ID string as its text and    * passes it to this method.  Returns the number of documents deleted.    * @return the number of documents deleted    * @see IndexReader#deleteDocuments(Term)    * @throws IllegalStateException if the index is closed    */
DECL|method|deleteDocuments
specifier|public
name|int
name|deleteDocuments
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexReader
argument_list|()
expr_stmt|;
return|return
name|indexReader
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
comment|/**    * Deletes the document numbered<code>docNum</code>.    * @see IndexReader#deleteDocument(int)    * @throws IllegalStateException if the index is closed    */
DECL|method|deleteDocument
specifier|public
name|void
name|deleteDocument
parameter_list|(
name|int
name|docNum
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexReader
argument_list|()
expr_stmt|;
name|indexReader
operator|.
name|deleteDocument
argument_list|(
name|docNum
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of documents currently in this index.    * @see IndexWriter#docCount()    * @see IndexReader#numDocs()    * @throws IllegalStateException if the index is closed    */
DECL|method|docCount
specifier|public
name|int
name|docCount
parameter_list|()
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
return|return
name|indexWriter
operator|.
name|docCount
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|indexReader
operator|.
name|numDocs
argument_list|()
return|;
block|}
block|}
block|}
comment|/**    * Merges all segments together into a single segment, optimizing an index    * for search.    * @see IndexWriter#optimize()    * @throws IllegalStateException if the index is closed    */
DECL|method|optimize
specifier|public
name|void
name|optimize
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * If non-null, information about merges and a message when    * {@link #getMaxFieldLength()} is reached will be printed to this.    *<p>Example:<tt>index.setInfoStream(System.err);</tt>    * @see IndexWriter#setInfoStream(PrintStream)    * @throws IllegalStateException if the index is closed    */
DECL|method|setInfoStream
specifier|public
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|setInfoStream
argument_list|(
name|infoStream
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
block|}
comment|/**    * @throws IOException    * @see IndexModifier#setInfoStream(PrintStream)    */
DECL|method|getInfoStream
specifier|public
name|PrintStream
name|getInfoStream
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
return|return
name|indexWriter
operator|.
name|getInfoStream
argument_list|()
return|;
block|}
block|}
comment|/**    * Setting to turn on usage of a compound file. When on, multiple files    * for each segment are merged into a single file once the segment creation    * is finished. This is done regardless of what directory is in use.    * @see IndexWriter#setUseCompoundFile(boolean)    * @throws IllegalStateException if the index is closed    */
DECL|method|setUseCompoundFile
specifier|public
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
block|}
comment|/**    * @throws IOException    * @see IndexModifier#setUseCompoundFile(boolean)    */
DECL|method|getUseCompoundFile
specifier|public
name|boolean
name|getUseCompoundFile
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
return|return
name|indexWriter
operator|.
name|getUseCompoundFile
argument_list|()
return|;
block|}
block|}
comment|/**    * The maximum number of terms that will be indexed for a single field in a    * document.  This limits the amount of memory required for indexing, so that    * collections with very large files will not crash the indexing process by    * running out of memory.<p/>    * Note that this effectively truncates large documents, excluding from the    * index terms that occur further in the document.  If you know your source    * documents are large, be sure to set this value high enough to accomodate    * the expected size.  If you set it to Integer.MAX_VALUE, then the only limit    * is your memory, but you should anticipate an OutOfMemoryError.<p/>    * By default, no more than 10,000 terms will be indexed for a field.    * @see IndexWriter#setMaxFieldLength(int)    * @throws IllegalStateException if the index is closed    */
DECL|method|setMaxFieldLength
specifier|public
name|void
name|setMaxFieldLength
parameter_list|(
name|int
name|maxFieldLength
parameter_list|)
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|setMaxFieldLength
argument_list|(
name|maxFieldLength
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|maxFieldLength
operator|=
name|maxFieldLength
expr_stmt|;
block|}
block|}
comment|/**    * @throws IOException    * @see IndexModifier#setMaxFieldLength(int)    */
DECL|method|getMaxFieldLength
specifier|public
name|int
name|getMaxFieldLength
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
return|return
name|indexWriter
operator|.
name|getMaxFieldLength
argument_list|()
return|;
block|}
block|}
comment|/**    * Determines the minimal number of documents required before the buffered    * in-memory documents are merging and a new Segment is created.    * Since Documents are merged in a {@link org.apache.lucene.store.RAMDirectory},    * large value gives faster indexing.  At the same time, mergeFactor limits    * the number of files open in a FSDirectory.    *    *<p>The default value is 10.    *    * @see IndexWriter#setMaxBufferedDocs(int)    * @throws IllegalStateException if the index is closed    * @throws IllegalArgumentException if maxBufferedDocs is smaller than 2    */
DECL|method|setMaxBufferedDocs
specifier|public
name|void
name|setMaxBufferedDocs
parameter_list|(
name|int
name|maxBufferedDocs
parameter_list|)
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|maxBufferedDocs
operator|=
name|maxBufferedDocs
expr_stmt|;
block|}
block|}
comment|/**    * @throws IOException    * @see IndexModifier#setMaxBufferedDocs(int)    */
DECL|method|getMaxBufferedDocs
specifier|public
name|int
name|getMaxBufferedDocs
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
return|return
name|indexWriter
operator|.
name|getMaxBufferedDocs
argument_list|()
return|;
block|}
block|}
comment|/**    * Determines how often segment indices are merged by addDocument().  With    * smaller values, less RAM is used while indexing, and searches on    * unoptimized indices are faster, but indexing speed is slower.  With larger    * values, more RAM is used during indexing, and while searches on unoptimized    * indices are slower, indexing is faster.  Thus larger values (&gt; 10) are best    * for batch index creation, and smaller values (&lt; 10) for indices that are    * interactively maintained.    *<p>This must never be less than 2.  The default value is 10.    *    * @see IndexWriter#setMergeFactor(int)    * @throws IllegalStateException if the index is closed    */
DECL|method|setMergeFactor
specifier|public
name|void
name|setMergeFactor
parameter_list|(
name|int
name|mergeFactor
parameter_list|)
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
block|}
block|}
comment|/**    * @throws IOException    * @see IndexModifier#setMergeFactor(int)    */
DECL|method|getMergeFactor
specifier|public
name|int
name|getMergeFactor
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
name|assureOpen
argument_list|()
expr_stmt|;
name|createIndexWriter
argument_list|()
expr_stmt|;
return|return
name|indexWriter
operator|.
name|getMergeFactor
argument_list|()
return|;
block|}
block|}
comment|/**    * Close this index, writing all pending changes to disk.    *    * @throws IllegalStateException if the index has been closed before already    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
if|if
condition|(
operator|!
name|open
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Index is closed already"
argument_list|)
throw|;
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
literal|null
expr_stmt|;
block|}
name|open
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Index@"
operator|+
name|directory
return|;
block|}
comment|/*   // used as an example in the javadoc:   public static void main(String[] args) throws IOException {     Analyzer analyzer = new StandardAnalyzer();     // create an index in /tmp/index, overwriting an existing one:     IndexModifier indexModifier = new IndexModifier("/tmp/index", analyzer, true);     Document doc = new Document();     doc.add(new Fieldable("id", "1", Fieldable.Store.YES, Fieldable.Index.UN_TOKENIZED));     doc.add(new Fieldable("body", "a simple test", Fieldable.Store.YES, Fieldable.Index.TOKENIZED));     indexModifier.addDocument(doc);     int deleted = indexModifier.delete(new Term("id", "1"));     System.out.println("Deleted " + deleted + " document");     indexModifier.flush();     System.out.println(indexModifier.docCount() + " docs in index");     indexModifier.close();   }*/
block|}
end_class

end_unit

