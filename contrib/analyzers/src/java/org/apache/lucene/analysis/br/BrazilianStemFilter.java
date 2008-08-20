begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.br
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|br
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
name|Token
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
name|TokenFilter
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Based on GermanStemFilter  *  * @author Jo&atilde;o Kramer  */
end_comment

begin_class
DECL|class|BrazilianStemFilter
specifier|public
specifier|final
class|class
name|BrazilianStemFilter
extends|extends
name|TokenFilter
block|{
comment|/**    * The actual token in the input stream.    */
DECL|field|stemmer
specifier|private
name|BrazilianStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Set
name|exclusions
init|=
literal|null
decl_stmt|;
DECL|method|BrazilianStemFilter
specifier|public
name|BrazilianStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stemmer
operator|=
operator|new
name|BrazilianStemmer
argument_list|()
expr_stmt|;
block|}
DECL|method|BrazilianStemFilter
specifier|public
name|BrazilianStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Set
name|exclusiontable
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|exclusions
operator|=
name|exclusiontable
expr_stmt|;
block|}
comment|/**    * @return Returns the next token in the stream, or null at EOS.    */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
name|Token
name|nextToken
init|=
name|input
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|term
init|=
name|nextToken
operator|.
name|term
argument_list|()
decl_stmt|;
comment|// Check the exclusion table.
if|if
condition|(
name|exclusions
operator|==
literal|null
operator|||
operator|!
name|exclusions
operator|.
name|contains
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|// If not stemmed, don't waste the time adjusting the token.
if|if
condition|(
operator|(
name|s
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|s
operator|.
name|equals
argument_list|(
name|term
argument_list|)
condition|)
name|nextToken
operator|.
name|setTermBuffer
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|nextToken
return|;
block|}
block|}
end_class

end_unit

