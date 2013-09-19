begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|FieldInfo
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
comment|/**  * Abstract API that consumes terms for an individual field.  *<p>  * The lifecycle is:  *<ol>  *<li>TermsConsumer is returned for each field   *       by {@link PushFieldsConsumer#addField(FieldInfo)}.  *<li>TermsConsumer returns a {@link PostingsConsumer} for  *       each term in {@link #startTerm(BytesRef)}.  *<li>When the producer (e.g. IndexWriter)  *       is done adding documents for the term, it calls   *       {@link #finishTerm(BytesRef, TermStats)}, passing in  *       the accumulated term statistics.  *<li>Producer calls {@link #finish(long, long, int)} with  *       the accumulated collection statistics when it is finished  *       adding terms to the field.  *</ol>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermsConsumer
specifier|public
specifier|abstract
class|class
name|TermsConsumer
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|TermsConsumer
specifier|protected
name|TermsConsumer
parameter_list|()
block|{   }
comment|/** Starts a new term in this field; this may be called    *  with no corresponding call to finish if the term had    *  no docs. */
DECL|method|startTerm
specifier|public
specifier|abstract
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Finishes the current term; numDocs must be> 0.    *<code>stats.totalTermFreq</code> will be -1 when term     *  frequencies are omitted for the field. */
DECL|method|finishTerm
specifier|public
specifier|abstract
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called when we are done adding terms to this field.    *<code>sumTotalTermFreq</code> will be -1 when term     *  frequencies are omitted for the field. */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

