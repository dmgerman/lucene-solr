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
name|search
operator|.
name|DocIdSetIterator
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
name|util
operator|.
name|AttributeSource
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
name|util
operator|.
name|Bits
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/** Iterates through the documents and term freqs.  *  NOTE: you must first call {@link #nextDoc} before using  *  any of the per-doc methods. */
end_comment

begin_class
DECL|class|DocsEnum
specifier|public
specifier|abstract
class|class
name|DocsEnum
extends|extends
name|DocIdSetIterator
block|{
comment|/**    * Flag to pass to {@link TermsEnum#docs(Bits,DocsEnum,int)} if you don't    * require term frequencies in the returned enum. When passed to    * {@link TermsEnum#docsAndPositions(Bits,DocsAndPositionsEnum,int)} means    * that no offsets and payloads will be returned.    */
DECL|field|FLAG_NONE
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_NONE
init|=
literal|0x0
decl_stmt|;
comment|/** Flag to pass to {@link TermsEnum#docs(Bits,DocsEnum,int)}    *  if you require term frequencies in the returned enum. */
DECL|field|FLAG_FREQS
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_FREQS
init|=
literal|0x1
decl_stmt|;
DECL|field|atts
specifier|private
name|AttributeSource
name|atts
init|=
literal|null
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|DocsEnum
specifier|protected
name|DocsEnum
parameter_list|()
block|{   }
comment|/**    * Returns term frequency in the current document, or 1 if the field was    * indexed with {@link IndexOptions#DOCS}. Do not call this before    * {@link #nextDoc} is first called, nor after {@link #nextDoc} returns    * {@link DocIdSetIterator#NO_MORE_DOCS}.    *     *<p>    *<b>NOTE:</b> if the {@link DocsEnum} was obtain with {@link #FLAG_NONE},    * the result of this method is undefined.    */
DECL|method|freq
specifier|public
specifier|abstract
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the related attributes. */
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|atts
operator|==
literal|null
condition|)
name|atts
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
return|return
name|atts
return|;
block|}
block|}
end_class

end_unit

