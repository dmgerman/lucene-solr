begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Map
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
name|Document
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
name|Field
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
name|FieldType
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
name|StorableField
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
name|JsonPreAnalyzedParser
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
name|PreAnalyzedField
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
name|PreAnalyzedField
operator|.
name|PreAnalyzedParser
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
name|SchemaField
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
name|SimplePreAnalyzedParser
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *<p>An update processor that parses configured fields of any document being added  * using {@link PreAnalyzedField} with the configured format parser.</p>  *   *<p>Fields are specified using the same patterns as in {@link FieldMutatingUpdateProcessorFactory}.  * They are then checked whether they follow a pre-analyzed format defined by<code>parser</code>.  * Valid fields are then parsed. The original {@link SchemaField} is used for the initial  * creation of {@link StorableField}, which is then modified to add the results from  * parsing (token stream value and/or string value) and then it will be directly added to  * the final Lucene {@link Document} to be indexed.</p>  *<p>Fields that are declared in the patterns list but are not present  * in the current schema will be removed from the input document.</p>  *<h3>Implementation details</h3>  *<p>This update processor uses {@link PreAnalyzedParser}  * to parse the original field content (interpreted as a string value), and thus  * obtain the stored part and the token stream part. Then it creates the "template"  * {@link Field}-s using the original {@link SchemaField#createFields(Object, float)}  * as declared in the current schema. Finally it sets the pre-analyzed parts if  * available (string value and the token  * stream value) on the first field of these "template" fields. If the declared  * field type does not support stored or indexed parts then such parts are silently  * discarded. Finally the updated "template" {@link Field}-s are added to the resulting  * {@link SolrInputField}, and the original value of that field is removed.</p>  *<h3>Example configuration</h3>  *<p>In the example configuration below there are two update chains, one that  * uses the "simple" parser ({@link SimplePreAnalyzedParser}) and one that uses  * the "json" parser ({@link JsonPreAnalyzedParser}). Field "nonexistent" will be  * removed from input documents if not present in the schema. Other fields will be  * analyzed and if valid they will be converted to {@link StorableField}-s or if  * they are not in a valid format that can be parsed with the selected parser they  * will be passed as-is. Assuming that<code>ssto</code> field is stored but not  * indexed, and<code>sind</code> field is indexed but not stored: if  *<code>ssto</code> input value contains the indexed part then this part will  * be discarded and only the stored value part will be retained. Similarly,  * if<code>sind</code> input value contains the stored part then it  * will be discarded and only the token stream part will be retained.</p>  *   *<pre class="prettyprint">  *&lt;updateRequestProcessorChain name="pre-analyzed-simple"&gt;  *&lt;processor class="solr.PreAnalyzedUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;title&lt;/str&gt;  *&lt;str name="fieldName"&gt;nonexistent&lt;/str&gt;  *&lt;str name="fieldName"&gt;ssto&lt;/str&gt;  *&lt;str name="fieldName"&gt;sind&lt;/str&gt;  *&lt;str name="parser"&gt;simple&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessorChain&gt;  *  *&lt;updateRequestProcessorChain name="pre-analyzed-json"&gt;  *&lt;processor class="solr.PreAnalyzedUpdateProcessorFactory"&gt;  *&lt;str name="fieldName"&gt;title&lt;/str&gt;  *&lt;str name="fieldName"&gt;nonexistent&lt;/str&gt;  *&lt;str name="fieldName"&gt;ssto&lt;/str&gt;  *&lt;str name="fieldName"&gt;sind&lt;/str&gt;  *&lt;str name="parser"&gt;json&lt;/str&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessorChain&gt;  *</pre>  *  */
end_comment

begin_class
DECL|class|PreAnalyzedUpdateProcessorFactory
specifier|public
class|class
name|PreAnalyzedUpdateProcessorFactory
extends|extends
name|FieldMutatingUpdateProcessorFactory
block|{
DECL|field|parser
specifier|private
name|PreAnalyzedField
name|parser
decl_stmt|;
DECL|field|parserImpl
specifier|private
name|String
name|parserImpl
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|NamedList
name|args
parameter_list|)
block|{
name|parserImpl
operator|=
operator|(
name|String
operator|)
name|args
operator|.
name|get
argument_list|(
literal|"parser"
argument_list|)
expr_stmt|;
name|args
operator|.
name|remove
argument_list|(
literal|"parser"
argument_list|)
expr_stmt|;
comment|// initialize inclusion / exclusion patterns
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
DECL|method|getInstance
specifier|public
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
name|PreAnalyzedUpdateProcessor
argument_list|(
name|getSelector
argument_list|()
argument_list|,
name|next
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
name|parser
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|super
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|PreAnalyzedField
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|parserImpl
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|put
argument_list|(
name|PreAnalyzedField
operator|.
name|PARSER_IMPL
argument_list|,
name|parserImpl
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|init
argument_list|(
name|core
operator|.
name|getLatestSchema
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|PreAnalyzedUpdateProcessor
class|class
name|PreAnalyzedUpdateProcessor
extends|extends
name|FieldMutatingUpdateProcessor
block|{
DECL|field|parser
specifier|private
name|PreAnalyzedField
name|parser
decl_stmt|;
DECL|field|schema
specifier|private
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|PreAnalyzedUpdateProcessor
specifier|public
name|PreAnalyzedUpdateProcessor
parameter_list|(
name|FieldNameSelector
name|sel
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|PreAnalyzedField
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|sel
argument_list|,
name|next
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
name|parser
operator|=
name|parser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mutate
specifier|protected
name|SolrInputField
name|mutate
parameter_list|(
name|SolrInputField
name|src
parameter_list|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|==
literal|null
condition|)
block|{
comment|// remove this field
return|return
literal|null
return|;
block|}
name|FieldType
name|type
init|=
name|PreAnalyzedField
operator|.
name|createFieldType
argument_list|(
name|sf
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
comment|// neither indexed nor stored - skip
return|return
literal|null
return|;
block|}
name|SolrInputField
name|res
init|=
operator|new
name|SolrInputField
argument_list|(
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|res
operator|.
name|setBoost
argument_list|(
name|src
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|src
control|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Field
name|pre
init|=
operator|(
name|Field
operator|)
name|parser
operator|.
name|createField
argument_list|(
name|sf
argument_list|,
name|o
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
if|if
condition|(
name|pre
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|addValue
argument_list|(
name|pre
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// restore the original value
name|log
operator|.
name|warn
argument_list|(
literal|"Could not parse field {} - using original value as is: {}"
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|res
operator|.
name|addValue
argument_list|(
name|o
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

