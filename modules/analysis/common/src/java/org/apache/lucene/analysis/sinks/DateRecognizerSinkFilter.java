begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Date
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
name|AttributeSource
import|;
end_import

begin_comment
comment|/**  * Attempts to parse the {@link CharTermAttribute#buffer()} as a Date using a {@link java.text.DateFormat}.  * If the value is a Date, it will add it to the sink.  *<p/>   *  **/
end_comment

begin_class
DECL|class|DateRecognizerSinkFilter
specifier|public
class|class
name|DateRecognizerSinkFilter
extends|extends
name|TeeSinkTokenFilter
operator|.
name|SinkFilter
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
DECL|field|dateFormat
specifier|protected
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|field|termAtt
specifier|protected
name|CharTermAttribute
name|termAtt
decl_stmt|;
comment|/**    * Uses {@link java.text.SimpleDateFormat#getDateInstance()} as the {@link java.text.DateFormat} object.    */
DECL|method|DateRecognizerSinkFilter
specifier|public
name|DateRecognizerSinkFilter
parameter_list|()
block|{
name|this
argument_list|(
name|DateFormat
operator|.
name|getDateInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DateRecognizerSinkFilter
specifier|public
name|DateRecognizerSinkFilter
parameter_list|(
name|DateFormat
name|dateFormat
parameter_list|)
block|{
name|this
operator|.
name|dateFormat
operator|=
name|dateFormat
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|termAtt
operator|==
literal|null
condition|)
block|{
name|termAtt
operator|=
name|source
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Date
name|date
init|=
name|dateFormat
operator|.
name|parse
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|//We don't care about the date, just that we can parse it as a date
if|if
condition|(
name|date
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{        }
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

