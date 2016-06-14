begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|FilteringTokenFilter
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

begin_comment
comment|/** Filters all tokens that cannot be parsed to a date, using the provided {@link DateFormat}. */
end_comment

begin_class
DECL|class|DateRecognizerFilter
specifier|public
class|class
name|DateRecognizerFilter
extends|extends
name|FilteringTokenFilter
block|{
DECL|field|DATE_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DATE_TYPE
init|=
literal|"date"
decl_stmt|;
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
DECL|field|dateFormat
specifier|private
specifier|final
name|DateFormat
name|dateFormat
decl_stmt|;
comment|/**    * Uses {@link DateFormat#DEFAULT} and {@link Locale#ENGLISH} to create a {@link DateFormat} instance.    */
DECL|method|DateRecognizerFilter
specifier|public
name|DateRecognizerFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|DateRecognizerFilter
specifier|public
name|DateRecognizerFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|DateFormat
name|dateFormat
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|dateFormat
operator|=
name|dateFormat
operator|!=
literal|null
condition|?
name|dateFormat
else|:
name|DateFormat
operator|.
name|getDateInstance
argument_list|(
name|DateFormat
operator|.
name|DEFAULT
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|()
block|{
try|try
block|{
comment|// We don't care about the date, just that the term can be parsed to one.
name|dateFormat
operator|.
name|parse
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// This term is not a date.
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

