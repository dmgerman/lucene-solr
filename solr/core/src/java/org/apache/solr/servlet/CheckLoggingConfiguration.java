begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|CheckLoggingConfiguration
specifier|final
class|class
name|CheckLoggingConfiguration
block|{
DECL|method|check
specifier|static
name|void
name|check
parameter_list|()
block|{
try|try
block|{
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CheckLoggingConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoClassDefFoundError
argument_list|(
literal|"Failed to initialize Apache Solr: "
operator|+
literal|"Could not find necessary SLF4j logging jars. If using Jetty, the SLF4j logging jars need to go in "
operator|+
literal|"the jetty lib/ext directory. For other containers, the corresponding directory should be used. "
operator|+
literal|"For more information, see: http://wiki.apache.org/solr/SolrLogging"
argument_list|)
throw|;
block|}
block|}
DECL|method|CheckLoggingConfiguration
specifier|private
name|CheckLoggingConfiguration
parameter_list|()
block|{}
block|}
end_class

end_unit

