begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
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
name|java
operator|.
name|text
operator|.
name|Collator
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
name|TermsEnum
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
name|FilteredTermsEnum
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Subclass of FilteredTermEnum for enumerating all terms that match the  * specified range parameters.  *<p>Term enumerations are always ordered by  * {@link #getComparator}.  Each term in the enumeration is  * greater than all that precede it.</p>  * @deprecated Index collation keys with CollationKeyAnalyzer or ICUCollationKeyAnalyzer instead.  *  This class will be removed in Lucene 5.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SlowCollatedTermRangeTermsEnum
specifier|public
class|class
name|SlowCollatedTermRangeTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|collator
specifier|private
name|Collator
name|collator
decl_stmt|;
DECL|field|upperTermText
specifier|private
name|String
name|upperTermText
decl_stmt|;
DECL|field|lowerTermText
specifier|private
name|String
name|lowerTermText
decl_stmt|;
DECL|field|includeLower
specifier|private
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|private
name|boolean
name|includeUpper
decl_stmt|;
comment|/**    * Enumerates all terms greater/equal than<code>lowerTerm</code>    * but less/equal than<code>upperTerm</code>.     *     * If an endpoint is null, it is said to be "open". Either or both     * endpoints may be open.  Open endpoints may not be exclusive     * (you can't select all but the first or last term without     * explicitly specifying the term to exclude.)    *     * @param tenum    * @param lowerTermText    *          The term text at the lower end of the range    * @param upperTermText    *          The term text at the upper end of the range    * @param includeLower    *          If true, the<code>lowerTerm</code> is included in the range.    * @param includeUpper    *          If true, the<code>upperTerm</code> is included in the range.    * @param collator    *          The collator to use to collate index Terms, to determine their    *          membership in the range bounded by<code>lowerTerm</code> and    *<code>upperTerm</code>.    */
DECL|method|SlowCollatedTermRangeTermsEnum
specifier|public
name|SlowCollatedTermRangeTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|String
name|lowerTermText
parameter_list|,
name|String
name|upperTermText
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
name|this
operator|.
name|collator
operator|=
name|collator
expr_stmt|;
name|this
operator|.
name|upperTermText
operator|=
name|upperTermText
expr_stmt|;
name|this
operator|.
name|lowerTermText
operator|=
name|lowerTermText
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
comment|// do a little bit of normalization...
comment|// open ended range queries should always be inclusive.
if|if
condition|(
name|this
operator|.
name|lowerTermText
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|lowerTermText
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
block|}
comment|// TODO: optimize
name|BytesRef
name|startBytesRef
init|=
operator|new
name|BytesRef
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|setInitialSeekTerm
argument_list|(
name|startBytesRef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
operator|(
name|includeLower
condition|?
name|collator
operator|.
name|compare
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|lowerTermText
argument_list|)
operator|>=
literal|0
else|:
name|collator
operator|.
name|compare
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|lowerTermText
argument_list|)
operator|>
literal|0
operator|)
operator|&&
operator|(
name|upperTermText
operator|==
literal|null
operator|||
operator|(
name|includeUpper
condition|?
name|collator
operator|.
name|compare
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|upperTermText
argument_list|)
operator|<=
literal|0
else|:
name|collator
operator|.
name|compare
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|upperTermText
argument_list|)
operator|<
literal|0
operator|)
operator|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
end_class

end_unit

