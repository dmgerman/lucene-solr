begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
package|;
end_package

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
name|LinkedHashMap
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
name|TimeZone
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
name|document
operator|.
name|DateTools
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
name|document
operator|.
name|DateTools
operator|.
name|Resolution
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|ConfigurationKey
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|StandardQueryParser
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|processors
operator|.
name|StandardQueryNodeProcessorPipeline
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
name|MultiTermQuery
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
name|MultiTermQuery
operator|.
name|RewriteMethod
import|;
end_import

begin_comment
comment|/**  * This query configuration handler is used for almost every processor defined  * in the {@link StandardQueryNodeProcessorPipeline} processor pipeline. It holds  * configuration methods that reproduce the configuration methods that could be set on the old  * lucene 2.4 QueryParser class.  *   * @see StandardQueryNodeProcessorPipeline  */
end_comment

begin_class
DECL|class|StandardQueryConfigHandler
specifier|public
class|class
name|StandardQueryConfigHandler
extends|extends
name|QueryConfigHandler
block|{
comment|/**    * Class holding keys for StandardQueryNodeProcessorPipeline options.    */
DECL|class|ConfigurationKeys
specifier|final
specifier|public
specifier|static
class|class
name|ConfigurationKeys
block|{
comment|/**      * Key used to set whether position increments is enabled      *       * @see StandardQueryParser#setEnablePositionIncrements(boolean)      * @see StandardQueryParser#getEnablePositionIncrements()      */
DECL|field|ENABLE_POSITION_INCREMENTS
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Boolean
argument_list|>
name|ENABLE_POSITION_INCREMENTS
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set whether expanded terms should be lower-cased      *       * @see StandardQueryParser#setLowercaseExpandedTerms(boolean)      * @see StandardQueryParser#getLowercaseExpandedTerms()      */
DECL|field|LOWERCASE_EXPANDED_TERMS
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Boolean
argument_list|>
name|LOWERCASE_EXPANDED_TERMS
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set whether leading wildcards are supported      *       * @see StandardQueryParser#setAllowLeadingWildcard(boolean)      * @see StandardQueryParser#getAllowLeadingWildcard()      */
DECL|field|ALLOW_LEADING_WILDCARD
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Boolean
argument_list|>
name|ALLOW_LEADING_WILDCARD
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the {@link Analyzer} used for terms found in the query      *       * @see StandardQueryParser#setAnalyzer(Analyzer)      * @see StandardQueryParser#getAnalyzer()      */
DECL|field|ANALYZER
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Analyzer
argument_list|>
name|ANALYZER
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the default boolean operator      *       * @see StandardQueryParser#setDefaultOperator(org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler.Operator)      * @see StandardQueryParser#getDefaultOperator()      */
DECL|field|DEFAULT_OPERATOR
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Operator
argument_list|>
name|DEFAULT_OPERATOR
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the default phrase slop      *       * @see StandardQueryParser#setPhraseSlop(int)      * @see StandardQueryParser#getPhraseSlop()      */
DECL|field|PHRASE_SLOP
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Integer
argument_list|>
name|PHRASE_SLOP
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the {@link Locale} used when parsing the query      *       * @see StandardQueryParser#setLocale(Locale)      * @see StandardQueryParser#getLocale()      */
DECL|field|LOCALE
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Locale
argument_list|>
name|LOCALE
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|TIMEZONE
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|TimeZone
argument_list|>
name|TIMEZONE
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the {@link RewriteMethod} used when creating queries      *       * @see StandardQueryParser#setMultiTermRewriteMethod(org.apache.lucene.search.MultiTermQuery.RewriteMethod)      * @see StandardQueryParser#getMultiTermRewriteMethod()      */
DECL|field|MULTI_TERM_REWRITE_METHOD
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|MultiTermQuery
operator|.
name|RewriteMethod
argument_list|>
name|MULTI_TERM_REWRITE_METHOD
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the fields a query should be expanded to when the field      * is<code>null</code>      *       * @see StandardQueryParser#setMultiFields(CharSequence[])      * @see StandardQueryParser#getMultiFields()      */
DECL|field|MULTI_FIELDS
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|CharSequence
index|[]
argument_list|>
name|MULTI_FIELDS
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set a field to boost map that is used to set the boost for each field      *       * @see StandardQueryParser#setFieldsBoost(Map)      * @see StandardQueryParser#getFieldsBoost()      */
DECL|field|FIELD_BOOST_MAP
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|>
name|FIELD_BOOST_MAP
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set a field to {@link Resolution} map that is used      * to normalize each date field value.      *       * @see StandardQueryParser#setDateResolutionMap(Map)      * @see StandardQueryParser#getDateResolutionMap()      */
DECL|field|FIELD_DATE_RESOLUTION_MAP
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|DateTools
operator|.
name|Resolution
argument_list|>
argument_list|>
name|FIELD_DATE_RESOLUTION_MAP
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the {@link FuzzyConfig} used to create fuzzy queries.      *       * @see StandardQueryParser#setFuzzyMinSim(float)      * @see StandardQueryParser#setFuzzyPrefixLength(int)      * @see StandardQueryParser#getFuzzyMinSim()      * @see StandardQueryParser#getFuzzyPrefixLength()      */
DECL|field|FUZZY_CONFIG
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|FuzzyConfig
argument_list|>
name|FUZZY_CONFIG
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set default {@link Resolution}.      *       * @see StandardQueryParser#setDateResolution(org.apache.lucene.document.DateTools.Resolution)      * @see StandardQueryParser#getDateResolution()      */
DECL|field|DATE_RESOLUTION
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|DateTools
operator|.
name|Resolution
argument_list|>
name|DATE_RESOLUTION
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the boost value in {@link FieldConfig} objects.      *       * @see StandardQueryParser#setFieldsBoost(Map)      * @see StandardQueryParser#getFieldsBoost()      */
DECL|field|BOOST
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Float
argument_list|>
name|BOOST
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set a field to its {@link NumericConfig}.      *       * @see StandardQueryParser#setNumericConfigMap(Map)      * @see StandardQueryParser#getNumericConfigMap()      */
DECL|field|NUMERIC_CONFIG
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|NumericConfig
argument_list|>
name|NUMERIC_CONFIG
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|/**      * Key used to set the {@link NumericConfig} in {@link FieldConfig} for numeric fields.      *       * @see StandardQueryParser#setNumericConfigMap(Map)      * @see StandardQueryParser#getNumericConfigMap()      */
DECL|field|NUMERIC_CONFIG_MAP
specifier|final
specifier|public
specifier|static
name|ConfigurationKey
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|NumericConfig
argument_list|>
argument_list|>
name|NUMERIC_CONFIG_MAP
init|=
name|ConfigurationKey
operator|.
name|newInstance
argument_list|()
decl_stmt|;
block|}
comment|/**    * Boolean Operator: AND or OR    */
DECL|enum|Operator
specifier|public
specifier|static
enum|enum
name|Operator
block|{
DECL|enum constant|AND
DECL|enum constant|OR
name|AND
block|,
name|OR
block|;   }
DECL|method|StandardQueryConfigHandler
specifier|public
name|StandardQueryConfigHandler
parameter_list|()
block|{
comment|// Add listener that will build the FieldConfig.
name|addFieldConfigListener
argument_list|(
operator|new
name|FieldBoostMapFCListener
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|addFieldConfigListener
argument_list|(
operator|new
name|FieldDateResolutionFCListener
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|addFieldConfigListener
argument_list|(
operator|new
name|NumericFieldConfigListener
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
comment|// Default Values
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ALLOW_LEADING_WILDCARD
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// default in 2.9
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ANALYZER
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//default value 2.4
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|DEFAULT_OPERATOR
argument_list|,
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|PHRASE_SLOP
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//default value 2.4
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|LOWERCASE_EXPANDED_TERMS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//default value 2.4
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|ENABLE_POSITION_INCREMENTS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//default value 2.4
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_BOOST_MAP
argument_list|,
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FUZZY_CONFIG
argument_list|,
operator|new
name|FuzzyConfig
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|LOCALE
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|MULTI_TERM_REWRITE_METHOD
argument_list|,
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_REWRITE
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_DATE_RESOLUTION_MAP
argument_list|,
operator|new
name|HashMap
argument_list|<
name|CharSequence
argument_list|,
name|DateTools
operator|.
name|Resolution
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

