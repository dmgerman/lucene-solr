begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.stempel
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|stempel
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
name|pl
operator|.
name|PolishAnalyzer
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
name|stempel
operator|.
name|StempelFilter
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
name|stempel
operator|.
name|StempelStemmer
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
name|util
operator|.
name|AbstractAnalysisFactory
import|;
end_import

begin_comment
comment|// javadocs
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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link StempelFilter} using a Polish stemming table.  */
end_comment

begin_class
DECL|class|StempelPolishStemFilterFactory
specifier|public
class|class
name|StempelPolishStemFilterFactory
extends|extends
name|TokenFilterFactory
block|{
comment|/** Sole constructor. See {@link AbstractAnalysisFactory} for initialization lifecycle. */
DECL|method|StempelPolishStemFilterFactory
specifier|public
name|StempelPolishStemFilterFactory
parameter_list|()
block|{}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|StempelFilter
argument_list|(
name|input
argument_list|,
operator|new
name|StempelStemmer
argument_list|(
name|PolishAnalyzer
operator|.
name|getDefaultTable
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

