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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|index
operator|.
name|IndexableField
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
name|JavaBinCodec
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
name|JavaBinCodec
operator|.
name|ObjectResolver
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
name|response
operator|.
name|QueryResponseWriter
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
name|response
operator|.
name|TextResponseWriter
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
name|response
operator|.
name|WriteableValue
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
import|;
end_import

begin_comment
comment|/**  * @since solr 5.2  */
end_comment

begin_class
DECL|class|RawValueTransformerFactory
specifier|public
class|class
name|RawValueTransformerFactory
extends|extends
name|TransformerFactory
block|{
DECL|field|applyToWT
name|String
name|applyToWT
init|=
literal|null
decl_stmt|;
DECL|method|RawValueTransformerFactory
specifier|public
name|RawValueTransformerFactory
parameter_list|()
block|{        }
DECL|method|RawValueTransformerFactory
specifier|public
name|RawValueTransformerFactory
parameter_list|(
name|String
name|wt
parameter_list|)
block|{
name|this
operator|.
name|applyToWT
operator|=
name|wt
expr_stmt|;
block|}
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
operator|&&
name|defaultUserArgs
operator|.
name|startsWith
argument_list|(
literal|"wt="
argument_list|)
condition|)
block|{
name|applyToWT
operator|=
name|defaultUserArgs
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
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
name|display
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|String
name|field
init|=
name|params
operator|.
name|get
argument_list|(
literal|"f"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|field
operator|=
name|display
expr_stmt|;
block|}
comment|// When a 'wt' is specified in the transformer, only apply it to the same wt
name|boolean
name|apply
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|applyToWT
operator|!=
literal|null
condition|)
block|{
name|String
name|qwt
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
decl_stmt|;
if|if
condition|(
name|qwt
operator|==
literal|null
condition|)
block|{
name|QueryResponseWriter
name|qw
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|QueryResponseWriter
name|dw
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
name|applyToWT
argument_list|)
decl_stmt|;
if|if
condition|(
name|qw
operator|!=
name|dw
condition|)
block|{
name|apply
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|apply
operator|=
name|applyToWT
operator|.
name|equals
argument_list|(
name|qwt
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|apply
condition|)
block|{
return|return
operator|new
name|RawTransformer
argument_list|(
name|field
argument_list|,
name|display
argument_list|)
return|;
block|}
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|display
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|// nothing
block|}
return|return
operator|new
name|RenameFieldTransformer
argument_list|(
name|field
argument_list|,
name|display
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|class|RawTransformer
specifier|static
class|class
name|RawTransformer
extends|extends
name|DocTransformer
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|display
specifier|final
name|String
name|display
decl_stmt|;
DECL|method|RawTransformer
specifier|public
name|RawTransformer
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|display
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|display
operator|=
name|display
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
name|display
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
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|Object
name|val
init|=
name|doc
operator|.
name|remove
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|val
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
name|current
init|=
operator|(
name|Collection
operator|)
name|val
decl_stmt|;
name|ArrayList
argument_list|<
name|WriteableStringValue
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<
name|RawValueTransformerFactory
operator|.
name|WriteableStringValue
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|v
range|:
name|current
control|)
block|{
name|vals
operator|.
name|add
argument_list|(
operator|new
name|WriteableStringValue
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|setField
argument_list|(
name|display
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|setField
argument_list|(
name|display
argument_list|,
operator|new
name|WriteableStringValue
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getExtraRequestFields
specifier|public
name|String
index|[]
name|getExtraRequestFields
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|this
operator|.
name|field
block|}
return|;
block|}
block|}
DECL|class|WriteableStringValue
specifier|public
specifier|static
class|class
name|WriteableStringValue
extends|extends
name|WriteableValue
block|{
DECL|field|val
specifier|public
specifier|final
name|Object
name|val
decl_stmt|;
DECL|method|WriteableStringValue
specifier|public
name|WriteableStringValue
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|String
name|name
parameter_list|,
name|TextResponseWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|str
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|IndexableField
condition|)
block|{
comment|// delays holding it in memory
name|str
operator|=
operator|(
operator|(
name|IndexableField
operator|)
name|val
operator|)
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|str
operator|=
name|val
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|Object
name|o
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|ObjectResolver
name|orig
init|=
name|codec
operator|.
name|getResolver
argument_list|()
decl_stmt|;
if|if
condition|(
name|orig
operator|!=
literal|null
condition|)
block|{
name|codec
operator|.
name|writeVal
argument_list|(
name|orig
operator|.
name|resolve
argument_list|(
name|val
argument_list|,
name|codec
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

