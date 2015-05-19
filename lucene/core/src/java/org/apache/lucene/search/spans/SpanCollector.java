begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|PostingsEnum
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
name|Term
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
comment|/**  * An interface defining the collection of postings information from the leaves  * of a {@link org.apache.lucene.search.spans.Spans}  *  * Typical use would be as follows:  *<pre>  *   while (spans.nextStartPosition() != NO_MORE_POSITIONS) {  *     spanCollector.reset();  *     spans.collect(spanCollector);  *     doSomethingWith(spanCollector);  *   }  *</pre>  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|SpanCollector
specifier|public
interface|interface
name|SpanCollector
block|{
comment|/**    * Called to indicate that the driving {@link org.apache.lucene.search.spans.Spans} has    * been moved to a new position    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Returns an integer indicating what postings information should be retrieved    *    * See {@link org.apache.lucene.index.TermsEnum#postings(org.apache.lucene.util.Bits, org.apache.lucene.index.PostingsEnum, int)}    *    * @return the postings flag    */
DECL|method|requiredPostings
specifier|public
name|int
name|requiredPostings
parameter_list|()
function_decl|;
comment|/**    * Collect information from postings    * @param postings a {@link PostingsEnum}    * @param term     the {@link Term} for this postings list    * @throws IOException on error    */
DECL|method|collectLeaf
specifier|public
name|void
name|collectLeaf
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return a {@link BufferedSpanCollector} for use by eager spans implementations, such    * as {@link NearSpansOrdered}.    *    * @return a BufferedSpanCollector    */
DECL|method|buffer
specifier|public
name|BufferedSpanCollector
name|buffer
parameter_list|()
function_decl|;
comment|/**    * @return the SpanCollector used by the {@link org.apache.lucene.search.spans.BufferedSpanCollector}    *          returned from {@link #buffer()}.    */
DECL|method|bufferedCollector
specifier|public
name|SpanCollector
name|bufferedCollector
parameter_list|()
function_decl|;
comment|/**    * A default No-op implementation of SpanCollector    */
DECL|field|NO_OP
specifier|public
specifier|static
specifier|final
name|SpanCollector
name|NO_OP
init|=
operator|new
name|SpanCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{      }
annotation|@
name|Override
specifier|public
name|int
name|requiredPostings
parameter_list|()
block|{
return|return
name|PostingsEnum
operator|.
name|POSITIONS
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collectLeaf
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|Term
name|term
parameter_list|)
block|{      }
annotation|@
name|Override
specifier|public
name|BufferedSpanCollector
name|buffer
parameter_list|()
block|{
return|return
name|BufferedSpanCollector
operator|.
name|NO_OP
return|;
block|}
annotation|@
name|Override
specifier|public
name|SpanCollector
name|bufferedCollector
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

