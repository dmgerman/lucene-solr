begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
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
name|queryParser
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfigListener
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
name|queryParser
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
import|;
end_import

begin_comment
comment|/**  * This listener listens for every field configuration request and assign a  * {@link ConfigurationKeys#DATE_RESOLUTION} to the equivalent {@link FieldConfig} based  * on a defined map: fieldName -> {@link Resolution} stored in  * {@link ConfigurationKeys#FIELD_DATE_RESOLUTION_MAP}.  *   * @see ConfigurationKeys#DATE_RESOLUTION  * @see ConfigurationKeys#FIELD_DATE_RESOLUTION_MAP  * @see FieldConfig  * @see FieldConfigListener  */
end_comment

begin_class
DECL|class|FieldDateResolutionFCListener
specifier|public
class|class
name|FieldDateResolutionFCListener
implements|implements
name|FieldConfigListener
block|{
DECL|field|config
specifier|private
name|QueryConfigHandler
name|config
init|=
literal|null
decl_stmt|;
DECL|method|FieldDateResolutionFCListener
specifier|public
name|FieldDateResolutionFCListener
parameter_list|(
name|QueryConfigHandler
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
DECL|method|buildFieldConfig
specifier|public
name|void
name|buildFieldConfig
parameter_list|(
name|FieldConfig
name|fieldConfig
parameter_list|)
block|{
name|DateTools
operator|.
name|Resolution
name|dateRes
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|CharSequence
argument_list|,
name|DateTools
operator|.
name|Resolution
argument_list|>
name|dateResMap
init|=
name|this
operator|.
name|config
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|FIELD_DATE_RESOLUTION_MAP
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateResMap
operator|!=
literal|null
condition|)
block|{
name|dateRes
operator|=
name|dateResMap
operator|.
name|get
argument_list|(
name|fieldConfig
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dateRes
operator|==
literal|null
condition|)
block|{
name|dateRes
operator|=
name|this
operator|.
name|config
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|DATE_RESOLUTION
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dateRes
operator|!=
literal|null
condition|)
block|{
name|fieldConfig
operator|.
name|set
argument_list|(
name|ConfigurationKeys
operator|.
name|DATE_RESOLUTION
argument_list|,
name|dateRes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

