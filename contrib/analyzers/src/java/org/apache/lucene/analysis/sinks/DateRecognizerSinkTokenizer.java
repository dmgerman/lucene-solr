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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|SinkTokenizer
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
name|Token
import|;
end_import

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
name|SimpleDateFormat
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
name|List
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

begin_comment
comment|/**  * Attempts to parse the {@link org.apache.lucene.analysis.Token#termBuffer()} as a Date using a {@link java.text.DateFormat}.  * If the value is a Date, it will add it to the sink.  *<p/>  * Also marks the sink token with {@link org.apache.lucene.analysis.Token#type()} equal to {@link #DATE_TYPE}  *  *  **/
end_comment

begin_class
DECL|class|DateRecognizerSinkTokenizer
specifier|public
class|class
name|DateRecognizerSinkTokenizer
extends|extends
name|SinkTokenizer
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
comment|/**    * Uses {@link java.text.SimpleDateFormat#getDateInstance()} as the {@link java.text.DateFormat} object.    */
DECL|method|DateRecognizerSinkTokenizer
specifier|public
name|DateRecognizerSinkTokenizer
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|SimpleDateFormat
operator|.
name|getDateInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DateRecognizerSinkTokenizer
specifier|public
name|DateRecognizerSinkTokenizer
parameter_list|(
name|DateFormat
name|dateFormat
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|dateFormat
argument_list|)
expr_stmt|;
block|}
comment|/**    * Uses {@link java.text.SimpleDateFormat#getDateInstance()} as the {@link java.text.DateFormat} object.    * @param input The input list of Tokens that are already Dates.  They should be marked as type {@link #DATE_TYPE} for completeness    */
DECL|method|DateRecognizerSinkTokenizer
specifier|public
name|DateRecognizerSinkTokenizer
parameter_list|(
name|List
comment|/*<Token>*/
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|SimpleDateFormat
operator|.
name|getDateInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param input    * @param dateFormat The date format to use to try and parse the date.  Note, this SinkTokenizer makes no attempt to synchronize the DateFormat object    */
DECL|method|DateRecognizerSinkTokenizer
specifier|public
name|DateRecognizerSinkTokenizer
parameter_list|(
name|List
comment|/*<Token>*/
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
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
comment|//Check to see if this token is a date
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Date
name|date
init|=
name|dateFormat
operator|.
name|parse
argument_list|(
operator|new
name|String
argument_list|(
name|t
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|termLength
argument_list|()
argument_list|)
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
name|t
operator|.
name|setType
argument_list|(
name|DATE_TYPE
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|t
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{        }
block|}
block|}
block|}
end_class

end_unit

