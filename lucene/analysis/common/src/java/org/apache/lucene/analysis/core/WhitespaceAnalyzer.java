begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_comment
comment|/**  * An Analyzer that uses {@link WhitespaceTokenizer}.  **/
end_comment

begin_class
DECL|class|WhitespaceAnalyzer
specifier|public
specifier|final
class|class
name|WhitespaceAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**    * Creates a new {@link WhitespaceAnalyzer}    */
DECL|method|WhitespaceAnalyzer
specifier|public
name|WhitespaceAnalyzer
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

