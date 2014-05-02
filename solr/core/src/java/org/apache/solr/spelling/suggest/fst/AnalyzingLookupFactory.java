begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest.fst
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
operator|.
name|fst
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|Lookup
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
name|analyzing
operator|.
name|AnalyzingSuggester
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
name|spelling
operator|.
name|suggest
operator|.
name|LookupFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link AnalyzingSuggester}  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AnalyzingLookupFactory
specifier|public
class|class
name|AnalyzingLookupFactory
extends|extends
name|LookupFactory
block|{
comment|/**    * If<code>true</code>, exact suggestions are returned first, even if they are prefixes    * of other strings in the automaton (possibly with larger weights).     */
DECL|field|EXACT_MATCH_FIRST
specifier|public
specifier|static
specifier|final
name|String
name|EXACT_MATCH_FIRST
init|=
literal|"exactMatchFirst"
decl_stmt|;
comment|/**    * If<code>true</code>, then a separator between tokens is preserved. This means that    * suggestions are sensitive to tokenization (e.g. baseball is different from base ball).    */
DECL|field|PRESERVE_SEP
specifier|public
specifier|static
specifier|final
name|String
name|PRESERVE_SEP
init|=
literal|"preserveSep"
decl_stmt|;
comment|/**    * When multiple suggestions collide to the same analyzed form, this is the limit of    * how many unique surface forms we keep.    */
DECL|field|MAX_SURFACE_FORMS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SURFACE_FORMS
init|=
literal|"maxSurfaceFormsPerAnalyzedForm"
decl_stmt|;
comment|/**    * When building the FST ("index-time"), we add each path through the tokenstream graph    * as an individual entry. This places an upper-bound on how many expansions will be added    * for a single suggestion.    */
DECL|field|MAX_EXPANSIONS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_EXPANSIONS
init|=
literal|"maxGraphExpansions"
decl_stmt|;
comment|// confusingly: the queryAnalyzerFieldType parameter is something totally different, this
comment|// is solr's "analysis" of the queries before they even reach the suggester (really makes
comment|// little sense for suggest at all, only for spellcheck). So we pick different names.
comment|/**    * The analyzer used at "query-time" and "build-time" to analyze suggestions.    */
DECL|field|QUERY_ANALYZER
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_ANALYZER
init|=
literal|"suggestAnalyzerFieldType"
decl_stmt|;
comment|/**    * Whether position holes should appear in the automaton.    */
DECL|field|PRESERVE_POSITION_INCREMENTS
specifier|public
specifier|static
specifier|final
name|String
name|PRESERVE_POSITION_INCREMENTS
init|=
literal|"preservePositionIncrements"
decl_stmt|;
comment|/**    * File name for the automaton.    *     */
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"wfsta.bin"
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Lookup
name|create
parameter_list|(
name|NamedList
name|params
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
comment|// mandatory parameter
name|Object
name|fieldTypeName
init|=
name|params
operator|.
name|get
argument_list|(
name|QUERY_ANALYZER
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldTypeName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error in configuration: "
operator|+
name|QUERY_ANALYZER
operator|+
literal|" parameter is mandatory"
argument_list|)
throw|;
block|}
name|FieldType
name|ft
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldTypeByName
argument_list|(
name|fieldTypeName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error in configuration: "
operator|+
name|fieldTypeName
operator|.
name|toString
argument_list|()
operator|+
literal|" is not defined in the schema"
argument_list|)
throw|;
block|}
name|Analyzer
name|indexAnalyzer
init|=
name|ft
operator|.
name|getIndexAnalyzer
argument_list|()
decl_stmt|;
name|Analyzer
name|queryAnalyzer
init|=
name|ft
operator|.
name|getQueryAnalyzer
argument_list|()
decl_stmt|;
comment|// optional parameters
name|boolean
name|exactMatchFirst
init|=
name|params
operator|.
name|get
argument_list|(
name|EXACT_MATCH_FIRST
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|EXACT_MATCH_FIRST
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|true
decl_stmt|;
name|boolean
name|preserveSep
init|=
name|params
operator|.
name|get
argument_list|(
name|PRESERVE_SEP
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|PRESERVE_SEP
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|true
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|exactMatchFirst
condition|)
block|{
name|flags
operator||=
name|AnalyzingSuggester
operator|.
name|EXACT_FIRST
expr_stmt|;
block|}
if|if
condition|(
name|preserveSep
condition|)
block|{
name|flags
operator||=
name|AnalyzingSuggester
operator|.
name|PRESERVE_SEP
expr_stmt|;
block|}
name|int
name|maxSurfaceFormsPerAnalyzedForm
init|=
name|params
operator|.
name|get
argument_list|(
name|MAX_SURFACE_FORMS
argument_list|)
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|MAX_SURFACE_FORMS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|256
decl_stmt|;
name|int
name|maxGraphExpansions
init|=
name|params
operator|.
name|get
argument_list|(
name|MAX_EXPANSIONS
argument_list|)
operator|!=
literal|null
condition|?
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|MAX_EXPANSIONS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
operator|-
literal|1
decl_stmt|;
name|boolean
name|preservePositionIncrements
init|=
name|params
operator|.
name|get
argument_list|(
name|PRESERVE_POSITION_INCREMENTS
argument_list|)
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|PRESERVE_POSITION_INCREMENTS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|false
decl_stmt|;
return|return
operator|new
name|AnalyzingSuggester
argument_list|(
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|flags
argument_list|,
name|maxSurfaceFormsPerAnalyzedForm
argument_list|,
name|maxGraphExpansions
argument_list|,
name|preservePositionIncrements
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|storeFileName
specifier|public
name|String
name|storeFileName
parameter_list|()
block|{
return|return
name|FILENAME
return|;
block|}
block|}
end_class

end_unit

