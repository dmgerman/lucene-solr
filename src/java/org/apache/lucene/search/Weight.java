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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2003 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_comment
comment|/** Expert: Calculate query weights and build query scorers.  *  *<p>A Weight is constructed by a query, given a Searcher ({@link  * Query#createWeight(Searcher)}).  The {@link #sumOfSquaredWeights()} method  * is then called on the top-level query to compute the query normalization  * factor (@link Similarity#queryNorm(float)}).  This factor is then passed to  * {@link #normalize(float)}.  At this point the weighting is complete and a  * scorer may be constructed by calling {@link #scorer(IndexReader)}.  */
end_comment

begin_interface
DECL|interface|Weight
specifier|public
interface|interface
name|Weight
extends|extends
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** The query that this concerns. */
DECL|method|getQuery
name|Query
name|getQuery
parameter_list|()
function_decl|;
comment|/** The weight for this query. */
DECL|method|getValue
name|float
name|getValue
parameter_list|()
function_decl|;
comment|/** The sum of squared weights of contained query clauses. */
DECL|method|sumOfSquaredWeights
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Assigns the query normalization factor to this. */
DECL|method|normalize
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
function_decl|;
comment|/** Constructs a scorer for this. */
DECL|method|scorer
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** An explanation of the score computation for the named document. */
DECL|method|explain
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

