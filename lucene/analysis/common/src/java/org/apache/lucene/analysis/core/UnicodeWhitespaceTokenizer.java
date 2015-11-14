begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Tokenizer
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
name|analysis
operator|.
name|util
operator|.
name|UnicodeProps
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
name|AttributeFactory
import|;
end_import

begin_comment
comment|/**  * A UnicodeWhitespaceTokenizer is a tokenizer that divides text at whitespace.  * Adjacent sequences of non-Whitespace characters form tokens (according to  * Unicode's WHITESPACE property).  *<p>  *<em>For Unicode version see: {@link UnicodeProps}</em>  */
end_comment

begin_class
DECL|class|UnicodeWhitespaceTokenizer
specifier|public
specifier|final
class|class
name|UnicodeWhitespaceTokenizer
extends|extends
name|CharTokenizer
block|{
comment|/**    * Construct a new UnicodeWhitespaceTokenizer.    */
DECL|method|UnicodeWhitespaceTokenizer
specifier|public
name|UnicodeWhitespaceTokenizer
parameter_list|()
block|{   }
comment|/**    * Construct a new UnicodeWhitespaceTokenizer using a given    * {@link org.apache.lucene.util.AttributeFactory}.    *    * @param factory    *          the attribute factory to use for this {@link Tokenizer}    */
DECL|method|UnicodeWhitespaceTokenizer
specifier|public
name|UnicodeWhitespaceTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
comment|/** Collects only characters which do not satisfy Unicode's WHITESPACE property. */
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
operator|!
name|UnicodeProps
operator|.
name|WHITESPACE
operator|.
name|get
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
end_class

end_unit

