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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2003 The Apache Software Foundation. All rights reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  A<code>FilterIndexReader</code> contains another IndexReader, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality. The class  *<code>FilterIndexReader</code> itself simply implements all abstract methods  * of<code>IndexReader</code> with versions that pass all requests to the  * contained index reader. Subclasses of<code>FilterIndexReader</code> may  * further override some of these methods and may also provide additional  * methods and fields. */
end_comment

begin_class
DECL|class|FilterIndexReader
specifier|public
class|class
name|FilterIndexReader
extends|extends
name|IndexReader
block|{
comment|/** Base class for filtering {@link TermDocs} implementations. */
DECL|class|FilterTermDocs
specifier|public
specifier|static
class|class
name|FilterTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|in
specifier|protected
name|TermDocs
name|in
decl_stmt|;
DECL|method|FilterTermDocs
specifier|public
name|FilterTermDocs
parameter_list|(
name|TermDocs
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|enum
function|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seek
argument_list|(
expr|enum
argument_list|)
expr_stmt|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|in
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|skipTo
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Base class for filtering {@link TermPositions} implementations. */
DECL|class|FilterTermPositions
specifier|public
specifier|static
class|class
name|FilterTermPositions
extends|extends
name|FilterTermDocs
implements|implements
name|TermPositions
block|{
DECL|method|FilterTermPositions
specifier|public
name|FilterTermPositions
parameter_list|(
name|TermPositions
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|in
operator|)
operator|.
name|nextPosition
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link TermEnum} implementations. */
DECL|class|FilterTermEnum
specifier|public
specifier|static
class|class
name|FilterTermEnum
extends|extends
name|TermEnum
block|{
DECL|field|in
specifier|protected
name|TermEnum
name|in
decl_stmt|;
DECL|method|FilterTermEnum
specifier|public
name|FilterTermEnum
parameter_list|(
name|TermEnum
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|term
specifier|public
name|Term
name|term
parameter_list|()
block|{
return|return
name|in
operator|.
name|term
argument_list|()
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|in
operator|.
name|docFreq
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|in
specifier|protected
name|IndexReader
name|in
decl_stmt|;
DECL|method|FilterIndexReader
specifier|public
name|FilterIndexReader
parameter_list|(
name|IndexReader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|in
operator|.
name|numDocs
argument_list|()
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|in
operator|.
name|maxDoc
argument_list|()
return|;
block|}
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|document
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|in
operator|.
name|isDeleted
argument_list|(
name|n
argument_list|)
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|in
operator|.
name|hasDeletions
argument_list|()
return|;
block|}
DECL|method|undeleteAll
specifier|public
name|void
name|undeleteAll
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
block|}
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|norms
argument_list|(
name|f
argument_list|)
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|terms
argument_list|()
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|terms
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|termDocs
argument_list|()
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|termPositions
argument_list|()
return|;
block|}
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|doDelete
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getFieldNames
argument_list|()
return|;
block|}
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|boolean
name|indexed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getFieldNames
argument_list|(
name|indexed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

