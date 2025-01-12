begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.uima.ae
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|uima
operator|.
name|ae
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

begin_comment
comment|/**  * Singleton factory class responsible of {@link AEProvider}s' creation  */
end_comment

begin_class
DECL|class|AEProviderFactory
specifier|public
class|class
name|AEProviderFactory
block|{
DECL|field|instance
specifier|private
specifier|static
specifier|final
name|AEProviderFactory
name|instance
init|=
operator|new
name|AEProviderFactory
argument_list|()
decl_stmt|;
DECL|field|providerCache
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AEProvider
argument_list|>
name|providerCache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|AEProviderFactory
specifier|private
name|AEProviderFactory
parameter_list|()
block|{
comment|// Singleton
block|}
DECL|method|getInstance
specifier|public
specifier|static
name|AEProviderFactory
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
comment|/**    * @param keyPrefix         a prefix of the key used to cache the AEProvider    * @param aePath            the AnalysisEngine descriptor path    * @param runtimeParameters map of runtime parameters to configure inside the AnalysisEngine    * @return AEProvider    */
DECL|method|getAEProvider
specifier|public
specifier|synchronized
name|AEProvider
name|getAEProvider
parameter_list|(
name|String
name|keyPrefix
parameter_list|,
name|String
name|aePath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
parameter_list|)
block|{
name|String
name|key
init|=
operator|new
name|StringBuilder
argument_list|(
name|keyPrefix
operator|!=
literal|null
condition|?
name|keyPrefix
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
name|aePath
argument_list|)
operator|.
name|append
argument_list|(
name|runtimeParameters
operator|!=
literal|null
condition|?
name|runtimeParameters
operator|.
name|toString
argument_list|()
else|:
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|providerCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|==
literal|null
condition|)
block|{
name|AEProvider
name|aeProvider
decl_stmt|;
if|if
condition|(
name|runtimeParameters
operator|!=
literal|null
condition|)
name|aeProvider
operator|=
operator|new
name|OverridingParamsAEProvider
argument_list|(
name|aePath
argument_list|,
name|runtimeParameters
argument_list|)
expr_stmt|;
else|else
name|aeProvider
operator|=
operator|new
name|BasicAEProvider
argument_list|(
name|aePath
argument_list|)
expr_stmt|;
name|providerCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|aeProvider
argument_list|)
expr_stmt|;
block|}
return|return
name|providerCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * @param aePath the AnalysisEngine descriptor path    * @return AEProvider    */
DECL|method|getAEProvider
specifier|public
specifier|synchronized
name|AEProvider
name|getAEProvider
parameter_list|(
name|String
name|aePath
parameter_list|)
block|{
return|return
name|getAEProvider
argument_list|(
literal|null
argument_list|,
name|aePath
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * @param aePath            the AnalysisEngine descriptor path    * @param runtimeParameters map of runtime parameters to configure inside the AnalysisEngine    * @return AEProvider    */
DECL|method|getAEProvider
specifier|public
specifier|synchronized
name|AEProvider
name|getAEProvider
parameter_list|(
name|String
name|aePath
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|runtimeParameters
parameter_list|)
block|{
return|return
name|getAEProvider
argument_list|(
literal|null
argument_list|,
name|aePath
argument_list|,
name|runtimeParameters
argument_list|)
return|;
block|}
block|}
end_class

end_unit

