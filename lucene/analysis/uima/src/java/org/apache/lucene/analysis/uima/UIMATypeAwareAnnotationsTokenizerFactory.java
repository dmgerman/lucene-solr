begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.uima
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
name|util
operator|.
name|TokenizerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

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
comment|/**  * {@link org.apache.lucene.analysis.util.TokenizerFactory} for {@link UIMATypeAwareAnnotationsTokenizer}  */
end_comment

begin_class
DECL|class|UIMATypeAwareAnnotationsTokenizerFactory
specifier|public
class|class
name|UIMATypeAwareAnnotationsTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|descriptorPath
specifier|private
name|String
name|descriptorPath
decl_stmt|;
DECL|field|tokenType
specifier|private
name|String
name|tokenType
decl_stmt|;
DECL|field|featurePath
specifier|private
name|String
name|featurePath
decl_stmt|;
DECL|field|configurationParameters
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configurationParameters
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|configurationParameters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|k
range|:
name|args
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|k
operator|.
name|equals
argument_list|(
literal|"featurePath"
argument_list|)
condition|)
block|{
name|featurePath
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"featurePath"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|k
operator|.
name|equals
argument_list|(
literal|"tokenType"
argument_list|)
condition|)
block|{
name|tokenType
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"tokenType"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|k
operator|.
name|equals
argument_list|(
literal|"descriptorPath"
argument_list|)
condition|)
block|{
name|descriptorPath
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"descriptorPath"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|configurationParameters
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|args
operator|.
name|get
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|descriptorPath
operator|==
literal|null
operator|||
name|tokenType
operator|==
literal|null
operator|||
name|featurePath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"descriptorPath, tokenType, and featurePath are mandatory"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|UIMATypeAwareAnnotationsTokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|UIMATypeAwareAnnotationsTokenizer
argument_list|(
name|descriptorPath
argument_list|,
name|tokenType
argument_list|,
name|featurePath
argument_list|,
name|configurationParameters
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
end_class

end_unit

