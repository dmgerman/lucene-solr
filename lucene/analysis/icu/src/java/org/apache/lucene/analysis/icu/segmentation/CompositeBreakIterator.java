begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UScript
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_comment
comment|/**  * An internal BreakIterator for multilingual text, following recommendations  * from: UAX #29: Unicode Text Segmentation. (http://unicode.org/reports/tr29/)  *<p>  * See http://unicode.org/reports/tr29/#Tailoring for the motivation of this  * design.  *<p>  * Text is first divided into script boundaries. The processing is then  * delegated to the appropriate break iterator for that specific script.  *<p>  * This break iterator also allows you to retrieve the ISO 15924 script code  * associated with a piece of text.  *<p>  * See also UAX #29, UTR #24  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompositeBreakIterator
specifier|final
class|class
name|CompositeBreakIterator
block|{
DECL|field|config
specifier|private
specifier|final
name|ICUTokenizerConfig
name|config
decl_stmt|;
DECL|field|wordBreakers
specifier|private
specifier|final
name|BreakIteratorWrapper
name|wordBreakers
index|[]
init|=
operator|new
name|BreakIteratorWrapper
index|[
name|UScript
operator|.
name|CODE_LIMIT
index|]
decl_stmt|;
DECL|field|rbbi
specifier|private
name|BreakIteratorWrapper
name|rbbi
decl_stmt|;
DECL|field|scriptIterator
specifier|private
specifier|final
name|ScriptIterator
name|scriptIterator
decl_stmt|;
DECL|field|text
specifier|private
name|char
name|text
index|[]
decl_stmt|;
DECL|method|CompositeBreakIterator
name|CompositeBreakIterator
parameter_list|(
name|ICUTokenizerConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|scriptIterator
operator|=
operator|new
name|ScriptIterator
argument_list|(
name|config
operator|.
name|combineCJ
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Retrieve the next break position. If the RBBI range is exhausted within the    * script boundary, examine the next script boundary.    *     * @return the next break position or BreakIterator.DONE    */
DECL|method|next
name|int
name|next
parameter_list|()
block|{
name|int
name|next
init|=
name|rbbi
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|==
name|BreakIterator
operator|.
name|DONE
operator|&&
name|scriptIterator
operator|.
name|next
argument_list|()
condition|)
block|{
name|rbbi
operator|=
name|getBreakIterator
argument_list|(
name|scriptIterator
operator|.
name|getScriptCode
argument_list|()
argument_list|)
expr_stmt|;
name|rbbi
operator|.
name|setText
argument_list|(
name|text
argument_list|,
name|scriptIterator
operator|.
name|getScriptStart
argument_list|()
argument_list|,
name|scriptIterator
operator|.
name|getScriptLimit
argument_list|()
operator|-
name|scriptIterator
operator|.
name|getScriptStart
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|rbbi
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|next
operator|==
name|BreakIterator
operator|.
name|DONE
operator|)
condition|?
name|BreakIterator
operator|.
name|DONE
else|:
name|next
operator|+
name|scriptIterator
operator|.
name|getScriptStart
argument_list|()
return|;
block|}
comment|/**    * Retrieve the current break position.    *     * @return the current break position or BreakIterator.DONE    */
DECL|method|current
name|int
name|current
parameter_list|()
block|{
specifier|final
name|int
name|current
init|=
name|rbbi
operator|.
name|current
argument_list|()
decl_stmt|;
return|return
operator|(
name|current
operator|==
name|BreakIterator
operator|.
name|DONE
operator|)
condition|?
name|BreakIterator
operator|.
name|DONE
else|:
name|current
operator|+
name|scriptIterator
operator|.
name|getScriptStart
argument_list|()
return|;
block|}
comment|/**    * Retrieve the rule status code (token type) from the underlying break    * iterator    *     * @return rule status code (see RuleBasedBreakIterator constants)    */
DECL|method|getRuleStatus
name|int
name|getRuleStatus
parameter_list|()
block|{
return|return
name|rbbi
operator|.
name|getRuleStatus
argument_list|()
return|;
block|}
comment|/**    * Retrieve the UScript script code for the current token. This code can be    * decoded with UScript into a name or ISO 15924 code.    *     * @return UScript script code for the current token.    */
DECL|method|getScriptCode
name|int
name|getScriptCode
parameter_list|()
block|{
return|return
name|scriptIterator
operator|.
name|getScriptCode
argument_list|()
return|;
block|}
comment|/**    * Set a new region of text to be examined by this iterator    *     * @param text buffer of text    * @param start offset into buffer    * @param length maximum length to examine    */
DECL|method|setText
name|void
name|setText
parameter_list|(
specifier|final
name|char
name|text
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|scriptIterator
operator|.
name|setText
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|scriptIterator
operator|.
name|next
argument_list|()
condition|)
block|{
name|rbbi
operator|=
name|getBreakIterator
argument_list|(
name|scriptIterator
operator|.
name|getScriptCode
argument_list|()
argument_list|)
expr_stmt|;
name|rbbi
operator|.
name|setText
argument_list|(
name|text
argument_list|,
name|scriptIterator
operator|.
name|getScriptStart
argument_list|()
argument_list|,
name|scriptIterator
operator|.
name|getScriptLimit
argument_list|()
operator|-
name|scriptIterator
operator|.
name|getScriptStart
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rbbi
operator|=
name|getBreakIterator
argument_list|(
name|UScript
operator|.
name|COMMON
argument_list|)
expr_stmt|;
name|rbbi
operator|.
name|setText
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getBreakIterator
specifier|private
name|BreakIteratorWrapper
name|getBreakIterator
parameter_list|(
name|int
name|scriptCode
parameter_list|)
block|{
if|if
condition|(
name|wordBreakers
index|[
name|scriptCode
index|]
operator|==
literal|null
condition|)
name|wordBreakers
index|[
name|scriptCode
index|]
operator|=
name|BreakIteratorWrapper
operator|.
name|wrap
argument_list|(
name|config
operator|.
name|getBreakIterator
argument_list|(
name|scriptCode
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|wordBreakers
index|[
name|scriptCode
index|]
return|;
block|}
block|}
end_class

end_unit

