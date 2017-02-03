begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|spell
operator|.
name|Dictionary
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
name|suggest
operator|.
name|InputIterator
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|BytesRefIterator
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
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|TestUtil
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
name|search
operator|.
name|SolrIndexSearcher
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
comment|/**  * Factory for a dictionary with an iterator over bounded-length random strings (with fixed  * weight of 1 and null payloads) that only operates when RandomDictionary.enabledSysProp  * is set; this will be true from the time a RandomDictionary has been constructed until  * its enabledSysProp has been cleared.  */
end_comment

begin_class
DECL|class|RandomTestDictionaryFactory
specifier|public
class|class
name|RandomTestDictionaryFactory
extends|extends
name|DictionaryFactory
block|{
DECL|field|RAND_DICT_MAX_ITEMS
specifier|public
specifier|static
specifier|final
name|String
name|RAND_DICT_MAX_ITEMS
init|=
literal|"randDictMaxItems"
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
DECL|field|DEFAULT_MAX_ITEMS
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_MAX_ITEMS
init|=
literal|100_000_000
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|RandomTestDictionary
name|create
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
comment|// should not happen; implies setParams was not called
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Value of params not set"
argument_list|)
throw|;
block|}
name|String
name|name
init|=
operator|(
name|String
operator|)
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
comment|// Shouldn't happen since this is the component name
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|CommonParams
operator|.
name|NAME
operator|+
literal|" is a mandatory parameter"
argument_list|)
throw|;
block|}
name|long
name|maxItems
init|=
name|DEFAULT_MAX_ITEMS
decl_stmt|;
name|Object
name|specifiedMaxItems
init|=
name|params
operator|.
name|get
argument_list|(
name|RAND_DICT_MAX_ITEMS
argument_list|)
decl_stmt|;
if|if
condition|(
name|specifiedMaxItems
operator|!=
literal|null
condition|)
block|{
name|maxItems
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|specifiedMaxItems
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RandomTestDictionary
argument_list|(
name|name
argument_list|,
name|maxItems
argument_list|)
return|;
block|}
DECL|class|RandomTestDictionary
specifier|public
specifier|static
class|class
name|RandomTestDictionary
implements|implements
name|Dictionary
block|{
DECL|field|SYS_PROP_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|SYS_PROP_PREFIX
init|=
name|RandomTestDictionary
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".enabled."
decl_stmt|;
DECL|field|enabledSysProp
specifier|private
specifier|final
name|String
name|enabledSysProp
decl_stmt|;
comment|// Clear this property to stop the input iterator
DECL|field|maxItems
specifier|private
specifier|final
name|long
name|maxItems
decl_stmt|;
DECL|field|emittedItems
specifier|private
name|long
name|emittedItems
init|=
literal|0L
decl_stmt|;
DECL|method|RandomTestDictionary
name|RandomTestDictionary
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|maxItems
parameter_list|)
block|{
name|enabledSysProp
operator|=
name|getEnabledSysProp
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxItems
operator|=
name|maxItems
expr_stmt|;
synchronized|synchronized
init|(
name|RandomTestDictionary
operator|.
name|class
init|)
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|enabledSysProp
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"System property '"
operator|+
name|enabledSysProp
operator|+
literal|"' is already in use."
argument_list|)
throw|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
name|enabledSysProp
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getEnabledSysProp
specifier|public
specifier|static
name|String
name|getEnabledSysProp
parameter_list|(
name|String
name|suggesterName
parameter_list|)
block|{
return|return
name|SYS_PROP_PREFIX
operator|+
name|suggesterName
return|;
block|}
annotation|@
name|Override
DECL|method|getEntryIterator
specifier|public
name|InputIterator
name|getEntryIterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InputIterator
operator|.
name|InputIteratorWrapper
argument_list|(
operator|new
name|RandomByteRefIterator
argument_list|()
argument_list|)
return|;
block|}
DECL|class|RandomByteRefIterator
specifier|private
class|class
name|RandomByteRefIterator
implements|implements
name|BytesRefIterator
block|{
DECL|field|MAX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LENGTH
init|=
literal|100
decl_stmt|;
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesRef
name|next
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|enabledSysProp
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|emittedItems
operator|<
name|maxItems
condition|)
block|{
operator|++
name|emittedItems
expr_stmt|;
name|next
operator|=
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|LuceneTestCase
operator|.
name|random
argument_list|()
argument_list|,
name|MAX_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|emittedItems
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|enabledSysProp
operator|+
literal|" emitted "
operator|+
name|emittedItems
operator|+
literal|" items."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
name|enabledSysProp
operator|+
literal|" disabled after emitting "
operator|+
name|emittedItems
operator|+
literal|" items."
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|enabledSysProp
argument_list|)
expr_stmt|;
comment|// disable once maxItems has been reached
name|emittedItems
operator|=
literal|0L
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
name|enabledSysProp
operator|+
literal|" invoked when disabled"
argument_list|)
expr_stmt|;
name|emittedItems
operator|=
literal|0L
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

