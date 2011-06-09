begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.uima.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|processor
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Configuration holding all the configurable parameters for calling UIMA inside Solr  *  *  */
end_comment

begin_class
DECL|class|SolrUIMAConfiguration
specifier|public
class|class
name|SolrUIMAConfiguration
block|{
DECL|field|fieldsToAnalyze
specifier|private
name|String
index|[]
name|fieldsToAnalyze
decl_stmt|;
DECL|field|fieldsMerging
specifier|private
name|boolean
name|fieldsMerging
decl_stmt|;
DECL|field|typesFeaturesFieldsMapping
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|typesFeaturesFieldsMapping
decl_stmt|;
DECL|field|aePath
specifier|private
name|String
name|aePath
decl_stmt|;
DECL|field|runtimeParameters
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
decl_stmt|;
DECL|field|ignoreErrors
specifier|private
name|boolean
name|ignoreErrors
decl_stmt|;
DECL|field|logField
specifier|private
name|String
name|logField
decl_stmt|;
DECL|method|SolrUIMAConfiguration
specifier|public
name|SolrUIMAConfiguration
parameter_list|(
name|String
name|aePath
parameter_list|,
name|String
index|[]
name|fieldsToAnalyze
parameter_list|,
name|boolean
name|fieldsMerging
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|typesFeaturesFieldsMapping
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
parameter_list|,
name|boolean
name|ignoreErrors
parameter_list|,
name|String
name|logField
parameter_list|)
block|{
name|this
operator|.
name|aePath
operator|=
name|aePath
expr_stmt|;
name|this
operator|.
name|fieldsToAnalyze
operator|=
name|fieldsToAnalyze
expr_stmt|;
name|this
operator|.
name|fieldsMerging
operator|=
name|fieldsMerging
expr_stmt|;
name|this
operator|.
name|runtimeParameters
operator|=
name|runtimeParameters
expr_stmt|;
name|this
operator|.
name|typesFeaturesFieldsMapping
operator|=
name|typesFeaturesFieldsMapping
expr_stmt|;
name|this
operator|.
name|ignoreErrors
operator|=
name|ignoreErrors
expr_stmt|;
name|this
operator|.
name|logField
operator|=
name|logField
expr_stmt|;
block|}
DECL|method|getFieldsToAnalyze
specifier|public
name|String
index|[]
name|getFieldsToAnalyze
parameter_list|()
block|{
return|return
name|fieldsToAnalyze
return|;
block|}
DECL|method|isFieldsMerging
specifier|public
name|boolean
name|isFieldsMerging
parameter_list|()
block|{
return|return
name|fieldsMerging
return|;
block|}
DECL|method|getTypesFeaturesFieldsMapping
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|MapField
argument_list|>
argument_list|>
name|getTypesFeaturesFieldsMapping
parameter_list|()
block|{
return|return
name|typesFeaturesFieldsMapping
return|;
block|}
DECL|method|getAePath
specifier|public
name|String
name|getAePath
parameter_list|()
block|{
return|return
name|aePath
return|;
block|}
DECL|method|getRuntimeParameters
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRuntimeParameters
parameter_list|()
block|{
return|return
name|runtimeParameters
return|;
block|}
DECL|method|isIgnoreErrors
specifier|public
name|boolean
name|isIgnoreErrors
parameter_list|()
block|{
return|return
name|ignoreErrors
return|;
block|}
DECL|method|getLogField
specifier|public
name|String
name|getLogField
parameter_list|()
block|{
return|return
name|logField
return|;
block|}
DECL|class|MapField
specifier|static
specifier|final
class|class
name|MapField
block|{
DECL|field|fieldName
DECL|field|fieldNameFeature
specifier|private
name|String
name|fieldName
decl_stmt|,
name|fieldNameFeature
decl_stmt|;
DECL|field|prefix
specifier|private
name|boolean
name|prefix
decl_stmt|;
comment|// valid if dynamicField == true
comment|// false: *_s, true: s_*
DECL|method|MapField
name|MapField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|fieldNameFeature
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|fieldNameFeature
operator|=
name|fieldNameFeature
expr_stmt|;
if|if
condition|(
name|fieldNameFeature
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|startsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|prefix
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|endsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|prefix
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|fieldName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"static field name cannot be used for dynamicField"
argument_list|)
throw|;
block|}
block|}
DECL|method|getFieldNameFeature
name|String
name|getFieldNameFeature
parameter_list|()
block|{
return|return
name|fieldNameFeature
return|;
block|}
DECL|method|getFieldName
name|String
name|getFieldName
parameter_list|(
name|String
name|featureValue
parameter_list|)
block|{
if|if
condition|(
name|fieldNameFeature
operator|!=
literal|null
condition|)
block|{
return|return
name|prefix
condition|?
name|fieldName
operator|+
name|featureValue
else|:
name|featureValue
operator|+
name|fieldName
return|;
block|}
return|return
name|fieldName
return|;
block|}
block|}
block|}
end_class

end_unit

