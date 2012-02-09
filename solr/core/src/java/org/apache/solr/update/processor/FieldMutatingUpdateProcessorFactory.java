begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|common
operator|.
name|SolrException
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
name|schema
operator|.
name|FieldType
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

begin_comment
comment|/**  * Base class for implementing Factories for FieldMutatingUpdateProcessors and   * FieldValueMutatingUpdateProcessors.  *  *<p>  * This class provides all of the plumbing for configuring the   * FieldNameSelector using the following init params to specify selection   * critera...  *</p>  *<ul>  *<li><code>fieldName</code> - selecting specific fields by field name lookup</li>  *<li><code>fieldRegex</code> - selecting specific fields by field name regex match (regexes are checked in the order specified)</li>  *<li><code>typeName</code> - selecting specific fields by fieldType name lookup</li>  *<li><code>typeClass</code> - selecting specific fields by fieldType class lookup, including inheritence and interfaces</li>  *</ul>  *  *<p>  * Each critera can specified as either an&lt;arr&gt; of&lt;str&gt;, or   * multiple&lt;str&gt; with the same name.  When multiple criteria of a   * single type exist, fields must match<b>at least one</b> to be selected.    * If more then one type of critera exist, fields must match   *<b>at least one of each</b> to be selected.  *</p>  *<p>  * One or more<code>excludes</code>&lt;lst&gt; params may also be specified,   * containing any of the above criteria, identifying fields to be excluded   * from seelction even if they match the selection criteria.  As with the main   * selection critiera a field must match all of criteria in a single exclusion   * in order to be excluded, but multiple exclusions may be specified to get an   *<code>OR</code> behavior  *</p>  *  *<p>  * In the ExampleFieldMutatingUpdateProcessorFactory configured below,   * fields will be mutated if the name starts with "foo"<i>or</i> "bar";   *<b>unless</b> the field name contains the substring "SKIP"<i>or</i>   * the fieldType is (or subclasses) DateField.  Meaning a field named   * "foo_SKIP" is gaurunteed not to be selected, but a field named "bar_smith"   * that uses StrField will be selected.  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.ExampleFieldMutatingUpdateProcessorFactory"&gt;  *&lt;str name="fieldRegex"&gt;foo.*&lt;/str&gt;  *&lt;str name="fieldRegex"&gt;bar.*&lt;/str&gt;  *&lt;!-- each set of exclusions is checked independently --&gt;  *&lt;lst name="exclude"&gt;  *&lt;str name="fieldRegex"&gt;.*SKIP.*&lt;/str&gt;  *&lt;/lst&gt;  *&lt;lst name="exclude"&gt;  *&lt;str name="typeClass"&gt;solr.DateField&lt;/str&gt;  *&lt;/lst&gt;  *&lt;/processor&gt;  *</pre>  *   *<p>  * Subclasses define the default selection behavior to be applied if no   * criteria is configured by the user.  User configured "exclude" criteria   * will be applied to the subclass defined default selector.  *</p>  *   * @see FieldMutatingUpdateProcessor  * @see FieldValueMutatingUpdateProcessor  * @see FieldMutatingUpdateProcessor.FieldNameSelector  */
end_comment

begin_class
DECL|class|FieldMutatingUpdateProcessorFactory
specifier|public
specifier|abstract
class|class
name|FieldMutatingUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
block|{
DECL|class|SelectorParams
specifier|private
specifier|static
class|class
name|SelectorParams
block|{
DECL|field|fieldName
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|fieldName
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
DECL|field|typeName
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|typeName
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
DECL|field|typeClass
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|typeClass
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
DECL|field|fieldRegex
specifier|public
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|fieldRegex
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
block|}
DECL|field|inclusions
specifier|private
name|SelectorParams
name|inclusions
init|=
operator|new
name|SelectorParams
argument_list|()
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Collection
argument_list|<
name|SelectorParams
argument_list|>
name|exclusions
init|=
operator|new
name|ArrayList
argument_list|<
name|SelectorParams
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|selector
specifier|private
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
name|selector
init|=
literal|null
decl_stmt|;
DECL|method|getSelector
specifier|protected
specifier|final
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
name|getSelector
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|selector
condition|)
return|return
name|selector
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
DECL|method|parseSelectorParams
specifier|private
specifier|static
specifier|final
name|SelectorParams
name|parseSelectorParams
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|SelectorParams
name|params
init|=
operator|new
name|SelectorParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|fieldName
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|oneOrMany
argument_list|(
name|args
argument_list|,
literal|"fieldName"
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|typeName
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|oneOrMany
argument_list|(
name|args
argument_list|,
literal|"typeName"
argument_list|)
argument_list|)
expr_stmt|;
comment|// we can compile the patterns now
name|Collection
argument_list|<
name|String
argument_list|>
name|patterns
init|=
name|oneOrMany
argument_list|(
name|args
argument_list|,
literal|"fieldRegex"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|patterns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|params
operator|.
name|fieldRegex
operator|=
operator|new
name|ArrayList
argument_list|<
name|Pattern
argument_list|>
argument_list|(
name|patterns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|patterns
control|)
block|{
try|try
block|{
name|params
operator|.
name|fieldRegex
operator|.
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Invalid 'fieldRegex' pattern: "
operator|+
name|s
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// resolve this into actual Class objects later
name|params
operator|.
name|typeClass
operator|=
name|oneOrMany
argument_list|(
name|args
argument_list|,
literal|"typeClass"
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
comment|/**    * Handles common initialization related to source fields for     * constructoring the FieldNameSelector to be used.    *    * Will error if any unexpected init args are found, so subclasses should    * remove any subclass-specific init args before calling this method.    */
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
name|inclusions
operator|=
name|parseSelectorParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|excList
init|=
name|args
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
literal|"'exclude' init param can not be null"
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
literal|"'exclude' init param must be<lst/>"
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
name|exclusions
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
literal|"Unexpected 'exclude' init sub-param(s): '"
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
comment|// call once per instance
name|args
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
block|}
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
name|selector
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
name|inclusions
operator|.
name|fieldName
argument_list|,
name|inclusions
operator|.
name|typeName
argument_list|,
name|inclusions
operator|.
name|typeClass
argument_list|,
name|inclusions
operator|.
name|fieldRegex
argument_list|,
name|getDefaultSelector
argument_list|(
name|core
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SelectorParams
name|exc
range|:
name|exclusions
control|)
block|{
name|selector
operator|=
name|FieldMutatingUpdateProcessor
operator|.
name|wrap
argument_list|(
name|selector
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
comment|/**    * Defines the default selection behavior when the user has not     * configured any specific criteria for selecting fields. The Default     * implementation matches all fields, and should be overridden by subclasses     * as needed.    *     * @see FieldMutatingUpdateProcessor#SELECT_ALL_FIELDS    */
specifier|protected
name|FieldMutatingUpdateProcessor
operator|.
name|FieldNameSelector
DECL|method|getDefaultSelector
name|getDefaultSelector
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|)
block|{
return|return
name|FieldMutatingUpdateProcessor
operator|.
name|SELECT_ALL_FIELDS
return|;
block|}
comment|/**    * Removes all instance of the key from NamedList, returning the Set of     * Strings that key refered to.  Throws an error if the key didn't refer     * to one or more strings (or arrays of strings)    * @exception SolrException invalid arr/str structure.    */
DECL|method|oneOrMany
specifier|private
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|oneOrMany
parameter_list|(
specifier|final
name|NamedList
name|args
parameter_list|,
specifier|final
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|args
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|err
init|=
literal|"init arg '"
operator|+
name|key
operator|+
literal|"' must be a string "
operator|+
literal|"(ie: 'str'), or an array (ie: 'arr') containing strings; found: "
decl_stmt|;
for|for
control|(
name|Object
name|o
init|=
name|args
operator|.
name|remove
argument_list|(
name|key
argument_list|)
init|;
literal|null
operator|!=
name|o
condition|;
name|o
operator|=
name|args
operator|.
name|remove
argument_list|(
name|key
argument_list|)
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|o
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Collection
condition|)
block|{
for|for
control|(
name|Object
name|item
range|:
operator|(
name|Collection
operator|)
name|o
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|item
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
name|err
operator|+
name|item
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|result
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
comment|// who knows what the hell we have
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
name|err
operator|+
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

