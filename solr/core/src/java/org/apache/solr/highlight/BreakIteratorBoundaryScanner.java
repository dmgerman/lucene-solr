begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
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
name|search
operator|.
name|vectorhighlight
operator|.
name|BoundaryScanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|HighlightParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_class
DECL|class|BreakIteratorBoundaryScanner
specifier|public
class|class
name|BreakIteratorBoundaryScanner
extends|extends
name|SolrBoundaryScanner
block|{
annotation|@
name|Override
DECL|method|get
specifier|protected
name|BoundaryScanner
name|get
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
comment|// construct Locale
name|String
name|language
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|BS_LANGUAGE
argument_list|)
decl_stmt|;
name|String
name|country
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|BS_COUNTRY
argument_list|)
decl_stmt|;
if|if
condition|(
name|country
operator|!=
literal|null
operator|&&
name|language
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|HighlightParams
operator|.
name|BS_LANGUAGE
operator|+
literal|" parameter cannot be null when you specify "
operator|+
name|HighlightParams
operator|.
name|BS_COUNTRY
argument_list|)
throw|;
block|}
name|Locale
name|locale
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|language
operator|!=
literal|null
condition|)
block|{
name|locale
operator|=
name|country
operator|==
literal|null
condition|?
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
else|:
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|locale
operator|=
name|Locale
operator|.
name|ROOT
expr_stmt|;
block|}
comment|// construct BreakIterator
name|String
name|type
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|BS_TYPE
argument_list|,
literal|"WORD"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|BreakIterator
name|bi
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"character"
argument_list|)
condition|)
block|{
name|bi
operator|=
name|BreakIterator
operator|.
name|getCharacterInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"word"
argument_list|)
condition|)
block|{
name|bi
operator|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"line"
argument_list|)
condition|)
block|{
name|bi
operator|=
name|BreakIterator
operator|.
name|getLineInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"sentence"
argument_list|)
condition|)
block|{
name|bi
operator|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|type
operator|+
literal|" is invalid for parameter "
operator|+
name|HighlightParams
operator|.
name|BS_TYPE
argument_list|)
throw|;
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|BreakIteratorBoundaryScanner
argument_list|(
name|bi
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////
comment|//////////////////////// SolrInfoMBeans methods ///////////////////////
comment|///////////////////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"BreakIteratorBoundaryScanner"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class

end_unit

