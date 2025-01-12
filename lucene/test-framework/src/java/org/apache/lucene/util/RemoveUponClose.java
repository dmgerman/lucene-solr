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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

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

begin_comment
comment|/**  * A {@link Closeable} that attempts to remove a given file/folder.  */
end_comment

begin_class
DECL|class|RemoveUponClose
specifier|final
class|class
name|RemoveUponClose
implements|implements
name|Closeable
block|{
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|failureMarker
specifier|private
specifier|final
name|TestRuleMarkFailure
name|failureMarker
decl_stmt|;
DECL|field|creationStack
specifier|private
specifier|final
name|String
name|creationStack
decl_stmt|;
DECL|method|RemoveUponClose
specifier|public
name|RemoveUponClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|TestRuleMarkFailure
name|failureMarker
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|failureMarker
operator|=
name|failureMarker
expr_stmt|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|e
range|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|creationStack
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// only if there were no other test failures.
if|if
condition|(
name|failureMarker
operator|.
name|wasSuccessful
argument_list|()
condition|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
try|try
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not remove temporary location '"
operator|+
name|path
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|"', created at stack trace:\n"
operator|+
name|creationStack
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

