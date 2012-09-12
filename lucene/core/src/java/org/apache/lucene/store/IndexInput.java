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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/** Abstract base class for input from a file in a {@link Directory}.  A  * random-access input stream.  Used for all Lucene index input operations.  *  *<p>{@code IndexInput} may only be used from one thread, because it is not  * thread safe (it keeps internal state like file position). To allow  * multithreaded use, every {@code IndexInput} instance must be cloned before  * used in another thread. Subclasses must therefore implement {@link #clone()},  * returning a new {@code IndexInput} which operates on the same underlying  * resource, but positioned independently. Lucene never closes cloned  * {@code IndexInput}s, it will only do this on the original one.  * The original instance must take care that cloned instances throw  * {@link AlreadyClosedException} when the original one is closed.    * @see Directory  */
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
comment|/** Sets current position in this file, where the next read will occur.    * @see #getFilePointer()    */
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
comment|/** {@inheritDoc}    *<p><b>Warning:</b> Lucene never closes cloned    * {@code IndexInput}s, it will only do this on the original one.    * The original instance must take care that cloned instances throw    * {@link AlreadyClosedException} when the original one is closed.    */
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
block|}
end_class

end_unit

