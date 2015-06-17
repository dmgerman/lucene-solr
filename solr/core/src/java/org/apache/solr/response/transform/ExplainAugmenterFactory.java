begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
package|;
end_package

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
name|search
operator|.
name|Explanation
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
name|SolrDocument
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
name|NamedList
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|SolrPluginUtils
import|;
end_import

begin_comment
comment|/**  *  * @since solr 4.0  */
end_comment

begin_class
DECL|class|ExplainAugmenterFactory
specifier|public
class|class
name|ExplainAugmenterFactory
extends|extends
name|TransformerFactory
block|{
DECL|enum|Style
specifier|public
specifier|static
enum|enum
name|Style
block|{
DECL|enum constant|nl
name|nl
block|,
DECL|enum constant|text
name|text
block|,
DECL|enum constant|html
name|html
block|}
empty_stmt|;
DECL|field|defaultStyle
specifier|protected
name|Style
name|defaultStyle
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultUserArgs
operator|!=
literal|null
condition|)
block|{
name|defaultStyle
operator|=
name|getStyle
argument_list|(
name|defaultUserArgs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|defaultStyle
operator|=
name|Style
operator|.
name|nl
expr_stmt|;
block|}
block|}
DECL|method|getStyle
specifier|public
specifier|static
name|Style
name|getStyle
parameter_list|(
name|String
name|str
parameter_list|)
block|{
try|try
block|{
return|return
name|Style
operator|.
name|valueOf
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unknown Explain Style: "
operator|+
name|str
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
name|s
init|=
name|params
operator|.
name|get
argument_list|(
literal|"style"
argument_list|)
decl_stmt|;
name|Style
name|style
init|=
operator|(
name|s
operator|==
literal|null
operator|)
condition|?
name|defaultStyle
else|:
name|getStyle
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExplainAugmenter
argument_list|(
name|field
argument_list|,
name|style
argument_list|)
return|;
block|}
DECL|class|ExplainAugmenter
specifier|static
class|class
name|ExplainAugmenter
extends|extends
name|DocTransformer
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|style
specifier|final
name|Style
name|style
decl_stmt|;
DECL|method|ExplainAugmenter
specifier|public
name|ExplainAugmenter
parameter_list|(
name|String
name|display
parameter_list|,
name|Style
name|style
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|display
expr_stmt|;
name|this
operator|.
name|style
operator|=
name|style
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|transform
specifier|public
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
block|{
if|if
condition|(
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getQuery
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Explanation
name|exp
init|=
name|context
operator|.
name|getSearcher
argument_list|()
operator|.
name|explain
argument_list|(
name|context
operator|.
name|getQuery
argument_list|()
argument_list|,
name|docid
argument_list|)
decl_stmt|;
if|if
condition|(
name|style
operator|==
name|Style
operator|.
name|nl
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|SolrPluginUtils
operator|.
name|explanationToNamedList
argument_list|(
name|exp
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|style
operator|==
name|Style
operator|.
name|html
condition|)
block|{
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|exp
operator|.
name|toHtml
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|exp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

