begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|schema
operator|.
name|IndexSchema
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|SolrResourceLoader
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
name|plugin
operator|.
name|SolrCoreAware
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
name|common
operator|.
name|SolrInputField
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
name|SolrInputDocument
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
import|import static
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
operator|.
name|*
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
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|processor
operator|.
name|FieldMutatingUpdateProcessorFactory
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
name|update
operator|.
name|processor
operator|.
name|FieldMutatingUpdateProcessorFactory
operator|.
name|SelectorParams
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
name|update
operator|.
name|processor
operator|.
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Clones the values found in any matching<code>source</code> field into   * the configured<code>dest</code> field.  *<p>  * While the<code>dest</code> field must be a single<code>&lt;str&gt;</code>,   * the<code>source</code> fields can be configured as either:  *</p>  *<ul>  *<li>One or more<code>&lt;str&gt;</code></li>  *<li>An<code>&lt;arr&gt;</code> of<code>&lt;str&gt;</code></li>  *<li>A<code>&lt;lst&gt;</code> containing {@link FieldMutatingUpdateProcessorFactory FieldMutatingUpdateProcessorFactory style selector arguments}</li>  *</ul>  *<p>  * If the<code>dest</code> field already exists in the document, then the   * values from the<code>source</code> fields will be added to it.  The   * "boost" value associated with the<code>dest</code> will not be changed,   * and any boost specified on the<code>source</code> fields will be ignored.    * (If the<code>dest</code> field did not exist prior to this processor, the   * newly created<code>dest</code> field will have the default boost of 1.0)  *</p>  *<p>  * In the example below, the<code>category</code> field will be cloned   * into the<code>category_s</code> field, both the<code>authors</code> and   *<code>editors</code> fields will be cloned into the<code>contributors</code>  * field, and any field with a name ending in<code>_price</code> -- except for   *<code>list_price</code> -- will be cloned into the<code>all_prices</code>   * field.   *</p>  *<!-- see solrconfig-update-processors-chains.xml for where this is tested -->  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain name="multiple-clones"&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;str name="source"&gt;category&lt;/str&gt;  *&lt;str name="dest"&gt;category_s&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;arr name="source"&gt;  *&lt;str&gt;authors&lt;/str&gt;  *&lt;str&gt;editors&lt;/str&gt;  *&lt;/arr&gt;  *&lt;str name="dest"&gt;contributors&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;lst name="source"&gt;  *&lt;str name="fieldRegex"&gt;.*_price&lt;/str&gt;  *&lt;lst name="exclude"&gt;  *&lt;str name="fieldName"&gt;list_price&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/lst&gt;  *&lt;str name="dest"&gt;all_prices&lt;/str&gt;  *&lt;/processor&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  */
end_comment

begin_class
DECL|class|CloneFieldUpdateProcessorFactory
specifier|public
class|class
name|CloneFieldUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|log
specifier|private
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CloneFieldUpdateProcessorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SOURCE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_PARAM
init|=
literal|"source"
decl_stmt|;
DECL|field|DEST_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|DEST_PARAM
init|=
literal|"dest"
decl_stmt|;
DECL|field|srcInclusions
specifier|private
name|SelectorParams
name|srcInclusions
init|=
operator|new
name|SelectorParams
argument_list|()
decl_stmt|;
DECL|field|srcExclusions
specifier|private
name|Collection
argument_list|<
name|SelectorParams
argument_list|>
name|srcExclusions
init|=
operator|new
name|ArrayList
argument_list|<
name|SelectorParams
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|srcSelector
specifier|private
name|FieldNameSelector
name|srcSelector
init|=
literal|null
decl_stmt|;
DECL|field|dest
specifier|private
name|String
name|dest
init|=
literal|null
decl_stmt|;
DECL|method|getSourceSelector
specifier|protected
specifier|final
name|FieldNameSelector
name|getSourceSelector
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|srcSelector
condition|)
return|return
name|srcSelector
return|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"selector was never initialized, "
operator|+
literal|" inform(SolrCore) never called???"
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|Object
name|d
init|=
name|args
operator|.
name|remove
argument_list|(
name|DEST_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|d
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|DEST_PARAM
operator|+
literal|"' must be specified"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|d
operator|instanceof
name|CharSequence
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|DEST_PARAM
operator|+
literal|"' must be a string (ie: 'str')"
argument_list|)
throw|;
block|}
name|dest
operator|=
name|d
operator|.
name|toString
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|sources
init|=
name|args
operator|.
name|getAll
argument_list|(
name|SOURCE_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|sources
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' must be specified"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|==
name|sources
operator|.
name|size
argument_list|()
operator|&&
name|sources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|NamedList
condition|)
block|{
comment|// nested set of selector options
name|NamedList
name|selectorConfig
init|=
operator|(
name|NamedList
operator|)
name|args
operator|.
name|remove
argument_list|(
name|SOURCE_PARAM
argument_list|)
decl_stmt|;
name|srcInclusions
operator|=
name|parseSelectorParams
argument_list|(
name|selectorConfig
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|excList
init|=
name|selectorConfig
operator|.
name|getAll
argument_list|(
literal|"exclude"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|excObj
range|:
name|excList
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|excObj
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' child 'exclude' can not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|excObj
operator|instanceof
name|NamedList
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' child 'exclude' must be<lst/>"
argument_list|)
throw|;
block|}
name|NamedList
name|exc
init|=
operator|(
name|NamedList
operator|)
name|excObj
decl_stmt|;
name|srcExclusions
operator|.
name|add
argument_list|(
name|parseSelectorParams
argument_list|(
name|exc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|<
name|exc
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' has unexpected 'exclude' sub-param(s): '"
operator|+
name|selectorConfig
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|// call once per instance
name|selectorConfig
operator|.
name|remove
argument_list|(
literal|"exclude"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|<
name|selectorConfig
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' contains unexpected child param(s): '"
operator|+
name|selectorConfig
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// source better be one or more strings
name|srcInclusions
operator|.
name|fieldName
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|FieldMutatingUpdateProcessorFactory
operator|.
name|oneOrMany
argument_list|(
name|args
argument_list|,
literal|"source"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|0
operator|<
name|args
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Unexpected init param(s): '"
operator|+
name|args
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
specifier|final
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|srcSelector
operator|=
name|FieldMutatingUpdateProcessor
operator|.
name|createFieldNameSelector
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|srcInclusions
operator|.
name|fieldName
argument_list|,
name|srcInclusions
operator|.
name|typeName
argument_list|,
name|srcInclusions
operator|.
name|typeClass
argument_list|,
name|srcInclusions
operator|.
name|fieldRegex
argument_list|,
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
argument_list|)
expr_stmt|;
for|for
control|(
name|SelectorParams
name|exc
range|:
name|srcExclusions
control|)
block|{
name|srcSelector
operator|=
name|FieldMutatingUpdateProcessor
operator|.
name|wrap
argument_list|(
name|srcSelector
argument_list|,
name|FieldMutatingUpdateProcessor
operator|.
name|createFieldNameSelector
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|exc
operator|.
name|fieldName
argument_list|,
name|exc
operator|.
name|typeName
argument_list|,
name|exc
operator|.
name|typeClass
argument_list|,
name|exc
operator|.
name|fieldRegex
argument_list|,
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_NO_FIELDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
specifier|final
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
return|return
operator|new
name|UpdateRequestProcessor
argument_list|(
name|next
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SolrInputDocument
name|doc
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
comment|// preserve initial values and boost (if any)
name|SolrInputField
name|destField
init|=
name|doc
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
condition|?
name|doc
operator|.
name|getField
argument_list|(
name|dest
argument_list|)
else|:
operator|new
name|SolrInputField
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fname
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|srcSelector
operator|.
name|shouldMutate
argument_list|(
name|fname
argument_list|)
condition|)
continue|continue;
for|for
control|(
name|Object
name|val
range|:
name|doc
operator|.
name|getFieldValues
argument_list|(
name|fname
argument_list|)
control|)
block|{
comment|// preserve existing dest boost (multiplicitive), ignore src boost
name|destField
operator|.
name|addValue
argument_list|(
name|val
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
name|modified
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|modified
condition|)
name|doc
operator|.
name|put
argument_list|(
name|dest
argument_list|,
name|destField
argument_list|)
expr_stmt|;
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/** macro */
DECL|method|parseSelectorParams
specifier|private
specifier|static
name|SelectorParams
name|parseSelectorParams
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
return|return
name|FieldMutatingUpdateProcessorFactory
operator|.
name|parseSelectorParams
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
end_class

end_unit

