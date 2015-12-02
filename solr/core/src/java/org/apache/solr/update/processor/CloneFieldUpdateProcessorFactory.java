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
name|SERVER_ERROR
import|;
end_import

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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
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
comment|/**  * Clones the values found in any matching<code>source</code> field into   * a configured<code>dest</code> field.  *<p>  * The<code>source</code> field(s) can be configured as either:  *</p>  *<ul>  *<li>One or more<code>&lt;str&gt;</code></li>  *<li>An<code>&lt;arr&gt;</code> of<code>&lt;str&gt;</code></li>  *<li>A<code>&lt;lst&gt;</code> containing {@link FieldMutatingUpdateProcessorFactory FieldMutatingUpdateProcessorFactory style selector arguments}</li>  *</ul>  *  *<p> The<code>dest</code> field can be a single<code>&lt;str&gt;</code>   * containing the literal name of a destination field, or it may be a<code>&lt;lst&gt;</code> specifying a   * regex<code>pattern</code> and a<code>replacement</code> string. If the pattern + replacement option   * is used the pattern will be matched against all fields matched by the source selector, and the replacement   * string (including any capture groups specified from the pattern) will be evaluated a using   * {@link Matcher#replaceAll(String)} to generate the literal name of the destination field.  *</p>  *  *<p>If the resolved<code>dest</code> field already exists in the document, then the   * values from the<code>source</code> fields will be added to it.  The   * "boost" value associated with the<code>dest</code> will not be changed,   * and any boost specified on the<code>source</code> fields will be ignored.    * (If the<code>dest</code> field did not exist prior to this processor, the   * newly created<code>dest</code> field will have the default boost of 1.0)  *</p>  *<p>  * In the example below:  *</p>  *<ul>  *<li>The<code>category</code> field will be cloned into the<code>category_s</code> field</li>  *<li>Both the<code>authors</code> and<code>editors</code> fields will be cloned into the   *<code>contributors</code> field  *</li>  *<li>Any field with a name ending in<code>_price</code> -- except for   *<code>list_price</code> -- will be cloned into the<code>all_prices</code>  *</li>  *<li>Any field name beginning with feat and ending in s (i.e. feats or features)   *       will be cloned into a field prefixed with key_ and not ending in s. (i.e. key_feat or key_feature)  *</li>  *</ul>  *  *<!-- see solrconfig-update-processors-chains.xml and   *      CloneFieldUpdateProcessorFactoryTest.testCloneFieldExample for where this is tested -->  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain name="multiple-clones"&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;str name="source"&gt;category&lt;/str&gt;  *&lt;str name="dest"&gt;category_s&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;arr name="source"&gt;  *&lt;str&gt;authors&lt;/str&gt;  *&lt;str&gt;editors&lt;/str&gt;  *&lt;/arr&gt;  *&lt;str name="dest"&gt;contributors&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.CloneFieldUpdateProcessorFactory"&gt;  *&lt;lst name="source"&gt;  *&lt;str name="fieldRegex"&gt;.*_price$&lt;/str&gt;  *&lt;lst name="exclude"&gt;  *&lt;str name="fieldName"&gt;list_price&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/lst&gt;  *&lt;str name="dest"&gt;all_prices&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.processor.CloneFieldUpdateProcessorFactory"&gt;  *&lt;lst name="source"&gt;  *&lt;str name="fieldRegex"&gt;^feat(.*)s$&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="dest"&gt;  *&lt;str name="pattern"&gt;^feat(.*)s$&lt;/str&gt;  *&lt;str name="replacement"&gt;key_feat$1&lt;/str&gt;  *&lt;/str&gt;  *&lt;/processor&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  *  *<p>  * In common case situations where you wish to use a single regular expression as both a   *<code>fieldRegex</code> selector and a destination<code>pattern</code>, a "short hand" syntax   * is support for convinience: The<code>pattern</code> and<code>replacement</code> may be specified   * at the top level, omitting<code>source</code> and<code>dest</code> declarations completely, and   * the<code>pattern</code> will be used to construct an equivilent<code>source</code> selector internally.  *</p>  *<p>  * For example, both of the following configurations are equivilent:  *</p>  *<pre class="prettyprint">  *&lt;!-- full syntax --&gt;  *&lt;processor class="solr.processor.CloneFieldUpdateProcessorFactory"&gt;  *&lt;lst name="source"&gt;  *&lt;str name="fieldRegex"^gt;$feat(.*)s$&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="dest"&gt;  *&lt;str name="pattern"&gt;^feat(.*)s$&lt;/str&gt;  *&lt;str name="replacement"&gt;key_feat$1&lt;/str&gt;  *&lt;/str&gt;  *&lt;/processor&gt;  *   *&lt;!-- syntactic sugar syntax --&gt;  *&lt;processor class="solr.processor.CloneFieldUpdateProcessorFactory"&gt;  *&lt;str name="pattern"&gt;^feat(.*)s$&lt;/str&gt;  *&lt;str name="replacement"&gt;key_feat$1&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  *  *<p>  * When cloning multiple fields (or a single multivalued field) into a single valued field, one of the   * {@link FieldValueSubsetUpdateProcessorFactory} implementations configured after the   *<code>CloneFieldUpdateProcessorFactory</code> can be useful to reduce the list of values down to a   * single value.  *</p>  *   * @see FieldValueSubsetUpdateProcessorFactory  */
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
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
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
DECL|field|PATTERN_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_PARAM
init|=
literal|"pattern"
decl_stmt|;
DECL|field|REPLACEMENT_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|REPLACEMENT_PARAM
init|=
literal|"replacement"
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
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|srcSelector
specifier|private
name|FieldNameSelector
name|srcSelector
init|=
literal|null
decl_stmt|;
comment|/**     * If pattern is null, this this is a literal field name.  If pattern is non-null then this    * is a replacement string that may contain meta-characters (ie: capture group identifiers)    * @see #pattern    */
DECL|field|dest
specifier|private
name|String
name|dest
init|=
literal|null
decl_stmt|;
comment|/** @see #dest */
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
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
comment|// high level (loose) check for which type of config we have.
comment|//
comment|// individual init methods do more strict syntax checking
if|if
condition|(
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|SOURCE_PARAM
argument_list|,
literal|0
argument_list|)
operator|&&
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|DEST_PARAM
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|initSourceSelectorSyntax
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|PATTERN_PARAM
argument_list|,
literal|0
argument_list|)
operator|&&
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|REPLACEMENT_PARAM
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|initSimpleRegexReplacement
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"A combination of either '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' + '"
operator|+
name|DEST_PARAM
operator|+
literal|"', or '"
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|"' + '"
operator|+
name|PATTERN_PARAM
operator|+
literal|"' init params are mandatory"
argument_list|)
throw|;
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
comment|/**    * init helper method that should only be called when we know for certain that both the     * "source" and "dest" init params do<em>not</em> exist.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initSimpleRegexReplacement
specifier|private
name|void
name|initSimpleRegexReplacement
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|// The syntactic sugar for the case where there is only one regex pattern for source and the same pattern
comment|// is used for the destination pattern...
comment|//
comment|//  pattern != null&& replacement != null
comment|//
comment|// ...as top level elements, with no other config options specified
comment|// if we got here we know we had pattern and replacement, now check for the other two  so that we can give a better
comment|// message than "unexpected"
if|if
condition|(
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|SOURCE_PARAM
argument_list|,
literal|0
argument_list|)
operator|||
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|DEST_PARAM
argument_list|,
literal|0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Short hand syntax must not be mixed with full syntax. Found "
operator|+
name|PATTERN_PARAM
operator|+
literal|" and "
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|" but also found "
operator|+
name|SOURCE_PARAM
operator|+
literal|" or "
operator|+
name|DEST_PARAM
argument_list|)
throw|;
block|}
assert|assert
name|args
operator|.
name|indexOf
argument_list|(
name|SOURCE_PARAM
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
assert|;
name|Object
name|patt
init|=
name|args
operator|.
name|remove
argument_list|(
name|PATTERN_PARAM
argument_list|)
decl_stmt|;
name|Object
name|replacement
init|=
name|args
operator|.
name|remove
argument_list|(
name|REPLACEMENT_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|patt
operator|||
literal|null
operator|==
name|replacement
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init params '"
operator|+
name|PATTERN_PARAM
operator|+
literal|"' and '"
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|"' are both mandatory if '"
operator|+
name|SOURCE_PARAM
operator|+
literal|"' and '"
operator|+
name|DEST_PARAM
operator|+
literal|"' are not both specified"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|0
operator|!=
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
literal|"Init params '"
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|"' and '"
operator|+
name|PATTERN_PARAM
operator|+
literal|"' must be children of '"
operator|+
name|DEST_PARAM
operator|+
literal|"' to be combined with other options."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|replacement
operator|instanceof
name|String
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
name|REPLACEMENT_PARAM
operator|+
literal|"' must be a string (i.e.<str>)"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|patt
operator|instanceof
name|String
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
name|PATTERN_PARAM
operator|+
literal|"' must be a string (i.e.<str>)"
argument_list|)
throw|;
block|}
name|dest
operator|=
name|replacement
operator|.
name|toString
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|patt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|pe
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init param "
operator|+
name|PATTERN_PARAM
operator|+
literal|" is not a valid regex pattern: "
operator|+
name|patt
argument_list|,
name|pe
argument_list|)
throw|;
block|}
name|srcInclusions
operator|=
operator|new
name|SelectorParams
argument_list|()
expr_stmt|;
name|srcInclusions
operator|.
name|fieldRegex
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|this
operator|.
name|pattern
argument_list|)
expr_stmt|;
block|}
comment|/**    * init helper method that should only be called when we know for certain that both the     * "source" and "dest" init params<em>do</em> exist.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initSourceSelectorSyntax
specifier|private
name|void
name|initSourceSelectorSyntax
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|// Full and complete syntax where source and dest are mandatory.
comment|//
comment|// source may be a single string or a selector.
comment|// dest may be a single string or list containing pattern and replacement
comment|//
comment|//   source != null&& dest != null
comment|// if we got here we know we had source and dest, now check for the other two so that we can give a better
comment|// message than "unexpected"
if|if
condition|(
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|PATTERN_PARAM
argument_list|,
literal|0
argument_list|)
operator|||
literal|0
operator|<=
name|args
operator|.
name|indexOf
argument_list|(
name|REPLACEMENT_PARAM
argument_list|,
literal|0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Short hand syntax must not be mixed with full syntax. Found "
operator|+
name|SOURCE_PARAM
operator|+
literal|" and "
operator|+
name|DEST_PARAM
operator|+
literal|" but also found "
operator|+
name|PATTERN_PARAM
operator|+
literal|" or "
operator|+
name|REPLACEMENT_PARAM
argument_list|)
throw|;
block|}
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
assert|assert
literal|null
operator|!=
name|d
assert|;
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
assert|assert
literal|null
operator|!=
name|sources
assert|;
if|if
condition|(
literal|1
operator|==
name|sources
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
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
comment|// consume from the named list so it doesn't interfere with subsequent processing
name|sources
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|1
operator|<=
name|sources
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// source better be one or more strings
name|srcInclusions
operator|.
name|fieldName
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|args
operator|.
name|removeConfigArgs
argument_list|(
literal|"source"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|srcInclusions
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Init params do not specify anything to clone, please supply either "
operator|+
name|SOURCE_PARAM
operator|+
literal|" and "
operator|+
name|DEST_PARAM
operator|+
literal|" or "
operator|+
name|PATTERN_PARAM
operator|+
literal|" and "
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|". See javadocs"
operator|+
literal|"for CloneFieldUpdateProcessorFactory for further details."
argument_list|)
throw|;
block|}
if|if
condition|(
name|d
operator|instanceof
name|NamedList
condition|)
block|{
name|NamedList
name|destList
init|=
operator|(
name|NamedList
operator|)
name|d
decl_stmt|;
name|Object
name|patt
init|=
name|destList
operator|.
name|remove
argument_list|(
name|PATTERN_PARAM
argument_list|)
decl_stmt|;
name|Object
name|replacement
init|=
name|destList
operator|.
name|remove
argument_list|(
name|REPLACEMENT_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|patt
operator|||
literal|null
operator|==
name|replacement
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
literal|"' children '"
operator|+
name|PATTERN_PARAM
operator|+
literal|"' and '"
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|"' are both mandatoryand can not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|patt
operator|instanceof
name|String
operator|&&
name|replacement
operator|instanceof
name|String
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
literal|"' children '"
operator|+
name|PATTERN_PARAM
operator|+
literal|"' and '"
operator|+
name|REPLACEMENT_PARAM
operator|+
literal|"' must both be strings (i.e.<str>)"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|0
operator|!=
name|destList
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
name|DEST_PARAM
operator|+
literal|"' has unexpected children: '"
operator|+
name|destList
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
try|try
block|{
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|patt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|pe
parameter_list|)
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
literal|"' child '"
operator|+
name|PATTERN_PARAM
operator|+
literal|" is not a valid regex pattern: "
operator|+
name|patt
argument_list|,
name|pe
argument_list|)
throw|;
block|}
name|dest
operator|=
name|replacement
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|d
operator|instanceof
name|String
condition|)
block|{
name|dest
operator|=
name|d
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
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
literal|"' must either be a string "
operator|+
literal|"(i.e.<str>) or a list (i.e.<lst>) containing '"
operator|+
name|PATTERN_PARAM
operator|+
literal|"' and '"
operator|+
name|REPLACEMENT_PARAM
argument_list|)
throw|;
block|}
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
argument_list|,
name|srcInclusions
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
argument_list|,
name|exc
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
specifier|final
name|FieldNameSelector
name|srcSelector
init|=
name|getSourceSelector
argument_list|()
decl_stmt|;
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
comment|// destination may be regex replace string, which can cause multiple output fields.
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
name|destMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// preserve initial values and boost (if any)
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
name|Collection
argument_list|<
name|Object
argument_list|>
name|srcFieldValues
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcFieldValues
operator|==
literal|null
operator|||
name|srcFieldValues
operator|.
name|isEmpty
argument_list|()
condition|)
continue|continue;
name|String
name|resolvedDest
init|=
name|dest
decl_stmt|;
if|if
condition|(
name|pattern
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|resolvedDest
operator|=
name|matcher
operator|.
name|replaceAll
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"CloneFieldUpdateProcessor.srcSelector.shouldMutate(\"{}\") returned true, "
operator|+
literal|"but replacement pattern did not match, field skipped."
argument_list|,
name|fname
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|SolrInputField
name|destField
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|containsKey
argument_list|(
name|resolvedDest
argument_list|)
condition|)
block|{
name|destField
operator|=
name|doc
operator|.
name|getField
argument_list|(
name|resolvedDest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SolrInputField
name|targetField
init|=
name|destMap
operator|.
name|get
argument_list|(
name|resolvedDest
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetField
operator|==
literal|null
condition|)
block|{
name|destField
operator|=
operator|new
name|SolrInputField
argument_list|(
name|resolvedDest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destField
operator|=
name|targetField
expr_stmt|;
block|}
block|}
for|for
control|(
name|Object
name|val
range|:
name|srcFieldValues
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
comment|// put it in map to avoid concurrent modification...
name|destMap
operator|.
name|put
argument_list|(
name|resolvedDest
argument_list|,
name|destField
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|dest
range|:
name|destMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|doc
operator|.
name|put
argument_list|(
name|dest
argument_list|,
name|destMap
operator|.
name|get
argument_list|(
name|dest
argument_list|)
argument_list|)
expr_stmt|;
block|}
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

