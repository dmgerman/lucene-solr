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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|InputStream
import|;
end_import

begin_class
DECL|class|SegmentTermPositions
specifier|final
class|class
name|SegmentTermPositions
extends|extends
name|SegmentTermDocs
implements|implements
name|TermPositions
block|{
DECL|field|proxStream
specifier|private
name|InputStream
name|proxStream
decl_stmt|;
DECL|field|proxCount
specifier|private
name|int
name|proxCount
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
DECL|method|SegmentTermPositions
name|SegmentTermPositions
parameter_list|(
name|SegmentReader
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|this
operator|.
name|proxStream
operator|=
operator|(
name|InputStream
operator|)
name|parent
operator|.
name|proxStream
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
DECL|method|seek
specifier|final
name|void
name|seek
parameter_list|(
name|TermInfo
name|ti
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|seek
argument_list|(
name|ti
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
name|proxStream
operator|.
name|seek
argument_list|(
name|ti
operator|.
name|proxPointer
argument_list|)
expr_stmt|;
else|else
name|proxCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|proxStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|public
specifier|final
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|proxCount
operator|--
expr_stmt|;
return|return
name|position
operator|+=
name|proxStream
operator|.
name|readVInt
argument_list|()
return|;
block|}
DECL|method|skippingDoc
specifier|protected
specifier|final
name|void
name|skippingDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|f
init|=
name|freq
init|;
name|f
operator|>
literal|0
condition|;
name|f
operator|--
control|)
comment|// skip all positions
name|proxStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
DECL|method|next
specifier|public
specifier|final
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|f
init|=
name|proxCount
init|;
name|f
operator|>
literal|0
condition|;
name|f
operator|--
control|)
comment|// skip unread positions
name|proxStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|super
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// run super
name|proxCount
operator|=
name|freq
expr_stmt|;
comment|// note frequency
name|position
operator|=
literal|0
expr_stmt|;
comment|// reset position
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|read
specifier|public
specifier|final
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/** Called by super.skipTo(). */
DECL|method|skipProx
specifier|protected
name|void
name|skipProx
parameter_list|(
name|long
name|proxPointer
parameter_list|)
throws|throws
name|IOException
block|{
name|proxStream
operator|.
name|seek
argument_list|(
name|proxPointer
argument_list|)
expr_stmt|;
name|proxCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

