begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|store
operator|.
name|MockDirectoryWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Attempts to close a {@link BaseDirectoryWrapper}.  *   * @see LuceneTestCase#newDirectory(java.util.Random)  */
end_comment

begin_class
DECL|class|CloseableDirectory
specifier|final
class|class
name|CloseableDirectory
implements|implements
name|Closeable
block|{
DECL|field|dir
specifier|private
specifier|final
name|BaseDirectoryWrapper
name|dir
decl_stmt|;
DECL|field|failureMarker
specifier|private
specifier|final
name|TestRuleMarkFailure
name|failureMarker
decl_stmt|;
DECL|method|CloseableDirectory
specifier|public
name|CloseableDirectory
parameter_list|(
name|BaseDirectoryWrapper
name|dir
parameter_list|,
name|TestRuleMarkFailure
name|failureMarker
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|failureMarker
operator|=
name|failureMarker
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// We only attempt to check open/closed state if there were no other test
comment|// failures.
try|try
block|{
if|if
condition|(
name|failureMarker
operator|.
name|wasSuccessful
argument_list|()
operator|&&
name|dir
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Directory not closed: "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// TODO: perform real close of the delegate: LUCENE-4058
comment|// dir.close();
block|}
block|}
block|}
end_class

end_unit

