begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_comment
comment|/** A Token is an occurence of a term from the text of a field.  It consists of   a term's text, the start and end offset of the term in the text of the field,   and a type string.    The start and end offsets permit applications to re-associate a token with   its source text, e.g., to display highlighted query terms in a document   browser, or to show matching text fragments in a KWIC (KeyWord In Context)   display, etc.    The type is an interned string, assigned by a lexical analyzer   (a.k.a. tokenizer), naming the lexical or syntactic class that the token   belongs to.  For example an end of sentence marker token might be implemented   with type "eos".  The default token type is "word".  */
end_comment

begin_class
DECL|class|Token
specifier|public
specifier|final
class|class
name|Token
block|{
DECL|field|termText
name|String
name|termText
decl_stmt|;
comment|// the text of the term
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
comment|// start in source text
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
comment|// end in source text
DECL|field|type
name|String
name|type
init|=
literal|"word"
decl_stmt|;
comment|// lexical type
comment|/** Constructs a Token with the given term text, and start& end offsets.       The type defaults to "word." */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|termText
operator|=
name|text
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
block|}
comment|/** Constructs a Token with the given text, start and end offsets,& type. */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|String
name|typ
parameter_list|)
block|{
name|termText
operator|=
name|text
expr_stmt|;
name|startOffset
operator|=
name|start
expr_stmt|;
name|endOffset
operator|=
name|end
expr_stmt|;
name|type
operator|=
name|typ
expr_stmt|;
block|}
comment|/** Returns the Token's term text. */
DECL|method|termText
specifier|public
specifier|final
name|String
name|termText
parameter_list|()
block|{
return|return
name|termText
return|;
block|}
comment|/** Returns this Token's starting offset, the position of the first character     corresponding to this token in the source text.      Note that the difference between endOffset() and startOffset() may not be     equal to termText.length(), as the term text may have been altered by a     stemmer or some other filter. */
DECL|method|startOffset
specifier|public
specifier|final
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/** Returns this Token's ending offset, one greater than the position of the     last character corresponding to this token in the source text. */
DECL|method|endOffset
specifier|public
specifier|final
name|int
name|endOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/** Returns this Token's lexical type.  Defaults to "word". */
DECL|method|type
specifier|public
specifier|final
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
end_class

end_unit

