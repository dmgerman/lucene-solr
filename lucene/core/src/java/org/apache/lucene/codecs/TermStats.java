begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|TermsEnum
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_comment
comment|/**  * Holder for per-term statistics.  *   * @see TermsEnum#docFreq  * @see TermsEnum#totalTermFreq  */
end_comment

begin_class
DECL|class|TermStats
specifier|public
class|class
name|TermStats
block|{
comment|/** How many documents have at least one occurrence of    *  this term. */
DECL|field|docFreq
specifier|public
specifier|final
name|int
name|docFreq
decl_stmt|;
comment|/** Total number of times this term occurs across all    *  documents in the field. */
DECL|field|totalTermFreq
specifier|public
specifier|final
name|long
name|totalTermFreq
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|TermStats
specifier|public
name|TermStats
parameter_list|(
name|int
name|docFreq
parameter_list|,
name|long
name|totalTermFreq
parameter_list|)
block|{
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|this
operator|.
name|totalTermFreq
operator|=
name|totalTermFreq
expr_stmt|;
block|}
block|}
end_class

end_unit

