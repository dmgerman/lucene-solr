begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|analysis
operator|.
name|core
operator|.
name|KeywordAnalyzer
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
name|analysis
operator|.
name|util
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
name|lucene
operator|.
name|util
operator|.
name|Version
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
name|analysis
operator|.
name|KeywordTokenizerFactory
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
name|analysis
operator|.
name|TokenizerChain
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
name|util
operator|.
name|DOMUtil
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
name|Config
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
name|AbstractPluginLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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

begin_class
DECL|class|FieldTypePluginLoader
specifier|public
specifier|final
class|class
name|FieldTypePluginLoader
extends|extends
name|AbstractPluginLoader
argument_list|<
name|FieldType
argument_list|>
block|{
DECL|field|LUCENE_MATCH_VERSION_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|LUCENE_MATCH_VERSION_PARAM
init|=
name|IndexSchema
operator|.
name|LUCENE_MATCH_VERSION_PARAM
decl_stmt|;
DECL|field|log
specifier|protected
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FieldTypePluginLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|xpath
specifier|private
specifier|final
name|XPath
name|xpath
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newXPath
argument_list|()
decl_stmt|;
comment|/**    * @param schema The schema that will be used to initialize the FieldTypes    * @param fieldTypes All FieldTypes that are instantiated by     *        this Plugin Loader will be added to this Map    * @param schemaAware Any SchemaAware objects that are instantiated by     *        this Plugin Loader will be added to this collection.    */
DECL|method|FieldTypePluginLoader
specifier|public
name|FieldTypePluginLoader
parameter_list|(
specifier|final
name|IndexSchema
name|schema
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|fieldTypes
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|SchemaAware
argument_list|>
name|schemaAware
parameter_list|)
block|{
name|super
argument_list|(
literal|"[schema.xml] fieldType"
argument_list|,
name|FieldType
operator|.
name|class
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|fieldTypes
operator|=
name|fieldTypes
expr_stmt|;
name|this
operator|.
name|schemaAware
operator|=
name|schemaAware
expr_stmt|;
block|}
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|fieldTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|fieldTypes
decl_stmt|;
DECL|field|schemaAware
specifier|private
specifier|final
name|Collection
argument_list|<
name|SchemaAware
argument_list|>
name|schemaAware
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|protected
name|FieldType
name|create
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|className
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|FieldType
name|ft
init|=
name|loader
operator|.
name|newInstance
argument_list|(
name|className
argument_list|,
name|FieldType
operator|.
name|class
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setTypeName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|String
name|expression
init|=
literal|"./analyzer[@type='query']"
decl_stmt|;
name|Node
name|anode
init|=
operator|(
name|Node
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|expression
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODE
argument_list|)
decl_stmt|;
name|Analyzer
name|queryAnalyzer
init|=
name|readAnalyzer
argument_list|(
name|anode
argument_list|)
decl_stmt|;
name|expression
operator|=
literal|"./analyzer[@type='multiterm']"
expr_stmt|;
name|anode
operator|=
operator|(
name|Node
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|expression
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODE
argument_list|)
expr_stmt|;
name|Analyzer
name|multiAnalyzer
init|=
name|readAnalyzer
argument_list|(
name|anode
argument_list|)
decl_stmt|;
comment|// An analyzer without a type specified, or with type="index"
name|expression
operator|=
literal|"./analyzer[not(@type)] | ./analyzer[@type='index']"
expr_stmt|;
name|anode
operator|=
operator|(
name|Node
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|expression
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODE
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
name|readAnalyzer
argument_list|(
name|anode
argument_list|)
decl_stmt|;
comment|// a custom similarity[Factory]
name|expression
operator|=
literal|"./similarity"
expr_stmt|;
name|anode
operator|=
operator|(
name|Node
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|expression
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODE
argument_list|)
expr_stmt|;
name|SimilarityFactory
name|simFactory
init|=
name|IndexSchema
operator|.
name|readSimilarity
argument_list|(
name|loader
argument_list|,
name|anode
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryAnalyzer
operator|==
literal|null
condition|)
name|queryAnalyzer
operator|=
name|analyzer
expr_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
name|analyzer
operator|=
name|queryAnalyzer
expr_stmt|;
if|if
condition|(
name|multiAnalyzer
operator|==
literal|null
condition|)
block|{
name|multiAnalyzer
operator|=
name|constructMultiTermAnalyzer
argument_list|(
name|queryAnalyzer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
name|ft
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setQueryAnalyzer
argument_list|(
name|queryAnalyzer
argument_list|)
expr_stmt|;
if|if
condition|(
name|ft
operator|instanceof
name|TextField
condition|)
operator|(
operator|(
name|TextField
operator|)
name|ft
operator|)
operator|.
name|setMultiTermAnalyzer
argument_list|(
name|multiAnalyzer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|simFactory
operator|!=
literal|null
condition|)
block|{
name|ft
operator|.
name|setSimilarity
argument_list|(
name|simFactory
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ft
operator|instanceof
name|SchemaAware
condition|)
block|{
name|schemaAware
operator|.
name|add
argument_list|(
operator|(
name|SchemaAware
operator|)
name|ft
argument_list|)
expr_stmt|;
block|}
return|return
name|ft
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|FieldType
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|DOMUtil
operator|.
name|toMapExcept
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|,
literal|"name"
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|plugin
operator|.
name|setArgs
argument_list|(
name|schema
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|protected
name|FieldType
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|plugin
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"fieldtype defined: "
operator|+
name|plugin
argument_list|)
expr_stmt|;
return|return
name|fieldTypes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|plugin
argument_list|)
return|;
block|}
comment|// The point here is that, if no multiterm analyzer was specified in the schema file, do one of several things:
comment|// 1> If legacyMultiTerm == false, assemble a new analyzer composed of all of the charfilters,
comment|//    lowercase filters and asciifoldingfilter.
comment|// 2> If legacyMultiTerm == true just construct the analyzer from a KeywordTokenizer. That should mimic current behavior.
comment|//    Do the same if they've specified that the old behavior is required (legacyMultiTerm="true")
DECL|method|constructMultiTermAnalyzer
specifier|private
name|Analyzer
name|constructMultiTermAnalyzer
parameter_list|(
name|Analyzer
name|queryAnalyzer
parameter_list|)
block|{
if|if
condition|(
name|queryAnalyzer
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
operator|!
operator|(
name|queryAnalyzer
operator|instanceof
name|TokenizerChain
operator|)
condition|)
block|{
return|return
operator|new
name|KeywordAnalyzer
argument_list|()
return|;
block|}
name|TokenizerChain
name|tc
init|=
operator|(
name|TokenizerChain
operator|)
name|queryAnalyzer
decl_stmt|;
name|MultiTermChainBuilder
name|builder
init|=
operator|new
name|MultiTermChainBuilder
argument_list|()
decl_stmt|;
name|CharFilterFactory
index|[]
name|charFactories
init|=
name|tc
operator|.
name|getCharFilterFactories
argument_list|()
decl_stmt|;
if|if
condition|(
name|charFactories
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CharFilterFactory
name|fact
range|:
name|charFactories
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fact
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|add
argument_list|(
name|tc
operator|.
name|getTokenizerFactory
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TokenFilterFactory
name|fact
range|:
name|tc
operator|.
name|getTokenFilterFactories
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fact
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|class|MultiTermChainBuilder
specifier|private
specifier|static
class|class
name|MultiTermChainBuilder
block|{
DECL|field|keyFactory
specifier|static
specifier|final
name|KeywordTokenizerFactory
name|keyFactory
decl_stmt|;
static|static
block|{
name|keyFactory
operator|=
operator|new
name|KeywordTokenizerFactory
argument_list|()
expr_stmt|;
name|keyFactory
operator|.
name|init
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|charFilters
name|ArrayList
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilters
init|=
literal|null
decl_stmt|;
DECL|field|filters
name|ArrayList
argument_list|<
name|TokenFilterFactory
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|tokenizer
name|TokenizerFactory
name|tokenizer
init|=
name|keyFactory
decl_stmt|;
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Object
name|current
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|current
operator|instanceof
name|MultiTermAwareComponent
operator|)
condition|)
return|return;
name|AbstractAnalysisFactory
name|newComponent
init|=
operator|(
operator|(
name|MultiTermAwareComponent
operator|)
name|current
operator|)
operator|.
name|getMultiTermComponent
argument_list|()
decl_stmt|;
if|if
condition|(
name|newComponent
operator|instanceof
name|TokenFilterFactory
condition|)
block|{
if|if
condition|(
name|filters
operator|==
literal|null
condition|)
block|{
name|filters
operator|=
operator|new
name|ArrayList
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|filters
operator|.
name|add
argument_list|(
operator|(
name|TokenFilterFactory
operator|)
name|newComponent
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newComponent
operator|instanceof
name|TokenizerFactory
condition|)
block|{
name|tokenizer
operator|=
operator|(
name|TokenizerFactory
operator|)
name|newComponent
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newComponent
operator|instanceof
name|CharFilterFactory
condition|)
block|{
if|if
condition|(
name|charFilters
operator|==
literal|null
condition|)
block|{
name|charFilters
operator|=
operator|new
name|ArrayList
argument_list|<
name|CharFilterFactory
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|charFilters
operator|.
name|add
argument_list|(
operator|(
name|CharFilterFactory
operator|)
name|newComponent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unknown analysis component from MultiTermAwareComponent: "
operator|+
name|newComponent
argument_list|)
throw|;
block|}
block|}
DECL|method|build
specifier|public
name|TokenizerChain
name|build
parameter_list|()
block|{
name|CharFilterFactory
index|[]
name|charFilterArr
init|=
name|charFilters
operator|==
literal|null
condition|?
literal|null
else|:
name|charFilters
operator|.
name|toArray
argument_list|(
operator|new
name|CharFilterFactory
index|[
name|charFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|TokenFilterFactory
index|[]
name|filterArr
init|=
name|filters
operator|==
literal|null
condition|?
operator|new
name|TokenFilterFactory
index|[
literal|0
index|]
else|:
name|filters
operator|.
name|toArray
argument_list|(
operator|new
name|TokenFilterFactory
index|[
name|filters
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenizerChain
argument_list|(
name|charFilterArr
argument_list|,
name|tokenizer
argument_list|,
name|filterArr
argument_list|)
return|;
block|}
block|}
comment|//
comment|//<analyzer><tokenizer class="...."/><tokenizer class="...." arg="....">
comment|//
comment|//
DECL|method|readAnalyzer
specifier|private
name|Analyzer
name|readAnalyzer
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
specifier|final
name|SolrResourceLoader
name|loader
init|=
name|schema
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
comment|// parent node used to be passed in as "fieldtype"
comment|// if (!fieldtype.hasChildNodes()) return null;
comment|// Node node = DOMUtil.getChild(fieldtype,"analyzer");
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|NamedNodeMap
name|attrs
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|analyzerName
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|attrs
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzerName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// No need to be core-aware as Analyzers are not in the core-aware list
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|clazz
init|=
name|loader
operator|.
name|findClass
argument_list|(
name|analyzerName
argument_list|,
name|Analyzer
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
comment|// first try to use a ctor with version parameter
comment|// (needed for many new Analyzers that have no default one anymore)
name|Constructor
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|cnstr
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|matchVersionStr
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|attrs
argument_list|,
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
decl_stmt|;
specifier|final
name|Version
name|luceneMatchVersion
init|=
operator|(
name|matchVersionStr
operator|==
literal|null
operator|)
condition|?
name|schema
operator|.
name|getDefaultLuceneMatchVersion
argument_list|()
else|:
name|Config
operator|.
name|parseLuceneVersionString
argument_list|(
name|matchVersionStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|luceneMatchVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Configuration Error: Analyzer '"
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|"' needs a 'luceneMatchVersion' parameter"
argument_list|)
throw|;
block|}
return|return
name|cnstr
operator|.
name|newInstance
argument_list|(
name|luceneMatchVersion
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
comment|// otherwise use default ctor
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot load analyzer: "
operator|+
name|analyzerName
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Cannot load analyzer: "
operator|+
name|analyzerName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// Load the CharFilters
specifier|final
name|ArrayList
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilters
init|=
operator|new
name|ArrayList
argument_list|<
name|CharFilterFactory
argument_list|>
argument_list|()
decl_stmt|;
name|AbstractPluginLoader
argument_list|<
name|CharFilterFactory
argument_list|>
name|charFilterLoader
init|=
operator|new
name|AbstractPluginLoader
argument_list|<
name|CharFilterFactory
argument_list|>
argument_list|(
literal|"[schema.xml] analyzer/charFilter"
argument_list|,
name|CharFilterFactory
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|CharFilterFactory
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|plugin
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|DOMUtil
operator|.
name|toMapExcept
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|String
name|configuredVersion
init|=
name|params
operator|.
name|remove
argument_list|(
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
decl_stmt|;
name|plugin
operator|.
name|setLuceneMatchVersion
argument_list|(
name|parseConfiguredVersion
argument_list|(
name|configuredVersion
argument_list|,
name|plugin
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|charFilters
operator|.
name|add
argument_list|(
name|plugin
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|CharFilterFactory
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|CharFilterFactory
name|plugin
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// used for map registration
block|}
block|}
decl_stmt|;
name|charFilterLoader
operator|.
name|load
argument_list|(
name|loader
argument_list|,
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"./charFilter"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
argument_list|)
expr_stmt|;
comment|// Load the Tokenizer
comment|// Although an analyzer only allows a single Tokenizer, we load a list to make sure
comment|// the configuration is ok
specifier|final
name|ArrayList
argument_list|<
name|TokenizerFactory
argument_list|>
name|tokenizers
init|=
operator|new
name|ArrayList
argument_list|<
name|TokenizerFactory
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AbstractPluginLoader
argument_list|<
name|TokenizerFactory
argument_list|>
name|tokenizerLoader
init|=
operator|new
name|AbstractPluginLoader
argument_list|<
name|TokenizerFactory
argument_list|>
argument_list|(
literal|"[schema.xml] analyzer/tokenizer"
argument_list|,
name|TokenizerFactory
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|TokenizerFactory
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|tokenizers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"The schema defines multiple tokenizers for: "
operator|+
name|node
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|DOMUtil
operator|.
name|toMapExcept
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|String
name|configuredVersion
init|=
name|params
operator|.
name|remove
argument_list|(
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
decl_stmt|;
name|plugin
operator|.
name|setLuceneMatchVersion
argument_list|(
name|parseConfiguredVersion
argument_list|(
name|configuredVersion
argument_list|,
name|plugin
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|tokenizers
operator|.
name|add
argument_list|(
name|plugin
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|TokenizerFactory
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|TokenizerFactory
name|plugin
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// used for map registration
block|}
block|}
decl_stmt|;
name|tokenizerLoader
operator|.
name|load
argument_list|(
name|loader
argument_list|,
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"./tokenizer"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure something was loaded
if|if
condition|(
name|tokenizers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"analyzer without class or tokenizer& filter list"
argument_list|)
throw|;
block|}
comment|// Load the Filters
specifier|final
name|ArrayList
argument_list|<
name|TokenFilterFactory
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|()
decl_stmt|;
name|AbstractPluginLoader
argument_list|<
name|TokenFilterFactory
argument_list|>
name|filterLoader
init|=
operator|new
name|AbstractPluginLoader
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|(
literal|"[schema.xml] analyzer/filter"
argument_list|,
name|TokenFilterFactory
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|TokenFilterFactory
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|plugin
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|DOMUtil
operator|.
name|toMapExcept
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
argument_list|,
literal|"class"
argument_list|)
decl_stmt|;
name|String
name|configuredVersion
init|=
name|params
operator|.
name|remove
argument_list|(
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
decl_stmt|;
name|plugin
operator|.
name|setLuceneMatchVersion
argument_list|(
name|parseConfiguredVersion
argument_list|(
name|configuredVersion
argument_list|,
name|plugin
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|plugin
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|TokenFilterFactory
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|TokenFilterFactory
name|plugin
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
comment|// used for map registration
block|}
block|}
decl_stmt|;
name|filterLoader
operator|.
name|load
argument_list|(
name|loader
argument_list|,
operator|(
name|NodeList
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
literal|"./filter"
argument_list|,
name|node
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenizerChain
argument_list|(
name|charFilters
operator|.
name|toArray
argument_list|(
operator|new
name|CharFilterFactory
index|[
name|charFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|tokenizers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|filters
operator|.
name|toArray
argument_list|(
operator|new
name|TokenFilterFactory
index|[
name|filters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseConfiguredVersion
specifier|private
name|Version
name|parseConfiguredVersion
parameter_list|(
name|String
name|configuredVersion
parameter_list|,
name|String
name|pluginClassName
parameter_list|)
block|{
name|Version
name|version
init|=
operator|(
name|configuredVersion
operator|!=
literal|null
operator|)
condition|?
name|Config
operator|.
name|parseLuceneVersionString
argument_list|(
name|configuredVersion
argument_list|)
else|:
name|schema
operator|.
name|getDefaultLuceneMatchVersion
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|pluginClassName
operator|+
literal|" is using deprecated "
operator|+
name|version
operator|+
literal|" emulation. You should at some point declare and reindex to at least 4.0, because "
operator|+
literal|"3.x emulation is deprecated and will be removed in 5.0"
argument_list|)
expr_stmt|;
block|}
return|return
name|version
return|;
block|}
block|}
end_class

end_unit

