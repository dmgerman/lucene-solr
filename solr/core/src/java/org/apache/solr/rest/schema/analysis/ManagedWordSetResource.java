begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.rest.schema.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
operator|.
name|analysis
package|;
end_package

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
name|Collections
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
name|Locale
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|rest
operator|.
name|BaseSolrResource
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
name|rest
operator|.
name|ManagedResource
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
name|rest
operator|.
name|ManagedResourceStorage
operator|.
name|StorageIO
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
comment|/**  * ManagedResource implementation for managing a set of words using the REST API;  * useful for managing stop words and/or protected words for analysis components   * like the KeywordMarkerFilter.  */
end_comment

begin_class
DECL|class|ManagedWordSetResource
specifier|public
class|class
name|ManagedWordSetResource
extends|extends
name|ManagedResource
implements|implements
name|ManagedResource
operator|.
name|ChildResourceSupport
block|{
DECL|field|WORD_SET_JSON_FIELD
specifier|public
specifier|static
specifier|final
name|String
name|WORD_SET_JSON_FIELD
init|=
literal|"wordSet"
decl_stmt|;
DECL|field|IGNORE_CASE_INIT_ARG
specifier|public
specifier|static
specifier|final
name|String
name|IGNORE_CASE_INIT_ARG
init|=
literal|"ignoreCase"
decl_stmt|;
DECL|field|managedWords
specifier|private
name|SortedSet
argument_list|<
name|String
argument_list|>
name|managedWords
init|=
literal|null
decl_stmt|;
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
DECL|method|ManagedWordSetResource
specifier|public
name|ManagedWordSetResource
parameter_list|(
name|String
name|resourceId
parameter_list|,
name|SolrResourceLoader
name|loader
parameter_list|,
name|StorageIO
name|storageIO
parameter_list|)
throws|throws
name|SolrException
block|{
name|super
argument_list|(
name|resourceId
argument_list|,
name|loader
argument_list|,
name|storageIO
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the set of words in this managed word set.    */
DECL|method|getWordSet
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getWordSet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|managedWords
argument_list|)
return|;
block|}
comment|/**    * Returns the boolean value of the {@link #IGNORE_CASE_INIT_ARG} init arg,    * or the default value (false) if it has not been specified    */
DECL|method|getIgnoreCase
specifier|public
name|boolean
name|getIgnoreCase
parameter_list|()
block|{
return|return
name|getIgnoreCase
argument_list|(
name|managedInitArgs
argument_list|)
return|;
block|}
comment|/**    * Returns the boolean value of the {@link #IGNORE_CASE_INIT_ARG} init arg,    * or the default value (false) if it has not been specified    */
DECL|method|getIgnoreCase
specifier|public
name|boolean
name|getIgnoreCase
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|initArgs
parameter_list|)
block|{
name|Boolean
name|ignoreCase
init|=
name|initArgs
operator|.
name|getBooleanArg
argument_list|(
name|IGNORE_CASE_INIT_ARG
argument_list|)
decl_stmt|;
comment|// ignoreCase = false by default
return|return
literal|null
operator|==
name|ignoreCase
condition|?
literal|false
else|:
name|ignoreCase
return|;
block|}
comment|/**    * Invoked when loading data from storage to initialize the     * list of words managed by this instance. A load of the    * data can happen many times throughout the life cycle of this    * object.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|onManagedDataLoadedFromStorage
specifier|protected
name|void
name|onManagedDataLoadedFromStorage
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|initArgs
parameter_list|,
name|Object
name|data
parameter_list|)
throws|throws
name|SolrException
block|{
comment|// the default behavior is to not ignore case,
name|boolean
name|ignoreCase
init|=
name|getIgnoreCase
argument_list|(
name|initArgs
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|initArgs
operator|.
name|get
argument_list|(
name|IGNORE_CASE_INIT_ARG
argument_list|)
condition|)
block|{
comment|// Explicitly include the default value of ignoreCase
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|initArgs
operator|)
operator|.
name|add
argument_list|(
name|IGNORE_CASE_INIT_ARG
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|managedWords
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wordList
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|data
decl_stmt|;
if|if
condition|(
name|ignoreCase
condition|)
block|{
comment|// if we're ignoring case, just lowercase all terms as we add them
for|for
control|(
name|String
name|word
range|:
name|wordList
control|)
block|{
name|managedWords
operator|.
name|add
argument_list|(
name|word
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|managedWords
operator|.
name|addAll
argument_list|(
name|wordList
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|storeManagedData
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// stores an empty word set
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Loaded "
operator|+
name|managedWords
operator|.
name|size
argument_list|()
operator|+
literal|" words for "
operator|+
name|getResourceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Implements the GET request to provide the list of words to the client.    * Alternatively, if a specific word is requested, then it is returned    * or a 404 is raised, indicating that the requested word does not exist.    */
annotation|@
name|Override
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
block|{
name|SolrQueryResponse
name|response
init|=
name|endpoint
operator|.
name|getSolrResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|childId
operator|!=
literal|null
condition|)
block|{
comment|// downcase arg if we're configured to ignoreCase
name|String
name|key
init|=
name|getIgnoreCase
argument_list|()
condition|?
name|childId
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
else|:
name|childId
decl_stmt|;
if|if
condition|(
operator|!
name|managedWords
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s not found in %s"
argument_list|,
name|childId
argument_list|,
name|getResourceId
argument_list|()
argument_list|)
argument_list|)
throw|;
name|response
operator|.
name|add
argument_list|(
name|childId
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|add
argument_list|(
name|WORD_SET_JSON_FIELD
argument_list|,
name|buildMapToStore
argument_list|(
name|managedWords
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Deletes words managed by this resource.    */
annotation|@
name|Override
DECL|method|doDeleteChild
specifier|public
specifier|synchronized
name|void
name|doDeleteChild
parameter_list|(
name|BaseSolrResource
name|endpoint
parameter_list|,
name|String
name|childId
parameter_list|)
block|{
comment|// downcase arg if we're configured to ignoreCase
name|String
name|key
init|=
name|getIgnoreCase
argument_list|()
condition|?
name|childId
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
else|:
name|childId
decl_stmt|;
if|if
condition|(
operator|!
name|managedWords
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s not found in %s"
argument_list|,
name|childId
argument_list|,
name|getResourceId
argument_list|()
argument_list|)
argument_list|)
throw|;
name|managedWords
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|storeManagedData
argument_list|(
name|managedWords
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Removed word: {}"
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Applies updates to the word set being managed by this resource.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|applyUpdatesToManagedData
specifier|protected
name|Object
name|applyUpdatesToManagedData
parameter_list|(
name|Object
name|updates
parameter_list|)
block|{
name|boolean
name|madeChanges
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|words
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|updates
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Applying updates: "
operator|+
name|words
argument_list|)
expr_stmt|;
name|boolean
name|ignoreCase
init|=
name|getIgnoreCase
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|words
control|)
block|{
if|if
condition|(
name|ignoreCase
condition|)
name|word
operator|=
name|word
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
if|if
condition|(
name|managedWords
operator|.
name|add
argument_list|(
name|word
argument_list|)
condition|)
block|{
name|madeChanges
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Added word: {}"
argument_list|,
name|word
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|madeChanges
condition|?
name|managedWords
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|updateInitArgs
specifier|protected
name|boolean
name|updateInitArgs
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|updatedArgs
parameter_list|)
block|{
if|if
condition|(
name|updatedArgs
operator|==
literal|null
operator|||
name|updatedArgs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|currentIgnoreCase
init|=
name|getIgnoreCase
argument_list|(
name|managedInitArgs
argument_list|)
decl_stmt|;
name|boolean
name|updatedIgnoreCase
init|=
name|getIgnoreCase
argument_list|(
name|updatedArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentIgnoreCase
operator|==
literal|true
operator|&&
name|updatedIgnoreCase
operator|==
literal|false
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
literal|"Changing a managed word set's ignoreCase arg from true to false is not permitted."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|currentIgnoreCase
operator|==
literal|false
operator|&&
name|updatedIgnoreCase
operator|==
literal|true
condition|)
block|{
comment|// rebuild the word set on policy change from case-sensitive to case-insensitive
name|SortedSet
argument_list|<
name|String
argument_list|>
name|updatedWords
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|word
range|:
name|managedWords
control|)
block|{
name|updatedWords
operator|.
name|add
argument_list|(
name|word
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|managedWords
operator|=
name|updatedWords
expr_stmt|;
block|}
comment|// otherwise currentIgnoreCase == updatedIgnoreCase: nothing to do
return|return
name|super
operator|.
name|updateInitArgs
argument_list|(
name|updatedArgs
argument_list|)
return|;
block|}
block|}
end_class

end_unit

