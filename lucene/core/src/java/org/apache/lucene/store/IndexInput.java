begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**   * Abstract base class for input from a file in a {@link Directory}.  A  * random-access input stream.  Used for all Lucene index input operations.  *  *<p>{@code IndexInput} may only be used from one thread, because it is not  * thread safe (it keeps internal state like file position). To allow  * multithreaded use, every {@code IndexInput} instance must be cloned before  * it is used in another thread. Subclasses must therefore implement {@link #clone()},  * returning a new {@code IndexInput} which operates on the same underlying  * resource, but positioned independently.   *   *<p><b>Warning:</b> Lucene never closes cloned  * {@code IndexInput}s, it will only call {@link #close()} on the original object.  *   *<p>If you access the cloned IndexInput after closing the original object,  * any<code>readXXX</code> methods will throw {@link AlreadyClosedException}.  *  * @see Directory  */
end_comment

begin_class
DECL|class|IndexInput
specifier|public
specifier|abstract
class|class
name|IndexInput
extends|extends
name|DataInput
implements|implements
name|Cloneable
implements|,
name|Closeable
block|{
DECL|field|resourceDescription
specifier|private
specifier|final
name|String
name|resourceDescription
decl_stmt|;
comment|/** resourceDescription should be a non-null, opaque string    *  describing this resource; it's returned from    *  {@link #toString}. */
DECL|method|IndexInput
specifier|protected
name|IndexInput
parameter_list|(
name|String
name|resourceDescription
parameter_list|)
block|{
if|if
condition|(
name|resourceDescription
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"resourceDescription must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|resourceDescription
operator|=
name|resourceDescription
expr_stmt|;
block|}
comment|/** Closes the stream to further operations. */
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
comment|/** Returns the current position in this file, where the next read will    * occur.    * @see #seek(long)    */
DECL|method|getFilePointer
specifier|public
specifier|abstract
name|long
name|getFilePointer
parameter_list|()
function_decl|;
comment|/** Sets current position in this file, where the next read will occur.  If this is    *  beyond the end of the file then this will throw {@code EOFException} and then the    *  stream is in an undetermined state.    *    * @see #getFilePointer()    */
DECL|method|seek
specifier|public
specifier|abstract
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** The number of bytes in the file. */
DECL|method|length
specifier|public
specifier|abstract
name|long
name|length
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|resourceDescription
return|;
block|}
comment|/** {@inheritDoc}    *     *<p><b>Warning:</b> Lucene never closes cloned    * {@code IndexInput}s, it will only call {@link #close()} on the original object.    *     *<p>If you access the cloned IndexInput after closing the original object,    * any<code>readXXX</code> methods will throw {@link AlreadyClosedException}.    *    *<p>This method is NOT thread safe, so if the current {@code IndexInput}    * is being used by one thread while {@code clone} is called by another,    * disaster could strike.    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|IndexInput
name|clone
parameter_list|()
block|{
return|return
operator|(
name|IndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Creates a slice of this index input, with the given description, offset, and length.     * The slice is seeked to the beginning.    */
DECL|method|slice
specifier|public
specifier|abstract
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Subclasses call this to get the String for resourceDescription of a slice of this {@code IndexInput}. */
DECL|method|getFullSliceDescription
specifier|protected
name|String
name|getFullSliceDescription
parameter_list|(
name|String
name|sliceDescription
parameter_list|)
block|{
if|if
condition|(
name|sliceDescription
operator|==
literal|null
condition|)
block|{
comment|// Clones pass null sliceDescription:
return|return
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|toString
argument_list|()
operator|+
literal|" [slice="
operator|+
name|sliceDescription
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**    * Creates a random-access slice of this index input, with the given offset and length.     *<p>    * The default implementation calls {@link #slice}, and it doesn't support random access,    * it implements absolute reads as seek+read.    */
DECL|method|randomAccessSlice
specifier|public
name|RandomAccessInput
name|randomAccessSlice
parameter_list|(
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|slice
init|=
name|slice
argument_list|(
literal|"randomaccess"
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|instanceof
name|RandomAccessInput
condition|)
block|{
comment|// slice() already supports random access
return|return
operator|(
name|RandomAccessInput
operator|)
name|slice
return|;
block|}
else|else
block|{
comment|// return default impl
return|return
operator|new
name|RandomAccessInput
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|slice
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|slice
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|readShort
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|slice
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|slice
operator|.
name|readShort
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|readInt
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|slice
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|slice
operator|.
name|readInt
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|readLong
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|slice
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|slice
operator|.
name|readLong
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

