begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * A filter that stems Russian words. The implementation was inspired by GermanStemFilter.  * The input should be filtered by RussianLowerCaseFilter before passing it to RussianStemFilter ,  * because RussianStemFilter only works  with lowercase part of any "russian" charset.  *  * @author    Boris Okner, b.okner@rogers.com  * @version   $Id$  */
end_comment

begin_class
DECL|class|RussianStemFilter
specifier|public
specifier|final
class|class
name|RussianStemFilter
extends|extends
name|TokenFilter
block|{
comment|/**      * The actual token in the input stream.      */
DECL|field|token
specifier|private
name|Token
name|token
init|=
literal|null
decl_stmt|;
DECL|field|stemmer
specifier|private
name|RussianStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|method|RussianStemFilter
specifier|public
name|RussianStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|char
index|[]
name|charset
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
name|RussianStemmer
argument_list|(
name|charset
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return  Returns the next token in the stream, or null at EOS      */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Token
argument_list|(
name|s
argument_list|,
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|,
name|token
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
return|return
name|token
return|;
block|}
block|}
comment|/**      * Set a alternative/custom RussianStemmer for this filter.      */
DECL|method|setStemmer
specifier|public
name|void
name|setStemmer
parameter_list|(
name|RussianStemmer
name|stemmer
parameter_list|)
block|{
if|if
condition|(
name|stemmer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

