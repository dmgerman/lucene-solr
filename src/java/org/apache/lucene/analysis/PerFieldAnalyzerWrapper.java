begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * This analyzer is used to facilitate scenarios where different  * fields require different analysis techniques.  Use {@link #addAnalyzer}  * to add a non-default analyzer on a field name basis.  *   *<p>Example usage:  *   *<pre>  *   PerFieldAnalyzerWrapper aWrapper =  *      new PerFieldAnalyzerWrapper(new StandardAnalyzer());  *   aWrapper.addAnalyzer("firstname", new KeywordAnalyzer());  *   aWrapper.addAnalyzer("lastname", new KeywordAnalyzer());  *</pre>  *   *<p>In this example, StandardAnalyzer will be used for all fields except "firstname"  * and "lastname", for which KeywordAnalyzer will be used.  *   *<p>A PerFieldAnalyzerWrapper can be used like any other analyzer, for both indexing  * and query parsing.  */
end_comment

begin_class
DECL|class|PerFieldAnalyzerWrapper
specifier|public
class|class
name|PerFieldAnalyzerWrapper
extends|extends
name|Analyzer
block|{
DECL|field|defaultAnalyzer
specifier|private
name|Analyzer
name|defaultAnalyzer
decl_stmt|;
DECL|field|analyzerMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|analyzerMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Constructs with default analyzer.    *    * @param defaultAnalyzer Any fields not specifically    * defined to use a different analyzer will use the one provided here.    */
DECL|method|PerFieldAnalyzerWrapper
specifier|public
name|PerFieldAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|)
block|{
name|this
argument_list|(
name|defaultAnalyzer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs with default analyzer and a map of analyzers to use for     * specific fields.    *    * @param defaultAnalyzer Any fields not specifically    * defined to use a different analyzer will use the one provided here.    * @param fieldAnalyzers a Map (String field name to the Analyzer) to be     * used for those fields     */
DECL|method|PerFieldAnalyzerWrapper
specifier|public
name|PerFieldAnalyzerWrapper
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|fieldAnalyzers
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
name|fieldAnalyzers
operator|!=
literal|null
condition|)
block|{
name|analyzerMap
operator|.
name|putAll
argument_list|(
name|fieldAnalyzers
argument_list|)
expr_stmt|;
block|}
name|setOverridesTokenStreamMethod
argument_list|(
name|PerFieldAnalyzerWrapper
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Defines an analyzer to use for the specified field.    *    * @param fieldName field name requiring a non-default analyzer    * @param analyzer non-default analyzer to use for field    */
DECL|method|addAnalyzer
specifier|public
name|void
name|addAnalyzer
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|analyzerMap
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|analyzer
argument_list|)
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
name|Analyzer
name|analyzer
init|=
name|analyzerMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|defaultAnalyzer
expr_stmt|;
block|}
return|return
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|overridesTokenStreamMethod
condition|)
block|{
comment|// LUCENE-1678: force fallback to tokenStream() if we
comment|// have been subclassed and that subclass overrides
comment|// tokenStream but not reusableTokenStream
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
name|Analyzer
name|analyzer
init|=
name|analyzerMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
name|analyzer
operator|=
name|defaultAnalyzer
expr_stmt|;
return|return
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
comment|/** Return the positionIncrementGap from the analyzer assigned to fieldName */
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Analyzer
name|analyzer
init|=
name|analyzerMap
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
name|analyzer
operator|=
name|defaultAnalyzer
expr_stmt|;
return|return
name|analyzer
operator|.
name|getPositionIncrementGap
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PerFieldAnalyzerWrapper("
operator|+
name|analyzerMap
operator|+
literal|", default="
operator|+
name|defaultAnalyzer
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

