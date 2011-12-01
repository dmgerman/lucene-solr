begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|NoSuchElementException
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
name|suggest
operator|.
name|fst
operator|.
name|Sort
operator|.
name|ByteSequencesReader
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
comment|/**  * Builds and iterates over sequences stored on disk.  */
end_comment

begin_class
DECL|class|ExternalRefSorter
specifier|public
class|class
name|ExternalRefSorter
implements|implements
name|BytesRefSorter
implements|,
name|Closeable
block|{
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|field|writer
specifier|private
name|Sort
operator|.
name|ByteSequencesWriter
name|writer
decl_stmt|;
DECL|field|input
specifier|private
name|File
name|input
decl_stmt|;
DECL|field|sorted
specifier|private
name|File
name|sorted
decl_stmt|;
comment|/**    * Will buffer all sequences to a temporary file and then sort (all on-disk).    */
DECL|method|ExternalRefSorter
specifier|public
name|ExternalRefSorter
parameter_list|(
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"RefSorter-"
argument_list|,
literal|".raw"
argument_list|,
name|Sort
operator|.
name|defaultTempDir
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|Sort
operator|.
name|ByteSequencesWriter
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|BytesRef
name|utf8
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
name|writer
operator|.
name|write
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sorted
operator|==
literal|null
condition|)
block|{
name|closeWriter
argument_list|()
expr_stmt|;
name|sorted
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"RefSorter-"
argument_list|,
literal|".sorted"
argument_list|,
name|Sort
operator|.
name|defaultTempDir
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|.
name|sort
argument_list|(
name|input
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
name|input
operator|.
name|delete
argument_list|()
expr_stmt|;
name|input
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|ByteSequenceIterator
argument_list|(
operator|new
name|Sort
operator|.
name|ByteSequencesReader
argument_list|(
name|sorted
argument_list|)
argument_list|)
return|;
block|}
DECL|method|closeWriter
specifier|private
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Removes any written temporary files.    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|closeWriter
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
name|input
operator|.
name|delete
argument_list|()
expr_stmt|;
if|if
condition|(
name|sorted
operator|!=
literal|null
condition|)
name|sorted
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Iterate over byte refs in a file.    */
DECL|class|ByteSequenceIterator
class|class
name|ByteSequenceIterator
implements|implements
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|reader
specifier|private
name|ByteSequencesReader
name|reader
decl_stmt|;
DECL|field|next
specifier|private
name|byte
index|[]
name|next
decl_stmt|;
DECL|method|ByteSequenceIterator
specifier|public
name|ByteSequenceIterator
parameter_list|(
name|ByteSequencesReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
name|BytesRef
name|r
init|=
operator|new
name|BytesRef
argument_list|(
name|next
argument_list|)
decl_stmt|;
try|try
block|{
name|next
operator|=
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
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
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

