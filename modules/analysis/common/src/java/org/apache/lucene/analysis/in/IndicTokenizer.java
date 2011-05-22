begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.in
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|in
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
name|util
operator|.
name|CharTokenizer
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Simple Tokenizer for text in Indian Languages.  */
end_comment

begin_class
DECL|class|IndicTokenizer
specifier|public
specifier|final
class|class
name|IndicTokenizer
extends|extends
name|CharTokenizer
block|{
DECL|method|IndicTokenizer
specifier|public
name|IndicTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|IndicTokenizer
specifier|public
name|IndicTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|source
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|IndicTokenizer
specifier|public
name|IndicTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
operator|||
name|Character
operator|.
name|getType
argument_list|(
name|c
argument_list|)
operator|==
name|Character
operator|.
name|NON_SPACING_MARK
operator|||
name|Character
operator|.
name|getType
argument_list|(
name|c
argument_list|)
operator|==
name|Character
operator|.
name|FORMAT
operator|||
name|Character
operator|.
name|getType
argument_list|(
name|c
argument_list|)
operator|==
name|Character
operator|.
name|COMBINING_SPACING_MARK
return|;
block|}
block|}
end_class

end_unit

