begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|core
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * General token testing helper functions  */
end_comment

begin_class
DECL|class|BaseTokenTestCase
specifier|public
specifier|abstract
class|class
name|BaseTokenTestCase
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** a map containing the default test version param for easy testing */
DECL|field|DEFAULT_VERSION_PARAM
specifier|protected
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|DEFAULT_VERSION_PARAM
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.luceneMatchVersion"
argument_list|,
literal|"LUCENE_CURRENT"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** The default test version for easy testing */
DECL|field|DEFAULT_VERSION
specifier|public
specifier|static
specifier|final
name|Version
name|DEFAULT_VERSION
init|=
name|Config
operator|.
name|parseLuceneVersionString
argument_list|(
name|DEFAULT_VERSION_PARAM
operator|.
name|get
argument_list|(
literal|"luceneMatchVersion"
argument_list|)
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

