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

begin_comment
comment|/** An Analyzer builds TokenStreams, which analyze text.  It thus represents a  *  policy for extracting index terms from text.  *<p>  *  Typical implementations first build a Tokenizer, which breaks the stream of  *  characters from the Reader into raw Tokens.  One or more TokenFilters may  *  then be applied to the output of the Tokenizer.  */
end_comment

begin_class
DECL|class|Analyzer
specifier|public
specifier|abstract
class|class
name|Analyzer
block|{
comment|/** Creates a TokenStream which tokenizes all the text in the provided    * Reader.  Must be able to handle null field name for backward compatibility.    */
DECL|method|tokenStream
specifier|public
specifier|abstract
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/** Creates a TokenStream that is allowed to be re-used    *  from the previous time that the same thread called    *  this method.  Callers that do not need to use more    *  than one TokenStream at the same time from this    *  analyzer should use this method for better    *  performance.    */
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
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|field|tokenStreams
specifier|private
name|ThreadLocal
name|tokenStreams
init|=
operator|new
name|ThreadLocal
argument_list|()
decl_stmt|;
comment|/** Used by Analyzers that implement reusableTokenStream    *  to retrieve previously saved TokenStreams for re-use    *  by the same thread. */
DECL|method|getPreviousTokenStream
specifier|protected
name|Object
name|getPreviousTokenStream
parameter_list|()
block|{
return|return
name|tokenStreams
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Used by Analyzers that implement reusableTokenStream    *  to save a TokenStream for later re-use by the same    *  thread. */
DECL|method|setPreviousTokenStream
specifier|protected
name|void
name|setPreviousTokenStream
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|tokenStreams
operator|.
name|set
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
comment|/**    * Invoked before indexing a Fieldable instance if    * terms have already been added to that field.  This allows custom    * analyzers to place an automatic position increment gap between    * Fieldable instances using the same field name.  The default value    * position increment gap is 0.  With a 0 position increment gap and    * the typical default token position increment of 1, all terms in a field,    * including across Fieldable instances, are in successive positions, allowing    * exact PhraseQuery matches, for instance, across Fieldable instance boundaries.    *    * @param fieldName Fieldable name being indexed.    * @return position increment gap, added to the next token emitted from {@link #tokenStream(String,Reader)}    */
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

