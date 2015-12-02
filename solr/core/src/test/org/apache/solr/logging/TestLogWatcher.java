begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.logging
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
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
name|solr
operator|.
name|common
operator|.
name|SolrDocument
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
name|SolrDocumentList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestLogWatcher
specifier|public
class|class
name|TestLogWatcher
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
name|LogWatcherConfig
name|config
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|config
operator|=
operator|new
name|LogWatcherConfig
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLog4jWatcher
specifier|public
name|void
name|testLog4jWatcher
parameter_list|()
block|{
name|LogWatcher
name|watcher
init|=
name|LogWatcher
operator|.
name|newRegisteredLogWatcher
argument_list|(
name|config
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|watcher
operator|.
name|getLastEvent
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"This is a test message"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|watcher
operator|.
name|getLastEvent
argument_list|()
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|events
init|=
name|watcher
operator|.
name|getHistory
argument_list|(
operator|-
literal|1
argument_list|,
operator|new
name|AtomicBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|events
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SolrDocument
name|event
init|=
name|events
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|event
operator|.
name|get
argument_list|(
literal|"logger"
argument_list|)
argument_list|,
literal|"org.apache.solr.logging.TestLogWatcher"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|event
operator|.
name|get
argument_list|(
literal|"message"
argument_list|)
argument_list|,
literal|"This is a test message"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

