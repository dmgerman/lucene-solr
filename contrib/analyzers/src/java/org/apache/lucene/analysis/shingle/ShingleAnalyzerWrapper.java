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
name|java
operator|.
name|io
operator|.
name|Reader
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
name|TokenStream
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

begin_comment
comment|/**  * A ShingleAnalyzerWrapper wraps a ShingleFilter around another analyzer.  A shingle is another term for a token based  * n-gram.  */
end_comment

begin_class
DECL|class|ShingleAnalyzerWrapper
specifier|public
class|class
name|ShingleAnalyzerWrapper
extends|extends
name|Analyzer
block|{
DECL|field|defaultAnalyzer
specifier|protected
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
DECL|field|maxShingleSize
specifier|protected
name|int
name|maxShingleSize
init|=
literal|2
decl_stmt|;
DECL|field|outputUnigrams
specifier|protected
name|boolean
name|outputUnigrams
init|=
literal|true
decl_stmt|;
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|defaultAnalyzer
operator|=
name|defaultAnalyzer
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxShingleSize
operator|=
name|maxShingleSize
expr_stmt|;
block|}
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|defaultAnalyzer
operator|=
operator|new
name|StandardAnalyzer
argument_list|()
expr_stmt|;
block|}
DECL|method|ShingleAnalyzerWrapper
specifier|public
name|ShingleAnalyzerWrapper
parameter_list|(
name|int
name|nGramSize
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxShingleSize
operator|=
name|nGramSize
expr_stmt|;
block|}
comment|/**    * The max shingle (ngram) size    * @return The max shingle (ngram) size    */
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
comment|/**    * Set the maximum size of output shingles (default: 2)    *    * @param maxShingleSize max shingle size    */
DECL|method|setMaxShingleSize
specifier|public
name|void
name|setMaxShingleSize
parameter_list|(
name|int
name|maxShingleSize
parameter_list|)
block|{
name|this
operator|.
name|maxShingleSize
operator|=
name|maxShingleSize
expr_stmt|;
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
comment|/**    * Shall the filter pass the original tokens (the "unigrams") to the output    * stream? (default: true)    *    * @param outputUnigrams Whether or not the filter shall pass the original    *                       tokens to the output stream    */
DECL|method|setOutputUnigrams
specifier|public
name|void
name|setOutputUnigrams
parameter_list|(
name|boolean
name|outputUnigrams
parameter_list|)
block|{
name|this
operator|.
name|outputUnigrams
operator|=
name|outputUnigrams
expr_stmt|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|ShingleFilter
name|filter
init|=
operator|new
name|ShingleFilter
argument_list|(
name|defaultAnalyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
argument_list|)
decl_stmt|;
name|filter
operator|.
name|setMaxShingleSize
argument_list|(
name|maxShingleSize
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setOutputUnigrams
argument_list|(
name|outputUnigrams
argument_list|)
expr_stmt|;
return|return
name|filter
return|;
block|}
block|}
end_class

end_unit

