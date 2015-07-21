begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Random
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
name|analysis
operator|.
name|core
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
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|TokenFilterFactory
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
name|CharFilterFactory
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
name|LuceneTestCase
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
name|handler
operator|.
name|admin
operator|.
name|LukeRequestHandlerTest
import|;
end_import

begin_comment
comment|// jdoc
end_comment

begin_comment
comment|/**  * A Test only custom FieldType that specifies null for various params when constructing   * TokenizerChain instances to ensure that they are still well behaved.  *  * @see LukeRequestHandlerTest#testNullFactories  */
end_comment

begin_class
DECL|class|CustomAnalyzerStrField
specifier|public
class|class
name|CustomAnalyzerStrField
extends|extends
name|StrField
block|{
DECL|field|indexAnalyzer
specifier|private
specifier|final
name|Analyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|queryAnalyzer
specifier|private
specifier|final
name|Analyzer
name|queryAnalyzer
decl_stmt|;
DECL|method|CustomAnalyzerStrField
specifier|public
name|CustomAnalyzerStrField
parameter_list|()
block|{
name|Random
name|r
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
decl_stmt|;
comment|// two arg constructor
name|Analyzer
name|a2
init|=
operator|new
name|TokenizerChain
argument_list|(
operator|new
name|KeywordTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|TokenFilterFactory
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// three arg constructor
name|Analyzer
name|a3
init|=
operator|new
name|TokenizerChain
argument_list|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|CharFilterFactory
index|[
literal|0
index|]
argument_list|,
operator|new
name|KeywordTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
argument_list|,
name|r
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|TokenFilterFactory
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|indexAnalyzer
operator|=
name|a2
expr_stmt|;
name|queryAnalyzer
operator|=
name|a3
expr_stmt|;
block|}
else|else
block|{
name|queryAnalyzer
operator|=
name|a2
expr_stmt|;
name|indexAnalyzer
operator|=
name|a3
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getIndexAnalyzer
specifier|public
name|Analyzer
name|getIndexAnalyzer
parameter_list|()
block|{
return|return
name|indexAnalyzer
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|queryAnalyzer
return|;
block|}
block|}
end_class

end_unit

