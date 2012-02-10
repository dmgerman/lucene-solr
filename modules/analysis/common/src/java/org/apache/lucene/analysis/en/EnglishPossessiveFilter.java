begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.en
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|en
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
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
comment|/**  * TokenFilter that removes possessives (trailing 's) from words.  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating EnglishPossessiveFilter:  *<ul>  *<li> As of 3.6, U+2019 RIGHT SINGLE QUOTATION MARK and   *         U+FF07 FULLWIDTH APOSTROPHE are also treated as  *         quotation marks.  *</ul>  */
end_comment

begin_class
DECL|class|EnglishPossessiveFilter
specifier|public
specifier|final
class|class
name|EnglishPossessiveFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|matchVersion
specifier|private
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * @deprecated Use {@link #EnglishPossessiveFilter(Version, TokenStream)} instead.    */
annotation|@
name|Deprecated
DECL|method|EnglishPossessiveFilter
specifier|public
name|EnglishPossessiveFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_35
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|EnglishPossessiveFilter
specifier|public
name|EnglishPossessiveFilter
parameter_list|(
name|Version
name|version
parameter_list|,
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|version
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bufferLength
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|bufferLength
operator|>=
literal|2
operator|&&
operator|(
name|buffer
index|[
name|bufferLength
operator|-
literal|2
index|]
operator|==
literal|'\''
operator|||
operator|(
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_36
argument_list|)
operator|&&
operator|(
name|buffer
index|[
name|bufferLength
operator|-
literal|2
index|]
operator|==
literal|'\u2019'
operator|||
name|buffer
index|[
name|bufferLength
operator|-
literal|2
index|]
operator|==
literal|'\uFF07'
operator|)
operator|)
operator|)
operator|&&
operator|(
name|buffer
index|[
name|bufferLength
operator|-
literal|1
index|]
operator|==
literal|'s'
operator|||
name|buffer
index|[
name|bufferLength
operator|-
literal|1
index|]
operator|==
literal|'S'
operator|)
condition|)
block|{
name|termAtt
operator|.
name|setLength
argument_list|(
name|bufferLength
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// Strip last 2 characters off
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

