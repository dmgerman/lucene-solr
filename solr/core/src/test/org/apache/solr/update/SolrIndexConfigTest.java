begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|index
operator|.
name|ConcurrentMergeScheduler
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|SimpleMergedSegmentWarmer
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
name|index
operator|.
name|TieredMergePolicy
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
name|SolrTestCaseJ4
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
name|DirectoryFactory
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
name|SolrConfig
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
name|TestMergePolicyConfig
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|IndexSchemaFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link SolrIndexConfig}  *  * @see TestMergePolicyConfig  */
end_comment

begin_class
DECL|class|SolrIndexConfigTest
specifier|public
class|class
name|SolrIndexConfigTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrConfigFileName
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileName
init|=
literal|"solrconfig.xml"
decl_stmt|;
DECL|field|solrConfigFileNameWarmerRandomMergePolicy
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileNameWarmerRandomMergePolicy
init|=
literal|"solrconfig-warmer.xml"
decl_stmt|;
DECL|field|solrConfigFileNameWarmerRandomMergePolicyFactory
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileNameWarmerRandomMergePolicyFactory
init|=
literal|"solrconfig-warmer-randommergepolicyfactory.xml"
decl_stmt|;
DECL|field|solrConfigFileNameTieredMergePolicy
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileNameTieredMergePolicy
init|=
literal|"solrconfig-tieredmergepolicy.xml"
decl_stmt|;
DECL|field|solrConfigFileNameTieredMergePolicyFactory
specifier|private
specifier|static
specifier|final
name|String
name|solrConfigFileNameTieredMergePolicyFactory
init|=
literal|"solrconfig-tieredmergepolicyfactory.xml"
decl_stmt|;
DECL|field|schemaFileName
specifier|private
specifier|static
specifier|final
name|String
name|schemaFileName
init|=
literal|"schema.xml"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
name|solrConfigFileName
argument_list|,
name|schemaFileName
argument_list|)
expr_stmt|;
block|}
DECL|field|instanceDir
specifier|private
specifier|final
name|Path
name|instanceDir
init|=
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testFailingSolrIndexConfigCreation
specifier|public
name|void
name|testFailingSolrIndexConfigCreation
parameter_list|()
block|{
try|try
block|{
name|SolrConfig
name|solrConfig
init|=
operator|new
name|SolrConfig
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"bad-mp-solrconfig.xml"
else|:
literal|"bad-mpf-solrconfig.xml"
argument_list|)
decl_stmt|;
name|SolrIndexConfig
name|solrIndexConfig
init|=
operator|new
name|SolrIndexConfig
argument_list|(
name|solrConfig
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|indexSchema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|schemaFileName
argument_list|,
name|solrConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|indexSchema
argument_list|)
expr_stmt|;
name|solrIndexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"a mergePolicy should have an empty constructor in order to be instantiated in Solr thus this should fail "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// it failed as expected
block|}
block|}
annotation|@
name|Test
DECL|method|testTieredMPSolrIndexConfigCreation
specifier|public
name|void
name|testTieredMPSolrIndexConfigCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|solrConfigFileName
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|solrConfigFileNameTieredMergePolicy
else|:
name|solrConfigFileNameTieredMergePolicyFactory
decl_stmt|;
name|SolrConfig
name|solrConfig
init|=
operator|new
name|SolrConfig
argument_list|(
name|instanceDir
argument_list|,
name|solrConfigFileName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrIndexConfig
name|solrIndexConfig
init|=
operator|new
name|SolrIndexConfig
argument_list|(
name|solrConfig
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexSchema
name|indexSchema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|schemaFileName
argument_list|,
name|solrConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|indexSchema
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|solrIndexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null mp"
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"mp is not TieredMergePolicy"
argument_list|,
name|iwc
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|TieredMergePolicy
argument_list|)
expr_stmt|;
name|TieredMergePolicy
name|mp
init|=
operator|(
name|TieredMergePolicy
operator|)
name|iwc
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"mp.maxMergeAtOnceExplicit"
argument_list|,
literal|19
argument_list|,
name|mp
operator|.
name|getMaxMergeAtOnceExplicit
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mp.segmentsPerTier"
argument_list|,
literal|9
argument_list|,
operator|(
name|int
operator|)
name|mp
operator|.
name|getSegmentsPerTier
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"null ms"
argument_list|,
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ms is not CMS"
argument_list|,
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
operator|instanceof
name|ConcurrentMergeScheduler
argument_list|)
expr_stmt|;
name|ConcurrentMergeScheduler
name|ms
init|=
operator|(
name|ConcurrentMergeScheduler
operator|)
name|iwc
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ms.maxMergeCount"
argument_list|,
literal|987
argument_list|,
name|ms
operator|.
name|getMaxMergeCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ms.maxThreadCount"
argument_list|,
literal|42
argument_list|,
name|ms
operator|.
name|getMaxThreadCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergedSegmentWarmerIndexConfigCreation
specifier|public
name|void
name|testMergedSegmentWarmerIndexConfigCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrConfig
name|solrConfig
init|=
operator|new
name|SolrConfig
argument_list|(
name|instanceDir
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|solrConfigFileNameWarmerRandomMergePolicy
else|:
name|solrConfigFileNameWarmerRandomMergePolicyFactory
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrIndexConfig
name|solrIndexConfig
init|=
operator|new
name|SolrIndexConfig
argument_list|(
name|solrConfig
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrIndexConfig
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|solrIndexConfig
operator|.
name|mergedSegmentWarmerInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SimpleMergedSegmentWarmer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|solrIndexConfig
operator|.
name|mergedSegmentWarmerInfo
operator|.
name|className
argument_list|)
expr_stmt|;
name|IndexSchema
name|indexSchema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|schemaFileName
argument_list|,
name|solrConfig
argument_list|)
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|setLatestSchema
argument_list|(
name|indexSchema
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|solrIndexConfig
operator|.
name|toIndexWriterConfig
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SimpleMergedSegmentWarmer
operator|.
name|class
argument_list|,
name|iwc
operator|.
name|getMergedSegmentWarmer
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testToMap
specifier|public
name|void
name|testToMap
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|solrConfigFileNameWarmer
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|solrConfigFileNameWarmerRandomMergePolicy
else|:
name|solrConfigFileNameWarmerRandomMergePolicyFactory
decl_stmt|;
specifier|final
name|String
name|solrConfigFileNameTMP
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|solrConfigFileNameTieredMergePolicy
else|:
name|solrConfigFileNameTieredMergePolicyFactory
decl_stmt|;
specifier|final
name|String
name|solrConfigFileName
init|=
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|solrConfigFileNameWarmer
else|:
name|solrConfigFileNameTMP
operator|)
decl_stmt|;
name|SolrConfig
name|solrConfig
init|=
operator|new
name|SolrConfig
argument_list|(
name|instanceDir
argument_list|,
name|solrConfigFileName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrIndexConfig
name|solrIndexConfig
init|=
operator|new
name|SolrIndexConfig
argument_list|(
name|solrConfig
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrIndexConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameTieredMergePolicyFactory
argument_list|)
operator|||
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameWarmerRandomMergePolicyFactory
argument_list|)
condition|)
block|{
name|assertNotNull
argument_list|(
name|solrIndexConfig
operator|.
name|mergePolicyFactoryInfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|solrIndexConfig
operator|.
name|mergePolicyInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameWarmerRandomMergePolicy
argument_list|)
operator|||
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameWarmerRandomMergePolicyFactory
argument_list|)
condition|)
block|{
name|assertNotNull
argument_list|(
name|solrIndexConfig
operator|.
name|mergedSegmentWarmerInfo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|solrIndexConfig
operator|.
name|mergedSegmentWarmerInfo
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|solrIndexConfig
operator|.
name|mergeSchedulerInfo
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|solrIndexConfig
operator|.
name|toMap
argument_list|()
decl_stmt|;
name|int
name|mSizeExpected
init|=
literal|0
decl_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"useCompoundFile"
argument_list|)
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"maxBufferedDocs"
argument_list|)
operator|instanceof
name|Integer
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"maxMergeDocs"
argument_list|)
operator|instanceof
name|Integer
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"mergeFactor"
argument_list|)
operator|instanceof
name|Integer
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"ramBufferSizeMB"
argument_list|)
operator|instanceof
name|Double
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"writeLockTimeout"
argument_list|)
operator|instanceof
name|Integer
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"lockType"
argument_list|)
operator|instanceof
name|String
argument_list|)
expr_stmt|;
block|{
specifier|final
name|String
name|lockType
init|=
operator|(
name|String
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"lockType"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SIMPLE
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
operator|||
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NATIVE
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
operator|||
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SINGLE
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
operator|||
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NONE
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
operator|||
name|DirectoryFactory
operator|.
name|LOCK_TYPE_HDFS
operator|.
name|equals
argument_list|(
name|lockType
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"infoStreamEnabled"
argument_list|)
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
block|{
name|assertFalse
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"infoStreamEnabled"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"mergeScheduler"
argument_list|)
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"mergePolicy"
argument_list|)
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameWarmerRandomMergePolicy
argument_list|)
operator|||
name|solrConfigFileName
operator|.
name|equals
argument_list|(
name|solrConfigFileNameWarmerRandomMergePolicyFactory
argument_list|)
condition|)
block|{
operator|++
name|mSizeExpected
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"mergedSegmentWarmer"
argument_list|)
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"mergedSegmentWarmer"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|mSizeExpected
argument_list|,
name|m
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

