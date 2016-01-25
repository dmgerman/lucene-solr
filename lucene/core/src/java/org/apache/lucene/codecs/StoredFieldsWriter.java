begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/*  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|document
operator|.
name|StoredField
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
name|FieldInfo
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
name|FieldInfos
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
name|IndexableField
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
name|IndexableFieldType
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
name|MergeState
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
name|StoredFieldVisitor
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
name|Bits
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

begin_comment
comment|/**  * Codec API for writing stored fields:  *<ol>  *<li>For every document, {@link #startDocument()} is called,  *       informing the Codec that a new document has started.  *<li>{@link #writeField(FieldInfo, IndexableField)} is called for   *       each field in the document.  *<li>After all documents have been written, {@link #finish(FieldInfos, int)}   *       is called for verification/sanity-checks.  *<li>Finally the writer is closed ({@link #close()})  *</ol>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|StoredFieldsWriter
specifier|public
specifier|abstract
class|class
name|StoredFieldsWriter
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|StoredFieldsWriter
specifier|protected
name|StoredFieldsWriter
parameter_list|()
block|{   }
comment|/** Called before writing the stored fields of the document.    *  {@link #writeField(FieldInfo, IndexableField)} will be called    *  for each stored field. Note that this is    *  called even if the document has no stored fields. */
DECL|method|startDocument
specifier|public
specifier|abstract
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Called when a document and all its fields have been added. */
DECL|method|finishDocument
specifier|public
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/** Writes a single stored field. */
DECL|method|writeField
specifier|public
specifier|abstract
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|IndexableField
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called before {@link #close()}, passing in the number    *  of documents that were written. Note that this is     *  intentionally redundant (equivalent to the number of    *  calls to {@link #startDocument()}, but a Codec should    *  check that this is the case to detect the JRE bug described     *  in LUCENE-1282. */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Merges in the stored fields from the readers in     *<code>mergeState</code>. The default implementation skips    *  over deleted documents, and uses {@link #startDocument()},    *  {@link #writeField(FieldInfo, IndexableField)}, and {@link #finish(FieldInfos, int)},    *  returning the number of documents that were written.    *  Implementations can override this method for more sophisticated    *  merging (bulk-byte copying, etc). */
DECL|method|merge
specifier|public
name|int
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docCount
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
name|mergeState
operator|.
name|storedFieldsReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|StoredFieldsReader
name|storedFieldsReader
init|=
name|mergeState
operator|.
name|storedFieldsReaders
index|[
name|i
index|]
decl_stmt|;
name|storedFieldsReader
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
name|MergeVisitor
name|visitor
init|=
operator|new
name|MergeVisitor
argument_list|(
name|mergeState
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|int
name|maxDoc
init|=
name|mergeState
operator|.
name|maxDocs
index|[
name|i
index|]
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|mergeState
operator|.
name|liveDocs
index|[
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
comment|// skip deleted docs
continue|continue;
block|}
name|startDocument
argument_list|()
expr_stmt|;
name|storedFieldsReader
operator|.
name|visitDocument
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
name|finishDocument
argument_list|()
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
block|}
block|}
name|finish
argument_list|(
name|mergeState
operator|.
name|mergeFieldInfos
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
return|return
name|docCount
return|;
block|}
comment|/**     * A visitor that adds every field it sees.    *<p>    * Use like this:    *<pre>    * MergeVisitor visitor = new MergeVisitor(mergeState, readerIndex);    * for (...) {    *   startDocument();    *   storedFieldsReader.visitDocument(docID, visitor);    *   finishDocument();    * }    *</pre>    */
DECL|class|MergeVisitor
specifier|protected
class|class
name|MergeVisitor
extends|extends
name|StoredFieldVisitor
implements|implements
name|IndexableField
block|{
DECL|field|binaryValue
name|BytesRef
name|binaryValue
decl_stmt|;
DECL|field|stringValue
name|String
name|stringValue
decl_stmt|;
DECL|field|numericValue
name|Number
name|numericValue
decl_stmt|;
DECL|field|currentField
name|FieldInfo
name|currentField
decl_stmt|;
DECL|field|remapper
name|FieldInfos
name|remapper
decl_stmt|;
comment|/**      * Create new merge visitor.      */
DECL|method|MergeVisitor
specifier|public
name|MergeVisitor
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|int
name|readerIndex
parameter_list|)
block|{
comment|// if field numbers are aligned, we can save hash lookups
comment|// on every field access. Otherwise, we need to lookup
comment|// fieldname each time, and remap to a new number.
for|for
control|(
name|FieldInfo
name|fi
range|:
name|mergeState
operator|.
name|fieldInfos
index|[
name|readerIndex
index|]
control|)
block|{
name|FieldInfo
name|other
init|=
name|mergeState
operator|.
name|mergeFieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fi
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
name|other
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fi
operator|.
name|name
argument_list|)
condition|)
block|{
name|remapper
operator|=
name|mergeState
operator|.
name|mergeFieldInfos
expr_stmt|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|binaryField
specifier|public
name|void
name|binaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|// TODO: can we avoid new BR here?
name|binaryValue
operator|=
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|write
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stringField
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|// TODO: can we avoid new String here?
name|stringValue
operator|=
operator|new
name|String
argument_list|(
name|value
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|write
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intField
specifier|public
name|void
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|numericValue
operator|=
name|value
expr_stmt|;
name|write
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|longField
specifier|public
name|void
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|numericValue
operator|=
name|value
expr_stmt|;
name|write
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|floatField
specifier|public
name|void
name|floatField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|numericValue
operator|=
name|value
expr_stmt|;
name|write
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doubleField
specifier|public
name|void
name|doubleField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|reset
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|numericValue
operator|=
name|value
expr_stmt|;
name|write
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsField
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Status
operator|.
name|YES
return|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|currentField
operator|.
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|IndexableFieldType
name|fieldType
parameter_list|()
block|{
return|return
name|StoredField
operator|.
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
return|return
name|binaryValue
return|;
block|}
annotation|@
name|Override
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|stringValue
return|;
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
return|return
name|numericValue
return|;
block|}
annotation|@
name|Override
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
literal|1F
return|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|reset
name|void
name|reset
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
if|if
condition|(
name|remapper
operator|!=
literal|null
condition|)
block|{
comment|// field numbers are not aligned, we need to remap to the new field number
name|currentField
operator|=
name|remapper
operator|.
name|fieldInfo
argument_list|(
name|field
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentField
operator|=
name|field
expr_stmt|;
block|}
name|binaryValue
operator|=
literal|null
expr_stmt|;
name|stringValue
operator|=
literal|null
expr_stmt|;
name|numericValue
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|write
name|void
name|write
parameter_list|()
throws|throws
name|IOException
block|{
name|writeField
argument_list|(
name|currentField
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

