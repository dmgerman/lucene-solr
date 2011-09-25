begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.shingle
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|shingle
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Analyzer
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
name|analysis
operator|.
name|AnalyzerWrapper
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|Version
import|;
end_import

begin_comment
comment|/**  * A ShingleAnalyzerWrapper wraps a {@link ShingleFilter} around another {@link Analyzer}.  *<p>  * A shingle is another name for a token based n-gram.  *</p>  */
end_comment

begin_class
DECL|class|ShingleAnalyzerWrapper
specifier|public
specifier|final
class|class
name|ShingleAnalyzerWrapper
extends|extends
name|AnalyzerWrapper
block|{
DECL|field|defaultAnalyzer
specifier|private
specifier|final
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
DECL|field|maxShingleSize
specifier|private
specifier|final
name|int
name|maxShingleSize
decl_stmt|;
DECL|field|minShingleSize
specifier|private
specifier|final
name|int
name|minShingleSize
decl_stmt|;
DECL|field|tokenSeparator
specifier|private
specifier|final
name|String
name|tokenSeparator
decl_stmt|;
DECL|field|outputUnigrams
specifier|private
specifier|final
name|boolean
name|outputUnigrams
decl_stmt|;
DECL|field|outputUnigramsIfNoShingles
specifier|private
specifier|final
name|boolean
name|outputUnigramsIfNoShingles
decl_stmt|;
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|)
block|{
name|this
argument_list|(
name|defaultAnalyzer
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|,
name|int
name|maxShingleSize
parameter_list|)
block|{
name|this
argument_list|(
name|defaultAnalyzer
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|maxShingleSize
argument_list|)
expr_stmt|;
block|}
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|,
name|int
name|minShingleSize
parameter_list|,
name|int
name|maxShingleSize
parameter_list|)
block|{
name|this
argument_list|(
name|defaultAnalyzer
argument_list|,
name|minShingleSize
argument_list|,
name|maxShingleSize
argument_list|,
name|ShingleFilter
operator|.
name|TOKEN_SEPARATOR
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new ShingleAnalyzerWrapper    *    * @param defaultAnalyzer Analyzer whose TokenStream is to be filtered    * @param minShingleSize Min shingle (token ngram) size    * @param maxShingleSize Max shingle size    * @param tokenSeparator Used to separate input stream tokens in output shingles    * @param outputUnigrams Whether or not the filter shall pass the original    *        tokens to the output stream    * @param outputUnigramsIfNoShingles Overrides the behavior of outputUnigrams==false for those    *        times when no shingles are available (because there are fewer than    *        minShingleSize tokens in the input stream)?    *        Note that if outputUnigrams==true, then unigrams are always output,    *        regardless of whether any shingles are available.    */
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|,
name|int
name|minShingleSize
parameter_list|,
name|int
name|maxShingleSize
parameter_list|,
name|String
name|tokenSeparator
parameter_list|,
name|boolean
name|outputUnigrams
parameter_list|,
name|boolean
name|outputUnigramsIfNoShingles
parameter_list|)
block|{
name|this
operator|.
name|defaultAnalyzer
operator|=
name|defaultAnalyzer
expr_stmt|;
if|if
condition|(
name|maxShingleSize
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Max shingle size must be>= 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxShingleSize
operator|=
name|maxShingleSize
expr_stmt|;
if|if
condition|(
name|minShingleSize
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Min shingle size must be>= 2"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minShingleSize
operator|>
name|maxShingleSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Min shingle size must be<= max shingle size"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minShingleSize
operator|=
name|minShingleSize
expr_stmt|;
name|this
operator|.
name|tokenSeparator
operator|=
operator|(
name|tokenSeparator
operator|==
literal|null
condition|?
literal|""
else|:
name|tokenSeparator
operator|)
expr_stmt|;
name|this
operator|.
name|outputUnigrams
operator|=
name|outputUnigrams
expr_stmt|;
name|this
operator|.
name|outputUnigramsIfNoShingles
operator|=
name|outputUnigramsIfNoShingles
expr_stmt|;
block|}
comment|/**    * Wraps {@link StandardAnalyzer}.     */
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|ShingleFilter
operator|.
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wraps {@link StandardAnalyzer}.     */
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|int
name|minShingleSize
parameter_list|,
name|int
name|maxShingleSize
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|minShingleSize
argument_list|,
name|maxShingleSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * The max shingle (token ngram) size    *     * @return The max shingle (token ngram) size    */
DECL|method|getMaxShingleSize
specifier|public
name|int
name|getMaxShingleSize
parameter_list|()
block|{
return|return
name|maxShingleSize
return|;
block|}
comment|/**    * The min shingle (token ngram) size    *     * @return The min shingle (token ngram) size    */
DECL|method|getMinShingleSize
specifier|public
name|int
name|getMinShingleSize
parameter_list|()
block|{
return|return
name|minShingleSize
return|;
block|}
DECL|method|getTokenSeparator
specifier|public
name|String
name|getTokenSeparator
parameter_list|()
block|{
return|return
name|tokenSeparator
return|;
block|}
DECL|method|isOutputUnigrams
specifier|public
name|boolean
name|isOutputUnigrams
parameter_list|()
block|{
return|return
name|outputUnigrams
return|;
block|}
DECL|method|isOutputUnigramsIfNoShingles
specifier|public
name|boolean
name|isOutputUnigramsIfNoShingles
parameter_list|()
block|{
return|return
name|outputUnigramsIfNoShingles
return|;
block|}
annotation|@
name|Override
DECL|method|getWrappedAnalyzer
specifier|protected
name|Analyzer
name|getWrappedAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|defaultAnalyzer
return|;
block|}
annotation|@
name|Override
DECL|method|wrapComponents
specifier|protected
name|TokenStreamComponents
name|wrapComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|TokenStreamComponents
name|components
parameter_list|)
block|{
name|ShingleFilter
name|filter
init|=
operator|new
name|ShingleFilter
argument_list|(
name|components
operator|.
name|getTokenStream
argument_list|()
argument_list|,
name|minShingleSize
argument_list|,
name|maxShingleSize
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setMinShingleSize
argument_list|(
name|minShingleSize
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setMaxShingleSize
argument_list|(
name|maxShingleSize
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setTokenSeparator
argument_list|(
name|tokenSeparator
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setOutputUnigrams
argument_list|(
name|outputUnigrams
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setOutputUnigramsIfNoShingles
argument_list|(
name|outputUnigramsIfNoShingles
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|components
operator|.
name|getTokenizer
argument_list|()
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

