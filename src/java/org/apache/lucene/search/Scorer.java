begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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

begin_comment
comment|/** Expert: Implements scoring for a class of queries. */
end_comment

begin_class
DECL|class|Scorer
specifier|public
specifier|abstract
class|class
name|Scorer
block|{
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
comment|/** Constructs a Scorer. */
DECL|method|Scorer
specifier|protected
name|Scorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/** Returns the Similarity implementation used by this scorer. */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|this
operator|.
name|similarity
return|;
block|}
comment|/** Scores all documents and passes them to a collector. */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|next
argument_list|()
condition|)
block|{
name|hc
operator|.
name|collect
argument_list|(
name|doc
argument_list|()
argument_list|,
name|score
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Advance to the next document matching the query.  Returns true iff there    * is another match. */
DECL|method|next
specifier|public
specifier|abstract
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the current document number.  Initially invalid, until {@link    * #next()} is called the first time. */
DECL|method|doc
specifier|public
specifier|abstract
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Returns the score of the current document.  Initially invalid, until    * {@link #next()} is called the first time. */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Skips to the first match beyond the current whose document number is    * greater than or equal to<i>target</i>.<p>Returns true iff there is such    * a match.<p>Behaves as if written:<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * Most implementations are considerably more efficient than that.    */
DECL|method|skipTo
specifier|public
specifier|abstract
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns an explanation of the score for<code>doc</code>. */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

