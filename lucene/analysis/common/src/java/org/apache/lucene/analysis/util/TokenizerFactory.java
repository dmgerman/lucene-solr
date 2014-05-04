begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|Token
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
name|Tokenizer
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
name|AttributeSource
operator|.
name|AttributeFactory
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

begin_comment
comment|/**  * Abstract parent class for analysis factories that create {@link Tokenizer}  * instances.  */
end_comment

begin_class
DECL|class|TokenizerFactory
specifier|public
specifier|abstract
class|class
name|TokenizerFactory
extends|extends
name|AbstractAnalysisFactory
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|AnalysisSPILoader
argument_list|<
name|TokenizerFactory
argument_list|>
name|loader
init|=
operator|new
name|AnalysisSPILoader
argument_list|<>
argument_list|(
name|TokenizerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** looks up a tokenizer by name from context classpath */
DECL|method|forName
specifier|public
specifier|static
name|TokenizerFactory
name|forName
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
return|return
name|loader
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|args
argument_list|)
return|;
block|}
comment|/** looks up a tokenizer class by name from context classpath */
DECL|method|lookupClass
specifier|public
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|TokenizerFactory
argument_list|>
name|lookupClass
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|loader
operator|.
name|lookupClass
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available tokenizer names from context classpath */
DECL|method|availableTokenizers
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availableTokenizers
parameter_list|()
block|{
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
comment|/**     * Reloads the factory list from the given {@link ClassLoader}.    * Changes to the factories are visible after the method ends, all    * iterators ({@link #availableTokenizers()},...) stay consistent.     *     *<p><b>NOTE:</b> Only new factories are added, existing ones are    * never removed or replaced.    *     *<p><em>This method is expensive and should only be called for discovery    * of new factories on the given classpath/classloader!</em>    */
DECL|method|reloadTokenizers
specifier|public
specifier|static
name|void
name|reloadTokenizers
parameter_list|(
name|ClassLoader
name|classloader
parameter_list|)
block|{
name|loader
operator|.
name|reload
argument_list|(
name|classloader
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initialize this factory via a set of key-value pairs.    */
DECL|method|TokenizerFactory
specifier|protected
name|TokenizerFactory
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
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a TokenStream of the specified input using the default attribute factory. */
DECL|method|create
specifier|public
specifier|final
name|Tokenizer
name|create
parameter_list|()
block|{
return|return
name|create
argument_list|(
name|Token
operator|.
name|TOKEN_ATTRIBUTE_FACTORY
argument_list|)
return|;
block|}
comment|/** Creates a TokenStream of the specified input using the given AttributeFactory */
DECL|method|create
specifier|abstract
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
function_decl|;
block|}
end_class

end_unit

