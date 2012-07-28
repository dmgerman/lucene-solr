begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * Parser for the FT docs in trec disks 4+5 collection format  */
end_comment

begin_class
DECL|class|TrecLATimesParser
specifier|public
class|class
name|TrecLATimesParser
extends|extends
name|TrecDocParser
block|{
DECL|field|DATE
specifier|private
specifier|static
specifier|final
name|String
name|DATE
init|=
literal|"<DATE>"
decl_stmt|;
DECL|field|DATE_END
specifier|private
specifier|static
specifier|final
name|String
name|DATE_END
init|=
literal|"</DATE>"
decl_stmt|;
DECL|field|DATE_NOISE
specifier|private
specifier|static
specifier|final
name|String
name|DATE_NOISE
init|=
literal|"day,"
decl_stmt|;
comment|// anything aftre the ','
DECL|field|SUBJECT
specifier|private
specifier|static
specifier|final
name|String
name|SUBJECT
init|=
literal|"<SUBJECT>"
decl_stmt|;
DECL|field|SUBJECT_END
specifier|private
specifier|static
specifier|final
name|String
name|SUBJECT_END
init|=
literal|"</SUBJECT>"
decl_stmt|;
DECL|field|HEADLINE
specifier|private
specifier|static
specifier|final
name|String
name|HEADLINE
init|=
literal|"<HEADLINE>"
decl_stmt|;
DECL|field|HEADLINE_END
specifier|private
specifier|static
specifier|final
name|String
name|HEADLINE_END
init|=
literal|"</HEADLINE>"
decl_stmt|;
annotation|@
name|Override
DECL|method|parse
specifier|public
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|TrecContentSource
name|trecSrc
parameter_list|,
name|StringBuilder
name|docBuf
parameter_list|,
name|ParsePathType
name|pathType
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|mark
init|=
literal|0
decl_stmt|;
comment|// that much is skipped
comment|// date...
name|Date
name|date
init|=
literal|null
decl_stmt|;
name|String
name|dateStr
init|=
name|extract
argument_list|(
name|docBuf
argument_list|,
name|DATE
argument_list|,
name|DATE_END
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateStr
operator|!=
literal|null
condition|)
block|{
name|int
name|d2a
init|=
name|dateStr
operator|.
name|indexOf
argument_list|(
name|DATE_NOISE
argument_list|)
decl_stmt|;
if|if
condition|(
name|d2a
operator|>
literal|0
condition|)
block|{
name|dateStr
operator|=
name|dateStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|d2a
operator|+
literal|3
argument_list|)
expr_stmt|;
comment|// we need the "day" part
block|}
name|dateStr
operator|=
name|stripTags
argument_list|(
name|dateStr
argument_list|,
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|date
operator|=
name|trecSrc
operator|.
name|parseDate
argument_list|(
name|dateStr
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// title... first try with SUBJECT, them with HEADLINE
name|String
name|title
init|=
name|extract
argument_list|(
name|docBuf
argument_list|,
name|SUBJECT
argument_list|,
name|SUBJECT_END
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|title
operator|==
literal|null
condition|)
block|{
name|title
operator|=
name|extract
argument_list|(
name|docBuf
argument_list|,
name|HEADLINE
argument_list|,
name|HEADLINE_END
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|title
operator|!=
literal|null
condition|)
block|{
name|title
operator|=
name|stripTags
argument_list|(
name|title
argument_list|,
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|stripTags
argument_list|(
name|docBuf
argument_list|,
name|mark
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
block|}
end_class

end_unit

