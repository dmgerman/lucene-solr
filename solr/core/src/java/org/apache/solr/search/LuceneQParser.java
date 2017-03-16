begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|Query
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
name|CommonParams
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
name|util
operator|.
name|StrUtils
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  * @see LuceneQParserPlugin  */
end_comment

begin_class
DECL|class|LuceneQParser
specifier|public
class|class
name|LuceneQParser
extends|extends
name|QParser
block|{
DECL|field|lparser
name|SolrQueryParser
name|lparser
decl_stmt|;
DECL|method|LuceneQParser
specifier|public
name|LuceneQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|qstr
init|=
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|qstr
operator|==
literal|null
operator|||
name|qstr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|String
name|defaultField
init|=
name|getParam
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultField
operator|==
literal|null
condition|)
block|{
name|defaultField
operator|=
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getDefaultSearchFieldName
argument_list|()
expr_stmt|;
block|}
name|lparser
operator|=
operator|new
name|SolrQueryParser
argument_list|(
name|this
argument_list|,
name|defaultField
argument_list|)
expr_stmt|;
name|lparser
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParsing
operator|.
name|getQueryParserDefaultOperator
argument_list|(
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|,
name|getParam
argument_list|(
name|QueryParsing
operator|.
name|OP
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|lparser
operator|.
name|setSplitOnWhitespace
argument_list|(
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|getParam
argument_list|(
name|QueryParsing
operator|.
name|SPLIT_ON_WHITESPACE
argument_list|)
argument_list|,
name|SolrQueryParser
operator|.
name|DEFAULT_SPLIT_ON_WHITESPACE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|lparser
operator|.
name|parse
argument_list|(
name|qstr
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultHighlightFields
specifier|public
name|String
index|[]
name|getDefaultHighlightFields
parameter_list|()
block|{
return|return
name|lparser
operator|==
literal|null
condition|?
operator|new
name|String
index|[]
block|{}
else|:
operator|new
name|String
index|[]
block|{
name|lparser
operator|.
name|getDefaultField
argument_list|()
block|}
return|;
block|}
block|}
end_class

end_unit

