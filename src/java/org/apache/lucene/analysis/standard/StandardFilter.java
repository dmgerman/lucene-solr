begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|*
import|;
end_import

begin_comment
comment|/** Normalizes tokens extracted with {@link StandardTokenizer}. */
end_comment

begin_class
DECL|class|StandardFilter
specifier|public
specifier|final
class|class
name|StandardFilter
extends|extends
name|TokenFilter
implements|implements
name|StandardTokenizerConstants
block|{
comment|/** Construct filtering<i>in</i>. */
DECL|method|StandardFilter
specifier|public
name|StandardFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|field|APOSTROPHE_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|APOSTROPHE_TYPE
init|=
name|tokenImage
index|[
name|APOSTROPHE
index|]
decl_stmt|;
DECL|field|ACRONYM_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|ACRONYM_TYPE
init|=
name|tokenImage
index|[
name|ACRONYM
index|]
decl_stmt|;
comment|/** Returns the next token in the stream, or null at EOS.    *<p>Removes<tt>'s</tt> from the end of words.    *<p>Removes dots from acronyms.    */
DECL|method|next
specifier|public
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|text
init|=
name|t
operator|.
name|termText
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|t
operator|.
name|type
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|APOSTROPHE_TYPE
operator|&&
comment|// remove 's
operator|(
name|text
operator|.
name|endsWith
argument_list|(
literal|"'s"
argument_list|)
operator|||
name|text
operator|.
name|endsWith
argument_list|(
literal|"'S"
argument_list|)
operator|)
condition|)
block|{
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
argument_list|(
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|ACRONYM_TYPE
condition|)
block|{
comment|// remove dots
name|StringBuffer
name|trimmed
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|'.'
condition|)
name|trimmed
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
argument_list|(
name|trimmed
operator|.
name|toString
argument_list|()
argument_list|,
name|t
operator|.
name|startOffset
argument_list|()
argument_list|,
name|t
operator|.
name|endOffset
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|t
return|;
block|}
block|}
block|}
end_class

end_unit

