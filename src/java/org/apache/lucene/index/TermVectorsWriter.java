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
name|OutputStream
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
name|StringHelper
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
name|util
operator|.
name|Vector
import|;
end_import

begin_comment
comment|/**  * Writer works by opening a document and then opening the fields within the document and then  * writing out the vectors for each field.  *   * Rough usage:  *<CODE>  for each document  {  writer.openDocument();  for each field on the document  {  writer.openField(field);  for all of the terms  {  writer.addTerm(...)  }  writer.closeField  }  writer.closeDocument()      }</CODE>  */
end_comment

begin_class
DECL|class|TermVectorsWriter
specifier|final
class|class
name|TermVectorsWriter
block|{
DECL|field|FORMAT_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_VERSION
init|=
literal|1
decl_stmt|;
comment|//The size in bytes that the FORMAT_VERSION will take up at the beginning of each file
DECL|field|FORMAT_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|FORMAT_SIZE
init|=
literal|4
decl_stmt|;
comment|//TODO: Figure out how to write with or w/o position information and read back in
DECL|field|TVX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TVX_EXTENSION
init|=
literal|".tvx"
decl_stmt|;
DECL|field|TVD_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TVD_EXTENSION
init|=
literal|".tvd"
decl_stmt|;
DECL|field|TVF_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|TVF_EXTENSION
init|=
literal|".tvf"
decl_stmt|;
DECL|field|tvx
DECL|field|tvd
DECL|field|tvf
specifier|private
name|OutputStream
name|tvx
init|=
literal|null
decl_stmt|,
name|tvd
init|=
literal|null
decl_stmt|,
name|tvf
init|=
literal|null
decl_stmt|;
DECL|field|fields
specifier|private
name|Vector
name|fields
init|=
literal|null
decl_stmt|;
DECL|field|terms
specifier|private
name|Vector
name|terms
init|=
literal|null
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|currentField
specifier|private
name|TVField
name|currentField
init|=
literal|null
decl_stmt|;
DECL|field|currentDocPointer
specifier|private
name|long
name|currentDocPointer
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Create term vectors writer for the specified segment in specified    *  directory.  A new TermVectorsWriter should be created for each    *  segment. The parameter<code>maxFields</code> indicates how many total    *  fields are found in this document. Not all of these fields may require    *  termvectors to be stored, so the number of calls to    *<code>openField</code> is less or equal to this number.    */
DECL|method|TermVectorsWriter
specifier|public
name|TermVectorsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Open files for TermVector storage
name|tvx
operator|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
name|TVX_EXTENSION
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeInt
argument_list|(
name|FORMAT_VERSION
argument_list|)
expr_stmt|;
name|tvd
operator|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
name|TVD_EXTENSION
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeInt
argument_list|(
name|FORMAT_VERSION
argument_list|)
expr_stmt|;
name|tvf
operator|=
name|directory
operator|.
name|createFile
argument_list|(
name|segment
operator|+
name|TVF_EXTENSION
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeInt
argument_list|(
name|FORMAT_VERSION
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
name|fields
operator|=
operator|new
name|Vector
argument_list|(
name|fieldInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
block|}
DECL|method|openDocument
specifier|public
specifier|final
name|void
name|openDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|closeDocument
argument_list|()
expr_stmt|;
name|currentDocPointer
operator|=
name|tvd
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|closeDocument
specifier|public
specifier|final
name|void
name|closeDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isDocumentOpen
argument_list|()
condition|)
block|{
name|closeField
argument_list|()
expr_stmt|;
name|writeDoc
argument_list|()
expr_stmt|;
name|fields
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentDocPointer
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|isDocumentOpen
specifier|public
specifier|final
name|boolean
name|isDocumentOpen
parameter_list|()
block|{
return|return
name|currentDocPointer
operator|!=
operator|-
literal|1
return|;
block|}
comment|/** Start processing a field. This can be followed by a number of calls to    *  addTerm, and a final call to closeField to indicate the end of    *  processing of this field. If a field was previously open, it is    *  closed automatically.    */
DECL|method|openField
specifier|public
specifier|final
name|void
name|openField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isDocumentOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot open field when no document is open."
argument_list|)
throw|;
name|closeField
argument_list|()
expr_stmt|;
name|currentField
operator|=
operator|new
name|TVField
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Finished processing current field. This should be followed by a call to    *  openField before future calls to addTerm.    */
DECL|method|closeField
specifier|public
specifier|final
name|void
name|closeField
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isFieldOpen
argument_list|()
condition|)
block|{
comment|/* DEBUG */
comment|//System.out.println("closeField()");
comment|/* DEBUG */
comment|// save field and terms
name|writeField
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|currentField
argument_list|)
expr_stmt|;
name|terms
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentField
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Return true if a field is currently open. */
DECL|method|isFieldOpen
specifier|public
specifier|final
name|boolean
name|isFieldOpen
parameter_list|()
block|{
return|return
name|currentField
operator|!=
literal|null
return|;
block|}
comment|/** Add term to the field's term vector. Field must already be open    *  of NullPointerException is thrown. Terms should be added in    *  increasing order of terms, one call per unique termNum. ProxPointer    *  is a pointer into the TermPosition file (prx). Freq is the number of    *  times this term appears in this field, in this document.    */
DECL|method|addTerm
specifier|public
specifier|final
name|void
name|addTerm
parameter_list|(
name|String
name|termText
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isDocumentOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add terms when document is not open"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|isFieldOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add terms when field is not open"
argument_list|)
throw|;
name|addTermInternal
argument_list|(
name|termText
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
DECL|method|addTermInternal
specifier|private
specifier|final
name|void
name|addTermInternal
parameter_list|(
name|String
name|termText
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|currentField
operator|.
name|length
operator|+=
name|freq
expr_stmt|;
name|TVTerm
name|term
init|=
operator|new
name|TVTerm
argument_list|()
decl_stmt|;
name|term
operator|.
name|termText
operator|=
name|termText
expr_stmt|;
name|term
operator|.
name|freq
operator|=
name|freq
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
comment|/** Add specified vectors to the document.    */
DECL|method|addVectors
specifier|public
specifier|final
name|void
name|addVectors
parameter_list|(
name|TermFreqVector
index|[]
name|vectors
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isDocumentOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add term vectors when document is not open"
argument_list|)
throw|;
if|if
condition|(
name|isFieldOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add term vectors when field is open"
argument_list|)
throw|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vectors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addTermFreqVector
argument_list|(
name|vectors
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Add specified vector to the document. Document must be open but no field    *  should be open or exception is thrown. The same document can have<code>addTerm</code>    *  and<code>addVectors</code> calls mixed, however a given field must either be    *  populated with<code>addTerm</code> or with<code>addVector</code>.     *    */
DECL|method|addTermFreqVector
specifier|public
specifier|final
name|void
name|addTermFreqVector
parameter_list|(
name|TermFreqVector
name|vector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isDocumentOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add term vector when document is not open"
argument_list|)
throw|;
if|if
condition|(
name|isFieldOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot add term vector when field is open"
argument_list|)
throw|;
name|addTermFreqVectorInternal
argument_list|(
name|vector
argument_list|)
expr_stmt|;
block|}
DECL|method|addTermFreqVectorInternal
specifier|private
specifier|final
name|void
name|addTermFreqVectorInternal
parameter_list|(
name|TermFreqVector
name|vector
parameter_list|)
throws|throws
name|IOException
block|{
name|openField
argument_list|(
name|vector
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vector
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|addTermInternal
argument_list|(
name|vector
operator|.
name|getTerms
argument_list|()
index|[
name|i
index|]
argument_list|,
name|vector
operator|.
name|getTermFrequencies
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|closeField
argument_list|()
expr_stmt|;
block|}
comment|/** Close all streams. */
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|closeDocument
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// make an effort to close all streams we can but remember and re-throw
comment|// the first exception encountered in this process
name|IOException
name|keep
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
try|try
block|{
name|tvx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tvd
operator|!=
literal|null
condition|)
try|try
block|{
name|tvd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|tvf
operator|!=
literal|null
condition|)
try|try
block|{
name|tvf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|keep
operator|==
literal|null
condition|)
name|keep
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|keep
operator|!=
literal|null
condition|)
throw|throw
operator|(
name|IOException
operator|)
name|keep
operator|.
name|fillInStackTrace
argument_list|()
throw|;
block|}
block|}
DECL|method|writeField
specifier|private
name|void
name|writeField
parameter_list|()
throws|throws
name|IOException
block|{
comment|// remember where this field is written
name|currentField
operator|.
name|tvfPointer
operator|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|//System.out.println("Field Pointer: " + currentField.tvfPointer);
specifier|final
name|int
name|size
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|size
operator|=
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|currentField
operator|.
name|length
operator|-
name|size
argument_list|)
expr_stmt|;
name|String
name|lastTermText
init|=
literal|""
decl_stmt|;
comment|// write term ids and positions
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|TVTerm
name|term
init|=
operator|(
name|TVTerm
operator|)
name|terms
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//tvf.writeString(term.termText);
name|int
name|start
init|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|lastTermText
argument_list|,
name|term
operator|.
name|termText
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|term
operator|.
name|termText
operator|.
name|length
argument_list|()
operator|-
name|start
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|start
argument_list|)
expr_stmt|;
comment|// write shared prefix length
name|tvf
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
comment|// write delta length
name|tvf
operator|.
name|writeChars
argument_list|(
name|term
operator|.
name|termText
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// write delta chars
name|tvf
operator|.
name|writeVInt
argument_list|(
name|term
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastTermText
operator|=
name|term
operator|.
name|termText
expr_stmt|;
block|}
block|}
DECL|method|writeDoc
specifier|private
name|void
name|writeDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isFieldOpen
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Field is still open while writing document"
argument_list|)
throw|;
comment|//System.out.println("Writing doc pointer: " + currentDocPointer);
comment|// write document index record
name|tvx
operator|.
name|writeLong
argument_list|(
name|currentDocPointer
argument_list|)
expr_stmt|;
comment|// write document data record
specifier|final
name|int
name|size
decl_stmt|;
comment|// write the number of fields
name|tvd
operator|.
name|writeVInt
argument_list|(
name|size
operator|=
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// write field numbers
name|int
name|lastFieldNumber
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|TVField
name|field
init|=
operator|(
name|TVField
operator|)
name|fields
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
operator|-
name|lastFieldNumber
argument_list|)
expr_stmt|;
name|lastFieldNumber
operator|=
name|field
operator|.
name|number
expr_stmt|;
block|}
comment|// write field pointers
name|long
name|lastFieldPointer
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|TVField
name|field
init|=
operator|(
name|TVField
operator|)
name|fields
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|tvd
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|tvfPointer
operator|-
name|lastFieldPointer
argument_list|)
expr_stmt|;
name|lastFieldPointer
operator|=
name|field
operator|.
name|tvfPointer
expr_stmt|;
block|}
comment|//System.out.println("After writing doc pointer: " + tvx.getFilePointer());
block|}
DECL|class|TVField
specifier|private
specifier|static
class|class
name|TVField
block|{
DECL|field|number
name|int
name|number
decl_stmt|;
DECL|field|tvfPointer
name|long
name|tvfPointer
init|=
literal|0
decl_stmt|;
DECL|field|length
name|int
name|length
init|=
literal|0
decl_stmt|;
comment|// number of distinct term positions
DECL|method|TVField
name|TVField
parameter_list|(
name|int
name|number
parameter_list|)
block|{
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
block|}
block|}
DECL|class|TVTerm
specifier|private
specifier|static
class|class
name|TVTerm
block|{
DECL|field|termText
name|String
name|termText
decl_stmt|;
DECL|field|freq
name|int
name|freq
init|=
literal|0
decl_stmt|;
comment|//int positions[] = null;
block|}
block|}
end_class

end_unit

