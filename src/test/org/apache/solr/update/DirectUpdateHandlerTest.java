begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

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
name|Field
operator|.
name|Index
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
operator|.
name|Store
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
name|SolrException
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/**  *   *  */
end_comment

begin_class
DECL|class|DirectUpdateHandlerTest
specifier|public
class|class
name|DirectUpdateHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|testRequireUniqueKey
specifier|public
name|void
name|testRequireUniqueKey
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|overwriteCommitted
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|overwritePending
operator|=
literal|true
expr_stmt|;
name|cmd
operator|.
name|allowDups
operator|=
literal|false
expr_stmt|;
comment|// Add a valid document
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"AAA"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"subject"
argument_list|,
literal|"xxxxx"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
comment|// Add a document with multiple ids
name|cmd
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
comment|// reset the id for this add
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"AAA"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"BBB"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"subject"
argument_list|,
literal|"xxxxx"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"added a document with multiple ids"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// expected
comment|// Add a document without an id
name|cmd
operator|.
name|indexedId
operator|=
literal|null
expr_stmt|;
comment|// reset the id for this add
name|cmd
operator|.
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|cmd
operator|.
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"subject"
argument_list|,
literal|"xxxxx"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|updater
operator|.
name|addDoc
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"added a document without an ids"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{ }
comment|// expected
block|}
block|}
end_class

end_unit

