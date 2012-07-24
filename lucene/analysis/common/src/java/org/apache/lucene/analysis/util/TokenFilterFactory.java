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
name|java
operator|.
name|util
operator|.
name|Set
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
name|TokenStream
import|;
end_import

begin_comment
comment|/**  * Abstract parent class for analysis factories that create {@link org.apache.lucene.analysis.TokenFilter}  * instances.  */
end_comment

begin_class
DECL|class|TokenFilterFactory
specifier|public
specifier|abstract
class|class
name|TokenFilterFactory
extends|extends
name|AbstractAnalysisFactory
block|{
DECL|field|loader
specifier|private
specifier|static
specifier|final
name|AnalysisSPILoader
argument_list|<
name|TokenFilterFactory
argument_list|>
name|loader
init|=
operator|new
name|AnalysisSPILoader
argument_list|<
name|TokenFilterFactory
argument_list|>
argument_list|(
name|TokenFilterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** looks up a tokenfilter by name */
DECL|method|forName
specifier|public
specifier|static
name|TokenFilterFactory
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|loader
operator|.
name|newInstance
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** returns a list of all available tokenfilter names */
DECL|method|availableTokenFilters
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|availableTokenFilters
parameter_list|()
block|{
return|return
name|loader
operator|.
name|availableServices
argument_list|()
return|;
block|}
comment|/** Transform the specified input TokenStream */
DECL|method|create
specifier|public
specifier|abstract
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
function_decl|;
block|}
end_class

end_unit

