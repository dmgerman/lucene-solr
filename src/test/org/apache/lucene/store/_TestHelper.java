begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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

begin_comment
comment|/** This class provides access to package-level features defined in the  *  store package. It is used for testing only.  */
end_comment

begin_class
DECL|class|_TestHelper
specifier|public
class|class
name|_TestHelper
block|{
comment|/** Returns true if the instance of the provided input stream is actually      *  an FSIndexInput.      */
DECL|method|isFSIndexInput
specifier|public
specifier|static
name|boolean
name|isFSIndexInput
parameter_list|(
name|IndexInput
name|is
parameter_list|)
block|{
return|return
name|is
operator|instanceof
name|FSIndexInput
return|;
block|}
comment|/** Returns true if the provided input stream is an FSIndexInput and      *  is a clone, that is it does not own its underlying file descriptor.      */
DECL|method|isFSIndexInputClone
specifier|public
specifier|static
name|boolean
name|isFSIndexInputClone
parameter_list|(
name|IndexInput
name|is
parameter_list|)
block|{
if|if
condition|(
name|isFSIndexInput
argument_list|(
name|is
argument_list|)
condition|)
block|{
return|return
operator|(
operator|(
name|FSIndexInput
operator|)
name|is
operator|)
operator|.
name|isClone
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/** Given an instance of FSDirectory.FSIndexInput, this method returns      *  true if the underlying file descriptor is valid, and false otherwise.      *  This can be used to determine if the OS file has been closed.      *  The descriptor becomes invalid when the non-clone instance of the      *  FSIndexInput that owns this descriptor is closed. However, the      *  descriptor may possibly become invalid in other ways as well.      */
DECL|method|isFSIndexInputOpen
specifier|public
specifier|static
name|boolean
name|isFSIndexInputOpen
parameter_list|(
name|IndexInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isFSIndexInput
argument_list|(
name|is
argument_list|)
condition|)
block|{
name|FSIndexInput
name|fis
init|=
operator|(
name|FSIndexInput
operator|)
name|is
decl_stmt|;
return|return
name|fis
operator|.
name|isFDValid
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

